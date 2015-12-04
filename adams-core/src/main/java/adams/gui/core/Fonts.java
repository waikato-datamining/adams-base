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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
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
  public final static String FILENAME = "Fonts.props";

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
        if (!m_Properties.hasKey(font)) {
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
    return (Font) UIManager.get(key + ".font");
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
    initializeProperties();

    return m_Properties.getFont(key, defValue);
  }

  /**
   * Returns the system wide Monospaced font.
   *
   * @return		the font
   */
  public static Font getMonospacedFont() {
    return getFont("Monospaced", new Font("monospaced", Font.PLAIN, 12));
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
    return getFont("Sans", new Font("helvetiva", Font.PLAIN, 12));
  }

  /**
   * Returns the system wide Sans font with a custom size.
   *
   * @param size	the custom size
   * @return		the font
   */
  public static Font getSansFont(int size) {
    return getSansFont().deriveFont(size);
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
}
