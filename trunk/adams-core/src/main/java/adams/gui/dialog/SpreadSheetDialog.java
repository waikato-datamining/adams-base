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
 * SpreadSheetDialog.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;

import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.visualization.core.PopupMenuCustomizer;

/**
 * Dialog for displaying a spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = 7604505322768892726L;

  /** the table for displaying the spreadsheet. */
  protected SpreadSheetPanel m_Panel;
  
  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public SpreadSheetDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public SpreadSheetDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public SpreadSheetDialog(Dialog owner, String title) {
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
  public SpreadSheetDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public SpreadSheetDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public SpreadSheetDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public SpreadSheetDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public SpreadSheetDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
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
    
    m_Panel = new SpreadSheetPanel();
    getContentPane().add(m_Panel);
    setJMenuBar(m_Panel.getMenuBar());
    
    setSize(400, 600);
  }
  
  /**
   * Sets the spreadsheet to display.
   * 
   * @param value	the spreadsheet to display
   */
  public void setSpreadSheet(SpreadSheet value) {
    m_Panel.setSpreadSheet(value);
  }
  
  /**
   * Returns the spreadsheet currently displayed.
   * 
   * @return		the spreadsheet on display
   */
  public SpreadSheet getSpreadSheet() {
    return m_Panel.getSpreadSheet();
  }

  /**
   * Sets the popup menu customizer to use.
   *
   * @param value	the customizer, null to remove it
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_Panel.setCellPopupMenuCustomizer(value);
  }

  /**
   * Returns the current popup menu customizer.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_Panel.getCellPopupMenuCustomizer();
  }

  /**
   * Checks whether a custom background color for negative values has been set.
   *
   * @return		true if custom color set
   */
  public boolean hasNegativeBackground() {
    return m_Panel.hasNegativeBackground();
  }

  /**
   * Sets the custom background color for negative values.
   *
   * @param value	the color, null to unset it
   */
  public void setNegativeBackground(Color value) {
    m_Panel.setNegativeBackground(value);
  }

  /**
   * Returns the custom background color for negative values, if any.
   *
   * @return		the color, null if none set
   */
  public Color getNegativeBackground() {
    return m_Panel.getNegativeBackground();
  }

  /**
   * Checks whether a custom background color for positive values has been set.
   *
   * @return		true if custom color set
   */
  public boolean hasPositiveBackground() {
    return m_Panel.hasPositiveBackground();
  }

  /**
   * Sets the custom background color for positive values.
   *
   * @param value	the color, null to unset it
   */
  public void setPositiveBackground(Color value) {
    m_Panel.setPositiveBackground(value);
  }

  /**
   * Returns the custom background color for positive values, if any.
   *
   * @return		the color, null if none set
   */
  public Color getPositiveBackground() {
    return m_Panel.getPositiveBackground();
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    m_Panel.setShowFormulas(value);
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return m_Panel.getShowFormulas();
  }

  /**
   * Sorts the spreadsheet with the given comparator.
   *
   * @param comparator	the row comparator to use
   */
  public void sort(RowComparator comparator) {
    m_Panel.sort(comparator);
  }

  /**
   * Sets the number of decimals to display. Use -1 to display all.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_Panel.setNumDecimals(value);
  }

  /**
   * Returns the currently set number of decimals. -1 if displaying all.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_Panel.getNumDecimals();
  }
  
  /**
   * Sets whether the search is visible.
   * 
   * @param value	true if to show search
   */
  public void setShowSearch(boolean value) {
    m_Panel.setShowSearch(value);
  }
  
  /**
   * Returns whether the search is visible.
   * 
   * @return 		true if search is shown
   */
  public boolean getShowSearch() {
    return m_Panel.getShowSearch();
  }
}
