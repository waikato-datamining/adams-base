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
 * Notes.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel.containerlistpopup;

import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * For displaying the notes of the selected containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Notes<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractContainerListPopupCustomizer<T,M,C> {

  private static final long serialVersionUID = 4973341996386365675L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Notes";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "view";
  }

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  @Override
  public boolean handles(DataContainerPanelWithContainerList<T, M, C> panel) {
    List<C> 	visibleConts;

    visibleConts = panel.getTableModelContainers(true);

    return ((visibleConts.size() > 0) && (visibleConts.get(0).getPayload() instanceof NotesHandler));
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param context	the context
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final Context<T,M,C> context, JPopupMenu menu) {
    JMenuItem		item;
    final List<C> 	visibleConts;

    visibleConts = context.visibleConts;
    item = new JMenuItem("Notes");
    item.addActionListener((ActionEvent e) -> context.panel.showNotes(visibleConts));
    menu.add(item);
  }
}
