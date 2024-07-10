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
 * Jitter.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence.pointpreprocessor;

import adams.core.Randomizable;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.AxisPanel;

import java.util.Random;

/**
 * Adds random jitter to data points, to make it easier to see overlapping ones.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Jitter
  extends AbstractPointPreprocessor
  implements Randomizable {

  private static final long serialVersionUID = 4238248242804099429L;

  /**
   * In which direction to apply the jitter.
   */
  public enum Direction {
    X,
    Y,
    X_AND_Y,
  }

  /** the seed. */
  protected long m_Seed;

  /** the minimum jitter in pixels. */
  protected int m_Min;

  /** the maximum jitter in pixels. */
  protected int m_Max;

  /** the direction to apply the jitter to. */
  protected Direction m_Direction;

  /** the random number generator. */
  protected Random m_Random;

  /** the range of the jitter. */
  protected int m_Range;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds random jitter to data points, to make it easier to see overlapping ones.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "min", "min",
      -5);

    m_OptionManager.add(
      "max", "max",
      5);

    m_OptionManager.add(
      "direction", "direction",
      Direction.X_AND_Y);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed value for the randomization.";
  }

  /**
   * Sets the minimum jitter to apply.
   *
   * @param value	the minimum
   */
  public void setMin(int value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum jitter to apply.
   *
   * @return  		the minimum
   */
  public int getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minTipText() {
    return "The minimum jitter to apply.";
  }

  /**
   * Sets the maximum jitter to apply.
   *
   * @param value	the maximum
   */
  public void setMax(int value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum jitter to apply.
   *
   * @return  		the maximum
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "The maximum jitter to apply.";
  }

  /**
   * Sets the direction of the jitter.
   *
   * @param value	the direction
   */
  public void setDirection(Direction value) {
    m_Direction = value;
    reset();
  }

  /**
   * Returns the direction of the jitter.
   *
   * @return  		the direction
   */
  public Direction getDirection() {
    return m_Direction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directionTipText() {
    return "The direction in which to apply the jitter to.";
  }

  /**
   * Resets the processor for another sequence.
   */
  @Override
  public void resetPreprocessor() {
    super.resetPreprocessor();
    m_Random = null;
  }

  /**
   * Preprocesses the point.
   *
   * @param point the point to process
   * @param axisX the X axis to use
   * @param axisY the Y axis to use
   * @return the new point
   */
  @Override
  protected XYSequencePoint doPreprocess(XYSequencePoint point, AxisPanel axisX, AxisPanel axisY) {
    XYSequencePoint	result;
    double		dx;
    double		dy;
    double		x;
    double		y;
    int			jitter;

    // nothing to do?
    if ((m_Min == 0) && (m_Max ==  0))
      return point;

    if (m_Random == null) {
      m_Random = new Random(m_Seed);
      m_Range  = m_Max - m_Min + 1;
    }

    x = point.getX();
    y = point.getY();

    jitter = m_Random.nextInt(m_Range) + m_Min;

    // jitter x
    if ((m_Direction == Direction.X) || (m_Direction == Direction.X_AND_Y)) {
      dx = axisX.posToValue(1) - axisX.posToValue(0);
      x  = x + jitter*dx;
    }

    // jitter y
    if ((m_Direction == Direction.Y) || (m_Direction == Direction.X_AND_Y)) {
      dy = axisY.posToValue(1) - axisY.posToValue(0);
      y  = y + jitter*dy;
    }

    result = (XYSequencePoint) point.getClone();
    result.setX(x);
    result.setY(y);

    return result;
  }
}
