package org.jicunit.framework.internal.webrunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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
//@javax.faces.bean.ManagedBean(name = "jSessionBean")
//@javax.faces.bean.ViewScoped
@javax.inject.Named("jSessionBean")
@javax.enterprise.context.SessionScoped
//@javax.faces.view.ViewScoped
public class JsfView extends ViewBase implements RunnerCallback, Serializable {

  private static final long serialVersionUID = 1L;


  private DataModel<TestDescription> mDataModel;
  private Map<TestDescription, Boolean> mSelectedTests = new HashMap<>();


  public JsfView() {
    super();
  }

  // only for unit test
  protected JsfView(SessionBean sessionBean, RunnerBean runnerBean) {
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

    mDataModel = new ListDataModel<TestDescription>(leafs);
  }


  @Override
  protected TestDescription getCurrentRow() {
    return mDataModel.getRowData();
  }

  
  @Override
  protected List<TestDescription> getSelectedTests() {
    List<TestDescription> selectedTests = new ArrayList<>();
    for (TestDescription testDescription : mDataModel) {
      if (mSelectedTests.get(testDescription) != null
          && (mSelectedTests.get(testDescription) == true)) {
        selectedTests.add(testDescription);
      }
    }
    return selectedTests;
  }

  
  public DataModel<TestDescription> getTests() {
    return mDataModel;
  }

  public Map<TestDescription, Boolean> getSelected() {
    return mSelectedTests;
  }
  

}
