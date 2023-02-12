package me.mc_cloud.playerfreezer.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 841961703297777268L;
	
	public JavaPlugin plugin;
	public Map<String, Command> cmds = new HashMap<String, Command>();
	
	public CommandManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void createCommand(String command) {
		cmds.put(command, new Command(plugin, command));
	}
	
	public Command getCommand(String cmd) {
		return cmds.get(cmd);
	}

}
