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
 * MultiRowScore.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Applies the specified row score algorithms sequentially to the row and combines the output.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row-score &lt;adams.data.spreadsheet.rowscore.AbstractRowScore&gt; [-row-score ...] (property: rowScores)
 * &nbsp;&nbsp;&nbsp;The row score algorithms to apply.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiRowScore
  extends AbstractRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;

  /** the row score algorithms to use. */
  protected AbstractRowScore[] m_RowScores;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the specified row score algorithms sequentially to the row "
	+ "and combines the output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row-score", "rowScores",
	    new AbstractRowScore[0]);
  }

  /**
   * Sets the row score algorithms to use.
   *
   * @param value	the algorithms
   */
  public void setRowScores(AbstractRowScore[] value) {
    m_RowScores = value;
    reset();
  }

  /**
   * Returns the row score algorithms to use.
   *
   * @return		the algorithms
   */
  public AbstractRowScore[] getRowScores() {
    return m_RowScores;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowScoresTipText() {
    return "The row score algorithms to apply.";
  }

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    int		result;
    
    result = 0;
    
    for (AbstractRowScore score: m_RowScores)
      result += score.getNumScores();
    
    return result;
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
    List<Double>	result;
    int			i;
    Double[]		scores;
    
    result = new ArrayList<Double>();
    
    for (AbstractRowScore score: m_RowScores) {
      scores = score.calculateScore(sheet, rowIndex);
      if (scores == null) {
	for (i = 0; i < score.getNumScores(); i++)
	  result.add(null);
      }
      else {
	result.addAll(Arrays.asList(scores));
      }
    }
    
    return result.toArray(new Double[result.size()]);
  }
}
