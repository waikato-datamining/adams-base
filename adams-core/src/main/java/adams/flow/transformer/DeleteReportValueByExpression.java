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

/**
 * DeleteReportValueByExpression.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeleteReportValueByExpression
  extends AbstractDeleteReportValueByExpression {

  private static final long serialVersionUID = -2551776226766445092L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Deletes a field and its value from a report if the provided boolean "
        + "expression evaluates to 'true'";
  }

  /**
   * Returns the default field for the option.
   *
   * @return		the default field
   */
  @Override
  protected AbstractField getDefaultField() {
    return new Field();
  }

  /**
   * Sets the field to delete from the report.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to delete from the report.
   *
   * @return		the field
   */
  public Field getField() {
    return (Field) m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String fieldTipText() {
    return "The field to remove from the report, if the expression evaluates to true.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }
}
