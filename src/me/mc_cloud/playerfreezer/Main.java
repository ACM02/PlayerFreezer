package me.mc_cloud.playerfreezer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import me.mc_cloud.playerfreezer.actions.Freeze;
import me.mc_cloud.playerfreezer.actions.FreezeAll;
import me.mc_cloud.playerfreezer.actions.FreezeGun;
import me.mc_cloud.playerfreezer.actions.Reload;
import me.mc_cloud.playerfreezer.actions.Unfreeze;
import me.mc_cloud.playerfreezer.actions.UnfreezeAll;
import me.mc_cloud.playerfreezer.listeners.CommandStopper;
import me.mc_cloud.playerfreezer.listeners.InventoryTrap;
import me.mc_cloud.playerfreezer.listeners.PlayerChat;
import me.mc_cloud.playerfreezer.listeners.PlayerInteract;
import me.mc_cloud.playerfreezer.listeners.PlayerLeave;
import me.mc_cloud.playerfreezer.listeners.PlayerMove;
import me.mc_cloud.playerfreezer.tools.CommandManager;
import me.mc_cloud.playerfreezer.tools.Messages;
import me.mc_cloud.playerfreezer.tools.UpdateChecker;
import me.mc_cloud.playerfreezer.tools.Utils;
import me.mc_cloud.playerfreezer.tools.freezeRayFire.FreezeRayFire_1_13;
import me.mc_cloud.playerfreezer.tools.freezeRayFire.FreezeRayFire_1_8;
import me.mc_cloud.playerfreezer.tools.freezeRayHit.FreezeRayHit_1_16;
import me.mc_cloud.playerfreezer.tools.freezeRayHit.FreezeRayHit_1_8;
import me.mc_cloud.playerfreezer.tools.potions.PotionEffectManager;
import me.mc_cloud.playerfreezer.tools.potions.Potions_1_13;
import me.mc_cloud.playerfreezer.tools.potions.Potions_1_8;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	/*
	 * TODO
	 * 
	 * Future plans: 
	 * More permissions (Different levels of frozen)
	 * New punishment types (Temp ban, 
	 * Specify level of freeze in command? (Look around, etc)
	 * Send admins update notifications
	 * GUI menu for freezing players?
	 * Improve config readability (comments)
	 * Version support
	 * 
	 * Requested features:
	 * Anti-leave feature that spams 'Close_Container' packets (ex: https://github.com/czQery/ToolKit)
	 * Enable/disable any command and rename too?
	 * Run on unfreeze commands
	 * Config layout overhaul (https://imgur.com/a/3kDKrbs)
	 * Freeze/unfreeze All
	 * 
	 */
	
	public static HashMap<String, Boolean> frozenPlayers = new HashMap<String, Boolean>();
	public static HashMap<String, Long> messageCooldowns = new HashMap<>(); // Stops players from getting spammed with "You're frozen" messages
	public static final ArrayList<String> ALLOWED_COMMANDS = new ArrayList<>();
	public static final ArrayList<String> PUNISH_COMMANDS = new ArrayList<>();
	public static ItemStack freezeGun;
	public static float MOVEMENT_TOLERANCE = (float) 0.06;
	public static Main instance;
	public static PotionEffectManager potionApplyer;
	public static CommandManager cmdManager;
	
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
		
		initConfig();
		
		List<String> allowedCommands = config.getStringList("allowedCommands");
		if (!allowedCommands.isEmpty()) {
			for (String string : allowedCommands) {
				ALLOWED_COMMANDS.add(string);
			}
		}
		
		List<String> punishCommands = config.getStringList("punishCommands");
		if (!punishCommands.isEmpty()) {
			for (String string : punishCommands) {
				PUNISH_COMMANDS.add(string);
			}
		}
		
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
		
		initVersionSpecifics();
		
		new PlayerMove(this);
		new PlayerLeave(this);
		new CommandStopper(this);
		new PlayerInteract(this);
		new PlayerChat(this);
		new InventoryTrap(this);
		
		File dir = getDataFolder();
		
		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("[" + getDescription().getName() + "] Could not create directory for plugin");
		
		frozenPlayers = (HashMap<String, Boolean>) Utils.load(new File(getDataFolder(), "frozenPlayers.dat"));
		
		if (frozenPlayers == null) {
			frozenPlayers = new HashMap<String, Boolean>();
		}
		
		Messages.init(instance);
		
		cmdManager = new CommandManager(this);
		cmdManager.createCommand("freeze");
		cmdManager.getCommand("freeze").registerAction(new Freeze());
		cmdManager.getCommand("freeze").registerAction(new FreezeAll());
		cmdManager.getCommand("freeze").registerAction(new Reload());
		cmdManager.getCommand("freeze").setUsageMessage(ChatColor.RED + "Improper usage: /freeze <player/all>");
		cmdManager.getCommand("freeze").setPermissionMessage(Messages.NO_PERMISSION);
		cmdManager.getCommand("freeze").addPermission("playerFreezer.freeze");
		cmdManager.createCommand("unfreeze");
		cmdManager.getCommand("unfreeze").registerAction(new UnfreezeAll());
		cmdManager.getCommand("unfreeze").registerAction(new Unfreeze());
		cmdManager.getCommand("unfreeze").setUsageMessage(ChatColor.RED + "Improper usage: /unfreeze <player/all>");
		cmdManager.getCommand("unfreeze").setPermissionMessage(Messages.NO_PERMISSION);
		cmdManager.getCommand("unfreeze").addPermission("playerFreezer.unfreeze");
		
		cmdManager.createCommand("freezegun");
		cmdManager.getCommand("freezegun").registerDefaultAction(new FreezeGun());
		cmdManager.getCommand("freezegun").setUsageMessage(ChatColor.RED + "Improper usage: /freezegun");
		cmdManager.getCommand("freezegun").setPermissionMessage(Messages.NO_PERMISSION);
		cmdManager.getCommand("freezegun").addPermission("playerFreezer.freeze-gun");
		
		new UpdateChecker(this, 101362).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is a new update available (" + version + ")! Go to https://www.spigotmc.org/resources/playerfreezer.101362/ to download the new version.");
            }
        });
	}
	
	private void initConfig() {
		FileConfiguration config = getConfig();
		config.addDefault("messages.onFreezeTarget", "&cYou have been frozen by staff, do not log out or you will be banned. Await further instruction.");
		config.addDefault("messages.onFreezeSender", "&aFroze %player%");
		config.addDefault("messages.unFreezeTarget", "&aYou have been unfrozen by staff");
		config.addDefault("messages.unFreezeSender", "&aUnfroze %player%");
		config.addDefault("messages.freezeWarning", "&cYou have been frozen, don't log out or you will be banned");
		config.addDefault("messages.blockCommand", "&cYou do not have permission to execute commands at this time");
		config.addDefault("messages.blockChat", "&cYou do not have permission to use chat at this time");
		config.addDefault("messages.blockInteract", "&cYou do not have permission to do that at this time");
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
		List<String> punishCommands = new ArrayList<String>();
		punishCommands.add("ban %player% You disconnected while frozen by staff");
		config.addDefault("punishCommands", punishCommands);
		List<String> allowedCommands = new ArrayList<String>();
		allowedCommands.add("/sampleCommand");
		config.addDefault("allowedCommands", allowedCommands);
		config.options().copyDefaults(true);
		saveConfig();
	}

	private void initVersionSpecifics() {
		String wholeVersion = getServerVersion();
		if (wholeVersion.contains("1.19")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_16(this);
		} else if (wholeVersion.contains("1.18")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_16(this);
		} else if (wholeVersion.contains("1.17")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_16(this);
		} else if (wholeVersion.contains("1.16")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_16(this);
		} else if (wholeVersion.contains("1.15")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.14")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.13")) {
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.12")) {
			potionApplyer = new Potions_1_8();
			new FreezeRayFire_1_8(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.11")) {
			potionApplyer = new Potions_1_8();
			new FreezeRayFire_1_8(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.10")) {
			potionApplyer = new Potions_1_8();
			new FreezeRayFire_1_8(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.9")) {
			potionApplyer = new Potions_1_8();
			new FreezeRayFire_1_8(this);
			new FreezeRayHit_1_8(this);
		} else if (wholeVersion.contains("1.8")) {
			potionApplyer = new Potions_1_8();
			new FreezeRayFire_1_8(this);
			new FreezeRayHit_1_8(this);
		} else {
			getLogger().warning("Running unsupported version. Attempting to load...");
			potionApplyer = new Potions_1_13();
			new FreezeRayFire_1_13(this);
			new FreezeRayHit_1_16(this);
		}
		
	}

	@Override
	public void onDisable() {
		Utils.save(frozenPlayers, new File(getDataFolder(), "frozenPlayers.dat"));
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
				potionApplyer.applyPotionEffects(target, PotionEffectType.BLINDNESS, 1000000, 0, false, false, false);
			}
			Messages.send(freezer, Messages.ON_FREEZE_SENDER, target);
			Messages.send(target, Messages.ON_FREEZE_TARGET, target);
		} else {
			Messages.send(freezer, Messages.ALREADY_FROZEN, target);
		}
	}
	
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
	
	private static String getServerVersion() {
	    return Bukkit.getBukkitVersion().split("-")[0];
	}

	public static void reload() {
		instance.reloadConfig();
		instance.initConfig();
		instance.reloadValues();
		Messages.init(instance);
		updateFrozens();
	}

	private static void updateFrozens() {
		for (String s : frozenPlayers.keySet()) {
			Player p = Bukkit.getPlayer(UUID.fromString(s));
			if (instance.getConfig().getBoolean("isBlind") && !p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
				potionApplyer.applyPotionEffects(p, PotionEffectType.BLINDNESS, 1000000, 0, false, false, false);
			} else if (!instance.getConfig().getBoolean("isBlind") && p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
				p.removePotionEffect(PotionEffectType.BLINDNESS);
			}
			if (instance.getConfig().getBoolean("inventoryTrap")) {
				p.openInventory(Bukkit.createInventory(null, 9, "You are frozen")); 
			}
		}
	}

	private void reloadValues() {
		FileConfiguration config = getConfig();
		List<String> allowedCommands = config.getStringList("allowedCommands");
		if (!allowedCommands.isEmpty()) {
			for (String string : allowedCommands) {
				if (!ALLOWED_COMMANDS.contains(string))
					ALLOWED_COMMANDS.add(string);
			}
		}
		
		List<String> punishCommands = config.getStringList("punishCommands");
		if (!punishCommands.isEmpty()) {
			for (String string : punishCommands) {
				if (!PUNISH_COMMANDS.contains(string))
					PUNISH_COMMANDS.add(string);
			}
		}
		
		if (getConfig().getBoolean("canLookAround")) {
			MOVEMENT_TOLERANCE = (float) 0.06;
		} else {
			MOVEMENT_TOLERANCE = 0;
		}
	}
}
