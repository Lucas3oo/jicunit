package org.jicunit.framework.internal.webrunner;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jicunit.framework.internal.model.TestDescription;
import org.junit.Before;
import org.junit.Test;

public class TrinidadViewTest {

  private List<String> mTestSuiteNames = new ArrayList<>();
  private ApplicationBean mApplicationBean;
  private RunnerBean mRunnerBean;
  private Set<String> mExpectedSelectedTests;
  private List<List<Integer>> mSelection;
  private TestDescription mTestSuites;
  private SessionBean mSessionBean;

  @Before
  public void setUp() {
    mTestSuiteNames.add("org.jicunit.framework.samples.Junit4CategorySuite");
    mTestSuiteNames.add("org.jicunit.framework.samples.TestSampleJunit3");
    mTestSuiteNames.add("org.jicunit.framework.samples.TestSampleJunit4");
    mTestSuiteNames.add("org.jicunit.framework.samples.TestSampleJunit4Category");
    mTestSuiteNames.add("org.jicunit.framework.samples.TestSampleJunit4Parameterized");
    mApplicationBean = new ApplicationBean();
    mApplicationBean.setTestSuiteNames(mTestSuiteNames);
    mTestSuites = mApplicationBean.loadTestSuites();
    mRunnerBean = new RunnerBean();
    mSessionBean = new SessionBean(mApplicationBean);
    mSessionBean.init();
    

    mExpectedSelectedTests = new HashSet<>();
    // add first leaf
    mExpectedSelectedTests
        .add("testDoSomething(org.jicunit.framework.samples.TestSampleJunit4Category)");
    // add complete suite
    mExpectedSelectedTests.add("testDoSomething(org.jicunit.framework.samples.TestSampleJunit4)");
    mExpectedSelectedTests
        .add("testDoSomethingThatErrors(org.jicunit.framework.samples.TestSampleJunit4)");
    mExpectedSelectedTests
        .add("testDoSomethingThatFailes(org.jicunit.framework.samples.TestSampleJunit4)");
    mExpectedSelectedTests
        .add("testDoSomethingIgnore(org.jicunit.framework.samples.TestSampleJunit4)");
    // add last leaf
    mExpectedSelectedTests.add("testDoThis[1: FunkyData: Goo Duded](org.jicunit.framework.samples.TestSampleJunit4Parameterized)");

    mSelection = new ArrayList<List<Integer>>();
    // add first leaf
    mSelection.add(new ArrayList<>(Arrays.asList(0, 0, 0)));
    // add complete suite
    mSelection.add(new ArrayList<>(Arrays.asList(2)));
    // add test in the above suite to get an overlap
    mSelection.add(new ArrayList<>(Arrays.asList(2, 0)));
    // add last leaf
    mSelection.add(new ArrayList<>(Arrays.asList(4, 1, 1)));
  
  
  }

  @Test
  public void testGetSelectedTests() {
    TrinidadView trinidadView = new TrinidadView(mSessionBean, mRunnerBean);
    trinidadView.init();

    TestDescription testDescription = mTestSuites;
    printTestDescription(testDescription, "", 0);

    List<TestDescription> selectedTests = trinidadView.getSelectedTests(mSelection);

    List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<TestDescription>();
    for (TestDescription desc : selectedTests) {
      trinidadView.includeAllLeafs(desc, selectedTestsIncludedLeafs);
    }
    
    // Create Set of test names so it can be compared with the expectedSelectedTests set
    Set<String> actualSelectedTests = new HashSet<>();
    for (TestDescription desc : selectedTestsIncludedLeafs) {
      actualSelectedTests.add(desc.getDisplayName());
    }
    
    assertEquals(mExpectedSelectedTests, actualSelectedTests);
    //System.out.println(selectedTestsIncludedLeafs);

  }
  
  @Test
  public void testRun() {
    TrinidadView trinidadView = new TrinidadView(mSessionBean, mRunnerBean);
    trinidadView.init();
    List<TestDescription> selectedTests = trinidadView.getSelectedTests(mSelection);

    List<TestDescription> selectedTestsIncludedLeafs = new ArrayList<TestDescription>();
    for (TestDescription desc : selectedTests) {
      trinidadView.includeAllLeafs(desc, selectedTestsIncludedLeafs);
    }
    trinidadView.run(selectedTestsIncludedLeafs);
    
  }

  private void printTestDescription(TestDescription testDescription, String indent, int rowKey) {
    System.out.println(indent + testDescription + " ----- rowKey " + rowKey);
    int childKey = 0;
    for (TestDescription child : testDescription.getTestDescriptions()) {
      printTestDescription(child, indent + "  ", childKey++);
    }
  }

}
