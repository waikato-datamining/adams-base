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
 * MathExpression.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import java.util.HashMap;

import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

/**
 <!-- globalinfo-start -->
 * Uses a mathematical expression to post-process or combine the score(s) returned from the base row score algorithm.<br/>
 * The individual scores of the base algorithm can be accessed using placeholders of the format: [n] with 'n' being the 1-based index in the score array.<br/>
 * 'null' scores get interpreted as 'NaN'.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row-score &lt;adams.data.spreadsheet.rowscore.AbstractRowScore&gt; (property: rowScore)
 * &nbsp;&nbsp;&nbsp;The row score algorithm to obtain the scores from.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowscore.ArrayStatistic -statistic adams.data.statistics.ArrayMean
 * </pre>
 * 
 * <pre>-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The mathematical expression to use for post-processing&#47;combining the score
 * &nbsp;&nbsp;&nbsp;(s) of the base algorithm.
 * &nbsp;&nbsp;&nbsp;default: [1]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathExpression
  extends AbstractMetaRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -9037884201569670797L;

  /** the expression to use for combining the scores. */
  protected MathematicalExpressionText m_Expression;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses a mathematical expression to post-process or combine the "
	+ "score(s) returned from the base row score algorithm.\n"
	+ "The individual scores of the base algorithm can be accessed using "
	+ "placeholders of the format: [n] with 'n' being the 1-based index "
	+ "in the score array.\n"
	+ "'null' scores get interpreted as 'NaN'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "expression", "expression",
	    new MathematicalExpressionText("[1]"));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String rowScoreTipText() {
    return "The row score algorithm to obtain the scores from.";
  }

  /**
   * Sets the range of columns to apply the row score algorithm to.
   *
   * @param value	the range
   */
  public void setExpression(MathematicalExpressionText value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the mathematical expression.
   *
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return 
	"The mathematical expression to use for post-processing/combining "
	+ "the score(s) of the base algorithm.";
  }

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    return m_RowScore.getNumScores();
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
    Double[]	result;
    Double[]	scores;
    HashMap	symbols;
    int		i;
    
    result = null;
    
    scores = m_RowScore.calculateScore(sheet, rowIndex);
    if (scores == null)
      return result;
    
    // parse
    symbols = new HashMap();
    for (i = 0; i < scores.length; i++) {
      if (scores[i] == null)
	symbols.put("" + (i+1), Double.NaN);
      else
	symbols.put("" + (i+1), scores[i]);
    }
    try {
      result = new Double[]{MathematicalExpression.evaluate(m_Expression.getValue(), symbols)};
    }
    catch (Exception e) {
      m_LastError = Utils.handleException(
	  this, 
	  "Failed to process expression '" + m_Expression + "' using symbols '" + symbols + "':", 
	  e);
    }
    
    return result;
  }
}
