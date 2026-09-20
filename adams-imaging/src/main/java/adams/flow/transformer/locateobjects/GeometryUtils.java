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
 * GeometryUtils.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.locateobjects;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

import java.awt.Polygon;
import java.awt.geom.Line2D;

/**
 * Geometry related helper methods.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GeometryUtils {

  /**
   * Checks whether any line segments of the polygon intersect with each other.
   *
   * @param poly	the polygon to check
   * @return		true if self-intersects
   */
  @MixedCopyright(
    author = "https://stackoverflow.com/users/5334403/para-parasolian",
    license = License.CC_BY_SA_4,
    url = "https://stackoverflow.com/a/61160160/4698227"
  )
  public static boolean selfIntersects(Polygon poly) {
    int 	len;
    int		i;
    int		j;
    boolean 	cut;

    if (poly == null)
      return false;

    len = poly.npoints;

    // no cross-over if len < 4
    if (len < 4)
      return false;

    for (i = 0; i < len-1; i++) {
      for (j = i+2; j < len; j++)
      {
	// eliminate combinations already checked or not valid
	if ((i == 0) && (j == (len-1)))
	  continue;

	cut = Line2D.linesIntersect(
	  poly.xpoints[i], poly.ypoints[i],
	  poly.xpoints[i+1], poly.ypoints[i+1],
	  poly.xpoints[j], poly.ypoints[j],
	  poly.xpoints[(j+1) % len], poly.ypoints[(j+1) % len]);

	if (cut)
	  return true;
      }
    }

    return false;
  }
}
