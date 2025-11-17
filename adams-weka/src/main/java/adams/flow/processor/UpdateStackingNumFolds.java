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
 * UpdateStackingNumFolds.java
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
import weka.classifiers.meta.Stacking;

import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 * Updates the number of cross-validation folds by Stacking to the supplied value.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class UpdateStackingNumFolds
  extends AbstractModifyingProcessor {

  private static final long serialVersionUID = 4995926193024269006L;

  /**
   * Sets the noUpdate flag of LWLSynchro classifiers.
   */
  public static class StackingObserver
    implements Observer {

    /** the number of updates. */
    protected int m_Updates;

    /** the number of folds. */
    protected int m_NumFolds;

    /**
     * Initializes the observer.
     *
     * @param numFolds	the number of folds to use
     */
    public StackingObserver(int numFolds) {
      m_NumFolds = numFolds;
    }

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
      if (child instanceof Stacking) {
	try {
	  if (((Stacking) child).getNumFolds() != m_NumFolds) {
	    ((Stacking) child).setNumFolds(m_NumFolds);
	    m_Updates++;
	  }
	}
	catch (Exception e) {
	  LoggingHelper.global().log(Level.SEVERE, "Failed to set number of folds for Stacking!", e);
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

  /** the number of folds to use. */
  protected int m_NumFolds;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the number of cross-validation folds by " + Utils.classToString(Stacking.class) + " to the supplied value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-folds", "numFolds",
      5, 2, null);
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	number of folds
   */
  public void setNumFolds(int value) {
    m_NumFolds = value;
    reset();
  }

  /**
   * Returns the number of folds to use.
   *
   * @return		number of folds
   */
  public int getNumFolds() {
    return m_NumFolds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFoldsTipText() {
    return "The number of folds to use in Stacking.";
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
	StackingObserver observer = new StackingObserver(m_NumFolds);
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
