package me.mc_cloud.playerfreezer.tools;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import me.mc_cloud.playerfreezer.Main;
import net.md_5.bungee.api.ChatColor;

public class Messages {

	public static String ON_FREEZE_TARGET;
	public static String ON_FREEZE_SENDER;
	public static String UNFREEZE_TARGET;
	public static String UNFREEZE_SENDER;
	public static String FREEZE_WARNING;
	public static String BLOCK_COMMAND;
	public static String BLOCK_CHAT;;
	public static String BLOCK_INTERACT;
	public static String PLAYER_NOT_FROZEN;
	public static String NOT_FREEZABLE;
	public static String ALREADY_FROZEN;
	public static String NO_PERMISSION;
	public static String PLAYER_NOT_FOUND;
	
	public static void init(Main plugin) {
		ON_FREEZE_TARGET = getMessage(plugin, "messages.onFreezeTarget");
		ON_FREEZE_SENDER = getMessage(plugin, "messages.onFreezeSender");
		UNFREEZE_TARGET = getMessage(plugin, "messages.unFreezeTarget");
		UNFREEZE_SENDER = getMessage(plugin, "messages.unFreezeSender");
		FREEZE_WARNING = getMessage(plugin, "messages.freezeWarning");
		BLOCK_COMMAND = getMessage(plugin, "messages.blockCommand");
		BLOCK_CHAT = getMessage(plugin, "messages.blockChat");
		BLOCK_INTERACT = getMessage(plugin, "messages.blockInteract");
		PLAYER_NOT_FROZEN = getMessage(plugin, "messages.playerNotFrozen");
		NOT_FREEZABLE = getMessage(plugin, "messages.notFreezable");
		ALREADY_FROZEN = getMessage(plugin, "messages.playerAlreadyFrozen");
		NO_PERMISSION = getMessage(plugin, "messages.noPermission");
		PLAYER_NOT_FOUND = getMessage(plugin, "messages.playerNotFound");
	}
	
	private static String getMessage(Main plugin, String path) {
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
	}
	
	/*
	 * Valid placeholders:
	 * %player%
	 */
	
	
//	public static void send(CommandSender p, String message) {
//		if (message == null || message.equalsIgnoreCase("")) return;
//		p.sendMessage(message);
//	}
	
	public static void send(CommandSender p, String message, Player target) {
		if (message == null || message.equalsIgnoreCase("")) return;
		if (target == null) {
			p.sendMessage(message);
		} else {
			message = message.replace("%player%", target.getName());
			p.sendMessage(message);
		}
	}
	
	public static void send(Player p, String message, Player target) {
		if (message == null || message.equalsIgnoreCase("")) return;
		if (target == null) {
			p.sendMessage(message);
		} else {
			message = message.replace("%player%", target.getName());
			p.sendMessage(message);
		}
	}


	public static void send(HumanEntity p, String message, HumanEntity target) {
		if (p instanceof Player && target instanceof Player) send((Player) p, message, (Player) target);
	}
}
