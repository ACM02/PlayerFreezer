package me.mc_cloud.playerfreezer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.mc_cloud.playerfreezer.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandStopper implements Listener {

	public Main plugin;
	
	public CommandStopper(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void commandStopper(PlayerCommandPreprocessEvent e) {
		if (Main.frozenPlayers.contains(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to execute commands at this time");
		}
	}

}
