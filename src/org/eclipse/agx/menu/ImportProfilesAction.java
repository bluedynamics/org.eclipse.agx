package org.eclipse.agx.menu;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
//import org.eclipse.ui.console.MessageConsole;
import org.eclipse.agx.main.AGX;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.agx.main.Config;

public class ImportProfilesAction implements IObjectActionDelegate {

	private IFile currentFile;
	private AGX agx;
	private IContainer currentDir;
	
	/**
	 * Constructor for Action1.
	 */
	public ImportProfilesAction() {
		super();
		agx = new AGX();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		String modelpath = currentFile.getLocation().toString();
		Config config = new Config(modelpath);
		String generator = config.getGenerator();
		String model = modelpath;
		String [] profiles = config.getProfiles();
		
		try {
			org.eclipse.agx.main.Util.applyProfiles(
					currentDir, currentFile.getName());
		} catch (IOException e) {
			MessageBox box=new MessageBox(null);
			box.setText(e.getMessage());
			box.open();
		}
		agx.importProfiles(generator, model, profiles);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
  		if (selection instanceof IStructuredSelection) {
 		    IStructuredSelection structuredSelection = 
 		    	(IStructuredSelection) selection;
  		    currentFile = (IFile)structuredSelection.getFirstElement();
  		    currentDir=currentFile.getParent();
  		}
  	}
}