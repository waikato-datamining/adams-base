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
 * PNGSpreadSheetReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;

import java.io.InputStream;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads PNGs as spreadsheet, e.g., for reading indexed PNGs.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 *
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PNGSpreadSheetReader
  extends AbstractSpreadSheetReader {

  private static final long serialVersionUID = 1662813915131999182L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads PNGs as spreadsheet, e.g., for reading indexed PNGs.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "PNG reader";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"png"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return null;
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.STREAM;
  }

  /**
   * Performs the actual reading.
   *
   * @param in		the input stream to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(InputStream in) {
    SpreadSheet 	result;
    PngReader 		reader;
    Row 		row;
    int			i;
    int			n;
    IImageLine 		line;
    ImageLineByte 	lineByte;
    ImageLineInt 	lineInt;

    try {
      reader = new PngReader(in);
      if (isLoggingEnabled())
	getLogger().info(reader.imgInfo.toString());

      result = new DefaultSpreadSheet();
      // header
      row = result.getHeaderRow();
      for (i = 0; i < reader.imgInfo.cols; i++)
        row.addCell("" + i).setContentAsString("" + (i+1));

      // data
      for (n = 0; n < reader.imgInfo.rows; n++) {
        row  = result.addRow();
        line = reader.readRow();
	if (line instanceof ImageLineByte) {
	  lineByte = (ImageLineByte) reader.readRow();
	  for (i = 0; i < reader.imgInfo.cols; i++)
	    row.addCell("" + i).setContent(lineByte.getElem(i));
	}
	else {
	  lineInt = (ImageLineInt) reader.readRow();
	  for (i = 0; i < reader.imgInfo.cols; i++)
	    row.addCell("" + i).setContent(lineInt.getElem(i));
	}
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read PNG!", e);
    }

    return result;
  }
}
