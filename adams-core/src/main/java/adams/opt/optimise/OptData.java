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
 * OptData.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.optimise;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

import adams.core.CleanUpHandler;
import adams.core.CloneHandler;

/**
 * Holds set of OptVars with current values.
 *
 * @author dale
 * @version $Revision$
 */
public class OptData
  implements Serializable, CleanUpHandler, CloneHandler<OptData> {

  /**suid.  */
  private static final long serialVersionUID = -8240249957587951006L;

  /** Store OptVars & values. */
  protected Hashtable<String,Double> m_data=new Hashtable<String,Double>();

  /** Var store. */
  protected Hashtable<String,OptVar> m_vars=new Hashtable<String,OptVar>();

  /**
   * Copy this object.
   *
   * @return copy.
   */
  public OptData getClone() {
    OptData ret=new OptData();
    for (String key:getVarNames()) {
      ret.add(getVar(key).getClone());
    }

    for (String key:getDataNames()) {
      ret.set(key,get(key));
    }
    return(ret);
  }

  /**
   * Set var value.
   *
   * @param var	var name.
   * @param val	var value.
   */
  public void set(String var, double val) {
    m_data.put(var, val);
  }

  /**
   * Get set of varnames.
   *
   * @return	varname set
   */
  public Set<String> getVarNames() {
    return(m_vars.keySet());
  }

  /**
   * Get set of datanames.
   *
   * @return	dataame set
   */
  public Set<String> getDataNames() {
    return(m_data.keySet());
  }

  /**
   * Add var.
   *
   * @param ov var
   */
  public void add(OptVar ov) {
    m_vars.put(ov.m_name, ov);
  }

  /**
   * Get var.
   *
   * @param name	varname
   * @return var
   */
  public OptVar getVar(String name) {
    return(m_vars.get(name));
  }

  /**
   * Get var value.
   *
   * @param var	varname
   * @return	var value
   */
  public Double get(String var) {
    return(m_data.get(var));
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_data.clear();
    m_vars.clear();
  }

  /**
   * Returns a string representation of the data structure.
   *
   * @return the string representation
   */
  public String toString() {
    String ret="";
    for (String key:getVarNames()) {
      ret+=key+":"+get(key)+",";
    }
    return(ret);
  }
  /**
   * Returns a string representation of the data structure.
   *
   * @return the string representation
   */
  public String toVarString() {
    String ret="";
    for (String key:getVarNames()) {
      ret+=key+":"+getVar(key).toString()+",";
    }
    return(ret);
  }
}
