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
 * YoloAnnotationsReportReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.filechanged.FileChangeMonitor;
import adams.core.io.filechanged.LastModified;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads text files with YOLO object annotations, one object definition per line:<br>
 * &lt;object-class&gt; &lt;x&gt; &lt;y&gt; &lt;width&gt; &lt;height&gt;<br>
 * Notes:<br>
 * - object-class: 0-based index<br>
 * - x&#47;y: normalized center of annotation<br>
 * - width&#47;height: normalized width&#47;height<br>
 * - Normalization uses image width&#47;height
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
public class YoloAnnotationsReportReader
    extends AbstractReportReader<Report>
    implements ObjectPrefixHandler {

  private static final long serialVersionUID = 5716807404370681434L;

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

  /** the prefix of the objects in the report. */
  protected String m_Prefix;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

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
    return "Reads text files with YOLO object annotations, one object definition per line:\n"
        + "<object-class> <x> <y> <width> <height>\n"
        + "Notes:\n"
        + "- object-class: 0-based index\n"
        + "- x/y: normalized center of annotation\n"
        + "- width/height: normalized width/height\n"
        + "- Normalization uses image width/height";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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
        "prefix", "prefix",
        ObjectLocationsOverlayFromReport.PREFIX_DEFAULT);

    m_OptionManager.add(
        "label-suffix", "labelSuffix",
        "type");
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
    return "YOLO Object Annotations";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"txt"};
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
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  @Override
  public Report newInstance() {
    return new Report();
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report>	result;
    LocatedObject	lobj;
    LocatedObjects 	lobjs;
    List<String>	lines;
    int			lineNo;
    String[]		parts;
    String		labelStr;
    double 		xN;
    double 		yN;
    double 		wN;
    double 		hN;
    int			x;
    int			y;
    int			w;
    int			h;
    boolean		loadLabels;

    result  = new ArrayList<>();

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
      m_Labels = readLabelDefinitions(m_LabelDefinitions, m_LabelReader, m_ColIndex, m_ColLabel);

    // annotations
    lines  = FileUtils.loadFromFile(m_Input);
    lobjs  = new LocatedObjects();
    lineNo = 0;
    for (String line: lines) {
      lineNo++;
      try {
        line = line.replace("\t", " ");
        line = line.replaceAll("[ ][ ]+", " ");
        parts = line.split(" ");
        if (parts.length == 5) {
          if (m_Labels != null)
            labelStr = m_Labels.get(Integer.parseInt(parts[0]));
          else
            labelStr = parts[0];
          xN = Double.parseDouble(parts[1]);
          yN = Double.parseDouble(parts[2]);
          wN = Double.parseDouble(parts[3]);
          hN = Double.parseDouble(parts[4]);

          w = (int) Math.round(m_Width * wN);
          h = (int) Math.round(m_Height * hN);
          x = (int) Math.round(xN * m_Width - w / 2.0);
          y = (int) Math.round(yN * m_Height - h / 2.0);

          lobj = new LocatedObject(x, y, w, h);
          lobj.getMetaData().put(m_LabelSuffix, labelStr);
          lobjs.add(lobj);
        }
        else {
          getLogger().warning("Invalid format in line #" + lineNo + ": " + line);
        }
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to parse line #" + lineNo + ": " + line, e);
      }
    }

    result.add(lobjs.toReport(m_Prefix));

    return result;
  }

  /**
   * Reads the label definitions, if the file exists.
   *
   * @param definitions		the definitions file
   * @param reader		the spreadsheet reader to use
   * @param index		the column with the 0-based label index
   * @param label		the column with the label string
   * @return			the mapping, null if nothing to read
   */
  public static Map<Integer,String> readLabelDefinitions(PlaceholderFile definitions, SpreadSheetReader reader, SpreadSheetColumnIndex index, SpreadSheetColumnIndex label) {
    Map<Integer,String> result;
    SpreadSheet		sheet;
    int			colIndex;
    int			colLabel;

    result = null;
    if (definitions.exists() && !definitions.isDirectory()) {
      sheet = reader.read(definitions);
      index.setSpreadSheet(sheet);
      colIndex = index.getIntIndex();
      if (colIndex == -1)
        throw new IllegalStateException("Column with label indices not found: " + index.getIndex());
      label.setSpreadSheet(sheet);
      colLabel = label.getIntIndex();
      if (colLabel == -1)
        throw new IllegalStateException("Column with label strings not found: " + label.getIndex());
      result = new HashMap<>();
      for (Row row: sheet.rows())
        result.put(row.getCell(colIndex).toLong().intValue(), row.getCell(colLabel).getContent());
    }

    return result;
  }
}
