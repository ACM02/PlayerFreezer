package me.mc_cloud.playerfreezer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.mc_cloud.playerfreezer.actions.Freeze;
import me.mc_cloud.playerfreezer.actions.Unfreeze;
import me.mc_cloud.playerfreezer.listeners.CommandStopper;
import me.mc_cloud.playerfreezer.listeners.PlayerLeave;
import me.mc_cloud.playerfreezer.listeners.PlayerMove;
import me.mc_cloud.playerfreezer.tools.CommandManager;
import me.mc_cloud.playerfreezer.tools.UpdateChecker;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	public static ArrayList<String> frozenPlayers = new ArrayList<String>();
	public static String ON_FREEZE_MESSAGE;
	public static String UNFREEZE_MESSAGE;
	public static String BLOCK_COMMAND_MESSAGE;
	public static final ArrayList<String> ALLOWED_COMMANDS = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		FileConfiguration config = getConfig();
		
		config.addDefault("onFreezeMessage", "&cYou have been frozen by staff, do not log out or you will be banned. Await further instruction.");
		config.addDefault("unFreezeMessage", "&aYou have been unfrozen by staff");
		config.addDefault("blockCommandMessage", "&cYou do not have permission to execute commands at this time");
		config.options().copyDefaults(true);
		
		List<String> allowedCommands = config.getStringList("allowedCommands");
		if (allowedCommands.isEmpty()) {
			allowedCommands = new ArrayList<>();
			allowedCommands.add("/sampleCommand");
			config.set("allowedCommands", allowedCommands);
			ALLOWED_COMMANDS.add("/sampleCommand");
		} else {
			for (String string : allowedCommands) {
				ALLOWED_COMMANDS.add(string);
			}
		}
		
		saveConfig();
		
		
		ON_FREEZE_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("onFreezeMessage"));
		UNFREEZE_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("unFreezeMessage"));
		BLOCK_COMMAND_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("blockCommandMessage"));
		
		new PlayerMove(this);
		new PlayerLeave(this);
		new CommandStopper(this);
		
		File dir = getDataFolder();
		
		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("[" + getDescription().getName() + "] Could not create directory for plugin");
		
		frozenPlayers = (ArrayList<String>) load(new File(getDataFolder(), "frozenPlayers.dat"));
		
		if (frozenPlayers == null) {
			frozenPlayers = new ArrayList<String>();
		}
		
		CommandManager cmdManager = new CommandManager(this);
		cmdManager.createCommand("freeze");
		cmdManager.getCommand("freeze").registerAction(new Freeze());
		cmdManager.getCommand("freeze").setUsageMessage(ChatColor.RED + "Improper usage: /freeze <player>");
		cmdManager.getCommand("freeze").setPermissionMessage(ChatColor.RED + "You do not have permission to execute this command");
		cmdManager.getCommand("freeze").addPermission("playerFreezer.use");
		cmdManager.createCommand("unfreeze");
		cmdManager.getCommand("unfreeze").registerAction(new Unfreeze());
		cmdManager.getCommand("unfreeze").setUsageMessage(ChatColor.RED + "Improper usage: /unfreeze <player>");
		cmdManager.getCommand("unfreeze").setPermissionMessage(ChatColor.RED + "You do not have permission to execute this command");
		cmdManager.getCommand("unfreeze").addPermission("playerFreezer.use");
		
		new UpdateChecker(this, 101362).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is a new update available (" + version + ")! Go to https://www.spigotmc.org/resources/playerfreezer.101362/ to download the new version.");
            }
        });
	}
	
	@Override
	public void onDisable() {
		save(frozenPlayers, new File(getDataFolder(), "frozenPlayers.dat"));
	}
	
	/**
	 * Saves an object to a file
	 * @param o	object to be saved
	 * @param f file to save the object to
	 */
	public void save(Object o, File f) {
        try {
        	
        	if (!f.exists()) {
        		f.createNewFile();
        	}
            FileOutputStream   stream = new FileOutputStream(f.getAbsoluteFile());
            ObjectOutputStream output = new ObjectOutputStream(stream);
            output.writeObject(o);
            output.close();
            return;
        }
        catch(NullPointerException e) {
            getLogger().warning("Unable to save files");
            return;
        }
        catch (IOException e) {
        	getLogger().warning("Unable to save files");
            return;
        }
	}
	
	/**
	 * Loads an object from a file
	 * @param f the file to read from
	 * @return the loaded object
	 */
	public Object load(File f) {
        try {
        	if (!f.exists()) return null;
            FileInputStream   stream = new FileInputStream(f.getAbsolutePath());
            ObjectInputStream input  = new ObjectInputStream(stream);
            Object object = input.readObject();
            input.close();
            return object;            
        }
        catch (IOException e) {
        	getLogger().warning("Unable to load files");
            return null;
        } catch (ClassNotFoundException e) {
        	getLogger().warning("Unable to load files");
        	return null;
		}
	}
}
