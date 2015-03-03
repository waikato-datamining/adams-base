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
 * FancyTickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * A fancy tick generator based on code from the 
 * <a href="http://www.flotcharts.org/" target="_blank">flotcharts</a> project.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2007-2013 IOLA and Ole Laursen",
    license = License.MIT,
    url = "https://github.com/flot/flot/blob/master/jquery.flot.js",
    note = "Original code was in JavaScript"
)
public class FancyTickGenerator
  extends AbstractLimitedTickGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3950212023344727427L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"A fancy tick generator based on code from the http://www.flotcharts.org/ project.\n"
	+ "It attempts to generate ticks at nice human-readable locations, e.g., '10, 20, 30, 40, 50'.";
  }

  /**
   * Round to nearby lower multiple of base.
   * 
   * @param n		the number
   * @param base	the base to use
   * @return		the result
   */
  protected double floorInBase(double n, double base) {
    return base * Math.floor(n / base);
  }

  /**
   * Generate the ticks of this axis.
   */
  @Override
  protected void doGenerate() {
    int 		noTicks;
    double 		delta;
    double 		dec;
    double 		magn;
    double 		norm;
    double 		size;
    double 		tickSize;
    double 		start;
    int 		i;
    double 		v;
    double 		prev;
    String 		label;

    // estimate number of ticks
    if (m_NumTicks > 0)
      noTicks = m_NumTicks;
    else
      // heuristic based on the model a*sqrt(x) fitted to
      // some data points that seemed reasonable
      noTicks = (int) (0.3 * Math.sqrt(m_Parent.getParent().getDirection() == Direction.VERTICAL ? m_Parent.getParent().getHeight() : m_Parent.getParent().getWidth()));

    delta  = (m_Parent.getActualMaximum() - m_Parent.getActualMinimum()) / noTicks;
    dec    = -Math.floor(Math.log(delta) / Math.log(10));
    magn   = Math.pow(10, -dec);
    norm   = delta / magn; // norm is between 1.0 and 10.0

    if (norm < 1.5) {
      size = 1;
    } 
    else if (norm < 3) {
      size = 2;
      // special case for 2.5, requires an extra decimal
      if (norm > 2.25) {
	size = 2.5;
	++dec;
      }
    } 
    else if (norm < 7.5) {
      size = 5;
    } 
    else {
      size = 10;
    }

    size    *= magn;
    tickSize = size;
    start    = floorInBase(m_Parent.getActualMinimum(), tickSize);
    i        = 0;
    v        = Double.NaN;

    do {
      prev = v;
      v    = start + i * tickSize;
      label = fixLabel(m_Parent.valueToDisplay(v));
      m_Ticks.add(new Tick(m_Parent.valueToPos(v), label));
      ++i;
      addLabel(label);
    } 
    while ((v < m_Parent.getActualMaximum()) && (v != prev));
  }
}