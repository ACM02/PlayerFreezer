package me.mc_cloud.playerfreezer.tools.potions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potions_1_13 extends PotionEffectManager {

	@Override
	public void applyPotionEffects(Player p, PotionEffectType potionType, int amplifier, int duration, boolean ambient,
			boolean particles, boolean icon) {
		p.addPotionEffect(new PotionEffect(potionType, amplifier, duration, ambient, particles, icon));
	}

}
