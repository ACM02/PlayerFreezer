package me.mc_cloud.playerfreezer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.BanList.Type;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.mc_cloud.playerfreezer.Main;

public class PlayerLeave implements Listener {

	public Main plugin;
	
	public PlayerLeave(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e) {
		if (Main.frozenPlayers.contains(e.getPlayer().getUniqueId().toString())) {
			Bukkit.getBanList(Type.NAME).addBan(e.getPlayer().getName(), "You disconnected while frozen by staff", null, "100");
		}
	}
	
}
