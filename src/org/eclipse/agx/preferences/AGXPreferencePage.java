package org.eclipse.agx.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.agx.Activator;


public class AGXPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private FileFieldEditor generatorExecuableEditor;
	private StringFieldEditor generationTargetEditor;

	public AGXPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("AGX Preferences");
	}
	
	public void createFieldEditors() {
		generatorExecuableEditor = new FileFieldEditor(
			PreferenceConstants.P_PATH, 
			"&AGX Executable:",
			getFieldEditorParent());
		
		generationTargetEditor = new StringFieldEditor(
			PreferenceConstants.P_STRING,
			"&Default Generation Target:",
			getFieldEditorParent());
		
		addField(generatorExecuableEditor);
		addField(generationTargetEditor);
	}
	
	protected void checkState() {
        super.checkState();
        if (generatorExecuableEditor.getStringValue() != null &&
          !generatorExecuableEditor.getStringValue().equals("")) {
            setErrorMessage(null);
            setValid(true);
        } else {
            setErrorMessage("Folder name cannot be blank!");
            setValid(false);
        }
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}