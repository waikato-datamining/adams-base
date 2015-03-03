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
 * SAXDistanceFromBaseline.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import gnu.trove.list.array.TDoubleArrayList;
import adams.data.filter.RowNorm;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.data.utils.SAXUtils;

/**
 <!-- globalinfo-start -->
 * Applies adams.data.filter.RowNorm to the timeseries before calculating the SAX distance of the timeseries to the baseline.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-windows &lt;int&gt; (property: numWindows)
 * &nbsp;&nbsp;&nbsp;The number of windows to use for Piecewise Aggregate Approximation (PAA).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use for the Gaussian.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SAXDistanceFromBaseline
  extends AbstractRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -13137285273610739L;

  /** the number of windows to use for PAA. */
  protected int m_NumWindows;
  
  /** the number of breakpoints to use (for the Gaussian). */
  protected int m_NumBins;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies " + RowNorm.class.getName() + " to the timeseries before "
	+ "calculating the SAX distance of the timeseries to the baseline.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-windows", "numWindows",
	    10, 1, null);

    m_OptionManager.add(
	    "num-bins", "numBins",
	    10, 1, null);
  }

  /**
   * Sets the number of windows to use for PAA.
   *
   * @param value 	the number
   */
  public void setNumWindows(int value) {
    if (value >= 1) {
      m_NumWindows = value;
      reset();
    }
    else {
      getLogger().severe("The number of windows must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of windows to use for PAA.
   *
   * @return 		the number
   */
  public int getNumWindows() {
    return m_NumWindows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numWindowsTipText() {
    return "The number of windows to use for Piecewise Aggregate Approximation (PAA).";
  }

  /**
   * Sets the number of bins to use for the Gaussian.
   *
   * @param value 	the number
   */
  public void setNumBins(int value) {
    if (value >= 1) {
      m_NumBins = value;
      reset();
    }
    else {
      getLogger().severe("The number of bins must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of bins to use for the Gaussian.
   *
   * @return 		the number
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use for the Gaussian.";
  }

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    return 1;
  }

  /**
   * Performs the actual calculation of the row score.
   *
   * @param sheet	the spreadsheet to generate the score for
   * @param rowIndex	the row index
   * @return		the generated score, null in case of an error
   */
  @Override
  protected Double[] doCalculateScore(SpreadSheet sheet, int rowIndex) {
    Double[]		result;
    TDoubleArrayList	x;
    double[]		norm;
    double[]		normSAX;
    double[]		baseSAX;
    Row			row;
    double[]		bps;
    double[][]		matrix;
    
    result = new Double[]{0.0};
    row    = sheet.getRow(rowIndex);
    x      = new TDoubleArrayList();
    for (Cell cell: row.cells()) {
      if (cell.isNumeric())
	x.add(cell.toDouble());
    }
    norm      = StatUtils.rowNorm(x.toArray());
    bps       = SAXUtils.calcBreakPoints(m_NumBins);
    matrix    = SAXUtils.calcDistMatrix(bps);
    normSAX   = SAXUtils.toSAX(norm, m_NumWindows, bps);
    baseSAX   = new double[normSAX.length];
    result[0] = SAXUtils.minDist(normSAX, baseSAX, matrix, x.size());
    
    return result;
  }
}
