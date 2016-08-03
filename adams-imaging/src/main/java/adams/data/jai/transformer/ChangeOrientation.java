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
 * ChangeOrientation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.jai.transformer;

import adams.data.PageOrientation;
import adams.data.image.BufferedImageContainer;
import adams.data.jai.transformer.Rotate.InterpolationType;

/**
 <!-- globalinfo-start -->
 * Ensures that the image has the specified orientation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-orientation &lt;PORTRAIT|LANDSCAPE&gt; (property: orientation)
 * &nbsp;&nbsp;&nbsp;The page orientation to ensure.
 * &nbsp;&nbsp;&nbsp;default: LANDSCAPE
 * </pre>
 * 
 * <pre>-angle-landscape-to-portrait &lt;double&gt; (property: angleLandscapeToPortrait)
 * &nbsp;&nbsp;&nbsp;The angle, in degrees, to rotate the image by (landscape -&gt; portrait).
 * &nbsp;&nbsp;&nbsp;default: 90.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 360.0
 * </pre>
 * 
 * <pre>-angle-portrait-to-landscape &lt;double&gt; (property: anglePortraitToLandscape)
 * &nbsp;&nbsp;&nbsp;The angle, in degrees, to rotate the image by (portrait -&gt; landscape).
 * &nbsp;&nbsp;&nbsp;default: 90.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 360.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ChangeOrientation
  extends AbstractJAITransformer {

  private static final long serialVersionUID = 789668617409423108L;

  /** the orientation for the image. */
  protected PageOrientation m_Orientation;

  /** the rotation angle for portrait to landscape. */
  protected double m_AnglePortraitToLandscape;

  /** the rotation angle for landscape to portrait. */
  protected double m_AngleLandscapeToPortrait;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ensures that the image has the specified orientation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "orientation", "orientation",
      PageOrientation.LANDSCAPE);

    m_OptionManager.add(
      "angle-landscape-to-portrait", "angleLandscapeToPortrait",
      90.0, 0.0, 360.0);

    m_OptionManager.add(
      "angle-portrait-to-landscape", "anglePortraitToLandscape",
      90.0, 0.0, 360.0);
  }

  /**
   * Sets the page orientation.
   *
   * @param value	the orientation
   */
  public void setOrientation(PageOrientation value) {
    m_Orientation = value;
    reset();
  }

  /**
   * Returns the page orientation.
   *
   * @return		the orientation
   */
  public PageOrientation getOrientation() {
    return m_Orientation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String orientationTipText() {
    return "The page orientation to ensure.";
  }

  /**
   * Sets the rotation angle (landscape -> portrait).
   *
   * @param value	the angle (0-360)
   */
  public void setAngleLandscapeToPortrait(double value) {
    if (getOptionManager().isValid("angleLandscapeToPortrait", value)) {
      m_AngleLandscapeToPortrait = value;
      reset();
    }
  }

  /**
   * Returns the rotation angle (landscape -> portrait).
   *
   * @return		the angle
   */
  public double getAngleLandscapeToPortrait() {
    return m_AngleLandscapeToPortrait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String angleLandscapeToPortraitTipText() {
    return "The angle, in degrees, to rotate the image by (landscape -> portrait).";
  }

  /**
   * Sets the rotation angle (portrait -> landscape).
   *
   * @param value	the angle (0-360)
   */
  public void setAnglePortraitToLandscape(double value) {
    if (getOptionManager().isValid("anglePortraitToLandscape", value)) {
      m_AnglePortraitToLandscape = value;
      reset();
    }
  }

  /**
   * Returns the rotation angle (portrait -> landscape).
   *
   * @return		the angle
   */
  public double getAnglePortraitToLandscape() {
    return m_AnglePortraitToLandscape;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String anglePortraitToLandscapeTipText() {
    return "The angle, in degrees, to rotate the image by (portrait -> landscape).";
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    boolean			change;
    Rotate			rotate;

    switch (m_Orientation) {
      case LANDSCAPE:
	change = (img.getHeight() > img.getWidth());
	break;
      case PORTRAIT:
	change = (img.getWidth() > img.getHeight());
	break;
      default:
	throw new IllegalStateException("Unhandled page orientation: " + m_Orientation);
    }

    if (change) {
      rotate = new Rotate();
      if (m_Orientation == PageOrientation.LANDSCAPE)
	rotate.setAngle(m_AnglePortraitToLandscape);
      else
	rotate.setAngle(m_AngleLandscapeToPortrait);
      if (isLoggingEnabled())
	getLogger().info("Orientation needs to be adjusted to " + m_Orientation + " by " + rotate.getAngle() + " degrees");
      rotate.setInterpolation(InterpolationType.NONE);
      result = rotate.transform(img);
      rotate.cleanUp();
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Orientation is already " + m_Orientation);
      result    = new BufferedImageContainer[1];
      result[0] = img;
    }

    return result;
  }
}
