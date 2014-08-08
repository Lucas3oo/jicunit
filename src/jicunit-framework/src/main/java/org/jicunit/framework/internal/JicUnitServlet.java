package org.jicunit.framework.internal;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jicunit.framework.internal.model.TestDescription;

/**
 * This servlet runs the test given in the URL parameters of: "suite" and test".
 * The format of the result can be controlled by "output=xml" or "output=html"
 * <p>
 * "suite" should be a class name and "test" shall be the test name in the
 * format of "methodName(className)".
 * 
 * 
 * @author lucas
 *
 */
public class JicUnitServlet extends HttpServlet {

  public static final String TEST_NAME_PARAM = "test";
  public static final String TEST_CLASS_NAME_PARAM = "suite";
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    // test class name
    String testClassName = req.getParameter(TEST_CLASS_NAME_PARAM);
    // test name , eg myTest(com.example.MyTestCase)
    String testDisplayName = req.getParameter(TEST_NAME_PARAM);
    

    if (testDisplayName == null || testClassName == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(
          "Bad Request. Missing mandatory parameters '%s' and/or '%s'", TEST_NAME_PARAM,
          TEST_CLASS_NAME_PARAM));
      return;
    }

    InContainerTestRunner testRunner = createTestRunner();
    
    TestDescription testDescription = new TestDescription(testDisplayName, testClassName);

    try {
      testDescription = testRunner.runTest(getClassLoader(), testDescription);
    } catch (ClassNotFoundException e) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Class not found: " + e.getMessage());
      return;
    }

    String xmlResult = testRunner.resultAsXml(testDescription);

    resp.setContentType("text/xml;charset=UTF-8");
    PrintWriter writer = resp.getWriter();
    writer.print(xmlResult);
    writer.flush();
    writer.close();
  }

  protected InContainerTestRunner createTestRunner() {
    return new InContainerTestRunner();
  }

  /**
   * 
   * @return the class loader that will be used to load the tests classes with
   */
  protected ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

}
