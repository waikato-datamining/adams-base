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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.idupdate;

import adams.core.Utils;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 * Sets the ID under the specified field in the report (either report or report handler).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReportField
  extends AbstractIDUpdater {

  private static final long serialVersionUID = -1554542870085562691L;

  /** the report field. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets the ID under the specified field in the report (either report or report handler).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      new Field("Sample ID", DataType.STRING));
  }

  /**
   * Sets the field to set the ID under.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to set the ID under.
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
    return "The field in the report to set the ID under.";
  }

  /**
   * Checks whether the data type is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof Report) || (obj instanceof ReportHandler);
  }

  /**
   * Updates the ID of the object.
   *
   * @param obj		the object to process
   * @param id 		the new ID
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdateID(Object obj, String id) {
    String	result;

    result = null;

    if (obj instanceof Report) {
      ((Report) obj).addField(m_Field);
      ((Report) obj).setValue(m_Field, id);
    }
    else if (obj instanceof ReportHandler) {
      ((ReportHandler) obj).getReport().addField(m_Field);
      ((ReportHandler) obj).getReport().setValue(m_Field, id);
    }
    else {
      result = "Unhandled object: " + Utils.classToString(obj);
    }

    return result;
  }
}
