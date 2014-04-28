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
 * InformativeStatistic.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import java.util.Iterator;


/**
 * Interface for statistics classes that can return several statistical
 * values.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InformativeStatistic {

  /**
   * Returns a description for this statistic.
   * 
   * @return		the description
   */
  public String getStatisticDescription();
  
  /**
   * Returns all the names of the available statistical values.
   * 
   * @return		the iterator over the names
   */
  public Iterator<String> statisticNames();
  
  /**
   * Returns the statistical value for the given statistic name.
   * 
   * @param name	the name of the statistical value
   * @return		the corresponding value
   */
  public double getStatistic(String name);
}
