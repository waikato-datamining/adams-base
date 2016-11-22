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

import adams.core.Range;
import adams.data.container.DataContainer;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.scripting.Invisible;
import adams.gui.scripting.Visible;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PaintletWithMarkers;
import adams.gui.visualization.core.PopupMenuCustomizer;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
  protected int[] getTableModelIndices(boolean visible) {
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
  protected List<C> getTableModelContainers(boolean visible) {
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
  protected int[] getSelectedContainerIndices(ContainerTable<M,C> table, int row) {
    int[] 			result;
    ContainerModel<M,C>		model;

    model  = m_ContainerList.getContainerModel();
    if (table.getSelectedRows().length == 0)
      result = new int[]{row};
    else
      result = table.getSelectedRows();

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
    JMenuItem 		item;
    final int[] 	indices;
    final List<C> 	visibleConts;
    boolean 		supportsVisibility;
    boolean		supportsColor;

    result       = new BasePopupMenu();
    indices      = getSelectedContainerIndices(table, row);
    visibleConts = getTableModelContainers(true);

    supportsVisibility = (getContainerManager() instanceof VisibilityContainerManager);
    supportsColor      = (getContainerManager() instanceof ColorContainerManager);

    if (supportsVisibility) {
      item = new JMenuItem("Toggle visibility");
      item.addActionListener((ActionEvent e) -> {
	TIntList visible = new TIntArrayList();
	TIntList invisible = new TIntArrayList();
	for (int index: indices) {
	  if (((VisibilityContainer) getContainerManager().get(index)).isVisible())
	    invisible.add(index);
	  else
	    visible.add(index);
	}
	Range range = new Range();
	range.setMax(getContainerManager().count());
	if (invisible.size() > 0) {
	  range.setIndices(invisible.toArray());
	  getScriptingEngine().add(
	    this,
	    processAction(Invisible.ACTION) + " " + range.getRange());
	}
	if (visible.size() > 0) {
	  range.setIndices(visible.toArray());
	  getScriptingEngine().add(
	    this,
	    processAction(Visible.ACTION) + " " + range.getRange());
	}
      });
      result.add(item);

      item = new JMenuItem("Show all");
      item.addActionListener((ActionEvent e) -> {
	int[] list = getTableModelIndices(false);
	if (list.length > 0) {
	  Range range = new Range();
	  range.setMax(getContainerManager().count());
	  range.setIndices(list);
	  getScriptingEngine().add(
	    this,
	    processAction(Visible.ACTION) + " " + range.getRange());
	}
      });
      result.add(item);

      item = new JMenuItem("Hide all");
      item.addActionListener((ActionEvent e) -> {
	int[] list = getTableModelIndices(true);
	if (list.length > 0) {
	  Range range = new Range();
	  range.setMax(getContainerManager().count());
	  range.setIndices(list);
	  getScriptingEngine().add(
	    this,
	    processAction(Invisible.ACTION) + " " + range.getRange());
	}
      });
      result.add(item);
    }

    if (supportsColor) {
      item = new JMenuItem("Choose color...");
      item.addActionListener((ActionEvent e) -> {
	String msg = "Choose color";
	C cont;
	Color color = Color.BLUE;
	if (indices.length == 1) {
	  cont = (C) getContainerManager().get(indices[0]);
	  if (cont instanceof NamedContainer)
	    msg += " for " + ((NamedContainer) cont).getID();
	  color = ((ColorContainer) cont).getColor();
	}
	Color c = JColorChooser.showDialog(
	  DataContainerPanelWithContainerList.this,
	  msg,
	  color);
	if (c == null)
	  return;
	for (int index : indices)
	  ((ColorContainer) getContainerManager().get(index)).setColor(c);
      });
      result.add(item);

      if (supportsStoreColorInReport()) {
	item = new JMenuItem("Store color in report...");
	item.addActionListener((ActionEvent e) -> {
	  String name = GUIHelper.showInputDialog(
	    DataContainerPanelWithContainerList.this, "Please enter the field name for storing the color");
	  if (name == null)
	    return;
	  storeColorInReport(indices, name);
	});
	result.add(item);
      }
    }

    if (getContainerManager().getAllowRemoval()) {
      result.addSeparator();

      item = new JMenuItem("Remove");
      item.addActionListener((ActionEvent e) -> m_ContainerList.getTable().removeContainers(indices));
      result.add(item);

      item = new JMenuItem("Remove all");
      item.addActionListener((ActionEvent e) -> m_ContainerList.getTable().removeAllContainers());
      result.add(item);
    }

    result.addSeparator();

    if (getContainerManager() instanceof NamedContainerManager) {
      item = new JMenuItem("Copy ID" + (indices.length > 1 ? "s" : ""));
      item.setEnabled(indices.length > 0);
      item.addActionListener((ActionEvent e) -> {
	StringBuilder id = new StringBuilder();
	for (int index: indices) {
	  if (id.length() > 0)
	    id.append("\n");
	  id.append(((NamedContainer) getContainerManager().get(index)).getDisplayID());
	}
	ClipboardHelper.copyToClipboard(id.toString());
      });
      result.add(item);
    }

    item = new JMenuItem("Notes");
    item.addActionListener((ActionEvent e) -> showNotes(visibleConts));
    result.add(item);

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
    JMenuItem			item;
    final List<C> 		visibleConts;
    PaintletWithMarkers		paintlet;

    visibleConts = getTableModelContainers(true);

    menu.addSeparator();

    if (getDataPaintlet() instanceof PaintletWithMarkers) {
      paintlet = (PaintletWithMarkers) getDataPaintlet();
      item = new JMenuItem();
      item.setIcon(GUIHelper.getEmptyIcon());
      if (!paintlet.isMarkersDisabled())
	item.setText("Disable markers");
      else
	item.setText("Enable markers");
      item.addActionListener((ActionEvent ae) -> {
	paintlet.setMarkersDisabled(!paintlet.isMarkersDisabled());
	repaint();
      });
      menu.add(item);
    }

    if (getDataPaintlet() instanceof AntiAliasingSupporter) {
      final AntiAliasingSupporter aapaintlet = (AntiAliasingSupporter) getDataPaintlet();
      item = new JMenuItem();
      item.setIcon(GUIHelper.getEmptyIcon());
      if (aapaintlet.isAntiAliasingEnabled())
	item.setText("Disable anti-aliasing");
      else
	item.setText("Enable anti-aliasing");
      item.addActionListener((ActionEvent ae) -> aapaintlet.setAntiAliasingEnabled(!aapaintlet.isAntiAliasingEnabled()));
      menu.add(item);
    }

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (isSidePanelVisible())
      item.setText("Hide side panel");
    else
      item.setText("Show side panel");
    item.addActionListener((ActionEvent ae) -> setSidePanelVisible(!isSidePanelVisible()));
    menu.add(item);

    item = new JMenuItem("Notes", GUIHelper.getEmptyIcon());
    item.addActionListener((ActionEvent ae) -> showNotes(visibleConts));
    menu.add(item);

    SendToActionUtils.addSendToSubmenu(this, menu);
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
  protected String processAction(String action) {
    return action;
  }

  /**
   * Displays the notes for the given chromatograms.
   *
   * @param data	the chromatograms to display
   */
  protected void showNotes(List<C> data) {
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
  protected abstract boolean supportsStoreColorInReport();

  /**
   * Stores the color of the container in the report of container's
   * data object.
   *
   * @param indices	the indices of the containers of the container manager
   * @param name	the field name to use
   */
  protected abstract void storeColorInReport(int[] indices, String name);

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
