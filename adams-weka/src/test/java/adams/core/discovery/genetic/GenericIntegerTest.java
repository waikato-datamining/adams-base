/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * GenericIntegerTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.genetic;

import adams.core.discovery.AbstractDiscoveryHandler;
import adams.core.discovery.PropertyDiscovery;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.classifiers.functions.PLSClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.AllFilter;
import weka.filters.supervised.attribute.PLSFilter;

/**
 * Tests the GenericInteger discovery handler. Use the following to run from command-line:<br>
 * adams.core.discovery.GenericIntegerTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericIntegerTest
  extends AbstractGeneticDiscoveryHandlerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public GenericIntegerTest(String name) {
    super(name);
  }

  /**
   * Returns the handler instance to use for testing in the {@link #testPackUnpack()}
   * method.
   *
   * @return		the handler instance
   */
  @Override
  protected AbstractGeneticDiscoveryHandler getPackUnpackHandler() {
    return new GenericInteger();
  }

  /**
   * Returns the property container to use for testing in the {@link #testPackUnpack()}
   * method.
   *
   * @return		the handler instance
   */
  @Override
  protected PropertyContainer getPackUnpackContainer() {
    AbstractGeneticDiscoveryHandler	handler;
    PropertyDiscovery discovery;

    handler = getPackUnpackHandler();
    discovery = getDiscovery();
    discovery.discover(new AbstractDiscoveryHandler[]{handler}, getRegressionObjects()[0]);

    return handler.getContainers().get(0);
  }

  /**
   * Returns the objects to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionObjects() {
    FilteredClassifier inner;
    FilteredClassifier 	outer;

    inner = new FilteredClassifier();
    inner.setClassifier(new PLSClassifier());
    inner.setFilter(new AllFilter());

    outer = new FilteredClassifier();
    outer.setClassifier(inner);
    outer.setFilter(new AllFilter());

    return new Object[]{outer, inner, new PLSFilter()};
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractDiscoveryHandler[] getRegressionSetups() {
    return new AbstractDiscoveryHandler[] {
      new GenericInteger(),
      new GenericInteger(),
      new GenericInteger(),
    };
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(GenericIntegerTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
