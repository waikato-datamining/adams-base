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
import adams.data.spreadsheet.SpreadSheetUtils;
import org.jfree.data.xy.DefaultXYZDataset;

/**
 * Generates {@link DefaultXYZDataset} datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultXYZ
  extends AbstractDatasetGenerator<DefaultXYZDataset> {

  private static final long serialVersionUID = -690091259560326881L;

  /** the X column. */
  protected SpreadSheetColumnIndex m_X;

  /** the Y column. */
  protected SpreadSheetColumnIndex m_Y;

  /** the Z column. */
  protected SpreadSheetColumnIndex m_Z;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an XYZ dataset from the specified columns.";
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
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "z", "Z",
      new SpreadSheetColumnIndex("3"));
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
   * Sets the index of column to use as Y.
   *
   * @param value	the index
   */
  public void setY(SpreadSheetColumnIndex value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the index of column to use as Y.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The column in the spreadsheet to use for the Y axis.";
  }

  /**
   * Sets the index of column to use as Z.
   *
   * @param value	the index
   */
  public void setZ(SpreadSheetColumnIndex value) {
    m_Z = value;
    reset();
  }

  /**
   * Returns the index of column to use as Z.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getZ() {
    return m_Z;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ZTipText() {
    return "The column in the spreadsheet to use for the Z axis.";
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
    result += QuickInfoHelper.toString(this, "Z", m_Z, ", Z: ");

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
      m_Z.setData(data);
      if (m_X.getIntIndex() == -1)
	result = "X column not valid? Provided: " + m_X;
      else if (m_Y.getIntIndex() == -1)
	result = "Y column not valid? Provided: " + m_Y;
      else if (m_Z.getIntIndex() == -1)
	result = "Z column not valid? Provided: " + m_Z;
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
  protected DefaultXYZDataset doGenerate(SpreadSheet data) {
    DefaultXYZDataset	result;
    int			x;
    int 		y;
    int 		z;
    int			i;
    double[] 		plotX;
    double[] 		plotY;
    double[] 		plotZ;

    m_X.setData(data);
    x = m_X.getIntIndex();
    m_Y.setData(data);
    y = m_Y.getIntIndex();
    m_Z.setData(data);
    z = m_Z.getIntIndex();

    result = new DefaultXYZDataset();
    plotX = SpreadSheetUtils.getNumericColumn(data, x);
    plotY = SpreadSheetUtils.getNumericColumn(data, y);
    plotZ = SpreadSheetUtils.getNumericColumn(data, z);
    result.addSeries(
      data.getColumnName(x) + "-" + data.getColumnName(y) + "-" + data.getColumnName(y),
      new double[][]{plotX, plotY, plotZ});

    return result;
  }
}
