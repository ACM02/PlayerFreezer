package me.mc_cloud.playerfreezer.tools.freezeRayHit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.listeners.FreezeRayHit;

public class FreezeRayHit_1_16 extends FreezeRayHit {

	public FreezeRayHit_1_16(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (!Main.instance.getConfig().getBoolean("freezeGun")) return;
		if (e.getEntity().hasMetadata("freezeRay")) {
			e.setCancelled(true);
			if (e.getHitEntity() == null) {
				e.getEntity().remove();
				return;
			}

			if (e.getHitEntity() instanceof Player) {
				Player p = (Player) e.getHitEntity();
				Player shooter = (Player) e.getEntity().getShooter();
				Main.freeze(p, shooter);
			}
			e.getEntity().remove();
		}
	}
	
}
