package org.jicunit.framework.internal.webrunner;

public interface RunnerCallback {

  ClassLoader getClassLoader();

  boolean isRunning();

  void setResult(int currentTestCount, int errorCount, int failureCount, int ignoredCount);

  void endRun();

}
