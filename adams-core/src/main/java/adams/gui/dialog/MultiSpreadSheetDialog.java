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
 * MultiSpreadSheetDialog.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.MultiPagePane;
import adams.gui.core.UISettingsSupporter;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.core.spreadsheettable.DefaultCellRenderingCustomizer;
import adams.gui.visualization.core.PopupMenuCustomizer;

import javax.swing.event.ChangeEvent;
import java.awt.Dialog;
import java.awt.Frame;

/**
 * Dialog for displaying multiple spreadsheets in a multi-page pane.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiSpreadSheetDialog
  extends ApprovalDialog
  implements UISettingsSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7604505322768892726L;

  /** the tabbed pane for the spreadsheets. */
  protected MultiPagePane m_MultiPagePane;

  /** the customizer for the table cells popup menu. */
  protected PopupMenuCustomizer m_CellPopupMenuCustomizer;

  /** for customizing the cell rendering. */
  protected CellRenderingCustomizer m_CellRenderingCustomizer;

  /** whether to show formulas rather than the result. */
  protected boolean m_ShowFormulas;

  /** the number of decimals to display. */
  protected int m_NumDecimals;

  /** whether to show the search box. */
  protected boolean m_ShowSearch;

  /** whether to show the combobox with column names. */
  protected boolean m_ShowColumnComboBox;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public MultiSpreadSheetDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public MultiSpreadSheetDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public MultiSpreadSheetDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public MultiSpreadSheetDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public MultiSpreadSheetDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public MultiSpreadSheetDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public MultiSpreadSheetDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public MultiSpreadSheetDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CellPopupMenuCustomizer = null;
    m_CellRenderingCustomizer = new DefaultCellRenderingCustomizer();
    m_ShowFormulas            = false;
    m_NumDecimals             = -1;
    m_ShowSearch              = false;
    m_ShowColumnComboBox      = false;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setApproveVisible(true);
    setCancelVisible(false);
    setDiscardVisible(false);

    m_MultiPagePane = new MultiPagePane();
    m_MultiPagePane.setReadOnly(true);
    m_MultiPagePane.addChangeListener((ChangeEvent e) -> {
      if (m_MultiPagePane.getSelectedIndex() > -1)
	setJMenuBar(getSelectedPanel().getMenuBar());
      else
	setJMenuBar(null);
    });
    getContentPane().add(m_MultiPagePane);

    setSize(400, 600);
  }

  /**
   * Sets the parameters for storing the divider location.
   *
   * @param cls		the class
   * @param property	the property
   */
  @Override
  public void setUISettingsParameters(Class cls, String property) {
    m_MultiPagePane.setUISettingsParameters(cls, property);
  }

  /**
   * Clears the para meters for storing the divider location.
   */
  @Override
  public void clearUISettingsParameters() {
    m_MultiPagePane.clearUISettingsParameters();
  }

  /**
   * Returns the number of spreadsheets currently display.
   *
   * @return		the number of spreadsheets
   */
  public int getPanelCount() {
    return m_MultiPagePane.getPageCount();
  }

  /**
   * Returns the spreadsheet panel at the specified location.
   *
   * @param index	the tab index (0-based)
   * @return		the panel
   */
  public SpreadSheetPanel getPanelAt(int index) {
    return (SpreadSheetPanel) m_MultiPagePane.getPageAt(index);
  }

  /**
   * Returns the selected spreadsheet panel.
   *
   * @return		the panel, null if none selected
   */
  public SpreadSheetPanel getSelectedPanel() {
    return (SpreadSheetPanel) m_MultiPagePane.getSelectedPage();
  }

  /**
   * Adds the spreadsheet as tab.
   *
   * @param value	the sheet to add
   */
  public void addSpreadSheet(SpreadSheet value) {
    String		title;
    SpreadSheetPanel	panel;

    title = "" + (m_MultiPagePane.getPageCount() + 1);
    if (value.getName() != null)
      title = value.getName();
    panel = new SpreadSheetPanel();
    panel.setSpreadSheet(value);
    panel.setCellPopupMenuCustomizer(m_CellPopupMenuCustomizer);
    panel.setCellRenderingCustomizer(m_CellRenderingCustomizer);
    panel.setShowFormulas(m_ShowFormulas);
    panel.setShowSearch(m_ShowSearch);
    panel.setShowColumnComboBox(m_ShowColumnComboBox);
    m_MultiPagePane.addPage(title, panel);
  }

  /**
   * Sets the spreadsheets to display.
   *
   * @param value	the spreadsheets to display
   */
  public void setSpreadSheets(SpreadSheet[] value) {
    m_MultiPagePane.removeAllPages();
    for (SpreadSheet sheet: value)
      addSpreadSheet(sheet);
    if (m_MultiPagePane.getPageCount() > 0)
      m_MultiPagePane.setSelectedIndex(0);
  }
  
  /**
   * Returns the spreadsheets currently displayed.
   * 
   * @return		the spreadsheets on display
   */
  public SpreadSheet[] getSpreadSheets() {
    SpreadSheet[]	result;
    int			i;

    result = new SpreadSheet[getPanelCount()];
    for (i = 0; i < getPanelCount(); i++)
      result[i] = getPanelAt(i).getSpreadSheet();

    return result;
  }

  /**
   * Sets the popup menu customizer to use.
   *
   * @param value	the customizer, null to remove it
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    int		i;

    m_CellPopupMenuCustomizer = value;
    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).setCellPopupMenuCustomizer(value);
  }

  /**
   * Returns the current popup menu customizer.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_CellPopupMenuCustomizer;
  }

  /**
   * Sets the renderer.
   *
   * @param value	the renderer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    int		i;

    m_CellRenderingCustomizer = value;
    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).setCellRenderingCustomizer(value);
  }

  /**
   * Returns the renderer.
   *
   * @return		the renderer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return m_CellRenderingCustomizer;
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    int		i;

    m_ShowFormulas = value;
    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).setShowFormulas(value);
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return m_ShowFormulas;
  }

  /**
   * Sets the number of decimals to display. Use -1 to display all.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    int		i;

    m_NumDecimals = value;
    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).setNumDecimals(value);
  }

  /**
   * Returns the currently set number of decimals. -1 if displaying all.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }
  
  /**
   * Sets whether the search is visible.
   * 
   * @param value	true if to show search
   */
  public void setShowSearch(boolean value) {
    int		i;

    m_ShowSearch = value;
    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).setShowSearch(value);
  }
  
  /**
   * Returns whether the search is visible.
   * 
   * @return 		true if search is shown
   */
  public boolean getShowSearch() {
    return m_ShowSearch;
  }

  /**
   * Sets whether the column combobox is visible.
   *
   * @param value	true if to show column combobox
   */
  public void setShowColumnComboBox(boolean value) {
    int		i;

    m_ShowColumnComboBox = value;
    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).setShowColumnComboBox(value);
  }

  /**
   * Returns whether the column combobox is visible.
   *
   * @return 		true if column combobox is shown
   */
  public boolean getShowColumnComboBox() {
    return m_ShowColumnComboBox;
  }
}
