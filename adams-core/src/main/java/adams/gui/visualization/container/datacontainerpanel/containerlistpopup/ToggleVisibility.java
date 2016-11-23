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
 * ToggleVisibility.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel.containerlistpopup;

import adams.core.Range;
import adams.data.container.DataContainer;
import adams.gui.scripting.Invisible;
import adams.gui.scripting.Visible;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.VisibilityContainer;
import adams.gui.visualization.container.VisibilityContainerManager;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

/**
 * For toggling the visibility.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ToggleVisibility<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractContainerListPopupCustomizer<T,M,C> {

  private static final long serialVersionUID = 4973341996386365675L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "0-Visibility-0-Toggle visibility";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "0-graphics";
  }

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  @Override
  public boolean handles(DataContainerPanelWithContainerList<T, M, C> panel) {
    return (panel.getContainerManager() instanceof VisibilityContainerManager);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param table	the affected table
   * @param row		the row the mouse is currently over
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final DataContainerPanelWithContainerList<T,M,C> panel, final ContainerTable<M,C> table, final int row, JPopupMenu menu) {
    JMenuItem	item;
    final int[] 	indices;

    indices = panel.getSelectedContainerIndices(table, row);
    item    = new JMenuItem("Toggle visibility");
    item.addActionListener((ActionEvent e) -> {
      TIntList visible = new TIntArrayList();
      TIntList invisible = new TIntArrayList();
      for (int index: indices) {
	if (((VisibilityContainer) panel.getContainerManager().get(index)).isVisible())
	  invisible.add(index);
	else
	  visible.add(index);
      }
      Range range = new Range();
      range.setMax(panel.getContainerManager().count());
      if (invisible.size() > 0) {
	range.setIndices(invisible.toArray());
	panel.getScriptingEngine().add(
	  panel,
	  panel.processAction(Invisible.ACTION) + " " + range.getRange());
      }
      if (visible.size() > 0) {
	range.setIndices(visible.toArray());
	panel.getScriptingEngine().add(
	  panel,
	  panel.processAction(Visible.ACTION) + " " + range.getRange());
      }
    });
    menu.add(item);
  }
}
