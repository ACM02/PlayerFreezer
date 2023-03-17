package me.mc_cloud.playerfreezer.listeners;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Messages;
import me.mc_cloud.playerfreezer.tools.Utils;

public class PlayerChat implements Listener {
	
	public Main plugin;
	
	public PlayerChat(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (plugin.getConfig().getBoolean("canChat")) return;
		if (e.getPlayer().hasPermission("playerFreezer.bypass")) return;
		if (Main.frozenPlayers.keySet().contains(e.getPlayer().getUniqueId().toString())) {
			e.setCancelled(true);
			if (!Main.messageCooldowns.containsKey(e.getPlayer().getUniqueId().toString()) ||
					Main.messageCooldowns.get(e.getPlayer().getUniqueId().toString()) <= new Date().getTime()) {
				Messages.send(e.getPlayer(), Messages.BLOCK_CHAT, e.getPlayer());
				Main.messageCooldowns.put(e.getPlayer().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
			}
		}
	}

}
