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
 * AdamsClassDescriptionGenerator.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.description;

import adams.core.EnumHelper;
import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractNumericOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import adams.core.option.UserMode;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates descriptions for ADAMS option handlers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AdamsClassDescriptionGenerator
  extends AbstractClassDescriptionGenerator {

  private static final long serialVersionUID = -5385178104241602341L;

  /**
   * Checks whether this class is handled by this generator.
   *
   * @param cls 	the class to check
   * @return 		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(OptionHandler.class, cls);
  }

  /**
   * Generates the description for the object.
   *
   * @param obj 	the object to process
   * @return 		the description, null if failed to generate
   */
  @Override
  public Map<String, Object> generate(Object obj) {
    Map<String, Object>		result;
    OptionHandler		handler;
    AbstractArgumentOption	option;
    AbstractNumericOption	numOption;
    Map<String,Object>		optionInfo;
    Method 			help;
    boolean			isEnum;
    Object			defValue;
    List<String> 		values;
    int				i;

    result = new HashMap<>();
    result.put(KEY_CLASS, obj.getClass().getName());
    result.put(KEY_OPTIONS, new ArrayList<>());

    handler = (OptionHandler) obj;
    for (AbstractOption opt: handler.getOptionManager().getOptionsList()) {
      if (opt instanceof AbstractArgumentOption) {
	option = (AbstractArgumentOption) opt;
	optionInfo = new HashMap<>();
	optionInfo.put("option", "-" + option.getCommandline());
	optionInfo.put("property", option.getProperty());
	optionInfo.put("type", Utils.classToString(option.getBaseClass()));
	optionInfo.put("multiple", option.isMultiple());

	// enum
	isEnum = option.getBaseClass().isEnum();
	if (isEnum) {
	  values = new ArrayList<>();
	  for (Object val: EnumHelper.getValues(option.getBaseClass()))
	    values.add(option.toString(val));
	  optionInfo.put("enum", values);
	}

	// default value
	defValue = option.getDefaultValue();
	if (defValue != null) {
	  if (defValue.getClass().isArray()) {
	    values = new ArrayList<>();
	    for (i = 0; i < Array.getLength(defValue); i++)
	      values.add(option.toString(Array.get(defValue, i)));
	    optionInfo.put("default", values);
	  }
	  else {
	    optionInfo.put("default", option.toString(defValue));
	  }
	}
	if (option.getMinUserMode() != UserMode.LOWEST)
	  optionInfo.put("minUserMode", option.getMinUserMode().toRaw());

	// bounds
	if (option instanceof AbstractNumericOption) {
	  numOption = (AbstractNumericOption) option;
	  if (numOption.getLowerBound() != null)
	    optionInfo.put("lowerBound", numOption.getLowerBound());
	  if (numOption.getUpperBound() != null)
	    optionInfo.put("upperBound", numOption.getUpperBound());
	  // special values
	  if (Double.isNaN(((Number) numOption.getDefaultValue()).doubleValue()))
	    optionInfo.put("default", "NaN");
	}

	// help
	help = option.getToolTipMethod();
	if (help != null) {
	  try {
	    optionInfo.put("help", help.invoke(option.getOwner().getOwner()));
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	((List) result.get(ClassDescriptionGenerator.KEY_OPTIONS)).add(optionInfo);
      }
    }

    return result;
  }
}
