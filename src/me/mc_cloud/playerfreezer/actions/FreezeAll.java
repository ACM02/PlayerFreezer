package me.mc_cloud.playerfreezer.actions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Action;
import me.mc_cloud.playerfreezer.tools.ArgType;
import me.mc_cloud.playerfreezer.tools.Argument;

public class FreezeAll extends Action {
	
	public FreezeAll() {
		Argument arg = new Argument(ArgType.PRESET, 0, "all");
		arg.extraTabArgs.add("all");
		addArg(arg);
	}
	
	@Override
	public void run(CommandSender sender, String[] args) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.hasPermission("playerFreezer.bypass")) {
				Main.freeze(p, sender);
			}
		}
		sender.sendMessage(ChatColor.GREEN + "Froze all players.");
	}

}
