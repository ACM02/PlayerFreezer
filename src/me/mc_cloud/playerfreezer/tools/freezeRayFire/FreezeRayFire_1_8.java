package me.mc_cloud.playerfreezer.tools.freezeRayFire;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.listeners.FreezeRayFire;

public class FreezeRayFire_1_8 extends FreezeRayFire {

	private Main plugin;
	
	public FreezeRayFire_1_8(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (!e.getPlayer().hasPermission("playerFreezer.freeze-gun")) return;
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().getItemInHand().getType() == Material.STICK && 
					e.getPlayer().getItemInHand().hasItemMeta() &&
					e.getPlayer().getItemInHand().getItemMeta().hasLore() &&
					e.getPlayer().getItemInHand().getItemMeta().getLore().contains(Main.freezeGun.getItemMeta().getLore().get(0))) {
				Snowball ball = (Snowball) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getEyeLocation().add(0, -0.1, 0), EntityType.SNOWBALL);
				
				//ball.setGravity(false);
				//ball.setSilent(true);
				ball.setBounce(true);
				ball.setVelocity(e.getPlayer().getEyeLocation().getDirection().multiply(4));
				ball.setMetadata("freezeRay", new FixedMetadataValue(plugin, true));
				ball.setShooter(e.getPlayer());
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					Snowball newBall = ball;
					
					@Override
					public void run() {
						newBall.remove();
					}
				}, 30L);
			} 
			
		}

	}

}
