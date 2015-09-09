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
 * AbstractDistanceBasedHitDetector.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2008 Pieter Iserbyt <pieter.iserbyt@gmail.com> (distance calculation between point and line)
 */

package adams.gui.visualization.core.plot;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

import java.awt.geom.Point2D;

/**
 * Detects hits based on the difference between two points in the plot,
 * based on their 2-dim coordinates.
 * <br><br>
 * Distance calcuation between point and line see <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/">here</a>
 * and <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/DistancePoint.java">DistancePoint.java</a>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Pieter Iserbyt (distance calculation between point and line)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2008 Pieter Iserbyt <pieter.iserbyt@gmail.com>",
    license = License.GPL3,
    url = "http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/DistancePoint.java",
    note = "Distance calcuation between point and line"
)
public abstract class AbstractDistanceBasedHitDetector
  extends AbstractHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = -5861816297859396332L;

  /** the minimum pixel difference to use for determining hit or miss. */
  protected int m_MinimumPixelDifference;

  /**
   * Initializes the hit detector.
   */
  public AbstractDistanceBasedHitDetector() {
    super();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-pixel-difference", "minimumPixelDifference",
      getDefaultMinimumPixelDifference());
  }

  /**
   * Returns the default minimum pixel difference.
   *
   * @return		the minimum
   */
  protected int getDefaultMinimumPixelDifference() {
    return 2;
  }

  /**
   * Sets the minimum pixel difference before a hit is declared.
   *
   * @param value	the number of pixels
   */
  public void setMinimumPixelDifference(int value) {
    if (value >= 0)
      m_MinimumPixelDifference = value;
    else
      System.err.println(getClass().getName() + ": minimum pixel different must be >= 0!");
  }

  /**
   * Returns the minimum pixel difference before a hit is declared.
   *
   * @return		the number of pixels
   */
  public int getMinimumPixelDifference() {
    return m_MinimumPixelDifference;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumPixelDifferenceDetectorTipText() {
    return "The minimum pixel difference to use.";
  }

  /**
   * Returns the distance of p3 to the segment defined by p1,p2.
   *
   * @param p1		First point of the segment
   * @param p2		Second point of the segment
   * @param p3		Point to which we want to know the distance of the segment
   * 			defined by p1,p2
   * @return 		The distance of p3 to the segment defined by p1,p2
   */
  protected double distance(Point2D p1, Point2D p2, Point2D p3) {
    final double xDelta = p2.getX() - p1.getX();
    final double yDelta = p2.getY() - p1.getY();

    if (isLoggingEnabled())
      getLogger().info("p1=" + p1 + ", p2=" + p2 + ", p3=" + p3);

    if ((xDelta == 0) && (yDelta == 0))
      return p1.distance(p3);

    final double u = ((p3.getX() - p1.getX()) * xDelta + (p3.getY() - p1.getY()) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

    final Point2D closestPoint;
    if (u < 0)
      closestPoint = p1;
    else if (u > 1)
      closestPoint = p2;
    else
      closestPoint = new Point2D.Double(p1.getX() + u * xDelta, p1.getY() + u * yDelta);

    return closestPoint.distance(p3);
  }
}