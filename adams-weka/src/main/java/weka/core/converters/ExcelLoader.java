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
 * ExcelLoader.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.converters;

import adams.core.DateTime;
import adams.core.DateUtils;
import adams.core.Index;
import adams.core.Range;
import adams.core.base.BaseRegExp;
import adams.core.management.User;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Environment;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Loads MS Excel spreadsheet files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -D
 *  Enables debug output.
 *  (default: off)</pre>
 *
 * <pre> -sheet-index &lt;1-based index&gt;
 *  The index of the worksheet to load. (default: 1)</pre>
 *
 * <pre> -auto-extend-header
 *  Enables automatically extending the header.
 *  (default: off)</pre>
 *
 * <pre> -text-columns &lt;range&gt;
 *  The range of columns to treat as text. (default: none)</pre>
 *
 * <pre> -no-header
 *  If enabled, the spreadsheet is presumed to have no header row.
 *  (default: off)</pre>
 *
 * <pre> -custom-column-headers &lt;comma-separated list&gt;
 *  The headers to use instead (comma-separated list). (default: none)</pre>
 *
 * <pre> -first-row &lt;index&gt;
 *  The first row in the spreadsheet (starts at 1). (default: 1)</pre>
 *
 * <pre> -num-rows &lt;count&gt;
 *  The number of rows to read, read all if &lt;1. (default: 0)</pre>
 *
 * <pre> -missing-value &lt;regexp&gt;
 *  The regular expression for identifying missing values. (default: ^(\?|)$)</pre>
 *
 * <pre> -max-labels &lt;int&gt;
 *  The maximum number of labels for nominal attributes before  they get converted to string. (default: 25)</pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @see Loader
 */
public class ExcelLoader
  extends AbstractFileLoader
  implements BatchConverter, OptionHandler {

  /** for serialization */
  private static final long serialVersionUID = -5037505317395902292L;

  /** Holds the determined structure (header) of the data set. */
  protected Instances m_structure = null;

  /** the actual data. */
  protected Instances m_Data = null;

  /** Holds the source of the data set. */
  protected File m_sourceFile = new File(User.getCWD());

  /** whether to print some debug information */
  protected boolean m_Debug = false;

  /** the sheet to read. */
  protected Index m_SheetIndex = new Index("1");

  /** whether to automatically extend the header if rows have more cells than header. */
  protected boolean m_AutoExtendHeader = false;

  /** the range of columns to force to be text. */
  protected Range m_TextColumns = new Range();

  /** whether the file has a header or not. */
  protected boolean m_NoHeader = false;

  /** the comma-separated list of column header names. */
  protected String m_CustomColumnHeaders = "";

  /** the first row to retrieve (1-based). */
  protected int m_FirstRow = 1;

  /** the number of rows to retrieve (less than 1 = unlimited). */
  protected int m_NumRows = 0;

  protected final static BaseRegExp DEFAULT_MISSING_VALUE = new BaseRegExp("^(\\" + SpreadSheet.MISSING_VALUE + "|)$");

  /** The placeholder for missing values. */
  protected BaseRegExp m_MissingValue = DEFAULT_MISSING_VALUE;

  /** the maximum number of labels for nominal attributes. */
  protected int m_MaxLabels = 25;

  /**
   * default constructor
   */
  public ExcelLoader() {
    // No instances retrieved yet
    setRetrieval(NONE);
  }

  /**
   * Returns a string describing this loader
   *
   * @return 		a description of the evaluator suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Loads MS Excel spreadsheet files.";
  }

  /**
   * Lists the available options
   *
   * @return 		an enumeration of the available options
   */
  public Enumeration listOptions() {
    Vector<Option> result = new Vector<>();

    result.add(new Option(
      "\tEnables debug output.\n"
	+ "\t(default: off)",
      "D", 0, "-D"));

    result.add(new Option(
      "\tThe index of the worksheet to load."
	+ "\t(default: 1)",
      "sheet-index", 1, "-sheet-index <1-based index>"));

    result.add(new Option(
      "\tEnables automatically extending the header.\n"
	+ "\t(default: off)",
      "auto-extend-header", 0, "-auto-extend-header"));

    result.add(new Option(
      "\tThe range of columns to treat as text."
	+ "\t(default: none)",
      "text-columns", 1, "-text-columns <range>"));

    result.add(new Option(
      "\tIf enabled, the spreadsheet is presumed to have no header row.\n"
	+ "\t(default: off)",
      "no-header", 0, "-no-header"));

    result.add(new Option(
      "\tThe headers to use instead (comma-separated list)."
	+ "\t(default: none)",
      "custom-column-headers", 1, "-custom-column-headers <comma-separated list>"));

    result.add(new Option(
      "\tThe first row in the spreadsheet (starts at 1)."
	+ "\t(default: 1)",
      "first-row", 1, "-first-row <index>"));

    result.add(new Option(
      "\tThe number of rows to read, read all if <1."
	+ "\t(default: 0)",
      "num-rows", 1, "-num-rows <count>"));

    result.add(new Option(
      "\tThe regular expression for identifying missing values."
	+ "\t(default: " + DEFAULT_MISSING_VALUE.getValue() + ")",
      "missing-value", 1, "-missing-value <regexp>"));

    result.add(new Option(
      "\tThe maximum number of labels for nominal attributes before "
	+ "\tthey get converted to string."
	+ "\t(default: 25)",
      "max-labels", 1, "-max-labels <int>"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the options
   * @throws Exception if options cannot be set
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    setDebug(Utils.getFlag("D", options));

    tmpStr = Utils.getOption("sheet-index", options);
    if (!tmpStr.isEmpty())
      setSheetIndex(new Index(tmpStr));
    else
      setSheetIndex(new Index("1"));

    setAutoExtendHeader(Utils.getFlag("auto-extend-header", options));

    tmpStr = Utils.getOption("text-columns", options);
    if (!tmpStr.isEmpty())
      setTextColumns(new Range(tmpStr));
    else
      setTextColumns(new Range());

    setNoHeader(Utils.getFlag("no-header", options));

    tmpStr = Utils.getOption("custom-column-headers", options);
    if (!tmpStr.isEmpty())
      setCustomColumnHeaders(tmpStr);
    else
      setCustomColumnHeaders("");

    tmpStr = Utils.getOption("first-row", options);
    if (!tmpStr.isEmpty())
      setFirstRow(Integer.parseInt(tmpStr));
    else
      setFirstRow(1);

    tmpStr = Utils.getOption("num-rows", options);
    if (!tmpStr.isEmpty())
      setNumRows(Integer.parseInt(tmpStr));
    else
      setNumRows(0);

    tmpStr = Utils.getOption("missing-value", options);
    if (!tmpStr.isEmpty())
      setMissingValue(new BaseRegExp(tmpStr));
    else
      setMissingValue(DEFAULT_MISSING_VALUE);

    tmpStr = Utils.getOption("max-labels", options);
    if (!tmpStr.isEmpty())
      setMaxLabels(Integer.parseInt(tmpStr));
    else
      setMaxLabels(25);
  }

  /**
   * Gets the setting
   *
   * @return the current setting
   */
  public String[] getOptions() {
    Vector<String> options = new Vector<>();

    if (getDebug())
      options.add("-D");

    options.add("-sheet-index");
    options.add(m_SheetIndex.getIndex());

    if (getAutoExtendHeader())
      options.add("-auto-extend-header");

    options.add("-text-columns");
    options.add(getTextColumns().getRange());

    if (getNoHeader())
      options.add("-no-header");

    options.add("-custom-column-headers");
    options.add(getCustomColumnHeaders());

    options.add("-first-row");
    options.add("" + getFirstRow());

    options.add("-num-rows");
    options.add("" + getNumRows());

    options.add("-missing-value");
    options.add(getMissingValue().getValue());

    options.add("-max-labels");
    options.add("" + getMaxLabels());

    return options.toArray(new String[0]);
  }

  /**
   * Sets whether to print some debug information.
   *
   * @param value	if true additional debug information will be printed.
   */
  public void setDebug(boolean value) {
    m_Debug = value;
  }

  /**
   * Gets whether additional debug information is printed.
   *
   * @return		true if additional debug information is printed
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * the tip text for this property
   *
   * @return 		the tip text
   */
  public String debugTipText(){
    return "Whether to print additional debug information to the console.";
  }

  /**
   * Sets the index of the sheet to load.
   *
   * @param value	the index
   */
  public void setSheetIndex(Index value) {
    m_SheetIndex = value;
  }

  /**
   * Returns the index of the sheet to load.
   *
   * @return 		the index
   */
  public Index getSheetIndex() {
    return m_SheetIndex;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String sheetIndexTipText() {
    return "The index of the worksheet to load.";
  }

  /**
   * Sets whether to automatically extend the header if there are more columns present.
   *
   * @param value	true if to extend
   */
  public void setAutoExtendHeader(boolean value) {
    m_AutoExtendHeader = value;
  }

  /**
   * Returns whether to automatically extend the header if there are more columns present.
   *
   * @return 		the reader in use.
   */
  public boolean getAutoExtendHeader() {
    return m_AutoExtendHeader;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String autoExtendHeaderTipText() {
    return "If enabled, automatically extends the header if there are more columns present.";
  }

  /**
   * Sets the range of columns to treat as text/string.
   *
   * @param value	the range
   */
  public void setTextColumns(Range value) {
    m_TextColumns = value;
  }

  /**
   * Returns the range of columns to treat as text/string.
   *
   * @return 		the range
   */
  public Range getTextColumns() {
    return m_TextColumns;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String textColumnsTipText() {
    return "The range of columns to treat as text/string.";
  }

  /**
   * Sets whether there is now header row in the worksheet.
   *
   * @param value	true if no header row
   */
  public void setNoHeader(boolean value) {
    m_NoHeader = value;
  }

  /**
   * Returns whether there is now header row in the worksheet
   *
   * @return 		true if no header row
   */
  public boolean getNoHeader() {
    return m_NoHeader;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String noHeaderTipText() {
    return "Enable if there is no header row in the worksheet.";
  }

  /**
   * Sets the custom headers to use.
   *
   * @param value	the headers (comma-separated list)
   */
  public void setCustomColumnHeaders(String value) {
    m_CustomColumnHeaders = value;
  }

  /**
   * Returns the custom headers to use.
   *
   * @return 		the headers (comma-separated list)
   */
  public String getCustomColumnHeaders() {
    return m_CustomColumnHeaders;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String customColumnHeadersTipText() {
    return "The comma-separated list of custom headers to use.";
  }

  /**
   * Sets the first row in the worksheet to read.
   *
   * @param value	the row (1-based)
   */
  public void setFirstRow(int value) {
    m_FirstRow = value;
  }

  /**
   * Returns the first row in the worksheet to read.
   *
   * @return 		the row (1-based)
   */
  public int getFirstRow() {
    return m_FirstRow;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String firstRowTipText() {
    return "The first row in the worksheet to read (1-based).";
  }

  /**
   * Sets the number of rows to read.
   *
   * @param value	the number of rows, <1 for all
   */
  public void setNumRows(int value) {
    m_NumRows = value;
  }

  /**
   * Returns the number of rows to read.
   *
   * @return 		the number of rows, <1 for all
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String numRowsTipText() {
    return "The number of rows to read, <1 for all.";
  }

  /**
   * Sets the regular expression for identifying missing value.
   *
   * @param value	the regexp
   */
  public void setMissingValue(BaseRegExp value) {
    m_MissingValue = value;
  }

  /**
   * Returns the regular expression for identifying missing values.
   *
   * @return 		the regexp
   */
  public BaseRegExp getMissingValue() {
    return m_MissingValue;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String missingValueTipText() {
    return "The regular expression for identifying missing values.";
  }

  /**
   * Sets the maximum number of labels for nominal attributes before they get converted to string.
   *
   * @param value	the maximum
   */
  public void setMaxLabels(int value) {
    m_MaxLabels = value;
  }

  /**
   * Returns the maximum number of labels for nominal attributes before they get converted to string.
   *
   * @return 		the maximum
   */
  public int getMaxLabels() {
    return m_MaxLabels;
  }

  /**
   * The tip text for this property.
   *
   * @return            the tip text
   */
  public String maxLabelsTipText() {
    return "The maximum number of labels for nominal attribute before they get converted to string.";
  }

  /**
   * Returns a description of the file type.
   *
   * @return 		a short file description
   */
  public String getFileDescription() {
    return "MS Excel files";
  }

  /**
   * Get the file extension used for this type of file
   *
   * @return the file extension
   */
  @Override
  public String getFileExtension() {
    return getFileExtensions()[0];
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new String[]{".xls", ".xlsx", ".xlsm"};
  }

  /**
   * Resets the loader ready to read a new data set
   */
  @Override
  public void reset() throws IOException {
    super.reset();

    m_structure = null;
    m_Data      = null;
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied File object.
   *
   * @param file 		the source file.
   * @throws IOException 	if an error occurs
   */
  @Override
  public void setSource(File file) throws IOException {
    File original = file;
    m_structure = null;

    setRetrieval(NONE);

    if (file == null)
      throw new IOException("Source file object is null!");

    String fName = file.getPath();
    try {
      if (m_env == null) {
	m_env = Environment.getSystemWide();
      }
      fName = m_env.substitute(fName);
    } catch (Exception e) {
      // ignore any missing environment variables at this time
      // as it is possible that these may be set by the time
      // the actual file is processed
    }
    file = new File(fName);
    // set the source only if the file exists
    if (file.exists() && file.isFile())
      m_sourceFile = file;
    else
      throw new IOException("File '" + file + "' not found or not an actual file!");

    if (m_useRelativePath) {
      try {
	m_sourceFile = Utils.convertToRelativePath(original);
	m_File = m_sourceFile.getPath();
      } catch (Exception ex) {
	//        System.err.println("[AbstractFileLoader] can't convert path to relative path.");
	m_sourceFile = original;
	m_File       = m_sourceFile.getPath();
      }
    } else {
      m_sourceFile = original;
      m_File       = m_sourceFile.getPath();
    }
  }

  /**
   * Turns a numeric cell into a string. Tries to use "long" representation
   * if possible.
   *
   * @param cell	the cell to process
   * @return		the string representation
   */
  protected String numericToString(Cell cell) {
    double	dbl;
    long	lng;

    dbl = cell.getNumericCellValue();
    lng = (long) dbl;
    if (dbl == lng)
      return "" + lng;
    else
      return "" + dbl;
  }

  /**
   * Fixes the header, if necessary, by adding a dummy column name.
   *
   * @param header 	the header to fix
   */
  protected void fixHeader(List<String> header) {
    int		i;

    for (i = 0; i < header.size(); i++) {
      if (header.get(i).isEmpty())
	header.set(i, "header-" + (i+1));
    }
  }

  /**
   * Fixes the number of cells in the rows, if necessary, by adding null values.
   *
   * @param numColumns 	the number of columns in the dataset
   * @param data 	the data to fix
   */
  protected void fixRows(int numColumns, List<List<Object>> data) {
    int		i;

    for (i = 0; i < data.size(); i++) {
      while (data.get(i).size() < numColumns)
	data.get(i).add(null);
    }
  }

  /**
   * Fixes the columns types, if necessary.
   *
   * @param header 	the column names
   * @param data 	the data to infer the types from
   * @return 		the attributes
   */
  protected ArrayList<Attribute> determineAttributes(List<String> header, List<List<Object>> data) {
    ArrayList<Attribute>	result;
    int				i;
    int				n;
    int				t;
    Object			value;
    Set<Object> 		values;
    List<String>		labels;

    result = new ArrayList<>();

    for (i = 0; i < header.size(); i++) {
      t      = Attribute.NUMERIC;
      values = new HashSet<>();
      for (n = 0; n < data.size(); n++) {
	value = data.get(n).get(i);

	// missing?
	if (value == null)
	  continue;

	// record value
	values.add(value);

	// check type
	switch (t) {
	  case Attribute.NUMERIC:
	    if (value instanceof Date) {
	      t = Attribute.DATE;
	      continue;
	    }
	    if (value instanceof String) {
	      t = Attribute.NOMINAL;
	      continue;
	    }
	    if (value instanceof Boolean) {
	      t = Attribute.NOMINAL;
	      continue;
	    }
	    break;
	  case Attribute.DATE:
	    if (value instanceof String) {
	      t = Attribute.NOMINAL;
	      continue;
	    }
	    if (value instanceof Boolean) {
	      t = Attribute.NOMINAL;
	      continue;
	    }
	    break;
	}
      }

      if (t == Attribute.NOMINAL) {
	if (values.size() > m_MaxLabels)
	  t = Attribute.STRING;
      }

      switch (t) {
	case Attribute.NUMERIC:
	  result.add(new Attribute(header.get(i)));
	  break;
	case Attribute.DATE:
	  result.add(new Attribute(header.get(i), DateUtils.getTimestampFormatter().toPattern()));
	  break;
	case Attribute.NOMINAL:
	  labels = new ArrayList<>();
	  for (Object o: values.toArray())
	    labels.add("" + o);
	  Collections.sort(labels);
	  result.add(new Attribute(header.get(i), labels));
	  break;
	case Attribute.STRING:
	  result.add(new Attribute(header.get(i), (List<String>) null));
	  break;
	default:
	  throw new IllegalStateException("Unhandled attribute type: " + Attribute.typeToString(t));
      }
    }

    return result;
  }

  /**
   * Converts the header/data to instances.
   *
   * @param atts 	the attributes
   * @param data	the data
   * @return		the generated data
   */
  protected Instances convert(ArrayList<Attribute> atts, List<List<Object>> data) {
    Instances	result;
    Instance	inst;
    Object	value;
    double[]	values;
    int		i;

    result = new Instances(m_sourceFile.getName(), atts, data.size());

    for (List<Object> row: data) {
      values = new double[row.size()];
      Arrays.fill(values, Utils.missingValue());
      for (i = 0; i < row.size(); i++) {
	value = row.get(i);
	if (value == null)
	  continue;
	switch (result.attribute(i).type()) {
	  case Attribute.NUMERIC:
	    values[i] = (Double) value;
	    break;
	  case Attribute.DATE:
	    values[i] = ((Date) value).getTime();
	    break;
	  case Attribute.NOMINAL:
	    values[i] = result.attribute(i).indexOfValue("" + value);
	    break;
	  case Attribute.STRING:
	    values[i] = result.attribute(i).addStringValue("" + value);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled attribute type at col " + (i+1) + ": " + Attribute.typeToString(result.attribute(i).type()));
	}
      }
      inst = new DenseInstance(1.0, values);
      result.add(inst);
    }

    return result;
  }

  /**
   * Reads the worksheet.
   *
   * @return		the worksheet data
   */
  protected Instances readWorksheet() {
    Instances 			result;
    int				index;
    Workbook 			workbook;
    Sheet 			sheet;
    Row 			exRow;
    Cell 			exCell;
    int 			i;
    int				n;
    CellType 			cellType;
    boolean			numeric;
    int                 	dataRowStart;
    int				firstRow;
    int 			lastRow;
    Object			value;
    String			valueStr;
    List<String>        	header;
    List<List<Object>>		data;
    List<Object>		dataRow;
    ArrayList<Attribute>	atts;

    result   = null;
    workbook = null;
    try {
      workbook     = WorkbookFactory.create(m_sourceFile);
      m_SheetIndex.setMax(workbook.getNumberOfSheets());
      index        = m_SheetIndex.getIntIndex();
      firstRow     = m_FirstRow - 1;
      dataRowStart = getNoHeader() ? firstRow : firstRow + 1;

      if (getDebug())
	System.out.println("sheet: " + (index+1));

      sheet = workbook.getSheetAt(index);
      if (sheet.getLastRowNum() == 0) {
	System.err.println("No rows in sheet #" + index);
	return null;
      }

      // header
      header = new ArrayList<>();
      if (getDebug())
	System.out.println("header row");
      exRow = sheet.getRow(firstRow);
      if (exRow == null) {
	System.err.println("No data in sheet #" + (index + 1) + "?");
      }
      else {
	m_TextColumns.setMax(exRow.getLastCellNum());
	if (getNoHeader()) {
	  header = SpreadSheetUtils.createHeader(exRow.getLastCellNum(), m_CustomColumnHeaders);
	}
	else {
	  if (!m_CustomColumnHeaders.trim().isEmpty()) {
	    header = SpreadSheetUtils.createHeader(exRow.getLastCellNum(), m_CustomColumnHeaders);
	  }
	  else {
	    for (i = 0; i < exRow.getLastCellNum(); i++) {
	      exCell = exRow.getCell(i);
	      if (exCell == null) {
		header.add("?");
		continue;
	      }
	      numeric = !m_TextColumns.isInRange(i);
	      switch (exCell.getCellType()) {
		case BLANK:
		case ERROR:
		  header.add("column-" + (i + 1));
		  break;
		case NUMERIC:
		  if (DateUtil.isCellDateFormatted(exCell))
		    header.add(new DateTime(DateUtil.getJavaDate(exCell.getNumericCellValue())).toString());
		  else if (numeric)
		    header.add("" + exCell.getNumericCellValue());
		  else
		    header.add(numericToString(exCell));
		  break;
		default:
		  header.add(exCell.getStringCellValue());
	      }
	    }
	  }
	}
      }

      // data
      data = new ArrayList<>();
      if (!header.isEmpty()) {
	// the rows to read
	if (m_NumRows < 1)
	  lastRow = sheet.getLastRowNum();
	else
	  lastRow = Math.min(firstRow + m_NumRows - 1, sheet.getLastRowNum());

	for (i = dataRowStart; i <= lastRow; i++) {
	  if (getDebug())
	    System.out.println("data row: " + (i+1));
	  dataRow = new ArrayList<>();
	  for (n = 0; n < header.size(); n++)
	    dataRow.add(null);
	  data.add(dataRow);
	  exRow = sheet.getRow(i);
	  if (exRow == null)
	    continue;
	  for (n = 0; n < exRow.getLastCellNum(); n++) {
	    // too few columns in header?
	    if ((n >= header.size()) && m_AutoExtendHeader) {
	      header.add("");
	      dataRow.add(null);
	    }

	    m_TextColumns.setMax(header.size());
	    exCell = exRow.getCell(n);
	    if (exCell == null)
	      continue;
	    cellType = exCell.getCellType();
	    if (cellType == CellType.FORMULA)
	      cellType = exCell.getCachedFormulaResultType();
	    numeric = !m_TextColumns.isInRange(n);
	    switch (cellType) {
	      case BLANK:
		if (!m_MissingValue.isMatch(""))
		  dataRow.set(n, "");
		break;
	      case ERROR:
		if (exCell instanceof XSSFCell)
		  valueStr = ((XSSFCell) exCell).getErrorCellString();
		else
		  valueStr = "Error: " + exCell.getErrorCellValue();
		if (!m_MissingValue.isMatch(valueStr))
		  dataRow.set(n, valueStr);
		break;
	      case NUMERIC:
		if (DateUtil.isCellDateFormatted(exCell))
		  dataRow.set(n, DateUtil.getJavaDate(exCell.getNumericCellValue()));
		else if (numeric)
		  dataRow.set(n, exCell.getNumericCellValue());
		else
		  dataRow.set(n, numericToString(exCell));
		break;
	      case BOOLEAN:
		dataRow.set(n, exCell.getBooleanCellValue());
		break;
	      case FORMULA:
		value = null;
		// numeric?
		try {
		  value = exCell.getNumericCellValue();
		}
		catch (Exception e) {
		  // ignored
		}
		// boolean?
		if (value == null) {
		  try {
		    value = exCell.getBooleanCellValue();
		  }
		  catch (Exception e) {
		    // ignored
		  }
		}
		// date?
		if (value == null) {
		  try {
		    value = exCell.getDateCellValue();
		  }
		  catch (Exception e) {
		    // ignored
		  }
		}
		// string?
		if (value == null) {
		  try {
		    value = exCell.getStringCellValue();
		  }
		  catch (Exception e) {
		    // ignored
		  }
		}
		if (value != null)
		  dataRow.set(n, value);
		break;
	      case STRING:
		valueStr = exCell.getStringCellValue();
		if (!m_MissingValue.isMatch(valueStr))
		  dataRow.set(n, valueStr);
	      default:
		try {
		  valueStr = exCell.getStringCellValue();
		  if (!m_MissingValue.isMatch(valueStr))
		    dataRow.set(n, valueStr);
		}
		catch (Exception e) {
		  // ignored
		}
	    }
	  }
	}
      }
    }
    catch (Exception ioe) {
      System.err.println("Failed to read sheet '" + m_SheetIndex + "':");
      ioe.printStackTrace();
      header = null;
      data   = null;
    }

    if (workbook != null) {
      try {
	workbook.close();
      }
      catch (Exception e) {
	// ignored
      }
    }

    // generate instances
    if (header != null) {
      fixHeader(header);
      fixRows(header.size(), data);
      atts   = determineAttributes(header, data);
      result = convert(atts, data);
    }

    return result;
  }

  /**
   * Determines and returns (if possible) the structure (internally the 
   * header) of the data set as an empty set of instances.
   *
   * @return 			the structure of the data set as an empty 
   * 				set of Instances
   * @throws IOException 	if an error occurs
   */
  @Override
  public Instances getStructure() throws IOException {
    if (m_structure == null) {
      // load spreadsheet
      if (m_Debug)
	System.out.println("Loading data from '" + m_sourceFile + "'...");
      m_Data = readWorksheet();
      if (m_Debug)
	System.out.println("Loading complete");
      if (m_Data == null)
	throw new IOException("No data loaded!");

      // convert data
      m_structure = new Instances(m_Data, 0);
    }

    return m_structure;
  }

  /**
   * Return the full data set. If the structure hasn't yet been determined
   * by a call to getStructure then method should do so before processing
   * the rest of the data set.
   *
   * @return the structure of the data set as an empty set of Instances
   * @throws IOException if there is no source or parsing fails
   */
  @Override
  public Instances getDataSet() throws IOException {
    // make sure that data has been read
    getStructure();
    return m_Data;
  }

  /**
   * SpreadSheetLoader is unable to process a data set incrementally.
   *
   * @param structure ignored
   * @return never returns without throwing an exception
   * @throws IOException always. AdamsCsvLoader is unable to process a data
   * set incrementally.
   */
  @Override
  public Instance getNextInstance(Instances structure) throws IOException {
    throw new IOException("ExcelLoader can't read data sets incrementally.");
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method.
   *
   * @param args should contain the name of an input file.
   */
  public static void main(String[] args) {
    runFileLoader(new ExcelLoader(), args);
  }
}
