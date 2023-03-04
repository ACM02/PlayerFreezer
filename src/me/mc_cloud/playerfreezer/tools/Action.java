package me.mc_cloud.playerfreezer.tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class Action {
	
    private List<Argument> args = new ArrayList<>();
    public List<String> permissions = new ArrayList<String>();
    public boolean consoleExecutable = true;
    public boolean playerExecutable = true;
    private int highestArg = 0;
	
	public abstract void run(CommandSender sender, String[] args);
	
	public boolean hasPermissions(CommandSender p) {
		boolean hasPermission = true;
		if (permissions.isEmpty()) return true;
		for (String s : permissions) {
			if (!p.hasPermission(s)) {
				hasPermission = false;
			}
		}
		return hasPermission;
	}

	public boolean isGreaterThan(Action action) {
		int count = getHighestArg();
		for (int i = 0; i < count + 1; i++) {
			ArrayList<Argument> posArgListThis = getArgsPos(i);
			ArrayList<Argument> posArgListThat = action.getArgsPos(i);
			
			if (posArgListThis.size() < posArgListThat.size()) {
				return true; // Return true if this action has LESS args in the position reached
			}
			else if (posArgListThis.size() > posArgListThat.size()) {
				return false; // Return false if this action has MORE args in the position reached
			}

			int totalThis = 0;
			int totalThat = 0;
			for (int j = 0; j < posArgListThis.size(); j++) { // They will be equal, it won't be larger or smaller
				totalThis += posArgListThis.get(j).type.level;
				totalThat += posArgListThat.get(j).type.level;
			}
			if (totalThis > totalThat) { 
				return true; // Return true if the actions have the same number of args in pos i and the total of the 
				// level values for the arguments in this for position i is larger than that of the other action
			} else if (totalThis < totalThat) {
				return false; // Return false if the actions have the same number of args in pos i and the total of the 
				// level values for the arguments in this for position i is smaller than that of the other action
			}
			
			
			// Equal values - determine if there are preset values to value, only if there's one
			if (posArgListThis.size() == 1) {
				//System.out.println("Both pos args lists are 1 long");
				if (posArgListThis.get(0).type == ArgType.PRESET &&
					posArgListThat.get(0).type == ArgType.PRESET) {
					if (posArgListThis.get(0).arg.compareToIgnoreCase(posArgListThat.get(0).arg) > 0) {
						return true;
						// Return true if the actions have the same # args in pos i, only 1 arg in pos i, and the arg for this is higher 
						// alphhabetically
					} else if (posArgListThis.get(0).arg.compareToIgnoreCase(posArgListThat.get(0).arg) < 0) {
						return false;
						// Return false if the actions have the same # args in pos i, only 1 arg in pos i, and the arg for this is lower 
						// alphhabetically
					}
				}
			}
			// If you reach here, the args at pos i are equal
		}
		return false;
	}
	
	public ArrayList<Argument> getArgsPos(int i) {
		ArrayList<Argument> toReturn = new ArrayList<Argument>();
		for (Argument argument : args) {
			if (argument.pos == i) {
				toReturn.add(argument);
			}
		}
		return toReturn;
	}

	public int getHighestArg() {
		return highestArg;
	}
	
	public void addArg(Argument arg) {
		args.add(arg);
		if (arg.pos > highestArg) highestArg = arg.pos;
	}
	
	public List<Argument> getArgs() {
		return args;
	}
}
