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
 * DataContainerPanelWithContainerList.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container;

import adams.core.ClassLister;
import adams.core.logging.LoggingLevel;
import adams.data.container.DataContainer;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.ConsolePanel;
import adams.gui.visualization.container.datacontainerpanel.PopupCustomizerComparator;
import adams.gui.visualization.container.datacontainerpanel.containerlistpopup.AbstractContainerListPopupCustomizer;
import adams.gui.visualization.container.datacontainerpanel.plotpopup.AbstractPlotPopupCustomizer;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PopupMenuCustomizer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data container panel with a container list in the side panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class DataContainerPanelWithContainerList<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends DataContainerPanelWithSidePanel<T,M>
  implements ContainerListPopupMenuSupplier<M,C>, PopupMenuCustomizer {

  private static final long serialVersionUID = 6258304909047252486L;

  /** the container list. */
  protected AbstractContainerList<M, C> m_ContainerList;

  /** the customizers for the plot popup menu. */
  protected List<AbstractPlotPopupCustomizer> m_PlotCustomizers;

  /** the customizers for the container list popup menu. */
  protected List<AbstractContainerListPopupCustomizer> m_ContainerListCustomizers;

  /**
   * Initializes the panel without title.
   */
  public DataContainerPanelWithContainerList() {
    super();
  }

  /**
   * Initializes the panel with the given title.
   *
   * @param title	the title for the panel
   */
  public DataContainerPanelWithContainerList(String title) {
    super(title);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initialize() {
    Class[]			classes;
    PopupCustomizerComparator	comp;

    super.initialize();

    comp = new PopupCustomizerComparator();

    m_PlotCustomizers = new ArrayList<>();
    classes = ClassLister.getSingleton().getClasses(AbstractPlotPopupCustomizer.class);
    for (Class c: classes) {
      try {
	m_PlotCustomizers.add((AbstractPlotPopupCustomizer) c.newInstance());
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  LoggingLevel.SEVERE, "Failed to instantiate plot popup customizer: " + c.getName(), e);
      }
    }
    Collections.sort(m_PlotCustomizers, comp);

    m_ContainerListCustomizers = new ArrayList<>();
    classes = ClassLister.getSingleton().getClasses(AbstractContainerListPopupCustomizer.class);
    for (Class c: classes) {
      try {
	m_ContainerListCustomizers.add((AbstractContainerListPopupCustomizer) c.newInstance());
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  LoggingLevel.SEVERE, "Failed to instantiate container list popup customizer: " + c.getName(), e);
      }
    }
    Collections.sort(m_ContainerListCustomizers, comp);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ContainerList = createContainerList();

    m_SidePanel.setLayout(new BorderLayout(0, 0));
    m_SidePanel.add(m_ContainerList);
  }

  /**
   * Returns the container list.
   *
   * @return		the list
   */
  protected abstract AbstractContainerList<M, C> createContainerList();

  /**
   * Returns the panel listing the spectrums.
   *
   * @return		the panel
   */
  public AbstractContainerList<M, C> getContainerList() {
    return m_ContainerList;
  }

  /**
   * Returns the indices of the visible/hidden containers from the table
   * model (if container manager supports visibility).
   *
   * @param visible	whether to return the visible or hidden ones
   * @return		the indices
   */
  public int[] getTableModelIndices(boolean visible) {
    TIntList 			result;
    ContainerModel<M,C>		model;
    C 				cont;

    model  = m_ContainerList.getContainerModel();
    result = new TIntArrayList();

    if (getContainerManager() instanceof VisibilityContainerManager) {
      for (int i = 0; i < model.getRowCount(); i++) {
	cont = model.getContainerAt(i);
	if (((VisibilityContainer) cont).isVisible() && visible)
	  result.add(getContainerManager().indexOf(cont));
	else if (!((VisibilityContainer) cont).isVisible() && !visible)
	  result.add(getContainerManager().indexOf(cont));
      }
    }

    return result.toArray();
  }

  /**
   * Returns the visible containers from the table model (if container manager
   * supports visibility).
   *
   * @param visible	whether to return the visible or hidden ones
   * @return		the containers
   */
  public List<C> getTableModelContainers(boolean visible) {
    List<C> 	result;
    int[]	indices;

    result  = new ArrayList<>();
    indices = getTableModelIndices(visible);
    for (int index: indices)
      result.add((C) getContainerManager().get(index));

    return result;
  }

  /**
   * Returns the actual indices in the container manager of the selected
   * containers.
   *
   * @param row  	the current row (used if no rows selected in table)
   * @return		the actual indices as used by the container manager
   */
  public int[] getSelectedContainerIndices(ContainerTable<M, C> table, int row) {
    int[] 		result;

    if (table.getSelectedRows().length == 0)
      result = new int[]{row};
    else
      result = table.getSelectedRows();

    return result;
  }

  /**
   * Returns the actual indices in the container manager of the selected
   * containers.
   *
   * @param row  	the current row (used if no rows selected in table)
   * @return		the actual indices as used by the container manager
   */
  public int[] getActualSelectedContainerIndices(ContainerTable<M, C> table, int row) {
    int[] 			result;
    ContainerModel<M,C>		model;

    result = getSelectedContainerIndices(table, row);
    model  = m_ContainerList.getContainerModel();

    for (int i = 0; i < result.length; i++) {
      C cont = model.getContainerAt(result[i]);
      result[i] = getContainerManager().indexOf(cont);
    }

    return result;
  }

  /**
   * Returns a popup menu for the table of the spectrum list.
   *
   * @param table	the affected table
   * @param row	the row the mouse is currently over
   * @return		the popup menu
   */
  @Override
  public BasePopupMenu getContainerListPopupMenu(final ContainerTable<M,C> table, final int row) {
    BasePopupMenu	result;
    String		group;

    result = new BasePopupMenu();

    group = null;
    for (AbstractContainerListPopupCustomizer customizer: m_ContainerListCustomizers) {
      if (!customizer.handles(this))
	continue;
      if (group != null) {
	if (!group.equals(customizer.getGroup()))
	  result.addSeparator();
      }
      customizer.customize(this, table, row, result);
      group = customizer.getGroup();
    }

    return result;
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		the mous event
   * @param menu	the menu to customize
   */
  @Override
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    String		group;

    menu.addSeparator();

    group = null;
    for (AbstractPlotPopupCustomizer customizer: m_PlotCustomizers) {
      if (!customizer.handles(this))
	continue;
      if (group != null) {
	if (!group.equals(customizer.getGroup()))
	  menu.addSeparator();
      }
      customizer.customize(this, e, menu);
      group = customizer.getGroup();
    }
  }

  /**
   * Returns the paintlet used for painting the data.
   *
   * @return		the paintlet
   */
  public abstract Paintlet getDataPaintlet();

  /**
   * Sets the paintlet to use for painting the data.
   *
   * @param value	the paintlet
   */
  public abstract void setDataPaintlet(Paintlet value);

  /**
   * Hook method for processing the name of the scripting action.
   *
   * @param action	the action to process
   * @return		the (potentially) updated name
   */
  public String processAction(String action) {
    return action;
  }

  /**
   * Displays the notes for the given chromatograms.
   *
   * @param data	the chromatograms to display
   */
  public void showNotes(List<C> data) {
    NotesFactory.Dialog		dialog;

    if (getParentDialog() != null)
      dialog = NotesFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = NotesFactory.getDialog(getParentFrame(), false);
    dialog.setData(data);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Returns true if storing the color in the report of container's data object
   * is supported.
   *
   * @return		true if supported
   */
  public abstract boolean supportsStoreColorInReport();

  /**
   * Stores the color of the container in the report of container's
   * data object.
   *
   * @param indices	the indices of the containers of the container manager
   * @param name	the field name to use
   */
  public abstract void storeColorInReport(int[] indices, String name);

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_ContainerList != null)
      m_ContainerList.cleanUp();
  }
}
