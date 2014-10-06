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
 * ReportDbUpdater.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

/**
 <!-- globalinfo-start -->
 * Adds all the data to the report (or reporthandler's report) passing through that the SQL statement returns.<br/>
 * The {ID} placeholder can be used in the SQL statement to represent the current handler's ID.<br/>
 * The following types of SQL statements are supported:<br/>
 * - multiple rows of key-value pairs.<br/>
 * - single row, with the key being the column name.<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br/>
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
 * &nbsp;&nbsp;&nbsp;default: ReportDbUpdater
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
 * <pre>-sql &lt;adams.db.SQLStatement&gt; (property: SQL)
 * &nbsp;&nbsp;&nbsp;The SQL statement that selects the key-value pairs for the report; you can 
 * &nbsp;&nbsp;&nbsp;use the {ID} placeholder for the current handler's ID in your SQL statement 
 * &nbsp;&nbsp;&nbsp;(if it is an adams.data.id.IDHandler).
 * &nbsp;&nbsp;&nbsp;default: select key,value from table where id = \\\"{ID}\\\"
 * </pre>
 * 
 * <pre>-query-type &lt;KEY_VALUE|COLUMN_AS_KEY&gt; (property: queryType)
 * &nbsp;&nbsp;&nbsp;The type of query that the SQL statement represents; multiple rows with 
 * &nbsp;&nbsp;&nbsp;key-value pairs (KEY_VALUE) or single row with the column name as key (COLUMN_AS_KEY
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: KEY_VALUE
 * </pre>
 * 
 * <pre>-column-key &lt;java.lang.String&gt; (property: columnKey)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the key for the key-value pairs to be 
 * &nbsp;&nbsp;&nbsp;added to the report.
 * &nbsp;&nbsp;&nbsp;default: key
 * </pre>
 * 
 * <pre>-column-value &lt;java.lang.String&gt; (property: columnValue)
 * &nbsp;&nbsp;&nbsp;The name of the column containing the value for the key-value pairs to be 
 * &nbsp;&nbsp;&nbsp;added to the report.
 * &nbsp;&nbsp;&nbsp;default: value
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportDbUpdater
  extends AbstractReportDbUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 7895423855845071565L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Adds all the data to the report (or reporthandler's report) passing "
	+ "through that the SQL statement returns.\n"
	+ "The " + PLACEHOLDER_ID + " placeholder can be used in the SQL "
	+ "statement to represent the current handler's ID.\n"
	+ "The following types of SQL statements are supported:\n"
	+ "- multiple rows of key-value pairs.\n"
	+ "- single row, with the key being the column name.\n";
  }
}
