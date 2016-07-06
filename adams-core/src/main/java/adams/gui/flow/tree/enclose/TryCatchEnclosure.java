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
 * TryCatchEnclosure.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.enclose;

import adams.flow.control.TryCatch;
import adams.flow.core.MutableActorHandler;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.TreeHelper;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

/**
 * Encloses the selected actors in a {@link TryCatch}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TryCatchEnclosure
  extends AbstractEncloseActor {

  private static final long serialVersionUID = -2613401104529816325L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encloses the selected actors in a " + TryCatch.class.getName() + ".";
  }

  /**
   * Checks whether this enclose suggestion is available.
   *
   * @param state	the current state
   * @return		true if enclosing is possible
   */
  @Override
  protected boolean canEnclose(StateContainer state) {
    return (state.numSel > 0) && (state.parent.getActor() instanceof MutableActorHandler);
  }

  /**
   * Returns a menu item that will perform the enclosing if selected.
   *
   * @param state	the current state
   * @return		the list of potential swaps
   */
  @Override
  public JMenuItem enclose(final StateContainer state) {
    JMenuItem		result;

    result = new JMenuItem(TryCatch.class.getSimpleName());
    result.addActionListener((ActionEvent e) -> {
      if (state.selPaths.length == 1)
	state.tree.addUndoPoint("Enclosing node '" + TreeHelper.pathToActor(state.selPaths[0]).getFullName() + "' in " + TryCatch.class.getName());
      else
	state.tree.addUndoPoint("Enclosing " + state.selPaths.length + " nodes in " + TryCatch.class.getName());

      MutableActorHandler parent = (MutableActorHandler) state.parent.getActor();
      Node currNode;
      TryCatch trycatch = new TryCatch();
      MutableActorHandler trybranch = (MutableActorHandler) trycatch.getTry();
      trybranch.removeAll();
      for (TreePath path: state.selPaths) {
	currNode = TreeHelper.pathToNode(path);
	trybranch.add(currNode.getFullActor());
      }
      Node newNode = TreeHelper.buildTree(null, trycatch, false);
      newNode.setOwner(state.tree);
      for (int i = 0; i < state.selPaths.length; i++) {
	currNode = TreeHelper.pathToNode(state.selPaths[i]);
	int index = state.parent.getIndex(currNode);
	state.parent.remove(index);
	if (i == 0)
	  state.parent.insert(newNode, index);
      }
      SwingUtilities.invokeLater(() -> {
	state.tree.updateActorName(newNode);
	state.tree.setModified(true);
	state.tree.nodeStructureChanged(state.parent);
	state.tree.expand(newNode);
      });
      SwingUtilities.invokeLater(() -> {
	state.tree.locateAndDisplay(newNode.getFullName());
	state.tree.notifyActorChangeListeners(new ActorChangeEvent(state.tree, state.parent, Type.MODIFY));
	state.tree.redraw();
      });
    });

    return result;
  }
}
