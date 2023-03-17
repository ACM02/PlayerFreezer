package me.mc_cloud.playerfreezer.tools.freezeRayHit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.listeners.FreezeRayHit;

public class FreezeRayHit_1_8 extends FreezeRayHit {

	public FreezeRayHit_1_8(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (!Main.instance.getConfig().getBoolean("freezeGun")) return;
		if (e.getDamager().hasMetadata("freezeRay")) {
			Entity damaged = e.getEntity();
		    Entity damageEntity = e.getDamager();
		 
		    if(damaged instanceof Player)
		    if(damageEntity instanceof Snowball) {
		    	Snowball snowball = (Snowball) damageEntity;
		    	Player playerHit = (Player)damaged;
		    	ProjectileSource thrower = snowball.getShooter();
		    	if (thrower instanceof Player) {
		    		Player shooter = (Player) thrower;
		    		Main.freeze(playerHit, shooter);
		    	}
		    	snowball.remove();
		    	e.setCancelled(true);
		            
		    }
		}
	}
	
}
