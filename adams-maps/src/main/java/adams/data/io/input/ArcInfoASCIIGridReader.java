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
 * ArcInfoASCIIGridReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads ASCII files in ESRI Grid format.<br>
 * <br>
 * For more information see:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Esri_grid
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
 * <pre>-output-grid &lt;boolean&gt; (property: outputGrid)
 * &nbsp;&nbsp;&nbsp;If enabled, a spreadsheet is generated that represents the data in the file
 * &nbsp;&nbsp;&nbsp;rather than one value per row with GPS coordinates.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The size of the chunks (= number of rows) of spreadsheets to output if not
 * &nbsp;&nbsp;&nbsp;in grid mode.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArcInfoASCIIGridReader
  extends AbstractSpreadSheetReader
  implements ChunkedSpreadSheetReader {

  private static final long serialVersionUID = 1925577691804114810L;

  /** the number of columns key. */
  public final static String KEY_NUMCOLS = "ncols";

  /** the number of rows key. */
  public final static String KEY_NUMROWS = "nrows";

  /** the left corner key. */
  public final static String KEY_LEFT = "xllcorner";

  /** the bottom corner key. */
  public final static String KEY_BOTTOM = "yllcorner";

  /** the number of columns key. */
  public final static String KEY_CELLSIZE = "cellsize";

  /** the no data value key. */
  public final static String KEY_NODATAVALUE = "NODATA_value";

  /** whether to output the grid instead the values alongside their GPS coordinates. */
  protected boolean m_OutputGrid;

  /** the chunk size. */
  protected int m_ChunkSize;

  /** the raw data. */
  protected List<String> m_Raw;

  /** the meta data. */
  protected HashMap<String,String> m_MetaData;

  /** the size of the header in rows. */
  protected int m_Header;

  /** the current offset. */
  protected int m_Offset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads ASCII files in ESRI Grid format.\n\n"
      + "For more information see:\n"
      + "https://en.wikipedia.org/wiki/Esri_grid";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-grid", "outputGrid",
      false);

    m_OptionManager.add(
      "chunk-size", "chunkSize",
      -1, -1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Raw      = new ArrayList<>();
    m_MetaData = new HashMap<>();
    m_Header   = 0;
    m_Offset   = 0;
  }

  /**
   * Sets whether to output a grid of values (as stored in the file) instead
   * of the values associated with their GPS coordinates.
   *
   * @param value	true if to output grid
   */
  public void setOutputGrid(boolean value) {
    m_OutputGrid = value;
    reset();
  }

  /**
   * Returns whether to output a grid of values (as stored in the file) instead
   * of the values associated with their GPS coordinates.
   *
   * @return		true if to output grid
   */
  public boolean getOutputGrid() {
    return m_OutputGrid;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputGridTipText() {
    return
      "If enabled, a spreadsheet is generated that represents the data in "
        + "the file rather than one value per row with GPS coordinates.";
  }

  /**
   * Sets the maximum chunk size (non-grid mode).
   *
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  @Override
  public void setChunkSize(int value) {
    m_ChunkSize = value;
    reset();
  }

  /**
   * Returns the current chunk size (non-grid mode).
   *
   * @return	the size of the chunks, &lt; 1 denotes infinity
   */
  @Override
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  @Override
  public String chunkSizeTipText() {
    return
      "The size of the chunks (= number of rows) of spreadsheets to output "
	+ "if not in grid mode.";
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.FILE;
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Arc/Info ASCII Grid";
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
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return null;
  }

  /**
   * Generates spreadsheets from the raw ASCII data.
   *
   * @return		the generated spreadsheet
   */
  protected SpreadSheet readData() {
    SpreadSheet		result;
    String		line;
    String[]		parts;
    Number[]		values;
    int			i;
    int			cols;
    int			rows;
    double		left;
    double		bottom;
    double		cellSize;
    double		lat;
    double		lon;
    String		missing;
    int 		rowIdx;
    int 		colIdx;
    Row			row;

    cols     = Integer.parseInt(m_MetaData.get(KEY_NUMCOLS));
    rows     = Integer.parseInt(m_MetaData.get(KEY_NUMROWS));
    left     = Double.parseDouble(m_MetaData.get(KEY_LEFT));
    bottom   = Double.parseDouble(m_MetaData.get(KEY_BOTTOM));
    cellSize = Double.parseDouble(m_MetaData.get(KEY_CELLSIZE));
    missing  = null;
    if (m_MetaData.containsKey(KEY_NODATAVALUE))
      missing = m_MetaData.get(KEY_NODATAVALUE);

    // header
    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    if (m_OutputGrid) {
      for (i = 0; i < cols; i++)
	row.addCell("" + i).setContent("col" + (i+1));
    }
    else {
      row.addCell("lat").setContent("Lat");
      row.addCell("lon").setContent("Lon");
      row.addCell("val").setContent("Value");
    }

    // data
    values = new Number[cols];
    for (i = m_Header + m_Offset; i < m_Raw.size(); i++) {
      line   = m_Raw.get(i).trim();
      parts  = line.split(" ");

      if (m_Stopped)
	break;

      // parse data
      for (colIdx = 0; colIdx < parts.length; colIdx++) {
	if ((missing != null) && parts[colIdx].equals(missing))
	  values[colIdx] = null;
	else if (Utils.isInteger(parts[colIdx]))
	  values[colIdx] = Integer.parseInt(parts[colIdx]);
	else
	  values[colIdx] = Double.parseDouble(parts[colIdx]);
      }

      // add to spreadsheet
      if (m_OutputGrid) {
	row = result.addRow();
	for (colIdx = 0; colIdx < parts.length; colIdx++)
	  row.addCell("" + colIdx).setNative(values[colIdx]);
      }
      else {
	rowIdx = (rows - 1) - (i - m_Header);  // row from bottom
	lat = bottom + rowIdx * cellSize;
	for (colIdx = 0; colIdx < parts.length; colIdx++) {
	  lon = left + colIdx * cellSize;
	  row = result.addRow();
	  row.addCell("lat").setContent(lat);
	  row.addCell("lon").setContent(lon);
	  row.addCell("val").setNative(values[colIdx]);
	}
      }

      // chunk size reached?
      if (!m_OutputGrid && (m_ChunkSize > 0)) {
	if ((i == m_Header + m_Offset + m_ChunkSize - 1) || (i == m_Raw.size() - 1)) {
	  m_Offset += m_ChunkSize;
	  break;
	}
      }
    }

    // only 1 chunk?
    if (m_Raw.size() - m_Header < m_ChunkSize)
      m_Offset = m_Raw.size() - m_Header;

    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Performs the actual reading. Must handle compression itself, if
   * {@link #supportsCompressedInput()} returns true.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   * @see		#supportsCompressedInput()
   */
  @Override
  protected SpreadSheet doRead(File file) {
    SpreadSheet			result;
    String			line;
    int				i;
    String[]			parts;

    // read data
    m_Raw = FileUtils.loadFromFile(file, m_Encoding.getValue());
    if (m_Raw == null) {
      setLastError("Failed to read data from: " + file);
      return null;
    }

    // meta-data
    m_MetaData.clear();
    m_Header = 0;
    for (i = 0; i < m_Raw.size(); i++) {
      line = m_Raw.get(i);
      if (!line.matches("^[a-zA-Z].*"))
	break;
      line = line.replaceAll("[ ][ ]*", " ");
      m_Header++;
      parts = line.split(" ");
      if (parts.length == 2)
	m_MetaData.put(parts[0], parts[1]);
      else
	getLogger().warning("Failed to parse meta-data: " + line);
    }
    if (!m_MetaData.containsKey(KEY_NUMCOLS)) {
      setLastError("Missing meta-data: " + KEY_NUMCOLS);
      return null;
    }
    if (!m_MetaData.containsKey(KEY_NUMROWS)) {
      setLastError("Missing meta-data: " + KEY_NUMROWS);
      return null;
    }
    if (!m_MetaData.containsKey(KEY_LEFT)) {
      setLastError("Missing meta-data: " + KEY_LEFT);
      return null;
    }
    if (!m_MetaData.containsKey(KEY_BOTTOM)) {
      setLastError("Missing meta-data: " + KEY_BOTTOM);
      return null;
    }
    if (!m_MetaData.containsKey(KEY_CELLSIZE)) {
      setLastError("Missing meta-data: " + KEY_CELLSIZE);
      return null;
    }

    m_Offset = 0;
    result = readData();

    return result;
  }

  /**
   * Checks whether there is more data to read.
   *
   * @return		true if there is more data available
   */
  @Override
  public boolean hasMoreChunks() {
    return !m_OutputGrid && (m_ChunkSize > 0) && (m_Offset < m_Raw.size() - m_Header - 1);
  }

  /**
   * Returns the next chunk.
   *
   * @return		the next chunk
   */
  @Override
  public SpreadSheet nextChunk() {
    return readData();
  }
}
