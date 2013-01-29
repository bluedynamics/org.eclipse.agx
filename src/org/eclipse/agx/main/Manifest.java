package org.eclipse.agx.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// reads a manifest file for model templates

class Manifest extends Object{
	private Properties properties;

	public Manifest(String path) throws IOException {
		properties = new Properties();
		File file = new File(path);
	    FileInputStream in = new FileInputStream(file);
	    load(in);
	    in.close();
	}

	public Manifest(InputStream is) throws IOException {
		properties = new Properties();
	    load(is);
	}

	private void load(InputStream in) throws IOException {
		// TODO Auto-generated method stub
	    properties.load(in);
	}

	public String[] getFilenames(){
		String files = properties.getProperty("files", "");
		if (files.trim() == "")
			return new String[0];
		
		String [] ret = Util.str2Arr(files);
		return ret;
	}
	
	public String getModelname(){
		return properties.getProperty("modelname", "");
	}
}