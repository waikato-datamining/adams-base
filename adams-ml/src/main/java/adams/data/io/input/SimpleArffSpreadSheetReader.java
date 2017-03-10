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
 * SimpleArffSpreadSheetReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.DateFormat;
import adams.core.DateTimeMsec;
import adams.core.Utils;
import adams.data.DateFormatString;
import adams.data.io.output.SimpleArffSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Simple reader for Weka ARFF files, only supports NUMERIC, NOMINAL, STRING and DATE attributes.
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
 * @version $Revision$
 */
public class SimpleArffSpreadSheetReader
  extends AbstractSpreadSheetReader {

  private static final long serialVersionUID = 7620213946139044919L;

  public static final String KEYWORD_RELATION = "@relation";

  public static final String KEYWORD_ATTRIBUTE = "@attribute";

  public static final String KEYWORD_DATA = "@data";

  /**
   * Attribute types.
   */
  public enum AttributeType {
    NUMERIC,
    NOMINAL,
    STRING,
    DATE
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simple reader for Weka ARFF files, only supports NUMERIC, NOMINAL, "
	+ "STRING and DATE attributes.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple ARFF file";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"arff", "arff.gz"};
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.READER;
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return new SimpleArffSpreadSheetWriter();
  }

  /**
   * Returns whether to automatically handle gzip compressed files.
   *
   * @return		true if to automatically decompress
   */
  @Override
  protected boolean supportsCompressedInput() {
    return true;
  }

  /**
   * Extracts the attribute name, type and date format from the line.
   *
   * @param line	the line to parse
   * @return		the extracted data
   */
  protected HashMap<String,String> parseAttribute(String line) {
    HashMap<String,String>	result;
    boolean			quoted;
    String 			current;
    String			lower;
    String			format;

    result  = new HashMap<>();
    current = line.replace("\t", " ");
    current = current.substring(KEYWORD_ATTRIBUTE.length() + 1).trim();

    // name
    if (current.startsWith("'")) {
      quoted = true;
      result.put("name", current.substring(1, current.indexOf('\'', 1)).trim());
    }
    else if (current.startsWith("\"")) {
      quoted = true;
      result.put("name", current.substring(1, current.indexOf('"', 1)).trim());
    }
    else {
      quoted = false;
      result.put("name", current.substring(0, current.indexOf(' ', 1)).trim());
    }
    current = current.substring(result.get("name").length() + (quoted ? 2 : 0)).trim();

    // type
    lower = current.toLowerCase();
    if (lower.startsWith("numeric") || lower.startsWith("real") || lower.startsWith("integer"))
      result.put("type", AttributeType.NUMERIC.toString());
    else if (lower.startsWith("string"))
      result.put("type", AttributeType.STRING.toString());
    else if (lower.startsWith("date"))
      result.put("type", AttributeType.DATE.toString());
    else if (lower.startsWith("{"))
      result.put("type", AttributeType.NOMINAL.toString());
    else
      throw new IllegalStateException("Unsupported attribute: " + current);

    // date format
    if (result.get("type").equals(AttributeType.DATE.toString())) {
      current = current.substring(5).trim();   // remove "date "
      if (current.startsWith("'"))
	format = Utils.unquote(current);
      else if (current.startsWith("\""))
	format = Utils.unDoubleQuote(current);
      else
	format = current;
      if (new DateFormatString().isValid(format))
	result.put("format", format);
      else
	throw new IllegalStateException("Invalid date format: " + format);
    }

    return result;
  }

  /**
   * Extracts the attribute name from the line.
   *
   * @param line	the line to parse
   * @return		the name
   */
  protected String getAttributeName(String line) {
    return parseAttribute(line).get("name");
  }

  /**
   * Extracts the attribute type from the line.
   *
   * @param line	the line to parse
   * @return		the type
   */
  protected AttributeType getAttributeType(String line) {
    return AttributeType.valueOf(parseAttribute(line).get("type"));
  }

  /**
   * Extracts the date format for the attribute from the line.
   *
   * @param line	the line to parse
   * @return		the format, null if not a date attribute
   */
  protected DateFormat getAttributeDateFormat(String line) {
    HashMap<String,String>	data;

    data = parseAttribute(line);
    if (data.containsKey("format"))
      return new DateFormat(data.get("format"));
    else
      return null;
  }

  /**
   * Performs the actual reading.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    DefaultSpreadSheet	result;
    BufferedReader	reader;
    String		line;
    String		lower;
    boolean		header;
    int 		lineIndex;
    Row			row;
    List<AttributeType> types;
    List<DateFormat>	formats;
    String[]		cells;
    int			i;
    Cell 		cell;

    result = new DefaultSpreadSheet();

    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    lineIndex = 0;
    header    = true;
    row       = null;
    types     = new ArrayList<>();
    formats   = new ArrayList<>();
    try {
      while ((line = reader.readLine()) != null) {
	lineIndex++;

	line = line.trim();
	if (line.isEmpty())
	  continue;
	if (line.startsWith("%"))
	  continue;

	if (header) {
	  if (row == null)
	    row = result.getHeaderRow();
	  lower = line.toLowerCase();
	  if (lower.startsWith(KEYWORD_RELATION)) {
	    result.setName(Utils.unquote(line.substring(KEYWORD_RELATION.length()).trim()));
	  }
	  else if (lower.startsWith(KEYWORD_ATTRIBUTE)) {
	    row.addCell("" + row.getCellCount()).setContentAsString(getAttributeName(line));
	    types.add(getAttributeType(line));
	    formats.add(getAttributeDateFormat(line));
	  }
	  else if (lower.startsWith(KEYWORD_DATA)) {
	    header = false;
	  }
	}
	else {
	  cells = SpreadSheetUtils.split(line, ',', false, '\'', true);
	  row   = result.addRow();
	  for (i = 0; i < cells.length && i < result.getColumnCount(); i++) {
            cells[i] = cells[i].trim();
	    if (cells[i].equals("?"))
	      continue;
	    cell = row.addCell(i);
	    if (!cells[i].equals("'?'"))
	      cells[i] = Utils.unquote(cells[i]);
	    switch (types.get(i)) {
	      case NUMERIC:
		cell.setContent(Double.parseDouble(cells[i]));
		break;
	      case NOMINAL:
	      case STRING:
		cell.setContentAsString(cells[i]);
		break;
	      case DATE:
		cell.setContent(new DateTimeMsec(formats.get(i).parse(cells[i])));
		break;
	      default:
		throw new IllegalStateException("Unhandled attribute type: " + types.get(i));
	    }
	  }
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read ARFF data from reader (line #" + (lineIndex +1) + ")!", e);
      result = null;
    }

    return result;
  }
}
