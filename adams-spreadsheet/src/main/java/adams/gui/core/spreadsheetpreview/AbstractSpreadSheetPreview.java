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
 * AbstractSpreadSheetPreview.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheetpreview;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;

/**
 * Ancestor for spreadsheet previews.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSpreadSheetPreview
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 7564861110651227717L;

  /**
   * Ancestor for spreadsheet preview panels.
   */
  public abstract static class AbstractSpreadSheetPreviewPanel
    extends BasePanel {

    private static final long serialVersionUID = 6219517977593904L;

    /**
     * Check method before generating the preview.
     *
     * @param sheet	the sheet to preview
     * @param rows	the rows to preview
     * @return		null if successfully checked, otherwise error message
     */
    protected String check(SpreadSheet sheet, int[] rows) {
      if (sheet == null)
	return "No spreadsheet provided!";
      return null;
    }

    /**
     * Previews the spreadsheet.
     *
     * @param sheet	the sheet to preview
     * @param rows	the rows to preview
     * @return		null if successfully previewed, otherwise error message
     */
    protected abstract String doPreview(SpreadSheet sheet, int[] rows);

    /**
     * Previews the spreadsheet.
     *
     * @param sheet	the sheet to preview
     * @param rows	the rows to preview
     * @return		null if successfully previewed, otherwise error message
     */
    public String preview(SpreadSheet sheet, int[] rows) {
      String	result;

      result = check(sheet, rows);
      if (result == null)
        result = doPreview(sheet, rows);

      return result;
    }
  }

  /**
   * Generates the preview panel.
   *
   * @return		the preview panel, null if none generated
   */
  public abstract AbstractSpreadSheetPreviewPanel generate();
}
