package org.jicunit.framework;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.jicunit.framework.internal.ContainerUrlHolder;
import org.jicunit.framework.internal.JicUnitServlet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;

public class IntegrationTestBase {

  private final static int PORT = 8085;
  private final static String PATH_SPEC = "/integrationtest-war/tests";
  public static Server sServer;
  public static JUnitCore sJUnitCore = new JUnitCore();
  

  @BeforeClass
  public static void setUpClass() throws Exception {
    // setup Jetty as a small Servlet container
    sServer = new Server(PORT);
    ServletHandler handler = new ServletHandler();
    sServer.setHandler(handler);
    handler.addServletWithMapping(JicUnitServlet.class, PATH_SPEC);

    // Start things up!
    sServer.start();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    sServer.stop();
  }
  
  @Before
  public void setUp() {
    // we must cheat a little hear since Jetty is also running in the same JVM
    // so the system property way can not be used. That will lead to the test
    // executed in Jetty also tries to call a remote server
    String containerUrl = "http://localhost:" + PORT + PATH_SPEC;
    ContainerUrlHolder.set(containerUrl);
  }
  
  @After
  public void tesrDown() {
    ContainerUrlHolder.set(null);
  }
  
  
  
  public Result runTest(Class<?> testClass, String methodName) throws Throwable {
    Filter filter = Filter.matchMethodDescription(Description.createTestDescription(testClass, methodName));
    Request request = Request.aClass(testClass);
    request = request.filterWith(filter);
    Result result = sJUnitCore.run(request);
    if (!result.wasSuccessful()) {
      List<Failure> failures = result.getFailures();
      assertEquals("Only one test should have been run so only one failure should exists.", 1, failures.size());
      Failure failure = failures.get(0);
      Throwable exception = failure.getException();
      throw exception;
    }
    return result;
  }

  public Result runTests(Class<?> testClass) {
    Request request = Request.aClass(testClass);
    Result result = sJUnitCore.run(request);
    return result;
  }

}
