package me.mc_cloud.playerfreezer.tools;

import java.util.ArrayList;

public class Argument {
	
	public String arg;
	public int pos;
	public ArgType type;
	public ArrayList<String> extraTabArgs = new ArrayList<String>();
	public String helpText = null;

	public Argument(ArgType type, int pos) {
		this.type = type;
		this.pos = pos;
	}
	
	public Argument(ArgType type, int pos, String arg) {
		this.type = type;
		this.pos = pos;
		this.arg = arg;
	}
	
	public Argument(ArgType type, String helpText, int pos, String arg) {
		this.type = type;
		this.pos = pos;
		this.arg = arg;
		this.helpText = helpText;
	}
	
	public Argument(ArgType type, String helpText, int pos) {
		this.type = type;
		this.pos = pos;
		this.helpText = helpText;
	}
	
	public void setArg (String arg) {
		this.arg = arg;
	}
	
	public boolean equals(Argument a) {
		if (this.type == ArgType.PRESET && a.type == ArgType.PRESET) {
			if (this.pos == a.pos) {
				if (this.arg.equalsIgnoreCase(a.arg)) {
					return true;
				}
				else {
					return false;
				}
			} else {
				
			}
		}
		if (this.type == a.type && this.pos == a.pos) {
			return true;
		}
		return false;
	}
}
