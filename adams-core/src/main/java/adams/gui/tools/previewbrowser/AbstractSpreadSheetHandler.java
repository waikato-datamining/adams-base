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
 * AbstractSpreadSheetHandler.java
 * Copyright (C) 2011-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.core.spreadsheettable.DefaultCellRenderingCustomizer;
import adams.gui.dialog.SpreadSheetPanel;

import java.awt.BorderLayout;
import java.io.File;

/**
 * Ancestor for handlers that display spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSpreadSheetHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 460332838835780629L;

  /** the prefix for unnamed sheets. */
  public final static String UNNAMED_SHEET_PREFIX = "Sheet";

  /** for customizing the cell rendering. */
  protected CellRenderingCustomizer m_CellRenderingCustomizer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the following spreadsheet types: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "cell-rendering-customizer", "cellRenderingCustomizer",
      new DefaultCellRenderingCustomizer());
  }

  /**
   * Sets the renderer.
   *
   * @param value	the renderer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    m_CellRenderingCustomizer = value;
    reset();
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cellRenderingCustomizerTipText() {
    return "The renderer to use for the cells.";
  }

  /**
   * Reads all the individual spreadsheets from the file.
   *
   * @param file	the file to read
   * @return		the spreadsheet objects that were read from the file
   */
  protected abstract SpreadSheet[] readAll(File file);

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    BasePanel		result;
    BaseTabbedPane	tabbedPane;
    SpreadSheetPanel 	panel;
    SpreadSheet[]	sheets;
    int			i;

    sheets = readAll(file);

    if (sheets.length > 1) {
      result     = new BasePanel(new BorderLayout());
      tabbedPane = new BaseTabbedPane();
      tabbedPane.setTabPlacement(BaseTabbedPane.BOTTOM);
      result.add(tabbedPane, BorderLayout.CENTER);
      for (i = 0; i < sheets.length; i++) {
	if (!sheets[i].hasName())
	  sheets[i].setName(UNNAMED_SHEET_PREFIX + (i+1));
	panel = new SpreadSheetPanel();
	panel.setCellRenderingCustomizer(ObjectCopyHelper.copyObject(m_CellRenderingCustomizer));
	panel.setSpreadSheet(sheets[i]);
	panel.setShowSearch(true);
	tabbedPane.addTab(sheets[i].getName(), panel);
      }
      panel = null;
    }
    else if (sheets.length == 1) {
      result = new BasePanel(new BorderLayout());
      panel = new SpreadSheetPanel();
      panel.setCellRenderingCustomizer(ObjectCopyHelper.copyObject(m_CellRenderingCustomizer));
      panel.setSpreadSheet(sheets[0]);
      panel.setShowSearch(true);
      result.add(panel, BorderLayout.CENTER);
    }
    else {
      result = new NoDataToPreviewPanel();
      panel = null;
    }

    if (panel != null)
      return new PreviewPanel(result, panel);
    else
      return new PreviewPanel(result);
  }
}
