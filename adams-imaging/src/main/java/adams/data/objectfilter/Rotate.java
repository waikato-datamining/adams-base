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
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 <!-- globalinfo-start -->
 * Rotates the objects using the specified degrees.<br>
 * Requires the original image width before the image got rotated in order to rotate the objects correctly.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-angle &lt;double&gt; (property: angle)
 * &nbsp;&nbsp;&nbsp;The rotation angle in degrees.
 * &nbsp;&nbsp;&nbsp;default: 0.0
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
  protected double m_Angle;

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
    return "Rotates the objects using the specified degrees.\n"
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
      0.0);

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
  public void setAngle(double value) {
    m_Angle = value;
    reset();
  }

  /**
   * Returns the rotation angle in degrees.
   *
   * @return		the angle
   */
  public double getAngle() {
    return m_Angle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String angleTipText() {
    return "The rotation angle in degrees.";
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
   * Filters the image objects.
   *
   * @param objects	the located objects
   * @return		the updated list of objects
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    LocatedObject	newObj;
    int			x;
    int			y;
    int			w;
    int			h;
    int			xN;
    int			yN;
    int			wN;
    int			hN;
    double		theta;
    boolean		hasPoly;
    Polygon poly;
    Point2D tl;
    Point2D		br;
    Point2D		tlN;
    Point2D 		brN;
    AffineTransform trans;
    int[]		xpoints;
    int[]		ypoints;
    Point2D[]		polyP;
    Point2D[]		polyPN;
    int			i;

    result = new LocatedObjects();
    theta  = m_Angle / 180 * Math.PI;
    trans  = AffineTransform.getRotateInstance(theta, m_ImageWidth / 2, m_ImageHeight / 2);

    for (LocatedObject obj: objects) {
      x       = obj.getX();
      y       = obj.getY();
      w       = obj.getWidth();
      h       = obj.getHeight();
      tl      = new Point2D.Double(x, y);
      br      = new Point2D.Double(x + w - 1, y + h - 1);
      tlN     = new Point2D.Double();
      brN     = new Point2D.Double();
      polyP   = new Point2D[0];
      polyPN  = new Point2D[0];
      hasPoly = obj.hasPolygon();
      if (hasPoly) {
	poly    = obj.getPolygon();
	xpoints = poly.xpoints;
	ypoints = poly.ypoints;
	polyP   = new Point2D[xpoints.length];
	polyPN  = new Point2D[xpoints.length];
	for (i = 0; i < xpoints.length; i++) {
	  polyP[i]  = new Point2D.Double(xpoints[i], ypoints[i]);
	  polyPN[i] = new Point2D.Double();
	}
      }

      trans.transform(tl, tlN);
      trans.transform(br, brN);
      if (hasPoly)
	trans.transform(polyP, 0, polyPN, 0, polyP.length);

      xN = (int) tlN.getX();
      yN = (int) tlN.getY();
      wN = (int) (brN.getX() - tlN.getX() + 1);
      hN = (int) (brN.getY() - tlN.getY() + 1);

      newObj = new LocatedObject(obj.getImage(), xN, yN, wN, hN, obj.getMetaData(true));

      if (hasPoly) {
        xpoints = new int[polyPN.length];
        ypoints = new int[polyPN.length];
        for (i = 0; i < polyPN.length; i++) {
          xpoints[i] = (int) polyPN[i].getX();
          ypoints[i] = (int) polyPN[i].getY();
	}
	newObj.setPolygon(new Polygon(xpoints, ypoints, xpoints.length));
      }
      result.add(newObj);
    }

    return result;
  }
}
