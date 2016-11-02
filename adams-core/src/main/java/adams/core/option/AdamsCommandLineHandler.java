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
 * AdamsCommandLineHandler.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.ClassLocator;

/**
 * Handles ADAMS-related commandlines, i.e., classes implementing
 * adams.core.option.OptionHandler.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see OptionHandler
 */
public class AdamsCommandLineHandler
  extends AbstractCommandLineHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1447070701753485854L;

  /**
   * Generates an object from the specified commandline.
   *
   * @param cmd		the commandline to create the object from
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromCommandLine(String cmd) {
    return AbstractOptionConsumer.fromString(ArrayConsumer.class, cmd);
  }

  /**
   * Generates an object from the commandline options.
   *
   * @param args	the commandline options to create the object from
   * @return		the created object, null in case of error
   */
  @Override
  public Object fromArray(String[] args) {
    Object		result;
    ArrayConsumer	consumer;

    consumer = new ArrayConsumer();
    consumer.setInput(args);
    result = consumer.consume();
    consumer.cleanUp();

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
    return AbstractOptionProducer.toString(ArrayProducer.class, (OptionHandler) obj);
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
    String	result;
    
    result = toCommandLine(obj);
    result = result.substring(obj.getClass().getPackage().getName().length() + 1);
    
    return result;
  }

  /**
   * Generates an options array from the specified object.
   *
   * @param obj		the object to create the array for
   * @return		the generated array
   */
  @Override
  public String[] toArray(Object obj) {
    String[]		result;
    ArrayProducer	producer;

    producer = new ArrayProducer();
    result   = producer.produce((OptionHandler) obj);

    return result;
  }

  /**
   * Returns the commandline options (without classname) of the specified object.
   *
   * @param obj		the object to get the options from
   * @return		the options
   */
  @Override
  public String[] getOptions(Object obj) {
    String[]		result;
    String[]		array;

    array  = toArray(obj);
    result = new String[array.length - 1];
    if (result.length > 0)
      System.arraycopy(array, 1, result, 0, result.length);

    return result;
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
    boolean		result;
    ArrayConsumer	consumer;

    consumer = new ArrayConsumer();
    ((OptionHandler) obj).getOptionManager().setDefaults();
    consumer.consume((OptionHandler) obj, args);
    result = !consumer.hasErrors();
    consumer.cleanUp();

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
      result = OptionUtils.splitOptions(cmdline);
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
    return OptionUtils.joinOptions(args);
  }

  /**
   * Checks whether the given class can be processed.
   *
   * @param cls		the class to inspect
   * @return		true if the handler can process the class
   */
  @Override
  public boolean handles(Class cls) {
    return (ClassLocator.hasInterface(OptionHandler.class, cls));
  }
}
