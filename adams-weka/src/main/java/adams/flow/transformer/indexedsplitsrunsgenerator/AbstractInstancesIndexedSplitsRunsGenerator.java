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
 * AbstractInstancesIndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import weka.core.Instances;

/**
 * Ancestor for generators that process Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInstancesIndexedSplitsRunsGenerator
  extends AbstractIndexedSplitsRunsGenerator
  implements InstancesIndexedSplitsRunsGenerator {

  private static final long serialVersionUID = -3421372018638798691L;

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }
}
