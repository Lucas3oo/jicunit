package org.jicunit.eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JicUnitPlugin extends AbstractUIPlugin {
  // The shared instance.
  private static JicUnitPlugin plugin;

  public static final String UNIQUE_IDENTIFIER = "org.jicunit.eclipse.jicunitplugin";

  /**
   * The constructor.
   */
  public JicUnitPlugin() {
    plugin = this;
  }

  /**
   * This method is called upon plug-in activation
   */
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }

  /**
   * This method is called when the plug-in is stopped
   */
  public void stop(BundleContext context) throws Exception {
    super.stop(context);
  }

  /**
   * Returns the shared instance.
   */
  public static JicUnitPlugin getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given
   * plug-in relative path
   * 
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(UNIQUE_IDENTIFIER, path);
  }

  public static Shell getShell() {
    return getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  /**
   * 
   * @param severity
   *          of IStatus
   * @param message
   * @param exception
   */
  public static void log(int severity, String message, Throwable exception) {
    IStatus status = new Status(severity, UNIQUE_IDENTIFIER, IStatus.OK, message, exception);
    getDefault().getLog().log(status);
  }

}