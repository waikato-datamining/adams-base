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
 * FeatureGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.flow.core.Actor;
import adams.flow.core.VariableMonitor;
import adams.flow.provenance.ProvenanceSupporter;

/**
 * General interface for feature generating actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface FeatureGenerator<T>
  extends Actor, ProvenanceSupporter, VariableMonitor {

  /**
   * Sets the algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(T value);

  /**
   * Returns the algorithm in use.
   *
   * @return		the algorithm
   */
  public T getAlgorithm();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText();
}
