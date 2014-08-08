package org.jicunit.framework;

import static org.junit.Assert.assertEquals;

import org.jicunit.framework.internal.XmlUtil;
import org.jicunit.framework.internal.model.ExceptionDescription;
import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.internal.model.TestDescription.Status;
import org.junit.Test;

public class PojoToXmlTest {

  @Test
  public void test() {
    TestDescription testDescription = new TestDescription("theName(TheClass)", "TheClass");
    XmlUtil xmlUtil = new XmlUtil();
    String xml = xmlUtil.convertToXml(testDescription, testDescription.getClass());
    //System.out.println(xml);
  }

  @Test
  public void testFailure() {
    TestDescription testDescription = new TestDescription("theName(TheClass)", "TheClass");
    testDescription.setExceptionDescription(new ExceptionDescription("message", "type", "some text"));
    XmlUtil xmlUtil = new XmlUtil();
    String xml = xmlUtil.convertToXml(testDescription, testDescription.getClass());
    System.out.println(xml);
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testcase classname=\"TheClass\" name=\"theName(TheClass)\"><exception message=\"message\" type=\"type\">some text</exception></testcase>";
    assertEquals("XML must be the same", expected, xml);
  }

  @Test
  public void testIgnored() {
    TestDescription testDescription = new TestDescription("theName(TheClass)", "TheClass");
    testDescription.setStatus(Status.Ignored);
    XmlUtil xmlUtil = new XmlUtil();
    String xml = xmlUtil.convertToXml(testDescription, testDescription.getClass());
    //System.out.println(xml);
    String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testcase classname=\"TheClass\" name=\"theName(TheClass)\" status=\"Ignored\"/>";
    assertEquals("XML must be the same", expected, xml);
  }

}

