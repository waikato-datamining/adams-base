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
 * ArrayDisplayEnclosure.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.enclose;

import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.sink.ArrayDisplay;
import adams.flow.sink.DisplayPanelProvider;
import adams.gui.core.GUIHelper;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.TreeHelper;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Encloses the {@link DisplayPanelProvider} in a {@link ArrayDisplay}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayDisplayEnclosure
    extends AbstractEncloseActor {

  private static final long serialVersionUID = -2613401104529816325L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encloses the selected " + DisplayPanelProvider.class.getName() + " actor in a " + ArrayDisplay.class.getName() + ".";
  }

  /**
   * Checks whether this enclose suggestion is available.
   *
   * @param state	the current state
   * @return		true if enclosing is possible
   */
  @Override
  protected boolean canEnclose(StateContainer state) {
    return (state.isSingleSel && (state.selNode.getActor() instanceof DisplayPanelProvider));
  }

  /**
   * Encloses the specified actor in a DisplayPanelManager actor.
   *
   * @param state	the current state
   */
  protected void encloseInArrayDisplay(StateContainer state) {
    Actor 		currActor;
    Node 		currNode;
    ArrayDisplay 	arrayDisplay;
    AbstractDisplay 	display;

    currNode     = TreeHelper.pathToNode(state.selPaths[0]);
    currActor    = currNode.getFullActor().shallowCopy();
    arrayDisplay = new ArrayDisplay();
    arrayDisplay.setName(currActor.getName());
    arrayDisplay.setPanelProvider((DisplayPanelProvider) currActor);
    if (currActor instanceof AbstractDisplay) {
      display = (AbstractDisplay) currActor;
      arrayDisplay.setWidth(display.getWidth());
      arrayDisplay.setHeight(display.getHeight());
      arrayDisplay.setX(display.getX());
      arrayDisplay.setY(display.getY());
    }

    state.tree.addUndoPoint("Enclosing node '" + currNode.getActor().getFullName() + "' in " + arrayDisplay.getClass().getName());

    SwingUtilities.invokeLater(() -> {
      List<String> exp = state.tree.getExpandedFullNames();
      currNode.setActor(arrayDisplay);
      state.tree.setModified(true);
      state.tree.nodeStructureChanged((Node) currNode.getParent());
      state.tree.notifyActorChangeListeners(new ActorChangeEvent(state.tree, currNode, Type.MODIFY));
      state.tree.setExpandedFullNames(exp);
      state.tree.expand(currNode);
      state.tree.locateAndDisplay(currNode.getFullName(), true);
    });
  }

  /**
   * Returns a menu item that will perform the enclosing if selected.
   *
   * @param state	the current state
   * @return		the list of potential swaps
   */
  @Override
  public JMenuItem enclose(final StateContainer state) {
    JMenuItem 	result;

    result = new JMenuItem(ArrayDisplay.class.getSimpleName());
    result.setIcon(GUIHelper.getIcon(ArrayDisplay.class));
    result.addActionListener((ActionEvent e) -> encloseInArrayDisplay(state));

    return result;
  }
}
