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
 * SpreadSheetInsertRowScore.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.rowscore.AbstractRowScore;
import adams.data.spreadsheet.rowscore.RowStatistic;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Inserts a score column at a specific position into spreadsheets coming through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetInsertRowScore
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-position &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the score column; An index is a number starting 
 * &nbsp;&nbsp;&nbsp;with 1; column names (case-sensitive) as well as the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-after &lt;boolean&gt; (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the score column is inserted after the position instead of at 
 * &nbsp;&nbsp;&nbsp;the position.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-header &lt;java.lang.String&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The name of the score column.
 * &nbsp;&nbsp;&nbsp;default: Score
 * </pre>
 * 
 * <pre>-score &lt;adams.data.spreadsheet.rowscore.AbstractRowScore&gt; (property: score)
 * &nbsp;&nbsp;&nbsp;The score algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowscore.RowStatistic -statistic adams.data.spreadsheet.rowstatistic.Mean
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7726 $
 */
public class SpreadSheetInsertRowScore
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;
  
  /** the position where to insert the column. */
  protected SpreadSheetColumnIndex m_Position;  
  
  /** whether to insert after the position instead of at. */
  protected boolean m_After;
  
  /** the column header. */
  protected String m_Header;
  
  /** the score algorithm to use. */
  protected AbstractRowScore m_Score;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Inserts a score column at a specific position into spreadsheets "
	+ "coming through.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Position = new SpreadSheetColumnIndex();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "position", "position",
	    new SpreadSheetColumnIndex(Index.LAST));

    m_OptionManager.add(
	    "after", "after",
	    true);

    m_OptionManager.add(
	    "header", "header",
	    "Score-#");

    m_OptionManager.add(
	    "score", "score",
	    new RowStatistic());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result = QuickInfoHelper.toString(this, "header", "'" + m_Header + "'", "header: ");

    if (QuickInfoHelper.hasVariable(this, "after"))
      result += ", at/after: ";
    else if (m_After)
      result += ", after: ";
    else
      result += ", at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);

    result += QuickInfoHelper.toString(this, "score", m_Score, ", score: ");

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the position where to insert the score column.
   *
   * @param value	the position
   */
  public void setPosition(SpreadSheetColumnIndex value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the score column.
   *
   * @return		the position
   */
  public SpreadSheetColumnIndex getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return
        "The position where to insert the score column; " + m_Position.getExample();
  }

  /**
   * Sets whether to insert at or after the position.
   *
   * @param value	true if to add after
   */
  public void setAfter(boolean value) {
    m_After = value;
    reset();
  }

  /**
   * Returns whether to insert at or after the position.
   *
   * @return		true if to add after
   */
  public boolean getAfter() {
    return m_After;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String afterTipText() {
    return
        "If enabled, the score column is inserted after the position instead of at "
	+ "the position.";
  }

  /**
   * Sets the name of the score column.
   *
   * @param value	the name
   */
  public void setHeader(String value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the name of the score column.
   *
   * @return		the name
   */
  public String getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The name of the score column; '#' is 1-based index for "
	+ "filled in score value, '$' is 1-based, absolute column index; "
	+ "Using a header definition of 'Att-1,Att-2,Att-3' with a size of 5 "
	+ "will give you: 'Score-#' -> 'Att-1,Att-2,Att-3,Score-1,Score-2', "
	+ "'Score-$' -> 'Att-1,Att-2,Att-3,Score-4,Score-5'";
  }

  /**
   * Sets the score algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setScore(AbstractRowScore value) {
    m_Score = value;
    reset();
  }

  /**
   * Returns the score algorithm to use.
   *
   * @return		the algorithm
   */
  public AbstractRowScore getScore() {
    return m_Score;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scoreTipText() {
    return "The score algorithm to use.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<Double[]>	scores;
    SpreadSheet		sheetOld;
    SpreadSheet		sheetNew;
    int			pos;
    int			currPos;
    int			row;
    int			i;
    int			numScores;
    String		header;
    Double[]		score;
    Cell		cell;

    result   = null;
    sheetOld = (SpreadSheet) m_InputToken.getPayload();

    // calc scores
    scores = new ArrayList<Double[]>();
    for (row = 0; row < sheetOld.getRowCount(); row++)
      scores.add(m_Score.calculateScore(sheetOld, row));
    
    // determine position
    m_Position.setSpreadSheet(sheetOld);
    pos = m_Position.getIntIndex();
    if (m_After)
      pos++;
    
    // add column
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();
    numScores = m_Score.getNumScores();
    currPos   = pos;
    for (i = 0; i < numScores; i++) {
      header = m_Header;
      header = header.replace("#", "" + i).replace("$", "" + (currPos + 1));
      sheetNew.insertColumn(currPos, header);
      currPos++;
    }

    // set scores
    for (row = 0; row < sheetNew.getRowCount(); row++) {
      score = scores.get(row);
      if (score != null) {
	currPos = pos;
	for (i = 0; i < numScores; i++) {
	  cell = sheetNew.getCell(row, currPos);
	  if (cell != null)
	    cell.setContent(score[i]);
	  if (isLoggingEnabled())
	    getLogger().info(SpreadSheet.getCellPosition(row, currPos) + ": " + score + " " + (cell == null ? "failed to set" : "set"));
	  currPos++;
	}
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info(SpreadSheet.getCellPosition(row, pos) + ": failed to calculate score");
      }
    }
    
    scores.clear();
    
    m_OutputToken = new Token(sheetNew);
    
    return result;
  }
}
