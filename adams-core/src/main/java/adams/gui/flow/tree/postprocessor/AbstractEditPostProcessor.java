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
 * AbstractEditPostProcessor.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.postprocessor;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.flow.core.Actor;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Ancestor for post-processors for edits in the tree.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEditPostProcessor
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -1710283049516306816L;

  /**
   * Checks whether this post processor scheme applies to the current situation.
   *
   * @param parent   the parent actor
   * @param oldActor the old actor
   * @param newActor the new, updated actor
   * @return true if this post processor applies to the situation
   */
  public abstract boolean applies(Actor parent, Actor oldActor, Actor newActor);

  /**
   * Post-processes the tree.
   *
   * @param tree     the tree to post-process
   * @param parent   the parent actor
   * @param oldActor the old actor
   * @param newActor the new, updated actor
   * @return true if tree got modified
   */
  protected abstract boolean doPostProcess(Tree tree, Actor parent, Actor oldActor, Actor newActor);

  /**
   * Post-processes the tree.
   *
   * @param tree     the tree to post-process
   * @param parent   the parent actor
   * @param oldActor the old actor
   * @param newActor the new, updated actor
   * @return true if tree got modified
   */
  public boolean postProcess(Tree tree, Actor parent, Actor oldActor, Actor newActor) {
    boolean result;
    final List<String> exp;
    final List<String> sel;

    exp = tree.getExpandedFullNames();
    sel = tree.getSelectionFullNames();

    result = doPostProcess(tree, parent, oldActor, newActor);

    if (result) {
      SwingUtilities.invokeLater(() -> {
	tree.setModified(true);
	tree.setExpandedFullNames(exp);
	tree.setSelectionFullNames(sel);
	tree.notifyActorChangeListeners(new ActorChangeEvent(tree, new Node[0], Type.MODIFY_BULK));
	tree.refreshTabs();
      });
    }

    return result;
  }

  /**
   * Returns a list with classes of post-processors.
   *
   * @return the post-processor classes
   */
  public static Class[] getPostProcessors() {
    return ClassLister.getSingleton().getClasses(AbstractEditPostProcessor.class);
  }

  /**
   * Applies all the post-processors, if applicable.
   *
   * @param tree     the tree that was modified
   * @param parent   the parent actor of the modified actor
   * @param oldActor the old actor
   * @param newActor the new, updated actor
   * @return true if tree got modified
   */
  public static boolean apply(Tree tree, Actor parent, Actor oldActor, Actor newActor) {
    boolean 			result;
    Class[] 			processors;
    AbstractEditPostProcessor 	proc;
    boolean 			confirmed;
    boolean 			modified;
    final List<String> 		exp;
    final List<String> 		sel;

    result = false;

    confirmed  = false;
    processors = getPostProcessors();
    exp        = tree.getExpandedFullNames();
    sel        = tree.getSelectionFullNames();
    for (Class processor : processors) {
      try {
	proc = (AbstractEditPostProcessor) processor.getDeclaredConstructor().newInstance();
	if (proc.applies(parent, oldActor, newActor)) {
	  if (!confirmed) {
	    int retVal = JOptionPane.showConfirmDialog(GUIHelper.getParentComponent(tree), "Propagate changes throughout the tree (if applicable)?");
	    if (retVal == JOptionPane.YES_OPTION) {
	      confirmed = true;
	      tree.addUndoPoint("Post-processing actor '" + newActor.getName() + "'");
	    }
	    else {
	      if (!tree.getIgnoreNameChangesUserPrompted()) {
		retVal = JOptionPane.showConfirmDialog(GUIHelper.getParentComponent(tree), "Do you want to ignore name changes for this flow?");
		if (retVal == JOptionPane.YES_OPTION)
		  tree.setIgnoreNameChanges(true);
		tree.setIgnoreNameChangesUserPrompted(true);
	      }
	      break;
	    }
	  }
	  modified = proc.postProcess(tree, parent, oldActor, newActor);
	  result = result || modified;
	}
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append("Error applying edit post-processor '" + processor.getName() + "':", e);
      }
    }
    SwingUtilities.invokeLater(() -> tree.setExpandedFullNames(exp));
    SwingUtilities.invokeLater(() -> tree.setSelectionFullNames(sel));

    return result;
  }
}
