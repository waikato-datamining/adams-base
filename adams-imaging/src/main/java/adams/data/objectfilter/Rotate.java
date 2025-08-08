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
 * Rotate.java
 * Copyright (C) 2018-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Polygon;

/**
 <!-- globalinfo-start -->
 * Rotates the objects using the specified degrees (90 degree increments only).<br>
 * Requires the original image width before the image got rotated in order to rotate the objects correctly.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-angle &lt;int&gt; (property: angle)
 * &nbsp;&nbsp;&nbsp;The rotation angle in degrees (90 degree increments only).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 360
 * </pre>
 *
 * <pre>-image-width &lt;int&gt; (property: imageWidth)
 * &nbsp;&nbsp;&nbsp;The original image width before rotation.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-image-height &lt;int&gt; (property: imageHeight)
 * &nbsp;&nbsp;&nbsp;The original image height before rotation.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Rotate
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -2181381799680316619L;

  /** the rotation in degrees. */
  protected int m_Angle;

  /** the original image width. */
  protected int m_ImageWidth;

  /** the original image height. */
  protected int m_ImageHeight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rotates the objects using the specified degrees (90 degree increments only).\n"
	     + "Requires the original image width before the image got rotated in order to rotate the objects correctly.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "angle", "angle",
      0, 0, 360);

    m_OptionManager.add(
      "image-width", "imageWidth",
      640, 1, null);

    m_OptionManager.add(
      "image-height", "imageHeight",
      480, 1, null);
  }

  /**
   * Sets the rotation angle in degrees.
   *
   * @param value	the angle
   */
  public void setAngle(int value) {
    if (getOptionManager().isValid("angle", value) && (value % 90 == 0)) {
      m_Angle = value;
      reset();
    }
  }

  /**
   * Returns the rotation angle in degrees.
   *
   * @return		the angle
   */
  public int getAngle() {
    return m_Angle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String angleTipText() {
    return "The rotation angle in degrees (90 degree increments only).";
  }

  /**
   * Sets the original image width before rotation.
   *
   * @param value	the width
   */
  public void setImageWidth(int value) {
    if (getOptionManager().isValid("imageWidth", value)) {
      m_ImageWidth = value;
      reset();
    }
  }

  /**
   * Returns the original image width before rotation.
   *
   * @return		the width
   */
  public int getImageWidth() {
    return m_ImageWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageWidthTipText() {
    return "The original image width before rotation.";
  }

  /**
   * Sets the original image height before rotation.
   *
   * @param value	the height
   */
  public void setImageHeight(int value) {
    if (getOptionManager().isValid("imageHeight", value)) {
      m_ImageHeight = value;
      reset();
    }
  }

  /**
   * Returns the original image height before rotation.
   *
   * @return		the height
   */
  public int getImageHeight() {
    return m_ImageHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageHeightTipText() {
    return "The original image height before rotation.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "angle", m_Angle, "angle: ");

    return result;
  }

  /**
   * Returns the new X location.
   *
   * @param x		the original x
   * @param y 		the original y
   * @return		the rotated x
   */
  protected int rotateX(int x, int y) {
    int		xN;
    int		angle;

    angle = m_Angle % 360;
    switch (angle) {
      case 0:
	xN = x;
	break;
      case 90:
	xN = m_ImageHeight - y - 1;
	break;
      case 180:
	xN = m_ImageWidth - x - 1;
	break;
      case 270:
	xN = y;
	break;
      default:
	throw new IllegalStateException("Invalid angle: " + m_Angle);
    }

    return xN;
  }

  /**
   * Returns the new Y location.
   *
   * @param x		the original x
   * @param y 		the original y
   * @return		the rotated y
   */
  protected int rotateY(int x, int y) {
    int		yN;
    int		angle;

    angle = m_Angle % 360;
    switch (angle) {
      case 0:
	yN = y;
	break;
      case 90:
	yN = x;
	break;
      case 180:
	yN = m_ImageHeight - y - 1;
	break;
      case 270:
	yN = m_ImageWidth - x - 1;
	break;
      default:
	throw new IllegalStateException("Invalid angle: " + m_Angle);
    }

    return yN;
  }

  /**
   * Computes the new X for the obj.
   *
   * @param obj		the obj to rotate
   * @return		the new X
   */
  protected int newX(LocatedObject obj) {
    int		xN;
    int		angle;

    angle = m_Angle % 360;
    switch (angle) {
      case 0: // same
	xN = obj.getX();
	break;
      case 90: // left/bottom
	xN = rotateX(obj.getX(), obj.getY() + obj.getHeight() - 1);
	break;
      case 180: // right/bottom
	xN = rotateX(obj.getX() + obj.getWidth() - 1, obj.getY() + obj.getHeight() - 1);
	break;
      case 270: // right/top
	xN = rotateX(obj.getX() + obj.getWidth() - 1, obj.getY());
	break;
      default:
	throw new IllegalStateException("Invalid angle: " + m_Angle);
    }

    return xN;
  }

  /**
   * Computes the new Y for the obj.
   *
   * @param obj		the obj to rotate
   * @return		the new Y
   */
  protected int newY(LocatedObject obj) {
    int 	yN;
    int		angle;

    angle = m_Angle % 360;
    switch (angle) {
      case 0: // same
	yN = obj.getY();
	break;
      case 90: // left/bottom
	yN = rotateY(obj.getX(), obj.getY() + obj.getHeight() - 1);
	break;
      case 180: // right/bottom
	yN = rotateY(obj.getX() + obj.getWidth() - 1, obj.getY() + obj.getHeight() - 1);
	break;
      case 270: // right/top
	yN = rotateY(obj.getX() + obj.getWidth() - 1, obj.getY());
	break;
      default:
	throw new IllegalStateException("Invalid angle: " + m_Angle);
    }

    return yN;
  }

  /**
   * Computes the new width for the obj.
   *
   * @param obj		the obj to rotate
   * @return		the new width
   */
  protected int newW(LocatedObject obj) {
    int		wN;
    int		angle;

    angle = m_Angle % 360;
    switch (angle) {
      case 0:
      case 180:
	wN = obj.getWidth();
	break;
      case 90:
      case 270:
	wN = obj.getHeight();
	break;
      default:
	throw new IllegalStateException("Invalid angle: " + m_Angle);
    }

    return wN;
  }

  /**
   * Computes the new height for the obj.
   *
   * @param obj		the obj to rotate
   * @return		the new height
   */
  protected int newH(LocatedObject obj) {
    int 	hN;
    int		angle;

    angle = m_Angle % 360;
    switch (angle) {
      case 0:
      case 180:
	hN = obj.getHeight();
	break;
      case 90:
      case 270:
	hN = obj.getWidth();
	break;
      default:
	throw new IllegalStateException("Invalid angle: " + m_Angle);
    }

    return hN;
  }

  /**
   * Filters the image objects.
   *
   * @param objects	the located objects
   * @return		the updated list of objects
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    LocatedObject	newObj;
    int[]		xpoints;
    int[]		ypoints;
    int			i;

    result = new LocatedObjects();

    for (LocatedObject obj: objects) {
      newObj = new LocatedObject(
	obj.getImage(),
	newX(obj),
	newY(obj),
	newW(obj),
	newH(obj),
	obj.getMetaData(true));

      if (obj.hasPolygon()) {
	xpoints = obj.getPolygonX();
	ypoints = obj.getPolygonY();
	for (i = 0; i < xpoints.length; i++) {
	  xpoints[i] = rotateX(xpoints[i], ypoints[i]);
	  ypoints[i] = rotateY(xpoints[i], ypoints[i]);
	}
	newObj.setPolygon(new Polygon(xpoints, ypoints, xpoints.length));
      }

      result.add(newObj);
    }

    return result;
  }
}
