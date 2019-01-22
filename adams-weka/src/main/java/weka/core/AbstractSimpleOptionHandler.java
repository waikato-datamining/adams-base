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
 * AbstractSimpleOptionHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.core;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.ClassOption;
import adams.core.option.OptionUtils;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Ancestor for Weka classes that use the ADAMS option handling framework.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleOptionHandler
  extends AbstractOptionHandler
  implements OptionHandler {

  private static final long serialVersionUID = -6267206505797613853L;

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  public Enumeration<Option> listOptions() {
    Vector<Option>	result;
    Option		opt;
    String		desc;
    String		flag;

    result = new Vector<>();
    for (AbstractOption option: getOptionManager().getOptionsList()) {
      try {
	desc = adams.core.Utils.flatten(adams.core.Utils.breakUp("" + option.getToolTipMethod().invoke(this), 60), "\n")
	  + "\n(default: " + option.getDefaultValue() + ")";
	desc = adams.core.Utils.commentOut(desc, "\t");
	flag = option.getCommandline();
	if (option instanceof ClassOption) {
	  opt = new Option(desc, flag, 1,
	    "-" + flag + " <" + adams.core.Utils.classToString(((ClassOption) option).getBaseClass()) + ">");
	}
	else if (option instanceof AbstractArgumentOption) {
	  opt = new Option(desc, flag, 1, "-" + flag + " <arg>");
	}
	else {
	  opt = new Option(desc, flag, 0, "-" + flag);
	}
	result.add(opt);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to process option: " + option);
      }
    }

    return result.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    ArrayConsumer	consumer;

    consumer = new ArrayConsumer();
    getOptionManager().setDefaults();
    consumer.consume(this, options);
    if (consumer.hasErrors())
      throw new IllegalArgumentException(adams.core.Utils.flatten(consumer.getErrors(), "\n"));
    consumer.cleanUp();
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  public String[] getOptions() {
    return OptionUtils.getOptions(this);
  }
}
