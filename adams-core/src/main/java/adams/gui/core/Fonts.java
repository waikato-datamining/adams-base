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
 * Fonts.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Properties;

import javax.swing.UIManager;
import java.awt.Font;

/**
 * Helper class for fonts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Fonts {

  /** the name of the props file. */
  public final static String FILENAME = "adams/gui/core/Fonts.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the supported font settings. */
  public static String[] FONTS = new String[]{
    "Button",
    "CheckBox",
    "CheckBoxMenuItem",
    "ComboBox",
    "EditorPane",
    "FormattedTextField",
    "Label",
    "List",
    "Menu",
    "MenuBar",
    "MenuItem",
    "PasswordField",
    "PopupMenu",
    "RadioButton",
    "RadioButtonMenuItem",
    "Spinner",
    "TabbedPane",
    "TextArea",
    "TextField",
    "TextPane",
    "ToggleButton",
    "ToolBar",
    "Tree",
  };

  /**
   * Initializes the properties if necessary.
   */
  protected static synchronized void initializeProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(FILENAME);
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
      for (String font: FONTS) {
        if (m_Properties.getProperty(font, "").isEmpty()) {
	  try {
	    m_Properties.setFont(font, getDefaultFont(font));
	  }
	  catch (Exception e) {
	    System.err.println("Failed to set default font: '" + font + "'");
	    e.printStackTrace();
	  }
	}
      }
    }
  }

  /**
   * Returns the properties in use.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    initializeProperties();
    return m_Properties;
  }

  /**
   * Returns the string value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static String getString(String key, String defValue) {
    initializeProperties();

    return m_Properties.getProperty(key, defValue);
  }

  /**
   * Returns the default Font used by the UI manager.
   *
   * @param key		the key of the font
   * @return		the default font
   */
  public static Font getDefaultFont(String key) {
    Font	result;

    result = (Font) UIManager.get(key + ".font");
    result = result.deriveFont((float) GUIHelper.scale(result.getSize()));

    return result;
  }

  /**
   * Returns the Font value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Font getFont(String key, Font defValue) {
    Font	result;

    initializeProperties();

    result = m_Properties.getFont(key, defValue);
    result = result.deriveFont((float) GUIHelper.scale(result.getSize()));

    return result;
  }

  /**
   * Returns the system wide Monospaced font.
   *
   * @return		the font
   */
  public static Font getMonospacedFont() {
    return getFont("Monospaced", new Font("monospaced", Font.PLAIN, GUIHelper.scale(12)));
  }

  /**
   * Returns the system wide Monospaced font with a custom size.
   *
   * @param size	the custom size
   * @return		the font
   */
  public static Font getMonospacedFont(int size) {
    return getMonospacedFont().deriveFont(size);
  }

  /**
   * Returns the system wide Sans font.
   *
   * @return		the font
   */
  public static Font getSansFont() {
    return getFont("Sans", new Font("helvetiva", Font.PLAIN, GUIHelper.scale(12)));
  }

  /**
   * Returns the system wide Sans font with a custom size.
   *
   * @param size	the custom size
   * @return		the font
   */
  public static Font getSansFont(int size) {
    return getSansFont().deriveFont((float) GUIHelper.scale(size));
  }

  /**
   * Initializes all the fonts.
   *
   * @see 		#FONTS
   */
  public static void initFonts() {
    String	value;
    Font	fontObj;
    String	property;

    initializeProperties();

    for (String font: FONTS) {
      value = getString(font, "");
      if (value.trim().isEmpty())
	continue;
      fontObj = getFont(font, null);
      property = font + ".font";
      UIManager.put(property, fontObj);
    }
  }

  /**
   * Parses a font string ('name-face-size') and returns the Font object.
   *
   * @param s		the string to parse
   * @return		the font
   * @see		Font#decode(String)
   */
  public static Font decodeFont(String s) {
    return Font.decode(s);
  }

  /**
   * Turns a font into a string representation that can get parsed with
   * {@link #decodeFont(String)} again.
   *
   * @param f		the font to turn into a string
   * @return		the font
   */
  public static String encodeFont(Font f) {
    String	result;
    String	face;

    if (f.isBold() && f.isItalic())
      face = "BOLDITALIC";
    else if (f.isBold())
      face = "BOLD";
    else if (f.isItalic())
      face = "ITALIC";
    else
      face = "PLAIN";

    result = f.getName() + "-" + face + "-" + f.getSize();

    return result;
  }
}
