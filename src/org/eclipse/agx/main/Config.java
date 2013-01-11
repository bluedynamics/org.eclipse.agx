package org.eclipse.agx.main;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.agx.Activator;
import org.eclipse.agx.preferences.PreferenceConstants;

public class Config extends Object {
	
	private String configpath;
	private Properties properties;
	private Util util;
	
	public Config(String path) {
		configpath = path + ".agx";
		properties = new Properties();
		util = new Util();
		try {
			File file = new File(configpath);
		    FileInputStream in = new FileInputStream(file);
		    properties.load(in);
		    in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public void update() throws IOException {
		File file = new File(configpath);
		FileOutputStream out = new FileOutputStream(file);
		properties.store(out, "---AGX Properties---");
		out.close();
	}
	
	public String getGenerator() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String defaultGenerator = 
			store.getString(PreferenceConstants.P_EXECUTABLE);
		String generator = properties.getProperty("generator", defaultGenerator);
		return generator;
	}
	
	public void setGenerator(String generator) {
		properties.setProperty("generator", generator);
	}
	
	public String getTarget() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String defaultTarget = 
			store.getString(PreferenceConstants.P_TARGET);
		String target = properties.getProperty("target", defaultTarget);
		return target;
	}
	
	public void setTarget(String target) {
		properties.setProperty("target", target);
	}
	
	public String [] getProfiles() {
		String profiles = properties.getProperty("profiles", "");
		String [] ret = Util.str2Arr(profiles);
		return ret;
	}
	
	public void setProfiles(String [] profiles) {
		String profileString = util.arr2Str(profiles);
		properties.setProperty("profiles", profileString);
	}
}