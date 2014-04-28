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
 * ReportFileWriter.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.io.output.AbstractReportWriter;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Saves a report to disk with the specified writer and passes the absolute filename on.<br/>
 * As filename&#47;directory name (depending on the writer) the database ID of the report is used (below the specified output directory).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
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
 * &nbsp;&nbsp;&nbsp;default: ReportFileWriter
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
 * <pre>-writer &lt;adams.data.io.output.AbstractReportWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for saving the reports.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.DefaultSimpleReportWriter
 * </pre>
 * 
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The output directory for the reports.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-generator &lt;adams.core.io.AbstractFilenameGenerator&gt; (property: filenameGenerator)
 * &nbsp;&nbsp;&nbsp;The filename generator to use for creating the filename for the report.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.DefaultFilenameGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportFileWriter
  extends AbstractReportFileWriter<Report> {

  /** for serialization. */
  private static final long serialVersionUID = -5209437097716008045L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Saves a report to disk with the "
      + "specified writer and passes the absolute filename on.\n"
      + "As filename/directory name (depending on the writer) the "
      + "database ID of the report is used (below the specified output "
      + "directory).";
  }

  /**
   * Returns the default writer.
   *
   * @return		the writer
   */
  protected AbstractReportWriter<Report> getDefaultWriter() {
    return new DefaultSimpleReportWriter();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the type of report
   */
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }
}
