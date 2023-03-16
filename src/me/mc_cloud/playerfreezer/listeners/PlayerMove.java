package me.mc_cloud.playerfreezer.listeners;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Messages;
import me.mc_cloud.playerfreezer.tools.Utils;

public class PlayerMove implements Listener {
	
	public Main plugin;
	
	public PlayerMove(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getPlayer().hasPermission("playerFreezer.bypass")) return;
		if (Main.frozenPlayers.keySet().contains(e.getPlayer().getUniqueId().toString())) {
			if (Main.MOVEMENT_TOLERANCE == 0) {
				e.setCancelled(true);
				e.setTo(e.getFrom());
				if (!Main.messageCooldowns.containsKey(e.getPlayer().getUniqueId().toString()) ||
						Main.messageCooldowns.get(e.getPlayer().getUniqueId().toString()) <= new Date().getTime()) {
					Messages.send(e.getPlayer(), Messages.FREEZE_WARNING, e.getPlayer());
					Main.messageCooldowns.put(e.getPlayer().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
				}
			} else if (e.getFrom().distance(e.getTo()) > Main.MOVEMENT_TOLERANCE) {
				e.setTo(e.getFrom());
				if (!Main.messageCooldowns.containsKey(e.getPlayer().getUniqueId().toString()) ||
						Main.messageCooldowns.get(e.getPlayer().getUniqueId().toString()) <= new Date().getTime()) {
					Messages.send(e.getPlayer(), Messages.FREEZE_WARNING, e.getPlayer());
					Main.messageCooldowns.put(e.getPlayer().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
				}
			}
		}
	}

}
