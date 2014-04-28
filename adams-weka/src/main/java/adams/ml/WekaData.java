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
 * WekaData.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.ml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.Utils;



public class WekaData extends Dataset{

  public class ArrayFinder {
    public String m_regex=null;
    // attributename->boolean is array.
    Hashtable<String,Boolean> m_isArray=new Hashtable<String,Boolean>();
    // arrayname->maxindex
    Hashtable<String,Integer> m_maxindex=new Hashtable<String,Integer>();
    // arrayname -> minindex
    Hashtable<String,Integer> m_minindex=new Hashtable<String,Integer>();
    // arrayname->(index->originalString)
    Hashtable<String,Hashtable<Integer,String>> m_attributemapping=new Hashtable<String,Hashtable<Integer,String>>();
    // arrayname->ordered vector of attributes
    Hashtable<String,Vector<String>> m_arrayCompress=new Hashtable<String,Vector<String>>();
    // arrayname->type
    Hashtable<String,BaseData.Type> httype=new Hashtable<String,BaseData.Type>();

    Hashtable<String,Boolean> m_remove=new Hashtable<String,Boolean>();

    Pattern m_pattern=null;
    boolean m_compress=true;
    boolean m_headergenerated=false;

    public void reset(){
      m_isArray=new Hashtable<String,Boolean>();
      m_maxindex =new Hashtable<String,Integer>();
      m_minindex=new Hashtable<String,Integer>();
      // arrayname->(index->originalString)
      m_attributemapping=new Hashtable<String,Hashtable<Integer,String>>();
      // arrayname->ordered vector of attributes
      m_arrayCompress=new Hashtable<String,Vector<String>>();
      // arrayname->type
      httype=new Hashtable<String,BaseData.Type>();
      // arrayname->irrelevant
      m_remove=new Hashtable<String,Boolean>();
      m_compress=true;
    }
    public void reset(String regex){
      if (!regex.equals(m_regex)){
	reset();
	m_regex=regex;
	m_pattern=Pattern.compile(regex);
	m_headergenerated=false;
      }
    }

  }

  /** List of attributes to use in modeling. */
  protected Vector<String> m_attributes;

  /** Target attribute. */
  protected String m_class;

  /** regex for arrays */
  protected Boolean m_findArrays=false;
  protected ArrayFinder m_af=new ArrayFinder();

  public static final String ARRAY_REGEX="(.+)\\[(\\d+)\\]";



  protected Hashtable<String,Hashtable<String,Integer>> m_Nominalise;
  protected Instances m_header=null;

  public WekaData(){
    super();
    m_Nominalise = new Hashtable<String,Hashtable<String,Integer>>();
    m_class = null;
  }

  public WekaData(Dataset ds){
    super();
    m_Nominalise = new Hashtable<String,Hashtable<String,Integer>>();
    m_class = null;
    m_header=null;
    use(ds);
  }

  public void setClass(String name){
    m_class=name;
  }

  public void setFindArrays(Boolean b){
    m_findArrays=b;
  }
  public void setFindArrays(String s){
    m_af.reset(s);
    m_findArrays=(s!=null);
  }
  /**
   * Try and guess at attribute type
   * @param key
   * @param ds
   * @return
   */
  protected Vector<Attribute> generateAttributes(String key){
    Vector<Attribute> va=new Vector<Attribute>();
    Mapping mapping=m_Mapping.get(key);
    if (mapping !=  null){
      switch(mapping.m_type){

	case NUMERIC:
	  va.add(new Attribute(key));
	  return(va);
	case STRING:
	  Hashtable<String,Integer> ht=m_Nominalise.get(key);
	  if (ht != null){
	    FastVector f=new FastVector();
	    for (String nom:ht.keySet()){
	      f.add(nom);
	    }
	    va.add(new Attribute(key,f));
	  } else {
	    va.add(new Attribute(key,(FastVector)null));
	  }
	  return(va);
	case ARRAY:
	  switch(mapping.m_basetype){
	    case NUMERIC:
	      for (int i=0;i<mapping.m_size;i++){
		va.add(new Attribute(key+"["+i+"]"));
	      }
	      return(va);
	    case STRING:
	      for (int i=0;i<mapping.m_size;i++){
		va.add(new Attribute(key+"["+i+"]",(FastVector)null));
	      }
	      return(va);
	    default:
	      System.err.println("Other1");
	      return(va);
	  }
	default:
	  System.err.println("Other2");
	  return(va);
      }
    } else {
      System.err.println("No Mapping for:"+key);
      return(va);
    }

    /*Hashtable<String,BaseData> ht= new Hashtable<String,BaseData>();
    for (int i=0;i<ds.count();i++){
      DataRow dr=ds.get(i);
      BaseData bdt=dr.get(key);
      if (bdt != null){
	ht.put(key, bdt);
      }
    }
    Attribute a=new Attribute(key, key, 0);
     */
  }


  public Vector<String> getAllAttributes(){
    Vector<String> attss=(Vector<String>)m_attributes.clone();
    if (m_class != null ){
      attss.add(m_class);
    }
    return(attss);
  }


  protected Instances generateHeader(){

    ArrayList<Attribute> atts = new ArrayList<Attribute>();
    Vector<String> ats=getAllAttributes();
    for (int i=0;i<ats.size();i++){
      String key=ats.get(i);
      Vector<Attribute> v=generateAttributes(key);
      for (Attribute a:v){
	atts.add(a);
      }
    }
    return(new Instances("", atts, 0));
  }

  public Instances toInstances(){
    return(toInstances(null));
  }

  public Instances toInstances(String[] requirePresent){
    //System.err.println("start gen header");

    m_header=generateHeader();
    //System.err.println("complete");
    m_header.setRelationName(m_name);
    Instances newI=new Instances(m_header,0);
    boolean add;
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      //System.err.println(dr.toString());
      add=true;
      if (requirePresent !=null){
	for (int i=0;i<requirePresent.length;i++){
	  Object bd=dr.getObject(requirePresent[i]);
	  if (bd == null ){
	    add=false;
	    break;
	  }
	}
      }
      if (add){
	Instance i=DataRowToInstance(dr);
	newI.add(i);
      }
    }
    if (m_class != null){
      newI.setClassIndex(newI.numAttributes()-1);
    }
    if (m_findArrays){
      findArrays();
    }
    return(newI);
  }

  protected Instance DataRowToInstance(DataRow dr){
    Instances newI=new Instances(m_header,0);
    DenseInstance di=new DenseInstance(m_header.numAttributes());
    Vector<String> ats=getAllAttributes();
    for (int i=0;i<ats.size();i++){
      String key=ats.get(i);
      BaseData bd=dr.get(key);
      if (bd != null){
	Mapping mapping=m_Mapping.get(key);
	if (mapping == null){
	  System.err.println("Unsure about:"+key);
	  continue;
	}
	Object o=bd.getData();
	Attribute a=newI.attribute(key);
	switch(mapping.m_type){
	  case NUMERIC:
	    if (bd.isNumeric()){
	      try{
		Double val=Utils.toDouble(o.toString());
		di.setValue(a, val);
	      } catch(Exception e){
		System.err.println("Not a number");
	      }
	    }
	    break;
	  case STRING:
	    Hashtable<String,Integer> ht=m_Nominalise.get(key);
	    if (ht != null){
	      if (ht.get(o.toString())!=null){
		di.setValue(a, o.toString());
	      } else {
		System.err.println("trying to set nominal value when not in setup:"+o.toString());
	      }
	    } else {
	      di.setValue(a, o.toString());
	    }
	    break;
	  case ARRAY:
	    switch(mapping.m_basetype){
	      case NUMERIC:
		if (bd.isArray()){
		  for (int j=0;j<mapping.m_size;j++){
		    if (j< Array.getLength(o)){
		      Object ao=Array.get(o, j);
		      a=newI.attribute(key+"["+j+"]");
		      if (bd.isNumeric(ao)){
			try{
			  Double val=Utils.toDouble(ao.toString());
			  di.setValue(a, val);
			} catch(Exception e){
			  System.err.println("Not a number");
			}
		      }
		    }
		  }
		} else {
		  System.err.println("Not an array:"+key);
		}
		break;
	      case STRING:
		for (int j=0;j<mapping.m_size;j++){
		  if (j< Array.getLength(o)){
		    Object ao=Array.get(o, j);
		    a=newI.attribute(key+"["+j+"]");
		    di.setValue(a, ao.toString());
		  }
		}
		break;
	      default:
		System.err.println("Other1");
		break;
	    }
	    break;
	  default:
	    System.err.println("Other2aaa");
	    break;
	}
      }
    }

    return(di);
  }


  public void setAttributes(String[] atts){
    m_attributes=new Vector<String>();
    if (atts != null){
      for (int i=0;i<atts.length;i++){
	if (atts[i] != null){
	  m_attributes.add(atts[i]);
	}
      }
    }
  }

  public void setNominal(String key,String[] noms ){
    Hashtable<String,Integer> ht=new Hashtable<String,Integer>();
    for (int i=0;i<noms.length;i++){
      ht.put(noms[i], i+1);
    }
    m_Nominalise.put(key,ht);
    m_Mapping.put(key, new Mapping(BaseData.Type.STRING));
  }

  public void setNominalFromDataset(String key){

    Hashtable<String,Integer> ht=new Hashtable<String,Integer>();
    int count=0;
    for (int i=0;i<count();i++){
      DataRow dr=get(i);
      BaseData bd=dr.get(key);
      if (bd != null){
	String s=bd.m_data.toString();
	if (ht.get(s) == null){
	  ht.put(s, ++count);
	}
      }
    }
    m_Nominalise.put(key,ht);
    m_Mapping.put(key, new Mapping(BaseData.Type.STRING));
  }

  public void setUseAllAttributes(){
    Hashtable<String,Boolean> ht=new Hashtable<String,Boolean>();
    for (int i=0;i<count();i++){
      DataRow dr=get(i);
      for (String s: dr.getKeys()){
	ht.put(s, true);
      }
    }
    String[] arr=new String[ht.keySet().size()];
    int i=0;
    for (String s: ht.keySet()){
      if (m_class==null ||(m_class!=null && !s.equals(m_class))){
	arr[i++]=s;
      }
    }
    setAttributes(arr);
  }


  protected void findArrays(){
    findArrays("(.+)\\[(\\d+)\\]");
  }

  protected void setArrayFinderFromInstancesHeader(){
    System.err.println("start setup af");
    Instances in=m_header;
    for (int i=0;i<in.numAttributes();i++){
      Attribute a=in.attribute(i);
      String s=a.name();

      m_af.m_isArray.put(s,false);

      Matcher matcher = m_af.m_pattern.matcher(s);
      if (matcher.matches()){
	m_af.m_isArray.put(s,true);
	String name=matcher.group(1);
	String index_s=matcher.group(2);
	Integer index;
	try{
	  index=Integer.parseInt(index_s);
	} catch (Exception e){
	  System.err.println("Funny looking index:"+name+","+index_s);
	  continue;
	}
	//
	Hashtable<Integer,String> indexmapping=(Hashtable<Integer,String>)m_af.m_attributemapping.get(name);
	Vector<String> vs=m_af.m_arrayCompress.get(name);
	if (indexmapping==null){
	  vs=new Vector<String>();
	  m_af.m_arrayCompress.put(name, vs);
	  indexmapping=new Hashtable<Integer,String>();
	  m_af.m_attributemapping.put(name, indexmapping);
	}
	vs.add(s);
	indexmapping.put(index, s);
	m_af.m_remove.put(s,true);
	//
	Integer max_index=m_af.m_maxindex.get(name);
	if (max_index == null || (max_index != null && max_index <= index)){
	  m_af.m_maxindex.put(name, index);
	  if (m_af.httype.get(name) == null){
	    if (a.isNumeric()){
	      m_af.httype.put(name, BaseData.Type.NUMERIC);
	    } else {
	      m_af.httype.put(name, BaseData.Type.STRING);
	    }
	  }
	}

	Integer min_index=m_af.m_minindex.get(name);
	if (min_index == null || (min_index != null && min_index >= index)){
	  m_af.m_minindex.put(name, index);
	}
      }
    }
    for (String key:m_af.httype.keySet()){
      this.setType(key,m_af.httype.get(key) , m_af.m_maxindex.get(key)-m_af.m_minindex.get(key)+1);
    }
    m_af.m_headergenerated = true;
    System.err.println("complete setup af");
  }
  /*
  protected void setArrayFinderFromInstances(Instances in,String regex, ArrayFinder af){
    if (af.pattern == null){
      af.pattern = Pattern.compile(regex);
    }

    for (int i=0;i<in.numAttributes();i++){
      Attribute a=in.attribute(i);
      String s=a.name();
      if (af.m_isArray.containsKey(s)){
	continue;
      }
      af.m_isArray.put(s,false);
      Matcher matcher = af.pattern.matcher(s);
      if (matcher.matches()){
	af.m_isArray.put(s,true);
	String name=matcher.group(1);
	String index_s=matcher.group(2);
	Integer index;
	try{
	  index=Integer.parseInt(index_s);
	} catch (Exception e){
	  System.err.println("Funny looking index:"+name+","+index_s);
	  continue;
	}
	af.htremove.put(s, true);
	//
	Hashtable<Integer,String> indexmapping=(Hashtable<Integer,String>)af.ht2.get(name);
	if (indexmapping==null){
	  indexmapping=new Hashtable<Integer,String>();
	  af.ht2.put(name, indexmapping);
	}
	indexmapping.put(index, s);

	//
	Integer max_index=af.ht.get(name);
	if (max_index == null || (max_index != null && max_index <= index)){
	  af.ht.put(name, index);
	  if (af.httype.get(name) == null){
	    if (a.isNumeric()){
	      af.httype.put(name, new Double(0));
	    } else {
	      af.httype.put(name, "");
	    }
	  }
	}

	Integer min_index=af.htmin.get(name);
	if (min_index == null || (min_index != null && min_index >= index)){
	  af.htmin.put(name, index);
	}
	//dr.set(s, (Object)null);
      }
    }
    af.doneHeader = true;
  } */
  protected void findArrays(DataRow dr){
    ArrayFinder af=m_af;

    if (m_af.m_headergenerated == false){
      this.setArrayFinderFromInstancesHeader();
    }

    for (String s: af.m_maxindex.keySet()){

      // generate array
      BaseData.Type tp=af.httype.get(s);
      if (tp==null){
	System.err.println("Problem with type for:"+s);
	continue;
      }

      switch(tp){
	case NUMERIC:
	  int max=m_af.m_maxindex.get(s);
	  int min=m_af.m_minindex.get(s);
	  //for (int i=0;i<count();i++){

	  //double[] arr=new double[ht.get(s)];
	  double[] arr;
	  Hashtable<Integer,String> indexmapping =m_af.m_attributemapping.get(s);
	  if (m_af.m_compress && indexmapping.size() != max-min+1){
	    Vector<String> vs=m_af.m_arrayCompress.get(s);
	    //System.err.println("compress");
	    arr=new double[vs.size()];
	    for (int i=0;i<arr.length;i++){
	      String attname=vs.get(i);
	      Object o2=dr.getObject(attname);
	      if (o2 == null){
		o2=Double.NaN;
	      }
	      if (o2 instanceof Number){
		arr[i]=((Number)o2).doubleValue();
	      } else if (BaseData.isNumeric(o2)){
		arr[i]=Utils.toDouble(o2.toString());
	      }
	    }
	  } else {
	    arr=new double[max-min+1];
	    for (Integer in:indexmapping.keySet()){

	      String attname=indexmapping.get(in);
	      Object o2=dr.getObject(attname);
	      if (o2 == null){
		o2=Double.NaN;
	      }
	      if (o2 instanceof Number){
		arr[in-min]=((Number)o2).doubleValue();
	      } else if (BaseData.isNumeric(o2)){
		arr[in-min]=Utils.toDouble(o2.toString());
	      }

	    }
	  }

	  dr.set(s, arr);
	  // }
	  break;
	case STRING:
	  // for (int i=0;i<count();i++){
	  //String[] arr=new String[ht.get(s)];
	  max=m_af.m_maxindex.get(s);
	  min=m_af.m_minindex.get(s);
	  //double[] arr=new double[ht.get(s)];
	  String[] arr2=new String[max-min+1];
	  indexmapping =m_af.m_attributemapping.get(s);
	  for (Integer in:indexmapping.keySet()){
	    String attname=indexmapping.get(in);
	    Object o2=dr.getObject(attname);
	    if (o2 !=null){
	      arr2[in-min]=o2.toString();
	    }
	  }
	  /* for (int aindex=0;aindex<ht.get(s);aindex++){
	      Object o2=dr.getObject(s+"["+aindex+"]");
	      if (o2 !=null){
		arr[aindex]=o2.toString();
	      }
	    }*/
	  dr.set(s, arr2);
	  // }
	  break;
	default:
	  System.err.println("aProblem with type for:"+s);
	  break;
      }
      // add new type
      //setType(s, this.getArrayType(af.httype.get(s)), af.ht.get(s));

      //m_attributes.add(s);
    }


    for (int i=0;i<count();i++){
      for (String key:af.m_remove.keySet()){
	dr.m_Data.remove(key);
      }
    }

  }


  protected void findArrays(String regex){
    System.err.println("start fa");
    m_af.reset(regex);
    for (int i=0;i<count();i++){
      findArrays(get(i));
    }
    System.err.println("complete fa");
  }
  /* protected void findArrays(String regex){

    // arryname->max
    Hashtable<String,Integer> ht=new Hashtable<String,Integer>();
    // arrayname->(index->originalString)
    Hashtable<String,Hashtable<Integer,String>> ht2=new Hashtable<String,Hashtable<Integer,String>>();
    // arrayname->type
    Hashtable<String,Object> httype=new Hashtable<String,Object>();
    // arrayname->irrelevant
    Hashtable<String,Boolean> htremove=new Hashtable<String,Boolean>();

    for (int i=0;i<count();i++){
      DataRow dr=get(i);
      Pattern pattern = Pattern.compile(regex);
      for (String s: dr.getKeys()){
	Matcher matcher = pattern.matcher(s);
	if (matcher.matches()){
	  String name=matcher.group(1);
	  String index_s=matcher.group(2);
	  Integer index;
	  try{
	    index=Integer.parseInt(index_s);
	  } catch (Exception e){
	    System.err.println("Funny looking index:"+name+","+index_s);
	    continue;
	  }
	  htremove.put(s, true);
	  //
	  Hashtable<Integer,String> indexmapping=(Hashtable<Integer,String>)ht2.get(name);
	  if (indexmapping==null){
	    indexmapping=new Hashtable<Integer,String>();
	    ht2.put(name, indexmapping);
	  }
	  indexmapping.put(index, s);

	  //
	  Integer max_index=ht.get(name);
	  if (max_index == null || (max_index != null && max_index <= index)){
	    ht.put(name, index+1);
	    if (httype.get(name) == null){
	      httype.put(name, dr.getObject(s));
	    }
	  }
	  //dr.set(s, (Object)null);
	}
      }
    }

    for (String s: ht.keySet()){
      // generate array
      Object o=httype.get(s);
      if (o==null){
	System.err.println("Problem with type for:"+s);
	continue;
      }
      BaseData.Type typ = getArrayType(httype.get(s));
      switch(typ){
	case NUMERIC:

	  for (int i=0;i<count();i++){
	    DataRow dr=get(i);
	    double[] arr=new double[ht.get(s)];

	    Hashtable<Integer,String> indexmapping =ht2.get(s);
	    for (Integer in:indexmapping.keySet()){
	      String attname=indexmapping.get(in);
	      Object o2=dr.getObject(attname);
	      if (o2 !=null && BaseData.isNumeric(o2)){
		arr[in]=Utils.toDouble(o2.toString());
	      }
	    }


	    dr.set(s, arr);
	  }
	  break;
	case STRING:
	  for (int i=0;i<count();i++){
	    DataRow dr=get(i);
	    String[] arr=new String[ht.get(s)];
	    Hashtable<Integer,String> indexmapping =ht2.get(s);
	    for (Integer in:indexmapping.keySet()){
	      String attname=indexmapping.get(in);
	      Object o2=dr.getObject(attname);
	      if (o2 !=null && BaseData.isNumeric(o2)){
		arr[in]=o2.toString();
	      }
	    }

	    dr.set(s, arr);
	  }
	  break;
	default:
	  System.err.println("aProblem with type for:"+s);
	  break;
      }
      // add new type
      setType(s, this.getArrayType(httype.get(s)), ht.get(s));

      m_attributes.add(s);
    }

    for (int i=0;i<count();i++){
      DataRow dr=get(i);
      for (String key:htremove.keySet()){
	dr.set(key, (Object)null);
      }
    }

    Vector<String> m_attributes_new = new Vector<String>();
    for (String att:m_attributes){
      if (!htremove.containsKey(att)){
	m_attributes_new.add(att);
      }
    }
    m_attributes=m_attributes_new;
  }
   */
  public DataRow instanceToDataRow(Instance in){
    DataRow dr=new DataRow();
    for (int j=0;j<in.numAttributes();j++){
      Attribute att=in.attribute(j);
      if (Double.isNaN(in.value(att))){
	continue;
      }
      if (att.type() == Attribute.NOMINAL || att.type() == Attribute.STRING){
	dr.set(att.name(), in.stringValue(att));
      }else if (att.type() == Attribute.NUMERIC){
	dr.set(att.name(),in.value(att));
      }
    }
    if (m_findArrays){
      findArrays(dr);
    }
    return(dr);
  }

  public boolean loadArff(String filename, boolean qad){
    try{
      BufferedReader reader = new BufferedReader(
	  new FileReader(filename));
      Instances data = new Instances(reader);
      reader.close();
      // setting class attribute
      data.setClassIndex(data.numAttributes() - 1);
      if (qad){
	instancesToDatasetNumericArray(data);
      } else {
	instancesToDataset(data);
      }
      return(true);
    } catch (Exception e){
      return(false);
    }
  }

  public boolean loadArff(String filename){
    try{
      BufferedReader reader = new BufferedReader(
	  new FileReader(filename));
      Instances data = new Instances(reader);
      reader.close();
      // setting class attribute
      data.setClassIndex(data.numAttributes() - 1);
      instancesToDataset(data);
      return(true);
    } catch (Exception e){
      return(false);
    }
  }

  public void instancesToDatasetNumericArray(Instances insts){
    empty();
    m_header=new Instances(insts,0);

    setName(insts.relationName());
    for (int i=0;i<insts.numInstances();i++){
      DataRow dr=new DataRow();

      Instance in=insts.get(i);
      double arr[] =in.toDoubleArray();
      double arr2[]=new double[arr.length-1];
      System.arraycopy(arr, 0, arr2, 0, arr.length-1);
      dr.set("attributes",arr2);
      dr.set(in.attribute(arr.length-1).name(),in.value(arr.length-1));
      add(dr);
    }

  }

  public void instancesToDataset(Instances insts){
    empty();
    m_header=new Instances(insts,0);
    Vector<String> nominalise=new Vector<String>();
    for (int j=0;j<insts.numAttributes();j++){
      Attribute att=insts.attribute(j);
      if (att.type() == Attribute.NOMINAL || att.type() == Attribute.STRING){
	setType(att.name(), BaseData.Type.STRING);
	if (att.type() == Attribute.NOMINAL){
	  nominalise.add(att.name());
	}
      }else if (att.type() == Attribute.NUMERIC){
	setType(att.name(), BaseData.Type.NUMERIC);
      }
    }
    setName(insts.relationName());
    for (int i=0;i<insts.numInstances();i++){
      DataRow dr=new DataRow();

      Instance in=insts.get(i);
      for (int j=0;j<in.numAttributes();j++){
	Attribute att=in.attribute(j);
	if (Double.isNaN(in.value(att))){
	  continue;
	}
	if (att.type() == Attribute.NOMINAL || att.type() == Attribute.STRING){
	  dr.set(att.name(), in.stringValue(att));
	  if (att.type() == Attribute.NOMINAL){
	    nominalise.add(att.name());
	  }
	}else if (att.type() == Attribute.NUMERIC){
	  dr.set(att.name(),in.value(att));
	}
      }
      add(dr);
      //setMappingFromDataRow(dr);
    }
    String arr[]=new String[insts.numAttributes()];
    for (int j=0;j<insts.numAttributes();j++){
      Attribute att=insts.attribute(j);
      if (insts.classIndex() == j){
	setClass(att.name());
      } else {
	arr[j]=att.name();
      }
    }
    if (insts.classIndex() == -1){
      setClass(null);
    }
    setAttributes(arr);
    for (String s:nominalise){
      setNominalFromDataset(s);
    }

    /* if (m_findArrays){
      System.err.println("find arrays");
      //System.err.println("find arrays part 1 done");
      findArrays(m_af.m_regex);
    } */
  }


  public static void main(String [ ] args){
    Dataset ds=new Dataset("Test");
    DataRow dr=new DataRow();
    dr.set("id", "a1");
    dr.set("fruit", "banana");
    double[] arr=new double[10];
    for (int i=0;i<10;i++){
      arr[i]=i*.99;
    }
    dr.set("spectrum", arr);
    ds.add(dr);

    dr=new DataRow();
    dr.set("id", "a2");
    dr.set("fruit", "tomao");
    int[] arr1=new int[10];
    for (int i=0;i<10;i++){
      arr1[i]=1+i;
    }
    dr.set("spectrum", arr1);
    ds.add(dr);
    ds.autoID("auto_id");

    WekaData wc=new WekaData(ds);
    DataRow dr2=new DataRow();
    dr.set("id", "a1");
    dr.set("fruit", "banana");
    double[] arr2=new double[10];
    for (int i=0;i<10;i++){
      arr[i]=i*.99;
    }
    dr.set("spectrum", arr2);
    // wc.add(dr2);
    wc.setClass("fruit");
    //098wc.setMappingFromDataRow(dr);
    //wc.setNominal("auto_id",new String[]{"1","2"});
    //wc.setNominalFromDataset("auto_id",ds);
    wc.setNominalFromDataset("fruit");
    //dr.set("spectrum", new BaseData("goog"));

    //wc.setType("id", BaseData.Type.STRING);
    //wc.setType("fruit", BaseData.Type.STRING);
    //wc.setType("spectrum", BaseData.Type.NUMERIC,55);

    wc.setAttributes(new String[]{"auto_id","id","spectrum"});
    Instances in=wc.toInstances();
    in.setClassIndex(in.numAttributes()-1);

    System.err.println(in.toString());

    WekaData wc2=new WekaData();
    wc2.instancesToDataset(in);
    //wc2.setUseAllAttributes();
    System.err.println(wc2.toString());
    System.err.println("now find arrays");
    wc2.findArrays("(.+)\\[(\\d+)\\]");
    //wc2.setUseAllAttributes();

    wc2.setAttributes(new String[]{"auto_id","id","spectrum"});
    Instances in2=wc2.toInstances();
    System.err.println(in2.toString());
    System.err.println(wc2.toString());
    /* WekaData wc2=new WekaData();
    ds1=wc2.instancesToDataset(in);

    wc2.findArrays(ds1,"(.+)\\[(\\d+)\\]");
    System.err.println(ds1.toString());
    in2=wc2.DatasetToInstances(ds1);
    System.err.println(in2.toString());*/
  }
}
