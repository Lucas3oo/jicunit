package org.jicunit.framework.internal.webrunner;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jicunit.framework.internal.InContainerTestRunner;
import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.internal.model.TestDescription.Status;

@Stateless
public class RunnerBean {

  public RunnerBean() {
  }

  @Asynchronous
  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  public void runAsync(RunnerCallback runnerCallback, List<TestDescription> selectedTests) {
    run(runnerCallback, selectedTests);
  }
  
  
  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  public void run(RunnerCallback runnerCallback, List<TestDescription> selectedTests) {
    ClassLoader classLoader = runnerCallback.getClassLoader();

    int currentTestCount = 0;
    int errorCount = 0;
    int failureCount = 0;
    int ignoredCount = 0;

    for (TestDescription desc : selectedTests) {
      if (runnerCallback.isRunning()) {
        InContainerTestRunner runner = new InContainerTestRunner();
        try {
          runner.runTest(classLoader, desc);
          currentTestCount++;
          Status status = desc.getStatus();
          switch (status) {
          case Error:
            errorCount++;
            break;
          case Failure:
            failureCount++;
            break;
          case Ignored:
            ignoredCount++;
          default:
            break;
          }
          // report progress
          runnerCallback.setResult(currentTestCount, errorCount, failureCount, ignoredCount);
        } catch (ClassNotFoundException e) {
          // should not happen since the class is already loaded once
        }
      } else {
        break;
      }
    }

    runnerCallback.endRun();

  }

}
