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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Turns an image into the specified type of image.
 * <p/>
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
 * @version $Revision: 8954 $
 */
public class ImageType
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * The types of color quantizers.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8954 $
   */
  public enum Type {
    TYPE_3BYTE_BGR(BufferedImage.TYPE_3BYTE_BGR),
    TYPE_4BYTE_ABGR(BufferedImage.TYPE_4BYTE_ABGR),
    TYPE_4BYTE_ABGR_PRE(BufferedImage.TYPE_4BYTE_ABGR_PRE),
    TYPE_BYTE_BINARY(BufferedImage.TYPE_BYTE_BINARY),
    TYPE_BYTE_GRAY(BufferedImage.TYPE_BYTE_GRAY),
    TYPE_BYTE_INDEXED(BufferedImage.TYPE_BYTE_INDEXED),
    TYPE_CUSTOM(BufferedImage.TYPE_CUSTOM),
    TYPE_INT_ARGB(BufferedImage.TYPE_INT_ARGB),
    TYPE_INT_ARGB_PRE(BufferedImage.TYPE_INT_ARGB_PRE),
    TYPE_INT_BGR(BufferedImage.TYPE_INT_BGR),
    TYPE_INT_RGB(BufferedImage.TYPE_INT_RGB),
    TYPE_USHORT_555_RGB(BufferedImage.TYPE_USHORT_555_RGB),
    TYPE_USHORT_565_RGB(BufferedImage.TYPE_USHORT_565_RGB),
    TYPE_USHORT_GRAY(BufferedImage.TYPE_USHORT_GRAY);
    
    private int m_Type;
    
    /**
     * Initializes the enum with the corresponding type.
     * 
     * @param type	the type to store
     */
    private Type(int type) {
      m_Type = type;
    }
    
    /**
     * Returns the associated type.
     * 
     * @return		the type
     */
    public int getType() {
      return m_Type;
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
