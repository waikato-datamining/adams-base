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
 * AbstractActorProcessor.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Abstract base class for schemes that process a flow/actor in some fashion.
 *
 * Derived classes only have to override the <code>processActor()</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options, which invalidate
 * the previously generated data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractActorProcessor
  extends AbstractOptionHandler
  implements Comparable, ShallowCopySupporter<AbstractActorProcessor> {

  /** for serialization. */
  private static final long serialVersionUID = 3610605513320220903L;

  /** for collecting errors. */
  protected ArrayList<String> m_Errors;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Errors = new ArrayList<String>();
  };
  
  /**
   * Resets the filter.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
    super.reset();
    
    m_Errors.clear();
  }

  /**
   * Processes the actor.
   *
   * @param actor	the actor to process
   */
  public void process(Actor actor) {
    m_Errors.clear();

    try {
      checkData(actor);
      processActor(actor);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to processed actor: " + actor, e);
      m_Errors.add("Failed to processed actor: " + Utils.throwableToString(e));
    }
  }

  /**
   * The default implementation only checks whether there is any actor set.
   *
   * @param actor	the actor to process
   */
  protected void checkData(Actor actor) {
    if (actor == null)
      throw new IllegalStateException("No actor provided!");
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  protected abstract void processActor(Actor actor);

  /**
   * Adds the error message to the internal list of errors.
   * Outputs the error on stderr as well.
   * 
   * @param msg		the error message to add
   */
  protected void addError(String msg) {
    addError(msg, true);
  }

  /**
   * Adds the error message to the internal list of errors and optionally
   * outputs it on stderr.
   * 
   * @param msg		the error message to add
   */
  protected void addError(String msg, boolean output) {
    m_Errors.add(msg);
    if (output)
      getLogger().severe(msg);
  }
  
  /**
   * Checks whether any errors were encountered.
   * 
   * @return		true if errors were encountered
   * @see		#getErrors()
   */
  public boolean hasErrors() {
    return (m_Errors.size() > 0);
  }
  
  /**
   * Returns the list of errors (if any).
   * 
   * @return		the errors
   */
  public List<String> getErrors() {
    return m_Errors;
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

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
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
  public AbstractActorProcessor shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractActorProcessor shallowCopy(boolean expand) {
    return (AbstractActorProcessor) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of processors.
   *
   * @return		the processor classnames
   */
  public static String[] getProcessors() {
    return ClassLister.getSingleton().getClassnames(AbstractActorProcessor.class);
  }

  /**
   * Instantiates the processor with the given options.
   *
   * @param classname	the classname of the processor to instantiate
   * @param options	the options for the processor
   * @return		the instantiated processor or null if an error occurred
   */
  public static AbstractActorProcessor forName(String classname, String[] options) {
    AbstractActorProcessor	result;

    try {
      result = (AbstractActorProcessor) OptionUtils.forName(AbstractActorProcessor.class, classname, options);
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
  public static AbstractActorProcessor forCommandLine(String cmdline) {
    return (AbstractActorProcessor) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
