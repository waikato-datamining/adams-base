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
 * EqualWidthBins.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import adams.core.Utils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Splits the row into bins with (more or less) same size and applies the base score algorithm to each of the bins.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row-score &lt;adams.data.spreadsheet.rowscore.AbstractRowScore&gt; (property: rowScore)
 * &nbsp;&nbsp;&nbsp;The row score algorithm to apply to the each of the bins.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowscore.ArrayStatistic -statistic adams.data.statistics.ArrayMean
 * </pre>
 * 
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EqualWidthBins
  extends AbstractMetaRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;

  /** the number of bins to apply the base score to. */
  protected int m_NumBins;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Splits the row into bins with (more or less) same size and applies "
	+ "the base score algorithm to each of the bins.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-bins", "numBins",
	    1, 1, null);
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String rowScoreTipText() {
    return "The row score algorithm to apply to the each of the bins.";
  }

  /**
   * Sets the number of bins.
   *
   * @param value	the number of bins
   */
  public void setNumBins(int value) {
    m_NumBins = value;
    reset();
  }

  /**
   * Returns the number of bins.
   *
   * @return		the number of bins
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
    return "The number of bins to use.";
  }

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    return m_NumBins * m_RowScore.getNumScores();
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
    int			i;
    int			n;
    int[] 		bins;
    SpreadSheet		subset;
    Row			rowNew;
    Row			rowOld;
    Double[]		scores;

    result = null;

    if (sheet.getColumnCount() >= m_NumBins) {
      // calculate bins
      bins    = new int[m_NumBins + 1];
      bins[0] = 0;
      for (i = 0; i < m_NumBins; i++)
	bins[i + 1] = sheet.getColumnCount() / m_NumBins * (i + 1);
      if (isLoggingEnabled())
	getLogger().info("bins: " + Utils.arrayToString(bins));

      // calculate scores
      rowOld = sheet.getRow(rowIndex);
      result = new Double[getNumScores()];
      for (i = 0; i < m_NumBins; i++) {
	// header
	subset = new DefaultSpreadSheet();
	rowNew = subset.getHeaderRow();
	for (n = bins[i]; n < bins[i + 1]; n++)
	  rowNew.addCell("" + n).setContent("H-" + n);
	// data
	rowNew = subset.addRow();
	for (n = bins[i]; n < bins[i + 1]; n++) {
	  if (rowOld.hasCell(n))
	    rowNew.addCell("" + n).assign(rowOld.getCell(n));
	  else
	    rowNew.addCell("" + n).setMissing();
	}
	// calculate scores on subset
	scores = m_RowScore.calculateScore(subset, 0);
	if (scores != null) {
	  for (n = 0; n < scores.length; n++)
	    result[i * m_RowScore.getNumScores() + n] = scores[n];
	}
      }
    }
    
    return result;
  }
}
