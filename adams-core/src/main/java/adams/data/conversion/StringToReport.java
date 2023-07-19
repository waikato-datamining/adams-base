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

import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.StringReportReader;
import adams.data.report.Report;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads the report from the incoming string using the specified report reader.
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
 * <pre>-reader &lt;adams.data.io.input.StringReportReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for parsing the string.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringToReport
  extends AbstractConversionFromString {

  private static final long serialVersionUID = 1432694530645128111L;

  /** the report reader to use. */
  protected StringReportReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads the report from the incoming string using the specified report reader.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new DefaultSimpleReportReader());
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(StringReportReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader
   */
  public StringReportReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for parsing the string.";
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
    Report[]	result;
    List 	reports;
    int		i;

    reports = m_Reader.read((String) m_Input);
    result  = new Report[reports.size()];
    for (i = 0; i < reports.size(); i++)
      result[i] = (Report) reports.get(i);

    return result;
  }
}
