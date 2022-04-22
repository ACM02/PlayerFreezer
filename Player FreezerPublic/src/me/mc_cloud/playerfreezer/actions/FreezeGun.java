package me.mc_cloud.playerfreezer.actions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Action;

public class FreezeGun extends Action {

	public FreezeGun() {
		consoleExecutable = false;
	}
	
	@Override
	public void run(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		p.getInventory().addItem(Main.freezeGun);
	}

}
