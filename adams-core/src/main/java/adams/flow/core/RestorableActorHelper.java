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
 * RestorableActorHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for writing/reading restoration information (= actor state).
 * Uses .props files for storing the data ({@link adams.core.Properties}).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.Properties
 */
public class RestorableActorHelper {

  /**
   * Checks whether the restoration file can be read.
   *
   * @param input	the file to check
   * @return		true if should be readable
   */
  public static boolean canRead(PlaceholderFile input) {
    return (input.exists() && !input.isDirectory());
  }

  /**
   * Loads the state as properties from the specified file.
   *
   * @param input	the state file
   * @param props	for storing the properties
   * @return		null if successful, otherwise error message
   */
  public static String read(PlaceholderFile input, Properties props) {
    String	result;
    Properties	p;

    result = null;
    p = new Properties();
    if (!p.load(input.getAbsolutePath()))
      result = "Failed to read restoration data from: " + input;
    else
      props.add(p);

    return result;
  }

  /**
   * Stores the state properties in the specified file.
   *
   * @param props	the properties to store
   * @param output	the output file
   * @return		null if successful, otherwise error message
   */
  public static String write(Properties props, PlaceholderFile output) {
    String	result;

    result = null;
    if (!props.save(output.getAbsolutePath()))
      result = "Failed to write restoration data to: " + output;

    return result;
  }

  /**
   * Reads the specified state information from the specified input file.
   *
   * @param input	the file to read the information from
   * @param actor	the actor to obtain the restore information from
   * @param properties	the bean properties to obtain and store
   * @return		null if successful, otherwise error message
   */
  public static String read(PlaceholderFile input, RestorableActor actor, String[] properties) {
    MessageCollection		result;
    Properties			props;
    AbstractOption		opt;
    AbstractArgumentOption	option;
    Object			objects;
    Method 			method;
    int				i;
    List<String> 		values;

    result = new MessageCollection();
    props  = new Properties();
    if (!props.load(input.getAbsolutePath())) {
      result.add("Failed to load restoration data from: " + input);
    }
    else {
      values = new ArrayList<>();
      for (String property : properties) {
	if (!props.hasKey(property))
	  continue;
	values.clear();
	try {
	  opt = actor.getOptionManager().findByProperty(property);
	  if (opt == null) {
	    result.add("Failed to locate property '" + property + "'!");
	    continue;
	  }
	  if (opt instanceof AbstractArgumentOption) {
	    option = (AbstractArgumentOption) opt;
	    values.addAll(Arrays.asList(OptionUtils.splitOptions(props.getProperty(property))));
	    objects = Array.newInstance(option.getBaseClass(), values.size());
	    for (i = 0; i < values.size(); i++) {
	      Array.set(objects, i, option.valueOf(values.get(i)));
	      if (!option.isMultiple())
		break;
	    }
	    method = option.getDescriptor().getWriteMethod();
	    if (!option.isMultiple())
	      method.invoke(
		option.getOptionHandler(),
		new Object[]{Array.get(objects, 0)});
	    else
	      method.invoke(
		option.getOptionHandler(),
		new Object[]{objects});
	  }
	}
	catch (Exception e) {
	  result.add(
	    "Failed to process property '" + property + "'!", e);
	}
      }
    }

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }

  /**
   * Stores the specified state information in the specified output file.
   *
   * @param actor	the actor to obtain the restore information from
   * @param properties	the bean properties to obtain and store
   * @param output	the output file to store the information in
   * @return		null if successful, otherwise error message
   */
  public static String write(RestorableActor actor, String[] properties, PlaceholderFile output) {
    MessageCollection 		result;
    Properties 			props;
    AbstractOption 		opt;
    AbstractArgumentOption 	option;
    List<String> 		values;
    Object			currValue;
    Object			currValues;
    int				i;

    result = new MessageCollection();
    props  = new Properties();
    values = new ArrayList<>();
    for (String property: properties) {
      values.clear();
      try {
	opt = actor.getOptionManager().findByProperty(property);
	if (opt == null) {
	  result.add("Failed to locate property '" + property + "'!");
	  continue;
	}
	if (opt instanceof AbstractArgumentOption) {
	  option    = (AbstractArgumentOption) opt;
	  currValue = option.getCurrentValue();
	  if (currValue != null) {
	    if (!option.isMultiple()) {
	      currValues = Array.newInstance(option.getBaseClass(), 1);
	      Array.set(currValues, 0, currValue);
	    }
	    else {
	      currValues = currValue;
	    }

	    for (i = 0; i < Array.getLength(currValues); i++) {
	      values.add(option.toString(Array.get(currValues, i)));
	    }
	  }
	}
	props.setProperty(property, OptionUtils.joinOptions(values.toArray(new String[values.size()])));
      }
      catch (Exception e) {
	result.add(
	  "Failed to process property '" + property + "'!", e);
      }
    }

    // save
    if (!props.save(output.getAbsolutePath()))
      result.add("Failed to store restoration data in: " + output);

    if (result.isEmpty())
      return null;
    else
      return result.toString();
  }
}
