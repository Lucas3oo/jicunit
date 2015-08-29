package org.jicunit.framework;

import static org.junit.Assert.*;

import org.jicunit.framework.internal.ExceptionUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author lucas
 *
 */
public class ExceptionUtilTest {

  private String mStackTrace = "java.lang.RuntimeException: The value could not be parsed\n"
      + "\tat com.example.jicunit.sample.SomeBusinessBean.parseValRuntimeExeption(SomeBusinessBean.java:20)\n"
      + "\tat com.example.jicunit.sample.SomeBusinessBeanTest.testParseWithRuntimeExceptionError(SomeBusinessBeanTest.java:45)\n"
      + "Caused by: java.lang.NumberFormatException: For input string: \"ade\"\n"
      + "\tat java.lang.NumberFormatException.forInputString(NumberFormatException.java)\n"
      + "\tat java.lang.Integer.parseInt(Unknown Source)\n"
      + "\tat java.lang.Integer.parseInt(Native Method)\n"
      + "\tat java.lang.Integer.parseInt(Integer.java:??)\n"
      + "\tat java.lang.Integer.parseInt(Integer.java:615)\n"
      + "\tat com.example.jicunit.sample.SomeBusinessBean.parseValRuntimeExeption(SomeBusinessBean.java:17)\n"
      + "\t... 49 more\n";

  private String[] mExpectedElements = {
      "java.lang.NumberFormatException.forInputString(NumberFormatException.java)",
      "java.lang.Integer.parseInt(Unknown Source)",
      "java.lang.Integer.parseInt(Native Method)",
      "java.lang.Integer.parseInt(Integer.java)",
      "java.lang.Integer.parseInt(Integer.java:615)",
      "com.example.jicunit.sample.SomeBusinessBean.parseValRuntimeExeption(SomeBusinessBean.java:17)",
      "com.example.jicunit.sample.SomeBusinessBeanTest.testParseWithRuntimeExceptionError(SomeBusinessBeanTest.java:45)" };

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testCreateFromStackTrace() {
    StackTraceElement[] elements = ExceptionUtil.createFromStackTrace(mStackTrace);
    assertEquals("The length of elements must be equal.", mExpectedElements.length, elements.length);
    for (int i = 0; i < elements.length; i++) {
      assertEquals("Cheking each element.", mExpectedElements[i], elements[i].toString());
    }
  }

}
