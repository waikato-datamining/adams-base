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
 * Tool.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.tools;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Stoppable;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;


/**
 * An abstract class for general commandline-handling classes.
 *
 * @author dale
 * @version $Revision$
 */
public abstract class AbstractTool
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = 8248797808829239144L;

  /** whether the tool was stopped. */
  protected boolean m_Stopped;
  
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

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((AbstractTool) o));
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
   * Before the actual run is executed. Default implementation only resets the stopped flag.
   */
  protected void preRun() {
    m_Stopped = false;
  }

  /**
   * Contains the actual run code.
   */
  protected abstract void doRun();

  /**
   * After the actual run was executed. Default implementation does nothing.
   */
  protected void postRun() {
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Cleans up data structures, frees up memory. Default implementation
   * does nothing.
   */
  public void cleanUp() {
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }

  /**
   * Executes the tool.
   */
  public void run() {
    preRun();
    if (!m_Stopped)
      doRun();
    if (!m_Stopped)
      postRun();
    cleanUp();
  }

  /**
   * Returns whether the tool was stopped.
   * 
   * @return		true if the tool was stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }
  
  /**
   * Returns a list with classnames of tools.
   *
   * @return		the tool classnames
   */
  public static String[] getTools() {
    return ClassLister.getSingleton().getClassnames(AbstractTool.class);
  }

  /**
   * Instantiates the tool with the given options.
   *
   * @param classname	the classname of the tool to instantiate
   * @param options	the options for the tool
   * @return		the instantiated tool or null if an error occurred
   */
  public static AbstractTool forName(String classname, String[] options) {
    AbstractTool	result;

    try {
      result = (AbstractTool) OptionUtils.forName(AbstractTool.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the tool from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			tool to instantiate
   * @return		the instantiated tool
   * 			or null if an error occurred
   */
  public static AbstractTool forCommandLine(String cmdline) {
    return (AbstractTool) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
