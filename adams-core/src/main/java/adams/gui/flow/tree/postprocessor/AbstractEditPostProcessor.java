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
 * AbstractEditPostProcessor.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.postprocessor;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.flow.core.AbstractActor;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.Tree;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import java.util.List;

/**
 * Ancestor for post-processors for edits in the tree.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEditPostProcessor
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -1710283049516306816L;

  /**
   * Checks whether this post processor scheme applies to the current situation.
   * 
   * @param parent	the parent actor
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if this post processor applies to the situation
   */
  public abstract boolean applies(AbstractActor parent, AbstractActor oldActor, AbstractActor newActor);

  /**
   * Backs up the expanded state of the tree.
   * 
   * @param tree	the tree to back up
   */
  protected List<TreePath> backupExpandedState(Tree tree) {
    return tree.getExpandedNodes();
  }
  
  /**
   * Restores the expanded state of the tree.
   * 
   * @param tree	the tree to restore
   * @param expanded	the list of expanded nodes
   * @return		true if successfully restored
   */
  protected boolean restoreExpandedState(Tree tree, List<TreePath> expanded) {
    return tree.restoreExpandedNodes(expanded);
  }
  
  /**
   * Post-processes the tree.
   * 
   * @param tree	the tree to post-process
   * @param parent	the parent actor
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if tree got modified
   */
  public abstract boolean postProcess(Tree tree, AbstractActor parent, AbstractActor oldActor, AbstractActor newActor);

  /**
   * Returns a list with classnames of post-processors.
   *
   * @return		the post-processor classnames
   */
  public static String[] getPostProcessors() {
    return ClassLister.getSingleton().getClassnames(AbstractEditPostProcessor.class);
  }
  
  /**
   * Applies all the post-processors, if applicable.
   * 
   * @param tree	the tree that was modified
   * @param parent	the parent actor of the modified actor
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if tree got modified
   */
  public static boolean apply(Tree tree, AbstractActor parent, AbstractActor oldActor, AbstractActor newActor) {
    boolean			result;
    String[]			processors;
    AbstractEditPostProcessor	proc;
    boolean			confirmed;
    boolean			modified;
    List<TreePath>		exp;
    
    result = false;
    
    confirmed = false;
    processors = getPostProcessors();
    for (String processor: processors) {
      try {
	proc = (AbstractEditPostProcessor) Class.forName(processor).newInstance();
	if (proc.applies(parent, oldActor, newActor)) {
	  if (!confirmed) {
	    if (JOptionPane.showConfirmDialog(GUIHelper.getParentComponent(tree), "Propagate changes throughout the tree (if applicable)?") == JOptionPane.YES_OPTION)
	      confirmed = true;
	    else
	      break;
	  }
	  exp      = proc.backupExpandedState(tree);
	  modified = proc.postProcess(tree, parent, oldActor, newActor);
	  if (modified)
	    proc.restoreExpandedState(tree, exp);
	  exp    = null;
	  result = result | modified;
	}
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Error applying edit post-processor '" + processor + "':\n" + Utils.throwableToString(e));
      }
    }

    return result;
  }
}
