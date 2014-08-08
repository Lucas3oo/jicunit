package org.jicunit.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import org.jicunit.framework.internal.BasicProxyRunner;
import org.jicunit.framework.internal.ContainerUrlHolder;
import org.jicunit.framework.internal.ParameterizedProxyRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

/**
 * A JUnit4 runner that delegates the actual execution of the test to be
 * performed in a JEE container. This is done via a HTTP call to the
 * <code>JicUnitServlet</code> via the URL specified in the System property of
 * <code>jicunit.url</code>. Typical the URL should be set to
 * <code> http://localhost:7001/my-jicunit-war/tests</code>
 * 
 * <p>
 * When the test is executed locally then this runner will delegate the
 * execution to the runner in the container. However this runner is also
 * instantiated in the container and then this runner will delegate the
 * execution to the default JUnit runner (or the runner specified by the
 * {@link RunInContainerWith} annotation).
 * 
 * 
 * @author lucas.persson
 *
 */
public class JicUnitRunner extends Runner implements Filterable, Sortable {

  public static final String CONTAINER_URL = "jicunit.url";

  private Runner mRunner;

  public JicUnitRunner(Class<?> testClass) throws Throwable {
    Class<? extends Runner> runnerClass;
    RunInContainerWith runInContainerWith = findAnnotation(testClass, RunInContainerWith.class);
    // figure out if this is happening locally or in the JEE container
    String containerUrl = getContainerUrl();
    if (containerUrl == null) {
      // this code is executed in the JEE container
      if (runInContainerWith != null) {
        runnerClass = runInContainerWith.value();
      } else {
        runnerClass = JUnit4.class;
      }
      try {
        mRunner = runnerClass.getDeclaredConstructor(Class.class).newInstance(testClass);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        throw new RuntimeException("Unable to create instanceof " + runnerClass, e);
      }
    } else {
      // this code is executed locally so create a ProxyRunner which will
      // forward the execution to the container
      if (runInContainerWith != null) {
        runnerClass = runInContainerWith.value();
        if (Parameterized.class.isAssignableFrom(runnerClass)) {
          mRunner = new ParameterizedProxyRunner(testClass, containerUrl);
        }
        else if (Suite.class.isAssignableFrom(runnerClass)) {
          throw new IllegalArgumentException(RunInContainerWith.class.getSimpleName()
              + " annotation does not support Suite runner or any subclass of Suite except Parameterized");
        }
      }
      else {
        mRunner = new BasicProxyRunner(testClass, containerUrl);
      }
    }

  }

  @Override
  public Description getDescription() {
    return mRunner.getDescription();
  }

  @Override
  public void run(RunNotifier notifier) {
    mRunner.run(notifier);
  }

  @Override
  public void sort(Sorter sorter) {
    // it is not sure that the custom runner support sorting so sorting is done
    // here too.
    if (mRunner instanceof Sortable) {
      Sortable sortableRunner = (Sortable) mRunner;
      sortableRunner.sort(sorter);
    }
  }

  @Override
  public void filter(Filter filter) throws NoTestsRemainException {
    // it is not sure that the custom runner support filtering so filtering is
    // done here too.
    if (mRunner instanceof Filterable) {
      Filterable filterableRunner = (Filterable) mRunner;
      filterableRunner.filter(filter);
    }
  }

  private static <A extends Annotation> A findAnnotation(final Class<?> clazz,
      final Class<A> annotationType) {
    A annotation = clazz.getAnnotation(annotationType);
    if (annotation != null) {
      return annotation;
    }
    for (Class<?> ifc : clazz.getInterfaces()) {
      annotation = findAnnotation(ifc, annotationType);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }

  /**
   * Retrieves the container from the system property {@link JicUnitRunner#CONTAINER_URL}.
   * 
   * @return URL pointing to the container
   */
  protected String getContainerUrl() {
    String containerUrl = System.getProperty(CONTAINER_URL);
    if (containerUrl == null) {
      // fallback to look for a thread local variable. Only useful for testing
      // of JicUNit itself
      containerUrl = ContainerUrlHolder.get();
    }
    return containerUrl;
  }

}
