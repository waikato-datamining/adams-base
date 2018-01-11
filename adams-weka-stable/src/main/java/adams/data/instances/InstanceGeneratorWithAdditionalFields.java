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
 * InstanceGeneratorWithFields.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.instances;

import adams.data.report.Field;

/**
 * Generators with additional fields.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InstanceGeneratorWithAdditionalFields {

  /**
   * Sets whether to drop the prefix for the additional fields.
   *
   * @param value	if true then no prefix
   */
  public void setNoAdditionalFieldsPrefix(boolean value);

  /**
   * Returns whether to drop the prefix for the additional fields.
   *
   * @return		true if no prefix
   */
  public boolean getNoAdditionalFieldsPrefix();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noAdditionalFieldsPrefixTipText();

  /**
   * Sets the additional fields to add.
   *
   * @param value	the fields
   */
  public void setAdditionalFields(Field[] value);
  /**
   * Returns the additional fields to add.
   *
   * @return		the fields
   */
  public Field[] getAdditionalFields();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalFieldsTipText();
}
