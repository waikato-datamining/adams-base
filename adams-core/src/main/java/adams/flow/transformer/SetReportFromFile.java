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
 * SetReportFromFile.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.flow.core.ReportUpdateType;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Loads a report from disk and replaces the current one in the token passing through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: SetReportFromFile
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-report-file &lt;adams.core.io.PlaceholderFile&gt; (property: reportFile)
 * &nbsp;&nbsp;&nbsp;The file to load the report from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the report.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 * 
 * <pre>-update-type &lt;REPLACE|MERGE_CURRENT_WITH_OTHER|MERGE_OTHER_WITH_CURRENT&gt; (property: updateType)
 * &nbsp;&nbsp;&nbsp;Determines how to update the report.
 * &nbsp;&nbsp;&nbsp;default: REPLACE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetReportFromFile
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8951982264797087668L;

  /** the file to load. */
  protected PlaceholderFile m_ReportFile;

  /** the report loader to use. */
  protected AbstractReportReader m_Reader;

  /** how to update. */
  protected ReportUpdateType m_UpdateType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Loads a report from disk and replaces the current one in the token "
	+ "passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "report-file", "reportFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "reader", "reader",
	    getDefaultReader());

    m_OptionManager.add(
	    "update-type", "updateType",
	    ReportUpdateType.REPLACE);
  }

  /**
   * Sets the file to load the report from.
   *
   * @param value	the file
   */
  public void setReportFile(PlaceholderFile value) {
    m_ReportFile = value;
    reset();
  }

  /**
   * Returns the file to load the report from.
   *
   * @return		the file
   */
  public PlaceholderFile getReportFile() {
    return m_ReportFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reportFileTipText() {
    return "The file to load the report from.";
  }

  /**
   * Returns the default reader to use.
   * 
   * @return		the reader
   */
  protected AbstractReportReader getDefaultReader() {
    return new DefaultSimpleReportReader();
  }
  
  /**
   * Sets the reader to use for reading the report.
   *
   * @param value	the reader
   */
  public void setReader(AbstractReportReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use for reading the report.
   *
   * @return		the reader
   */
  public AbstractReportReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for loading the report.";
  }

  /**
   * Sets the report update type.
   *
   * @param value	the update type
   */
  public void setUpdateType(ReportUpdateType value) {
    m_UpdateType = value;
    reset();
  }

  /**
   * Returns the report update type.
   *
   * @return		the update type
   */
  public ReportUpdateType getUpdateType() {
    return m_UpdateType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateTypeTipText() {
    return "Determines how to update the report.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "reportFile", m_ReportFile);
    result += QuickInfoHelper.toString(this, "reader", m_Reader, " with ");
    result += QuickInfoHelper.toString(this, "updateType", m_UpdateType, ", update: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    return new Class[]{MutableReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated output
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (getOptionManager().getVariableForProperty("reportFile") == null) {
	if (m_ReportFile.isDirectory())
	  result = "Report file '" + m_ReportFile + "' points to a directory!";
	else if (!m_ReportFile.exists())
	  result = "Report file '" + m_ReportFile + "' does not exist!";
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    MutableReportHandler	handler;
    List			reports;
    Report			other;

    result  = null;

    handler = (MutableReportHandler) m_InputToken.getPayload();
    m_Reader.setInput(m_ReportFile);
    reports = m_Reader.read();
    if (reports.size() > 0) {
      if (reports.size() > 1)
	getLogger().severe("WARNING: report file '" + m_ReportFile + "' contains more than one report, using only first report!");
      other = (Report) reports.get(0);
      switch (m_UpdateType) {
        case REPLACE:
          handler.setReport(other);
          break;
	case MERGE_CURRENT_WITH_OTHER:
	  handler.getReport().mergeWith(other);
	  break;
	case MERGE_OTHER_WITH_CURRENT:
	  other.mergeWith(handler.getReport());
	  handler.setReport(other);
	  break;
	default:
	  throw new IllegalStateException("Unhandled update type: " + m_UpdateType);
      }
    }
    else {
      result = "Not able to extract a report from file '" + m_ReportFile + "'!";
    }
    
    if (result == null)
      m_OutputToken = new Token(handler);
    
    return result;
  }
}
