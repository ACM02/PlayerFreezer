package me.mc_cloud.playerfreezer.tools;

public enum ArgType {
	PRESET(5), 
	PLAYER(4), 
	DOUBLE(3), 
	INTEGER(2), 
	STRING(1), 
	BLANK(0);
	
	public final int level;
	
	private ArgType(int level) {
		this.level = level;
	}
}
