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

/*
 * Copyright (C) 2010-2013 University of Waikato
 */

package weka.core;

import java.io.Serializable;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.ListParameter;
import weka.core.setupgenerator.MathParameter;
import weka.test.AdamsTestHelper;
import weka.test.Regression;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests SetupGenerator. Run from the command line with:<br><br>
 * java weka.core.SetupGeneratorTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetupGeneratorTest
  extends AdamsTestCase {
  
  static {
    AdamsTestHelper.setRegressionRoot();
  }

  /**
   * Constructs the <code>SetupGeneratorTest</code>.
   *
   * @param name 	the name of the test
   */
  public SetupGeneratorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    Environment.setEnvironmentClass(adams.env.Environment.class);

    super.setUp();
  }

  /**
   * Returns a fully configured generator.
   *
   * @return		the generator
   */
  protected SetupGenerator getGenerator() {
    SetupGenerator		generator;
    AbstractParameter[]		params;
    weka.classifiers.Classifier	fc;

    // setup the generator
    generator = new SetupGenerator();
    fc = new weka.classifiers.meta.FilteredClassifier();
    ((weka.classifiers.meta.FilteredClassifier) fc).setFilter(new weka.filters.supervised.attribute.PLSFilter());
    ((weka.classifiers.meta.FilteredClassifier) fc).setClassifier(new weka.classifiers.functions.LinearRegression());
    generator.setBaseObject((Serializable) fc);

    params = new AbstractParameter[3];

    params[0] = new MathParameter();
    ((MathParameter) params[0]).setProperty("classifier.ridge");
    ((MathParameter) params[0]).setMin(-5);
    ((MathParameter) params[0]).setMax(+3);
    ((MathParameter) params[0]).setStep(1);
    ((MathParameter) params[0]).setBase(10);
    ((MathParameter) params[0]).setExpression("pow(BASE,I)");

    params[1] = new MathParameter();
    ((MathParameter) params[1]).setProperty("filter.numComponents");
    ((MathParameter) params[1]).setMin(+5);
    ((MathParameter) params[1]).setMax(+20);
    ((MathParameter) params[1]).setStep(1);
    ((MathParameter) params[1]).setBase(10);
    ((MathParameter) params[1]).setExpression("I");

    params[2] = new ListParameter();
    ((ListParameter) params[2]).setProperty("filter.algorithm");
    ((ListParameter) params[2]).setList("PLS1 SIMPLS");

    generator.setParameters(params);

    return generator;
  }

  /**
   * Runs a typical setup.
   */
  public void testTypical() {
    SetupGenerator		generator;
    Enumeration<Serializable>	setups;
    Serializable		obj;
    int				count;

    generator = getGenerator();
    setups    = generator.setups();
    assertNotNull("Error encountered in setup generation", setups);
    assertTrue("Failed to generate setups", setups.hasMoreElements());

    count = 0;
    while (setups.hasMoreElements()) {
      count++;
      obj = setups.nextElement();
      assertNotNull("Problem with setup #" + count, obj);
    }
  }

  /**
   * Runs a regression test -- this checks that the output of the tested
   * object matches that in a reference version. When this test is
   * run without any pre-existing reference output, the reference version
   * is created.
   *
   * @throws Exception 	if something goes wrong
   */
  public void testRegression() throws Exception {
    Regression 			reg;
    StringBuilder		results;
    SetupGenerator		generator;
    Enumeration<Serializable>	setups;
    Serializable		obj;

    if (m_NoRegressionTest)
      return;

    results   = new StringBuilder();
    reg       = new Regression(getClass());
    generator = getGenerator();
    setups    = generator.setups();
    assertNotNull("Error encountered in setup generation", setups);

    while (setups.hasMoreElements()) {
      obj = setups.nextElement();
      results.append(obj.getClass().getName());
      if (obj instanceof OptionHandler)
	results.append(" " + Utils.joinOptions(((OptionHandler) obj).getOptions()));
      results.append("\n");
    }
    reg.println(results.toString());

    try {
      String diff = reg.diff();
      if (diff == null) {
        System.err.println("Warning: No reference available, creating.");
      } else if (!diff.equals("")) {
        fail("Regression test failed. Difference:\n" + diff);
      }
    }
    catch (java.io.IOException ex) {
      fail("Problem during regression testing.\n" + ex);
    }
  }

  /**
   * Returns a suite for this test.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SetupGeneratorTest.class);
  }

  /**
   * Runs the test from the command-line.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }
}
