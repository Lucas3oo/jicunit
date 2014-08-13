package org.jicunit.framework;

import junit.framework.TestCase;

import org.jicunit.framework.internal.ContainerUrlHolder;
import org.jicunit.framework.internal.JicUnitServletClient;
import org.w3c.dom.Document;

/**
 * JUnit3 classes that inherits from this class will have there test executed in the
 * container instead when the system property "jicunit.url" is set.
 * 
 * @author lucas.persson
 * 
 */
public class JicUnitTestCase extends TestCase {

  public static final String CONTAINER_URL = "jicunit.url";

  public JicUnitTestCase() {
  }

  public JicUnitTestCase(String name) {
    super(name);
  }

  /**
   * JUnit method that is used to run the tests. However, it is intercepted
   * so that we can call the server side of JicUnit where the tests will be run
   * (instead of on the client side).
   * 
   * 
   * <p>
   * Both {@link #setUp()} and {@link #tearDown()} will only be run in container.
   * 
   * @throws java.lang.Throwable if any exception is thrown during the test.
   *          Any exception will be displayed by the JUnit Test Runner
   */
  @Override
  public void runBare() throws Throwable {
    // typically http://localhost:7001/integrationtest-war/TestServlet
    String containerUrl = getContainerUrl();
    if (containerUrl == null) {
      // this code is executed in the JEE container 
      super.runBare();
    } else {
      // make a remote call to the container/server to execute the test
      runBareLocally(containerUrl);
    }
  }

  /**
   * Executes JUnit tests on the server side. It is the equivalent of
   * junit.framework.TestCase.runBare() but run on the server side.
   * 
   * @throws java.lang.Throwable if an error occurred when running the test on
   *          the server side
   */
  private void runBareLocally(String containerUrl) throws Throwable {
    JicUnitServletClient client = new JicUnitServletClient();
    Document document = client.runAtServer(containerUrl, getClass().getName(), getName() + "("
        + getClass().getName() + ")");

    client.processResults(document);
  }

  protected String getContainerUrl() {
    String containerUrl = System.getProperty(CONTAINER_URL);
    if (containerUrl == null) {
      // fallback to look for a thread local variable. Only useful for testing of JicUNit itself
      containerUrl = ContainerUrlHolder.get();
    }
    return containerUrl;
  }


}
