package me.mc_cloud.playerfreezer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.mc_cloud.playerfreezer.Main;

public class CommandStopper implements Listener {

	public Main plugin;
	
	public CommandStopper(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void commandStopper(PlayerCommandPreprocessEvent e) {
		if (Main.ALLOWED_COMMANDS.contains(e.getMessage().split(" ")[0]) || e.getMessage().split(" ")[0].contains("freeze")) return;
		if (Main.frozenPlayers.keySet().contains(e.getPlayer().getUniqueId().toString()) && !e.getPlayer().hasPermission("playerFreezer.bypass")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(Main.BLOCK_COMMAND_MESSAGE);
		}
	}

}
