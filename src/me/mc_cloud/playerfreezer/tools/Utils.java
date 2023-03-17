package me.mc_cloud.playerfreezer.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import me.mc_cloud.playerfreezer.Main;

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
	
	/**
	 * Saves an object to a file
	 * @param o	object to be saved
	 * @param f file to save the object to
	 */
	public static void save(Object o, File f) {
        try {
        	
        	if (!f.exists()) {
        		f.createNewFile();
        	}
            FileOutputStream   stream = new FileOutputStream(f.getAbsoluteFile());
            ObjectOutputStream output = new ObjectOutputStream(stream);
            output.writeObject(o);
            output.close();
            return;
        }
        catch(NullPointerException e) {
        	Main.instance.getLogger().warning("Unable to save files");
            return;
        }
        catch (IOException e) {
        	Main.instance.getLogger().warning("Unable to save files");
            return;
        }
	}
	
	/**
	 * Loads an object from a file
	 * @param f the file to read from
	 * @return the loaded object
	 */
	public static Object load(File f) {
        try {
        	if (!f.exists()) return null;
            FileInputStream   stream = new FileInputStream(f.getAbsolutePath());
            ObjectInputStream input  = new ObjectInputStream(stream);
            Object object = input.readObject();
            input.close();
            return object;            
        }
        catch (IOException e) {
        	Main.instance.getLogger().warning("Unable to load files");
            return null;
        } catch (ClassNotFoundException e) {
        	Main.instance.getLogger().warning("Unable to load files");
        	return null;
		}
	}
}
