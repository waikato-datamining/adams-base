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
 * ImageClassificationJsonReportReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.data.json.JsonHelper;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Retrieves the label with the highest score from the JSON file and stores them in a report.<br>
 * JSON file format: { LABEL1: SCORE1; LABEL2: SCORE2;  }
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
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageClassificationJsonReportReader
  extends AbstractReportReader<Report> {

  private static final long serialVersionUID = 961116146272004314L;

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
    return "Retrieves the label with the highest score from the JSON file and stores them in a report.\n"
      + "JSON file format: { LABEL1: SCORE1; LABEL2: SCORE2;  }";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field-label", "fieldLabel",
      new Field("Classification", DataType.STRING));

    m_OptionManager.add(
      "field-score", "fieldScore",
      new Field("Score", DataType.NUMERIC));
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
    return new String[]{"json"};
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
    Object		json;
    JSONObject		obj;
    String		label;
    double		score;
    Report		report;

    result = new ArrayList<>();

    json  = JsonHelper.parse(m_Input, this);
    label = null;
    score = 0.0;
    if (json instanceof JSONObject) {
      obj = (JSONObject) json;
      for (String key: obj.keySet()) {
	try {
	  if (obj.getAsNumber(key).doubleValue() > score) {
	    label = key;
	    score = obj.getAsNumber(key).doubleValue();
	  }
	}
	catch (Exception e) {
	  // ignored
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
