package org.jicunit.framework.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * This runner will forward all execution of the tests to a container
 * 
 * @author lucas
 *
 */
public class ParameterizedProxyRunner extends Parameterized {

  private String mContainerUrl;
  private Class<?> mTestClass;

  public ParameterizedProxyRunner(Class<?> testClass, String containerUrl) throws Throwable {
    super(testClass);
    mTestClass = testClass;
    mContainerUrl = containerUrl;
  }

  // replace the runners with slightly modified BasicProxyRunner 
  @Override
  protected List<Runner> getChildren() {
    // one runner for each parameter
    List<Runner> runners = super.getChildren();
    List<Runner> proxyRunners = new ArrayList<>(runners.size());
    for (Runner runner : runners) {
      // if the next line fails then the internal of Parameterized.class has been updated since this works with JUnit 4.11
      BlockJUnit4ClassRunner blockJUnit4ClassRunner = (BlockJUnit4ClassRunner) runner;
      Description description = blockJUnit4ClassRunner.getDescription();
      String name = description.getDisplayName();
      try {
        proxyRunners
            .add(new ProxyTestClassRunnerForParameters(mTestClass, mContainerUrl, name));
      } catch (InitializationError e) {
        throw new RuntimeException("Could not create runner for paramamter " + name, e);
      }
    }
    return proxyRunners;
  }

  private static class ProxyTestClassRunnerForParameters extends BasicProxyRunner {
    private String mName;
    public ProxyTestClassRunnerForParameters(Class<?> testClass, String containerUrl,
        String name) throws InitializationError {
      super(testClass, containerUrl);
      mName = name;
    }

    @Override
    protected String getName() {
      return mName;
    }

    @Override
    protected String testName(FrameworkMethod method) {
      String testName = method.getName() + getName();
      return testName;
    }
  }

 
  /**
   * Suppress any BeforeClass annotation since it shall not be run locally.
   */
  @Override
  protected Statement withBeforeClasses(Statement statement) {
    return statement;
  }

  /**
   * Suppress any AfterClass annotation since it shall not be run locally.
   */
  @Override
  protected Statement withAfterClasses(Statement statement) {
    return statement;
  }
}
