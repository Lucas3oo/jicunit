package org.jicunit.framework.internal.webrunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jicunit.framework.internal.model.TestDescription;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * JSF/PrimeFaces SessionBean to implement a JUnit runner GUI
 * 
 * The bean must be passivation capable hence it implements Serializable. This
 * is a WELD requirement.
 * 
 * @author lucas
 *
 */
// @javax.faces.bean.ManagedBean(name = "pSessionBean")
// @javax.faces.bean.ViewScoped
@javax.inject.Named("pSessionBean")
@javax.enterprise.context.SessionScoped
// @javax.faces.view.ViewScoped
public class PrimeView extends ViewBase implements RunnerCallback, Serializable {

  private static final long serialVersionUID = 1L;

  private TreeNode mDataModel;
  private TreeNode[] mSelection = new TreeNode[] {};

  private TestDescription mCurrentRow;

  public PrimeView() {
    super();
  }

  // only for unit test
  protected PrimeView(SessionBean sessionBean, RunnerBean runnerBean) {
    super(sessionBean, runnerBean);
  }

  @PostConstruct
  public void init() {
    super.init();

    List<TestDescription> leafs = new ArrayList<>();
    // include all except the root test suit
    for (TestDescription testDescription : mTestDescription.getTestDescriptions()) {
      includeAll(testDescription, leafs);
    }

    mDataModel = buildTree(mTestDescription, null);
  }

  public TreeNode getTests() {
    return mDataModel;
  }

  public TreeNode[] getSelection() {
    return mSelection;
  }

  public void setSelection(TreeNode[] selection) {
    mSelection = selection;
  }

  protected TreeNode buildTree(TestDescription testDescription, TreeNode parentNode) {
    DefaultTreeNode node = new DefaultTreeNode(testDescription, parentNode);

    if (testDescription.isSuite()) {
      for (TestDescription childTest : testDescription.getTestDescriptions()) {
        buildTree(childTest, node);
      }
    }
    return node;
  }

  
  
  @Override
  public TestDescription getCurrentRow() {
    return mCurrentRow;
  }
  
  public void setCurrentRow(TestDescription testDescription) {
    mCurrentRow = testDescription;
  }

  @Override
  protected List<TestDescription> getSelectedTests() {
    List<TestDescription> tests = new ArrayList<TestDescription>(mSelection.length);
    for (TreeNode treeNode : mSelection) {
      TestDescription test = (TestDescription) treeNode.getData();
      tests.add(test);
    }
    return tests;
  }

    
  public void expandAll() {
    collapsingORexpanding(mDataModel, true);
  }
  public void collapseAll() {
    collapsingORexpanding(mDataModel, false);
  }

  private void collapsingORexpanding(TreeNode n, boolean expanding) {
    if (n.getChildren().size() == 0) {
      n.setSelected(false);
    } else {
      for (TreeNode s : n.getChildren()) {
        collapsingORexpanding(s, expanding);
      }
      n.setExpanded(expanding);
      n.setSelected(false);
    }
  }
  
  

}
