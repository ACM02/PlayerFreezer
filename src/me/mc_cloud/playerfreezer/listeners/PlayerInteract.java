package me.mc_cloud.playerfreezer.listeners;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Messages;
import me.mc_cloud.playerfreezer.tools.Utils;

public class PlayerInteract implements Listener {
	
	public Main plugin;
	
	public PlayerInteract(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (plugin.getConfig().getBoolean("canInteract")) return;
		if (e.getPlayer().hasPermission("playerFreezer.bypass")) return;
		if (Main.frozenPlayers.keySet().contains(e.getPlayer().getUniqueId().toString())) {
			e.setCancelled(true);
			if (!Main.messageCooldowns.containsKey(e.getPlayer().getUniqueId().toString()) ||
					Main.messageCooldowns.get(e.getPlayer().getUniqueId().toString()) <= new Date().getTime()) {
				Messages.send(e.getPlayer(), Messages.BLOCK_INTERACT, e.getPlayer());
				Main.messageCooldowns.put(e.getPlayer().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
			}
		}
	}

}
