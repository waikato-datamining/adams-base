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
 * ContainerTable.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import adams.gui.core.BaseTable;

/**
 * A table for displaying the currently loaded containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <M> the type of container manager to use
 * @param <C> the type of container to use
 */
public class ContainerTable<M extends AbstractContainerManager, C extends AbstractContainer>
  extends BaseTable  {

  /** for serialization. */
  private static final long serialVersionUID = -7750679423202155069L;

  /** whether the column widths have been set already. */
  protected boolean m_ColumnWidthsSet;

  /** whether a manual resize occurred. */
  protected boolean m_ManuallyResized;
  
  /**
   * Initializes the table with an empty model.
   */
  public ContainerTable() {
    this(new ContainerModel((M) null));
  }

  /**
   * Initializes the table with the given model.
   *
   * @param model	the model to use
   */
  public ContainerTable(ContainerModel<M,C> model) {
    super(model);

    setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    setDefaultRenderer(String.class, new ContainerTableCellRenderer<M,C>());
    setDefaultRenderer(Integer.class, new ContainerTableCellRenderer<M,C>());
    setDefaultRenderer(Double.class, new ContainerTableCellRenderer<M,C>());

    getTableHeader().setReorderingAllowed(false);

    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_DELETE) {
	  removeContainers(getSelectedRows());
	  e.consume();
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    });
    
    getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      public void columnSelectionChanged(ListSelectionEvent e) {
      }
      public void columnRemoved(TableColumnModelEvent e) {
      }
      public void columnMoved(TableColumnModelEvent e) {
      }
      public void columnMarginChanged(ChangeEvent e) {
	m_ManuallyResized = true;
      }
      public void columnAdded(TableColumnModelEvent e) {
      }
    });
    
    resizeAndRepaint();
  }

  /**
   * Removes all containers from the table.
   */
  public void removeAllContainers() {
    removeContainers(null);
  }

  /**
   * Removes the containers from the table.
   *
   * @param indices	the indices in the table of the containers to remove;
   * 			use null to remove all
   */
  public void removeContainers(int[] indices) {
    M 		manager;
    int		i;

    if ((indices != null) && (indices.length == 0))
      return;

    manager = getManager();
    if (indices == null) {
      manager.clear();
    }
    else if (indices.length > 1) {
      manager.startUpdate();
      for (i = indices.length - 1; i >= 0; i--)
	manager.remove(indices[i]);
      manager.finishUpdate();
    }
    else {
      manager.remove(indices[0]);
    }
  }

  /**
   * Uses the given model.
   *
   * @param value	the model to use
   */
  public void setModel(TableModel value) {
    if (!(value instanceof ContainerModel))
      throw new IllegalArgumentException(
          "Only models of type " + ContainerModel.class.getName() + " can be used!");

    // deregister listener first
    if (getModel() instanceof ContainerModel)
      ((ContainerModel) getModel()).unregister();

    super.setModel(value);

    m_ColumnWidthsSet = false;
    m_ManuallyResized = false;
  }

  /**
   * Updates the column widths.
   */
  protected void updateColumnWidths() {
    Runnable	runnable;

    if (getModel() == null)
      return;
    if (getColumnCount() != getColumnModel().getColumnCount())
      return;
    if (getTableHeader() == null)
      return;

    runnable = new Runnable() {
      public void run() {
	ContainerModel<M,C> model = (ContainerModel<M,C>) getModel();
	TableColumnModel colModel = getColumnModel();
	synchronized(model) {
	  synchronized(colModel) {
	    for (int i = 0; i < colModel.getColumnCount(); i++)
	      colModel.getColumn(i).setPreferredWidth(model.getColumnWidth(i));
	  }
	}
      };
    };
    m_ColumnWidthsSet = true;
    SwingUtilities.invokeLater(runnable);
  }

  /**
   * Returns whether the column widths have been set.
   *
   * @return		true if column widths set
   */
  public boolean isColumnWidthsSet() {
    return m_ColumnWidthsSet;
  }

  /**
   * Returns whether the columns where manually resized.
   * 
   * @return		true if resized
   */
  public boolean isManuallyResized() {
    return m_ManuallyResized;
  }
  
  /**
   * Invalidates the column widths and layout.
   */
  public void invalidateColumnWidths() {
    m_ColumnWidthsSet = false;
    m_ManuallyResized = false;
    
    super.invalidate();
  }

  /**
   * Revalidates the table layout.
   */
  public void revalidate() {
    super.revalidate();

    if (!isColumnWidthsSet() && !isManuallyResized())
      updateColumnWidths();
  }

  /**
   * Returns the underlying manager.
   *
   * @return		the manager, null if none available
   */
  public M getManager() {
    if (getModel() != null)
      return ((ContainerModel<M,C>) getModel()).getManager();
    else
      return null;
  }

  /**
   * Invoked when this table's <code>TableModel</code> generates
   * a <code>TableModelEvent</code>.
   * The <code>TableModelEvent</code> should be constructed in the
   * coordinate system of the model; the appropriate mapping to the
   * view coordinate system is performed by this <code>JTable</code>
   * when it receives the event.
   * <p>
   * Application code will not use these methods explicitly, they
   * are used internally by <code>JTable</code>.
   * <p>
   * Note that as of 1.3, this method clears the selection, if any.
   *
   * @param e		the event
   */
  public void tableChanged(TableModelEvent e) {
    if (e.getColumn() == TableModelEvent.ALL_COLUMNS)
      m_ColumnWidthsSet = false;
 
    super.tableChanged(e);
  }
}