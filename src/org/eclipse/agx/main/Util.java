package org.eclipse.agx.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import org.eclipse.agx.Activator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.UMLPackage;

import com.sun.xml.internal.ws.util.StringUtils;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.emf.common.util.URI;

//import org.eclipse.papyrus.umlutils.PackageUtil;

public class Util {

	protected static final ResourceSet RESOURCE_SET = new ResourceSetImpl();
	static String MODEL_ROOT = "/model_templates/";

	/*
	 * return array from ';' seperated string
	 */
	public String[] str2Arr(String str) {
		String[] ret = str.split(";");
		if (ret.length == 1 && ret[0].equals("")) {
			return new String[] {};
		}
		return ret;
	}

	/*
	 * return ';' seperated string from array
	 */
	public String arr2Str(String[] arr) {
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
	public String[] getProfileNames(String[] profiles) {
		String[] ret = new String[profiles.length];
		for (int i = 0; i < profiles.length; i++) {
			ret[i] = profiles[i].substring(0, profiles[i].indexOf(" "));
		}
		return ret;
	}

	/*
	 * Return paths from "name /path" profile definitions returned by AGX for
	 * given profile names.
	 */
	public String[] getProfilePaths(String[] profiles,
			String[] availableProfiles) {
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
		return profilePaths.toArray(new String[] {});
	}

	public static org.eclipse.uml2.uml.Package loadPackage(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;
		try {
			Resource resource = RESOURCE_SET.getResource(uri, true);
			package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
					.getObjectByType(resource.getContents(),
							UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			// err(we.getMessage());
		}
		return package_;
	}

	public static org.eclipse.uml2.uml.Model loadModel(URI uri) {
		org.eclipse.uml2.uml.Package model = null;
		Resource resource = RESOURCE_SET.getResource(uri, true);
		model = (org.eclipse.uml2.uml.Model) EcoreUtil.getObjectByType(
				resource.getContents(), UMLPackage.Literals.MODEL);

		resource.unload();
		return (Model) model;
	}

	public static org.eclipse.uml2.uml.Model getModel(Resource resource) {
		org.eclipse.uml2.uml.Package model = null;
		model = (org.eclipse.uml2.uml.Model) EcoreUtil.getObjectByType(
				resource.getContents(), UMLPackage.Literals.MODEL);

		return (Model) model;
	}

	public void saveModel(org.eclipse.uml2.uml.Model model, URI uri) {
		Resource resource = new ResourceSetImpl().createResource(uri);
		resource.getContents().add(model);
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

	public static void copyResourceDir(File inres, IContainer container,
			String name) {

	}

	public static ArrayList<String> listTemplateNames() throws IOException {
		InputStream manistream = Util.class
				.getResourceAsStream("/model_templates/manifest.txt");
		return readStreamLines(manistream);
	}

	public static ArrayList<String> readStreamLines(InputStream stream)
			throws IOException {
		InputStreamReader ir = new InputStreamReader(stream);
		BufferedReader bi = new BufferedReader(ir);
		ArrayList<String> res = new ArrayList<String>();
		String line = null;
		do {
			if (!bi.ready())
				break;
			line = bi.readLine().trim();
			if (line.isEmpty())
				continue;

			res.add(line);
		} while (line != null);
		return res;
	}

	public static void copyModel(String path, IContainer container,
			String targetpath, IProgressMonitor monitor) throws IOException,
			CoreException {
		InputStream istream = Util.class.getResourceAsStream(MODEL_ROOT + path
				+ "/manifest.txt");
		ArrayList<String> filenames = readStreamLines(istream);
		Iterator<String> it = filenames.iterator();
		// create the new directory

		String modelfile = "model.uml".replace("model", targetpath);

		while (it.hasNext()) {
			String fname = it.next();
			IPath inpath = new Path(fname);
			String filepath = MODEL_ROOT + path + "/" + inpath;
			InputStream fstream = Util.class.getResourceAsStream(filepath);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IFile outfile = container.getFile(new Path(fname.replace("model",
					targetpath)));
			outfile.create(fstream, 1, monitor);
		}
		applyProfiles(container, modelfile);

		// update the model references inside .di and .notation
		// XXX:for the moment i do it via string replace
		// if there is a purist he might do this correctly with xml parsing ;)

		String notationfile = "model.notation".replace("model", targetpath);
		fileStringReplace(container, notationfile, "model.uml", modelfile);
		String difile = "model.di".replace("model", targetpath);
		fileStringReplace(container, difile, "model.notation", notationfile);

	}

	public static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	public static void fileStringReplace(IContainer container,
			String file, String from, String to) throws CoreException,
			IOException {
		IFile ifile = container.getFile(new Path(file));
		String path = ifile.getLocation().toOSString();

		String buf = readFile(path).replace(from, to);
		FileWriter fw = new FileWriter(path);
		fw.write(buf);
		fw.close();
	}

	public static void applyProfiles(IContainer container, String modelpath)
			throws IOException {
		String modelfullpath = container.getFile(new Path(modelpath))
				.getLocation().toOSString();
		IFile file = container.getFile(new Path(modelpath));
		String fullpath = file.getLocation().toOSString();
		Config conf = new Config(fullpath);
		String generator = conf.getGenerator();
		String[] profiles = conf.getProfiles();

		// conf.update();
		AGX agx = new AGX();
		agx.importProfiles(generator, modelfullpath, profiles);

		// and now apply the profiles
		String[] profilepaths = new String[profiles.length];
		for (int i = 0; i < profiles.length; i++) {
			profilepaths[i] = profiles[i] + ".profile.uml";
		}
		applyProfiles(container, modelpath, profilepaths);

	}

	public static void applyProfiles(IContainer container, String modelpath,
			String[] profilepaths) throws IOException {
		ResourceSetImpl RESOURCE_SET = new ResourceSetImpl();

		IFile file = container.getFile(new Path(modelpath));
		URI uri1 = URI.createURI(file.getFullPath().toOSString());

		Resource resource = RESOURCE_SET.getResource(uri1, true);
		Model model = getModel(resource);

		for (int i = 0; i < profilepaths.length; i++) {
			// retrieve uri for the profile model
			String profilepath = profilepaths[i];
			file = container.getFile(new Path(profilepath));
			URI uri = URI.createURI(file.getFullPath().toOSString());

			// extract the profile
			Resource profresource = RESOURCE_SET.getResource(uri, true);
			Profile profile = (Profile) EcoreUtil.getObjectByType(
					profresource.getContents(), UMLPackage.Literals.PACKAGE);

			// slam it into the model
			model.applyProfile(profile);
		}

		resource.setModified(true);
		resource.save(null); // save doesnt work, so we must save as over same
								// file

	}
}
