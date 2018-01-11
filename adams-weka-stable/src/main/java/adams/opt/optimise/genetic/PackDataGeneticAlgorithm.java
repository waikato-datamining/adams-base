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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.optimise.genetic;

import java.util.Vector;

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

  protected void printBits(int[] bits) {
    for (int i=0;i<bits.length;i++) {
      System.out.print(bits[i]);
    }
    System.out.println();
  }

  protected void print(double[] bits) {
    for (int i=0;i<bits.length;i++) {
      System.out.print(i+":"+bits[i]+" ");
    }
    System.out.println();
  }

  @Override
  public Vector<int[]> getInitialSetups() {
    // TODO Auto-generated method stub
    Vector<int[]> ret=new Vector<int[]>();
    Vector<PackData> vpd=getDataSetups();
    for (PackData pd:vpd) {
      ret.add(pd.getBits());
      printBits(pd.getBits());
    }
    return(ret);
  }
  public void init(int ch) {
    m_pdd=getDataDef();
    init(ch,m_pdd.size());
  }



  public abstract PackDataDef getDataDef();

  public abstract Vector<PackData> getDataSetups();
}
