package me.mc_cloud.playerfreezer.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import me.mc_cloud.playerfreezer.Main;
import net.md_5.bungee.api.ChatColor;

public class FreezeRayHit implements Listener {

	public FreezeRayHit(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (e.getEntity().hasMetadata("freezeRay")) {
			e.setCancelled(true);
			if (e.getHitEntity() == null) {
				e.getEntity().remove();
				return;
			}

			if (e.getHitEntity() instanceof Player) {
				Player p = (Player) e.getHitEntity();
				if (!Main.frozenPlayers.keySet().contains(p.getUniqueId().toString())) {
					Main.frozenPlayers.put(p.getUniqueId().toString(), p.getAllowFlight());
					p.setAllowFlight(true);
					p.setFlying(true);
					if (e.getEntity().getShooter() instanceof Player) {
						Player shooter = (Player) e.getEntity().getShooter();
						shooter.sendMessage(ChatColor.GREEN + "Froze " + p.getName());
					}
					p.sendMessage(Main.ON_FREEZE_MESSAGE);
				} else {
					Player shooter = (Player) e.getEntity().getShooter();
					shooter.sendMessage(ChatColor.RED + "Player is already frozen");
				}
				
			}
			e.getEntity().remove();
			
		}
	}
	
}
