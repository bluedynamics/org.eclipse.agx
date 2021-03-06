package org.eclipse.agx.menu;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.agx.main.AGX;
import org.eclipse.core.resources.IFile;
import org.eclipse.agx.main.Config;

public class GenerateAction implements IObjectActionDelegate {

	private IFile currentFile;
	private AGX agx;
	
	/**
	 * Constructor for Action1.
	 */
	public GenerateAction() {
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
		String target = config.getTarget();
		String [] profiles = config.getProfiles();
		agx.generate(generator, model, target, profiles);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
  		if (selection instanceof IStructuredSelection) {
 		    IStructuredSelection structuredSelection = 
 		    	(IStructuredSelection) selection;
  		    currentFile = (IFile)structuredSelection.getFirstElement();
  		}
  	}
}
