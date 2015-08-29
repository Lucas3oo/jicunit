package org.jicunit.framework.internal;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtil {

  public ExceptionUtil() {
  }

  
  public static StackTraceElement[] createFromStackTrace(String stackTrace) {
    List<StackTraceElement> elements;
    List<StackTraceElement> allElements = new ArrayList<StackTraceElement>();
    List<StackTraceElement> causedByElements = new ArrayList<StackTraceElement>();
    String[] segments = stackTrace.split(System.lineSeparator());
    elements = allElements;
    for (int i = 1; i < segments.length; i++) {
      String segment = segments[i];
      if (segment.startsWith("\tat ")) {
        StackTraceElement stackTraceElement = createStackTraceElement(segment);
        elements.add(stackTraceElement);
      }
      else if (segment.startsWith("Caused by:")) {
        elements = causedByElements;
      }
    }
    
    if (causedByElements.size() > 0) {
      // replace the first element by the causedByElements list
      allElements.remove(0);
      allElements.addAll(0, causedByElements);
    }
    return allElements.toArray(new StackTraceElement[]{});
  }


  private static StackTraceElement createStackTraceElement(String segment) {
    if (segment.startsWith("\tat ")) {
      segment = segment.substring(4);
      
      String[] tup = segment.split("[():]");
      String methodName = tup[0].substring(tup[0].lastIndexOf('.')+1);
      String declaringClass = tup[0].substring(0, tup[0].lastIndexOf('.'));
      String fileName = tup[1];
      int lineNumber = -1;
      if (tup.length > 2) {
        try {
          lineNumber = Integer.parseInt(tup[2]);
        } catch (NumberFormatException e) {
          // some funny stacktrace line -> use lineNumber = -1
        }
      }
      StackTraceElement stackTraceElement = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
      return stackTraceElement;
    }
    else {
      throw new IllegalArgumentException("The segment can not be parsed to a stackTrace element: " + segment);
    }
  }
  
  /**
   * Removes all entries that has to do with the internal way of 
   * running the test method.
   * 
   * @param stackTrace
   * @return filtered stackTrace
   */
  public static String filterdStackTrace(String stackTrace) {
    boolean skip = false;
    StringBuilder filteredStackTrace = new StringBuilder();
    // read line by line until the reflective call to the test method is found
    String[] segments = stackTrace.split("\n");
    for (int i = 0; i < segments.length; i++) {
      String segment = segments[i];
      
      if (skip) {
        // check if Cause by appears then stop skip
        if (segment.startsWith("Caused by:")) {
          skip = false;
          filteredStackTrace.append(segment).append("\n");
        }
      }
      else {
        if (segment.startsWith("\tat sun.reflect.NativeMethodAccessorImpl")) {
          // skip all lines until Caused by appears
          skip = true;
        }
        else if (segment.startsWith("\tat org.junit.") || segment.startsWith("\tat junit.framework.")) {
          // skip
        }
        else {
          filteredStackTrace.append(segment).append("\n");
        }
      }
    }
    // remove last newline
    filteredStackTrace.delete(filteredStackTrace.length()-1, filteredStackTrace.length());
    return filteredStackTrace.toString();
  }
  
}
