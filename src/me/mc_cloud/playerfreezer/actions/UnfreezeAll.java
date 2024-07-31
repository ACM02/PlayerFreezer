package me.mc_cloud.playerfreezer.actions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Action;
import me.mc_cloud.playerfreezer.tools.ArgType;
import me.mc_cloud.playerfreezer.tools.Argument;

public class UnfreezeAll extends Action {
	
	public UnfreezeAll() {
		addArg(new Argument(ArgType.PRESET, 0, "all"));
	}
	
	@Override
	public void run(CommandSender sender, String[] args) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Main.frozenPlayers.keySet().contains(p.getUniqueId().toString()))
				Main.unfreeze(p, sender);
		}
		sender.sendMessage(ChatColor.GREEN + "Unfroze all players.");
	}

}
