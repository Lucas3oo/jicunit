package org.jicunit.framework.internal.webrunner;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.internal.model.TestDescription.Status;

/**
 * JSF SessionBean to implement a JUnit runner GUI
 * 
 * The bean must be passivation capable hence it implements Serializable. This
 * is a WELD requirement.
 * 
 * @author lucas
 *
 */
// @ManagedBean(name = "sessionBean")
// @SessionScoped
@Named("jSessionBean")
@SessionScoped
public class JsfSessionBean implements RunnerCallback, Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private ApplicationBean mApplicationBean;

  @EJB
  private RunnerBean mRunnerBean;

  private DataModel<TestDescription> mDataModel;
  private Map<TestDescription, Boolean> mSelectedTests = new HashMap<>();

  private volatile boolean mRunning = false;

  // root of the tree of test suites/classes test methods
  private TestDescription mTestDescription;

  // count for last run
  private volatile int mTotalTestCount;
  private volatile int mCurrentTestCount;
  private volatile int mErrorCount;
  private volatile int mFailureCount;
  private volatile int mIgnoredCount;

  public JsfSessionBean() {
  }

  // only for unit test
  protected JsfSessionBean(ApplicationBean applicationBean, RunnerBean runnerBean) {
    mApplicationBean = applicationBean;
    mRunnerBean = runnerBean;
  }

  @PostConstruct
  public void init() {
    mTestDescription = mApplicationBean.getTestDescription();

    List<TestDescription> leafs = new ArrayList<>();

    // include all except the root test suit
    for (TestDescription testDescription : mTestDescription.getTestDescriptions()) {
      includeAll(testDescription, leafs);
    }

    mDataModel = new ListDataModel<TestDescription>(leafs);

  }

  public void clearResults() {
    mTestDescription.clearResult();
    mTotalTestCount = 0;
    mCurrentTestCount = 0;
    mErrorCount = 0;
    mFailureCount = 0;
    mIgnoredCount = 0;
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
  }

  public void cancelRun() {
    endRun();
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
    List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<>();
    for (TestDescription desc : selectedTests) {
      includeAllLeafs(desc, selectedTestsIncludedLeafs);
    }
    // run all tests in selectedTestsIncludedLeafs
    run(selectedTestsIncludedLeafs);

  }

  /**
   * Run the test on the current row. The test or test suite on the same row as
   * the button was clicked on.
   */
  protected void runSingle() {
    // get the current clicked row
    TestDescription testDescription = mDataModel.getRowData();
    if (testDescription != null) {
      List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<>();
      includeAllLeafs(testDescription, selectedTestsIncludedLeafs);
      run(selectedTestsIncludedLeafs);
    }
  }

  protected void run(List<TestDescription> selectedTests) {
    mRunning = true;
    mTotalTestCount = selectedTests.size();
    mRunnerBean.run(this, selectedTests);
  }

  private List<TestDescription> getSelectedTests() {
    List<TestDescription> selectedTests = new ArrayList<>();
    for (TestDescription testDescription : mDataModel) {
      if (mSelectedTests.get(testDescription) != null
          && (mSelectedTests.get(testDescription) == true)) {
        selectedTests.add(testDescription);
      }
    }
    return selectedTests;
  }

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

  public DataModel<TestDescription> getTests() throws IntrospectionException {
    return mDataModel;
  }

  public Map<TestDescription, Boolean> getSelected() {
    return mSelectedTests;
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

}
