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
 * Copyright (C) 2020-2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
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
    int			channels;
    int			color;

    try {
      reader = new PngReader(in);
      if (isLoggingEnabled())
	getLogger().info(reader.imgInfo.toString());

      result = new DefaultSpreadSheet();
      // header
      channels = reader.imgInfo.channels;
      row = result.getHeaderRow();
      for (i = 0; i < reader.imgInfo.cols; i++)
        row.addCell("" + i).setContentAsString("" + (i+1));

      // data
      for (n = 0; n < reader.imgInfo.rows; n++) {
        row  = result.addRow();
        line = reader.readRow();
	if (line instanceof ImageLineByte) {
	  lineByte = (ImageLineByte) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    if (channels == 4) {
	      color = lineByte.getElem(i * channels + 3) << 24  // A
		| lineByte.getElem(i * channels) << 16          // R
		| lineByte.getElem(i * channels + 1) << 8       // G
		| lineByte.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 3) {
	      color = lineByte.getElem(i * channels) << 16      // R
		| lineByte.getElem(i * channels + 1) << 8       // G
		| lineByte.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 2) {
	      color = lineByte.getElem(i * channels + 1) << 24  // A
		| lineByte.getElem(i * channels) << 16          // gray
		| lineByte.getElem(i * channels) << 8           // gray
		| lineByte.getElem(i * channels);               // gray
	    }
	    else {
	      color = lineByte.getElem(i) << 16      // gray
		| lineByte.getElem(i) << 8           // gray
		| lineByte.getElem(i);               // gray
	    }
	    row.addCell("" + i).setContent(color);
	  }
	}
	else {
	  lineInt = (ImageLineInt) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    if (channels == 4) {
	      color = lineInt.getElem(i * channels + 3) << 24  // A
		| lineInt.getElem(i * channels) << 16          // R
		| lineInt.getElem(i * channels + 1) << 8       // G
		| lineInt.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 3) {
	      color = lineInt.getElem(i * channels) << 16      // R
		| lineInt.getElem(i * channels + 1) << 8       // G
		| lineInt.getElem(i * channels + 2);           // B
	    }
	    else if (channels == 2) {
	      color = lineInt.getElem(i * channels + 1) << 24  // A
		| lineInt.getElem(i * channels) << 16          // gray
		| lineInt.getElem(i * channels) << 8           // gray
		| lineInt.getElem(i * channels);               // gray
	    }
	    else {
	      color = lineInt.getElem(i) << 16      // gray
		| lineInt.getElem(i) << 8           // gray
		| lineInt.getElem(i);               // gray
	    }
	    row.addCell("" + i).setContent(color);
	  }
	}
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read PNG!", e);
    }

    return result;
  }

  /**
   * Runs the reader from the command-line.
   *
   * Use the option {@link #OPTION_INPUT} to specify the input file.
   * If the option {@link #OPTION_OUTPUT} is specified then the read sheet
   * gets output as .csv files in that directory.
   *
   * @param args	the command-line options to use
   */
  public static void main(String[] args) {
    runReader(Environment.class, PNGSpreadSheetReader.class, args);
  }
}
