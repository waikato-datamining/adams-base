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
 * WekaEditorsRegistration.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;


/**
 * Registers first the WEKA GenericObjectEditor editors and the ADAMS ones.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEditorsRegistration
  extends AbstractEditorRegistration {

  /** for serialization. */
  private static final long serialVersionUID = -2908979337117222215L;

  /** whether registration already occurred. */
  protected static boolean m_Registered;

  /**
   * Returns whether registration already occurred.
   *
   * @return		true if registration already occurred
   */
  protected boolean hasRegistered() {
    return m_Registered;
  }

  /**
   * Performs the registration of the editors.
   *
   * @return		true if registration successful
   */
  protected boolean doRegister() {
    weka.gui.GenericObjectEditor.determineClasses();
    weka.gui.GenericObjectEditor.registerEditors();
    m_Registered = true;
    return true;
  }
}
