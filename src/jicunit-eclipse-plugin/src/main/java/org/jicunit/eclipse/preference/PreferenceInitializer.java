package org.jicunit.eclipse.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jicunit.eclipse.JicUnitPlugin;

/**
 * @author lucas
 *
 */
public class PreferenceInitializer  extends AbstractPreferenceInitializer {
  
  public static final String CONTAINER_URL = "jicunit.url";

  public PreferenceInitializer() {
  }

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = JicUnitPlugin.getDefault().getPreferenceStore();
    store.setDefault(CONTAINER_URL, "http://localhost:7001/integrationtest-war/tests");
  }

}
