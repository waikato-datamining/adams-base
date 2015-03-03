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
 * PackData.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise.genetic;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import adams.optimise.genetic.PackDataDef.DataInfo;


/**
 * ???
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class PackData
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 5282852389389950782L;

  protected PackDataDef m_pdd;
  protected Hashtable<String,Integer> m_data=new Hashtable<String,Integer>();

  public PackData(PackDataDef pdd) {
    m_pdd=pdd;
  }

  public String toString() {
    String ret="";
    for (String name:m_data.keySet()) {
      ret+=name+":"+get(name)+":"+m_data.get(name)+":";
      int[] bits=getBits(name);
      for (int i=0;i<bits.length;i++) {
	ret+=bits[i];
      }
      ret+="\n";
    }
    return(ret);
  }

  public Set<String> getKeySet() {
    return(m_data.keySet());
  }

  public void set(String name, double val) {
    DataInfo di=m_pdd.get(name);
    if (di == null) {
      System.err.println("not there:"+name);
    }
    int bits=di.toBits(val);
    m_data.put(name, bits);
  }

  public double get(String name) {

    DataInfo di=m_pdd.get(name);
    if (di == null) {
      System.err.println("not there:"+name);
    }
    Integer val=m_data.get(name);
    if (val == null) {
      System.err.println("not there:"+name);
      return(0);
    }
    return(di.fromBits(val));
  }

  public int[] getBits() {
    int[] ret=new int[m_pdd.size()];
    int pos=0;
    for (DataInfo di:m_pdd.m_packed) {
      setInt(ret,pos,di.m_bits,m_data.get(di.getName()));
      pos+=di.m_bits;
    }
    return(ret);
  }

  public int[] getBits(String name) {
    DataInfo di=m_pdd.get(name);
    int[] ret=new int[di.m_bits];
    int pos=0;

    setInt(ret,pos,di.m_bits,m_data.get(di.getName()));

    return(ret);
  }

  public void putBits(int[] bits) {
    if (bits.length != m_pdd.size()) {
      System.err.println("Bad size:"+bits.length);
    }else{
      int pos=0;
      for (DataInfo di:m_pdd.m_packed) {
	m_data.put(di.getName(),getInt(bits,pos,di.m_bits));
	pos+=di.m_bits;
      }
    }
  }

  protected int getInt(int[] weights,int start, int bits) {
    int res=0;
    for (int i=start;i<start+bits;i++) {
	res=(res<<1)+weights[i];
    }
    return(res);
  }

  protected void setInt(int[] weights,int start, int bits, int num) {
    for (int i=(start+bits)-1;i>=start;i--) {
      weights[i]=num & 1;

      //m_SystemErr.println("weights["+(i)+"]="+num+" & " +1 +" = "+(num & 1));
      num >>=1;
      //weights[start+bits-i]=num & (1 << (i-start));
    }
  }



}
