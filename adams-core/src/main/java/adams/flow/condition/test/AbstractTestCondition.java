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
 * AbstractTestCondition.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;


import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;

/**
 * Abstract base class for conditions.
 *
 * Derived classes only have to override the <code>performTest()</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options, which invalidate
 * the previously run tests.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTestCondition
  extends AbstractOptionHandler
  implements Comparable, TestCondition {

  /** for serialization. */
  private static final long serialVersionUID = 3879444121041551968L;

  /** whether the condition has been tested. */
  protected boolean m_Tested;

  /** the test result, null=test passed, otherwise error message. */
  protected String m_TestResult;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    m_TestResult = null;
  }

  /**
   * Resets the condition.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
    m_Tested     = false;
    m_TestResult = null;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  protected abstract String performTest();

  /**
   * Returns the test result of testing the conditions. Performs the test, if
   * necessary.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  public String getTestResult() {
    if (!m_Tested) {
      m_TestResult = performTest();
      m_Tested     = true;
    }

    return m_TestResult;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public TestCondition shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public TestCondition shallowCopy(boolean expand) {
    return (TestCondition) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of filters.
   *
   * @return		the filter classnames
   */
  public static String[] getConditions() {
    return ClassLister.getSingleton().getClassnames(TestCondition.class);
  }

  /**
   * Instantiates the filter with the given options.
   *
   * @param classname	the classname of the filter to instantiate
   * @param options	the options for the filter
   * @return		the instantiated filter or null if an error occurred
   */
  public static TestCondition forName(String classname, String[] options) {
    TestCondition	result;

    try {
      result = (TestCondition) OptionUtils.forName(TestCondition.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the filter from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			filter to instantiate
   * @return		the instantiated filter
   * 			or null if an error occurred
   */
  public static TestCondition forCommandLine(String cmdline) {
    return (TestCondition) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
