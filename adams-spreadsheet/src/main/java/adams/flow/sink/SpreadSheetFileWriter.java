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
 * SpreadSheetFileWriter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.IncrementalSpreadSheetWriter;
import adams.data.io.output.MultiSheetSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Actor that writes SpreadSheet objects to files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.Row<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetFileWriter
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the output file.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.csv
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.SpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer for storing the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFileWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 393925191813730213L;

  /** the writer to use. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor that writes SpreadSheet objects to files.";
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

  @Override
  protected void reset() {
    super.reset();

    if (m_Writer != null)
      m_Writer.reset();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "writer", m_Writer);
    result += QuickInfoHelper.toString(this, "outputFile", m_OutputFile, ", file: ");

    return result;
  }

  /**
   * Returns the default output file.
   *
   * @return		the file
   */
  @Override
  protected PlaceholderFile getDefaultOutputFile() {
    return new PlaceholderFile("${TMP}/out.csv");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The name of the output file.";
  }

  /**
   * Sets the writer to use.
   *
   * @param value	the writer to use
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer in use.
   *
   * @return		the writer in use
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
    return "The writer for storing the spreadsheet.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.spreadsheet.SpreadSheet.class, adams.data.spreadsheet.Row.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    List<Class>	result;
    
    result = new ArrayList<Class>();
    result.add(SpreadSheet.class);

    if (m_Writer instanceof MultiSheetSpreadSheetWriter)
      result.add(SpreadSheet[].class);

    if (m_Writer instanceof IncrementalSpreadSheetWriter) {
      if (((IncrementalSpreadSheetWriter) m_Writer).isIncremental())
	result.add(Row.class);
    }
    
    return result.toArray(new Class[result.size()]);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Row			row;
    SpreadSheet		sheet;
    SpreadSheet[]	sheets;

    result = null;

    if (m_InputToken.getPayload() instanceof Row) {
      row = (Row) m_InputToken.getPayload();
      if (!((IncrementalSpreadSheetWriter) m_Writer).write(row, m_OutputFile))
	result = "Problems writing row to '" + m_OutputFile + "'!";
    }
    else if (m_InputToken.getPayload() instanceof SpreadSheet) {
      sheet = (SpreadSheet) m_InputToken.getPayload();
      if (!m_Writer.write(sheet, m_OutputFile))
	result = "Problems writing spreadsheet to '" + m_OutputFile + "'!";
    }
    else {
      sheets = (SpreadSheet[]) m_InputToken.getPayload();
      if (!((MultiSheetSpreadSheetWriter) m_Writer).write(sheets, m_OutputFile))
	result = "Problems writing spreadsheets to '" + m_OutputFile + "'!";
    }

    return result;
  }
}
