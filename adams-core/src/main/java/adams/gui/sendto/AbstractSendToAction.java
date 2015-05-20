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
 * AbstractSendTo.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import javax.swing.ImageIcon;

import adams.core.logging.LoggingObject;
import adams.gui.core.GUIHelper;

/**
 * Ancestor for classes that allow data forwarding in some sense, e.g.,
 * via email.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSendToAction
  extends LoggingObject
  implements Comparable<AbstractSendToAction> {

  /** for serialization. */
  private static final long serialVersionUID = 4432813063252680408L;

  /** the owner. */
  protected SendToActionSupporter m_Owner;
  
  /**
   * Sets the owner of this action.
   * 
   * @param value	the owner
   */
  public void setOwner(SendToActionSupporter value) {
    m_Owner = value;
  }
  
  /**
   * Returns the current owner of this action.
   * 
   * @return		the owner, null if none set
   */
  public SendToActionSupporter getOwner() {
    return m_Owner;
  }
  
  /**
   * Returns the short description of the sendto action.
   * Description gets used for menu items.
   *
   * @return		the short description
   */
  public abstract String getAction();

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return null;
  }

  /**
   * Returns the icon.
   *
   * @return		the icon or empty icon if no icon name available
   * @see		#getIconName()
   */
  public ImageIcon getIcon() {
    ImageIcon		result;

    result = null;

    if (getIconName() != null) {
      if (getIconName().indexOf("/") > -1)
        result = GUIHelper.getExternalIcon(getIconName());
      else
        result = GUIHelper.getIcon(getIconName());
    }
    else {
      result = GUIHelper.getEmptyIcon();
    }

    return result;
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  public abstract Class[] accepts();

  /**
   * Performs the actual sending/forwarding/processing of the data.
   *
   * @param o		the object to send
   * @return		null if everything OK, otherwise error message
   */
  public abstract String send(Object o);

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Simply performs comparison on the action string.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @see     #getAction()
   */
  public int compareTo(AbstractSendToAction o) {
    return getAction().compareTo(o.getAction());
  }

  /**
   * Returns whether the specified object is the same as this one.
   * <br><br>
   * For AbstractSendToAction objects, it merely uses the action string
   * for comparison.
   *
   * @param o		the object to compare with
   * @return		true if both AbstractSendToAction instances and the
   * 			same action string
   * @see		#compareTo(AbstractSendToAction)
   * @see		#getAction()
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof AbstractSendToAction)
      return (compareTo((AbstractSendToAction) o) == 0);
    else
      return false;
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * {@link #getAction()} string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return getAction().hashCode();
  }
}
