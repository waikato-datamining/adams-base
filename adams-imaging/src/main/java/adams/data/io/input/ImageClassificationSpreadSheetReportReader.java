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
 * ImageClassificationSpreadSheetReportReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Retrieves the label with the highest score from the spreadsheet and stores them in a report.
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
 * &nbsp;&nbsp;&nbsp;The spreadsheet reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-column-label &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnLabel)
 * &nbsp;&nbsp;&nbsp;The spreadsheet column containing the label.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-column-score &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnScore)
 * &nbsp;&nbsp;&nbsp;The spreadsheet column containing the score.
 * &nbsp;&nbsp;&nbsp;default: second
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-field-label &lt;adams.data.report.Field&gt; (property: fieldLabel)
 * &nbsp;&nbsp;&nbsp;The field to store the label under.
 * &nbsp;&nbsp;&nbsp;default: Classification[S]
 * </pre>
 *
 * <pre>-field-score &lt;adams.data.report.Field&gt; (property: fieldScore)
 * &nbsp;&nbsp;&nbsp;The field to store the score under.
 * &nbsp;&nbsp;&nbsp;default: Score[N]
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageClassificationSpreadSheetReportReader
  extends AbstractReportReader<Report> {

  private static final long serialVersionUID = 961116146272004314L;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /** the column with the label. */
  protected SpreadSheetColumnIndex m_ColumnLabel;

  /** the column with the score. */
  protected SpreadSheetColumnIndex m_ColumnScore;

  /** the report field to store the label under. */
  protected Field m_FieldLabel;

  /** the report file to store the score under. */
  protected Field m_FieldScore;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Retrieves the label with the highest score from the spreadsheet and stores them in a report.";
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
      "column-label", "columnLabel",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
      "column-score", "columnScore",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.SECOND));

    m_OptionManager.add(
      "field-label", "fieldLabel",
      new Field("Classification", DataType.STRING));

    m_OptionManager.add(
      "field-score", "fieldScore",
      new Field("Score", DataType.NUMERIC));
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
   * Returns the spreadsheet reader to use.
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
    return "The spreadsheet reader to use.";
  }

  /**
   * Sets the column containing the label.
   *
   * @param value	the column
   */
  public void setColumnLabel(SpreadSheetColumnIndex value) {
    m_ColumnLabel = value;
    reset();
  }

  /**
   * Returns the column containing the label.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnLabel() {
    return m_ColumnLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnLabelTipText() {
    return "The spreadsheet column containing the label.";
  }

  /**
   * Sets the column containing the score.
   *
   * @param value	the column
   */
  public void setColumnScore(SpreadSheetColumnIndex value) {
    m_ColumnScore = value;
    reset();
  }

  /**
   * Returns the column containing the score.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnScore() {
    return m_ColumnScore;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnScoreTipText() {
    return "The spreadsheet column containing the score.";
  }

  /**
   * Sets the field for the label.
   *
   * @param value	the field
   */
  public void setFieldLabel(Field value) {
    m_FieldLabel = value;
    reset();
  }

  /**
   * Returns the field for the label.
   *
   * @return		the field
   */
  public Field getFieldLabel() {
    return m_FieldLabel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldLabelTipText() {
    return "The field to store the label under.";
  }

  /**
   * Sets the field for the score.
   *
   * @param value	the field
   */
  public void setFieldScore(Field value) {
    m_FieldScore = value;
    reset();
  }

  /**
   * Returns the field for the score.
   *
   * @return		the field
   */
  public Field getFieldScore() {
    return m_FieldScore;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldScoreTipText() {
    return "The field to store the score under.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Image classification predictions";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return getReader().getFormatExtensions();
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
    List<Report>	result;
    SpreadSheet		sheet;
    int			colLabel;
    int			colScore;
    String		label;
    double		score;
    Report		report;

    result = new ArrayList<>();

    sheet    = m_Reader.read(m_Input);
    label    = null;
    score    = 0.0;
    colLabel = -1;
    colScore = -1;
    if (sheet != null) {
      m_ColumnLabel.setSpreadSheet(sheet);
      colLabel = m_ColumnLabel.getIntIndex();
      m_ColumnScore.setSpreadSheet(sheet);
      colScore = m_ColumnScore.getIntIndex();
    }

    if (colLabel == -1)
      getLogger().severe("Failed to locate label column: " + m_ColumnLabel.getIndex());
    if (colScore == -1)
      getLogger().severe("Failed to locate score column: " + m_ColumnScore.getIndex());

    if ((colLabel > -1) && (colScore > -1)) {
      for (Row row: sheet.rows()) {
	if (row.hasCell(colLabel) && !row.getCell(colLabel).isMissing() && row.hasCell(colScore) && !row.getCell(colScore).isMissing()) {
	  if (row.getCell(colScore).toDouble() > score) {
	    label = row.getCell(colLabel).getContent();
	    score = row.getCell(colScore).toDouble();
	  }
	}
      }
    }

    if (label != null) {
      report = new Report();
      report.setValue(m_FieldLabel, label);
      report.setValue(m_FieldScore, score);
      result.add(report);
    }
    else {
      getLogger().severe("Failed to determine label/score from: " + m_Input);
    }

    return result;
  }
}
