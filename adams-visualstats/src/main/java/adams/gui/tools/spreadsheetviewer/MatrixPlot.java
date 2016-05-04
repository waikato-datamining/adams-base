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
 * MatrixPlot.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;
import adams.gui.visualization.stats.scatterplot.Matrix;

/**
 * Generates a matrix plot for the data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatrixPlot
  extends AbstractViewPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 9089022183434856748L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a matrix plot for the data.";
  }

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  @Override
  public String getMenuText() {
    return "Matrix plot";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getMenuIcon() {
    return "matrixplot.png";
  }

  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  @Override
  protected BasePanel doGenerate(SpreadSheet sheet) {
    Matrix	result;

    result = new Matrix();
    result.setData(sheet);
    result.setPercent(100);
    result.setPaintlet(new ScatterPaintletCircle());
    result.setPlotSize(100);
    result.reset();
    
    return result;
  }
}
