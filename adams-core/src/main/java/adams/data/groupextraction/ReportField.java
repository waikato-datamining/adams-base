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
 * ReportField.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.data.groupextraction;

import adams.core.QuickInfoHelper;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 * Returns the value of the specified field.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReportField
  extends AbstractGroupExtractor {

  private static final long serialVersionUID = 6130414784797102811L;

  /** the report field. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the value of the specified field.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      new Field("Sample Type", DataType.STRING));
  }

  /**
   * Sets the field to get the group from.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to get the group from.
   *
   * @return		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The field in the report to get the group from.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "field", m_Field);
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj) {
    return (obj instanceof ReportHandler)
      || (obj instanceof Report);
  }

  /**
   * Extracts the group from the object.
   *
   * @param obj		the object to process
   * @return		the extracted group, null if failed to extract or not handled
   */
  @Override
  protected String doExtractGroup(Object obj) {
    String	result;

    result = null;

    if (obj instanceof Report) {
      if (((Report) obj).hasValue(m_Field))
	result = "" + ((Report) obj).getValue(m_Field);
    }
    else if (obj instanceof ReportHandler) {
      result = doExtractGroup(((ReportHandler) obj).getReport());
    }

    return result;
  }
}
