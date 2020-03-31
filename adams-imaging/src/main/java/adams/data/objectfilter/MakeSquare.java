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
 * MakeSquare.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 <!-- globalinfo-start -->
 * Generates a square bounding box around the current center of the bounding box.<br>
 * Discards any polygon data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-side-type &lt;SMALLER|LARGER|WIDTH|HEIGHT&gt; (property: sideType)
 * &nbsp;&nbsp;&nbsp;The type of side of the current bounding box to use for the square box.
 * &nbsp;&nbsp;&nbsp;default: SMALLER
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MakeSquare
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -2181381799680316619L;

  /** the side to use. */
  public enum SideType {
    SMALLER,
    LARGER,
    WIDTH,
    HEIGHT,
  }

  /** what side to use. */
  protected SideType m_SideType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a square bounding box around the current center of the bounding box.\n"
      + "Discards any polygon data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "side-type", "sideType",
      SideType.SMALLER);
  }

  /**
   * Sets the type of side of the current bounding box to use for the square box.
   *
   * @param value	the type
   */
  public void setSideType(SideType value) {
    m_SideType = value;
    reset();
  }

  /**
   * Returns the type of side of the current bounding box to use for the square box.
   *
   * @return		the type
   */
  public SideType getSideType() {
    return m_SideType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sideTypeTipText() {
    return "The type of side of the current bounding box to use for the square box.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "sideType", m_SideType, "side: ");

    return result;
  }

  /**
   * Scales/translates the given object.
   *
   * @param object	the object to transform
   * @param scaleX	the scale for X
   * @param scaleY	the scale for Y
   * @param transX	the X translation
   * @param transY	the Y translation
   * @return		the updated object
   */
  protected LocatedObject transform(LocatedObject object, double scaleX, double scaleY, int transX, int transY) {
    LocatedObject	result;

    result = new LocatedObject(
      object.getX() + transX,
      object.getY() + transY,
      (int) (object.getWidth() * scaleX),
      (int) (object.getHeight() * scaleY));
    result.getMetaData().putAll(object.getMetaData());

    // remove polygon
    result.getMetaData().remove(LocatedObject.KEY_POLY_X);
    result.getMetaData().remove(LocatedObject.KEY_POLY_Y);

    if (isLoggingEnabled())
      getLogger().info(object + " -> " + result);

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
    int			w;
    int			h;
    SideType		type;
    double		scaleX;
    double		scaleY;
    int			transX;
    int			transY;

    result = new LocatedObjects();

    for (LocatedObject obj: objects) {
      w = obj.getWidth();
      h = obj.getHeight();

      // determine actual type
      type = m_SideType;
      switch (m_SideType) {
	case SMALLER:
	  if (w < h)
	    type = SideType.WIDTH;
	  else
	    type = SideType.HEIGHT;
	  break;
	case LARGER:
	  if (w > h)
	    type = SideType.WIDTH;
	  else
	    type = SideType.HEIGHT;
	  break;
      }

      switch (type) {
	case WIDTH:
	  scaleX = 1.0;
	  scaleY = (double) w / (double) h;
	  transX = 0;
	  transY = -(w - h) / 2;
	  break;
	case HEIGHT:
	  scaleX = (double) h / (double) w;
	  scaleY = 1.0;
	  transX = -(h - w) / 2;
	  transY = 0;
	  break;
	default:
	  throw new IllegalStateException("Unhandled side type: " + m_SideType);
      }

      if (isLoggingEnabled())
        getLogger().info("w=" + w + ", h=" + h + " -> scaleX=" + scaleX + ", scaleY=" + scaleY + ", transX=" + transX + ", transY=" + transY);

      result.add(transform(obj, scaleX, scaleY, transX, transY));
    }

    return result;
  }
}
