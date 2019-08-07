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
 * ReportFieldRegExp.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.groupextraction;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 * Returns the value of the regexp group applied to the specified report field.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReportFieldRegExp
  extends AbstractGroupExtractor {

  private static final long serialVersionUID = 6130414784797102811L;

  /** the report field. */
  protected Field m_Field;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** the group to extract. */
  protected String m_Group;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the value of the regexp group applied to the specified report field.";
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

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp("(.*)"));

    m_OptionManager.add(
      "group", "group",
      "$1");
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
   * Sets the regular expression to apply to the report value.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the report value.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to apply to the report value.";
  }

  /**
   * Sets the group to extract.
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the group to extract.
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The group of the expression to extract.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "field", m_Field, "field: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    result += QuickInfoHelper.toString(this, "group", m_Group, ", group: ");

    return result;
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
    String	value;

    result = null;
    value  = null;

    if (obj instanceof Report) {
      if (((Report) obj).hasValue(m_Field))
	value = "" + ((Report) obj).getValue(m_Field);
    }
    else if (obj instanceof ReportHandler) {
      value = doExtractGroup(((ReportHandler) obj).getReport());
    }

    if (value != null)
      result = value.replaceAll(m_RegExp.getValue(), m_Group);

    return result;
  }
}
