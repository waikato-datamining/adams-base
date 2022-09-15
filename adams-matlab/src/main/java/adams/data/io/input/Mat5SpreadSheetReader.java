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
 * Mat5SpreadSheetReader.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.logging.LoggingHelper;
import adams.data.conversion.MatlabArrayToSpreadSheet;
import adams.data.io.output.Mat5SpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SheetRange;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.MatFile;
import us.hebi.matlab.mat.types.Struct;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads Matlab .mat files (format 5)
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
 * <pre>-sheets &lt;adams.core.Range&gt; (property: sheetRange)
 * &nbsp;&nbsp;&nbsp;The range of sheets to load.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 *
 * <pre>-entry-name &lt;java.lang.String&gt; (property: entryName)
 * &nbsp;&nbsp;&nbsp;The name of the entry to retrieve, takes precedence over range.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-field-name &lt;java.lang.String&gt; (property: fieldName)
 * &nbsp;&nbsp;&nbsp;The name of the field to retrieve from the struct.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mat5SpreadSheetReader
    extends AbstractMultiSheetSpreadSheetReader<SheetRange> {

  private static final long serialVersionUID = -9113442938603879820L;

  /** the name of the entry to retrieve (the first one if empty). */
  protected String m_EntryName;

  /** the name of the field to retrieve from the struct. */
  protected String m_FieldName;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads Matlab .mat files (format 5)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"entry-name", "entryName",
	"");

    m_OptionManager.add(
	"field-name", "fieldName",
	"");
  }

  /**
   * Returns the default sheet range.
   *
   * @return		the default
   */
  protected SheetRange getDefaultSheetRange() {
    return new SheetRange(SheetRange.FIRST);
  }

  /**
   * Sets the name of the entry to retrieve, takes precedence over range.
   *
   * @param value	the name
   */
  public void setEntryName(String value) {
    m_EntryName = value;
    reset();
  }

  /**
   * Returns the name of the entry to retrieve, takes precedence over range.
   *
   * @return		the name
   */
  public String getEntryName() {
    return m_EntryName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String entryNameTipText() {
    return "The name of the entry to retrieve, takes precedence over range.";
  }

  /**
   * Sets the name of the field to retrieve to retrieve from the struct.
   *
   * @param value	the name
   */
  public void setFieldName(String value) {
    m_FieldName = value;
    reset();
  }

  /**
   * Returns the name of the field to retrieve to retrieve from the struct.
   *
   * @return		the name
   */
  public String getFieldName() {
    return m_FieldName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldNameTipText() {
    return "The name of the field to retrieve from the struct.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Matlab .mat files";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"mat"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return new Mat5SpreadSheetWriter();
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.FILE;
  }

  /**
   * Converts the array.
   *
   * @param array	the array to convert
   * @return		the generated spreadsheet
   * @throws Exception	if conversion failed
   */
  protected SpreadSheet convert(Array array) throws Exception {
    MatlabArrayToSpreadSheet	conv;

    conv = new MatlabArrayToSpreadSheet();
    conv.setInput(array);
    m_LastError = conv.convert();
    if (m_LastError == null)
      return (SpreadSheet) conv.getOutput();
    else
      getLogger().severe(m_LastError);

    return null;
  }

  /**
   * Performs the actual reading. Must handle compression itself, if
   * {@link #supportsCompressedInput()} returns true.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   */
  @Override
  protected SpreadSheet doRead(File file) {
    Mat5File		mat5;
    Array		array;
    SpreadSheet		sheet;
    String		entryName;

    if (m_EntryName.isEmpty())
      getLogger().warning("Entry name is empty, reading first entry!");

    try {
      mat5 = Mat5.readFromFile(file.getAbsoluteFile());

      if (isLoggingEnabled()) {
	for (MatFile.Entry entry: mat5.getEntries())
	  getLogger().info("Entry: " + entry.getName());
      }
      entryName = m_EntryName;

      array = null;
      for (MatFile.Entry entry: mat5.getEntries()) {
	if (entry.getName().equals(entryName) || entryName.isEmpty()) {
	  array     = entry.getValue();
	  entryName = entry.getName();
	  break;
	}
      }
      if (array == null) {
	m_LastError = "Failed to load entry: " + m_EntryName;
	getLogger().severe(m_LastError);
      }
      else {
	if (array instanceof Struct)
	  array = ((Struct) array).get(m_FieldName);
	sheet = convert(array);
	if (sheet != null)
	  sheet.setName(entryName);
	return sheet;
      }
    }
    catch (Exception e) {
      m_LastError = LoggingHelper.handleException(this, "Failed to read: " + file, e);
    }

    return null;
  }

  /**
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the spreadsheets or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected List<SpreadSheet> doReadRange(File file) {
    List<SpreadSheet>	result;
    SpreadSheet		sheet;
    Mat5File		mat5;
    Array		array;
    int			i;
    TIntSet		indices;
    String[]		entryNames;

    result = new ArrayList<>();

    // entry name -> read only that array?
    if (!m_EntryName.isEmpty()) {
      sheet = read(file);
      if (sheet != null)
	result.add(sheet);
      return result;
    }

    try {
      mat5       = Mat5.readFromFile(file.getAbsoluteFile());
      entryNames = new String[mat5.getNumEntries()];
      i          = 0;
      for (MatFile.Entry entry: mat5.getEntries()) {
        entryNames[i] = entry.getName();
        i++;
        if (isLoggingEnabled())
	  getLogger().info("Entry #" + (i+1) + ": " + entry.getName());
      }

      m_SheetRange.setSheetNames(entryNames);
      indices = new TIntHashSet(m_SheetRange.getIntIndices());
      i       = 0;
      for (MatFile.Entry entry: mat5.getEntries()) {
	if (indices.contains(i)) {
	  array = entry.getValue();
	  sheet = convert(array);
	  if (sheet != null) {
	    sheet.setName(entry.getName());
	    result.add(sheet);
	  }
	}
	i++;
      }
    }
    catch (Exception e) {
      m_LastError = LoggingHelper.handleException(this, "Failed to read: " + file, e);
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
    runReader(Environment.class, Mat5SpreadSheetReader.class, args);
  }
}
