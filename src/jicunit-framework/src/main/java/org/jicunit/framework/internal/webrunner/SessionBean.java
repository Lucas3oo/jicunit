package org.jicunit.framework.internal.webrunner;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetTreeImpl;
import org.apache.myfaces.trinidad.model.TreeModel;
import org.jicunit.framework.internal.model.TestDescription;

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
@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private ApplicationBean mApplicationBean;
  
  @EJB
  private RunnerBean mRunnerBean;

  private TreeModel mTestDescriptionTreeModel;
  private RowKeySet mSelectedRowKeySet;
  private volatile boolean mRunning = false;

  // root of the tree of test suites/classes test methods
  private TestDescription mTestDescription;

  // count for last run
  private volatile int mTotalTestCount;
  private volatile int mCurrentTestCount;
  private volatile int mErrorCount;
  private volatile int mFailureCount;
  private volatile int mIgnoredCount;

  public SessionBean() {
  }

  // only for unit test
  protected SessionBean(ApplicationBean applicationBean, RunnerBean runnerBean) {
    mApplicationBean = applicationBean;
    mRunnerBean = runnerBean;
  }

  @PostConstruct
  public void init() {
    mTestDescription = mApplicationBean.getTestDescription();
    mTestDescriptionTreeModel = new ChildPropertyTreeModel(mTestDescription, "testDescriptions");
    mSelectedRowKeySet = new RowKeySetTreeImpl();
  }
  
  
  public void clearResults() {
    mTestDescription.clearResult();

    mTotalTestCount = 0;
    mCurrentTestCount = 0;
    mErrorCount = 0;
    mFailureCount = 0;
    mIgnoredCount = 0;
  }

  public boolean isRunning() {
    return mRunning;
  }

  
  public void beginRun() {
    if (!isRunning()) {
      clearResults();
      run();
    }
  }

  public void beginRunSingle() {
    if (!isRunning()) {
      clearResults();
      runSingle();
    }
  }
  
  public void endRun() {
    mRunning = false;
  }

  public void cancelRun() {
    endRun();
  }

  /**
   * Execute the selected tests
   */
  protected void run() {
    List<TestDescription> selectedTests = getSelectedTests(getSelectedRowKeys());
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
    Object rowData = mTestDescriptionTreeModel.getRowData();
    if (rowData != null && (rowData instanceof TestDescription)) {
      TestDescription testDescription = (TestDescription) rowData;
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

  private List<List<Integer>> getSelectedRowKeys() {
    List<List<Integer>> selection = new ArrayList<List<Integer>>();
    Iterator<Object> iterator = mSelectedRowKeySet.iterator();
    while (iterator.hasNext()) {
      @SuppressWarnings("unchecked")
      List<Object> rowKey = (List<Object>) iterator.next();
      List<Integer> rowKeyAsInt = new ArrayList<>();
      for (Object seg : rowKey.subList(1, rowKey.size())) {
        Integer segAsInt = (Integer) seg;
        rowKeyAsInt.add(segAsInt);
      }
      selection.add(rowKeyAsInt);
    }
    return selection;
  }

  /**
   * 
   * @param selectedRowKeys
   *          list of paths in the tree
   * @return List of TestDescription
   */
  protected List<TestDescription> getSelectedTests(List<List<Integer>> selectedRowKeys) {
    List<TestDescription> list = new ArrayList<>();
    for (List<Integer> selectedPath : selectedRowKeys) {
      TestDescription testDescription = getSelectedTest(mTestDescription, selectedPath);
      if (!list.contains(testDescription)) {
        list.add(testDescription);
      }
    }
    return list;
  }

  /**
   * 
   * @param testDescription
   *          root
   * @param selectedPath
   *          points to which node in the tree that shall be included
   */
  protected TestDescription getSelectedTest(TestDescription testDescription,
      List<Integer> selectedPath) {
    TestDescription selected = testDescription.getTestDescriptions().get(selectedPath.get(0));
    if (selectedPath.size() == 1) {
      return selected;
    } else {
      return getSelectedTest(selected, selectedPath.subList(1, selectedPath.size()));
    }
  }

  protected void includeAllLeafs(TestDescription testDescription, List<TestDescription> list) {
    if (testDescription.getTestDescriptions().size() == 0) {
      if (!list.contains(testDescription)) {
        list.add(testDescription);
      }
    } else {
      for (TestDescription child : testDescription.getTestDescriptions()) {
        includeAllLeafs(child, list);
      }
    }
  }

  /**
   * 
   * @return the class loader that will be used to run the tests with
   */
  protected ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  public TreeModel getTests() throws IntrospectionException {
    return mTestDescriptionTreeModel;
  }

  public RowKeySet getSelectedRowKeySet() {
    return mSelectedRowKeySet;
  }

  public void setSelectedRowKeySet(RowKeySet selectedRowKeySet) {
    mSelectedRowKeySet = selectedRowKeySet;
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

  public void setResult(int currentTestCount, int errorCount, int failureCount, int ignoredCount) {
    mCurrentTestCount = currentTestCount;
    mErrorCount = errorCount;
    mFailureCount = failureCount;
    mIgnoredCount = ignoredCount;    
  }
  
  


}
