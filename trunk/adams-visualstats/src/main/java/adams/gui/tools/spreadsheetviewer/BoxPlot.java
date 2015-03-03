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
 * BoxPlot.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import java.awt.Color;

import weka.core.Instances;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.boxplot.BoxPlotManager;

/**
 * Generates box plots for the data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoxPlot
  extends AbstractViewPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 9089022183434856748L;

  /** the generated data. */
  protected Instances m_Data;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates box plots for the data.";
  }

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  @Override
  public String getMenuText() {
    return "Box plot";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getMenuIcon() {
    return "boxplot.png";
  }

  /**
   * Checks the spreadsheet.
   * 
   * @param sheet	the spreadsheet to check
   * @return		null if check passed, otherwise error message
   */
  @Override
  protected String check(SpreadSheet sheet) {
    String			result;
    SpreadSheetToWekaInstances	convert;
    
    result = super.check(sheet);

    if (result == null) {
      m_Data  = null;
      convert = new SpreadSheetToWekaInstances();
      convert.setInput(sheet);
      result = convert.convert();
      if (result == null)
	m_Data = (Instances) convert.getOutput();
      convert.cleanUp();
    }
    
    return result;
  }
  
  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  @Override
  protected BasePanel doGenerate(SpreadSheet sheet) {
    BoxPlotManager	result;

    result = new BoxPlotManager();
    result.setInstances(m_Data);
    result.setNumHorizontal(3);
    result.setFill(true);
    result.setColor(Color.RED);
    result.reset();
    
    return result;
  }
}
