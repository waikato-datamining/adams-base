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
 * AbstractContainerListPopupCustomizer.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel.containerlistpopup;

import adams.data.container.DataContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.AbstractPopupCustomizer;

import javax.swing.JPopupMenu;
import java.util.List;

/**
 * Ancestor for actions for the container list popup menu.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractContainerListPopupCustomizer<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractPopupCustomizer<T,M,C> {

  private static final long serialVersionUID = 6847657392180231208L;

  /**
   * Container for the context.
   *
   * @param <T> the data container
   * @param <M> the container manager
   * @param <C> the container
   */
  public static class Context<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer> {
    public DataContainerPanelWithContainerList<T,M,C> panel;
    public ContainerTable<M,C> table;
    public int row;
    public int[] actualSelectedContainerIndices;
    public List<C>  visibleConts;
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param context	the context
   * @param menu	the popup menu to customize
   */
  public abstract void customize(final Context<T,M,C> context, JPopupMenu menu);

  /**
   * Generates the context container.
   *
   * @param panel	the panel
   * @param table	the table
   * @param row		the row
   * @return		the context
   */
  public Context<T,M,C> createContext(final DataContainerPanelWithContainerList<T,M,C> panel, final ContainerTable<M,C> table, final int row) {
    Context<T,M,C>	result;

    result = new Context<>();
    result.panel = panel;
    result.table = table;
    result.row   = row;
    result.actualSelectedContainerIndices = panel.getActualSelectedContainerIndices(table, row);
    result.visibleConts = panel.getTableModelContainers(true);
    return result;
  }
}
