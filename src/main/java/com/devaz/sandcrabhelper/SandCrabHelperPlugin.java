package com.devaz.sandcrabhelper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
        name = "Sand Crab Helper (by Dev_Az)",
        description = "Tracks aggression timer, crab spawns, and alerts for idle and reset. Created by Dev_Az.",
        tags = {"crab", "afk", "combat", "xp", "training"}
)
public class SandCrabHelperPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private SandCrabHelperOverlay overlay;
    @Inject private SandCrabHelperConfig config;
    @Inject private ConfigManager configManager;

    private Instant lastAggroTime;
    private Instant lastCombatTime;
    private Instant nextLoopSoundTime;
    private boolean inAggroWindow = false;
    private boolean aggroAlertPlayed = false;
    private boolean idleAlertPlayed = false;
    private boolean aggroSoundPlayed = false;

    private final Map<Integer, Instant> crabEngageTimes = new HashMap<>();

    public Instant getLastAggroTime() { return lastAggroTime; }
    public boolean isInAggroWindow() { return inAggroWindow; }
    public Duration getTimeSinceLastCombat()
    {
        return lastCombatTime != null ? Duration.between(lastCombatTime, Instant.now()) : null;
    }

    private boolean isCrab(String name)
    {
        return name != null && name.toLowerCase().contains("crab");
    }

    private void fullReset()
    {
        lastAggroTime = null;
        lastCombatTime = null;
        nextLoopSoundTime = null;
        inAggroWindow = false;
        aggroAlertPlayed = false;
        idleAlertPlayed = false;
        aggroSoundPlayed = false;
        crabEngageTimes.clear();
        log.info("Aggro timer fully reset manually");
    }

    @Override
    protected void startUp()
    {
        fullReset();
        overlayManager.add(overlay);
        log.info("Sand Crab Helper started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        fullReset();
        log.info("Sand Crab Helper stopped");
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        final Player local = client.getLocalPlayer();
        if (local == null) return;

        if (config.restartAggroTimerNow())
        {
            fullReset();
            configManager.setConfiguration("sandcrabhelper", "restartAggroTimerNow", false);
        }

        boolean anyAggro = false;
        int timeout = config.aggroWindowMinutes() * 60;
        int idle = config.idleTimeoutSeconds();

        for (NPC npc : client.getNpcs())
        {
            if (!isCrab(npc.getName())) continue;

            int idx = npc.getIndex();
            Actor target = npc.getInteracting();

            if (target != null && target.equals(local))
            {
                crabEngageTimes.put(idx, Instant.now());
                anyAggro = true;

                if (lastAggroTime == null)
                {
                    lastAggroTime = Instant.now();
                    inAggroWindow = true;
                    aggroAlertPlayed = false;
                    idleAlertPlayed = false;
                    aggroSoundPlayed = false;
                    nextLoopSoundTime = null;
                    log.info("Aggro window started");
                }

                if (idleAlertPlayed)
                {
                    idleAlertPlayed = false;
                    nextLoopSoundTime = null;
                    log.debug("Combat resumed — idle state cleared");
                }
            }
        }

        if (anyAggro)
            lastCombatTime = Instant.now();

        crabEngageTimes.entrySet().removeIf(e ->
                Duration.between(e.getValue(), Instant.now()).getSeconds() > timeout
        );

        if (lastAggroTime != null && !aggroSoundPlayed)
        {
            long elapsed = Duration.between(lastAggroTime, Instant.now()).getSeconds();
            if (elapsed >= timeout)
            {
                aggroSoundPlayed = true;
                if (config.playResetSound())
                {
                    client.playSoundEffect(config.customSoundId());
                    log.info("Aggro timer reached 0:00 — sound played.");
                }
            }
        }

        // ✅ New robust idle tracking (even if aggro timer expired too)
        boolean hasTimedOut = lastCombatTime != null &&
                Duration.between(lastCombatTime, Instant.now()).getSeconds() >= idle;

        if (hasTimedOut && !idleAlertPlayed)
        {
            idleAlertPlayed = true;
            nextLoopSoundTime = Instant.now().plusSeconds(10);

            log.info("Idle timeout reached (Threshold={}s) — loop initiated.", idle);

            if (config.playResetSound())
            {
                client.playSoundEffect(config.customSoundId());
                log.info("Idle timeout sound played.");
            }

            inAggroWindow = false;
        }

        if (idleAlertPlayed && config.enableAfkSoundLoop())
        {
            if (nextLoopSoundTime != null && Instant.now().isAfter(nextLoopSoundTime))
            {
                client.playSoundEffect(config.customSoundId());
                nextLoopSoundTime = Instant.now().plusSeconds(10);
                log.debug("AFK sound loop triggered.");
            }
        }

        if (inAggroWindow && crabEngageTimes.isEmpty() && aggroSoundPlayed)
        {
            log.info("All crabs passive — resetting aggro window.");
            inAggroWindow = false;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING)
        {
            log.debug("Map unloading...");
        }
        else if (event.getGameState() == GameState.LOGGED_IN)
        {
            fullReset();
            log.info("Map reload — reset all states.");
        }
    }

    @Provides
    SandCrabHelperConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SandCrabHelperConfig.class);
    }
}
