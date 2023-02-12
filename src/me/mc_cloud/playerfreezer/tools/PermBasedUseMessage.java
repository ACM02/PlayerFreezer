package me.mc_cloud.playerfreezer.tools;

import org.bukkit.command.CommandSender;

public class PermBasedUseMessage {
	
	public int priority;
	public String[] permissions;
	public String message;
	
	public PermBasedUseMessage(String message, int priority, String...permissions) {
		this.priority = priority;
		this.message = message;
		this.permissions = permissions;
	}
	
	public boolean hasPermissions(CommandSender sender) {
		boolean hasPermission = true;
		for (String perm : permissions) {
			if (!sender.hasPermission(perm)) {
				hasPermission = false;
			}
		}
		return hasPermission;
	}

}
