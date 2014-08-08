package org.jicunit.framework.internal;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtil {

  public ExceptionUtil() {
  }

  
  public static StackTraceElement[] createFromStackTrace(String stackTrace) {
    List<StackTraceElement> elements = new ArrayList<StackTraceElement>();
    String[] segments = stackTrace.split("\n");
    for (int i = 1; i < segments.length; i++) {
      String segment = segments[i];
      if (segment.indexOf("\tat ") == 0) {
        segment = segment.substring(4);
        
        String[] tup = segment.split("[():]");
        String methodName = tup[0].substring(tup[0].lastIndexOf('.')+1);
        String declaringClass = tup[0].substring(0, tup[0].lastIndexOf('.'));
        String fileName = tup[1];
        int lineNumber = -1;
        if (tup.length > 2) {
          lineNumber = Integer.parseInt(tup[2]);
        }
        StackTraceElement stackTraceElement = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
        elements.add(stackTraceElement);
      }
    }
    return elements.toArray(new StackTraceElement[]{});
  }
  
  /**
   * Removes all entries that has to do with the internal way of 
   * running the test method.
   * 
   * @param stackTrace
   * @return filtered stackTrace
   */
  public static String filterdStackTrace(String stackTrace) {
    StringBuilder filteredStackTrace = new StringBuilder();
    // read line by line until the reflective call to the test method is found
    String[] segments = stackTrace.split("\n");
    for (int i = 0; i < segments.length; i++) {
      String segment = segments[i];
      if (segment.startsWith("\tat sun.reflect.NativeMethodAccessorImpl")) {
        break;
      }
      else if (segment.startsWith("\tat org.junit.") || segment.startsWith("\tat junit.framework.")) {
        // skipp
      }
      else {
        filteredStackTrace.append(segment).append("\n");
      }
    }
    // remove last newline
    filteredStackTrace.delete(filteredStackTrace.length()-1, filteredStackTrace.length());
    return filteredStackTrace.toString();
  }
  
}
