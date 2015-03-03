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
 * DataRow.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.ml;

import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.Set;

import adams.ml.BaseData.Date;

/**
 * 
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class DataRow {
  protected Hashtable<String,BaseData> m_Data;

  
  public DataRow(){
    m_Data=new Hashtable<String,BaseData>();
  }

  
  public void renameColumn(String from, String to){
    BaseData bd=m_Data.get(from);
    if (bd != null){
      m_Data.put(to, bd);
      m_Data.remove(from);
    }
  }
  
  public BaseData get(String key){
    return(m_Data.get(key));       
  }

  public Date getDate(String key){
    try{
      return((Date)getObject(key));
    } catch(Exception e){
      return(null);
    }
  }

  public String getString(String key){
    try{
      return((String)getObject(key));
    } catch(Exception e){
      return(null);
    }
  }


  public String getAsString(String key){
    try{
      return(getObject(key).toString());
    } catch(Exception e){
      return(null);
    }
  }
  
  
  public Integer getAsInteger(String key){
    try{
      return((Integer)getObject(key));
    } catch(Exception e){
      return(null);
    }
  }
  public Integer getAsInteger(String key, Integer defaultvalue ){
    try{
      if (getObject(key)==null){
	return(defaultvalue);
      }
      return((Integer)getObject(key));
    } catch(Exception e){
      return(defaultvalue);
    }
  }
  public Float getAsFloat(String key){
    try{
      return((Float)getObject(key));
    } catch(Exception e){
      return(null);
    }
  }
  public Object getObject(String key){
    if (m_Data.get(key) == null){
      return(null);
    }
    return(m_Data.get(key).m_data);
  }

  public DataRow copy(){
    DataRow ret=new DataRow();
    ret.m_Data=(Hashtable<String,BaseData>)m_Data.clone();
    //for (String key:getKeys()){
    //  ret.set(key, getObject(key));
    //}
    return(ret);
  }



  public void set(String key, BaseData data){
    m_Data.put(key, data);
  }

  public void set(String key, Object data){
    m_Data.put(key, new BaseData(data));
  }

  public boolean isPresent(String key){
    return(m_Data.get(key) != null);
  }

  public Set<String> getKeys(){
    return(m_Data.keySet());
  }

  public String toString(String[] arr){
    StringBuilder sb=new StringBuilder();

    DataRow dr=this;
    for (String s:arr){
      Object o=dr.getObject(s);
      if (o != null && o.getClass().isArray()){
	sb.append("("+s+":");
	for (int j=0;j<Array.getLength(o);j++){
	  Object p=Array.get(o, j);
	  sb.append(p.toString());
	  if (j < Array.getLength(o)-1){
	    sb.append(",");
	  }
	}
	sb.append(")");
      } else if (o != null ){
	sb.append("("+s+":"+o.toString()+")");
      }
    }


    return(sb.toString());
  }

  @Override
  public String toString(){
    StringBuilder sb=new StringBuilder();

    DataRow dr=this;
    for (String s:dr.getKeys()){
      Object o=dr.getObject(s);
      if (o != null && o.getClass().isArray()){
	sb.append("("+s+":");
	for (int j=0;j<Array.getLength(o);j++){
	  Object p=Array.get(o, j);
	  sb.append(p.toString());
	  if (j < Array.getLength(o)-1){
	    sb.append(",");
	  }
	}
	sb.append(")");
      } else if (o != null ){
	sb.append("("+s+":"+o.toString()+")");
      }
    }


    return(sb.toString());
  }

}
