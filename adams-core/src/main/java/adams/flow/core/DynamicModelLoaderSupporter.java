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
 * ModelLoaderSupporter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Interface for classes that support model loading.
 * Optionally, model gets generated on the fly.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see AbstractModelLoader
 */
public interface DynamicModelLoaderSupporter
  extends ModelLoaderSupporter {

  /**
   * Sets whether the model gets built on the fly and might not be present
   * at start up time.
   *
   * @param value	if true then the model does not have to be present at
   * 			start up time
   */
  public void setOnTheFly(boolean value);

  /**
   * Returns whether the model gets built on the fly and might not be present
   * at start up time.
   *
   * @return		true if the model is not necessarily present at start
   * 			up time
   */
  public boolean getOnTheFly();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onTheFlyTipText();
}
