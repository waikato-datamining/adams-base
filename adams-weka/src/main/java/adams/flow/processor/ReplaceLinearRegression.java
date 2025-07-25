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
 * ReplaceLinearRegression.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import adams.core.Utils;
import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.Actor;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.LinearRegressionJ;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Replaces all LinearRegression occurrences with the LinearRegressionJ pure Java version.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReplaceLinearRegression
  extends AbstractModifyingProcessor {

  private static final long serialVersionUID = 4995926193024269006L;

  /**
   * Switches to LinearRegressionJ.
   */
  public static class LinearRegressionObserver
    implements Observer {

    /** the number of updates. */
    protected int m_Updates;

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     * @return		true if to continue observing
     */
    @Override
    public boolean observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (child.getClass().equals(LinearRegression.class)) {
	LinearRegression lr = (LinearRegression) child;
	LinearRegressionJ lrj = new LinearRegressionJ();
	try {
	  lrj.setOptions(lr.getOptions());
	  Method method = desc.getWriteMethod();
	  method.invoke(parent, lrj);
	  m_Updates++;
	}
	catch (Exception e) {
	  LoggingHelper.global().log(Level.SEVERE, "Failed to convert LinearRegression at " + path + " to LinearRegressionJ!", e);
	}
      }
      return true;
    }

    /**
     * Returns the number of updates that occurred.
     *
     * @return		the number of updates
     */
    public int getUpdates() {
      return m_Updates;
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces all " + Utils.classToString(LinearRegression.class) + " occurrences with "
	     + "the " + Utils.classToString(LinearRegressionJ.class) + " pure Java version.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(Actor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      @Override
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// nothing to do
      }
      @Override
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	LinearRegressionObserver observer = new LinearRegressionObserver();
	PropertyTraversal traversal = new PropertyTraversal();
	traversal.traverse(observer, current);
	if (observer.getUpdates() > 0)
	  m_Modified = true;
      }
      @Override
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// nothing to do
      }
      @Override
      public boolean canHandle(AbstractOption option) {
	return (option instanceof ClassOption);
      }
      @Override
      public boolean canRecurse(Class cls) {
	return true;
      }
      @Override
      public boolean canRecurse(Object obj) {
	return true;
      }
    });
  }
}
