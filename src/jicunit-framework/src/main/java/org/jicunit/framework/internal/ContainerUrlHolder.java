package org.jicunit.framework.internal;

public class ContainerUrlHolder {

  private static final ThreadLocal<String> sContainerUrl = new ThreadLocal<String>();

  public static String get() {
    return sContainerUrl.get();
  }

  public static void set(String containerUrl) {
    sContainerUrl.set(containerUrl);
  }
}
