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
 * DefaultXY.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.dataset;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Generates {@link DefaultXYDataset} datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultXY
  extends AbstractDatasetGenerator<DefaultXYDataset> {

  private static final long serialVersionUID = -690091259560326881L;

  /** the X column. */
  protected SpreadSheetColumnIndex m_X;

  /** the Y columns. */
  protected SpreadSheetColumnRange m_Y;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "For each of the Y columns, a separate series is generated.\n"
	+ "The column name is used as name for each series.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x", "X",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "y", "Y",
      new SpreadSheetColumnRange("2"));
  }

  /**
   * Sets the index of the column to act as X axis.
   *
   * @param value	the index
   */
  public void setX(SpreadSheetColumnIndex value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the index of the column to act as X axis.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The index of the column in the spreadsheet to use for the X axis.";
  }

  /**
   * Sets the range of columns to use as Y.
   *
   * @param value	the range
   */
  public void setY(SpreadSheetColumnRange value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the range of columns to use as Y.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The columns in the spreadsheet to use for the Y axis/axes.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "X", m_X, "X: ");
    result += QuickInfoHelper.toString(this, "Y", m_Y, ", Y: ");

    return result;
  }

  /**
   * Hook method for checks before generating the dataset.
   *
   * @param data	the data to use
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check(SpreadSheet data) {
    String  	result;

    result = super.check(data);

    if (result == null) {
      m_X.setData(data);
      m_Y.setData(data);
      if (m_X.getIntIndex() == -1)
	result = "X column not valid? Provided: " + m_X;
      else if (m_Y.getIntIndices().length == 0)
	result = "No Y column(s) available? Provided: " + m_Y;
    }

    return result;
  }

  /**
   * Performs the actual generation of the dataset.
   *
   * @param data	the data to use
   * @return		the dataset
   */
  @Override
  protected DefaultXYDataset doGenerate(SpreadSheet data) {
    DefaultXYDataset	result;
    int			x;
    int[]		y;
    int			i;
    double[] 		plotX;
    double[] 		plotY;

    m_X.setData(data);
    x = m_X.getIntIndex();
    m_Y.setData(data);
    y = m_Y.getIntIndices();

    result = new DefaultXYDataset();
    plotX = SpreadSheetUtils.getNumericColumn(data, x);
    for (i = 0; i < y.length; i++) {
      plotY = SpreadSheetUtils.getNumericColumn(data, y[i]);
      result.addSeries(data.getColumnName(y[i]), new double[][]{plotX, plotY});
    }

    return result;
  }
}
