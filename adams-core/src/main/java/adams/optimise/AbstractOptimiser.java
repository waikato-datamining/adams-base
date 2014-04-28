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
 * AbstractOptimiser.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Abstract ancestor for optimization schemes.
 *
 * @author Dale
 * @version $Revision$
 */
public abstract class AbstractOptimiser
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler {

  /** suid. */
  private static final long serialVersionUID = -8047951676584896826L;

  /** curr best fitness.*/
  protected Double m_bestf;

  /** curr best vars . */
  protected OptData m_bestv;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_bestv = null;
    m_bestf = Double.NEGATIVE_INFINITY;
  }

  /**
   * Resets the optimizer.
   */
  public void reset() {
    super.reset();

    m_bestv = null;
    m_bestf = Double.NEGATIVE_INFINITY;
  }

  /**
   * Do the optimisation.
   *
   * @param datadef	data initialisations.
   * @param fitness	fitness function.
   * @return		best vars
   */
  public abstract OptData optimise(OptData datadef, FitnessFunction fitness);

  public synchronized void checkBest(Double fitness, OptData vars, FitnessFunction ff) {
    if (fitness > m_bestf) {
      m_bestf=new Double(fitness);
      if (m_bestv != null)
	m_bestv.cleanUp();
      m_bestv=vars.getClone();
      ff.newBest(fitness,vars);
    }
  }

  /**
   * Default implementation does nothing.
   */
  public void cleanUp() {
    if (m_bestv != null)
      m_bestv.cleanUp();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Calls cleanUp() and cleans up the options.
   */
  public void destroy() {
    cleanUp();
    super.destroy();
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
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractOptimiser shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractOptimiser shallowCopy(boolean expand) {
    return (AbstractOptimiser) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of optimiser.
   *
   * @return		the optimiser classnames
   */
  public static String[] getOptimisers() {
    return ClassLister.getSingleton().getClassnames(AbstractOptimiser.class);
  }

  /**
   * Instantiates the optimiser with the given options.
   *
   * @param classname	the classname of the optimiser to instantiate
   * @param options	the options for the optimiser
   * @return		the instantiated optimiser or null if an error occurred
   */
  public static AbstractOptimiser forName(String classname, String[] options) {
    AbstractOptimiser	result;

    try {
      result = (AbstractOptimiser) OptionUtils.forName(AbstractOptimiser.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the  optimiser from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			generator to instantiate
   * @return		the instantiated optimiser
   * 			or null if an error occurred
   */
  public static AbstractOptimiser forCommandLine(String cmdline) {
    return (AbstractOptimiser) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
