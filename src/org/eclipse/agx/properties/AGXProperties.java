package org.eclipse.agx.properties;

import java.util.ArrayList;
import org.eclipse.core.resources.IResource;
// import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.agx.main.AGX;
import org.eclipse.agx.main.Config;
import org.eclipse.agx.main.Util;

public class AGXProperties extends PropertyPage {
	
	// labels
	private static final String SOURCE_TITLE = "Source";
	private static final String TARGET_TITLE = "Target";
	private static final String GENERATOR_TITLE = "Generator";
	private static final String PROFILES_TITLE = "Profiles";

	// ui settings
	private static final int LABEL_FIELD_WIDTH = 12;
	private static final int TEXT_FIELD_WIDTH = 50;
	private static final int LIST_FIELD_WIDTH = 30;

	// property widgets
	private Label agxInfoLabel;
	private Text targetText;
	private Text generatorText;
	private List availableProfilesList;
	private List profilesList;
	
	// AGX
	private AGX agx;
	private Config config;
	private Util util;
	private IResource resource;

	public AGXProperties() {
		super();
		agx = new AGX();
		util = new Util();
	}
	
	private String getSource() {
		resource = (IResource) getElement();
		return resource.getLocation().toString();
	}
	
	private String [] getAvailableProfiles() {
		String generator = config.getGenerator();
		
		// case generator was initial set in properties page
		if (generator.equals("")) {
			generator = generatorText.getText();
		}
		
		String [] profiles = util.getProfileNames(
			agx.getConfiguredProfiles(generator));
		String [] usedProfiles = profilesList.getItems();
		
		ArrayList<String> availableProfiles = new ArrayList<String>(0);
		for (int i = 0; i < profiles.length; i++) {
			String profile = profiles[i];
			boolean skip = false;
			for (int j = 0; j < usedProfiles.length; j++) {
				if (profile.equals(usedProfiles[j])) {
					skip = true;
				}
			}
			if (!skip) {
				availableProfiles.add(profile);
			}
		}
		return availableProfiles.toArray(new String[] {});
	}
	
	private Composite createColComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}
	
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}
	
	private void addSourceSection(Composite parent) {
		Composite composite = createColComposite(parent);
		
		GridData labelGd = new GridData();
		labelGd.widthHint = convertWidthInCharsToPixels(LABEL_FIELD_WIDTH);
		
		Label sourceLabel = new Label(composite, SWT.NONE);
		sourceLabel.setLayoutData(labelGd);
		sourceLabel.setText(SOURCE_TITLE);
		
		Text sourceText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		sourceText.setText(getSource());
	}
	
	private void addTargetSection(Composite parent) {
		Composite composite = createColComposite(parent);
		
		GridData labelGd = new GridData();
		labelGd.widthHint = convertWidthInCharsToPixels(LABEL_FIELD_WIDTH);
		
		Label targetLabel = new Label(composite, SWT.NONE);
		targetLabel.setLayoutData(labelGd);
		targetLabel.setText(TARGET_TITLE);

		GridData textGd = new GridData();
		textGd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		
		targetText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		targetText.setLayoutData(textGd);
		targetText.setText(config.getTarget());
		
		Button button = new Button(composite, SWT.PUSH);
	    button.setText("browse");
	    
	    addTargetListener(button);
	}
	
	private void addTargetListener(Button button) {
		Listener listener = new Listener() {
	    	public void handleEvent(Event event) {
	    		Display display = Display.getDefault();
	    	    Shell shell = new Shell(display);
	    	    
	    	    DirectoryDialog dialog = new DirectoryDialog(shell);
	    	    dialog.setFilterPath("/");
	    	    String path = dialog.open();
	    	    
	    	    targetText.setText(path);
	    	    
	    	    while (!shell.isDisposed()) {
	    	        if (!display.readAndDispatch())
	    	            display.sleep();
	    	    }
	    	    display.dispose();
		    }
	    };
	    button.addListener(SWT.Selection, listener);
	}
	
	private void addGeneratorSection(Composite parent) {
		String generator = config.getGenerator();
		
		agxInfoLabel = new Label(parent, SWT.NONE);
		agxInfoLabel.setText(agx.getInfo(generator));
		
		Composite composite = createColComposite(parent);
		
		GridData labelGd = new GridData();
		labelGd.widthHint = convertWidthInCharsToPixels(LABEL_FIELD_WIDTH);
		
		Label generatorLabel = new Label(composite, SWT.NONE);
		generatorLabel.setLayoutData(labelGd);
		generatorLabel.setText(GENERATOR_TITLE);
		
		GridData textGd = new GridData();
		textGd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		
		generatorText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		generatorText.setLayoutData(textGd);
		generatorText.setText(generator);
		
		Button button = new Button(composite, SWT.PUSH);
	    button.setText("browse");
	    
	    addGeneratorListener(button);
	}
	
	private void addGeneratorListener(Button button) {
		Listener listener = new Listener() {
	    	public void handleEvent(Event event) {
	    		Display display = Display.getDefault();
	    	    Shell shell = new Shell(display);
	    	    
	    	    FileDialog dialog = new FileDialog(shell);
	    	    dialog.setFilterPath("/");
	    	    String path = dialog.open();
	    	    
	    	    generatorText.setText(path);
	    	    
	    	    // try to read available profiles as soon as generator is set.
	    	    try {
	    	        availableProfilesList.setItems(getAvailableProfiles());
	    	    } catch (Exception e) {
	    	    }
	    	    
	    	    while (!shell.isDisposed()) {
	    	        if (!display.readAndDispatch())
	    	            display.sleep();
	    	    }
	    	    display.dispose();
		    }
	    };
	    button.addListener(SWT.Selection, listener);
	}
	
    private void addProfilesSection(Composite parent) {
		Label profilesLabel = new Label(parent, SWT.NONE);
		profilesLabel.setText(PROFILES_TITLE);
		
    	Composite composite = createColComposite(parent);
		
		GridData listGd = new GridData();
		listGd.heightHint = convertHeightInCharsToPixels(6);
		listGd.widthHint = convertWidthInCharsToPixels(LIST_FIELD_WIDTH);
		
		availableProfilesList = new List(composite,
				                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		availableProfilesList.setLayoutData(listGd);
		
		Composite buttonComposite = new Composite(composite, SWT.NULL);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 1;
		buttonComposite.setLayout(buttonLayout);

		GridData buttonGd = new GridData();
		buttonComposite.setLayoutData(buttonGd);
		
		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText(">>");
	    
	    addProfilesListener(addButton);
	    
	    Button removeButton = new Button(buttonComposite, SWT.PUSH);
	    removeButton.setText("<<");
	    
	    removeProfilesListener(removeButton);
		
		profilesList = new List(composite,
				                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		profilesList.setLayoutData(listGd);
		profilesList.setItems(config.getProfiles());
		availableProfilesList.setItems(getAvailableProfiles());
		
		Button importButton = new Button(parent, SWT.PUSH);
		importButton.setText("import selected profiles");
		
		importProfilesListener(importButton);
	}
    
    private void addProfilesListener(Button button) {
    	Listener listener = new Listener() {
	    	public void handleEvent(Event event) {
	    		int [] selectedItems = 
	    			availableProfilesList.getSelectionIndices();
	    		if (selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		int selectedItem = selectedItems[0];
	    		String [] availableProfiles = availableProfilesList.getItems();
	    		String selectedValue = availableProfiles[selectedItem];
	    		
	    		String [] profiles = profilesList.getItems();
	    		String [] newProfiles = new String [profiles.length + 1];
	    		for (int i = 0; i < newProfiles.length; i++) {
	    			if (i == newProfiles.length - 1) {
	    				newProfiles[i] = selectedValue;
	    			} else {
	    				newProfiles[i] = profiles[i];
	    			}
	    		}
	    		profilesList.setItems(newProfiles);
	    		availableProfilesList.setItems(getAvailableProfiles());
		    }
	    };
	    button.addListener(SWT.Selection, listener);
    }
    
    private void removeProfilesListener(Button button) {
    	Listener listener = new Listener() {
	    	public void handleEvent(Event event) {
	    		int [] selectedItems = 
	    			profilesList.getSelectionIndices();
	    		if (selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		int selectedItem = selectedItems[0];
	    		String [] profiles = profilesList.getItems();
	    		String selectedValue = profiles[selectedItem];
	    		
	    		ArrayList<String> newProfiles = new ArrayList<String>(0);
	    		for (int i = 0; i < profiles.length; i++) {
	    			if (profiles[i] == selectedValue) {
	    				continue;
	    			}
	    			newProfiles.add(profiles[i]);
	    		}
	    		
	    		String [] newProfilesArr = newProfiles.toArray(new String[] {});
	    		
	    		profilesList.setItems(newProfilesArr);
	    		availableProfilesList.setItems(getAvailableProfiles());
		    }
	    };
	    button.addListener(SWT.Selection, listener);
    }
    
    public void importProfilesListener(Button button) {
    	Listener listener = new Listener() {
	    	public void handleEvent(Event event) {
	    		int [] selectedItems = 
	    			profilesList.getSelectionIndices();
	    		if (selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		agx.importProfiles(generatorText.getText(),
			 			           getSource(),
			 			           profilesList.getItems());
	    	}
	    };
	    button.addListener(SWT.Selection, listener);
    }
	
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		config = new Config(getSource());
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		addSourceSection(composite);
		addSeparator(composite);
		
		addTargetSection(composite);
		addSeparator(composite);
		
		addGeneratorSection(composite);
		addSeparator(composite);
		
		addProfilesSection(composite);
		addSeparator(composite);
		
		return composite;
	}

	protected void performDefaults() {
		targetText.setText(".");
		generatorText.setText("");
		profilesList.setItems(new String [] {});
	}
	
	public boolean performOk() {
		try {
			config.setTarget(targetText.getText());
			config.setGenerator(generatorText.getText());
			config.setProfiles(profilesList.getItems());
			config.update();
			
			agxInfoLabel.setText(agx.getInfo(generatorText.getText()));
			
			//URI modelUri = URI.createURI(resource.getLocation().toString());
			//org.eclipse.uml2.uml.Package model = util.loadModel(modelUri);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}