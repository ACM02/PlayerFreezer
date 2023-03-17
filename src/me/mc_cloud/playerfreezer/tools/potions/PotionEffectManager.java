package me.mc_cloud.playerfreezer.tools.potions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public abstract class PotionEffectManager {
	
	public abstract void applyPotionEffects(Player p, PotionEffectType potionType, int amplifier, int duration, boolean ambient, boolean particles, boolean icon);

}
