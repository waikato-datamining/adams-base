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
 *    CustomPanelSupplier.java
 *    Copyright (C) 2002 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import javax.swing.JPanel;

/**
 * An interface for objects that are capable of supplying their own
 * custom GUI components. The original purpose for this interface is
 * to provide a mechanism allowing the GenericObjectEditor to override
 * the standard PropertyPanel GUI.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision$
 */
public interface CustomPanelSupplier {

  /**
   * Gets the custom panel for the object.
   *
   * @return 		the custom JPanel
   */
  public JPanel getCustomPanel();
}
