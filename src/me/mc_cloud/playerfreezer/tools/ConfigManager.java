package me.mc_cloud.playerfreezer.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.mc_cloud.playerfreezer.Main;

public class ConfigManager {

	public static String newestVersion = "1.0";
	
	public static void translateConfig() {
		String version = Main.instance.getConfig().getString("configVersion");
		if (version == null) {
			fromVersion0();
			//Main.instance.getLogger().severe("Unable to determine config version. Using default values.");
			
		}
	}
	
//	config.addDefault("onFreezeMessage", "&cYou have been frozen by staff, do not log out or you will be banned. Await further instruction.");
//	config.addDefault("unFreezeMessage", "&aYou have been unfrozen by staff");
//	config.addDefault("freezeWarning", "&cYou have been frozen, don't log out or you will be banned");
//	config.addDefault("blockCommandMessage", "&cYou do not have permission to execute commands at this time");
//	config.addDefault("freezeGun", true);
//	config.addDefault("lookAround", true);
//	config.addDefault("interact", true);
//	config.addDefault("blind", true);
//	config.addDefault("chat", true);
//	config.addDefault("inventoryTrap", false);
//	config.options().copyDefaults(true);
//	
//	List<String> allowedCommands = config.getStringList("allowedCommands");
//	if (allowedCommands.isEmpty()) {
//		allowedCommands = new ArrayList<>();
//		allowedCommands.add("/sampleCommand");
//		config.set("allowedCommands", allowedCommands);
//		ALLOWED_COMMANDS.add("/sampleCommand");
//	} else {
//		for (String string : allowedCommands) {
//			ALLOWED_COMMANDS.add(string);
//		}
//	}
	
	private static void fromVersion0() {
		FileConfiguration config = Main.instance.getConfig();
		String onFreezeTarget = config.getString("onFreezeMessage");
		System.out.println(Main.instance.getConfig().getString("onFreezeMessage"));
		String unFreezeTarget = config.getString("unFreezeMessage");
		String freezeWarning = config.getString("freezeWarning");
		String blockCommand = config.getString("blockCommandMessage");
		boolean freezeGun = config.getBoolean("freezeGun");
		boolean canLookAround = config.getBoolean("lookAround");
		boolean canInteract = config.getBoolean("interact");
		boolean isBlind = config.getBoolean("blind");
		boolean canChat = config.getBoolean("chat");
		boolean inventoryTrap = config.getBoolean("inventoryTrap");
		//List<String> allowedCommands = config.getStringList("allowedCommands");
		
		File configFile = new File(Main.instance.getDataFolder(), "config.yml");
		configFile.delete();
//		if(!configFile.exists()) {
//		    try {
//		        configFile.createNewFile();
//		    } catch(IOException e) {
//
//		    }
//		}
		Main.instance.reloadConfig();
		Main.instance.saveDefaultConfig();
		//config = YamlConfiguration.loadConfiguration(configFile);
		config = Main.instance.getConfig();
		config.addDefault("messages.onFreezeTarget", onFreezeTarget);
		config.addDefault("messages.unFreezeTarget", unFreezeTarget);
		config.addDefault("messages.freezeWarning", freezeWarning);
		config.addDefault("messages.blockCommand", blockCommand);
		config.addDefault("freezeGun", freezeGun);
		config.addDefault("canLookAround", canLookAround);
		config.addDefault("canInteract", canInteract);
		config.addDefault("isBlind", isBlind);
		config.addDefault("canChat", canChat);
		config.addDefault("inventoryTrap", inventoryTrap);
		//config.addDefault("allowedCommands", allowedCommands);
		System.out.println(Main.instance.getConfig().getString("messages.onFreezeTarget"));
		Main.instance.getConfig().options().copyDefaults(true);
	}
	
}
