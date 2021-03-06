package me.mc_cloud.playerfreezer.actions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Action;
import me.mc_cloud.playerfreezer.tools.ArgType;
import me.mc_cloud.playerfreezer.tools.Argument;
import net.md_5.bungee.api.ChatColor;

public class Unfreeze extends Action {

	public Unfreeze() {
		addArg(new Argument(ArgType.PLAYER, 0));
	}
	
	@Override
	public void run(CommandSender sender, String[] args) {
		Player p;
		if (Bukkit.getPlayer(args[0]) != null) {
			p = Bukkit.getPlayer(args[0]);
		} else {
			sender.sendMessage(ChatColor.RED + "Player not found");
			return;
		}
		if (Main.frozenPlayers.keySet().contains(p.getUniqueId().toString())) {
			p.setFlying(false);
			p.setAllowFlight(Main.frozenPlayers.get(p.getUniqueId().toString()));
			Main.frozenPlayers.remove(p.getUniqueId().toString());
			sender.sendMessage(ChatColor.GREEN + "Unfroze " + args[0]);
			p.sendMessage(Main.UNFREEZE_MESSAGE);
		} else {
			sender.sendMessage(ChatColor.YELLOW + "That player is not frozen");
		}
	}

}
