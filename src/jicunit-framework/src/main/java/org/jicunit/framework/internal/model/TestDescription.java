package org.jicunit.framework.internal.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jicunit.framework.internal.InContainerTestRunner;


/**
 * Describes a test and it status or a collection of tests.
 * Used in the GUI and in the {@link InContainerTestRunner}
 * 
 * 
 * @author lucas
 *
 */
@XmlRootElement(name = "testcase")
public class TestDescription {
  
  public enum Status { OK, Ignored, Error, Failure };
 

  /**
   * Display name of the test like "testSome(com.example.SomeTest)" or "com.example.SomeTest"
   */
  private String mDisplayName;
  private String mClassName;

  private Status mStatus;
  
  private long mStartTime;
  /**
   * Execution time in ms
   */
  private Long mTime;

  private ExceptionDescription mExceptionDescription;
  private List<TestDescription> mTestDescriptions = new ArrayList<TestDescription>();
  
  /**
   * Needed by JAXB, not supposed to be used.
   */
  public TestDescription() {
  }
  
  public TestDescription(String displayName, String className) {
    mDisplayName = displayName;
    mClassName = className;
  }

  public void clearResult() {
    mStatus = null;
    mStartTime = 0;
    mTime = null;
    mExceptionDescription = null;
    
    if (isSuite()) {
      for (TestDescription testDescription : mTestDescriptions) {
        testDescription.clearResult();
      }
    }
    
  }
  
  /**
   * 
   * @return the display name
   */
  @XmlAttribute(name="name")
  public String getDisplayName() {
    return mDisplayName;
  }


  public void setDisplayName(String displayName) {
    mDisplayName = displayName;
  }
  
  /**
   * @return name of the test class
   */
  @XmlAttribute(name="classname")
  public String getClassName() {
    return mClassName;
  }
  
  @XmlAttribute
  public Status getStatus() {
    if (isSuite()) {
      // aggregate status, Error and Failure have precedence
      Status status = null;
      for (TestDescription testDescription : mTestDescriptions) {
        Status childStatus = testDescription.getStatus();
        if (childStatus != null) {
          if (childStatus.equals(Status.Error) || childStatus.equals(Status.Failure)) {
            status = childStatus;
          }
          else {
            // only set to childStatus if aggregate status has not ever been set
            if (status == null) {
              status = childStatus;
            }
          }
        }
      }
      return status;
    }
    else {
      return mStatus;
    }
  }

  public void setStatus(Status status) {
    mStatus = status;
  }
  
  @XmlTransient
  public long getStartTime() {
    return mStartTime;
  }
  
  public void setStartTime(long startTime) {
    mStartTime = startTime;
  }
  
  /**
   * 
   * @return execution time in ms
   */
  @XmlAttribute
  public Long getTime() {
    return mTime;
  }

  public void setTime(Long time) {
    mTime = time;
  }
  
  /**
   * 
   * @return execution time in seconds
   */
  @XmlTransient
  public Double getTimeAsSeconds() {
    if (isSuite()) {
      // aggregate time
      Double time = null;
      for (TestDescription testDescription : mTestDescriptions) {
        Double childTime = testDescription.getTimeAsSeconds();
        if (childTime != null) {
          if (time == null) {
            time = new Double(0);
          }
          time = time + childTime;
        }
      }
      return time;
    }
    else {
      if (mTime != null) {
        return ((double)mTime) / 1000;
      }
      else {
        return null;
      }
    }
  }


  @XmlElement(name = "exception")
  public ExceptionDescription getExceptionDescription() {
    return mExceptionDescription;
  }
  
  public void setExceptionDescription(ExceptionDescription exceptionDescription) {
    mExceptionDescription = exceptionDescription;
  }

  @XmlTransient
  public List<TestDescription> getTestDescriptions() {
    return mTestDescriptions;
  }


  public void setTestDescriptions(List<TestDescription> testDescriptions) {
    mTestDescriptions = testDescriptions;
  }


  public void addTestDescription(TestDescription testDescriptionChild) {
    mTestDescriptions.add(testDescriptionChild);
  }
  
  @XmlTransient
  public boolean isSuite() {
    return mTestDescriptions.size() > 0;
  }
  
  

  @Override
  public String toString() {
    return mDisplayName;
  }

}
