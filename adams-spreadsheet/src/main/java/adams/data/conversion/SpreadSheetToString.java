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
 * SpreadSheetToString.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.io.StringWriter;

import adams.core.option.OptionUtils;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a string using the specified spreadsheet writer.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.SpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer setup to use for generating the string.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToString
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4890225060389916155L;

  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer;

  /** the actual spreadsheet writer in use. */
  protected SpreadSheetWriter m_ActualWriter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a spreadsheet into a string using the specified spreadsheet writer.";
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
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_ActualWriter = null;
  }

  /**
   * Sets the writer used for generating the string.
   *
   * @param value	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer used for generating the string.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer setup to use for generating the string.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    StringWriter	writer;
    
    writer = new StringWriter();

    if (m_ActualWriter == null)
      m_ActualWriter = (SpreadSheetWriter) OptionUtils.shallowCopy(m_Writer, true);
    m_ActualWriter.write((SpreadSheet) m_Input, writer);

    return writer.toString();
  }
}
