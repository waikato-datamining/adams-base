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
 * CallableActorRenamed.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.postprocessor;

import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorHandler;
import adams.flow.processor.UpdateCallableActorName;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;

import javax.swing.SwingUtilities;

/**
 <!-- globalinfo-start -->
 * Updates all references of the callable actor that was renamed.
 * <br><br>
 <!-- globalinfo-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableActorRenamed
  extends AbstractEditPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 4776606137570074080L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Updates all references of the callable actor that was renamed.";
  }

  /**
   * Checks whether this post processor scheme applies to the current situation.
   *
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if this post processor applies to the situation
   */
  @Override
  public boolean applies(AbstractActor parent, AbstractActor oldActor, AbstractActor newActor) {
    return (parent instanceof CallableActorHandler) && !oldActor.getName().equals(newActor.getName());
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
  @Override
  public boolean postProcess(Tree tree, AbstractActor parent, AbstractActor oldActor, AbstractActor newActor) {
    boolean			result;
    UpdateCallableActorName	updater;

    result = false;

    updater = new UpdateCallableActorName();
    updater.setOldName(oldActor.getName());
    updater.setNewName(newActor.getName());
    updater.process(tree.getActor());
    if (updater.isModified()) {
      result = true;
      SwingUtilities.invokeLater(() -> {
	  tree.setModified(true);
	  tree.setActor(updater.getModifiedActor());
      });
      SwingUtilities.invokeLater(() -> {
	tree.notifyActorChangeListeners(new ActorChangeEvent(tree, new Node[0], Type.MODIFY_BULK));
	tree.refreshTabs();
      });
    }

    return result;
  }
}
