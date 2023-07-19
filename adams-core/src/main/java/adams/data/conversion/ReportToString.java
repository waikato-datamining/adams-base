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
 * StringToReport.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.MessageCollection;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.io.output.StringReportWriter;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Generates a string representation from the report using the specified report writer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-writer &lt;adams.data.io.output.StringReportWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for parsing the string.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.DefaultSimpleReportWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportToString
  extends AbstractConversionFromString {

  private static final long serialVersionUID = 1432694530645128111L;

  /** the report writer to use. */
  protected StringReportWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a string representation from the report using the specified report writer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "writer", "writer",
      new DefaultSimpleReportWriter());
  }

  /**
   * Sets the writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(StringReportWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer in use.
   *
   * @return		the writer
   */
  public StringReportWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for parsing the string.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Report[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    String		result;
    MessageCollection 	errors;

    errors = new MessageCollection();
    result = m_Writer.write((Report) m_Input, errors);
    if (result == null)
      throw new IllegalStateException(errors.toString());

    return result;
  }
}
