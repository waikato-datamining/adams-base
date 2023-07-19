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
 * ImageType.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.core.EnumWithCustomDisplay;
import adams.core.QuickInfoHelper;
import adams.core.option.AbstractOption;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Turns an image into the specified type of image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-type &lt;TYPE_3BYTE_BGR|TYPE_4BYTE_ABGR|TYPE_4BYTE_ABGR_PRE|TYPE_BYTE_BINARY|TYPE_BYTE_GRAY|TYPE_BYTE_INDEXED|TYPE_CUSTOM|TYPE_INT_ARGB|TYPE_INT_ARGB_PRE|TYPE_INT_BGR|TYPE_INT_RGB|TYPE_USHORT_555_RGB|TYPE_USHORT_565_RGB|TYPE_USHORT_GRAY&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of image to convert to.
 * &nbsp;&nbsp;&nbsp;default: TYPE_INT_ARGB
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageType
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * The types of color quantizers.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum Type
    implements EnumWithCustomDisplay<Type> {
    
    TYPE_3BYTE_BGR(BufferedImage.TYPE_3BYTE_BGR, "3Byte BGR"),
    TYPE_4BYTE_ABGR(BufferedImage.TYPE_4BYTE_ABGR, "4Byte ABGR"),
    TYPE_4BYTE_ABGR_PRE(BufferedImage.TYPE_4BYTE_ABGR_PRE, "4Byte ABGR pre-computed alpha"),
    TYPE_BYTE_BINARY(BufferedImage.TYPE_BYTE_BINARY, "Byte Binary"),
    TYPE_BYTE_GRAY(BufferedImage.TYPE_BYTE_GRAY, "Byte Gray"),
    TYPE_BYTE_INDEXED(BufferedImage.TYPE_BYTE_INDEXED, "Byte Indexed"),
    TYPE_CUSTOM(BufferedImage.TYPE_CUSTOM, "Custom"),
    TYPE_INT_ARGB(BufferedImage.TYPE_INT_ARGB, "Int ARGB"),
    TYPE_INT_ARGB_PRE(BufferedImage.TYPE_INT_ARGB_PRE, "Int ARGB pre-computed alpha"),
    TYPE_INT_BGR(BufferedImage.TYPE_INT_BGR, "Int BGR"),
    TYPE_INT_RGB(BufferedImage.TYPE_INT_RGB, "Int RGB"),
    TYPE_USHORT_555_RGB(BufferedImage.TYPE_USHORT_555_RGB, "UShort 555 RGB"),
    TYPE_USHORT_565_RGB(BufferedImage.TYPE_USHORT_565_RGB, "UShort 565 RGB"),
    TYPE_USHORT_GRAY(BufferedImage.TYPE_USHORT_GRAY, "UShort Gray");

    private int m_Type;

    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * Initializes the enum with the corresponding type and display string.
     *
     * @param type	the type to store
     * @param display   the display string to use
     */
    private Type(int type, String display) {
      m_Type    = type;
      m_Display = display;
      m_Raw     = super.toString();
    }

    /**
     * Returns the associated type.
     *
     * @return		the type
     */
    public int getType() {
      return m_Type;
    }

    /**
     * Returns the human-readable display.
     *
     * @return		the display
     */
    @Override
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    @Override
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toString() {
      return toDisplay();
    }

    /**
     * Returns the enum type for the integer type, if possible.
     *
     * @param type  	the type to get the enum for
     * @return		the enum, null if not found
     */
    public static Type type(int type) {
      Type  result;

      result = null;

      for (Type t: values()) {
	if (t.getType() == type) {
	  result = t;
	  break;
	}
      }

      return result;
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public Type parse(String s) {
      return (Type) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((Type) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str	the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static Type valueOf(AbstractOption option, String str) {
      Type result;

      result = null;

      // default parsing
      try {
        result = valueOf(str);
      }
      catch (Exception e) {
        // ignored
      }

      // try display
      if (result == null) {
        for (Type t : values()) {
          if (t.toDisplay().equalsIgnoreCase(str)) {
            result = t;
            break;
          }
        }
      }

      return result;
    }
  }

  /** the image type. */
  protected Type m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an image into the specified type of image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      Type.TYPE_INT_ARGB);
  }

  /**
   * Sets the type of image to convert to.
   *
   * @param value	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of image to convert to.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "The type of image to convert to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    BufferedImage		image;

    result     = new BufferedImageContainer[1];
    image      = BufferedImageHelper.convert(img.getImage(), m_Type.getType());
    result[0]  = (BufferedImageContainer) img.getHeader();
    result[0].setImage(image);

    return result;
  }
}
