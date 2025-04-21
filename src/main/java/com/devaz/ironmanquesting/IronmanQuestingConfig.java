// File: src/main/java/com/devaz/ironmanquesting/IronmanQuestingConfig.java
// Description: Config interface for RuneLite integration, with a sample toggle.
// ⚠️ Required by provideConfig() in IronmanQuestingPlugin. Expandable for future settings.

package com.devaz.ironmanquesting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ironmanquesting")
public interface IronmanQuestingConfig extends Config
{
    @ConfigItem(
            keyName = "enableDevTest",
            name = "Enable Dev Test Step",
            description = "Mark step 1 as done on startup (for testing only)"
    )
    default boolean enableDevTest()
    {
        return true;
    }
}