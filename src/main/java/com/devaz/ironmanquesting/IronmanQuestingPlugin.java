
// File: src/main/java/com/devaz/ironmanquesting/IronmanQuestingPlugin.java
// Description: Main plugin class. Registers overlay on start and removes on shutdown.
// ⚠️ Depends on IronmanQuestingOverlay and IronmanQuestingConfig.
// ⚠️ Ensures overlay receives injected ConfigManager to persist step state.
// ⚠️ Includes optional developer test hook to simulate step completion (config-controlled).

package com.devaz.ironmanquesting;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Ironman Questing Helper by Az"
)
public class IronmanQuestingPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private IronmanQuestingOverlay overlay;

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Inject
    private IronmanQuestingConfig config;

    @Override
    protected void startUp()
    {
        System.out.println("[IronmanQuestingPlugin] 🔧 Ironman Questing Helper started");
        overlayManager.add(overlay);

        // 🔧 Developer test hook (optional: controlled by config)
        if (config.enableDevTest())
        {
            configManager.setConfiguration("ironmanquesting", "step.1", true);
            Boolean testStep = configManager.getConfiguration("ironmanquesting", "step.1", boolean.class);
            System.out.println("🔄 DevMode enabled: Step 1 marked as done = " + testStep);
        }
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        System.out.println("[IronmanQuestingPlugin] ❌ Ironman Questing Helper stopped");
    }

    @Provides
    IronmanQuestingConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(IronmanQuestingConfig.class);
    }
}
