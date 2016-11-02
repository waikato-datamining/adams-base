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
 * ContainerTableCellRenderer.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

/**
 * The cell renderer for displaying the containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <M> the type of container manager to use
 * @param <C> the type of container to use
 */
public class ContainerTableCellRenderer<M extends AbstractContainerManager, C extends AbstractContainer>
  extends DefaultTableCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = 5612216629317818452L;

  /**
   * Returns the default table cell renderer.
   *
   * @param table		the table this renderer belongs to
   * @param value		the object to render
   * @param isSelected	whether the object is selected
   * @param hasFocus		whether the object has the focus
   * @param row		the row in the table
   * @param column		the column in the table
   * @return			the component for rendering
   */
  public Component getTableCellRendererComponent(
      JTable table, Object value,
      boolean isSelected, boolean hasFocus,
      int row, int column) {

    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    M manager = ((ContainerModel<M,C>) table.getModel()).getManager();
    if (!manager.isUpdating() && (row < manager.count())) {
      C cont = null;
      if (manager.isFiltered())
        cont = (C) manager.getFiltered(row);
      if (cont == null)
        cont = (C) manager.get(row);
      if (cont instanceof ColorContainer)
        c.setForeground(((ColorContainer) cont).getColor());
    }

    return c;
  }
}