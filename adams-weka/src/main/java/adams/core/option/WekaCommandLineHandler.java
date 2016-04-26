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
 * WekaCommandLineHandler.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.ClassLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles objects of classes that implement the weka.core.OptionHandler
 * interface.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.core.OptionHandler
 */
public class WekaCommandLineHandler
  extends AbstractCommandLineHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5233496867185402778L;

  /**
   * Generates an object from the specified commandline.
   *
   * @param cmd		the commandline to create the object from
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromCommandLine(String cmd) {
    Object	result;

    try {
      result = fromArray(weka.core.Utils.splitOptions(cmd));
    }
    catch (Exception e) {
      System.err.println("Failed to process commandline '" + cmd + "':");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Generates an object from the commandline options.
   *
   * @param args	the commandline options to create the object from
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromArray(String[] args) {
    Object	result;
    String	classname;

    result = null;
    args   = args.clone();

    if (args.length > 0) {
      try {
	classname = args[0];
	args[0]   = "";
	result = weka.core.Utils.forName(Object.class, Conversion.getSingleton().rename(classname), args);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object from array (fromArray):");
	e.printStackTrace();
	result = null;
      }
    }

    return result;
  }

  /**
   * Generates a commandline from the specified object.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  @Override
  public String toCommandLine(Object obj) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(obj.getClass().getName());
    if (obj instanceof weka.core.OptionHandler) {
      result.append(" ");
      result.append(weka.core.Utils.joinOptions(((weka.core.OptionHandler) obj).getOptions()));
    }

    return result.toString().trim();
  }

  /**
   * Generates a commandline from the specified object. Uses a shortened
   * format, e.g., removing the package from the class.
   *
   * @param obj		the object to create the commandline for
   * @return		the generated commandline
   */
  @Override
  public String toShortCommandLine(Object obj) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(obj.getClass().getSimpleName());
    if (obj instanceof weka.core.OptionHandler) {
      result.append(" ");
      result.append(weka.core.Utils.joinOptions(((weka.core.OptionHandler) obj).getOptions()));
    }

    return result.toString().trim();
  }

  /**
   * Generates an options array from the specified object.
   *
   * @param obj		the object to create the array for
   * @return		the generated array
   */
  @Override
  public String[] toArray(Object obj) {
    List<String>	result;

    result = new ArrayList<String>();
    result.add(obj.getClass().getName());
    if (obj instanceof weka.core.OptionHandler)
      result.addAll(Arrays.asList(((weka.core.OptionHandler) obj).getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the commandline options (without classname) of the specified object.
   *
   * @param obj		the object to get the options from
   * @return		the options
   */
  @Override
  public String[] getOptions(Object obj) {
    return ((weka.core.OptionHandler) obj).getOptions();
  }

  /**
   * Sets the options of the specified object.
   *
   * @param obj		the object to set the options for
   * @param args	the options
   * @return		true if options successfully set
   */
  @Override
  public boolean setOptions(Object obj, String[] args) {
    boolean	result;

    try {
      ((weka.core.OptionHandler) obj).setOptions(args.clone());
      result = true;
    }
    catch (Exception e) {
      result = false;
    }

    return result;
  }

  /**
   * Splits the commandline into an array.
   *
   * @param cmdline	the commandline to split
   * @return		the generated array of options
   */
  @Override
  public String[] splitOptions(String cmdline) {
    String[]	result;

    try {
      result = weka.core.Utils.splitOptions(cmdline);
    }
    catch (Exception e) {
      result = new String[0];
    }

    return result;
  }

  /**
   * Turns the option array back into a commandline.
   *
   * @param args	the options to turn into a commandline
   * @return		the generated commandline
   */
  @Override
  public String joinOptions(String[] args) {
    return weka.core.Utils.joinOptions(args);
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		true if the handler can process the class
   */
  @Override
  public boolean handles(Class cls) {
    return (ClassLocator.hasInterface(weka.core.OptionHandler.class, cls));
  }
}
