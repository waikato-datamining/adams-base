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
 * AbstractDrawOperation.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import java.awt.image.BufferedImage;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.AbstractActor;

/**
 * Ancestor to all draw operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDrawOperation
  extends AbstractOptionHandler 
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 981933376964417371L;

  /** the owner of the operation. */
  protected AbstractActor m_Owner;
  
  /**
   * Sets the owner.
   * 
   * @param value	the owning actor
   */
  public void setOwner(AbstractActor value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owner.
   * 
   * @return		the owner, null if none set
   */
  public AbstractActor getOwner() {
    return m_Owner;
  }
  
  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks the image.
   * <p/>
   * Default implementation only checks whether an owner and image is present.
   * 
   * @param image	the image to check
   * @return		null if OK, otherwise error message
   */
  protected String check(BufferedImage image) {
    if (m_Owner == null)
      return "No owner set!";
    if (image == null)
      return "No image supplied!";
    return null;
  }
  
  /**
   * Performs the actual draw operation.
   * 
   * @param image	the image to draw on
   * @return		null if OK, otherwise error message
   */
  protected abstract String doDraw(BufferedImage image);
  
  /**
   * Performs the draw operation on the image.
   * 
   * @param image	the image to draw on
   * @return		null if OK, otherwise error message
   */
  public String draw(BufferedImage image) {
    String	result;
    
    result = check(image);
    if (result == null)
      result = doDraw(image);
    
    return result;
  }
}
