// File: src/main/java/com/devaz/ironmanquesting/IronmanQuestingOverlay.java
// Description: Overlay that renders quest steps from quest_steps.json.
// ⚠️ Depends on IronmanQuestingPlugin, loads from resource path `/quest_steps.json`.
// ⚠️ quest_steps.json must be structured with a top-level object and "steps" array.

package com.devaz.ironmanquesting;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IronmanQuestingOverlay extends Overlay
{
    private final Client client;
    private final ConfigManager configManager;
    private final PanelComponent panelComponent = new PanelComponent();
    private final List<QuestStep> steps = new ArrayList<>();

    @Inject
    public IronmanQuestingOverlay(Client client, ConfigManager configManager)
    {
        this.client = client;
        this.configManager = configManager;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        loadSteps();
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Ironman Questing Steps")
                .build());

        if (client.getGameState().ordinal() < GameState.LOGGED_IN.ordinal())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("🧪 Dev Preview")
                    .right("No Login")
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("✔️ Overlay working")
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("❌ Step tracking inactive")
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Steps Loaded")
                    .right(String.valueOf(steps.size()))
                    .build());

            return panelComponent.render(graphics);
        }

        for (QuestStep step : steps)
        {
            boolean done = configManager.getConfiguration("ironmanquesting", "step." + step.id, boolean.class) != null
                    ? configManager.getConfiguration("ironmanquesting", "step." + step.id, boolean.class)
                    : step.done;

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(step.id + ". " + step.description)
                    .right(done ? "✔️" : "⏳")
                    .build());
        }

        return panelComponent.render(graphics);
    }

    private void loadSteps()
    {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/quest_steps.json")))
        {
            QuestData questData = new Gson().fromJson(reader, QuestData.class);
            if (questData != null && questData.steps != null)
            {
                steps.clear();
                steps.addAll(questData.steps);
                System.out.println("📋 Loaded " + steps.size() + " steps from quest_steps.json");
            }
        }
        catch (Exception e)
        {
            System.err.println("[IronmanQuestingOverlay] Failed to load quest_steps.json: " + e.getMessage());
        }
    }

    public Client getClient()
    {
        return client;
    }

    private static class QuestData
    {
        @SerializedName("steps")
        List<QuestStep> steps;
    }

    private static class QuestStep
    {
        int id;
        String description;
        boolean done;
    }
}