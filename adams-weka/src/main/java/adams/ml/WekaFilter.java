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
 * WekaFilter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.ml;

import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.SavitzkyGolay;

public class WekaFilter
extends Filter {
  protected Instances m_Instances;
  protected WekaData m_WekaData_in;
  protected WekaData m_WekaData_out;

  /** regex for arrays */
  protected String m_regex=null;

  protected weka.filters.Filter m_Filter;

  public WekaFilter(){
    m_Instances=null;
    m_WekaData_in=null;
    m_WekaData_out=null;
  }
  public void setInstances(Instances in){
    m_Instances=in;
  }


  public void setFindArrays(String s){
    m_regex=s;
  }
  /*
  public void setDataset(WekaData wd){
    m_WekaData=wd;
    m_Instances=null;
  }
  public void setDataset(Dataset ds){
    m_WekaData=new WekaData(ds);
    m_Instances=null;
  }
	*/
  public void setFilter(weka.filters.Filter filter){
    m_Filter=filter;
  }


  protected WekaData buildandfilterP(Dataset d, String[] atts) {
    if (d instanceof WekaData){
      m_WekaData_in=(WekaData)d;
    } else{
      m_WekaData_in=new WekaData(d);
    }
    if (atts == null){
      m_WekaData_in.setUseAllAttributes();
    } else {
      m_WekaData_in.setAttributes(atts);
    }
    if (m_Instances==null){
      m_Instances = m_WekaData_in.toInstances();
      System.err.println(m_Instances.toString());
    }
    try {
      //m_Classifier.buildClassifier(m_Instances);
      m_Filter.setInputFormat(m_Instances);
      Instances newData=weka.filters.Filter.useFilter(m_Instances, m_Filter);
      m_WekaData_out=new WekaData();
      //m_WekaData_out.setFindArrays(m_regex);
      m_WekaData_out.instancesToDataset(newData);
      m_WekaData_out.findArrays(m_regex);
      return(m_WekaData_out);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(null);
    }
  }


  public WekaData buildandfilter(Dataset d,String[] atts) {
    return(buildandfilterP(d,atts));
  }

  public WekaData buildandfilter(Dataset d) {
    return(buildandfilter(d,null));
  }

  @Override
  public DataRow filter(DataRow d) {
    // TODO Auto-generated method stub
    try {
      //m_Classifier.buildClassifier(m_Instances);
      //m_Filter.setInputFormat(m_Instances);
      Instance inData=m_WekaData_in.DataRowToInstance(d);
      inData.setDataset(m_Instances);

      m_Filter.input(inData);
      //m_Filter.batchFinished();
      Instance newData=m_Filter.output();

      //m_WekaData_out=new WekaData();
      DataRow ret=m_WekaData_out.instanceToDataRow(newData);
      m_WekaData_out.findArrays(ret);
      return(ret);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(null);
    }
  }

  @Override
  public Dataset filter(Dataset d) {
    // TODO Auto-generated method stub
    try {
      //m_Classifier.buildClassifier(m_Instances);
      //m_Filter.setInputFormat(m_Instances);
      Instances newData=weka.filters.Filter.useFilter(m_Instances, m_Filter);
      //m_WekaData_out=new WekaData();
      m_WekaData_out.instancesToDataset(newData);
      m_WekaData_out.findArrays(m_regex);
      return(m_WekaData_out);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(null);
    }
  }
  @Override
  public BuildResult build(String[] srta, String classv) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public BuildResult build(Dataset d) {
    if (d instanceof WekaData){
      m_WekaData_in=(WekaData)d;
    } else{
      m_WekaData_in=new WekaData(d);
    }

    m_WekaData_in.setUseAllAttributes();
    if (m_Instances==null){
      m_Instances = m_WekaData_in.toInstances();
      System.err.println(m_Instances.toString());
    }
    try {
      //m_Classifier.buildClassifier(m_Instances);
      m_Filter.setInputFormat(m_Instances);
      Instances newData=weka.filters.Filter.useFilter(m_Instances, m_Filter);
      m_WekaData_out=new WekaData();
      m_WekaData_out.instancesToDataset(newData);
      m_WekaData_out.findArrays(m_regex);

      return(new BuildResult("Success"));
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(new BuildResult("Fail"));
    }
  }
  public static void main(String [ ] args){
    WekaData wd=new WekaData();
    Random r=new Random();
    for (int i=0;i<30;i++){
      DataRow dr=new DataRow();
      double[] d=new double[20];

      for (int j=0;j<20;j++){
	d[j]=r.nextGaussian();
      }
      dr.set("spec", d);
      dr.set("spec2", d.clone());
      dr.set("class", r.nextDouble());
      wd.add(dr);

    }
    wd.setClass("class");
    WekaFilter wc=new WekaFilter();
    //wc.setFindArrays("(.+)\\[(\\d+)\\]");
    //wc.setFindArrays("(.+)_(\\d+)");
    wc.setFindArrays("(att)(\\d+)");
    //wc.setDataset(wd);
    //wd.setNominalFromDataset("class");
    //wc.setClassifier(new J48());
    wc.setFilter(new SavitzkyGolay());
    //wc.build(wd);
    WekaData res=wc.buildandfilter(wd);//,new String[]{"spec"});
    //out.findArrays();
    //System.err.println(res.toString());

   for (int i=0;i<wd.count();i++){
      DataRow din=wd.get(i);
      DataRow dout=wc.filter(din);
      System.err.println(din.toString());
      System.err.println(dout.toString());
    }

  }
}
