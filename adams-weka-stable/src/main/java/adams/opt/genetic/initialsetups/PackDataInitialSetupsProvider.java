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
 * PackDataInitialSetupsProvider.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.initialsetups;

import adams.opt.genetic.PackDataGeneticAlgorithm;
import adams.opt.optimise.genetic.PackData;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PackDataInitialSetupsProvider<T extends PackDataGeneticAlgorithm>
  extends AbstractInitialSetupsProvider<T> {

  private static final long serialVersionUID = 6250075691260338046L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides the initial gene setups for " + PackDataGeneticAlgorithm.class.getName() + " algorithms.";
  }

  /**
   * Provides the initial gene setup.
   *
   * @param owner	the owning the algorithm
   * @return		the genes (0s and 1s)
   */
  @Override
  public List<int[]> getInitialSetups(T owner) {
    List<int[]> result = new ArrayList<>();
    List<PackData> vpd = owner.getDataSetups();
    for (PackData pd:vpd) {
      result.add(pd.getBits());
      owner.printBits(pd.getBits());
    }
    return result;
  }
}
