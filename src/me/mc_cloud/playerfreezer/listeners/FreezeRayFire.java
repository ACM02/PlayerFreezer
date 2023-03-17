package me.mc_cloud.playerfreezer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class FreezeRayFire implements Listener {
	
	@EventHandler
	public abstract void onClick(PlayerInteractEvent e);
	
}
