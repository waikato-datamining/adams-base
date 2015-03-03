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
 * AbstractSpreadSheetHandler.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.awt.BorderLayout;
import java.io.File;

import adams.core.Utils;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.SpreadSheetTable;

/**
 * Ancestor for handlers that display spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Report
 */
public abstract class AbstractSpreadSheetHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 460332838835780629L;

  /** the prefix for unnamed sheets. */
  public final static String UNNAMED_SHEET_PREFIX = "Sheet";

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
   * Reads all the invidivual spreadsheets from the file.
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
    SpreadSheetTable	table;
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
	table = new SpreadSheetTable(sheets[i]);
	tabbedPane.addTab(sheets[i].getName(), new BaseScrollPane(table));
      }
      table = null;
    }
    else if (sheets.length == 1) {
      result = new BasePanel(new BorderLayout());
      table  = new SpreadSheetTable(sheets[0]);
      result.add(new BaseScrollPane(table), BorderLayout.CENTER);
    }
    else {
      result = new NoDataToPreviewPanel();
      table  = null;
    }

    if (table != null)
      return new PreviewPanel(result, table);
    else
      return new PreviewPanel(result);
  }
}
