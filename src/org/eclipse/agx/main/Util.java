package org.eclipse.agx.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;
import org.eclipse.uml2.uml.UMLPackage;

import com.sun.xml.internal.ws.util.StringUtils;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;


//import org.eclipse.papyrus.umlutils.PackageUtil;

public class Util {
	
	protected static final ResourceSet RESOURCE_SET = new ResourceSetImpl();
	static String MODEL_ROOT="/model_templates/";
	
	/*
	 * return array from ';' seperated string
	 */
	public String [] str2Arr(String str) {
		String [] ret = str.split(";");
		if (ret.length == 1 && ret[0].equals("")) {
			return new String [] {};
		}
		return ret;
	}
	
	/*
	 * return ';' seperated string from array
	 */
	public String arr2Str(String [] arr) {
		String ret = new String();
		for (int i = 0; i < arr.length; i++) {
			ret += arr[i];
			if (i < arr.length - 1) {
				ret += ";";
			}
		}
		return ret;
	}
	
	/*
	 * Return names from "name /path" profile definitions returned by AGX.
	 */
	public String [] getProfileNames(String [] profiles) {
		String [] ret = new String [profiles.length];
		for (int i = 0; i < profiles.length; i++) {
			ret[i] = profiles[i].substring(0, profiles[i].indexOf(" "));
		}
		return ret;
	}
	
	/*
	 * Return paths from "name /path" profile definitions returned by AGX for
	 * given profile names.
	 */
	public String [] getProfilePaths(String [] profiles,
			                         String [] availableProfiles) {
		ArrayList<String> profilePaths = new ArrayList<String>(0);
		for (int i = 0; i < profiles.length; i++) {
			for (int j = 0; j < availableProfiles.length; j++) {
				if (availableProfiles[j].startsWith(profiles[i])) {
					String profilepath = availableProfiles[j].substring(
					    availableProfiles[j].indexOf(" ") + 1,
					    availableProfiles[j].length());
					profilePaths.add(profilepath);
				}
			}
		}
		return profilePaths.toArray(new String [] {});
	}
	
	public org.eclipse.uml2.uml.Package loadModel(URI uri) {
        org.eclipse.uml2.uml.Package package_ = null;
        try {
             Resource resource = RESOURCE_SET.getResource(uri, true);
        	 package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
        	             resource.getContents(), UMLPackage.Literals.PACKAGE);
        } catch (WrappedException we) {
             //err(we.getMessage());
        }
        return package_;
    }
	
	public void saveModel(org.eclipse.uml2.uml.Package package_, URI uri) {
        Resource resource = new ResourceSetImpl().createResource(uri);
        resource.getContents().add(package_);
        try {
            resource.save(null);
        } catch (IOException ioe) {
            // err(ioe.getMessage());
        }
    }
	
	public void applyProfile(org.eclipse.uml2.uml.Package package_,
			                           org.eclipse.uml2.uml.Profile profile) {
        package_.applyProfile(profile);
    }
	
	public static void copyResourceDir(File inres, IContainer container, String name){
		
		
	}
	
	public static ArrayList<String> listTemplateNames() throws IOException{
		InputStream manistream=Util.class.getResourceAsStream("/model_templates/manifest.txt");
		return readStreamLines(manistream);
	}
	
	public static ArrayList<String> readStreamLines(InputStream stream) throws IOException{
		InputStreamReader ir = new InputStreamReader(stream);
		BufferedReader bi = new BufferedReader(ir);
		ArrayList<String> res = new ArrayList<String>() ;
		String line = null;
		do{
			if (!bi.ready())
				break;
			line = bi.readLine().trim();
			if (line.isEmpty()) continue;
			
			res.add(line);
		} while (line != null);
		return res;
	}
	
	public static void copyModel(String path, IContainer container,String targetpath, IProgressMonitor monitor) throws IOException, CoreException{
		InputStream istream = Util.class.getResourceAsStream(MODEL_ROOT+path+"/manifest.txt");
		ArrayList<String> filenames = readStreamLines(istream);
		Iterator<String> it = filenames.iterator();
		//create the new directory
		
		IPath fpath = new Path(targetpath);
		
//		IFolder f=container.getFolder(fpath);
//		f.create(true,true,monitor);
//		IFolder modelfolder = container.getFolder(fpath);
		while(it.hasNext()){
			IPath fname = new Path(it.next());
			String filepath = MODEL_ROOT+path+"/"+fname;
			InputStream fstream = Util.class.getResourceAsStream(filepath);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IFile file = container.getFile(fname);
			file.create(fstream, 1, monitor);
		}
	}
}
