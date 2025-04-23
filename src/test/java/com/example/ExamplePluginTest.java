package com.example;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(com.devaz.sandcrabhelper.SandCrabHelperPlugin.class);
		RuneLite.main(args);
	}
}
