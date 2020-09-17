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
 * ObjectLocationsSpreadSheetReader.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.rowfinder.AbstractRowFinder;
import adams.data.spreadsheet.rowfinder.AllFinder;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads object locations from a spreadsheet into a report.<br>
 * Top&#47;left column is required.<br>
 * Either right&#47;bottom or width&#47;height need to be supplied.<br>
 * In addition, polygon coordinates (X and Y coordinates as comma-separated lists in two separate columns) can be read as well.<br>
 * If the coordinates&#47;dimensions represent normalized ones (ie 0-1), then specify the width&#47;height of the image to relate them back to actual pixel-based sizes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a report.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for reading the spreadsheet data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-row-finder &lt;adams.data.spreadsheet.rowfinder.RowFinder&gt; (property: rowFinder)
 * &nbsp;&nbsp;&nbsp;The row finder to use for selecting a subset before extracting object locations.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.rowfinder.AllFinder
 * </pre>
 *
 * <pre>-col-left &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colLeft)
 * &nbsp;&nbsp;&nbsp;The column containing the left coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-top &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colTop)
 * &nbsp;&nbsp;&nbsp;The column containing the top coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-right &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colRight)
 * &nbsp;&nbsp;&nbsp;The column containing the right coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-bottom &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colBottom)
 * &nbsp;&nbsp;&nbsp;The column containing the bottom coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-width &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colWidth)
 * &nbsp;&nbsp;&nbsp;The column containing the width coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-height &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colHeight)
 * &nbsp;&nbsp;&nbsp;The column containing the height coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-polygon-x &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colPolygonX)
 * &nbsp;&nbsp;&nbsp;The column containing the X coordinates of the polygon (comma-separated
 * &nbsp;&nbsp;&nbsp;list of coordinates); cannot be used without bounding box.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-polygon-y &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colPolygonY)
 * &nbsp;&nbsp;&nbsp;The column containing the Y coordinates of the polygon (comma-separated
 * &nbsp;&nbsp;&nbsp;list of coordinates); cannot be used without bounding box.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-type &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colType)
 * &nbsp;&nbsp;&nbsp;The column containing the object label.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-range-meta-data &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: rangeMetaData)
 * &nbsp;&nbsp;&nbsp;The columns to store as meta-data; all other columns get automatically excluded
 * &nbsp;&nbsp;&nbsp;from the meta-data.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-use-normalized &lt;boolean&gt; (property: useNormalized)
 * &nbsp;&nbsp;&nbsp;If enabled, the coordinates&#47;dimensions are interpreted as normalized (0-
 * &nbsp;&nbsp;&nbsp;1) rather than absolute pixels.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the image to use when reading normalized coordinates&#47;dimensions.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the image to use when reading normalized coordinates&#47;dimensions.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-label-suffix &lt;java.lang.String&gt; (property: labelSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix to use in the report for labels.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectLocationsSpreadSheetReader
  extends AbstractReportReader<Report>
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -45890668031870078L;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /** the row finder to apply before extracting the objects. */
  protected RowFinder m_RowFinder;

  /** the column with the left coordinate. */
  protected SpreadSheetColumnIndex m_ColLeft;

  /** the column with the top coordinate. */
  protected SpreadSheetColumnIndex m_ColTop;

  /** the column with the right coordinate. */
  protected SpreadSheetColumnIndex m_ColRight;

  /** the column with the bottom coordinate. */
  protected SpreadSheetColumnIndex m_ColBottom;

  /** the column with the width. */
  protected SpreadSheetColumnIndex m_ColWidth;

  /** the column with the height. */
  protected SpreadSheetColumnIndex m_ColHeight;

  /** the column with the polygon X coordinates. */
  protected SpreadSheetColumnIndex m_ColPolygonX;

  /** the column with the polygon Y coordinates. */
  protected SpreadSheetColumnIndex m_ColPolygonY;

  /** the column with the label. */
  protected SpreadSheetColumnIndex m_ColType;

  /** the columns with meta-data. */
  protected SpreadSheetColumnRange m_RangeMetaData;

  /** whether to use normalized coordinates/dimensions. */
  protected boolean m_UseNormalized;

  /** the image width to use as basis for normalized coordinates/dimensions. */
  protected int m_Width;

  /** the image height to use as basis for normalized coordinates/dimensions. */
  protected int m_Height;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads object locations from a spreadsheet into a report.\n"
      + "Top/left column is required.\n"
      + "Either right/bottom or width/height need to be supplied.\n"
      + "In addition, polygon coordinates (X and Y coordinates as comma-separated "
      + "lists in two separate columns) can be read as well.\n"
      + "If the coordinates/dimensions represent normalized ones (ie 0-1), "
      + "then specify the width/height of the image to relate them back to "
      + "actual pixel-based sizes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new CsvSpreadSheetReader());

    m_OptionManager.add(
      "row-finder", "rowFinder",
      new AllFinder());

    m_OptionManager.add(
      "col-left", "colLeft",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-top", "colTop",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-right", "colRight",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-bottom", "colBottom",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-width", "colWidth",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-height", "colHeight",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-polygon-x", "colPolygonX",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-polygon-y", "colPolygonY",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "col-type", "colType",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "range-meta-data", "rangeMetaData",
      new SpreadSheetColumnRange());

    m_OptionManager.add(
      "use-normalized", "useNormalized",
      false);

    m_OptionManager.add(
      "width", "width",
      1000, 1, null);

    m_OptionManager.add(
      "height", "height",
      1000, 1, null);

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "label-suffix", "labelSuffix",
      "type");
  }

  /**
   * Sets the spreadsheet reader to use.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader in use.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for reading the spreadsheet data.";
  }

  /**
   * Sets the row finder to use for selecting a subset before extracting object locations.
   *
   * @param value	the finder
   */
  public void setRowFinder(RowFinder value) {
    m_RowFinder = value;
    reset();
  }

  /**
   * Returns the row finder to use for selecting a subset before extracting object locations.
   *
   * @return		the finder
   */
  public RowFinder getRowFinder() {
    return m_RowFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowFinderTipText() {
    return "The row finder to use for selecting a subset before extracting object locations.";
  }

  /**
   * Sets the column containing the left coordinate.
   *
   * @param value	the column
   */
  public void setColLeft(SpreadSheetColumnIndex value) {
    m_ColLeft = value;
    reset();
  }

  /**
   * Returns the column containing the left coordinate.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColLeft() {
    return m_ColLeft;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colLeftTipText() {
    return "The column containing the left coordinate.";
  }

  /**
   * Sets the column containing the top coordinate.
   *
   * @param value	the column
   */
  public void setColTop(SpreadSheetColumnIndex value) {
    m_ColTop = value;
    reset();
  }

  /**
   * Returns the column containing the top coordinate.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColTop() {
    return m_ColTop;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colTopTipText() {
    return "The column containing the top coordinate.";
  }

  /**
   * Sets the column containing the right coordinate.
   *
   * @param value	the column
   */
  public void setColRight(SpreadSheetColumnIndex value) {
    m_ColRight = value;
    reset();
  }

  /**
   * Returns the column containing the right coordinate.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColRight() {
    return m_ColRight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colRightTipText() {
    return "The column containing the right coordinate.";
  }

  /**
   * Sets the column containing the bottom coordinate.
   *
   * @param value	the column
   */
  public void setColBottom(SpreadSheetColumnIndex value) {
    m_ColBottom = value;
    reset();
  }

  /**
   * Returns the column containing the bottom coordinate.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColBottom() {
    return m_ColBottom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colBottomTipText() {
    return "The column containing the bottom coordinate.";
  }

  /**
   * Sets the column containing the width coordinate.
   *
   * @param value	the column
   */
  public void setColWidth(SpreadSheetColumnIndex value) {
    m_ColWidth = value;
    reset();
  }

  /**
   * Returns the column containing the width coordinate.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColWidth() {
    return m_ColWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colWidthTipText() {
    return "The column containing the width coordinate.";
  }

  /**
   * Sets the column containing the height coordinate.
   *
   * @param value	the column
   */
  public void setColHeight(SpreadSheetColumnIndex value) {
    m_ColHeight = value;
    reset();
  }

  /**
   * Returns the column containing the height coordinate.
   * Cannot be used without bounding box.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColHeight() {
    return m_ColHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colHeightTipText() {
    return "The column containing the height coordinate.";
  }

  /**
   * Sets the column containing the X coordinates of the polygon (comma-separated list of coordinates).
   * Cannot be used without bounding box.
   *
   * @param value	the column
   */
  public void setColPolygonX(SpreadSheetColumnIndex value) {
    m_ColPolygonX = value;
    reset();
  }

  /**
   * Returns the column containing the X coordinates of the polygon (comma-separated list of coordinates).
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColPolygonX() {
    return m_ColPolygonX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colPolygonXTipText() {
    return "The column containing the X coordinates of the polygon (comma-separated list of coordinates); cannot be used without bounding box.";
  }

  /**
   * Sets the column containing the Y coordinates of the polygon (comma-separated list of coordinates).
   * Cannot be used without bounding box.
   *
   * @param value	the column
   */
  public void setColPolygonY(SpreadSheetColumnIndex value) {
    m_ColPolygonY = value;
    reset();
  }

  /**
   * Returns the column containing the Y coordinates of the polygon (comma-separated list of coordinates).
   * Cannot be used without bounding box.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColPolygonY() {
    return m_ColPolygonY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colPolygonYTipText() {
    return "The column containing the Y coordinates of the polygon (comma-separated list of coordinates); cannot be used without bounding box.";
  }

  /**
   * Sets the column containing the object label.
   *
   * @param value	the column
   */
  public void setColType(SpreadSheetColumnIndex value) {
    m_ColType = value;
    reset();
  }

  /**
   * Returns the column containing the object label.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColType() {
    return m_ColType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colTypeTipText() {
    return "The column containing the object label.";
  }

  /**
   * Sets whether the coordinates/dimensions are interpreted as normalized (0-1) 
   * rather than absolute pixels.
   *
   * @param value	true if normalized
   */
  public void setUseNormalized(boolean value) {
    m_UseNormalized = value;
    reset();
  }

  /**
   * Returns whether the coordinates/dimensions are interpreted as normalized (0-1) 
   * rather than absolute pixels.
   *
   * @return		true if normalized
   */
  public boolean getUseNormalized() {
    return m_UseNormalized;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useNormalizedTipText() {
    return "If enabled, the coordinates/dimensions are interpreted as normalized (0-1) rather than absolute pixels.";
  }

  /**
   * Sets the width of the image to use when reading normalized coordinates/dimensions.
   *
   * @param value	the image width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width of the image to use when reading normalized coordinates/dimensions.
   *
   * @return		the image width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the image to use when reading normalized coordinates/dimensions.";
  }

  /**
   * Sets the height of the image to use when reading normalized coordinates/dimensions.
   *
   * @param value	the image height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height of the image to use when reading normalized coordinates/dimensions.
   *
   * @return		the image height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the image to use when reading normalized coordinates/dimensions.";
  }

  /**
   * Sets the range of columns to store as meta-data.
   *
   * @param value	the column range
   */
  public void setRangeMetaData(SpreadSheetColumnRange value) {
    m_RangeMetaData = value;
    reset();
  }

  /**
   * Returns the range of columns to store as meta-data.
   *
   * @return		the column range
   */
  public SpreadSheetColumnRange getRangeMetaData() {
    return m_RangeMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeMetaDataTipText() {
    return "The columns to store as meta-data; all other columns get automatically excluded from the meta-data.";
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Sets the field suffix used in the report for labels.
   *
   * @param value 	the field suffix
   */
  public void setLabelSuffix(String value) {
    m_LabelSuffix = value;
    reset();
  }

  /**
   * Returns the field suffix used in the report for labels.
   *
   * @return 		the field suffix
   */
  public String getLabelSuffix() {
    return m_LabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelSuffixTipText() {
    return "The suffix to use in the report for labels.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Object locations";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"*"};
  }

  /**
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  @Override
  public Report newInstance() {
    return new Report();
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return -1;
  }

  /**
   * Parses comma-separated list of coordinates and returns a double array.
   *
   * @param coords	the coordinates to parse
   * @return		the parsed values, empty array if failed to parse
   */
  protected double[] parseCoords(String coords) {
    TDoubleList		result;

    result = new TDoubleArrayList();
    try {
      for (String coord: coords.split(","))
        result.add(Double.parseDouble(coord));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse coordinates '" + coords + "'!", e);
      result = new TDoubleArrayList();
    }

    return result.toArray();
  }

  /**
   * Multiplies the coordinates by the specified factor (in-place).
   *
   * @param coords	the coordinates to update
   * @param factor	the factor to use
   * @return		the updated coordinates
   */
  protected double[] multipleCoords(double[] coords, double factor) {
    int		i;

    for (i = 0; i < coords.length; i++)
      coords[i] = coords[i] * factor;

    return coords;
  }

  /**
   * Turns the coordinates into a comma-separated list.
   *
   * @param coords	the coordinates to convert
   * @return		the comma-separated list
   */
  protected String coordsToList(double[] coords) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < coords.length; i++) {
      if (i > 0)
        result.append(",");
      result.append("" + coords[i]);
    }

    return result.toString();
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report> 	result;
    SpreadSheet		sheet;
    int			left;
    int			top;
    int			right;
    int			bottom;
    int			width;
    int			height;
    int			polyX;
    int			polyY;
    int			type;
    int[]		meta;
    TIntSet		metaSet;
    LocatedObjects	objects;
    LocatedObject	object;

    result = new ArrayList<>();
    sheet  = m_Reader.read(m_Input);

    // create subset?
    if (!(m_RowFinder instanceof AllFinder))
      sheet = AbstractRowFinder.filter(sheet, m_RowFinder);

    m_ColLeft.setData(sheet);
    left = m_ColLeft.getIntIndex();
    if (left == -1) {
      getLogger().severe("Failed to locate 'left' column: " + m_ColLeft.getIndex());
      return result;
    }

    m_ColTop.setData(sheet);
    top = m_ColTop.getIntIndex();
    if (top == -1) {
      getLogger().severe("Failed to locate 'top' column: " + m_ColTop.getIndex());
      return result;
    }

    m_ColRight.setData(sheet);
    right = m_ColRight.getIntIndex();
    m_ColBottom.setData(sheet);
    bottom = m_ColBottom.getIntIndex();
    m_ColWidth.setData(sheet);
    width = m_ColWidth.getIntIndex();
    m_ColHeight.setData(sheet);
    height = m_ColHeight.getIntIndex();
    m_ColPolygonX.setData(sheet);
    polyX = m_ColPolygonX.getIntIndex();
    m_ColPolygonY.setData(sheet);
    polyY = m_ColPolygonY.getIntIndex();
    m_ColType.setData(sheet);
    type = m_ColType.getIntIndex();

    if (right == -1) {
      if (width == -1) {
	getLogger().severe("If 'right' column is not defined, then 'width' must be: " + m_ColWidth.getIndex());
	return result;
      }
    }
    else {
      if (bottom == -1) {
	getLogger().severe("If 'right' column is defined, then 'bottom' must be as well: " + m_ColBottom.getIndex());
	return result;
      }
    }
    if (bottom == -1) {
      if (height == -1) {
	getLogger().severe("If 'bottom' column is not defined, then 'height' must be: " + m_ColHeight.getIndex());
	return result;
      }
    }
    else {
      if (right == -1) {
	getLogger().severe("If 'bottom' column is defined, then 'right' must be as well: " + m_ColRight.getIndex());
	return result;
      }
    }

    m_RangeMetaData.setData(sheet);
    meta = m_RangeMetaData.getIntIndices();
    // ensure that other columns aren't included in meta-data
    metaSet = new TIntHashSet(meta);
    metaSet.remove(left);
    metaSet.remove(top);
    metaSet.remove(right);
    metaSet.remove(bottom);
    metaSet.remove(width);
    metaSet.remove(height);
    metaSet.remove(polyX);
    metaSet.remove(polyY);
    metaSet.remove(type);
    meta = metaSet.toArray();

    objects = new LocatedObjects();
    for (Row row: sheet.rows()) {
      if (m_UseNormalized) {
	if (width != -1) {
	  object = new LocatedObject(
	    (int) (row.getCell(left).toDouble() * m_Width),
	    (int) (row.getCell(top).toDouble() * m_Height),
	    (int) (row.getCell(width).toDouble() * m_Width),
	    (int) (row.getCell(height).toDouble() * m_Height));
	}
	else {
	  object = new LocatedObject(
	    (int) (row.getCell(left).toDouble() * m_Width),
	    (int) (row.getCell(top).toDouble() * m_Height),
	    (int) (row.getCell(right).toDouble() * m_Width - row.getCell(left).toDouble() * m_Width),
	    (int) (row.getCell(bottom).toDouble() * m_Height - row.getCell(top).toDouble() * m_Height));
	}
	if ((polyX != -1) && row.hasCell(polyX) && !row.getCell(polyX).isMissing())
	  object.getMetaData().put(
	    LocatedObject.KEY_POLY_X,
	    coordsToList(multipleCoords(parseCoords(row.getCell(polyX).getContent()), m_Width)));
	if ((polyY != -1) && row.hasCell(polyY) && !row.getCell(polyY).isMissing())
	  object.getMetaData().put(
	    LocatedObject.KEY_POLY_Y,
	    coordsToList(multipleCoords(parseCoords(row.getCell(polyY).getContent()), m_Height)));
      }
      else {
	if (width != -1) {
	  object = new LocatedObject(
	    row.getCell(left).toDouble().intValue(),
	    row.getCell(top).toDouble().intValue(),
	    row.getCell(width).toDouble().intValue(),
	    row.getCell(height).toDouble().intValue());
	}
	else {
	  object = new LocatedObject(
	    row.getCell(left).toDouble().intValue(),
	    row.getCell(top).toDouble().intValue(),
	    row.getCell(right).toDouble().intValue() - row.getCell(left).toDouble().intValue() + 1,
	    row.getCell(bottom).toDouble().intValue() - row.getCell(top).toDouble().intValue() + 1);
	}
	if ((polyX != -1) && row.hasCell(polyX) && !row.getCell(polyX).isMissing())
	  object.getMetaData().put(
	    LocatedObject.KEY_POLY_X,
	    coordsToList(parseCoords(row.getCell(polyX).getContent())));
	if ((polyY != -1) && row.hasCell(polyY) && !row.getCell(polyY).isMissing())
	  object.getMetaData().put(
	    LocatedObject.KEY_POLY_Y,
	    coordsToList(parseCoords(row.getCell(polyY).getContent())));
      }
      for (int m: meta)
        object.getMetaData().put(sheet.getColumnName(m), row.getCell(m).getNative());
      if (type != -1)
        object.getMetaData().put(m_LabelSuffix, row.getCell(type).getContent());
      objects.add(object);
    }

    result.add(objects.toReport(m_Prefix));

    return result;
  }
}
