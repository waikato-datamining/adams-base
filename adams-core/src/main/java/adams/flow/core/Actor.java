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
 * Actor.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.awt.Component;
import java.util.HashSet;

import adams.core.AdditionalInformationHandler;
import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.Stoppable;
import adams.core.Variables;
import adams.core.base.BaseAnnotation;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;
import adams.core.option.OptionHandler;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeListener;
import adams.flow.control.Flow;
import adams.flow.control.ScopeHandler;
import adams.flow.control.StorageHandler;
import adams.flow.execution.FlowExecutionListeningSupporter;

/**
 * Interface for actors.
 * <p/>
 * NB: Not yet final version. Using ShallowCopySupporter&lt;Actor&gt; has 
 * far-reaching consequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Actor
  extends Comparable, AdditionalInformationHandler,
          CleanUpHandler, Stoppable, VariableChangeListener, OptionHandler, 
          /*ShallowCopySupporter<Actor>,*/ QuickInfoSupporter, ErrorHandler, 
          LoggingSupporter, LoggingLevelHandler {
  // TODO use Actor interface

  /** the file extension for flows (excl. dot). */
  public final static String FILE_EXTENSION = "flow";

  /** the file extension for gzipped flows (excl. dot). */
  public final static String FILE_EXTENSION_GZ = "flow.gz";

  /**
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  public void setHeadless(boolean value);

  /**
   * Returns whether the actor is run in headless mode.
   *
   * @return		true if GUI components are suppressed
   */
  public boolean isHeadless();

  /**
   * Returns the default name of the actor.
   *
   * @return		the default name
   */
  public String getDefaultName();

  /**
   * Sets the name of the actor.
   *
   * @param value 	the name
   */
  public void setName(String value);

  /**
   * Returns the name of the actor.
   *
   * @return 		the name
   */
  public String getName();

  /**
   * Sets the annoations.
   *
   * @param value	the annotations
   */
  public void setAnnotations(BaseAnnotation value);

  /**
   * Returns the current annotations.
   *
   * @return		the annotations
   */
  public BaseAnnotation getAnnotations();

  /**
   * Sets whether the transformation is skipped or not.
   *
   * @param value 	true if transformation is to be skipped
   */
  public void setSkip(boolean value);

  /**
   * Returns whether transformation is skipped.
   *
   * @return 		true if transformation is skipped
   */
  public boolean getSkip();

  /**
   * Sets whether to stop the flow in case this actor encounters an error.
   *
   * @param value 	true if flow gets stopped in case of an error
   */
  public void setStopFlowOnError(boolean value);

  /**
   * Returns whether to stop the flow in case this actor encounters an error.
   *
   * @return 		true if flow gets stopped in case of an error
   */
  public boolean getStopFlowOnError();

  /**
   * Handles the given error message with the flow that this actor belongs to,
   * if the flow has error logging turned on. Might stop the flow as well.
   *
   * @param source	the source of the error
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		null if error has been handled, otherwise the error message
   * @see		Flow#getLogErrors()
   * @see		Flow#getErrorHandling()
   * @see		#getStopFlowOnError()
   */
  // TODO use Actor interface
  public String handleError(AbstractActor source, String type, String msg);

  /**
   * Sets the parent of this actor, e.g., the group it belongs to.
   *
   * @param value	the new parent
   */
  // TODO use Actor interface
  public void setParent(AbstractActor value);

  /**
   * Returns the parent of this actor, e.g., the group.
   *
   * @return		the current parent, can be null
   */
  public Actor getParent();
  
  /**
   * Returns the current parent component for interactive actors.
   * 
   * @return		the parent, null if not set
   */
  public Component getParentComponent();

  /**
   * Returns the index of this actor in its parent's collection.
   *
   * @return		the index, -1 if not applicable (e.g., no parent set)
   */
  public int index();

  /**
   * Returns the root of this actor, e.g., the group at the highest level.
   *
   * @return		the root, can be null
   */
  public Actor getRoot();

  /**
   * Updates the Variables instance in use, if different from current one.
   * <p/>
   * Use with caution!
   *
   * @param value	the instance to use
   * @see		#forceVariables(Variables)
   */
  public void setVariables(Variables value);

  /**
   * Returns the Variables instance to use.
   *
   * @return		the variables instance
   */
  public Variables getVariables();

  /**
   * Returns the storage handler to use.
   *
   * @return		the storage handler
   */
  public StorageHandler getStorageHandler();

  /**
   * Returns the scope handler for this actor.
   *
   * @return		the scope handler
   */
  public ScopeHandler getScopeHandler();

  /**
   * Returns the flow execution handler in use.
   * 
   * @return		the execution handler
   */
  public FlowExecutionListeningSupporter getFlowExecutionListeningSupporter();
  
  /**
   * Returns the full name of the actor, i.e., the concatenated names of all
   * parents. Used in error messages.
   *
   * @return		the full name
   */
  public String getFullName();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo();

  /**
   * Checks if an error handler is set.
   * 
   * @return		true if an error handler is set
   */
  public boolean hasErrorHandler();

  /**
   * Sets the error handler to use for handling errors in the flow.
   * 
   * @param value	the error handler
   */
  public void setErrorHandler(ErrorHandler value);

  /**
   * Returns the current error handler for handling errors in the flow.
   * 
   * @return		the error handler
   */
  public ErrorHandler getErrorHandler();

  /**
   * Recursively finds all the variables used in the actor's setup.
   *
   * @return		the variables that were found
   */
  public HashSet<String> findVariables();

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e);

  /**
   * Returns the variables this actor is responsible for.
   *
   * @return		the variables
   */
  public HashSet<String> getDetectedVariables();

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#reset()
   */
  public String setUp();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String execute();

  /**
   * Returns whether the item has finished. The <code>execute()</code> will be
   * called as long as the <code>isFinished()</code> method returns false.
   *
   * @return		true if finished, false if further calls to execute()
   * 			are necessary. Default implementation returns always
   * 			true, i.e., fires only once.
   */
  public boolean isFinished();

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  public void wrapUp();

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp();

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Calls cleanUp().
   */
  @Override
  public void destroy();

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution();

  /**
   * Stops the execution.
   *
   * @param msg		the message to set as reason for stopping, can be null
   */
  public void stopExecution(String msg);

  /**
   * Returns whether the execution was stopped.
   *
   * @return		true if the execution was stopped
   */
  public boolean isStopped();

  /**
   * Returns whether a stop message is available (in case the flow was stopped
   * with a message).
   *
   * @return		true if a message is available
   */
  public boolean hasStopMessage();

  /**
   * Returns the stop message.
   *
   * @return		the message, can be null
   */
  public String getStopMessage();

  /**
   * Returns whether the actor has been executed, after setting it up.
   *
   * @return		true if the actor has been executed
   */
  public boolean isExecuted();

  /**
   * If the actor is part of a group, this method returns the actor
   * preceding it in that group.
   *
   * @return		the preceding actor, null if not available
   */
  public Actor getPreviousSibling();

  /**
   * If the actor is part of a group, this method returns the actor
   * following it in that group.
   *
   * @return		the following actor, null if not available
   */
  public Actor getNextSibling();

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  @Override
  public int compareTo(Object o);

  /**
   * Returns whether the two objects are the same.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o);

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  // TODO use Actor interface
  //@Override
  //public Actor shallowCopy();

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  // TODO use Actor interface
  //@Override
  //public Actor shallowCopy(boolean expand);

  /**
   * Returns the size of the object.
   *
   * @return		the size of the object
   */
  public int sizeOf();
}
