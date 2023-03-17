package me.mc_cloud.playerfreezer.actions;

import org.bukkit.command.CommandSender;

import me.mc_cloud.playerfreezer.Main;
import me.mc_cloud.playerfreezer.tools.Action;
import me.mc_cloud.playerfreezer.tools.ArgType;
import me.mc_cloud.playerfreezer.tools.Argument;
import net.md_5.bungee.api.ChatColor;

public class Reload extends Action {

	public Reload() {
		addArg(new Argument(ArgType.PRESET, 0, "reload"));
	}
	
	@Override
	public void run(CommandSender sender, String[] args) {
		Main.reload();
		sender.sendMessage(ChatColor.GREEN + "Reloaded successfully");
		
	}

}
