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
 * OptionalPasswordPrompt.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

/**
 * Interface for actors that prompt the user to enter a password in case
 * no password currently provided.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface OptionalPasswordPrompt
  extends InteractiveActor {

  /**
   * Sets whether to prompt for a password if none currently provided.
   * 
   * @param value	true if to prompt for a password
   */
  public void setPromptForPassword(boolean value);
  
  /**
   * Returns whether to prompt for a password if none currently provided.
   * 
   * @return		true if to prompt for a password
   */
  public boolean getPromptForPassword();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String promptForPasswordTipText();
}
