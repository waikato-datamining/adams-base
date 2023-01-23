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
 * ExternalCommandWithOptions.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

import adams.core.base.BaseString;
import adams.core.base.BaseText;

import java.util.List;

/**
 * Interface for external commands that take options.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ExternalCommandWithOptions
  extends ExternalCommand {

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptions(List<String> value);

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptions(String[] value);

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptions(BaseString[] value);

  /**
   * Returns the options for the command.
   *
   * @return		the options
   */
  public BaseString[] getOptions();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsTipText();

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptionsString(String value);

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptionsString(BaseText value);

  /**
   * Returns the options for the command as single string.
   *
   * @return		the options
   */
  public BaseText getOptionsString();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsStringTipText();

  /**
   * Returns the actual options to use. The options string takes precendence over the array.
   *
   * @return		the options
   */
  public String[] getActualOptions();
}
