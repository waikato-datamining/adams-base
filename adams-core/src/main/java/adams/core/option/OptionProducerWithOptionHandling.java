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
 * OptionProducerWithOptionHandling.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Interface for option producers that offer option handling.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data that gets generated
 * @param <I> the internal type used while nesting
 */
public interface OptionProducerWithOptionHandling<O,I>
  extends OptionProducer<O,I>, OptionHandler {

  /**
   * Adds options to the internal list of options.
   * <br><br>
   * Every subclass needs to add the necessary Option objects to its internal
   * List with Option objects.
   */
  public void defineOptions();

  /**
   * Returns the option manager.
   * <br><br>
   * Only needs to be implemented in the superclass, which declares the
   * OptionManager object managing all the Option objects.
   *
   * @return		the internal option list
   */
  public OptionManager getOptionManager();

  /**
   * Cleans up the options.
   */
  public void cleanUpOptions();
}
