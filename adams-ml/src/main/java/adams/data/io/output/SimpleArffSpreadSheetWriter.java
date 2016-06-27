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
 * SimpleArffSpreadSheetWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.data.DateFormatString;
import adams.data.io.input.SimpleArffSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Project;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Simple writer for Weka ARFF files, only supports NUMERIC, NOMINAL, STRING and DATE attributes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-nominal-values &lt;int&gt; (property: maxNominalValues)
 * &nbsp;&nbsp;&nbsp;The maximum number of different values to accept for NOMINAL attributes 
 * &nbsp;&nbsp;&nbsp;before switching to STRING.
 * &nbsp;&nbsp;&nbsp;default: 25
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-max-decimals &lt;int&gt; (property: maxDecimals)
 * &nbsp;&nbsp;&nbsp;The maximum number of decimals to use for numeric values.
 * &nbsp;&nbsp;&nbsp;default: 6
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-date-format &lt;adams.data.DateFormatString&gt; (property: dateFormat)
 * &nbsp;&nbsp;&nbsp;The format to use for the date attributes.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleArffSpreadSheetWriter
  extends AbstractSpreadSheetWriter {

  private static final long serialVersionUID = -5681810295868479786L;

  /** the maximum number of values for nominal attributes. */
  protected int m_MaxNominalValues;

  /** the maximum number of decimals to use for numeric values. */
  protected int m_MaxDecimals;

  /** the format to use. */
  protected DateFormatString m_DateFormat;

  /** for generating the timestamp. */
  protected transient DateFormat m_Formatter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simple writer for Weka ARFF files, only supports NUMERIC, NOMINAL, "
	+ "STRING and DATE attributes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-nominal-values", "maxNominalValues",
      25, 0, null);

    m_OptionManager.add(
      "max-decimals", "maxDecimals",
      6, 0, null);

    m_OptionManager.add(
      "date-format", "dateFormat",
      getDefaultFormat());
  }

  /**
   * Sets the maximum number of different values to accept for NOMINAL
   * attributes before switching to STRING.
   *
   * @param value	the maximum
   */
  public void setMaxNominalValues(int value) {
    if (getOptionManager().isValid("maxNominalValues", value)) {
      m_MaxNominalValues = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of different values to accept for NOMINAL
   * attributes before switching to STRING.
   *
   * @return		the maximum
   */
  public int getMaxNominalValues() {
    return m_MaxNominalValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxNominalValuesTipText() {
    return "The maximum number of different values to accept for NOMINAL attributes before switching to STRING.";
  }

  /**
   * Sets the maximum number of decimals to use for numeric values.
   *
   * @param value	the maximum
   */
  public void setMaxDecimals(int value) {
    if (getOptionManager().isValid("maxDecimals", value)) {
      m_MaxDecimals = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of decimals to use for numeric values.
   *
   * @return		the maximum
   */
  public int getMaxDecimals() {
    return m_MaxDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxDecimalsTipText() {
    return "The maximum number of decimals to use for numeric values.";
  }

  /**
   * Returns the instance of a date formatter to use.
   *
   * @return		the formatter object
   */
  protected synchronized DateFormat getFormatter() {
    if (m_Formatter == null)
      m_Formatter = m_DateFormat.toDateFormat();

    return m_Formatter;
  }

  /**
   * Returns the default format to use.
   *
   * @return		the format
   */
  protected DateFormatString getDefaultFormat() {
    return new DateFormatString("yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Sets the date format string to use.
   *
   * @param value	the format
   */
  public void setDateFormat(DateFormatString value) {
    m_DateFormat = value;
    reset();
  }

  /**
   * Returns the date format string in use.
   *
   * @return		the format
   */
  public DateFormatString getDateFormat() {
    return m_DateFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dateFormatTipText() {
    return "The format to use for the date attributes.";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public SpreadSheetReader getCorrespondingReader() {
    return new SimpleArffSpreadSheetReader();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleArffSpreadSheetReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleArffSpreadSheetReader().getFormatExtensions();
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.WRITER;
  }

  /**
   * Returns whether to automatically compress.
   *
   * @return		true if to automatically decompress
   */
  @Override
  protected boolean supportsCompressedOutput() {
    return true;
  }

  /**
   * Cleans up the cell types.
   *
   * @param cellTypes	the cell types to process
   */
  protected void cleanUpTypes(HashSet<ContentType> cellTypes) {
    cellTypes.remove(ContentType.MISSING);
    // numeric
    if (cellTypes.contains(ContentType.LONG)) {
      cellTypes.remove(ContentType.LONG);
      cellTypes.add(ContentType.DOUBLE);
    }
    // date
    if (cellTypes.contains(ContentType.TIME)) {
      cellTypes.remove(ContentType.TIME);
      cellTypes.add(ContentType.DATETIMEMSEC);
    }
    if (cellTypes.contains(ContentType.TIMEMSEC)) {
      cellTypes.remove(ContentType.TIMEMSEC);
      cellTypes.add(ContentType.DATETIMEMSEC);
    }
    if (cellTypes.contains(ContentType.DATE)) {
      cellTypes.remove(ContentType.DATE);
      cellTypes.add(ContentType.DATETIMEMSEC);
    }
    if (cellTypes.contains(ContentType.DATETIME)) {
      cellTypes.remove(ContentType.DATETIME);
      cellTypes.add(ContentType.DATETIMEMSEC);
    }
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    boolean			result;
    BufferedWriter		bwriter;
    String			name;
    boolean[]			nominal;
    ContentType[]		types;
    int				i;
    int				n;
    HashSet<ContentType>	cellTypes;
    List<String>[]		values;
    Cell			cell;

    result = true;

    if (writer instanceof BufferedWriter)
      bwriter = (BufferedWriter) writer;
    else
      bwriter = new BufferedWriter(writer);

    try {
      // relation
      name = content.getName();
      if (name == null)
	name = Project.NAME;
      bwriter.write(SimpleArffSpreadSheetReader.KEYWORD_RELATION + " " + Utils.quote(name));
      bwriter.newLine();

      // separator
      bwriter.newLine();

      // header
      nominal = new boolean[content.getColumnCount()];
      types   = new ContentType[content.getColumnCount()];
      values  = new List[content.getColumnCount()];
      for (i = 0; i < content.getColumnCount(); i++) {
	cellTypes = new HashSet<>(content.getContentTypes(i));
	cleanUpTypes(cellTypes);
	// determine type
	if (cellTypes.size() == 0)
	  types[i] = ContentType.DOUBLE;
	else if (cellTypes.size() == 1)
	  types[i] = cellTypes.iterator().next();
	else
	  throw new IllegalStateException("Failed to determine single cell type for column #" + (i+1) + "!");
	// nominal or string?
	if (types[i] == ContentType.STRING) {
	  values[i] = content.getCellValues(i);
	  for (n = 0; n < values[i].size(); n++)
	    values[i].set(n, Utils.quote(values[i].get(n)));
	  if (values[i].size() <= m_MaxNominalValues)
	    nominal[i] = true;
	}
	// output
	bwriter.write(SimpleArffSpreadSheetReader.KEYWORD_ATTRIBUTE + " " + content.getColumnName(i) + " ");
	switch (types[i]) {
	  case STRING:
	    if (nominal[i]) {
	      bwriter.write("{");
	      bwriter.write(Utils.flatten(values[i], ","));
	      bwriter.write("}");
	    }
	    else {
	      bwriter.write("string");
	    }
	    break;
	  case DOUBLE:
	    bwriter.write("numeric");
	    break;
	  case DATE:
	    bwriter.write("date " + Utils.quote(m_DateFormat.getValue()));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled cell type: " + types[i]);
	}
	bwriter.newLine();
      }

      // separator
      bwriter.newLine();

      // data
      bwriter.write(SimpleArffSpreadSheetReader.KEYWORD_DATA);
      bwriter.newLine();
      for (Row row: content.rows()) {
	for (i = 0; i < content.getColumnCount(); i++) {
	  cell = row.getCell(i);
	  if (i > 0)
	    bwriter.write(',');
	  if ((cell != null) && !cell.isMissing()) {
	    switch (types[i]) {
	      case STRING:
                if (cell.getContent().equals("?"))
                  bwriter.write("'?'");
                else
                  bwriter.write(Utils.quote(cell.getContent()));
		break;
	      case DOUBLE:
		bwriter.write(Utils.doubleToString(cell.toDouble(), m_MaxDecimals));
		break;
	      case DATE:
		bwriter.write(Utils.quote(getFormatter().format(cell.toAnyDateType())));
		break;
	      default:
		throw new IllegalStateException("Unhandled cell type: " + types[i]);
	    }
	  }
	  else {
	    bwriter.write('?');
	  }
	}
	bwriter.newLine();
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write spreadsheet!", e);
      result = false;
    }

    return result;
  }
}
