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
 * <pre>-rotation &lt;_0|_90|_180|_270&gt; (property: rotation)
 * &nbsp;&nbsp;&nbsp;The rotation in degrees.
 * &nbsp;&nbsp;&nbsp;default: _0
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

  /**
   * The Rotation in degrees.
   */
  public enum Rotation {
    _0,
    _90,
    _180,
    _270,
  }

  /** the rotation. */
  protected Rotation m_Rotation;

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
      "rotation", "rotation",
      Rotation._0);

    m_OptionManager.add(
      "image-width", "imageWidth",
      640, 1, null);

    m_OptionManager.add(
      "image-height", "imageHeight",
      480, 1, null);
  }

  /**
   * Sets the rotation in degrees.
   *
   * @param value	the rotation
   */
  public void setRotation(Rotation value) {
    m_Rotation = value;
    reset();
  }

  /**
   * Returns the rotation in degrees.
   *
   * @return		the rotation
   */
  public Rotation getRotation() {
    return m_Rotation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rotationTipText() {
    return "The rotation in degrees.";
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

    result  = QuickInfoHelper.toString(this, "rotation", m_Rotation, "degrees: ");

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
    int			wI;
    int			hI;

    result = new LocatedObjects();
    wI     = m_ImageWidth;
    hI     = m_ImageHeight;
    for (LocatedObject obj: objects) {
      x = obj.getX();
      y = obj.getY();
      w = obj.getWidth();
      h = obj.getHeight();

      // x/y
      switch (m_Rotation) {
	case _0:
	  xN = x;
	  yN = y;
	  break;
	case _90:
	  xN = hI - (y + h);
	  yN = x;
	  break;
	case _180:
	  xN = wI - x - w;
	  yN = hI - y - h;
	  break;
	case _270:
	  xN = y;
	  yN = wI - x - w;
	  break;
	default:
	  throw new IllegalStateException("Unhandled rotation: " + m_Rotation);
      }

      // width/height
      switch (m_Rotation) {
	case _0:
	case _180:
	  wN = w;
	  hN = h;
	  break;
	case _90:
	case _270:
	  wN = h;
	  hN = w;
	  break;
	default:
	  throw new IllegalStateException("Unhandled rotation: " + m_Rotation);
      }

      newObj = new LocatedObject(obj.getImage(), xN, yN, wN, hN, obj.getMetaData(true));
      result.add(newObj);
    }

    return result;
  }
}
