package org.jicunit.framework.internal;

import java.util.List;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.w3c.dom.Document;

/**
 * This runner will forward all execution of the tests to a container
 * 
 * @author lucas
 *
 */
public class BasicProxyRunner extends BlockJUnit4ClassRunner {

  private String mContainerUrl;
  private JicUnitServletClient mClient;
  private Description mDescription;

  public BasicProxyRunner(Class<?> testClass, String containerUrl) throws InitializationError {
    super(testClass);
    mContainerUrl = containerUrl;

    mClient = new JicUnitServletClient();
  }

  public BasicProxyRunner(Class<?> testClass, String containerUrl, Description description) throws InitializationError {
    this(testClass, containerUrl);
    mDescription = description;
  }

  @Override
  protected void collectInitializationErrors(List<Throwable> errors) {
    // bypass all validation of the test class since that will anyway be done
    // when the real runner is executing in the container
  }

  
  @Override
  public Description getDescription() {
    if (mDescription != null) {
      return mDescription;
    }
    else {
      return super.getDescription();
    }
  }
  
  /**
   * This method will be called for the set of methods annotated with
   * Before/Test/After.
   */
  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier) {

    Description description = describeChild(method);
    if (method.getAnnotation(Ignore.class) != null) {
      notifier.fireTestIgnored(description);
      return;
    }

    notifier.fireTestStarted(description);
    String testClassName = getTestClass().getJavaClass().getName();
    String testName = description.getDisplayName();

    try {
      Document result = mClient.runAtServer(mContainerUrl, testClassName, testName);
      mClient.processResults(result);
    } catch (Throwable e) {
      notifier.fireTestFailure(new Failure(description, e));
    } finally {
      notifier.fireTestFinished(description);
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
