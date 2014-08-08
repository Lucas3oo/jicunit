package org.jicunit.framework.internal;

import org.jicunit.framework.internal.model.ExceptionDescription;
import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.internal.model.TestDescription.Status;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Runs the actual test in the container and format the result. It uses
 * <code>org.junit.runner.JUnitCore</code> to actually execute the tests that
 * handles both JUnit3 and JUnit4 type of tests.
 * 
 * @author lucas
 *
 */
public class InContainerTestRunner extends RunListener {

  private JUnitCore mJUnitCore;
  private TestDescription mTestDescription;

  public InContainerTestRunner() {
    mJUnitCore = new JUnitCore();
    mJUnitCore.addListener(this);
  }

  /**
   * 
   * @param classLoader
   * @param testDescription description of the test that shall be run. 
   * @return testDescription with update result 
   * @throws ClassNotFoundException
   */
  public TestDescription runTest(ClassLoader classLoader, TestDescription testDescription) throws ClassNotFoundException {
    mTestDescription = testDescription;
    mTestDescription.clearResult();
    String testClassName = mTestDescription.getClassName();
    String testDisplayName = mTestDescription.getDisplayName();
    Filter filter = createMethodFilter(testClassName, testDisplayName);
    return runTest(classLoader, testClassName, filter);
  }

  private Filter createMethodFilter(final String testClassName, final String testDisplayName) {
    String testMethod = testDisplayName.substring(0, testDisplayName.indexOf('('));
    return Filter.matchMethodDescription(Description.createTestDescription(testClassName,
        testMethod));
  }

  private TestDescription runTest(ClassLoader classLoader, String testClassName, Filter filter) throws ClassNotFoundException {
    Class<?> clazz = classLoader.loadClass(testClassName);
    Request request = Request.aClass(clazz);
    request = request.filterWith(filter);

    mJUnitCore.run(request);

    return mTestDescription;
  }

  @Override
  public void testRunStarted(Description description) throws Exception {
  }

  @Override
  public void testRunFinished(Result result) throws Exception {
  }

  @Override
  public void testStarted(Description description) throws Exception {
    mTestDescription.setStartTime(System.currentTimeMillis());
  }

  // testFinished will be called last. I.e after testFailure
  @Override
  public void testFinished(Description description) throws Exception {
    long stopTime = System.currentTimeMillis();
    mTestDescription.setTime(stopTime - mTestDescription.getStartTime());
    if (mTestDescription.getStatus() == null) {
      mTestDescription.setStatus(Status.OK);
    }
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    Throwable exception = failure.getException();
    mTestDescription.setExceptionDescription(new ExceptionDescription(failure.getMessage(),
        exception.getClass().getName(), ExceptionUtil.filterdStackTrace(failure.getTrace())));
    
    if (exception instanceof AssertionError) {
      // -> this is a failure
      mTestDescription.setStatus(Status.Failure);
    } else {
      // -> this is an error
      mTestDescription.setStatus(Status.Error);
    }

  }

  @Override
  public void testIgnored(Description description) throws Exception {
    mTestDescription.setStatus(Status.Ignored);
  }

  @Override
  public void testAssumptionFailure(Failure failure) {
    try {
      testFailure(failure);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Returns the TestDescription as XML
   * @param testDescription
   * @return XML as String
   */
  public String resultAsXml(TestDescription testDescription) {
    XmlUtil xmlUtil = new XmlUtil();
    String xml = xmlUtil.convertToXml(testDescription, testDescription.getClass());
    return xml;

  }

}
