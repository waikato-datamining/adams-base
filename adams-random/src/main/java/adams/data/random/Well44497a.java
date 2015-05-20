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
 * Well44497a.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import org.apache.commons.math3.random.RandomGenerator;

/**
 <!-- globalinfo-start -->
 * This class implements the Well44497a pseudo-random number generator from Francois Panneton, Pierre L'Ecuyer and Makoto Matsumoto
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the random number generator.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-generate-doubles &lt;boolean&gt; (property: generateDoubles)
 * &nbsp;&nbsp;&nbsp;If enabled, doubles instead of integers are returned.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-min-value &lt;int&gt; (property: minValue)
 * &nbsp;&nbsp;&nbsp;The smallest integer that could be generated.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-max-value &lt;int&gt; (property: maxValue)
 * &nbsp;&nbsp;&nbsp;The largest integer that could be generated.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see org.apache.commons.math3.random.Well44497a
 */
public class Well44497a
  extends AbstractCommonsRandomNumberGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 7025706231165646059L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"This class implements the Well44497a pseudo-random number generator "
	+ "from Francois Panneton, Pierre L'Ecuyer and Makoto Matsumoto";
  }

  /**
   * The underlying random number generator.
   * 
   * @return		the configured generator
   */
  @Override
  public RandomGenerator getRandomGenerator() {
    return new org.apache.commons.math3.random.Well44497a(m_Seed);
  }
}
