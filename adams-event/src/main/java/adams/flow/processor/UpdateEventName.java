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
 * UpdateEventName.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.util.logging.Level;

import adams.core.ClassLocator;
import adams.flow.core.EventReference;

/**
 <!-- globalinfo-start -->
 * Updates all occurrences of the old event name with the new one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-old-name &lt;java.lang.String&gt; (property: oldName)
 * &nbsp;&nbsp;&nbsp;The old event name to replace with the new one.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-new-name &lt;java.lang.String&gt; (property: newName)
 * &nbsp;&nbsp;&nbsp;The new event name that replaces the old one.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateEventName
  extends AbstractNameUpdater<EventReference> {

  /** for serialization. */
  private static final long serialVersionUID = 7133896476260133469L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Updates all occurrences of the old event name with the new one.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String oldNameTipText() {
    return "The old event name to replace with the new one.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String newNameTipText() {
    return "The new event name that replaces the old one.";
  }

  /**
   * Returns whether the base class that we're looking for to perform the
   * replacement on is a match.
   *
   * @param cls		the class to check
   * @return		true if a match
   */
  @Override
  protected boolean isBaseClassMatch(Class cls) {
    return ClassLocator.isSubclass(EventReference.class, cls);
  }

  /**
   * Checks whether the located object matches the old name that requires
   * replacement.
   *
   * @param old		the old object to check
   * @param oldName	the old name to look for
   * @return		true if a match
   */
  @Override
  protected boolean isNameMatch(EventReference old, String oldName) {
    return old.getValue().equals(oldName);
  }

  /**
   * Returns the replacement object.
   *
   * @param old		the old object
   * @param newName	the new name to use
   * @return		the replacement object, null in case of error
   */
  @Override
  protected EventReference getReplacement(EventReference old, String newName) {
    EventReference	result;

    try {
      result = (EventReference) old.getClass().newInstance();
      result.setValue(newName);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to create instance of " + old.getClass().getName() + ":", e);
      result = null;
    }

    return result;
  }
}
