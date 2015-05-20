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
 * SpreadSheetTimeseriesWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.util.List;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.timeseries.Timeseries;

/**
 <!-- globalinfo-start -->
 * Writes timeseries data using a spreadsheet writer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The directory to write the container to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.tmp
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.SpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for writing the spreadsheet file.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetTimeseriesWriter
  extends AbstractTimeseriesWriter 
  implements MetaFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -1167695200549050674L;
  
  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes timeseries data using a spreadsheet writer.";
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Spreadsheet timeseries";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"*"};
  }

  /**
   * Returns the underlying format extensions.
   * 
   * @return		the format extensions (excluding dot)
   */
  public String[] getActualFormatExtensions() {
    return m_Writer.getFormatExtensions();
  }

  /**
   * Sets the writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer to use.
   *
   * @return 		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for writing the spreadsheet file.";
  }

  /**
   * Returns whether writing of multiple containers is supported.
   * 
   * @return 		true if multiple containers are supported
   */
  @Override
  public boolean canWriteMultiple() {
    if (m_Writer instanceof MultiSheetSpreadSheetWriter)
      return ((MultiSheetSpreadSheetWriter) m_Writer).canWriteMultiple();
    else
      return false;
  }
  
  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(List<Timeseries> data) {
    SpreadSheet[]	sheets;
    int			i;
    
    sheets = new SpreadSheet[data.size()];
    for (i = 0; i < data.size(); i++)
      sheets[i] = data.get(i).toSpreadSheet();
    
    if (m_Writer instanceof MultiSheetSpreadSheetWriter)
      return ((MultiSheetSpreadSheetWriter) m_Writer).write(sheets, m_Output);
    else
      return m_Writer.write(sheets[0], m_Output);
  }
}
