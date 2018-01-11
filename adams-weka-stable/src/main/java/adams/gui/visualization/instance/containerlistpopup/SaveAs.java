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
 * SaveAs.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instance.containerlistpopup;

import adams.data.instance.Instance;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.containerlistpopup.AbstractContainerListPopupCustomizer;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstancePanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

/**
 * Allows the saving of an instance container.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SaveAs
  extends AbstractContainerListPopupCustomizer<Instance, InstanceContainerManager, InstanceContainer>{

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
  public boolean handles(DataContainerPanelWithContainerList<Instance, InstanceContainerManager, InstanceContainer> panel) {
    return (panel instanceof InstancePanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param context	the context
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final Context<Instance,InstanceContainerManager,InstanceContainer> context, JPopupMenu menu) {
    JMenuItem		item;
    final int[]		indices;

    indices = context.actualSelectedContainerIndices;
    item    = new JMenuItem("Save as...");
    item.setEnabled(indices.length == 1);
    item.addActionListener((ActionEvent e) -> ((InstancePanel) context.panel).saveInstance(context.panel.getContainerManager().get(indices[0])));
    menu.add(item);
  }
}
