package me.mc_cloud.playerfreezer.tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Command implements CommandExecutor, TabCompleter {

	public String improperUseMessage;
	public String noPermissionMessage = ChatColor.RED + "You do not have permission to execute this command";
	private List<PermBasedUseMessage> permBasedUseMessages = new ArrayList<PermBasedUseMessage>();
	public JavaPlugin plugin;
	
	private List<String> permissions = new ArrayList<String>();
	private List<String> actionPermissions = new ArrayList<String>();
	
	public boolean playerExecutable = true;
	public boolean consoleExecutable = true;
	private List<List<Argument>> arguments = new ArrayList<>();
	private List<Action> actions = new ArrayList<>();
	private Action defaultAction;
	
	public Command(JavaPlugin plugin, String command) {
		this.plugin = plugin;
		improperUseMessage = ChatColor.RED + "Improper usage. Type " + ChatColor.YELLOW + "/" + command + " help" + ChatColor.RED + " for help";
		
		plugin.getCommand(command).setExecutor(this);
		plugin.getCommand(command).setTabCompleter(this);
	}
	
	public void registerAction(Action action) {
		if (actions.size() > 1) {
			boolean added = false;
			for (int i = actions.size() - 1; i > -1; i--) {
				if (action.isGreaterThan(actions.get(i))) {
					actions.add(i, action);
					added = true;
					i = -1;
				}
			}
			if (!added) {
				actions.add(action);
			}
		} else {
			actions.add(action);
		}
		
		for (String perm : action.permissions) {
			actionPermissions.add(perm);
		}
		if (action.getHighestArg() > arguments.size() - 1) {
			for (int i = 0; i < action.getHighestArg() + 1; i++) {
				if (arguments.size() <= i) {
					arguments.add(new ArrayList<Argument>());
				}
			}
		}
		for (Argument newArg : action.getArgs()) {
			arguments.get(newArg.pos).add(newArg);
		}
		for (int i = 0; i < arguments.size(); i++) {
			for (int j = 0; j < arguments.get(i).size(); j++) {
				for (int k = 0; k < arguments.get(i).size(); k++) {
					if (j != k)
						if (arguments.get(i).get(j).equals(arguments.get(i).get(k))) arguments.get(i).remove(k);
				}
			}
		}
	}
	
	public void registerDefaultAction(Action a) {
		defaultAction = a;
	}
	
	public void setUsageMessage(String message) {
		improperUseMessage = message;
	}
	
	public void addPermission(String permission) {
		permissions.add(permission);
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void setPermissionMessage(String message) {
		noPermissionMessage = message;
	}
	
	public void addPermissionBasedUseMessage(String message, int priority, String...permissions) {
		permBasedUseMessages.add(new PermBasedUseMessage(message, priority, permissions));
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				runHelp(sender, label, args);
				return false;
			}
		}

		if (sender instanceof Player && !playerExecutable) {
			sender.sendMessage(ChatColor.RED + "Only console can execute this command");
			return false;
		}
		if (!(sender instanceof Player) && !consoleExecutable) {
			sender.sendMessage(ChatColor.RED + "Only players can execute this command");
			return false;
		}
		if (sender instanceof Player) {
			if (!hasPermissions((Player) sender) && !hasActionPermissions((Player) sender)) {
				sender.sendMessage(noPermissionMessage);
				return false;
			}
		}
		
		if (args.length == 0 && defaultAction != null) {
			defaultAction.run(sender, args);
			return false;
		} else if (args.length == 0) {
			sendImproperUseMessage(sender);
			return false;
		}
		
		List<Argument> finalArgList = orderArgsCmd(args);
		
		if (finalArgList == null || finalArgList.size() < args.length) {
			sendImproperUseMessage(sender);
			return false;
		}
		for (Action a : actions) {
			if (a.getHighestArg() == finalArgList.size() - 1) {
				boolean same = true;
				for (int i = 0; i < a.getHighestArg() + 1; i++) {
					boolean matchesPosition = false;
					for (Argument argument : a.getArgs()) {
						if (argument.pos == i) {
							if (argument.equals(finalArgList.get(i))) {
								matchesPosition = true;
							}
						}
					}
					if (!matchesPosition) {
						same = false;
					}
				}
				if (same) {
					if (sender instanceof Player) {
						if (a.hasPermissions((Player) sender)) {
							if (a.playerExecutable) {
								a.run(sender, args);
							} else {
								sender.sendMessage(ChatColor.RED + "Only console can do that");
							}
						} else {
							sender.sendMessage(noPermissionMessage);
							return false;
						}
					} else {
						if (a.consoleExecutable) {
							a.run(sender, args);
						} else {
							sender.sendMessage(ChatColor.RED + "Only players can do that");
						}
					}
					return false;
				}
			}
		}
		sendImproperUseMessage(sender);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if (args.length == 0 || !hasPermissions((Player) sender)) {
			return toReturn;
		}
		
		if (args.length == 1) {
			List<String> args1 = new ArrayList<String>();
			args1.add("help");
			for (Action action : actions) {
				if (action.hasPermissions((Player) sender)) {
					for (Argument arg : action.getArgs()) {
						if (arg.pos == 0) {
							if (arg.type == ArgType.PRESET) {
								if (!args1.contains(arg.arg))
									args1.add(arg.arg);
							} else if (arg.type == ArgType.PLAYER) {
								for (Player p : Bukkit.getOnlinePlayers()) {
									if (!args1.contains(p.getName()))
										args1.add(p.getName());
								}
							} else if (arg.type == ArgType.DOUBLE || arg.type == ArgType.INTEGER || arg.type == ArgType.STRING) {
								for (String s : arg.extraTabArgs) {
									if (!args1.contains(s))
										args1.add(s);
								}
							}
						}
					}
				}
			}
			for (String a : args1) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;
			
		}
		if (args.length > 1) {
			List<Argument> finalArgList = orderArgsTab(args);
			if (finalArgList == null || finalArgList.size() == 0) {
				return toReturn;
			}
			List<String> args2 = new ArrayList<String>();
			
			for (Action action : actions) {
				boolean match = true;
				if (finalArgList.size() < action.getArgs().size()) {
					for (int i = 0; i < finalArgList.size(); i++) {
						if (!(finalArgList.get(i).type == action.getArgs().get(i).type)) {
							match = false;
						} else if (finalArgList.get(i).type == ArgType.PRESET && action.getArgs().get(i).type == ArgType.PRESET) {
							if (!finalArgList.get(i).arg.equalsIgnoreCase(action.getArgs().get(i).arg)) {
								match = false;
							}
						}
					}
				} else match = false;
				if (match) {
					if (action.hasPermissions((Player) sender)) {
						for (Argument arg : action.getArgs()) {
							if (arg.pos == args.length -1) {
								if (arg.type == ArgType.PRESET) {
									if (!args2.contains(arg.arg))
										args2.add(arg.arg);
								} else if (arg.type == ArgType.PLAYER) {
									for (Player p : Bukkit.getOnlinePlayers()) {
										if (!args2.contains(p.getName()))
												args2.add(p.getName());
									}
								} else if (arg.type == ArgType.DOUBLE || arg.type == ArgType.INTEGER || arg.type == ArgType.STRING) {
									for (String s : arg.extraTabArgs) {
										if (!args2.contains(s))
											args2.add(s);
									}
								}
							}
						}
					}
				}
			}
			
			for (String a : args2) {
				if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;
			
		}
		return toReturn;
	}
	
	public List<Argument> orderArgsTab (String[] args) {
		if (args.length > 1) {
			for (int i = 0; i < args.length - 1; i++) {
				if (args[i].equals("")) 
					return null;
			}
		}
		int maxArgs = 0;
		for (Action a : actions) {
			if (a.getHighestArg() > maxArgs) {
				maxArgs = a.getHighestArg();
			}
		}
		int loopCount = 0;
		if (args.length <= maxArgs) {
			loopCount = args.length - 1;
		} else {
			loopCount = maxArgs - 1;
		}
		List<Argument> finalArgList = new ArrayList<>();
		for (int i = 0; i < loopCount; i++) {
			finalArgList.add(new Argument(ArgType.BLANK, i));
			List<Argument> posArgList = arguments.get(i);
			for (Argument arg : posArgList) {
				if (arg.type == ArgType.PRESET && args[i].equalsIgnoreCase(arg.arg)) {
					if (!finalArgList.get(i).equals(arg)) {
						finalArgList.set(i, arg);
					}
				} else if (arg.type == ArgType.INTEGER) {
					try {
						Integer.parseInt(args[i]);
						if (!finalArgList.get(i).equals(arg)) {
							finalArgList.set(i, arg);
						}
					} catch (NumberFormatException e) {}
				} else if (arg.type == ArgType.DOUBLE) {
					try {
						Double.parseDouble(args[i]);
						if (!finalArgList.get(i).equals(arg)) {
							finalArgList.set(i, arg);
						}
					} catch (NumberFormatException e) {}
				} else if (arg.type == ArgType.PLAYER) {
					if (Bukkit.getPlayer(args[i]) != null) {
						if (Bukkit.getPlayer(args[i]).getName().equals(args[i])) {
							if (!finalArgList.get(i).equals(arg)) {
								finalArgList.set(i, arg);
							}
						}
					}
				} else if (arg.type == ArgType.STRING) {
					if (finalArgList.get(i).type == ArgType.BLANK) {
						if (!finalArgList.get(i).equals(arg)) {
							finalArgList.set(i, arg);
						}
					}
				}
			}
			if (finalArgList.get(i).type == ArgType.BLANK) {
				//Bukkit.broadcastMessage("Removed arg");
				finalArgList.remove(i);
				return finalArgList;
			}
		}
		return finalArgList;
	}
	
	@SuppressWarnings("deprecation")
	public List<Argument> orderArgsCmd (String[] args) {
		int maxArgs = 0;
		for (Action a : actions) {
			if (a.getHighestArg() > maxArgs) {
				maxArgs = a.getHighestArg();
			}
		}
		int loopCount = 0;
		if (args.length <= maxArgs) {
			loopCount = args.length - 1;
		} else {
			loopCount = maxArgs;
		}
		List<Argument> finalArgList = new ArrayList<>(); // List to be filled with the final accepted args
		for (int i = 0; i < loopCount + 1; i++) { // Looping through each player given argument.
			finalArgList.add(new Argument(ArgType.BLANK, i)); // a blank arg to be filled in
			List<Argument> posArgList = arguments.get(i); // list to be filled with all possible args for this position
			boolean matchInPos = false;
			for (int j = 0; j < posArgList.size(); j++) {
				Argument arg = posArgList.get(j);
				boolean match = true;
				if (i != 0) match = matchesAnAction(finalArgList, arg);
				// Here we loop through all the arguments in this position to see if there's a match 
				// first, check if the string matches, don't want it mistaken as a player
				if (match) {
					matchInPos = true;
					if (arg.type == ArgType.PRESET && args[i].equalsIgnoreCase(arg.arg)) {
						if (!finalArgList.get(i).equals(arg)) {
							finalArgList.set(i, arg);
							j = posArgList.size();
						}
					} else if (arg.type == ArgType.INTEGER) {
						try {
							Integer.parseInt(args[i]);
							if (!finalArgList.get(i).equals(arg)) {
								finalArgList.set(i, arg);
							}
						} catch (NumberFormatException e) {}
					} else if (arg.type == ArgType.DOUBLE) {
						try {
							Double.parseDouble(args[i]);
							if (!finalArgList.get(i).equals(arg)) {
								finalArgList.set(i, arg);
							}
						} catch (NumberFormatException e) {}
					} else if (arg.type == ArgType.PLAYER) {
						OfflinePlayer p = Bukkit.getOfflinePlayer(args[i]);
						if (p != null) {
							if (p.getName().equals(args[i])) {
								if (!finalArgList.get(i).equals(arg)) {
									finalArgList.set(i, arg);
								}
							}
						}
					} else if (arg.type == ArgType.STRING) {
						if (finalArgList.get(i).type == ArgType.BLANK) {
							if (!finalArgList.get(i).equals(arg)) {
								finalArgList.set(i, arg);
							}
						}
					}
				}
			}
			if (!matchInPos) {
				return null; // No given arguments fit the user input, therefore no argument path can be constructed
			}
		}
		return finalArgList;
	}
	
	private boolean hasPermissions(Player p) {
		if (permissions.isEmpty()) {
			return true;
		}
		for (String s : permissions) {
			if (p.hasPermission(s)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasActionPermissions(Player p) {
		if (actionPermissions.isEmpty()) {
			return true;
		}
		for (String s : permissions) {
			if (p.hasPermission(s)) {
				return true;
			}
		}
		return false;
	}
	
	private void sendImproperUseMessage(CommandSender sender) {
		PermBasedUseMessage highestPriority = null;
		for (PermBasedUseMessage message : permBasedUseMessages) {
			if (message.hasPermissions(sender)) {
				if (highestPriority == null) highestPriority = message;
				if (message.priority > highestPriority.priority) {
					highestPriority = message;
				}
			}
		}
		if (highestPriority == null) {
			sender.sendMessage(improperUseMessage);
		} else {
			sender.sendMessage(highestPriority.message);
		}
	}
	
	public boolean matchesAnAction(List<Argument> oldArgs, Argument newArg) {
		ArrayList<Argument> args = new ArrayList<Argument>();
		args.addAll(oldArgs);
		args.set(args.size() - 1, newArg);

		for (Action a : actions) {
			if (args.size() <= a.getHighestArg() + 1) {
				boolean same = true;
				for (int i = 0; i < args.size(); i++) {
					boolean matchesPosition = false;
					for (Argument argument : a.getArgs()) {
						if (argument.pos == i) {
							if (argument.equals(args.get(i))) {
								matchesPosition = true;
							}
						}
					}
					if (!matchesPosition) {
						same = false;
					}
				}
				if (same) {
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void runHelp(CommandSender sender, String label, String[] args) {
		int page = -1;
		if (args.length == 1) {
			page = 1;
		} else {
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid page entered");
			}
		}
		if (page < 1) {
			sender.sendMessage(ChatColor.RED + "Invalid page entered");
		}
		ArrayList<Action> hasPermissionsList = new ArrayList<Action>();
		for (Action action : actions) {
			if (action.hasPermissions(sender)) {
				hasPermissionsList.add(action);
			}
		}
		
		final int argPathsPerPage = 10;
		int numActions = hasPermissionsList.size();
		int loopCount = 0;
		if (numActions >= argPathsPerPage * page) {
			loopCount = argPathsPerPage * page;
		} else {
			loopCount = numActions;
		}
		int maxPages = numActions / argPathsPerPage;
		if (numActions % argPathsPerPage > 0) {
			maxPages++; // ROUND UP ON ANY DECIMAL
		}

		TextComponent allText = new TextComponent(ChatColor.YELLOW + "---------Help (/" + label + ") - Page: " + page + "/" + maxPages + "---------");

		for (int i = (page - 1) * argPathsPerPage; i < loopCount; i++) {
			Action a = hasPermissionsList.get(i);
			String suggestText = "/" + label;
			String actionText = "\n" + ChatColor.GREEN + "  /" + label;
			for (int j = 0; j < a.getHighestArg() + 1; j++) {
				int count = 0;
				//actionText += "<";
				actionText += " ";
				suggestText += " ";
				for (Argument arg : a.getArgsPos(j)) {
					if (count >= 1) {
						actionText += "/";
						suggestText += "/";
					}
					if (arg.helpText != null) {
						actionText += "<" + arg.helpText + ">";
						suggestText += "<" + arg.helpText + ">";
						count++;
					} else if (arg.type == ArgType.PRESET) {
						actionText += arg.arg;
						suggestText += arg.arg;
						count++;
					} else if (arg.type == ArgType.PLAYER) {
						actionText += "<player>";
						suggestText += "<player>";
						count++;
					} else if (arg.type == ArgType.DOUBLE) {
						actionText += "<number>";
						suggestText += "<number>";
						count++;
					} else if (arg.type == ArgType.INTEGER) {
						actionText += "<whole-number>";
						suggestText += "<whole-number>";
						count++;
					} else if (arg.type == ArgType.STRING) {
						actionText += "<any>";
						suggestText += "<any>";
						count++;
					}
				}
				//actionText += "> ";
			}
			TextComponent component = new TextComponent(actionText);
			component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestText));
			component.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Click to suggest command").create()));
			allText.addExtra(component);
		}
		if (sender instanceof Player) {
			Player p = (Player) sender;
			p.spigot().sendMessage(allText);
		} else {
			sender.sendMessage(allText.getText());
		}

	}
}