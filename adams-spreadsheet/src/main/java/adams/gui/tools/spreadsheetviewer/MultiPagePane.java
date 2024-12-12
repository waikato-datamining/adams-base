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
 * MultiPagePane.java
 * Copyright (C) 2009-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.core.spreadsheettable.DefaultCellRenderingCustomizer;
import adams.gui.tools.SpreadSheetViewerPanel;

import javax.swing.event.ChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A specialized tabbed pane with a few methods for easier access.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiPagePane
  extends adams.gui.core.MultiPagePane {

  /** for serialization. */
  private static final long serialVersionUID = -2048229771213837710L;

  /** prefix for new titles. */
  public final static String PREFIX_TITLE = "new";

  /** prefix for modified titles. */
  public final static String PREFIX_MODIFIED = "*";

  /** the owning viewer. */
  protected SpreadSheetViewerPanel m_Owner;
  
  /** the number of decimals to display. */
  protected int m_NumDecimals;

  /** the custom background color for negative values (null if none set). */
  protected CellRenderingCustomizer m_CellRenderingCustomizer;

  /**
   * Initializes the tabbed pane.
   * 
   * @param owner	the owning viewer
   */
  public MultiPagePane(SpreadSheetViewerPanel owner) {
    super();
    setOwner(owner);

    addChangeListener((ChangeEvent e) -> pageSelected(e));
    setMaxPageCloseUndo(10);
    setPageCloseApprover((adams.gui.core.MultiPagePane source, int index) -> {
      SpreadSheetPanel panel = getPanelAt(index);
      boolean result = checkForModified(panel);
      // to avoid second popup from checkModified() in removeTab method
      if (result && panel.isModified()) {
	panel.setModified(false);
	updatePage(index);
      }
      return result;
    });
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_NumDecimals             = -1;
    m_CellRenderingCustomizer = new DefaultCellRenderingCustomizer();
  }
  
  /**
   * Sets the owning viewer.
   * 
   * @param value	the owner
   */
  public void setOwner(SpreadSheetViewerPanel value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owning viewer.
   * 
   * @return		the owner, null if none set
   */
  public SpreadSheetViewerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the number of panels in the tabbed pane.
   *
   * @return		the number of panels
   */
  public int getPanelCount() {
    return getPageCount();
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public SpreadSheetPanel[] getAllPanels() {
    SpreadSheetPanel[]	result;
    int			i;
    
    result = new SpreadSheetPanel[getPageCount()];
    for (i = 0; i < getPageCount(); i++)
      result[i] = getPanelAt(i);
    
    return result;
  }

  /**
   * Returns the panel at the specified position.
   *
   * @param index	the page index of the table
   * @return		the panel, null if not available or invalid index
   */
  public SpreadSheetPanel getPanelAt(int index) {
    SpreadSheetPanel	result;

    if ((index < 0) || (index >= getPageCount()))
      return null;
    
    result = (SpreadSheetPanel) getPageAt(index);

    return result;
  }
  
  /**
   * Returns the table at the specified position.
   *
   * @param index	the page index of the table
   * @return		the table, null if not available or invalid index
   */
  public SpreadSheetTable getTableAt(int index) {
    SpreadSheetTable	result;
    SpreadSheetPanel	panel;

    result = null;
    panel  = getPanelAt(index);
    if (panel != null)
      result = panel.getTable();

    return result;
  }
  
  /**
   * Returns the table at the specified position.
   *
   * @param index	the page index of the table
   * @return		the table
   */
  public SpreadSheet getSheetAt(int index) {
    SpreadSheet		result;
    SpreadSheetTable	table;
    
    result = null;
    table  = getTableAt(index);
    if (table == null)
      return result;

    result = table.toSpreadSheet();

    return result;
  }

  /**
   * Sets the number of decimals to use.
   *
   * @param index	the page index
   * @param numDec	the number of decimals to use
   */
  public void setNumDecimalsAt(int index, int numDec) {
    getTableAt(index).setNumDecimals(numDec);
  }

  /**
   * returns the number of decimals in use.
   *
   * @param index	the page index
   * @return		the number of decimals in use
   */
  public int getNumDecimalsAt(int index) {
    return getTableAt(index).getNumDecimals();
  }

  /**
   * Sets the number of decimals to use for all tables.
   *
   * @param numDec	the number of decimals to use
   */
  public void setNumDecimals(int numDec) {
    int	i;

    m_NumDecimals = numDec;
    for (i = 0; i < getPageCount(); i++)
      setNumDecimalsAt(i, numDec);
  }
  
  /**
   * Returns the currently set number of decimals.
   * 
   * @return		the number of decimals to use
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Sets the cell rendering customizer at the index.
   *
   * @param index	the page index
   * @param cust	the customizer
   */
  public void setCellRenderingCustomizerAt(int index, CellRenderingCustomizer cust) {
    getTableAt(index).setCellRenderingCustomizer(cust);
  }

  /**
   * Returns the cell rendering customizer at the index.
   *
   * @param index	the page index
   * @return		the customizer
   */
  public CellRenderingCustomizer getCellRenderingCustomizerAt(int index) {
    return getTableAt(index).getCellRenderingCustomizer();
  }

  /**
   * Sets the cell rendering customizer to use.
   *
   * @param cust	the customizer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer cust) {
    int	i;

    m_CellRenderingCustomizer = cust;
    for (i = 0; i < getPageCount(); i++)
      setCellRenderingCustomizerAt(i, cust);
  }

  /**
   * Returns the current cell rendering customizer.
   *
   * @return		the customizer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return m_CellRenderingCustomizer;
  }

  /**
   * Sets the optimal column widths for all pages.
   */
  public void setOptimalColumnWidth() {
    int	i;

    for (i = 0; i < getPageCount(); i++)
      setOptimalColumnWidthAt(i);
  }

  /**
   * Sets the optimal column widths for the specified pane.
   *
   * @param index	the page index
   */
  public void setOptimalColumnWidthAt(int index) {
    getPanelAt(index).getTable().setOptimalColumnWidth();
  }

  /**
   * Sets the column widths for all pages.
   */
  public void setColumnWidths(int width) {
    int	i;

    for (i = 0; i < getPageCount(); i++)
      setColumnWidthsAt(i, width);
  }

  /**
   * Sets the column width for the specified pane.
   *
   * @param index	the page index
   * @param width 	the width to use
   */
  public void setColumnWidthsAt(int index, int width) {
    getPanelAt(index).getTable().setColumnWidths(width);
  }

  /**
   * Sets whether to show the formulas.
   *
   * @param index	the page index
   * @param value	whether to show the formulas
   */
  public void setShowFormulasAt(int index, boolean value) {
    getTableAt(index).setShowFormulas(value);
  }

  /**
   * Returns whether to show the formulas.
   *
   * @param index	the page index
   * @return		whether to show the formulas
   */
  public boolean getShowFormulas(int index) {
    return getTableAt(index).getShowFormulas();
  }

  /**
   * Sets whether to show the formulas.
   *
   * @param value	whether to show the formulas
   */
  public void setShowFormulas(boolean value) {
    int	i;

    for (i = 0; i < getPageCount(); i++)
      setShowFormulasAt(i, value);
  }

  /**
   * Sets whether to show the cell types rather than values.
   *
   * @param index	the page index
   * @param value	whether to show the cell types
   */
  public void setShowCellTypesAt(int index, boolean value) {
    getTableAt(index).setShowCellTypes(value);
  }

  /**
   * Returns whether to show the cell types.
   *
   * @param index	the page index
   * @return		whether to show the cell types
   */
  public boolean getShowCellTypes(int index) {
    return getTableAt(index).getShowCellTypes();
  }

  /**
   * Sets whether to show the cell types.
   *
   * @param value	whether to show the cell types
   */
  public void setShowCellTypes(boolean value) {
    int	i;

    for (i = 0; i < getPageCount(); i++)
      setShowCellTypesAt(i, value);
  }

  /**
   * Sets the readonly state.
   *
   * @param index	the page index
   * @param value	whether to show the formulas
   */
  public void setReadOnlyAt(int index, boolean value) {
    getTableAt(index).setReadOnly(value);
  }

  /**
   * Returns whether to show the formulas.
   *
   * @param index	the page index
   * @return		whether to show the formulas
   */
  public boolean getReadOnlyAt(int index) {
    return getTableAt(index).isReadOnly();
  }

  /**
   * Sets the readonly state of all tabs.
   *
   * @param value	true if readonly
   */
  public void setReadOnly(boolean value) {
    int	i;

    for (i = 0; i < getPageCount(); i++)
      setReadOnlyAt(i, value);
  }

  /**
   * Sets the modified state.
   *
   * @param index	the page index
   * @param value	true if modified
   */
  public void setModifiedAt(int index, boolean value) {
    getTableAt(index).setModified(value);
  }

  /**
   * Returns the modified state.
   *
   * @param index	the page index
   * @return		true if modified
   */
  public boolean isModifiedAt(int index) {
    return getTableAt(index).isModified();
  }

  /**
   * Sets the modified state of all tab.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    int	i;

    for (i = 0; i < getPageCount(); i++)
      setModifiedAt(i, value);
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the sheet or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    if (m_Owner == null)
      return true;
    return m_Owner.checkForModified();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the sheet or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified(SpreadSheetPanel panel) {
    if (m_Owner == null)
      return true;
    return m_Owner.checkForModified(panel);
  }

  /**
   * Returns the currently selected panel.
   *
   * @return		the current panel, null if not available
   */
  public SpreadSheetPanel getCurrentPanel() {
    return getPanelAt(getSelectedIndex());
  }

  /**
   * Returns the currently selected table.
   * 
   * @return		the table, null if none available
   */
  public SpreadSheetTable getCurrentTable() {
    return getTableAt(getSelectedIndex());
  }

  /**
   * Returns the currently selected sheet.
   * 
   * @return		the sheet, null if none available
   */
  public SpreadSheet getCurrentSheet() {
    return getSheetAt(getSelectedIndex());
  }
  
  /**
   * Adds the sheet.
   * 
   * @param file	the file this sheet is from
   * @param sheet	the sheet to add
   * @return		the created panel
   */
  public SpreadSheetPanel addPage(File file, SpreadSheet sheet) {
    SpreadSheetPanel	result;

    result = addPage(createPageTitle(file, sheet), sheet);
    result.setFilename(file);

    return result;
  }
  
  /**
   * Adds the sheet.
   * 
   * @param title	the title for the tab
   * @param sheet	the sheet to add
   * @return		the created panel
   */
  public SpreadSheetPanel addPage(String title, SpreadSheet sheet) {
    SpreadSheetPanel	result;
    
    result = new SpreadSheetPanel(this);
    result.setNumDecimals(m_NumDecimals);
    result.setCellRenderingCustomizer(m_CellRenderingCustomizer);
    result.setSheet(sheet);
    result.setReadOnly(false);
    addPage(title, result);
    setSelectedIndex(getPageCount() - 1);
    
    return result;
  }

  /**
   * Creates a page title.
   * 
   * @param file	the file name to use
   * @param sheet	the sheet loaded from the file
   * @return		the generated title
   */
  public String createPageTitle(File file, SpreadSheet sheet) {
    return file.getName() + (sheet.getName() != null ? "/" + sheet.getName() : "");
  }
  
  /**
   * Returns all the page titles.
   * 
   * @return		the titles
   */
  public List<String> getPageTitles() {
    ArrayList<String>	result;
    int			i;
    
    result = new ArrayList<>();
    
    for (i = 0; i < getPageCount(); i++)
      result.add(getTitleAt(i));
    
    return result;
  }
  
  /**
   * Returns a new title (does not reserve it).
   */
  public String newTitle() {
    String		result;
    int			i;
    HashSet<String>	titles;
    
    titles = new HashSet<>(getPageTitles());
    i      = 0;
    do {
      i++;
      result = PREFIX_TITLE + i;
    }
    while (titles.contains(result));
    
    return result;
  }

  /**
   * Gets called when a page gets selected.
   * 
   * @param e		the event that triggered the action
   */
  protected void pageSelected(ChangeEvent e) {
    // actor tabs
    if ((getPanelCount() == 0) || (getSelectedIndex() == -1))
      m_Owner.getViewerTabs().notifyTabs(null);
    else
      m_Owner.getViewerTabs().notifyTabs(m_Owner.getCurrentPanel());

    m_Owner.updateMenu();
    m_Owner.updateActions();
  }

  /**
   * Removes the page at <code>index</code>.
   * After the component associated with <code>index</code> is removed,
   * its visibility is reset to true to ensure it will be visible
   * if added to other containers.
   *
   * @param index the index of the page to be removed
   */
  @Override
  public PageContainer removePageAt(int index) {
    SpreadSheetPanel panel;

    if (index < 0)
      return null;
    if (!checkForModified(getPanelAt(index)))
      return null;

    panel = getPanelAt(index);
    panel.cleanUp();

    return super.removePageAt(index);
  }

  /**
   * Updates the title of the currently selected page, taking the modified
   * state into account.
   */
  public void updateCurrentPage() {
    updatePage(getSelectedIndex());
  }

  /**
   * Updates the page title at the specified index, taking the modified
   * state into account.
   *
   * @param index	the index of the tab
   */
  public void updatePage(int index) {
    String	title;

    if (getPanelAt(index) == null)
      return;

    title = getTitleAt(index);
    if (isModifiedAt(index)) {
      if (!title.startsWith(PREFIX_MODIFIED))
	title = PREFIX_MODIFIED + title;
    }
    else {
      if (title.startsWith(PREFIX_MODIFIED))
	title = title.substring(PREFIX_MODIFIED.length());
    }
    setTitleAt(index, title);
  }
}