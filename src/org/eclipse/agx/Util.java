package org.eclipse.agx;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.UMLPackage;

//import org.eclipse.papyrus.umlutils.PackageUtil;

public class Util {
	
	protected static final ResourceSet RESOURCE_SET = new ResourceSetImpl();
	
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
}
