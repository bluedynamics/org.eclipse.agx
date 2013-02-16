package org.eclipse.agx.main;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Color;

public class AGX extends Object {
	
	private Util util;
	private String [] configuredProfilesCached = null;
	private String[] configuredTemplatesCached;
	private String generator;
	
	public AGX() {
		super();
		util = new Util();
	}

	public AGX(String generator) {
		super();
		this.generator=generator;
		util = new Util();
	}

	/*
	 * Return generator info.
	 * 
	 * @param generator: path to AGX executable.
	 */
	public String getInfo(String generator) {
		String ret = "Error: ?";
		try {
    		String command = generator + " -i";
    		
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error =
                new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            String line;
            while ((line = input.readLine()) != null) {
            	if (!line.substring(0, 3).equals("AGX")) {
            		ret = "Error: Response not from AGX";
            	} else {
            		ret = line;
            	}
            	break;
            }
            MessageConsoleStream out = null;
            while ((line = error.readLine()) != null) {
            	if (out == null) {
            		MessageConsole console = getConsole();
            		out = console.newMessageStream();
            		Color red = new Color(null, 255, 0, 0);
                	out.setColor(red);
            	}
            	out.println(line);
            }
            
            input.close();
            error.close();
        } catch (Exception e) {
        	//err.printStackTrace();
        	ret = "Error: Invalid Interpreter";
        }
        return ret;
	}
	
	/*
	 * Return profile names and paths known by recent AGX.
	 * 
	 * @param generator: path to AGX executable.
	 */
    public String [] getConfiguredProfiles(String generator) {
    	if (configuredProfilesCached != null) {
    		return configuredProfilesCached;
    	}
    	try {
    		String command = generator + " -l";
    		
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error =
                new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            ArrayList<String> configuredProfiles = new ArrayList<String>(0);
            String line;
            while ((line = input.readLine()) != null) {
            	configuredProfiles.add(line);
            }
            MessageConsoleStream out = null;
            while ((line = error.readLine()) != null) {
            	if (out == null) {
            		MessageConsole console = getConsole();
            		out = console.newMessageStream();
            		Color red = new Color(null, 255, 0, 0);
                	out.setColor(red);
            	}
            	out.println(line);
            }
            
            error.close();
            input.close();
            
            configuredProfilesCached = 
            	configuredProfiles.toArray(new String[] {});
            return configuredProfilesCached;
        } catch (Exception e) {
        	//err.printStackTrace();
            return new String [] {};
        }
    }
    
	/*
	 * Return templates names and paths known by recent AGX.
	 * 
	 * @param generator: path to AGX executable.
	 */
	public String[] getModelTemplates() {
		if (configuredTemplatesCached != null) {
			return configuredTemplatesCached;
		}
		try {
			String command = generator + " -t -s";

			Process p = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			ArrayList<String> configuredTemplates = new ArrayList<String>(0);
			String line;
			while ((line = input.readLine()) != null) {
				configuredTemplates.add(line);
			}
			MessageConsoleStream out = null;
			while ((line = error.readLine()) != null) {
				if (out == null) {
					MessageConsole console = getConsole();
					out = console.newMessageStream();
					Color red = new Color(null, 255, 0, 0);
					out.setColor(red);
				}
				out.println(line);
			}

			error.close();
			input.close();

			configuredTemplatesCached = configuredTemplates
					.toArray(new String[] {});
			return configuredTemplatesCached;
		} catch (Exception e) {
			// err.printStackTrace();
			return new String[] {};
		}
	}

	// creates a model from a template given by name
	public void createModel(String template_name, IContainer container,
			String model_name, IProgressMonitor monitor) throws IOException,
			CoreException {
		
		String containerpath =
			new File(container.getLocationURI()).getAbsolutePath();

    	MessageConsole console = getConsole();
    	console.activate();
		
    	MessageConsoleStream out = console.newMessageStream();
		Color black = new Color(null, 0, 0, 0);
		out.setColor(black);
		
		MessageConsoleStream info = console.newMessageStream();
		Color green = new Color(null, 0, 200, 0);
		info.setColor(green);
		
		MessageConsoleStream err = console.newMessageStream();
		Color red = new Color(null, 255, 0, 0);
    	err.setColor(red);
    	
    	out.println("AGX: creating model from template "+template_name);
    	
    	String command = generator + " -c " +
    					template_name + " -o " +
    					containerpath + ' '+ model_name;
    	
    	out.println("AGX: Import profiles");
		out.println("Command: " + command);
    	
    	try {
    		Process p = Runtime.getRuntime().exec(command);
            BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error =
                new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            String line;
            while ((line = input.readLine()) != null) {
            	info.println(line);
            }
            while ((line = error.readLine()) != null) {
            	err.println(line);
            }
            
            input.close();
            error.close();
            out.println("");
    	} catch (Exception e) {
        	err.println("Error: " + e.toString());
        	out.println("");
        }
	}
    /*
	 * Call generator.
	 * 
	 * @param generator: path to AGX executable.
	 * @param model: path to model XMI
	 * @param target path where the generated code goes
	 * @param profiles: names of profiles to use for generation.
	 */
    public void generate(String generator,
    		             String model,
    		             String target,
    		             String [] profiles) {
    	MessageConsole console = getConsole();
    	console.activate();
    	
    	// get relative path for the target in order to refresh it
    	// we have to calculate target+(model\modelname)
    	IPath modelcontainer = new Path(model).removeLastSegments(1);
    	IPath targetpath = modelcontainer.append(new Path(target));
    	
    	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    	IPath rootpath = root.getLocation();

    	IPath relpath = targetpath.makeRelativeTo(rootpath);
    	IResource targetres = root.findMember(relpath);
    	
    	MessageConsoleStream out = console.newMessageStream();
		Color black = new Color(null, 0, 0, 0);
		out.setColor(black);
		
		MessageConsoleStream info = console.newMessageStream();
		Color green = new Color(null, 0, 200, 0);
		info.setColor(green);
		
		MessageConsoleStream err = console.newMessageStream();
		Color red = new Color(null, 255, 0, 0);
    	err.setColor(red);
    	
    	out.println("Start AGX");
    	
    	if (generator.equals("")) {
    		err.println("Error: No generator configured");
    		out.println("");
    		return;
    	}
    	if (target.equals("")) {
    		err.println("Error: No target configured");
    		out.println("");
    		return;
    	}
    	if (profiles.length == 0) {
    		err.println("Error: No profiles applied");
    		out.println("");
    		return;
    	}
    	
    	try {
    		out.println("AGX: Read configured profiles");
    		String [] availableProfiles = getConfiguredProfiles(generator);
    		String [] availableProfileNames = 
    			util.getProfileNames(availableProfiles);
    		
    		for (int i = 0; i < profiles.length; i++) {
    			boolean found = false;
    			for (int j = 0; j < availableProfileNames.length; j++) {
    				if (profiles[i].equals(availableProfileNames[j])) {
    					found = true;
    				}
    			}
    			if (!found) {
    				String msg = 
    					"Error: One or more required profiles not available";
    				err.println(msg);
    				out.println("");
    				return;
    			}
    		}
    		
    		String [] profilePathsArray = util.getProfilePaths(
    			profiles, availableProfiles);
    		
    		String profilesConcat = "";
    		for (int i = 0; i < profilePathsArray.length; i++) {
    			profilesConcat += profilePathsArray[i];
    			if (i != profilePathsArray.length - 1) {
        			profilesConcat += ",";
        		}
    		}
    		
    		String command = generator + 
    		                 " " + model +
    		                 " -p " + profilesConcat +
    		                 " -o " + target;
    		
    		out.println("AGX: Invoke generator");
    		out.println("Command: " + command);
    		
    		Process p = Runtime.getRuntime().exec(command);
    		p.waitFor();
    		
    		targetres.refreshLocal(IResource.DEPTH_INFINITE, null);
    		
            BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error =
                new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            String line;
            while ((line = input.readLine()) != null) {
            	info.println(line);
            }
            while ((line = error.readLine()) != null) {
            	err.println(line);
            }
            
            input.close();
            error.close();
            out.println("");
        } catch (Exception e) {
        	err.println("Error: " + e.toString());
        	out.println("");
        }
    }
    
    /*
	 * Import profiles for model.
	 * 
	 * @param generator: path to AGX executable.
	 * @param model: path to model XMI
	 * @param profiles: names of profiles to import.
	 */
    public void importProfiles(String generator,
                               String model,
                               String [] profiles) {
    	String profilesConcat = "";
    	for (int i = 0; i < profiles.length; i++) {
    		profilesConcat += profiles[i];
    		if (i != profiles.length - 1) {
    			profilesConcat += ";";
    		}
    	}
    	
    	MessageConsole console = getConsole();
    	console.activate();
		
    	MessageConsoleStream out = console.newMessageStream();
		Color black = new Color(null, 0, 0, 0);
		out.setColor(black);
		
		MessageConsoleStream info = console.newMessageStream();
		Color green = new Color(null, 0, 200, 0);
		info.setColor(green);
		
		MessageConsoleStream err = console.newMessageStream();
		Color red = new Color(null, 255, 0, 0);
    	err.setColor(red);
    	
    	out.println("Start AGX");
    	
    	String command = generator + 
        " " + model +
        " -e " + profilesConcat;
    	
    	out.println("AGX: Import profiles");
		out.println("Command: " + command);
    	
    	try {
    		Process p = Runtime.getRuntime().exec(command);
            BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error =
                new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            String line;
            while ((line = input.readLine()) != null) {
            	info.println(line);
            }
            while ((line = error.readLine()) != null) {
            	err.println(line);
            }
            
            input.close();
            error.close();
            out.println("");
    	} catch (Exception e) {
        	err.println("Error: " + e.toString());
        	out.println("");
        }
    }
    
    /*
     * Get console to write to.
     */
    public MessageConsole getConsole() {
    	String name = "AGX";
    	ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++)
           if (name.equals(existing[i].getName()))
              return (MessageConsole) existing[i];
        MessageConsole myConsole = new MessageConsole(name, null);
        conMan.addConsoles(new IConsole[]{myConsole});
        return myConsole;
     }
}