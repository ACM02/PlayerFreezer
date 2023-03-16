package me.mc_cloud.playerfreezer.actions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Action;
import me.mc_cloud.playerfreezer.tools.ArgType;
import me.mc_cloud.playerfreezer.tools.Argument;
import me.mc_cloud.playerfreezer.tools.Messages;

public class Unfreeze extends Action {
	
	public Unfreeze() {
		addArg(new Argument(ArgType.PLAYER, 0));
	}
	
	@Override
	public void run(CommandSender sender, String[] args) {
		Player p = Bukkit.getPlayer(args[0]);
		if (p == null) {
			Messages.send(sender, Messages.PLAYER_NOT_FOUND, null);
			return;
		}
		Main.unfreeze(p, sender);
	}

}
