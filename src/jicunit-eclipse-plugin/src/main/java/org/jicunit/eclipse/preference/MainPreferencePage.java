package org.jicunit.eclipse.preference;


import static org.jicunit.eclipse.preference.PreferenceInitializer.CONTAINER_URL;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jicunit.eclipse.JicUnitPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field
 * support built into JFace that allows us to create a page that is
 * small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in
 * the preference store that belongs to the main plug-in class. That
 * way, preferences can be accessed directly via the preference store.
 * 
 * To get a value do 
 * String myPrefString =
 * JicUnitPlugin.getDefault().getPreferenceStore().getString("MySTRING1");
 */
public class MainPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  public MainPreferencePage() {
    super(GRID);
  }

  public void init(IWorkbench workbench) {
    setPreferenceStore(JicUnitPlugin.getDefault().getPreferenceStore());
  }

  public void createFieldEditors() {
    addField(new StringFieldEditor(CONTAINER_URL, "Container URL:", getFieldEditorParent()));
  }
}