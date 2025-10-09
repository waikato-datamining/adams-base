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
 * Text.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.image.BufferedImageContainer;
import adams.gui.core.ColorHelper;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;

import java.awt.Font;
import java.awt.Graphics;

/**
 <!-- globalinfo-start -->
 * Draws text with a specified font at the given location.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the pixel.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the top-left corner of the text (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the top-left corner of the text (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y-increment &lt;int&gt; (property: YIncrement)
 * &nbsp;&nbsp;&nbsp;The Y increment when outputting multiple lines of text.
 * &nbsp;&nbsp;&nbsp;default: 16
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the text.
 * &nbsp;&nbsp;&nbsp;default: Monospaced-PLAIN-12
 * </pre>
 * 
 * <pre>-text &lt;java.lang.String&gt; (property: text)
 * &nbsp;&nbsp;&nbsp;The text to draw; it is possible to multiple lines of text, simply use '
 * &nbsp;&nbsp;&nbsp;\n' as line break.
 * &nbsp;&nbsp;&nbsp;default: Hello World!
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Text
  extends AbstractColorDrawOperation
  implements AntiAliasingDrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = -1242368406478391978L;

  /** the X position of the text (1-based). */
  protected int m_X;

  /** the Y position of the text (1-based). */
  protected int m_Y;

  /** the Y increment when outputting multiple lines. */
  protected int m_YIncrement;

  /** the font to use. */
  protected Font m_Font;

  /** the text to draw. */
  protected String m_Text;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws text with a specified font at the given location.";
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
	    "y-increment", "YIncrement",
	    16, 1, null);

    m_OptionManager.add(
	    "font", "font",
	    Fonts.getMonospacedFont());

    m_OptionManager.add(
	    "text", "text",
	    "Hello World!");

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    true);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "X", m_X, "X: ");
    result += QuickInfoHelper.toString(this, "Y", m_Y, ", Y: ");
    result += QuickInfoHelper.toString(this, "YIncrement", m_YIncrement, ", Y-Inc.: ");
    result += QuickInfoHelper.toString(this, "font", Fonts.encodeFont(m_Font), ", F: ");
    result += QuickInfoHelper.toString(this, "text", m_Text, ", T: ");
    result += QuickInfoHelper.toString(this, "color", ColorHelper.toHex(m_Color), ", Color: ");
    
    return result;
  }

  /**
   * Sets the X position of the text (top-left corner).
   *
   * @param value	the position, 1-based
   */
  public void setX(int value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the X position of the text (top-left corner).
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
    return "The X position of the top-left corner of the text (1-based).";
  }

  /**
   * Sets the Y position of the text (top-left corner).
   *
   * @param value	the position, 1-based
   */
  public void setY(int value) {
    if (getOptionManager().isValid("Y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the Y position of the text (top-left corner).
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
    return "The Y position of the top-left corner of the text (1-based).";
  }

  /**
   * Sets the Y increment when outputting multiple lines.
   *
   * @param value	the increment
   */
  public void setYIncrement(int value) {
    if (getOptionManager().isValid("YIncrement", value)) {
      m_YIncrement = value;
      reset();
    }
  }

  /**
   * Returns the Y increment when outputting multiple lines.
   *
   * @return		the increment
   */
  public int getYIncrement() {
    return m_YIncrement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YIncrementTipText() {
    return "The Y increment when outputting multiple lines of text.";
  }

  /**
   * Sets the font to use.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font in use.
   *
   * @return		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the text.";
  }

  /**
   * Sets the text to draw. Use \n as line break.
   *
   * @param value	the text, backquoted
   */
  public void setText(String value) {
    m_Text = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the text to draw. Uses \n as line break.
   *
   * @return		the text, backquoted
   */
  public String getText() {
    return Utils.backQuoteChars(m_Text);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textTipText() {
    return "The text to draw; it is possible to multiple lines of text, simply use '\\n' as line break.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    reset();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing.";
  }

  /**
   * Checks the image.
   *
   * @param image	the image to check
   * @return		null if OK, otherwise error message
   */
  protected String check(BufferedImageContainer image) {
    String        result;

    result = super.check(image);

    if (result == null) {
      if (m_X > image.getWidth())
        result = "X is larger than image width: " + m_X + " > " + image.getWidth();
      else if (m_Y > image.getHeight())
        result = "Y is larger than image height: " + m_Y + " > " + image.getHeight();
    }

    return result;
  }

  /**
   * Performs the actual draw operation.
   * 
   * @param image	the image to draw on
   */
  @Override
  protected String doDraw(BufferedImageContainer image) {
    Graphics	g;
    String[] 	lines;
    int		i;

    if (!m_Text.isEmpty()) {
      lines = Utils.split(m_Text, "\n");
      g = image.getImage().getGraphics();
      g.setColor(m_Color);
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);
      g.setFont(m_Font);
      for (i = 0; i < lines.length; i++)
	g.drawString(lines[i], m_X - 1, m_Y - 1 + i * m_YIncrement);
    }
    
    return null;
  }
}
