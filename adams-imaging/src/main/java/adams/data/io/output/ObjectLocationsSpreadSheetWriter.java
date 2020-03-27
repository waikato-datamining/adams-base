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
 * ObjectLocationsSpreadSheetWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.base.BaseString;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 *
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the report to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.*
 * </pre>
 *
 * <pre>-writer &lt;adams.data.io.output.SpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for writing the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use for selecting a subset of objects before generating
 * &nbsp;&nbsp;&nbsp;the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-col-left &lt;java.lang.String&gt; (property: colLeft)
 * &nbsp;&nbsp;&nbsp;The column containing the left coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-top &lt;java.lang.String&gt; (property: colTop)
 * &nbsp;&nbsp;&nbsp;The column containing the top coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-right &lt;java.lang.String&gt; (property: colRight)
 * &nbsp;&nbsp;&nbsp;The column containing the right coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-bottom &lt;java.lang.String&gt; (property: colBottom)
 * &nbsp;&nbsp;&nbsp;The column containing the bottom coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-width &lt;java.lang.String&gt; (property: colWidth)
 * &nbsp;&nbsp;&nbsp;The column containing the width coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-height &lt;java.lang.String&gt; (property: colHeight)
 * &nbsp;&nbsp;&nbsp;The column containing the height coordinate.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-polygon-x &lt;java.lang.String&gt; (property: colPolygonX)
 * &nbsp;&nbsp;&nbsp;The column containing the X coordinates of the polygon (comma-separated
 * &nbsp;&nbsp;&nbsp;list of coordinates); cannot be used without bounding box.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-polygon-y &lt;java.lang.String&gt; (property: colPolygonY)
 * &nbsp;&nbsp;&nbsp;The column containing the Y coordinates of the polygon (comma-separated
 * &nbsp;&nbsp;&nbsp;list of coordinates); cannot be used without bounding box.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-col-type &lt;java.lang.String&gt; (property: colType)
 * &nbsp;&nbsp;&nbsp;The column containing the object label.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-meta-data-key-type &lt;java.lang.String&gt; (property: metaDataKeyType)
 * &nbsp;&nbsp;&nbsp;The meta-data key for the type (= label).
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-meta-data-keys &lt;adams.core.base.BaseString&gt; [-meta-data-keys ...] (property: metaDataKeys)
 * &nbsp;&nbsp;&nbsp;The keys of the meta-data values to output as well (keys are used as column
 * &nbsp;&nbsp;&nbsp;names).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-output-normalized &lt;boolean&gt; (property: outputNormalized)
 * &nbsp;&nbsp;&nbsp;If enabled, normalized coordinates&#47;dimensions (0-1) are output as well with
 * &nbsp;&nbsp;&nbsp;a 'n' suffix in the column name.
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectLocationsSpreadSheetWriter
  extends AbstractReportWriter<Report> {

  private static final long serialVersionUID = -199610824853876294L;

  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer;

  /** the row finder to apply before extracting the objects. */
  protected ObjectFinder m_Finder;

  /** the column with the left coordinate. */
  protected String m_ColLeft;

  /** the column with the top coordinate. */
  protected String m_ColTop;

  /** the column with the right coordinate. */
  protected String m_ColRight;

  /** the column with the bottom coordinate. */
  protected String m_ColBottom;

  /** the column with the width. */
  protected String m_ColWidth;

  /** the column with the height. */
  protected String m_ColHeight;

  /** the column with the polygon X coordinates. */
  protected String m_ColPolygonX;

  /** the column with the polygon Y coordinates. */
  protected String m_ColPolygonY;

  /** the column with the label. */
  protected String m_ColType;

  /** the meta-data key for the type. */
  protected String m_MetaDataKeyType;

  /** the meta-data keys to output as well (comma-separated). */
  protected BaseString[] m_MetaDataKeys;

  /** whether to use normalized coordinates/dimensions. */
  protected boolean m_OutputNormalized;

  /** the image width to use as basis for normalized coordinates/dimensions. */
  protected int m_Width;

  /** the image height to use as basis for normalized coordinates/dimensions. */
  protected int m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return null;
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

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "col-left", "colLeft",
      "");

    m_OptionManager.add(
      "col-top", "colTop",
      "");

    m_OptionManager.add(
      "col-right", "colRight",
      "");

    m_OptionManager.add(
      "col-bottom", "colBottom",
      "");

    m_OptionManager.add(
      "col-width", "colWidth",
      "");

    m_OptionManager.add(
      "col-height", "colHeight",
      "");

    m_OptionManager.add(
      "col-polygon-x", "colPolygonX",
      "");

    m_OptionManager.add(
      "col-polygon-y", "colPolygonY",
      "");

    m_OptionManager.add(
      "col-type", "colType",
      "");

    m_OptionManager.add(
      "meta-data-key-type", "metaDataKeyType",
      "type");

    m_OptionManager.add(
      "meta-data-keys", "metaDataKeys",
      new BaseString[0]);

    m_OptionManager.add(
      "output-normalized", "outputNormalized",
      false);

    m_OptionManager.add(
      "width", "width",
      1000, 1, null);

    m_OptionManager.add(
      "height", "height",
      1000, 1, null);
  }

  /**
   * Sets the spreadsheet writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the spreadsheet writer in use.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for writing the spreadsheet.";
  }

  /**
   * Sets the row finder to use for selecting a subset before extracting object locations.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the row finder to use for selecting a subset before extracting object locations.
   *
   * @return		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use for selecting a subset of objects before generating the spreadsheet.";
  }

  /**
   * Sets the column containing the left coordinate.
   *
   * @param value	the column
   */
  public void setColLeft(String value) {
    m_ColLeft = value;
    reset();
  }

  /**
   * Returns the column containing the left coordinate.
   *
   * @return		the column
   */
  public String getColLeft() {
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
  public void setColTop(String value) {
    m_ColTop = value;
    reset();
  }

  /**
   * Returns the column containing the top coordinate.
   *
   * @return		the column
   */
  public String getColTop() {
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
  public void setColRight(String value) {
    m_ColRight = value;
    reset();
  }

  /**
   * Returns the column containing the right coordinate.
   *
   * @return		the column
   */
  public String getColRight() {
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
  public void setColBottom(String value) {
    m_ColBottom = value;
    reset();
  }

  /**
   * Returns the column containing the bottom coordinate.
   *
   * @return		the column
   */
  public String getColBottom() {
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
  public void setColWidth(String value) {
    m_ColWidth = value;
    reset();
  }

  /**
   * Returns the column containing the width coordinate.
   *
   * @return		the column
   */
  public String getColWidth() {
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
  public void setColHeight(String value) {
    m_ColHeight = value;
    reset();
  }

  /**
   * Returns the column containing the height coordinate.
   * Cannot be used without bounding box.
   *
   * @return		the column
   */
  public String getColHeight() {
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
  public void setColPolygonX(String value) {
    m_ColPolygonX = value;
    reset();
  }

  /**
   * Returns the column containing the X coordinates of the polygon (comma-separated list of coordinates).
   *
   * @return		the column
   */
  public String getColPolygonX() {
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
  public void setColPolygonY(String value) {
    m_ColPolygonY = value;
    reset();
  }

  /**
   * Returns the column containing the Y coordinates of the polygon (comma-separated list of coordinates).
   * Cannot be used without bounding box.
   *
   * @return		the column
   */
  public String getColPolygonY() {
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
  public void setColType(String value) {
    m_ColType = value;
    reset();
  }

  /**
   * Returns the column containing the object label.
   *
   * @return		the column
   */
  public String getColType() {
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
   * Sets the meta-data key containing the object label.
   *
   * @param value	the key
   */
  public void setMetaDataKeyType(String value) {
    m_MetaDataKeyType = value;
    reset();
  }

  /**
   * Returns the meta-data key containing the object label.
   *
   * @return		the key
   */
  public String getMetaDataKeyType() {
    return m_MetaDataKeyType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeyTypeTipText() {
    return "The meta-data key for the type (= label).";
  }

  /**
   * Sets whether to output normalized (0-1) coordinates/dimensions as well
   * (with 'n' column suffix).
   *
   * @param value	true if to output
   */
  public void setOutputNormalized(boolean value) {
    m_OutputNormalized = value;
    reset();
  }

  /**
   * Returns whether to output normalized (0-1) coordinates/dimensions as well
   * (with 'n' column suffix).
   *
   * @return		true if to output
   */
  public boolean getOutputNormalized() {
    return m_OutputNormalized;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputNormalizedTipText() {
    return "If enabled, normalized coordinates/dimensions (0-1) are output as well with a 'n' suffix in the column name.";
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
   * Sets the meta-data keys to output as well.
   *
   * @param value	the column range
   */
  public void setMetaDataKeys(BaseString[] value) {
    m_MetaDataKeys = value;
    reset();
  }

  /**
   * Returns the range of columns to store as meta-data.
   *
   * @return		the column range
   */
  public BaseString[] getMetaDataKeys() {
    return m_MetaDataKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeysTipText() {
    return "The keys of the meta-data values to output as well (keys are used as column names).";
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
   * Normalizes the x value.
   *
   * @param x		the value to normalize
   * @return		the normalized value
   */
  protected double normalizeX(int x) {
    return (double) x / (double) m_Width;
  }

  /**
   * Normalizes the x values.
   *
   * @param x		the values to normalize
   * @return		the normalized values
   */
  protected double[] normalizeX(int[] x) {
    double[]	result;
    int		i;

    result = new double[x.length];
    for (i = 0; i < result.length; i++)
      result[i] = (double) x[i] / (double) m_Width;

    return result;
  }

  /**
   * Normalizes the y value.
   *
   * @param y		the value to normalize
   * @return		the normalized value
   */
  protected double normalizeY(int y) {
    return (double) y / (double) m_Height;
  }

  /**
   * Normalizes the y values.
   *
   * @param y		the values to normalize
   * @return		the normalized values
   */
  protected double[] normalizeY(int[] y) {
    double[]	result;
    int		i;

    result = new double[y.length];
    for (i = 0; i < result.length; i++)
      result[i] = (double) y[i] / (double) m_Height;

    return result;
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(Report data) {
    LocatedObjects	objects;
    SpreadSheet		sheet;
    Row			row;
    int			colLeft;
    int			colTop;
    int			colRight;
    int			colBottom;
    int			colWidth;
    int			colHeight;
    int			colPolygonX;
    int			colPolygonY;
    int			colType;
    int			colLeftN;
    int			colTopN;
    int			colRightN;
    int			colBottomN;
    int			colWidthN;
    int			colHeightN;
    int			colPolygonXN;
    int			colPolygonYN;
    int[]		metaData;
    int			i;
    Object		value;

    objects = m_Finder.findObjects(data);
    sheet   = new DefaultSpreadSheet();

    // header
    row = sheet.getHeaderRow();
    colLeft = -1;
    colTop = -1;
    colRight = -1;
    colBottom = -1;
    colWidth = -1;
    colHeight = -1;
    colPolygonX = -1;
    colPolygonY = -1;
    colType = -1;
    colLeftN = -1;
    colTopN = -1;
    colRightN = -1;
    colBottomN = -1;
    colWidthN = -1;
    colHeightN = -1;
    colPolygonXN = -1;
    colPolygonYN = -1;
    metaData = new int[m_MetaDataKeys.length];
    Arrays.fill(metaData, -1);

    if (!m_ColLeft.trim().isEmpty()) {
      colLeft = row.getCellCount();
      row.addCell("L").setContentAsString(m_ColLeft);
    }
    if (!m_ColTop.trim().isEmpty()) {
      colTop = row.getCellCount();
      row.addCell("T").setContentAsString(m_ColTop);
    }
    if (!m_ColRight.trim().isEmpty()) {
      colRight = row.getCellCount();
      row.addCell("R").setContentAsString(m_ColRight);
    }
    if (!m_ColBottom.trim().isEmpty()) {
      colBottom = row.getCellCount();
      row.addCell("B").setContentAsString(m_ColBottom);
    }
    if (!m_ColWidth.trim().isEmpty()) {
      colWidth = row.getCellCount();
      row.addCell("W").setContentAsString(m_ColWidth);
    }
    if (!m_ColHeight.trim().isEmpty()) {
      colHeight = row.getCellCount();
      row.addCell("H").setContentAsString(m_ColHeight);
    }
    if (!m_ColPolygonX.trim().isEmpty()) {
      colPolygonX = row.getCellCount();
      row.addCell("PX").setContentAsString(m_ColPolygonX);
    }
    if (!m_ColPolygonY.trim().isEmpty()) {
      colPolygonY = row.getCellCount();
      row.addCell("PY").setContentAsString(m_ColPolygonY);
    }
    if (!m_ColType.trim().isEmpty()) {
      colType = row.getCellCount();
      row.addCell("TYPE").setContentAsString(m_ColType);
    }
    for (i = 0; i < m_MetaDataKeys.length; i++) {
      metaData[i] = row.getCellCount();
      row.addCell("MD-" + i).setContentAsString(m_MetaDataKeys[i].getValue());
    }

    if (m_OutputNormalized) {
      if (!m_ColLeft.trim().isEmpty()) {
	colLeftN = row.getCellCount();
	row.addCell("LN").setContentAsString(m_ColLeft + "n");
      }
      if (!m_ColTop.trim().isEmpty()) {
	colTopN = row.getCellCount();
	row.addCell("TN").setContentAsString(m_ColTop + "n");
      }
      if (!m_ColRight.trim().isEmpty()) {
	colRightN = row.getCellCount();
	row.addCell("RN").setContentAsString(m_ColRight + "n");
      }
      if (!m_ColBottom.trim().isEmpty()) {
	colBottomN = row.getCellCount();
	row.addCell("BN").setContentAsString(m_ColBottom + "n");
      }
      if (!m_ColWidth.trim().isEmpty()) {
	colWidthN = row.getCellCount();
	row.addCell("WN").setContentAsString(m_ColWidth + "n");
      }
      if (!m_ColHeight.trim().isEmpty()) {
	colHeightN = row.getCellCount();
	row.addCell("HN").setContentAsString(m_ColHeight + "n");
      }
      if (!m_ColPolygonX.trim().isEmpty()) {
	colPolygonXN = row.getCellCount();
	row.addCell("PXN").setContentAsString(m_ColPolygonX + "n");
      }
      if (!m_ColPolygonY.trim().isEmpty()) {
	colPolygonYN = row.getCellCount();
	row.addCell("PYN").setContentAsString(m_ColPolygonY + "n");
      }
    }

    // data
    for (LocatedObject object: objects) {
      row = sheet.addRow();
      if (colLeft > -1)
        row.addCell("L").setContent(object.getX());
      if (colTop > -1)
        row.addCell("T").setContent(object.getY());
      if (colRight > -1)
        row.addCell("R").setContent(object.getX() + object.getWidth() - 1);
      if (colBottom > -1)
        row.addCell("B").setContent(object.getY() + object.getHeight() - 1);
      if (colWidth > -1)
        row.addCell("W").setContent(object.getWidth());
      if (colHeight > -1)
        row.addCell("H").setContent(object.getHeight());
      if (object.hasPolygon()) {
        if (colPolygonX > -1)
	  row.addCell("PX").setContentAsString(Utils.flatten(StatUtils.toNumberArray(object.getPolygonX()), ","));
        if (colPolygonY > -1)
	  row.addCell("PY").setContentAsString(Utils.flatten(StatUtils.toNumberArray(object.getPolygonY()), ","));
      }
      if ((colType > -1) && object.getMetaData().containsKey(m_MetaDataKeyType))
        row.addCell("TYPE").setContentAsString("" + object.getMetaData().get(m_MetaDataKeyType));
      for (i = 0; i < metaData.length; i++) {
        if (object.getMetaData().containsKey(m_MetaDataKeys[i].getValue())) {
          value = object.getMetaData().get(m_MetaDataKeys[i].getValue());
          if (value instanceof String)
	    row.addCell("MD-" + i).setContentAsString((String) value);
          else
	    row.addCell("MD-" + i).setContent("" + value);
        }
      }

      if (m_OutputNormalized) {
	if (colLeftN > -1)
	  row.addCell("LN").setContent(normalizeX(object.getX()));
	if (colTopN > -1)
	  row.addCell("TN").setContent(normalizeY(object.getY()));
	if (colRightN > -1)
	  row.addCell("RN").setContent(normalizeX(object.getX() + object.getWidth() - 1));
	if (colBottomN > -1)
	  row.addCell("BN").setContent(normalizeY(object.getY() + object.getHeight() - 1));
	if (colWidthN > -1)
	  row.addCell("WN").setContent(normalizeX(object.getWidth()));
	if (colHeightN > -1)
	  row.addCell("HN").setContent(normalizeY(object.getHeight()));
	if (object.hasPolygon()) {
	  if (colPolygonXN > -1)
	    row.addCell("PXN").setContentAsString(Utils.flatten(StatUtils.toNumberArray(normalizeX(object.getPolygonX())), ","));
	  if (colPolygonYN > -1)
	    row.addCell("PYN").setContentAsString(Utils.flatten(StatUtils.toNumberArray(normalizeY(object.getPolygonY())), ","));
	}
      }
    }

    return m_Writer.write(sheet, m_Output);
  }
}
