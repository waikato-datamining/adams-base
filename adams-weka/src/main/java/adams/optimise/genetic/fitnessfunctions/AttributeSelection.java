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
 * AttributeSelection.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise.genetic.fitnessfunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Random;
import java.util.logging.Level;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;
import adams.core.option.OptionUtils;
import adams.optimise.OptData;
import adams.optimise.OptVar;

/**
 * Perform attribute selection using WEKA classification.
 * @author dale
 * @version $Revision$
 */
public class AttributeSelection extends AbstractWEKAFitnessFunction {

  /**suid.  */
  private static final long serialVersionUID = 1967190416117903831L;


  @Override
  public String globalInfo() {
    return "Attribute selection";
  }

  public OptData getDataDef() {
    init();
    OptData odd=new OptData();
    for (int i=0;i<m_Instances.numAttributes()-1;i++) {
      odd.add(new OptVar(""+i, 0,1 ,true));
    }
    return(odd);
  }

  protected int[] getWeights(OptData opd) {
    int[] weights=new int[getInstances().numAttributes()-1];
    int cnt = 0;
    for (int i = 0; i < getInstances().numAttributes(); i++) {
      if ( i == getInstances().classIndex()) {
	continue;
      }
      weights[cnt]=opd.get(""+cnt).intValue();
      cnt++;
    }
    return(weights);
  }

  public double evaluate(OptData opd) {
    init();
    int cnt = 0;
    int[] weights=getWeights(opd);
    Instances newInstances = new Instances(getInstances());
    for (int i = 0; i < getInstances().numInstances(); i++) {
      Instance in = newInstances.instance(i);
      cnt = 0;
      for (int a = 0; a < getInstances().numAttributes(); a++) {
	if (a == getInstances().classIndex())
	  continue;
	if (weights[cnt++] == 0) {
	  in.setValue(a,0);
	}else {
	  in.setValue(a,in.value(a));
	}
      }
    }
    Classifier newClassifier = null;

    try {
      newClassifier = AbstractClassifier.makeCopy(getClassifier());
      // evaluate classifier on data
      Evaluation evaluation = new Evaluation(newInstances);
      evaluation.crossValidateModel(
	  newClassifier,
	  newInstances,
	  getFolds(),
	  new Random(getCrossValidationSeed()));

      // obtain measure
      double measure = 0;
      if (getMeasure() == Measure.ACC)
	measure = evaluation.pctCorrect();
      else if (getMeasure() == Measure.CC)
	measure = evaluation.correlationCoefficient();
      else if (getMeasure() == Measure.MAE)
	measure = evaluation.meanAbsoluteError();
      else if (getMeasure() == Measure.RAE)
	measure = evaluation.relativeAbsoluteError();
      else if (getMeasure() == Measure.RMSE)
	measure = evaluation.rootMeanSquaredError();
      else if (getMeasure() == Measure.RRSE)
	measure = evaluation.rootRelativeSquaredError();
      else
	throw new IllegalStateException("Unhandled measure '" + getMeasure() + "'!");
      measure = getMeasure().adjust(measure);

      return(measure);
      // process fitness

    }
    catch(Exception e) {
      getLogger().log(Level.SEVERE, "Error evaluating", e);
    }

    return 0;
  }
  /**
   * Generates a range string of attributes to keep (= one has to use
   * the inverse matching sense with the Remove filter).
   *
   * @return		the range of attributes to keep
   */
  public String getRemoveAsString(int[] m_weights) {
    String ret = "";
    int pos = 0;
    int last = -1;
    boolean thefirst = true;
    for(int a = 0; a < getInstances().numAttributes()-1; a++)
    {
      if(m_weights[a] == 0 && a != getInstances().classIndex())
      {
	if(last == -1)
	  continue;
	if(thefirst)
	  thefirst = false;
	else
	  ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
	if(pos - last > 1)
	  ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append("-").append(pos + 1).toString();
	else
	  if(pos - last == 1)
	    ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append(",").append(pos + 1).toString();
	  else
	    ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).toString();
	last = -1;
      }
      if(m_weights[a] != 0 || a == getInstances().classIndex())
      {
	if(last == -1)
	  last = a;
	pos = a;
      }
    }

    if(last != -1)
    {
      if(!thefirst)
	ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
      if(pos - last > 1)
	ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append("-").append(pos + 1).toString();
      else
	if(pos - last == 1)
	  ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append(",").append(pos + 1).toString();
	else
	  ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).toString();
    }
    return ret;

  }


  /**
   * Callback for best measure so far
   */
  @Override
  public void newBest(double val, OptData opd) {
    int cnt = 0;
    int[] weights=getWeights(opd);
    Instances newInstances = new Instances(getInstances());
    for (int i = 0; i < getInstances().numInstances(); i++) {
      Instance in = newInstances.instance(i);
      cnt = 0;
      for (int a = 0; a < getInstances().numAttributes(); a++) {
	if (a == getInstances().classIndex())
	  continue;
	if (weights[cnt++] == 0) {
	  in.setValue(a,0);
	}else {
	  in.setValue(a,in.value(a));
	}
      }
    }
    try{
      File file = new File(
	  getOutputDirectory().getAbsolutePath()
	  + File.separator + Double.toString(getMeasure().adjust(val)) + ".arff");
      file.createNewFile();
      Writer writer = new BufferedWriter(new FileWriter(file));
      Instances header = new Instances(newInstances, 0);


      // remove filter setup
      Remove remove = new Remove();
      remove.setAttributeIndices(getRemoveAsString(weights));
      remove.setInvertSelection(true);

      header.setRelationName(OptionUtils.getCommandLine(remove));


      writer.write(header.toString());
      writer.write("\n");
      for (int i = 0; i < newInstances.numInstances(); i++) {
	writer.write(newInstances.instance(i).toString());
	writer.write("\n");
      }
      writer.flush();
      writer.close();
    }catch(Exception e) {
      e.printStackTrace();
    }
  }

}
