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
 * Dataset.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.ml;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.math.stat.StatUtils;

/**
 * Use {@link adams.ml.data.Dataset} instead.
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class Dataset implements Serializable,Cloneable{

  public class Mapping {
    public Mapping(BaseData.Type typ){
      m_type=typ;
    }
    public Mapping(){
    }
    protected BaseData.Type m_type;
    protected BaseData.Type m_basetype; // for arrays
    protected int m_size=-1; //for arrays
  }
  protected Hashtable<String,Mapping> m_Mapping;
  protected Hashtable<Integer, Vector<String>> m_errors = new Hashtable<Integer, Vector<String>>(); // row, err column

  /**
   *
   */
  private static final long serialVersionUID = 4458447431240251541L;

  public class DataRowIntegerComparable implements Comparator<DataRow>{

    public DataRowIntegerComparable(String f){
      m_field=f;
    }
    protected String m_field;

    @Override
    public int compare(DataRow o1, DataRow o2) {
      return ((Integer)o1.get(m_field).getData()>(Integer)o2.get(m_field).getData() ? 1 : (o1==o2 ? 0 : -1));
    }
  }
  /** optional name of this dataset. */
  protected String m_name="";

  protected Vector<DataRow> m_rows;

  public boolean sortIntegerField(String field){
    Collections.sort(m_rows, new DataRowIntegerComparable(field));
    return(true);
  }


  public Mapping getType(String columnname){
    return(m_Mapping.get(columnname));
  }

  public Dataset getWithStringValue(String columnname, String val){
    Dataset ret=new Dataset();

    for (int row=0;row<count();row++){
      if (m_errors.get(row) != null){
	continue;
      }
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      if (val.equals(cell.getData().toString())){
	ret.add(dr.copy());
      }
    }
    return(ret);
  }
  protected String summariseNumericBy(String columnname,String by){
    StringBuffer ret=new StringBuffer();
    Vector<String> distinct = getDistinctStrings(by);
    for (String s:distinct){
      Dataset ds=getWithStringValue(by,s);
      ret.append(s+"\n");
      ret.append("----------------\n");
      ret.append(ds.summariseNumeric(columnname));
    }
    return(ret.toString());
  }
  protected String summariseNumeric(String columnname){
    Vector<Float> vf=new Vector<Float>();
    Hashtable<Float, Boolean> ind=new Hashtable<Float, Boolean>();
    int numErrors=0;
    int missing=0;
    int ignored=0;
    for (int i=0;i<count();i++){
      DataRow dr=get(i);
      if (dr.get(columnname)==null){
	missing++;
	continue;
      }
      dr=getSafe(i);
      if (dr==null){
	ignored++;
	continue;
      }
      try{
	Number val=(Number)dr.get(columnname).getData();
	vf.add(val.floatValue());
	if (ind.get(val.floatValue()) == null){
	  ind.put(val.floatValue(), true);
	}
      } catch (Exception e){
	try{
	  Float pi=Float.parseFloat(dr.getAsString(columnname));
	  vf.add(pi);
	} catch (Exception e1){
	  numErrors++;
	  continue;
	}
      }
    }
    double[] f=new double[vf.size()];
    for (int i=0;i<f.length;i++){
      f[i]=vf.get(i);
    }
    StringBuffer ret=new StringBuffer();
    ret.append("Total OK: "+vf.size()+"\n");
    ret.append("Ignored Rows: "+ignored+"\n");
    ret.append("Missing: "+missing+"\n");
    ret.append("Errors: "+numErrors+"\n");
    ret.append("Different: "+ind.size()+"\n");
    double min=StatUtils.min(f);
    double max=StatUtils.max(f);
    double mean=StatUtils.mean(f);
    double stdev=Math.sqrt(StatUtils.variance(f));
    double median=StatUtils.percentile(f, 50);
    ret.append("Min,Max: "+min+","+max+"\n");
    ret.append("Mean: "+mean+"\n");
    ret.append("Standard Deviation: "+stdev+"\n");
    ret.append("Median: "+median+"\n");
    return(ret.toString());
  }

  public Vector<String> getDistinctStrings(String columnname){
    Hashtable<String,Boolean> ht=new Hashtable<String,Boolean>();
    Vector<String> ret=new Vector<String>();
    for (int i=0;i<count();i++){
      DataRow dr=getSafe(i);
      if (dr == null){
	continue;
      }
      String cll=dr.getAsString(columnname);
      if (cll != null){
	if (!ht.containsKey(cll)){
	  ht.put(cll, true);
	}
      }
    }
    for (String s:ht.keySet()){
      ret.add(s);
    }
    return(ret);
  }
  
  
  public DataRow getSafe(int i){
    if (m_errors.get(i) != null){
      return(null);
    }
    return(m_rows.get(i));
  }
  
 /* protected String summariseString(String columnname){
    for (DataRow dr:m_rows){
      addWithoutCheck(dr.copy());
    }
  }

  public String summarise(String columnname){
    BaseData.Type typ;
    Mapping m=getType(columnname);
    if (m == null){
      // assume String
      typ=BaseData.Type.STRING;
    } else {
      typ=m.m_type;
    }


  }

*/
  public Dataset(String name){
    m_name=name;
    m_Mapping = new Hashtable<String,Mapping>();
    m_rows=new Vector<DataRow>();
  }
  public Dataset(){
    m_Mapping = new Hashtable<String,Mapping>();
    m_rows=new Vector<DataRow>();
  }
  public void setType(String key, BaseData.Type type){
    Mapping m=new Mapping();
    m.m_type=type;
    m_Mapping.put(key, m);
  }

  public void setType(String key, BaseData.Type basetype, int size){
    Mapping m=new Mapping();
    m.m_type=BaseData.Type.ARRAY;
    m.m_basetype=basetype;
    m.m_size=size;
    m_Mapping.put(key, m);
  }

  public void setMappingFromBaseData(String key, BaseData bd){

    if (bd != null){

      if (bd.isNumeric()){
	setType(key,BaseData.Type.NUMERIC);
      } else if (bd.isDate()){
	setType(key,BaseData.Type.DATE);
      } else if (bd.isTime()){
	setType(key,BaseData.Type.TIME);
      } else if (bd.isArray()){
	if (Array.getLength(bd.getData()) > 0){
	  Object ao=Array.get(bd.getData(), 0);
	  if (BaseData.isNumeric(ao)){
	    setType(key,BaseData.Type.NUMERIC,Array.getLength(bd.getData()));
	  } else {
	    setType(key,BaseData.Type.STRING,Array.getLength(bd.getData()));
	  }
	}
      } else {
	setType(key,BaseData.Type.STRING);
      }
    }
  }

  public BaseData.Type getArrayType(Object bd){

    if (bd != null){
      if (BaseData.isNumeric(bd)){
	return(BaseData.Type.NUMERIC);
      } else if (BaseData.isArray(bd)){
	System.err.println("can't handle Multidim array");
	return(null);
      } else {
	return(BaseData.Type.STRING);
      }
    } else {
      return(null);
    }
  }

  public void setMappingFromDataRow(DataRow dr){
    for (String key:dr.getKeys()){
      BaseData bd=dr.get(key);
      if (bd != null){
	if (bd.isNumeric()){
	  setType(key,BaseData.Type.NUMERIC);
	} else if (bd.isArray()){
	  if (Array.getLength(bd.getData()) > 0){
	    Object ao=Array.get(bd.getData(), 0);
	    if (BaseData.isNumeric(ao)){
	      setType(key,BaseData.Type.NUMERIC,Array.getLength(bd.getData()));
	    } else {
	      setType(key,BaseData.Type.STRING,Array.getLength(bd.getData()));
	    }
	  }
	} else {
	  setType(key,BaseData.Type.STRING);
	}
      }
    }
  }

  public Dataset copy(){
    Dataset ret=new Dataset(m_name);
    for (DataRow dr:m_rows){
      ret.add(dr.copy());
    }
    //ret.m_rows=(Vector<DataRow>)m_rows.clone();
    return(ret);
  }

  public void empty(){
    m_rows=new Vector<DataRow>();
  }
  public void useWithoutCheck(Dataset d){
    m_rows=new Vector<DataRow>();
    for (DataRow dr:d.m_rows){
      addWithoutCheck(dr.copy());
    }
  }
  public void use(Dataset d){
    m_rows=new Vector<DataRow>();
    for (DataRow dr:d.m_rows){
      add((DataRow)dr.copy());
    }
  }
  public String getName(){
    return(m_name);
  }
  public void setName(String name){
    m_name=name;
  }

  public boolean add(DataRow dr){
    for (String s:dr.getKeys()){
      Mapping m=m_Mapping.get(s);
      if (m==null){
	setMappingFromBaseData(s, dr.get(s));
      } else {
	boolean eq=BaseData.typeEquals(dr.getObject(s), m.m_type);
	if (!eq){
	  System.err.println("Fails insert:"+s+","+m.m_type.toString()+","+BaseData.getType(dr.getObject(s)));
	  return(false);
	}
      }
    }
    m_rows.add(dr);
    return(true);
  }

  public void addWithoutCheck(DataRow dr){
    m_rows.add(dr);
  }
  public int count(){
    return(m_rows.size());
  }
  public DataRow get(int i){
    return(m_rows.get(i));
  }
  public void autoID(String key){
    int i=1;
    for (DataRow dr:m_rows){
      dr.set(key, new BaseData(i++));
    }
  }

  @Override
  public String toString(){
    StringBuilder sb=new StringBuilder();
    for (int i=0;i<count();i++){
      DataRow dr=get(i);
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
      sb.append("\n");
    }
    return(sb.toString());
  }

}
