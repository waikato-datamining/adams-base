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
 * YoloAnnotationsReportWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.filechanged.FileChangeMonitor;
import adams.core.io.filechanged.LastModified;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.input.YoloAnnotationsReportReader;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Writes text files with YOLO object annotations, one object definition per line:<br>
 * BBox format:<br>
 * - format: &lt;object-class&gt; &lt;x&gt; &lt;y&gt; &lt;width&gt; &lt;height&gt;<br>
 * - object-class: 0-based index<br>
 * - x&#47;y: normalized center of annotation<br>
 * - width&#47;height: normalized width&#47;height<br>
 * - Normalization uses image width&#47;height<br>
 * Polygon format:<br>
 * - format: &lt;object-class&gt; &lt;x0&gt; &lt;y0&gt; &lt;x1&gt; &lt;y1&gt;...<br>
 * - object-class: 0-based index<br>
 * - x&#47;y: normalized polygon point
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
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.txt
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label, ignored if empty.
 * &nbsp;&nbsp;&nbsp;default:
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
 * <pre>-label-definitions &lt;adams.core.io.PlaceholderFile&gt; (property: labelDefinitions)
 * &nbsp;&nbsp;&nbsp;The spreadsheet file with the label index &#47; label string relation.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-label-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: labelReader)
 * &nbsp;&nbsp;&nbsp;The spreadsheet reader to use for the label definitions.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-col-index &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colIndex)
 * &nbsp;&nbsp;&nbsp;The spreadsheet column containing the 0-based label index.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-col-label &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: colLabel)
 * &nbsp;&nbsp;&nbsp;The spreadsheet column containing the associated label string.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-use-polygon-format &lt;boolean&gt; (property: usePolygonFormat)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the data in polygon format rather than bbox format.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class YoloAnnotationsReportWriter
  extends AbstractReportWriter<Report> {

  private static final long serialVersionUID = -7250784020894287952L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the meta-data key with the label. */
  protected String m_LabelKey;

  /** the image width to use as basis for normalized coordinates/dimensions. */
  protected int m_Width;

  /** the image height to use as basis for normalized coordinates/dimensions. */
  protected int m_Height;

  /** the spreadsheet with the label index/label relation, ignored if directory. */
  protected PlaceholderFile m_LabelDefinitions;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_LabelReader;

  /** the column with the label index. */
  protected SpreadSheetColumnIndex m_ColIndex;

  /** the column with the label string. */
  protected SpreadSheetColumnIndex m_ColLabel;

  /** output polygon format rather than bbox format. */
  protected boolean m_UsePolygonFormat;

  /** the label mapping. */
  protected transient Map<Integer,String> m_Labels;

  /** to monitor whether the file with the labels has changed. */
  protected transient FileChangeMonitor m_LabelDefinitionsMonitor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes text files with YOLO object annotations, one object definition per line:\n"
      + "BBox format:\n"
      + "- format: <object-class> <x> <y> <width> <height>\n"
      + "- object-class: 0-based index\n"
      + "- x/y: normalized center of annotation\n"
      + "- width/height: normalized width/height\n"
      + "- Normalization uses image width/height\n"
      + "Polygon format:\n"
      + "- format: <object-class> <x0> <y0> <x1> <y1>...\n"
      + "- object-class: 0-based index\n"
      + "- x/y: normalized polygon point";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "label-key", "labelKey",
      "");

    m_OptionManager.add(
      "width", "width",
      1000, 1, null);

    m_OptionManager.add(
      "height", "height",
      1000, 1, null);

    m_OptionManager.add(
      "label-definitions", "labelDefinitions",
      new PlaceholderFile());

    m_OptionManager.add(
      "label-reader", "labelReader",
      new CsvSpreadSheetReader());

    m_OptionManager.add(
      "col-index", "colIndex",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "col-label", "colLabel",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "use-polygon-format", "usePolygonFormat",
      false);
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
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
    return "The object finder to use.";
  }

  /**
   * Sets the key in the meta-data containing the label.
   *
   * @param value	the key
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the key in the meta-data containing the label.
   *
   * @return		the key
   */
  public String getLabelKey() {
    return m_LabelKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelKeyTipText() {
    return "The key in the meta-data containing the label, ignored if empty.";
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
   * Sets the spreadsheet file with the label index / label string relation.
   *
   * @param value 	the file
   */
  public void setLabelDefinitions(PlaceholderFile value) {
    m_LabelDefinitions = value;
    reset();
  }

  /**
   * Returns the spreadsheet file with the label index / label string relation.
   *
   * @return 		the file
   */
  public PlaceholderFile getLabelDefinitions() {
    return m_LabelDefinitions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelDefinitionsTipText() {
    return "The spreadsheet file with the label index / label string relation.";
  }

  /**
   * Sets the spreadsheet reader to use for the definitions.
   *
   * @param value 	the reader
   */
  public void setLabelReader(SpreadSheetReader value) {
    m_LabelReader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader to use for the definitions.
   *
   * @return 		the reader
   */
  public SpreadSheetReader getLabelReader() {
    return m_LabelReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelReaderTipText() {
    return "The spreadsheet reader to use for the label definitions.";
  }

  /**
   * Sets the spreadsheet column containing the 0-based label index.
   *
   * @param value 	the file
   */
  public void setColIndex(SpreadSheetColumnIndex value) {
    m_ColIndex = value;
    reset();
  }

  /**
   * Returns the spreadsheet column containing the 0-based label index.
   *
   * @return 		the column
   */
  public SpreadSheetColumnIndex getColIndex() {
    return m_ColIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colIndexTipText() {
    return "The spreadsheet column containing the 0-based label index.";
  }

  /**
   * Sets the spreadsheet column containing the 0-based label index.
   *
   * @param value 	the file
   */
  public void setColLabel(SpreadSheetColumnIndex value) {
    m_ColLabel = value;
    reset();
  }

  /**
   * Returns the spreadsheet column containing the 0-based label index.
   *
   * @return 		the column
   */
  public SpreadSheetColumnIndex getColLabel() {
    return m_ColLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colLabelTipText() {
    return "The spreadsheet column containing the associated label string.";
  }

  /**
   * Sets whether to use polygon format or bbox format.
   *
   * @param value 	true if to use
   */
  public void setUsePolygonFormat(boolean value) {
    m_UsePolygonFormat = value;
    reset();
  }

  /**
   * Returns whether to use polygon format or bbox format.
   *
   * @return 		true if to use
   */
  public boolean getUsePolygonFormat() {
    return m_UsePolygonFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePolygonFormatTipText() {
    return "If enabled, outputs the data in polygon format rather than bbox format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new YoloAnnotationsReportReader().getFormatDescription();
  }

  /**
   * Returns the extension of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new YoloAnnotationsReportReader().getFormatExtensions();
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(Report data) {
    LocatedObjects 	objs;
    StringBuilder	line;
    List<String>	lines;
    Map<String,Integer>	revLabels;
    String		label;
    int			index;
    boolean		loadLabels;
    double		xN;
    double		yN;
    double		wN;
    double		hN;
    int[]		polyX;
    int[]		polyY;
    int			i;

    // labels?
    loadLabels = false;
    if ((m_LabelDefinitions.exists() && !m_LabelDefinitions.isDirectory())) {
      loadLabels = (m_Labels == null);
      if (m_LabelDefinitionsMonitor == null)
	m_LabelDefinitionsMonitor = new LastModified();
      if (!loadLabels)
	loadLabels = m_LabelDefinitionsMonitor.hasChanged(m_LabelDefinitions);
      m_LabelDefinitionsMonitor.update(m_LabelDefinitions);
    }
    if (loadLabels)
      m_Labels = YoloAnnotationsReportReader.readLabelDefinitions(m_LabelDefinitions, m_LabelReader, m_ColIndex, m_ColLabel);
    revLabels = new HashMap<>();
    if (m_Labels != null) {
      for (Map.Entry<Integer,String> entry: m_Labels.entrySet())
	revLabels.put(entry.getValue(), entry.getKey());
    }

    lines = new ArrayList<>();
    objs = m_Finder.findObjects(data);
    for (LocatedObject obj: objs) {
      index = -1;
      if (obj.getMetaData().containsKey(m_LabelKey)) {
	label = "" + obj.getMetaData().get(m_LabelKey);
	if (revLabels.containsKey(label))
	  index = revLabels.get(label);
	else
	  getLogger().warning("Label " + label + " not found in definitions, skipping: " + obj);
      }
      else {
	getLogger().warning("No label found under '" + m_LabelKey + "', skipping: " + obj);
      }

      if (index == -1)
	continue;

      line = new StringBuilder().append(index);
      if (m_UsePolygonFormat) {
	if (obj.hasPolygon()) {
	  polyX = obj.getPolygonX();
	  polyY = obj.getPolygonY();
	}
	else {
	  polyX = new int[]{obj.getX(), obj.getX() + obj.getWidth() - 1, obj.getX() + obj.getWidth() - 1,  obj.getX()};
	  polyY = new int[]{obj.getY(), obj.getY(),                      obj.getY() + obj.getHeight() - 1, obj.getY() + obj.getHeight() - 1};
	}
	for (i = 0; i < polyX.length; i++)
	  line.append(" ").append((double) polyX[i] / m_Width)
	    .append(" ").append((double) polyY[i] / m_Height);
      }
      else {
	xN = (double) (obj.getX() + obj.getWidth() / 2) / m_Width;
	yN = (double) (obj.getY() + obj.getHeight() / 2) / m_Height;
	wN = (double) obj.getWidth() / m_Width;
	hN = (double) obj.getHeight() / m_Height;

	line.append(" ").append(xN)
	  .append(" ").append(yN)
	  .append(" ").append(wN)
	  .append(" ").append(hN);
      }
      lines.add(line.toString());
    }

    if (lines.size() > 0) {
      return FileUtils.saveToFile(lines, m_Output);
    }
    else {
      getLogger().warning("No annotations in report, skipping output file: " + m_Output);
      return true;
    }
  }
}
