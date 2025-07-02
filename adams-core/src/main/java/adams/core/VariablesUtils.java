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
 * VariablesUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;

/**
 * Helper methods around variable handling.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariablesUtils {

  /**
   * Transfers the variable (if any attached).
   *
   * @param source	the source from which to transfer the variable
   * @param target	the target to receive the variable
   * @param property	the property for which to transfer the variable
   */
  public static void transferVariable(OptionHandler source, OptionHandler target, String property) {
    String		var;
    AbstractOption option;

    if (source.getOptionManager().hasVariableForProperty(property)) {
      var    = source.getOptionManager().getVariableForProperty(property);
      option = target.getOptionManager().findByProperty(property);
      if (option instanceof AbstractArgumentOption)
	((AbstractArgumentOption) option).setVariable(var);
    }
  }

  /**
   * Transfers the variable (if any attached) form one property to another.
   *
   * @param source		the source from which to transfer the variable
   * @param sourceProperty	the source property for which to transfer the variable
   * @param target		the target to receive the variable
   * @param targetProperty	the target property to receive the variable
   */
  public static void transferVariable(OptionHandler source, String sourceProperty, OptionHandler target, String targetProperty) {
    String		var;
    AbstractOption	option;

    if (source.getOptionManager().hasVariableForProperty(sourceProperty)) {
      var    = source.getOptionManager().getVariableForProperty(sourceProperty);
      option = target.getOptionManager().findByProperty(targetProperty);
      if (option instanceof AbstractArgumentOption)
	((AbstractArgumentOption) option).setVariable(var);
    }
  }
}
