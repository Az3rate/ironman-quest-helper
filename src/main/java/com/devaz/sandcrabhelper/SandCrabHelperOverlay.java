package com.devaz.sandcrabhelper;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class SandCrabHelperOverlay extends Overlay
{
    private final Client client;
    private final SandCrabHelperPlugin plugin;
    private final SandCrabHelperConfig config;

    private static final int PADDING = 6;
    private static final int SPACING_SECTION = 6;
    private static final int SPACING_LINE = 2;
    private static final int SPACING_AFTER_BAR = 12;
    private static final int BAR_HEIGHT = 14;
    private static final int BAR_WIDTH = 220;

    @Inject
    public SandCrabHelperOverlay(Client client, SandCrabHelperPlugin plugin, SandCrabHelperConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        Instant aggroStart = plugin.getLastAggroTime();
        if (aggroStart == null)
            return null;

        int totalSeconds = config.aggroWindowMinutes() * 60;
        Duration elapsed = Duration.between(aggroStart, Instant.now());
        int elapsedSeconds = (int) elapsed.getSeconds();
        int remainingSeconds = Math.max(0, totalSeconds - elapsedSeconds);
        boolean expired = elapsedSeconds >= totalSeconds;

        Font fontTitle = new Font("Arial", Font.BOLD, 12);
        Font fontLine = new Font("Arial", Font.PLAIN, 11);
        Font fontSmall = new Font("Arial", Font.PLAIN, 10);
        Font fontItalic = new Font("Arial", Font.ITALIC, 11);

        int height = 0;
        int width = BAR_WIDTH;

        g.setFont(fontTitle);
        height += g.getFontMetrics().getHeight() + SPACING_SECTION;

        height += BAR_HEIGHT + SPACING_AFTER_BAR;

        g.setFont(fontLine);
        FontMetrics fmLine = g.getFontMetrics();
        height += fmLine.getHeight();

        String idleLine = null;
        if (config.showTimeSinceLastCombat())
        {
            Duration sinceLastCombat = plugin.getTimeSinceLastCombat();
            if (sinceLastCombat != null)
            {
                idleLine = "Idle: " + sinceLastCombat.getSeconds() + "s";
                height += fmLine.getHeight() + SPACING_LINE;
            }
        }

        String expiredLine = null;
        if (expired)
        {
            g.setFont(fontItalic);
            expiredLine = "Reload map to reset crabs.";
            height += g.getFontMetrics().getHeight() + SPACING_LINE;
        }

        g.setFont(fontSmall);
        height += g.getFontMetrics().getHeight();

        int boxWidth = width + PADDING * 2;
        int boxHeight = height + PADDING * 2;

        // === Background box ===
        g.setColor(new Color(10, 10, 10, 230));
        g.fillRect(8 - PADDING, 20 - 16, boxWidth, boxHeight);

        int drawY = 20;

        // === Title ===
        g.setFont(fontTitle);
        g.setColor(Color.WHITE);
        g.drawString("Sand Crab Timer by Dev_Az", 8, drawY);
        drawY += g.getFontMetrics().getHeight() + SPACING_SECTION;

        // === Bar ===
        int barY = drawY;
        if (config.showProgressBar())
        {
            float pct = Math.min(1f, Math.max(0f, elapsedSeconds / (float) totalSeconds));
            Color fill = Color.getHSBColor((1 - pct) * 0.33f, 1f, 1f);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(8, barY, BAR_WIDTH, BAR_HEIGHT);

            g.setColor(fill);
            g.fillRect(8, barY, (int) (BAR_WIDTH * (1 - pct)), BAR_HEIGHT);

            g.setColor(Color.BLACK);
            g.drawRect(8, barY, BAR_WIDTH, BAR_HEIGHT);
        }

        // === Timer centered ===
        g.setFont(fontLine);
        String timerText = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60);
        FontMetrics fmTimer = g.getFontMetrics();
        int timerWidth = fmTimer.stringWidth(timerText);
        int timerHeight = fmTimer.getHeight();

        g.setColor(Color.WHITE);
        int centerY = barY + BAR_HEIGHT / 2 + timerHeight / 2 - 1;
        g.drawString(timerText, 8 + (BAR_WIDTH - timerWidth) / 2, centerY);

        drawY = barY + BAR_HEIGHT + SPACING_AFTER_BAR;

        // === Idle ===
        if (idleLine != null)
        {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(idleLine, 8, drawY);
            drawY += g.getFontMetrics().getHeight() + SPACING_LINE;
        }

        // === Expired hint ===
        if (expiredLine != null)
        {
            g.setFont(fontItalic);
            g.setColor(Color.GRAY);
            g.drawString(expiredLine, 8, drawY);
            drawY += g.getFontMetrics().getHeight() + SPACING_LINE;
        }

        // === Options ===
        g.setFont(fontSmall);
        FontMetrics fmSmall = g.getFontMetrics();
        int optY = drawY;
        int optX = 8;

        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Options:", optX, optY);
        optX += fmSmall.stringWidth("Options: ") + 4;

        drawOption(g, "Sound", config.playResetSound(), optX, optY);
        optX += fmSmall.stringWidth("[ ] Sound") + 10;
        drawOption(g, "Loop", config.enableAfkSoundLoop(), optX, optY);
        optX += fmSmall.stringWidth("[ ] Loop") + 10;
        drawOption(g, "Bar", config.showProgressBar(), optX, optY);

        return new Dimension(boxWidth, boxHeight);
    }

    private void drawOption(Graphics2D g, String label, boolean enabled, int x, int y)
    {
        g.setColor(enabled ? new Color(120, 255, 120) : new Color(100, 100, 100));
        g.drawString("[ ] " + label, x, y);
    }
}
