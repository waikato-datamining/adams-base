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
 * PackDataGeneticAlgorithm.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.genetic;

import adams.opt.genetic.initialsetups.AbstractInitialSetupsProvider;
import adams.opt.genetic.initialsetups.PackDataInitialSetupsProvider;
import adams.opt.optimise.genetic.PackData;
import adams.opt.optimise.genetic.PackDataDef;

import java.util.List;

/**
 * ???
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class PackDataGeneticAlgorithm
  extends AbstractGeneticAlgorithm {

  /** suid.*/
  private static final long serialVersionUID = 4301615908806659455L;

  protected PackDataDef m_pdd;

  /**
   * Returns the default initial setups provider.
   *
   * @return		the default
   */
  protected AbstractInitialSetupsProvider getDefaultInitialSetupsProvider() {
    return new PackDataInitialSetupsProvider<>();
  }

  /**
   * Outputs the bits with the logger - logging needs to be enabled.
   *
   * @param bits	the bits to output
   */
  public void printBits(int[] bits) {
    if (!isLoggingEnabled())
      return;
    StringBuilder bitStr = new StringBuilder();
    for (int bit: bits)
      bitStr.append(bit);
    getLogger().info(bitStr.toString());
  }

  /**
   * Outputs the bits with the logger - logging needs to be enabled.
   *
   * @param bits	the bits to output
   */
  protected void print(double[] bits) {
    if (!isLoggingEnabled())
      return;
    StringBuilder bitStr = new StringBuilder();
    for (int i=0;i<bits.length;i++)
      bitStr.append(i+":"+bits[i]+" ");
    getLogger().info(bitStr.toString());
  }

  public void init(int ch) {
    m_pdd=getDataDef();
    init(ch,m_pdd.size());
  }

  public abstract PackDataDef getDataDef();

  public abstract List<PackData> getDataSetups();
}
