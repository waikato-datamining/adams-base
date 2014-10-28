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
 * AbstractMetaRowScore.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

/**
 * Ancestor for row score algorithms that use a base algorithm.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaRowScore
  extends AbstractRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;

  /** the row score to use on the subset. */
  protected AbstractRowScore m_RowScore;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row-score", "rowScore",
	    getDefaultRowScore());
  }

  /**
   * Returns the default row score algorithm to use.
   * 
   * @return		the default algorithm
   */
  protected AbstractRowScore getDefaultRowScore() {
    return new ArrayStatistic();
  }
  
  /**
   * Sets the row score algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setRowScore(AbstractRowScore value) {
    m_RowScore = value;
    reset();
  }

  /**
   * Returns the row score algorithm in use.
   *
   * @return		the algorithm
   */
  public AbstractRowScore getRowScore() {
    return m_RowScore;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String rowScoreTipText();

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    return m_RowScore.getNumScores();
  }
}
