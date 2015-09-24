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
 * AdaptiveResize.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.im;

import org.im4java.core.IMOperation;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Adaptive resize (option -adaptive-resize)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width in pixels; -1 for current width.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height in pixels; -1 for current height.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-geometry &lt;java.lang.String&gt; (property: geometry)
 * &nbsp;&nbsp;&nbsp;The geometry specification character, empty or one of the following characters:
 * &nbsp;&nbsp;&nbsp; &lt;&gt;!&#64;%
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdaptiveResize
  extends AbstractIMSimpleOperation {

  /** for serialization. */
  private static final long serialVersionUID = 3529048936510645338L;

  /** the geomtry characters. */
  public final static String GEOMETRY_CHARS = "<>!@%";
  
  /** the width. */
  protected int m_Width;

  /** the height. */
  protected int m_Height;

  /** the geometry. */
  protected String m_Geometry;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adaptive resize (option -adaptive-resize)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    -1, -1, null);

    m_OptionManager.add(
	    "height", "height",
	    -1, -1, null);

    m_OptionManager.add(
	    "geometry", "geometry",
	    "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "width", m_Width, "width: ");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", height: ");
    
    return result;
  }

  /**
   * Sets the width in pixels.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if ((value == -1) || (value > 0)) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().warning("Width must be -1 or >0, provided: " + value);
    }
  }

  /**
   * Returns the width in pixels.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width in pixels; -1 for current width.";
  }

  /**
   * Sets the height in pixels.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    if ((value == -1) || (value > 0)) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().warning("Height must be -1 or >0, provided: " + value);
    }
  }

  /**
   * Returns the height in pixels.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height in pixels; -1 for current height.";
  }

  /**
   * Sets the geometry specification.
   *
   * @param value	the geometry
   * @see		#GEOMETRY_CHARS
   */
  public void setGeometry(String value) {
    value = value.trim();
    if (    (value.length() == 0) 
	 || ((value.length() == 1) && (GEOMETRY_CHARS.indexOf(value.charAt(0)) > -1))) {
      m_Geometry = value;
      reset();
    }
    else {
      getLogger().warning(
	  "Geometry specfication must be empty or one of the following "
	      + "characters: " + GEOMETRY_CHARS);
    }
  }

  /**
   * Returns the geometry specification.
   *
   * @return		the geometry
   * @see		#GEOMETRY_CHARS
   */
  public String getGeometry() {
    return m_Geometry;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String geometryTipText() {
    return 
	"The geometry specification character, empty or one of the following "
	+ "characters: " + GEOMETRY_CHARS;
  }

  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  @Override
  protected void addOperation(IMOperation op) {
    if (m_Height == -1) {
      if (m_Width == -1)
	op.adaptiveResize();
      else
	op.adaptiveResize(m_Width);
    }
    else {
      if (m_Geometry.length() == 0)
	op.adaptiveResize(m_Width, m_Height);
      else
	op.adaptiveResize(m_Width, m_Height, m_Geometry);
    }
  }
}
