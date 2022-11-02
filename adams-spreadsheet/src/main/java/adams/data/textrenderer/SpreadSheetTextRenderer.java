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
 * SpreadSheetTextRenderer.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetView;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Renders spreadsheet as CSV up to the specified maximum of rows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetTextRenderer
  extends AbstractLimitedTextRenderer {

  private static final long serialVersionUID = 2413293721997389467L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Renders spreadsheet as CSV up to the specified maximum of rows.";
  }

  /**
   * Returns the default limit.
   *
   * @return		the default
   */
  @Override
  public int getDefaultLimit() {
    return 100;
  }

  /**
   * Returns the minimum limit.
   *
   * @return		the minimum
   */
  @Override
  public Integer getMinLimit() {
    return 0;
  }

  /**
   * Returns the maximum limit.
   *
   * @return		the maximum
   */
  @Override
  public Integer getMaxLimit() {
    return null;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String limitTipText() {
    return "The maximum number of rows to render.";
  }

  /**
   * Checks whether the object is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof SpreadSheet);
  }

  /**
   * Checks whether the class is handled.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.matches(SpreadSheet.class, cls);
  }

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  @Override
  protected String doRender(Object obj) {
    StringBuilder	result;
    SpreadSheet		sheet;
    int			i;
    TIntList		rows;

    sheet = (SpreadSheet) obj;
    if (sheet.getRowCount() > getActualLimit()) {
      rows = new TIntArrayList();
      for (i = 0; i < m_Limit; i++)
        rows.add(i);
      sheet = new SpreadSheetView(sheet, rows.toArray(), null);
    }

    result = new StringBuilder(sheet.toString());
    if (sheet.getRowCount() > getActualLimit())
      result.append(DOTS);

    return result.toString();
  }
}
