package me.mc_cloud.playerfreezer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.mc_cloud.playerfreezer.Main;
import net.md_5.bungee.api.ChatColor;

public class PlayerMove implements Listener {
	
	public Main plugin;
	
	public PlayerMove(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (Main.frozenPlayers.contains(e.getPlayer().getUniqueId().toString())) {
			if (e.getFrom().distance(e.getTo()) > 0.06) {
				e.setTo(e.getFrom());
				e.getPlayer().sendMessage(ChatColor.RED + "You have been frozen, don't log out or you will be banned");
			}
		}
	}

}
