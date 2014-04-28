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
 * PackDataDef.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise.genetic;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

/**
 * ???
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class PackDataDef
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -4776734918844200382L;

  public class DataInfo{
    public String m_name;
    public int m_bits;
    public double m_max; //scale
    public double m_min; //scale

    protected double m_scale;

    public DataInfo(String name,int bits, double min, double max) {
      m_bits=bits;
      m_max=max;
      m_min=min;
      m_name=name;
      m_scale=(m_max-m_min)/(double)getMaxVal();
    }

    public String getName() {
      return(m_name);
    }

    public void resetMinMax(double min, double max) {

      m_max=max;
      m_min=min;

      m_scale=(m_max-m_min)/(double)getMaxVal();
    }

    public int toBits(double val) {
      return((int)((val-m_min)/m_scale));
    }

    public double fromBits(int bits) {
      return((double)(bits * m_scale)+m_min);
    }

    public int getMaxVal() {
      return((1<<m_bits)-1);
    }
  }

  public int size() {
    int count=0;
    for (DataInfo di:m_packed) {
      count+=di.m_bits;
    }
    return(count);
  }


  protected Hashtable<String,Integer> m_sort_packed=new Hashtable<String,Integer>();
  protected Vector<DataInfo> m_packed = new Vector<DataInfo>();

  public void add(String name, int bits, double min, double max) {
    DataInfo di=new DataInfo(name,bits,min,max);
    m_sort_packed.put(name, m_packed.size());
    m_packed.add(di);
  }

  public void setMinMax(String name, double min,double max) {
    Integer pos=m_sort_packed.get(name);
    if (pos == null) {
      System.err.println("not there:"+name);
    }
    DataInfo di=m_packed.get(pos);
    di.resetMinMax(min,max);
  }

  public DataInfo get(String name) {
    Integer pos=m_sort_packed.get(name);
    if (pos == null) {
      System.err.println("not there:"+name);
      return(null);
    }
    DataInfo di=m_packed.get(pos);
    return(di);
  }

}
