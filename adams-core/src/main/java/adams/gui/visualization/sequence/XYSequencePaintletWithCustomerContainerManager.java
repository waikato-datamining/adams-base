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
 * XYSequencePaintletWithCustomerContainerManager.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

/**
 * Interface for XY sequence paintlets that support custom container managers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface XYSequencePaintletWithCustomerContainerManager
  extends XYSequencePaintlet {

  /**
   * Sets the custom container manager to obtain the sequences from.
   * 
   * @param value	the manager
   */
  public void setCustomContainerManager(XYSequenceContainerManager value);
  
  /**
   * Returns the current custom container manager to obtain the sequences from.
   * 
   * @return		the manager, null if none set
   */
  public XYSequenceContainerManager getCustomerContainerManager();
  
  /**
   * Returns the container manager in use. Custom manager overrides the sequence
   * panel's one.
   * 
   * @return		the container manager
   * @see		#getCustomerContainerManager()
   */
  public XYSequenceContainerManager getActualContainerManager();
}
