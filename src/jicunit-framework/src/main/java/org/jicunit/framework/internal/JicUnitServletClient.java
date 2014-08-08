package org.jicunit.framework.internal;

import java.io.IOException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jicunit.framework.internal.model.TestDescription.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.jicunit.framework.internal.JicUnitServlet.TEST_CLASS_NAME_PARAM;
import static org.jicunit.framework.internal.JicUnitServlet.TEST_NAME_PARAM;

/**
 * This class does the actual call to the servlet.
 * 
 * @author lucas
 *
 */
public class JicUnitServletClient {

  private static final String ENCODING = "UTF-8";

  public JicUnitServletClient() {
  }

  /**
   * Execute the test at the server
   * 
   * @param containerUrl
   * @param testClassName
   * @param testDisplayName
   * @return
   */
  public Document runAtServer(String containerUrl, String testClassName, String testDisplayName) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      String url = containerUrl
          + String.format("?%s=%s&%s=%s", TEST_CLASS_NAME_PARAM, testClassName,
              TEST_NAME_PARAM, URLEncoder.encode(testDisplayName, ENCODING));
      builder = factory.newDocumentBuilder();
      Document document = builder.parse(url);
      return document;
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Processes the actual XML result and generate exceptions in case there was
   * an issue on the server side.
   * 
   * @param document
   * @throws Throwable
   */
  public void processResults(Document document) throws Throwable {
    Element root = document.getDocumentElement();
    root.normalize();
    
    // top element should testcase which has attributes for the outcome of the test
    // e.g <testcase classname="TheClass" name="theName(TheClass)" status="Error"><exception message="message" type="type">some text</exception></testcase>
    NamedNodeMap attributes = root.getAttributes();
    String statusAsStr = attributes.getNamedItem("status").getNodeValue();
    Status status = Status.valueOf(statusAsStr);
    if (status.equals(Status.Error) || status.equals(Status.Failure)) {
      throw getException(root, status);
    }
  }

  private Throwable getException(Element root, Status errorOrFailure) {
    NodeList nodes = root.getElementsByTagName("exception");
    Node node = nodes.item(0);
    NamedNodeMap attributes = node.getAttributes();
    String message = "";
    Node messageNode = attributes.getNamedItem("message");
    if (messageNode != null) {
      message = messageNode.getNodeValue();
    }
    String type = "";
    Node typeNode = attributes.getNamedItem("type");
    if (typeNode != null) {
      type = typeNode.getNodeValue();
    }
    
    String stackTrace = node.getTextContent();
    
    if (errorOrFailure.equals(Status.Error)) {
      ExceptionWrapper e = new ExceptionWrapper(message, type, stackTrace);
      return e;
    } else {
      AssertionErrorWrapper e = new AssertionErrorWrapper(message, type, stackTrace);
      throw e;
    }

  }
}
