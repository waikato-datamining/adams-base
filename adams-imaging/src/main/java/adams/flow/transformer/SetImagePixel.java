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
 * SetImagePixel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.awt.Color;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Draws a pixel at the specified location onto the image passing through.<br/>
 * <br/>
 * DEPRECATED<br/>
 * Use adams.flow.transformer.Draw with adams.flow.transformer.draw.Pixel instead.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.jai.BufferedImageContainer<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SetImagePixel
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the pixel (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the pixel (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-type &lt;RGBA|COLOR&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of value to use for the pixel.
 * &nbsp;&nbsp;&nbsp;default: RGBA
 * </pre>
 * 
 * <pre>-rgba &lt;int&gt; (property: RGBA)
 * &nbsp;&nbsp;&nbsp;The RGBA value (&gt;= 0).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the pixel.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class SetImagePixel
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -992178802927111511L;

  /**
   * Enumeration that determines what value to use for the pixel.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PixelValueType {
    /** uses the RGBA value. */
    RGBA,
    /** uses the Color value. */
    COLOR
  }
  
  /** the X position of the pixel (1-based). */
  protected int m_X;

  /** the Y position of the pixel (1-based). */
  protected int m_Y;

  /** what type of value to use for the pixel. */
  protected PixelValueType m_Type;
  
  /** the RGBA value of the pixel. */
  protected int m_RGBA;
  
  /** the color of the pixel. */
  protected Color m_Color;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Draws a pixel at the specified location onto the image passing through.\n\n"
	+ "DEPRECATED\n"
	+ "Use " + adams.flow.transformer.Draw.class.getName() + " with " + adams.flow.transformer.draw.Pixel.class.getName() + " instead.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "x", "X",
	    1, 1, null);

    m_OptionManager.add(
	    "y", "Y",
	    1, 1, null);

    m_OptionManager.add(
	    "type", "type",
	    PixelValueType.RGBA);

    m_OptionManager.add(
	    "rgba", "RGBA",
	    0, 0, null);

    m_OptionManager.add(
	    "color", "color",
	    Color.BLACK);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "X", m_X);
    result += QuickInfoHelper.toString(this, "Y", m_Y, "/");
    result += ": ";
    switch (m_Type) {
      case RGBA:
	result += QuickInfoHelper.toString(this, "RGBA", m_RGBA);
	break;
      case COLOR:
	result += QuickInfoHelper.toString(this, "color", m_Color);
	break;
    }

    return result;
  }

  /**
   * Sets the X position of the pixel.
   *
   * @param value	the position, 1-based
   */
  public void setX(int value) {
    if (value > 0) {
      m_X = value;
      reset();
    }
    else {
      getLogger().severe("X must be >0, provided: " + value);
    }
  }

  /**
   * Returns the X position of the pixel.
   *
   * @return		the position, 1-based
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position of the pixel (1-based).";
  }

  /**
   * Sets the Y position of the pixel.
   *
   * @param value	the position, 1-based
   */
  public void setY(int value) {
    if (value > 0) {
      m_Y = value;
      reset();
    }
    else {
      getLogger().severe("Y must be >0, provided: " + value);
    }
  }

  /**
   * Returns the X position of the pixel.
   *
   * @return		the position, 1-based
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position of the pixel (1-based).";
  }

  /**
   * Sets the type of value to use.
   *
   * @param value	the type
   */
  public void setType(PixelValueType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of value to use.
   *
   * @return		the type
   */
  public PixelValueType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of value to use for the pixel.";
  }

  /**
   * Sets the RGBA value of the pixel.
   *
   * @param value	the RGBA value
   */
  public void setRGBA(int value) {
    if (value >= 0) {
      m_RGBA = value;
      reset();
    }
    else {
      getLogger().severe("RGBA must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the RGBA value of the pixel.
   *
   * @return		the RGBA value
   */
  public int getRGBA() {
    return m_RGBA;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String RGBATipText() {
    return "The RGBA value (>= 0).";
  }

  /**
   * Sets the color of the pixel.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color of the pixel.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color of the pixel.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImage.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.jai.BufferedImageContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    AbstractImage		image;
    BufferedImageContainer	cont;

    result = null;

    image = (AbstractImage) m_InputToken.getPayload();

    if (m_X > image.getWidth())
      result = "X is larger than image width: " + m_X + " > " + image.getWidth();
    else if (m_Y > image.getHeight())
      result = "Y is larger than image height: " + m_Y + " > " + image.getHeight();
    
    if (result == null) {
      cont = new BufferedImageContainer();
      cont.setReport(image.getReport().getClone());
      cont.setImage(image.toBufferedImage());
      switch (m_Type) {
	case RGBA:
	  cont.getImage().setRGB(m_X - 1, m_Y - 1, m_RGBA);
	  break;
	case COLOR:
	  cont.getImage().setRGB(m_X - 1, m_Y - 1, m_Color.getRGB());
	  break;
	default:
	  throw new IllegalStateException("Unhandled pixel value type: " + m_Type);
      }
      
      if (isLoggingEnabled())
	getLogger().info("X=" + m_X + ", Y=" + m_Y + " -> " + m_RGBA);
      
      m_OutputToken = new Token(cont);
    }

    return result;
  }
}
