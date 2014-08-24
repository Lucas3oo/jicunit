package org.jicunit.framework.internal.webrunner;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.jicunit.framework.internal.model.TestDescription;

@javax.enterprise.context.SessionScoped
public class SessionBean implements Serializable {
  private static final long serialVersionUID = 1L;
  
  // root of the tree of test suites/classes test methods
  protected TestDescription mTestDescription;

  
  @javax.inject.Inject
  //@javax.faces.bean.ManagedProperty("#{applicationBean}")
  protected ApplicationBean mApplicationBean;


  public SessionBean() {
  }
  
  // only for unit test
  protected SessionBean(ApplicationBean applicationBean) {
    mApplicationBean = applicationBean;    
  }

  @PostConstruct
  public void init() {
    mTestDescription = mApplicationBean.createTestDescription();
  }
  
  public TestDescription getTestDescription() {
    return mTestDescription;
  }
  
}
