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
 * SpreadSheetQuery.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.parser.GrammarSupplier;
import adams.parser.SpreadSheetQueryText;

import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Applies a query (SELECT, UPDATE, DELETE) on a spreadsheet.<br>
 * Variables are supported as well, e.g., : SELECT * WHERE Blah = &#64;{val}  with 'val' being a variable available at execution time.<br>
 * The following grammar is used for the query:<br>
 * <br>
 * expr_list ::= expr_list expr_part | expr_part;<br>
 * <br>
 * expr_part ::= select | update | delete;<br>
 * <br>
 * select    ::=   SELECT col_list [limit]<br>
 *               | SELECT col_list WHERE cond_list [limit]<br>
 *               | SELECT col_list ORDER BY order_list [limit]<br>
 *               | SELECT col_list WHERE cond_list ORDER BY order_list [limit]<br>
 *               | SELECT agg_list<br>
 *               | SELECT agg_list GROUP BY col_list<br>
 *               | SELECT agg_list HAVING cond_list<br>
 *               | SELECT agg_list GROUP BY col_list HAVING cond_list<br>
 *               ;<br>
 * <br>
 * update    ::=   UPDATE SET upd_list<br>
 *               | UPDATE SET upd_list WHERE cond_list<br>
 *               ;<br>
 * <br>
 * delete    ::=   DELETE WHERE cond_list<br>
 *               ;<br>
 * <br>
 * col_list  ::=   col_list COMMA col<br>
 *               | col<br>
 *               | SELECT NUMBER [subsample: &lt;1 = percent; &gt;= 1 number of rows]<br>
 *               ;<br>
 * <br>
 * col       ::=   * <br>
 *               | COLUMN<br>
 *               | COLUMN AS COLUMN<br>
 *               ;<br>
 * <br>
 * upd_list  ::= upd_list COMMA upd | upd;<br>
 * <br>
 * upd       ::=   COLUMN = value<br>
 *               ;<br>
 * <br>
 * order_list::= order_list COMMA order | order;<br>
 * <br>
 * order     ::=   COLUMN<br>
 *               | COLUMN ASC<br>
 *               | COLUMN DESC<br>
 *               ;<br>
 *               <br>
 * cond_list ::=   cond_list cond <br>
 *               | cond<br>
 *               ;<br>
 * <br>
 * cond      ::=   COLUMN &lt; value<br>
 *               | COLUMN &lt;= value<br>
 *               | COLUMN = value<br>
 *               | COLUMN &lt;&gt; value<br>
 *               | COLUMN &gt;= value<br>
 *               | COLUMN &gt; value<br>
 *               | COLUMN REGEXP STRING<br>
 *               | COLUMN IS NULL<br>
 *               | CELLTYPE ( COLUMN ) = "numeric|long|double|boolean|string|time|date|datetime|timestamp|object|missing"<br>
 *               | ( cond )<br>
 *               | cond:c1 AND cond:c2<br>
 *               | cond:c1 OR cond:c2<br>
 *               | NOT cond<br>
 *               ;<br>
 * <br>
 * value     ::=   NUMBER<br>
 *               | STRING<br>
 *               | PARSE ( "number" , STRING )<br>
 *               | PARSE ( "date" , STRING )<br>
 *               | PARSE ( "time" , STRING )<br>
 *               | PARSE ( "timestamp" , STRING )<br>
 *               ;<br>
 * <br>
 * limit     ::=   LIMIT NUMBER:max<br>
 *               | LIMIT NUMBER:offset , NUMBER:max<br>
 *               ;<br>
 * agg_list  ::=   agg_list COMMA agg <br>
 *               | agg<br>
 *               ;<br>
 * <br>
 * agg       ::=   COUNT [(*)] [AS COLUMN]<br>
 *               | MIN ( COLUMN ) [AS COLUMN]<br>
 *               | MAX ( COLUMN ) [AS COLUMN]<br>
 *               | RANGE ( COLUMN ) [AS COLUMN] (= MIN - MAX)<br>
 *               | MEAN ( COLUMN ) [AS COLUMN]<br>
 *               | AVERAGE ( COLUMN ) [AS COLUMN]<br>
 *               | STDEV ( COLUMN ) [AS COLUMN]<br>
 *               | STDEVP ( COLUMN ) [AS COLUMN]<br>
 *               | SUM ( COLUMN ) [AS COLUMN]<br>
 *               | IQR ( COLUMN ) [AS COLUMN]<br>
 *               | INTERQUARTILE ( COLUMN ) [AS COLUMN]<br>
 * <br>
 * Notes:<br>
 * - time format: 'HH:mm'<br>
 * - date format: 'yyyy-MM-dd'<br>
 * - timestamp format: 'yyyy-MM-dd HH:mm'<br>
 * - STRING is referring to characters enclosed by double quotes<br>
 * - COLUMN is either a string with no blanks (consisting of letters, numbers, hyphen or underscore; eg 'MyCol-1') or a bracket enclosed string when containing blanks (eg '[Some other col]')<br>
 * - columns used in the ORDER BY clause must be present in the SELECT part; also, any alias given to them in SELECT must be used instead of original column name<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetQuery
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-query &lt;adams.parser.SpreadSheetQueryText&gt; (property: query)
 * &nbsp;&nbsp;&nbsp;The query to execute.
 * &nbsp;&nbsp;&nbsp;default: SELECT *
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetQuery
  extends AbstractSpreadSheetTransformer
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -4633161214275622241L;

  /** for query to execute. */
  protected SpreadSheetQueryText m_Query;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies a query (SELECT, UPDATE, DELETE) on a spreadsheet.\n"
	+ "Variables are supported as well, e.g., : SELECT * WHERE Blah = @{val} "
	+ " with 'val' being a variable available at execution time.\n"
	+ "The following grammar is used for the query:\n\n"
	+ getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "query", "query",
	    new SpreadSheetQueryText("SELECT *"));
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new adams.parser.SpreadSheetQuery().getGrammar();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "query", Shortening.shortenEnd(m_Query.getValue().replaceAll("\\s+", " "), 50));
  }

  /**
   * Sets the query to execute.
   *
   * @param value	the query
   */
  public void setQuery(SpreadSheetQueryText value) {
    m_Query = value;
    reset();
  }

  /**
   * Returns the query to execute.
   *
   * @return		the finder
   */
  public SpreadSheetQueryText getQuery() {
    return m_Query;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String queryTipText() {
    return "The query to execute.";
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	query;
    SpreadSheet	sheet;
    
    result = null;
    
    query = m_Query.getValue();
    
    // replace variables with their actual values
    if (isLoggingEnabled())
      getLogger().info("Query: " + query);
    query = getVariables().expand(query);
    if (isLoggingEnabled())
      getLogger().info("--> expanded: " + query);
    
    sheet = null;
    try {
      sheet = adams.parser.SpreadSheetQuery.evaluate(query, new HashMap(), (SpreadSheet) m_InputToken.getPayload());
    }
    catch (Exception e) {
      result = handleException("Failed to evaluate query: " + query, e);
    }
    
    if (sheet != null)
      m_OutputToken = new Token(sheet);
    
    return result;
  }
}
