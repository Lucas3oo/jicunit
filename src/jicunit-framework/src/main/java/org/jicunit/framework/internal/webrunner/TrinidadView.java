package org.jicunit.framework.internal.webrunner;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

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
//@javax.faces.bean.ManagedBean(name = "tSessionBean")
//@javax.faces.bean.ViewScoped
@javax.inject.Named("tSessionBean")
@javax.enterprise.context.SessionScoped
//@javax.faces.view.ViewScoped
public class TrinidadView extends ViewBase implements RunnerCallback, Serializable {

  private static final long serialVersionUID = 1L;


  private TreeModel mDataModel;
  private RowKeySet mSelectedRowKeySet;

  public TrinidadView() {
  }

  // only for unit test
  protected TrinidadView(SessionBean sessionBean, RunnerBean runnerBean) {
    super(sessionBean, runnerBean);
  }

  @PostConstruct
  public void init() {
    super.init();
    mDataModel = new ChildPropertyTreeModel(mTestDescription, "testDescriptions");
    mSelectedRowKeySet = new RowKeySetTreeImpl();
  }
  
  public TreeModel getTests() throws IntrospectionException {
    return mDataModel;
  }
  
  
  @Override
  protected TestDescription getCurrentRow() {
    Object rowData = mDataModel.getRowData();
    if (rowData != null && (rowData instanceof TestDescription)) {
      TestDescription testDescription = (TestDescription) rowData;
      return testDescription;
    }
    else {
      return null;
    }
  }
    

  @Override
  protected List<TestDescription> getSelectedTests() {
    return getSelectedTests(getSelectedRowKeys());
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
  List<TestDescription> getSelectedTests(List<List<Integer>> selectedRowKeys) {
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
  private TestDescription getSelectedTest(TestDescription testDescription,
      List<Integer> selectedPath) {
    TestDescription selected = testDescription.getTestDescriptions().get(selectedPath.get(0));
    if (selectedPath.size() == 1) {
      return selected;
    } else {
      return getSelectedTest(selected, selectedPath.subList(1, selectedPath.size()));
    }
  }



  public RowKeySet getSelectedRowKeySet() {
    return mSelectedRowKeySet;
  }

  public void setSelectedRowKeySet(RowKeySet selectedRowKeySet) {
    mSelectedRowKeySet = selectedRowKeySet;
  }





}
