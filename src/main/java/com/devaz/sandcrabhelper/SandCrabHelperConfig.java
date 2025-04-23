package com.devaz.sandcrabhelper;

import net.runelite.client.config.*;

@ConfigGroup("sandcrabhelper")
public interface SandCrabHelperConfig extends Config
{
    @ConfigItem(
            keyName = "aggroWindowMinutes",
            name = "Aggro Timeout (minutes)",
            description = "Duration (in minutes) that crabs remain aggressive after being reset.\n" +
                    "Example: 10 = Crabs lose aggro after 10 minutes of inactivity.",
            position = 1
    )
    @Range(min = 1, max = 15)
    default int aggroWindowMinutes() { return 10; }

    @ConfigItem(
            keyName = "idleTimeoutSeconds",
            name = "Idle Timeout (seconds)",
            description = "Seconds without combat before considering you idle and ending the aggro window.\n" +
                    "Example: 30 = If you aren't attacked for 30s, aggro window ends.",
            position = 2
    )
    @Range(min = 5, max = 120)
    default int idleTimeoutSeconds() { return 30; }

    @ConfigItem(
            keyName = "showProgressBar",
            name = "Show Aggro Progress Bar",
            description = "Toggle a visual progress bar showing time left before crabs lose aggression.",
            position = 3
    )
    default boolean showProgressBar() { return true; }

    @ConfigItem(
            keyName = "showTimeSinceLastCombat",
            name = "Show Idle Timer",
            description = "Display how long it's been since you were last attacked by a crab.",
            position = 4
    )
    default boolean showTimeSinceLastCombat() { return true; }

    @ConfigItem(
            keyName = "playResetSound",
            name = "Play Reset Sound",
            description = "Plays a sound when either:\n" +
                    "• The aggression timer expires\n" +
                    "• You are idle for too long\n",
            position = 5
    )
    default boolean playResetSound() { return true; }

    @ConfigItem(
            keyName = "customSoundId",
            name = "Sound Effect ID",
            description = "RuneLite sound effect ID to use for alerts.\n" +
                    "Example: 3385 = default ding. Range: 0–9999.\n" +
                    "Tip: Search 'List of sound IDs OSRS Wiki' to find more.",
            position = 6
    )
    @Range(min = 0, max = 9999)
    default int customSoundId() { return 3385; }


    @ConfigItem(
            keyName = "enableAfkSoundLoop",
            name = "Enable AFK Sound Loop",
            description = "When idle is triggered, plays the alert sound on a loop until movement is detected.\n" +
                    "Recommended for long AFK sessions.",
            position = 7
    )
    default boolean enableAfkSoundLoop() { return false; }

    @ConfigItem(
            keyName = "restartAggroTimerNow",
            name = "Restart Aggro Timer",
            description = "Manually resets the aggression timer and idle tracking. Toggle to true, it resets instantly.",
            position = 8
    )
    default boolean restartAggroTimerNow() { return false; }

}
