package org.jicunit.framework.internal.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class ExceptionDescription {
  private String mMessage;
  private String mType;
  private String mStackTrace;

  public ExceptionDescription() {
  }

  
  public ExceptionDescription(String message, String type, String stackTrace) {
    super();
    mMessage = message;
    mType = type;
    mStackTrace = stackTrace;
  }

  @XmlAttribute
  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String message) {
    mMessage = message;
  }

  /**
   * @return the name of the Exception/Error class
   */
  @XmlAttribute
  public String getType() {
    return mType;
  }

  public void setType(String type) {
    mType = type;
  }

  @XmlValue
  public String getStackTrace() {
    return mStackTrace;
  }

  public void setStackTrace(String stackTrace) {
    mStackTrace = stackTrace;
  }

  @Override
  public String toString() {
    return "ExceptionResult [mMessage=" + mMessage + ", mType=" + mType + ", mStackTrace=" + mStackTrace
        + "]";
  }
}
