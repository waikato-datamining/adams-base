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
 * ExperimentWithCustomizableRelationNames.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.experiment;

/**
 * Interface for experiments that allow customizing the relation names
 * of the datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ExperimentWithCustomizableRelationNames {

  /**
   * Sets whether to use the filename (w/o path) instead of the relationname.
   *
   * @param value	true if to use filename
   */
  public void setUseFilename(boolean value);

  /**
   * Returns whether to use the filename (w/o path) instead of the relationname.
   *
   * @return		true if to use the filename
   */
  public boolean getUseFilename();

  /**
   * Sets whether to prefix the datasets with the index.
   *
   * @param value	true if to prefix
   */
  public void setPrefixDatasetsWithIndex(boolean value);

  /**
   * Returns whether to prefix the datasets with the index.
   *
   * @return		true if to prefix
   */
  public boolean getPrefixDatasetsWithIndex();
}
