package me.mc_cloud.playerfreezer.listeners;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Messages;
import me.mc_cloud.playerfreezer.tools.Utils;

public class InventoryTrap implements Listener {
	
	public Main plugin;
	
	public InventoryTrap(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getPlayer().hasPermission("playerFreezer.bypass")) return;
		if (Main.frozenPlayers.keySet().contains(e.getPlayer().getUniqueId().toString())) {
			
			Bukkit.getScheduler().runTask(Main.instance, new Runnable(){
			    @Override
			    public void run() {
			        //executions
					e.getPlayer().openInventory(Bukkit.createInventory(null, 9, "You are frozen"));  
			    }
			           
			});
			
			if (!Main.messageCooldowns.containsKey(e.getPlayer().getUniqueId().toString()) ||
					Main.messageCooldowns.get(e.getPlayer().getUniqueId().toString()) <= new Date().getTime()) {
				Messages.send(e.getPlayer(), Messages.FREEZE_WARNING, e.getPlayer());
				Main.messageCooldowns.put(e.getPlayer().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked().hasPermission("playerFreezer.bypass")) return;
		if (Main.frozenPlayers.keySet().contains(e.getWhoClicked().getUniqueId().toString())) {
			if (e.getView().getTitle().equals("You are frozen")) {
				e.setCancelled(true);
				if (!Main.messageCooldowns.containsKey(e.getWhoClicked().getUniqueId().toString()) ||
						Main.messageCooldowns.get(e.getWhoClicked().getUniqueId().toString()) <= new Date().getTime()) {
					Messages.send(e.getWhoClicked(), Messages.FREEZE_WARNING, e.getWhoClicked());
					Main.messageCooldowns.put(e.getWhoClicked().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (e.getWhoClicked().hasPermission("playerFreezer.bypass")) return;
		if (Main.frozenPlayers.keySet().contains(e.getWhoClicked().getUniqueId().toString())) {
			if (e.getView().getTitle().equals("You are frozen")) {
				e.setCancelled(true);
				if (!Main.messageCooldowns.containsKey(e.getWhoClicked().getUniqueId().toString()) ||
						Main.messageCooldowns.get(e.getWhoClicked().getUniqueId().toString()) <= new Date().getTime()) {
					Messages.send(e.getWhoClicked(), Messages.FREEZE_WARNING, e.getWhoClicked());
					Main.messageCooldowns.put(e.getWhoClicked().getUniqueId().toString(), Utils.todayPlus(0, 0, 0, 5));
				}
			}
		}
	}

}
