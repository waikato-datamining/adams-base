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
 * String.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.clipboard;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.gui.core.GUIHelper;

/**
 * Copies/pastes buffered images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImage
  extends AbstractClipboardData<java.awt.image.BufferedImage> {

  /** for serialization. */
  private static final long serialVersionUID = 3264721542935946632L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public java.lang.String globalInfo() {
    return "Copies/pastes (buffered) images.";
  }

  /**
   * Returns the supported class.
   * 
   * @return		the supported class
   */
  public Class getFlowClass() {
    return BufferedImageContainer.class;
  }

  /**
   * Copies the data to the clipboard.
   * 
   * @param data	the data to copy
   */
  public void copy(java.awt.image.BufferedImage data) {
    GUIHelper.copyToClipboard(data);
  }
  
  /**
   * Unwraps the actual data from the token.
   * 
   * @param data	the token to unwrap
   * @return		the actual data, null if can't handle
   */
  protected java.awt.image.BufferedImage unwrap(Token data) {
    if (data.getPayload() instanceof java.awt.image.BufferedImage)
      return (java.awt.image.BufferedImage) data.getPayload();
    else if (data.getPayload() instanceof AbstractImage)
      return ((AbstractImage) data.getPayload()).toBufferedImage();
    else
      return null;
  }
  
  /**
   * Checks whether the required data type is available from the clipboard.
   * 
   * @return		true if data is available
   */
  public boolean canPaste() {
    return GUIHelper.canPasteFromClipboard(DataFlavor.imageFlavor);
  }
  
  /**
   * Wraps the data from the clipboard in a flow token.
   * 
   * @param 
   * @return		the pasted data, null if failed to paste
   */
  protected Token wrap(java.awt.image.BufferedImage data) {
    BufferedImageContainer	cont;
    
    cont = new BufferedImageContainer();
    cont.setImage(data);
    
    return new Token(data);
  }

  /**
   * Pastes the data from the clipboard.
   * 
   * @return		the pasted data, null if failed to paste
   */
  @MixedCopyright(
      author = "ImageJ",
      license = License.PUBLIC_DOMAIN,
      url = "http://rsbweb.nih.gov/ij/developer/source/ij/plugin/Clipboard.java.html",
      note = "taken from showSystemClipboard() method"
  )
  public java.awt.image.BufferedImage paste() {
    java.awt.image.BufferedImage	result;
    Image				img;
    int 				width;
    int 				height;
    Graphics 				g;

    result = null;
    img    = (Image) GUIHelper.pasteFromClipboard(DataFlavor.imageFlavor);
    if (img != null) {
      width  = img.getWidth(null);
      height = img.getHeight(null);
      result = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
      g      = result.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
    }

    return result;
  }
}
