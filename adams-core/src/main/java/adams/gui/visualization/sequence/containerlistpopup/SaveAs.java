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
 * SaveAs.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence.containerlistpopup;

import adams.data.sequence.XYSequence;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.containerlistpopup.AbstractContainerListPopupCustomizer;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

/**
 * Allows the saving of a sequence container.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SaveAs
  extends AbstractContainerListPopupCustomizer<XYSequence, XYSequenceContainerManager, XYSequenceContainer>{

  private static final long serialVersionUID = -7796583803269239174L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Save as...";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "export";
  }

   /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
 @Override
  public boolean handles(DataContainerPanelWithContainerList<XYSequence, XYSequenceContainerManager, XYSequenceContainer> panel) {
    return (panel instanceof XYSequencePanel);
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
  public void customize(DataContainerPanelWithContainerList<XYSequence, XYSequenceContainerManager, XYSequenceContainer> panel, ContainerTable<XYSequenceContainerManager, XYSequenceContainer> table, int row, JPopupMenu menu) {
    JMenuItem		item;
    final int[]		indices;

    indices = panel.getSelectedContainerIndices(table, row);
    item    = new JMenuItem("Save as...");
    item.setEnabled(indices.length == 1);
    item.addActionListener((ActionEvent e) -> ((XYSequencePanel) panel).saveSequence(panel.getContainerManager().get(indices[0])));
    menu.add(item);
  }
}
