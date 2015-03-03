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
 * WekaClassifier.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.ml;

import java.util.Random;
import java.util.Vector;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.GPD;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.supervised.attribute.PLSFilter;

/**
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifier
extends Classifier {

  protected Instances m_Instances;
  protected WekaData m_WekaData;

  protected weka.classifiers.Classifier m_Classifier;


  public WekaClassifier(){
    m_Instances=null;
    m_WekaData=null;
  }
  public void setInstances(Instances in){
    m_Instances=in;
  }
  public void setDataset(WekaData wd){
    m_WekaData=wd;
    m_Instances=null;
  }
  public void setDataset(Dataset ds){
    m_WekaData=new WekaData(ds);
    m_Instances=null;
  }

  public void setClassifier(weka.classifiers.Classifier classifier){
    m_Classifier=classifier;
  }

  public Evaluation crossValidate(int numFolds, int rnd){
    if (m_Instances==null && m_WekaData != null){
      m_Instances = m_WekaData.toInstances(new String[]{m_class});
    }
    try {
      Evaluation e=new Evaluation(m_Instances);
      e.crossValidateModel(m_Classifier, m_Instances, numFolds, new Random(rnd));
      //System.out.println(e.toSummaryString("\nResults\n======\n", false));
      return(e);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(null);
    }

  }

  @Override
  BuildResult build(String cls) {
    // TODO Auto-generated method stub
    m_WekaData.setClass(cls);
    m_WekaData.setUseAllAttributes();
    if (m_Instances==null && m_WekaData != null){
      m_Instances = m_WekaData.toInstances(new String[]{cls});
      //System.err.println(m_Instances.toString());
    }
    try {
      m_Classifier.buildClassifier(m_Instances);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(new BuildResult("Fail:\n"+e.toString()));
    }
    return(new BuildResult("Success\n"+m_Classifier.toString()));

  }
  @Override
  BuildResult build(String[] srta, String classv) {
    // TODO Auto-generated method stub
    m_WekaData.setAttributes(srta);
    return build(classv);
  }
  @Override
  BuildResult build(Vector<String> attributes, String classv) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  ClassificationResult classify(DataRow row) {
    // TODO Auto-generated method stub
    Instance in=m_WekaData.DataRowToInstance(row);
    in.setDataset(m_Instances);
    try {
      double res=m_Classifier.classifyInstance(in);
      if (in.classAttribute().isNominal()){
	return(new ClassificationResult("Success",res,in.classAttribute().value((int)res)));
      } else {
	return(new ClassificationResult("Success",res));
      }
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return(new ClassificationResult("Fail:\n"+e.toString()));
    }

  }

  public static void main(String [ ] args){
    WekaData wd=new WekaData();
    /*Random r=new Random();
    for (int i=0;i<100;i++){
      DataRow dr=new DataRow();
      double[] d=new double[20];

      for (int j=0;j<20;j++){
	d[j]=r.nextGaussian();
      }
      dr.set("spec", d);
      dr.set("class", r.nextDouble());
      wd.addWithCheck(dr);
    } */
    //wd.setFindArrays("(amplitude)-(\\d+)");

    wd.loadArff("/home/dale/fatty3.arff");
    System.err.println("loaded");
    wd.findArrays("(amplitude)-(\\d+)");
    System.err.println("found");

   // System.err.println(wd.toString());

    //WekaFilter wf=new WekaFilter();
    //wf.setFilter(new RemoveInstancesWithMissingValue());
    //wf.setFindArrays("(amplitude)\\[(\\d+)\\]");
    //wf.setFindArrays(WekaData.ARRAY_REGEX);
    //wd=wf.buildandfilter(wd);
    System.err.println(wd.toString());
    WekaClassifier wc=new WekaClassifier();
    wc.setDataset(wd);
    //wd.setNominalFromDataset("class");
    weka.classifiers.meta.FilteredClassifier c=new FilteredClassifier();
    c.setClassifier(new GPD());
    weka.filters.supervised.attribute.PLSFilter f=new PLSFilter();
    f.setNumComponents(3);
    c.setFilter(f);
    wc.setClassifier(c);
    BuildResult br=wc.build("ref_dat.value");
    Evaluation e=wc.crossValidate(10, 1);
    System.out.println(e.toSummaryString("\nResults\n======\n", false));
    //System.err.println(br.m_result.toString());

    for (int i=0;i<wd.count();i++){
      DataRow d=wd.get(i);
      //System.err.println(d.toString());
      ClassificationResult cr=wc.classify(d);
      System.err.println(cr.toString());
    }
  }
}
