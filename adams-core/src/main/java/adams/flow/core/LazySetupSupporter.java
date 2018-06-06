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
 * LazySetupSupporter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Interface for actors that optionally perform a {@link #setUp()}
 * call first time they get executed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LazySetupSupporter
  extends Actor {

  /**
   * Sets whether to perform a lazy setup, ie when first executed.
   *
   * @param value	true if to perform lazy setup
   */
  public void setPerformLazySetup(boolean value);

  /**
   * Returns whether to perform a lazy setup, ie when first executed.
   *
   * @return		true if to perform lazy setup
   */
  public boolean getPerformLazySetup();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String performLazySetupTipText();

  /**
   * Returns whether lazy setup has been performed.
   *
   * @return		true if performed
   */
  public boolean hasLazySetupPerformed();

  /**
   * Performs the lazy setup.
   *
   * @return		null if successful, otherwise error message
   */
  public String lazySetup();
}
