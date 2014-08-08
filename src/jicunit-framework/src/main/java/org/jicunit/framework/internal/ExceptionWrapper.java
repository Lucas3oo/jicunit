package org.jicunit.framework.internal;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ExceptionWrapper extends Throwable {
  
  private static final long serialVersionUID = 1L;

  // The stack trace that was sent back from the servlet redirector as
  // a string.
  private String mStackTrace;

  private String mCause;

  private StackTraceElement[] mStackTraceElements;


  public ExceptionWrapper() {
  }

  public ExceptionWrapper(String message) {
    super(message);
  }


  /**
   * The constructor to use to simulate a real exception. 
   */
  public ExceptionWrapper(String message, String cause, String stackTrace) {
    super(message);
    mCause = cause;
    mStackTrace = stackTrace;
    mStackTraceElements = ExceptionUtil.createFromStackTrace(stackTrace);
  }

  /**
   * Simulates a printing of a stack trace by printing the string
   * stack trace 
   */
  public void printStackTrace(PrintStream printStream) {
    if (mStackTrace == null) {
      printStream.print(getMessage());
    }
    else {
      printStream.print(mStackTrace);
    }
  }
  
  /**
   * Simulates a printing of a stack trace by printing the string
   * stack trace 
   */
  public void printStackTrace(PrintWriter printWriter) {
    if (mStackTrace == null) {
      printWriter.print(getMessage());
    }
    else {
      printWriter.print(mStackTrace);
    }
  }

  @Override
  public String toString() {
    String s = mCause;
    String message = getLocalizedMessage();
    return (message != null) ? (s + ": " + message) : s;
  }
  
  @Override
  public StackTraceElement[] getStackTrace() {
    return mStackTraceElements;
  }
  
}
