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
 * DryRunSupporter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Interface for actors that support dry runs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4 $
 */
public interface DryRunSupporter
  extends Actor {

  /**
   * Sets whether this is only a dry-run.
   *
   * @param value 	if true a dry-run will be performed
   */
  public void setDryRun(boolean value);

  /**
   * Returns whether this is only a dry-run.
   *
   * @return 		true if a dry-run will be performed
   */
  public boolean getDryRun();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dryRunTipText();
}
