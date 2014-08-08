package org.jicunit.eclipse.launch;

import static org.jicunit.eclipse.preference.PreferenceInitializer.CONTAINER_URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.jicunit.eclipse.JicUnitPlugin;



public class JUnitInContainerLaunchShortcut extends JUnitLaunchShortcut  {

/* (non-Javadoc)
   * @see org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut#createLaunchConfiguration(org.eclipse.jdt.core.IJavaElement)
   */
  @Override
  protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IJavaElement element)
      throws CoreException {
    ILaunchConfigurationWorkingCopy wc =  super.createLaunchConfiguration(element);

    String containerUrl = JicUnitPlugin.getDefault().getPreferenceStore()
        .getString(CONTAINER_URL);    
    String extraVwArg = "-D" + CONTAINER_URL + "=" + containerUrl;
    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, extraVwArg);
    
    return wc;
  }  
  

}
