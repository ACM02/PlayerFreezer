package me.mc_cloud.playerfreezer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.mc_cloud.playerfreezer.actions.Freeze;
import me.mc_cloud.playerfreezer.actions.FreezeGun;
import me.mc_cloud.playerfreezer.actions.Unfreeze;
import me.mc_cloud.playerfreezer.listeners.CommandStopper;
import me.mc_cloud.playerfreezer.listeners.FreezeRayFire;
import me.mc_cloud.playerfreezer.listeners.FreezeRayHit;
import me.mc_cloud.playerfreezer.listeners.InventoryTrap;
import me.mc_cloud.playerfreezer.listeners.PlayerChat;
import me.mc_cloud.playerfreezer.listeners.PlayerInteract;
import me.mc_cloud.playerfreezer.listeners.PlayerLeave;
import me.mc_cloud.playerfreezer.listeners.PlayerMove;
import me.mc_cloud.playerfreezer.tools.CommandManager;
import me.mc_cloud.playerfreezer.tools.Messages;
import me.mc_cloud.playerfreezer.tools.UpdateChecker;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	/*
	 * TODO
	 * 
	 * Future plans: 
	 * More permissions (Different levels of frozen)
	 * Changing punishment type (Both default and specify in command)
	 * New punishment types (Temp ban, 
	 * Specify level of freeze in command? (Look around, etc)
	 * Send admins update notifications
	 * GUI menu for frozen players?
	 * Improve config readability (Add 'can' in front of several options)
	 * Version support
	 * 
	 * Requested features:
	 * Anti-leave feature that spams 'Close_Container' packets (ex: https://github.com/czQery/ToolKit)
	 * Enable/disable any command and rename too?
	 * Punishment customization (List of commands to run in config.yml
	 * playerFreezer reload command
	 * Config layout overhaul (https://imgur.com/a/3kDKrbs)
	 * 
	 */
	
	public static HashMap<String, Boolean> frozenPlayers = new HashMap<String, Boolean>();
	public static HashMap<String, Long> messageCooldowns = new HashMap<>(); // Stops players from getting spammed with "You're frozen" messages
	public static final ArrayList<String> ALLOWED_COMMANDS = new ArrayList<>();
	public static ItemStack freezeGun;
	public static float MOVEMENT_TOLERANCE = (float) 0.06;
	public static Main instance;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		instance = this;
		FileConfiguration config = getConfig();
		//saveDefaultConfig();
		
//		SimpleConfigManager manager = new SimpleConfigManager(instance);
//		manager.prepareFile("\\PlayerFreezer", "config.yml");
//		
//		
//		ConfigManager.translateConfig();
		
		config.addDefault("messages.onFreezeTarget", "&cYou have been frozen by staff, do not log out or you will be banned. Await further instruction.");
		config.addDefault("messages.onFreezeSender", "&aFroze %player%");
		config.addDefault("messages.unFreezeTarget", "&aYou have been unfrozen by staff");
		config.addDefault("messages.unFreezeSender", "&aUnfroze %player%");
		config.addDefault("messages.freezeWarning", "&cYou have been frozen, don't log out or you will be banned");
		config.addDefault("messages.blockCommand", "&cYou do not have permission to execute commands at this time");
		config.addDefault("messages.blockChat", "&cYou do not have permission to use chat at this time");
		config.addDefault("messages.blockInteract", "You do not have permission to do that at this time");
		config.addDefault("messages.playerNotFrozen", "&eThat player is not frozen");
		config.addDefault("messages.playerAlreadyFrozen", "&cThat player is already frozen");
		config.addDefault("messages.notFreezable", "&eThat player cannot be frozen");
		config.addDefault("messages.noPermission", "&cYou do not have permission to use that command");
		config.addDefault("messages.playerNotFound", "&cPlayer not found");
		config.addDefault("freezeGun", true);
		config.addDefault("canLookAround", true);
		config.addDefault("canInteract", true);
		config.addDefault("isBlind", true);
		config.addDefault("canChat", true);
		config.addDefault("inventoryTrap", false);
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
		
		config.options().copyDefaults(true);
		saveConfig();
		
		freezeGun = new ItemStack(Material.STICK, 1);
		ItemMeta meta = freezeGun.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Freeze gun");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.BLUE + "Shoots a freezing snowball");
		meta.setLore(lore);
		freezeGun.setItemMeta(meta);
		
		if (getConfig().getBoolean("canLookAround")) {
			MOVEMENT_TOLERANCE = (float) 0.06;
		} else {
			MOVEMENT_TOLERANCE = 0;
		}
		
		new PlayerMove(this);
		new PlayerLeave(this);
		new CommandStopper(this);
		new FreezeRayFire(this);
		new FreezeRayHit(this);
		if (!config.getBoolean("canInteract")) {
			new PlayerInteract(this);
		}
		if (!config.getBoolean("canChat")) {
			new PlayerChat(this);
		}
		if (config.getBoolean("inventoryTrap")) {
			new InventoryTrap(this);
		}
		
		File dir = getDataFolder();
		
		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("[" + getDescription().getName() + "] Could not create directory for plugin");
		
		frozenPlayers = (HashMap<String, Boolean>) load(new File(getDataFolder(), "frozenPlayers.dat"));
		
		if (frozenPlayers == null) {
			frozenPlayers = new HashMap<String, Boolean>();
		}
		
		Messages.init(instance);
		
		CommandManager cmdManager = new CommandManager(this);
		cmdManager.createCommand("freeze");
		cmdManager.getCommand("freeze").registerAction(new Freeze());
		cmdManager.getCommand("freeze").setUsageMessage(ChatColor.RED + "Improper usage: /freeze <player>");
		cmdManager.getCommand("freeze").setPermissionMessage(Messages.NO_PERMISSION);
		cmdManager.getCommand("freeze").addPermission("playerFreezer.freeze");
		cmdManager.createCommand("unfreeze");
		cmdManager.getCommand("unfreeze").registerAction(new Unfreeze());
		cmdManager.getCommand("unfreeze").setUsageMessage(ChatColor.RED + "Improper usage: /unfreeze <player>");
		cmdManager.getCommand("unfreeze").setPermissionMessage(Messages.NO_PERMISSION);
		cmdManager.getCommand("unfreeze").addPermission("playerFreezer.unfreeze");
		
		if (config.getBoolean("freezeGun")) {
			cmdManager.createCommand("freezegun");
			cmdManager.getCommand("freezegun").registerDefaultAction(new FreezeGun());
			cmdManager.getCommand("freezegun").setUsageMessage(ChatColor.RED + "Improper usage: /freezegun");
			cmdManager.getCommand("freezegun").setPermissionMessage(Messages.NO_PERMISSION);
			cmdManager.getCommand("freezegun").addPermission("playerFreezer.freeze-gun");
		}
		
		
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
	
	public static void freeze(Player target, CommandSender freezer) {
		if (target.hasPermission("playerFreezer.bypass")) {
			Messages.send(freezer, Messages.NOT_FREEZABLE, target);
			return;
		}
		if (!Main.frozenPlayers.keySet().contains(target.getUniqueId().toString())) {
			Main.frozenPlayers.put(target.getUniqueId().toString(), target.getAllowFlight());
			target.setAllowFlight(true);
			target.setFlying(true);
			if (instance.getConfig().getBoolean("inventoryTrap")) {
				target.openInventory(Bukkit.createInventory(null, 9, "You are frozen"));
			}
			if (instance.getConfig().getBoolean("isBlind")) {
				target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false, false));
			}
			Messages.send(freezer, Messages.ON_FREEZE_SENDER, target);
			Messages.send(target, Messages.ON_FREEZE_TARGET, target);
		} else {
			Messages.send(freezer, Messages.ALREADY_FROZEN, target);
		}
	}
	
//	public static void freeze(Player target, Player freezer) {
//		if (target.hasPermission("playerFreezer.bypass")) {
//			Messages.send(freezer, ChatColor.RED + "That player cannot be frozen");
//			return;
//		}
//		if (!Main.frozenPlayers.keySet().contains(target.getUniqueId().toString())) {
//			Main.frozenPlayers.put(target.getUniqueId().toString(), target.getAllowFlight());
//			target.setAllowFlight(true);
//			target.setFlying(true);
//			if (instance.getConfig().getBoolean("inventoryTrap")) {
//				target.openInventory(Bukkit.createInventory(null, 9, "You are frozen"));
//			}
//			if (instance.getConfig().getBoolean("blind")) {
//				target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false, false));
//			}
//			Messages.send(freezer, ChatColor.GREEN + "Froze " + target.getName());
//			Messages.send(target, Messages.ON_FREEZE_MESSAGE);
//		} else {
//			Messages.send(freezer, ChatColor.RED + "Player is already frozen");
//		}
//	}
	
	public static void unfreeze(Player target, CommandSender freezer) {
		if (Main.frozenPlayers.keySet().contains(target.getUniqueId().toString())) {
			target.setFlying(false);
			target.setAllowFlight(Main.frozenPlayers.get(target.getUniqueId().toString()));
			Main.frozenPlayers.remove(target.getUniqueId().toString());
			if (target.hasPotionEffect(PotionEffectType.BLINDNESS) && instance.getConfig().getBoolean("isBlind")) {
				target.removePotionEffect(PotionEffectType.BLINDNESS);
			}
			if (instance.getConfig().getBoolean("inventoryTrap")) {
				target.closeInventory();
			}
			Messages.send(freezer, Messages.UNFREEZE_SENDER, target);
			Messages.send(target, Messages.UNFREEZE_TARGET, target);
		} else {
			Messages.send(freezer, Messages.PLAYER_NOT_FROZEN, target);
		}
	}
}
