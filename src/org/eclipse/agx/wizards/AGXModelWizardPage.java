package org.eclipse.agx.wizards;

import java.util.HashMap;
import org.eclipse.agx.main.AGX;
import org.eclipse.agx.main.Config;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (uml).
 */
public class AGXModelWizardPage extends WizardPage {

	private Text fileText;

	private ISelection selection;

	private Combo modelType;

	String templateName;

	private String containerpath;

	private HashMap<String, String> templmap;

	private Config config;

	public AGX agx;

	private Label descriptionLabel;

	private HashMap<String, String> descriptionMap;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public AGXModelWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("AGX Model");
		setDescription("This wizard creates a new file with *.uml extension "
				+ "that can be opened by a multi-page editor.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		//map template labels to template name
		templmap = new HashMap<String, String>();
		//map template labels to descriptions
		descriptionMap=new HashMap<String, String>();
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		// model name
		Label label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("");

		// template name
		label = new Label(container, SWT.NULL);
		label.setText("&Template:");

		modelType = new Combo(container, SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		modelType.setLayoutData(gd);
		modelType.select(0);
		modelType.addSelectionListener(new SelectionListener() {
			private void update(){
				templateName = templmap.get(modelType.getText());
				String description = descriptionMap.get(modelType.getText());
				descriptionLabel.setText(description);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				update();
			}
		});

		//fill the list
		initialize();
		String[] templates = null;
		templates=agx.getModelTemplates();

		for (String entry : templates) {
			String[] arr = entry.split("\t");
			String templ = arr[0];
			String templabel = templ;
			String description="";
			if (arr.length > 1)
				templabel = arr[1];
			if (arr.length>2)
				description=arr[2];
			modelType.add(templabel);
			templmap.put(templabel, templ);
			description=description.replace("<br/>", "\n").replace("\\", "");
			descriptionMap.put(templabel, description);
		}

		label = new Label(container, SWT.NULL);
		label.setText("");

		//description text
		gd = new GridData(GridData.FILL_HORIZONTAL);
		label = new Label(container, SWT.NULL);
		label.setText("");

		descriptionLabel = new Label(container, SWT.NULL);
		descriptionLabel.setText("");
		descriptionLabel.setLayoutData(gd);

		//reserve lines for the label (XXX: evil hack)
		descriptionLabel.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		label = new Label(container, SWT.NULL);
		label.setText("");

		dialogChanged();
		setControl(container);
	}

	IResource extractSelection(ISelection sel) {
		if (!(sel instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) sel;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return (IResource) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		// PythonProjectSourceFolder ff;
		// IWrappedResource wr;
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			IResource resource = extractSelection(selection);
			containerpath=resource.getFullPath().toString();
			resource = extractSelection(selection);
			config = new Config(resource.getLocation().toString());
			containerpath=resource.getFullPath().toString();
			this.agx=new AGX(config.getGenerator());

		}
		fileText.setText("model");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
		IWorkspace wsp = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wsp.getRoot();
		String pathstring = getContainerName();
		String fileName = "" ;
		if (pathstring != null) {
			IResource container = root.findMember(new Path(pathstring));

			fileName = getFileName();

			if (pathstring.length() == 0) {
				updateStatus("File container must be specified");
				return;
			}
			if (container == null
					|| (container.getType() 
							& (IResource.PROJECT | IResource.FOLDER)) == 0) {
				updateStatus("File container must exist");
				return;
			}
			if (!container.isAccessible()) {
				updateStatus("Project must be writable");
				return;
			}
		}
		
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		//@SuppressWarnings("unused")
		String mtype = modelType.getText();
		if (mtype.isEmpty()) {
			updateStatus("Please select a model template!");
			return;
		}
		//int dotLoc = fileName.lastIndexOf('.');
		//if (dotLoc != -1) {
			//String ext = fileName.substring(dotLoc + 1);
			// if (ext.equalsIgnoreCase("uml") == false) {
			// updateStatus("File extension must be \"uml\"");
			// return;
			// }
		//}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerpath;
	}

	public String getFileName() {
		return fileText.getText();
	}
}