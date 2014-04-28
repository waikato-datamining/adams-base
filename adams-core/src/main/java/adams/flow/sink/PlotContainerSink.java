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
 * PlotContainerSink.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Hashtable;

import adams.core.NamedCounter;
import adams.core.QuickInfoHelper;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;

/**
 <!-- globalinfo-start -->
 * Actor that outputs the plot containers in various formats.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: PlotContainerSink
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to save the generated output to.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-writer &lt;adams.core.io.AbstractSpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for outputting the plot containers.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.CsvSpreadSheetWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlotContainerSink
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 3238389451500168650L;

  /**
   * Comparator for doubles that are stored as string.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DoubleComparator
    implements Comparator<String>, Serializable {

    /** for serialization. */
    private static final long serialVersionUID = 4012515986361784010L;

    /**
     * Compares the two strings representing two double values.
     *
     * @param o1	the first double
     * @param o2	the second double
     * @return		-1, 0, +1 if first double is less than, equal to or
     * 			greater than the second double
     */
    public int compare(String o1, String o2) {
      Double	d1;
      Double	d2;

      d1 = new Double(o1);
      d2 = new Double(o2);

      return d1.compareTo(d2);
    }
  }

  /** the key for storing the spreadsheet generated so far in the backup. */
  public final static String BACKUP_SPREADSHEET = "spreadsheet";

  /** the key for storing the token counts in the backup. */
  public final static String BACKUP_TOKENCOUNTS = "token counts";

  /** the key for storing the X value in the spreadsheet. */
  public final static String COLUMN_X = "x";

  /** the writer to use. */
  protected SpreadSheetWriter m_Writer;

  /** stores the content of the plot containers. */
  protected SpreadSheet m_SpreadSheet;

  /** for keeping track of the number of tokens being added for a specific
   * sequence, in case the X value is undefined (seqname - count relation). */
  protected NamedCounter m_TokenCounts;

  /** the comparator to use. */
  protected DoubleComparator m_Comparator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor that outputs the plot containers in various formats.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	     new CsvSpreadSheetWriter());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_SpreadSheet = new SpreadSheet();
    m_TokenCounts = new NamedCounter();
    m_Comparator  = new DoubleComparator();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void reset() {
    super.reset();

    m_SpreadSheet = new SpreadSheet();
    m_SpreadSheet.getHeaderRow().addCell(COLUMN_X).setContent(COLUMN_X);
    m_TokenCounts.clear();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to save the generated output to.";
  }

  /**
   * Sets the writer to use for outputting the plot containers.
   *
   * @param value 	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer to use for outputting the plot containers.
   *
   * @return 		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for outputting the plot containers.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_SPREADSHEET);
    pruneBackup(BACKUP_TOKENCOUNTS);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_SPREADSHEET, m_SpreadSheet);
    result.put(BACKUP_TOKENCOUNTS, m_TokenCounts);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_SPREADSHEET)) {
      m_SpreadSheet = (SpreadSheet) state.get(BACKUP_SPREADSHEET);
      state.remove(BACKUP_SPREADSHEET);
    }
    if (state.containsKey(BACKUP_TOKENCOUNTS)) {
      m_TokenCounts = (NamedCounter) state.get(BACKUP_TOKENCOUNTS);
      state.remove(BACKUP_TOKENCOUNTS);
    }

    super.restoreState(state);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SequencePlotterContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SequencePlotterContainer	cont;
    String			name;
    Double			x;
    Double			y;
    Row				row;
    String			rowKey;

    result = null;

    cont = (SequencePlotterContainer) m_InputToken.getPayload();
    name = (String) cont.getValue(SequencePlotterContainer.VALUE_PLOTNAME);
    x    = (Double) cont.getValue(SequencePlotterContainer.VALUE_X);
    y    = (Double) cont.getValue(SequencePlotterContainer.VALUE_Y);

    // update counts
    m_TokenCounts.next(name);

    // add new header column if necessary
    row = m_SpreadSheet.getHeaderRow();
    if (!row.hasCell(name))
      row.addCell(name).setContent(name);

    // add container to spreadsheet
    if (x == null)
      x = new Double(m_TokenCounts.current(name));
    rowKey = "" + x;
    if (m_SpreadSheet.hasRow(rowKey)) {
      row = m_SpreadSheet.getRow(rowKey);
    }
    else {
      row = m_SpreadSheet.addRow(rowKey);
      row.addCell(COLUMN_X).setContent(x);
    }
    if (!row.hasCell(name))
      row.addCell(name).setContent(y);
    else
      row.getCell(name).setContent(y);

    // sort row keys
    m_SpreadSheet.sort(m_Comparator);

    if (!m_Writer.write(m_SpreadSheet, m_OutputFile))
      result = "Failed to write plot data to '" + m_OutputFile + "'!";

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_SpreadSheet = null;
    if (m_TokenCounts != null)
      m_TokenCounts.clear();
    m_TokenCounts = null;

    super.wrapUp();
  }
}
