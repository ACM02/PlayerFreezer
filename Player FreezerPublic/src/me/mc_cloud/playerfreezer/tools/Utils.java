package me.mc_cloud.playerfreezer.tools;

import java.util.Date;

public class Utils {

	public static long todayPlus(int days, int hours, int mins, int secs) {
		return new Date().getTime() + (days * 1000 * 60 * 60 * 24) + (hours * 1000 * 60 * 60) + (mins * 1000 * 60) + (secs * 1000);
	}
	
	public static int milisUntil(long date) {
		return (int) ((date - new Date().getTime()));
	}
	
	public static int secsUntil(long date) {
		return (int) ((date - new Date().getTime()) / 1000);
	}
	
	public static int minsUntil(long date) {
		return (int) ((date - new Date().getTime()) / 1000 / 60);
	}
	
	public static int hoursUntil(long date) {
		return (int) ((date - new Date().getTime()) / 1000 / 60 / 60);
	}
	
	public static int daysUntil(long date) {
		return (int) ((date - new Date().getTime()) / 1000 / 60 / 60 / 24);
	}
}
