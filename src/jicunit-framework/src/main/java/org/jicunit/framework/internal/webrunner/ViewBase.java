package org.jicunit.framework.internal.webrunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jicunit.framework.internal.FactGenerator;
import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.internal.model.TestDescription.Status;

/**
 * JSF SessionBean base class to implement a JUnit runner GUI
 * 
 * The bean must be passivation capable hence it implements Serializable. This
 * is a WELD requirement.
 * 
 * @author lucas
 *
 */
public abstract class ViewBase implements RunnerCallback, Serializable {

  private static final long serialVersionUID = 1L;

  @javax.inject.Inject
//  @javax.faces.bean.ManagedProperty("#{applicationBean}")
  protected SessionBean mSessionBean;

  @EJB
  protected RunnerBean mRunnerBean;


  protected volatile boolean mRunning = false;

  // root of the tree of test suites/classes test methods
  protected TestDescription mTestDescription;

  // count for last run
  protected volatile int mTotalTestCount;
  protected volatile int mCurrentTestCount;
  protected volatile int mErrorCount;
  protected volatile int mFailureCount;
  protected volatile int mIgnoredCount;

  private String mTheme = "default";

  public ViewBase() {
  }

  // only for unit test
  protected ViewBase(SessionBean sessionBean, RunnerBean runnerBean) {
    mSessionBean = sessionBean;
    mRunnerBean = runnerBean;
  }

  public void init() {
    mTestDescription = mSessionBean.getTestDescription();
    FacesContext facesContext = FacesContext.getCurrentInstance();
    if (facesContext != null) {
      String theme = facesContext.getExternalContext().getInitParameter("org.jicunit.framework.theme");
      if (theme != null) {
        mTheme = theme;
      }
    }
  }


  public void clearResults() {
    if (!isRunning()) {
      mTestDescription.clearResult();
      mTotalTestCount = 0;
      mCurrentTestCount = 0;
      mErrorCount = 0;
      mFailureCount = 0;
      mIgnoredCount = 0;
    }
  }

  @Override
  public boolean isRunning() {
    return mRunning;
  }

  public void beginRunAll() {
    if (!isRunning()) {
      clearResults();
      runAll();
    }
  }

  public void beginRunSelected() {
    if (!isRunning()) {
      clearResults();
      runSelected();
    }
  }

  public void beginRunSingle() {
    if (!isRunning()) {
      clearResults();
      runSingle();
    }
  }

  @Override
  public void endRun() {
    mRunning = false;
    onComplete();
  }

  public void cancelRun() {
    endRun();
  }
  
  /**
   * Start running the tests
   * 
   * @param selectedTests
   */
  protected void run(List<TestDescription> selectedTests) {
    mRunning = true;
    mTotalTestCount = selectedTests.size();
    mRunnerBean.run(this, selectedTests);
  }
  
  
  protected void runAll() {
    List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<>();
    includeAllLeafs(mTestDescription, selectedTestsIncludedLeafs);
    run(selectedTestsIncludedLeafs);
  }

  /**
   * Execute the selected tests
   */
  protected void runSelected() {
    List<TestDescription> selectedTests = getSelectedTests();
    if (selectedTests != null) {
      List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<>();
      for (TestDescription desc : selectedTests) {
        includeAllLeafs(desc, selectedTestsIncludedLeafs);
      }
      // run all tests in selectedTestsIncludedLeafs
      run(selectedTestsIncludedLeafs);
    }
  }

  /**
   * Run the test on the current row. The test or test suite on the same row as
   * the button was clicked on.
   */
  protected void runSingle() {
    // get the current clicked row
    TestDescription testDescription = getCurrentRow();
    runSingle(testDescription);
  }

  protected void runSingle(TestDescription testDescription) {
    if (testDescription != null) {
      List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<>();
      includeAllLeafs(testDescription, selectedTestsIncludedLeafs);
      run(selectedTestsIncludedLeafs);
    }
  }

  
  public Integer getProgress() {
    Integer progress = 0;  
    if (mTotalTestCount > 0) {
      progress = (mCurrentTestCount*100) / mTotalTestCount;
    }
    return progress;
  }  
  
  /**
   * Called by the progress bar component.
   * Just post a growl style notification.
   */
  public void onComplete() {
    if (FacesContext.getCurrentInstance() != null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Tests Completed"));
    }
  }

  public void onStarted() {
    if (FacesContext.getCurrentInstance() != null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Tests Started"));
    }
  }
  
  
  
  protected abstract TestDescription getCurrentRow();


  protected abstract List<TestDescription> getSelectedTests();
  

  protected void includeAllLeafs(TestDescription testDescription, List<TestDescription> list) {
    if (!testDescription.isSuite()) {
      if (!list.contains(testDescription)) {
        list.add(testDescription);
      }
    } else {
      for (TestDescription child : testDescription.getTestDescriptions()) {
        includeAllLeafs(child, list);
      }
    }
  }

  protected void includeAll(TestDescription testDescription, List<TestDescription> list) {
    if (!list.contains(testDescription)) {
      list.add(testDescription);
    }
    for (TestDescription child : testDescription.getTestDescriptions()) {
      includeAll(child, list);
    }
  }
  
  /**
   * 
   * @return the class loader that will be used to run the tests with
   */
  @Override
  public ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  public int getCurrentTestCount() {
    return mCurrentTestCount;
  }

  public int getTotalTestCount() {
    return mTotalTestCount;
  }

  public int getErrorCount() {
    return mErrorCount;
  }

  public int getFailureCount() {
    return mFailureCount;
  }

  public int getIgnoredCount() {
    return mIgnoredCount;
  }
  
  public Status getOverallStatus() {
    return mTestDescription.getStatus();
  }
  
  public Double getOverallTime() {
    return mTestDescription.getTimeAsSeconds();
  }

  @Override
  public void setResult(int currentTestCount, int errorCount, int failureCount, int ignoredCount) {
    mCurrentTestCount = currentTestCount;
    mErrorCount = errorCount;
    mFailureCount = failureCount;
    mIgnoredCount = ignoredCount;
  }

  
  public String getFact() {
    return new FactGenerator().random();
  }
  
  public boolean isThemeChuckNorrisEnabled() {
    return (mTheme.equalsIgnoreCase("chucknorris"));
  }
}
