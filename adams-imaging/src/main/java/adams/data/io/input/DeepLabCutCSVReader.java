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
 * DeepLabCutCSVReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.visualization.image.ReportPointOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Reads point annotations into multiple reports from DeepLabCut's CSV format.
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Point.
 * </pre>
 *
 * <pre>-file-regexp &lt;adams.core.base.BaseRegExp&gt; (property: fileRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the file names must match in order to be turned
 * &nbsp;&nbsp;&nbsp;into reports.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeepLabCutCSVReader
  extends AbstractReportReader<Report> {

  private static final long serialVersionUID = 5868402740072822596L;

  public static final String KEY_FILE = "File";

  /** the prefix of the points in the report. */
  protected String m_Prefix;

  /** the regexp the files must match. */
  protected BaseRegExp m_FileRegExp;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads point annotations into multiple reports from DeepLabCut's CSV format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      ReportPointOverlay.PREFIX_DEFAULT);

    m_OptionManager.add(
      "file-regexp", "fileRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));
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
   * Sets the regexp the file names must match.
   *
   * @param value	the expression
   */
  public void setFileRegExp(BaseRegExp value) {
    m_FileRegExp = value;
    reset();
  }

  /**
   * Returns the regexp the file names must match.
   *
   * @return		the expression
   */
  public BaseRegExp getFileRegExp() {
    return m_FileRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileRegExpTipText() {
    return "The regular expression that the file names must match in order to be turned into reports.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "DeepLabCut CSV";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"csv"};
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report the report to determine the ID for
   * @return the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return Constants.NO_ID;
  }

  /**
   * Returns a new instance of the report class in use.
   *
   * @return the new (empty) report
   */
  @Override
  public Report newInstance() {
    return new Report();
  }

  /**
   * Performs the actual reading.
   *
   * @return the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report>		result;
    CsvSpreadSheetReader	reader;
    SpreadSheet			sheet;
    int				numLabels;
    String			label;
    List<String>		labels;
    List<String>		files;
    int				i;
    int				n;
    int				col;
    String			file;
    double			x;
    double			y;
    Map<String,Report>		reports;
    Report			report;
    List<String>		individuals;
    Row				indRow;
    boolean			multi;

    result = new ArrayList<>();
    reader = new CsvSpreadSheetReader();
    reader.setParseFormulas(false);
    sheet = reader.read(m_Input);

    if (sheet.getRowCount() < 3) {
      getLogger().warning("Not enough rows in spreadsheet?");
      return result;
    }

    // multiple or single individual?
    multi = ((sheet.getRowCount() > 0) && (sheet.getCell(0, 0).getContent().equals("individuals")));

    // determine labels
    labels = new ArrayList<>();
    if (sheet.getRowCount() > 1) {
      for (i = 1; i < sheet.getColumnCount(); i += 2) {
        if (multi)
	  label = sheet.getRow(1).getCell(i).getContent();
        else
	  label = sheet.getRow(0).getCell(i).getContent();
        if (!labels.contains(label))
	  labels.add(label);
      }
    }

    // multi-animal?
    individuals = new ArrayList<>();
    if (multi) {
      indRow = sheet.getRow(0);
      for (i = 1; i < indRow.getCellCount(); i += labels.size()*2)
        individuals.add(indRow.getCell(i).getContent());
    }
    if (!multi)
      individuals.add("dummy");

    // remove unnecessary rows (individuals/bodyparts/coords)
    if (multi)
      sheet.removeRow(0);
    sheet.removeRow(0);
    sheet.removeRow(0);

    // iterate rows
    reports = new HashMap<>();
    for (Row row: sheet.rows()) {
      file = row.getCell(0).getContent();
      if (!m_FileRegExp.isMatchAll() && !m_FileRegExp.isMatch(file))
        continue;
      if (!reports.containsKey(file)) {
        report = new Report();
        report.setStringValue(KEY_FILE, file);
	reports.put(file, report);
      }
      report = reports.get(file);
      for (n = 0; n < individuals.size(); n++) {
	for (i = 0; i < labels.size(); i++) {
	  col = 1 + n * labels.size() + i * 2;
	  if (row.hasCell(col) && !row.getCell(col).isMissing()) {
	    x = row.getCell(col).toDouble();
	    y = row.getCell(col + 1).toDouble();
	    report.setNumericValue(m_Prefix + Utils.padLeft("" + (n * labels.size() + i + 1), '0', 4) + ".x", x);
	    report.setNumericValue(m_Prefix + Utils.padLeft("" + (n * labels.size() + i + 1), '0', 4) + ".y", y);
	    report.setStringValue(m_Prefix + Utils.padLeft("" + (n * labels.size() + i + 1), '0', 4) + ".type", labels.get(i));
	    if (multi)
	      report.setStringValue(m_Prefix + Utils.padLeft("" + (n * labels.size() + i + 1), '0', 4) + ".individual", individuals.get(n));
	  }
	}
      }
    }

    files = new ArrayList<>(reports.keySet());
    Collections.sort(files);
    for (String f: files)
      result.add(reports.get(f));

    return result;
  }
}
