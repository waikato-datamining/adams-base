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
 * AbstractInstanceEvaluator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.evaluator;

import weka.core.Instance;
import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Ancestor for evaluators that evaluate weka.core.Instance objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInstanceEvaluator
  extends AbstractOptionHandler
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7170199569631424162L;

  /** whether the evaluator got initialized. */
  protected boolean m_Initialized;

  /**
   * Resets the scheme.
   */
  protected void reset() {
    super.reset();

    m_Initialized = false;
  }

  /**
   * Performs necessary initializations before being able to evaluate.
   * <br><br>
   * Default implementation only returns null.
   *
   * @return		null if everything fine, error message otherwise
   */
  protected String setUp() {
    return null;
  }

  /**
   * Peforms checks on the instance that is about to be evaluated.
   * <br><br>
   * Default implementation only checks whether class value is non-missing.
   *
   * @param inst	the instance to evaluate
   * @return		null if Instance can be evaluated, error message otherwise
   */
  protected String check(Instance inst) {
    if (inst.classIsMissing())
      return "No class value set!";
    else
      return null;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param inst	the instance to evaluate
   * @return		evaluation range, between 0 and 1 (0 = bad, 1 = good, -1 = if unable to evaluate)
   */
  protected abstract double doEvaluate(Instance inst);

  /**
   * Evaluates the given instance.
   *
   * @param inst	the instance to evaluate
   * @return		evaluation range, between 0 and 1 (0 = bad, 1 = good, -1 = if unable to evaluate)
   */
  public double evaluate(Instance inst) {
    String	msg;

    if (!m_Initialized) {
      msg           = setUp();
      m_Initialized = (msg == null);
      if (msg != null) {
	throw new IllegalStateException(
	    "Failed to initialize " + getClass().getName() + "!\n" + msg);
      }
    }

    msg = check(inst);
    if (msg != null)
      throw new IllegalStateException(
	  "Instance cannot be evaluated:\n" + msg);

    return doEvaluate(inst);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
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

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((AbstractInstanceEvaluator) o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractInstanceEvaluator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractInstanceEvaluator shallowCopy(boolean expand) {
    return (AbstractInstanceEvaluator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of evaluators.
   *
   * @return		the evaluator classnames
   */
  public static String[] getEvaluators() {
    return ClassLister.getSingleton().getClassnames(AbstractInstanceEvaluator.class);
  }

  /**
   * Instantiates the evaluator with the given options.
   *
   * @param classname	the classname of the evaluator to instantiate
   * @param options	the options for the evaluator
   * @return		the instantiated evaluator or null if an error occurred
   */
  public static AbstractInstanceEvaluator forName(String classname, String[] options) {
    AbstractInstanceEvaluator	result;

    try {
      result = (AbstractInstanceEvaluator) OptionUtils.forName(AbstractInstanceEvaluator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the evaluator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			evaluator to instantiate
   * @return		the instantiated evaluator
   * 			or null if an error occurred
   */
  public static AbstractInstanceEvaluator forCommandLine(String cmdline) {
    return (AbstractInstanceEvaluator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br><br>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Calls cleanUp() and cleans up the options.
   */
  public void destroy() {
    cleanUp();
    super.destroy();
  }
}
