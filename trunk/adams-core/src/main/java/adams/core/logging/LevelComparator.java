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
 * LevelComparator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * Comparator for logging levels.
 * <p/>
 * The following order is used:
 * <pre>
 * FINEST > FINER > FINE > CONFIG > INFO > WARNING > SEVERE > OFF
 * </pre>
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LevelComparator
  implements Comparator<Level> {

  /**
   * Turns the level into an integer representation.
   * 
   * @param level	the level to process
   * @return		the integer equivalent
   */
  protected Integer toInteger(Level level) {
    if (level == Level.SEVERE)
      return 20;
    else if (level == Level.WARNING)
      return 30;
    else if (level == Level.INFO)
      return 40;
    else if (level == Level.CONFIG)
      return 50;
    else if (level == Level.FINE)
      return 60;
    else if (level == Level.FINER)
      return 70;
    else if (level == Level.FINEST)
      return 80;
    else
      return 0;
  }
  
  /**
   * Compares its two arguments for order.  Returns a negative integer,
   * zero, or a positive integer as the first argument is less than, equal
   * to, or greater than the second.
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the
   * 	       first argument is less than, equal to, or greater than the
   *	       second.
   */
  @Override
  public int compare(Level o1, Level o2) {
    return toInteger(o1).compareTo(toInteger(o2));
  }
}
