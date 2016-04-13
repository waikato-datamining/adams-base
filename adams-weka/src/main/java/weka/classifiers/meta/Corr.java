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
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import adams.core.io.FileUtils;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.functions.LinearRegressionJ;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Assume NO MISSING VALUES, all attributes must be  NUMERIC (or 0/1 maybe ...). Simple attribute selection for regression: select k most correlated attrs ...
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.functions.LinearRegression)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -K &lt;int&gt;
 *  The number of attrs. (default: 0 = numEx/2)</pre>
 *
 <!-- options-end -->
 *
 * @author ???
 * @version $Revision$
 */
public class Corr
  extends SingleClassifierEnhancer
  implements WeightedInstancesHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8615616615151098897L;

  protected Remove m_remove;

  protected int m_classIndex = -1;

  protected int m_k = 0;

  protected int[] m_subset;

  protected double[] m_coeffs;

  // (pearson) sample correlation
  // r = (Sum xy - N*mean(x)*mean(y)) / (n-1)*s_x*s_y

  ///   BETTER   ???
  ///   r12 = [(Yi1 - Y-bar1)*(Yi2 - Y-bar2)] / [(Yi1 - Y-bar1)2 * (Yi2 - Y-bar2)2]1/2

  /// or "re-use" simpleLinearRegression estimates
  /*
	m_slope = 0;
	for (int j = 0; j < insts.numInstances(); j++) {
	  Instance inst = insts.instance(j);
	  if (!inst.isMissing(i) && !inst.classIsMissing()) {
	    double xDiff = inst.value(i) - xMean;
	    double yDiff = inst.classValue() - yMean;
	    double weightedXDiff = inst.weight() * xDiff;
	    double weightedYDiff = inst.weight() * yDiff;
	    m_slope += weightedXDiff * yDiff;
	    sumWeightedXDiffSquared += weightedXDiff * xDiff;
	    sumWeightedYDiffSquared += weightedYDiff * yDiff;
	  }
	}

	// Skip attribute if not useful
	if (sumWeightedXDiffSquared == 0) {
	  continue;
	}
	double numerator = m_slope;
	m_slope /= sumWeightedXDiffSquared;
	m_intercept = yMean - m_slope * xMean;

	// Compute sum of squared errors
	double msq = sumWeightedYDiffSquared - m_slope * numerator;


	int k = insts.numAttributes;
	int classIndex = insts.classIndex();

	double[] mean = means();
	double[] slope = new double[k];
	double[] sumWeightedDiffSquared = new double[k];

	for (int j = 0; j < insts.numInstances(); j++) {

	  Instance inst = insts.instance(j);
	  double yDiff = inst.value(classIndex) - mean[classIndex];

	  for(int i = 0; i < inst.numAttributes(); i++) {
	  // must include classIndex here
	    double xDiff = inst.value(i) - mean[i];
	    double weightedXDiff = inst.weight() * xDiff;
	    slope[i] += weightedXDiff * yDiff;
	    sumWeightedDiffSquared[i] += weightedXDiff * xDiff;
	  }
	}

	msq = new double[insts.numAttributes()];
	Arrays.fill(msq,Double.MAX_VALUE);
	// Skip attribute if not useful
	for(int i = 0; i < msq.length; i++) {
	if (sumWeightedDiffSquared[i] == 0) {
	continue;
	}

	// Compute sum of squared errors
	double sse = sumWeightedDiffSquared[classIndex] - m_slope[i] * m_slope[i] / sumWeightedDiffSquared[i];
	if (Double.isInfinite(sse)) continue;
	if (Double.isNaN(sse)) continue;
	msq[i] = sse;
}
  */

  /**
   * Initializes the classifier.
   * /
  public Corr() {
    m_Classifier = new LinearRegression();
  }
  */

  /**
   * String describing default classifier.
   *
   * @return		the default classifier
   */
  protected String defaultClassifierString() {
    return LinearRegressionJ.class.getName();
  }

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Assume NO MISSING VALUES, all attributes must be  NUMERIC (or 0/1 maybe ...). "
      + "Simple attribute selection for regression: select k most correlated attrs ...";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {

    Vector newVector = new Vector();

    Enumeration enu = super.listOptions();
    while (enu.hasMoreElements()) {
      newVector.addElement(enu.nextElement());
    }

    newVector.addElement(new Option("\tThe number of attrs. (default: 0 = numEx/2)",
				    "K", 1, "-K <int>"));

    return newVector.elements();
  }

  /**
   * Parses a given list of options.
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -W
   *  Full name of base classifier.
   *  (default: weka.classifiers.functions.LinearRegression)</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.rules.ZeroR:
   * </pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -K &lt;int&gt;
   *  The number of attrs. (default: 0 = numEx/2)</pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {

    String tmpStr = Utils.getOption('K', options);
    if (tmpStr.length() > 0) {
      setNumattrs(Integer.parseInt(tmpStr));
    } else {
      setNumattrs(0);
    }

    super.setOptions(options);

  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<String>();

    result.add("-K");
    result.add(""+getNumattrs());

    String[] options = super.getOptions();
    for (String option: options)
      result.add(option);

    return result.toArray(new String[result.size()]);
  }

  public int getNumattrs() {
    return m_k;
  }

  public void setNumattrs(int k) {
    m_k = k;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numattrsTipText() {
    return "The number of attributes.";
  }

  public double[] sampleCorrs(Instances data) {
    double[] mean = means(data);
    double[] s = sampleDevs(data,mean);
    double[] sumXY = sumXY(data);

    int n = data.numInstances();
    int k = mean.length;

    double[] corrs = new double[k];
    int yIndex = data.classIndex();

    for(int i = 0; i < k; i++) {
      double r = (sumXY[i] - n * mean[i] * mean[yIndex]) / ((n-1.0)*s[i]*s[yIndex]);
      corrs[i] = r*r;
      if (getDebug()) System.out.println("x " + i + " sXY " + sumXY[i] + " m " + mean[i] + " mY " + mean[yIndex] + " s " + s[i] + " sY " + s[yIndex]);
    }

    return corrs;
  }

  public double[] sumXY(Instances data) {
    int classIndex = data.classIndex();
    int k = data.numAttributes();
    double[] sum = new double[k];
    for(int i = 0; i < data.numInstances(); i++) {
      Instance instance = data.instance(i);
      double classValue = instance.value(classIndex);
      for(int j = 0; j < k; j++) {
	sum[j] += classValue * instance.value(j);
      }
    }
    return sum;
  }

  public double[] sampleDevs(Instances data, double[] mean) {
    int k = mean.length;
    double[] sum = new double[k];
    for(int i = 0; i < data.numInstances(); i++) {
      Instance instance = data.instance(i);
      for(int j = 0; j < k; j++) {
	double delta = instance.value(j) - mean[j];
	sum[j] += delta*delta;
      }
    }
    double factor = 1.0 / (data.numInstances() - 1.0);
    for(int j = 0; j < k; j++) {
      sum[j] = Math.sqrt(sum[j] * factor);
    }
    return sum;
  }

  public double[] msq(Instances data) {
    int k = data.numAttributes();
    int classIndex = data.classIndex();

    double[] mean = means(data);
    double[] slope = new double[k];
    double[] sumWeightedDiffSquared = new double[k];

    for (int j = 0; j < data.numInstances(); j++) {

      Instance inst = data.instance(j);
      double yDiff = inst.value(classIndex) - mean[classIndex];

      for(int i = 0; i < k; i++) {
	// must include classIndex here
	double xDiff = inst.value(i) - mean[i];
	double weightedXDiff = inst.weight() * xDiff;
	slope[i] += weightedXDiff * yDiff;
	sumWeightedDiffSquared[i] += weightedXDiff * xDiff;
      }
    }

    double[] msq = new double[k];
    Arrays.fill(msq,Double.MAX_VALUE);

    for(int i = 0; i < msq.length; i++) {
      // Skip attribute if not useful
      if (i == classIndex) continue;
      if (sumWeightedDiffSquared[i] == 0) continue;

      // Compute sum of squared errors
      double sse = sumWeightedDiffSquared[classIndex] - slope[i] * slope[i] / sumWeightedDiffSquared[i];
      if (Double.isInfinite(sse)) continue;
      if (Double.isNaN(sse)) continue;
      msq[i] = sse;
    }
    return msq;
  }

  public double[] means(Instances data) {
    int k = data.numAttributes();
    double[] sum = new double[k];
    for(int i = 0; i < data.numInstances(); i++) {
      Instance instance = data.instance(i);
      for(int j = 0; j < k; j++) {
	sum[j] += instance.value(j);
      }
    }
    double factor = 1.0 / data.numInstances();
    for(int j = 0; j < k; j++) {
      sum[j] *= factor;
    }
    return sum;
  }

  // based on msq smaller is better ...
  public String keepIndices(Instances data) {
    double[] msq = msq(data);
    int[] ascending = Utils.sort(msq);

    if (getDebug()) {
      System.out.println("msq " + Arrays.toString(msq));
      System.out.println("order " + Arrays.toString(ascending));
    }
    int numKeep = getNumattrs();
    if (numKeep == 0) {
      numKeep = (data.numInstances()+1) / 2;
    }
    int classIndex = data.classIndex();
    if (getDebug()) {
      System.out.println("keep " + data.numInstances() + " => " + numKeep + " ci " + classIndex);
    }
    StringBuilder sb = new StringBuilder(""+(1+classIndex));
    int[] indices = new int[numKeep];
    for(int i = 0; i < numKeep; i++) {
      int index = ascending[i];
      assert (index != classIndex);
      sb.append(","+(1+index));
      indices[i] = index;
    }
    if (getDebug()) {
      System.out.println("keep " + data.numInstances() + " " + numKeep + " " + sb.toString());
      System.out.println("indices " + indices.length + " " + Arrays.toString(indices));
    }
    m_subset = indices;
    return sb.toString();
  }

  public String keepIndicesBasedOnCorrelation(Instances data) {
    double[] corrs = sampleCorrs(data);
    int[] ascending = Utils.sort(corrs);

    if (getDebug()) {
      System.out.println("corrs " + Arrays.toString(corrs));
      System.out.println("order " + Arrays.toString(ascending));
    }
    int numKeep = getNumattrs();
    if (numKeep == 0) {
      numKeep = (data.numInstances()+1) / 2;
    }
    int classIndex = data.classIndex();
    StringBuilder sb = new StringBuilder(""+(1+classIndex));
    int[] indices = new int[numKeep];
    int offset = 0;
    for(int i = ascending.length-numKeep; i < ascending.length; i++) {
      int index = ascending[i];
      if (index != classIndex) {
	sb.append(","+(1+index));
	indices[offset++] = index;
      }
    }
    if (getDebug()) {
      System.out.println("keep " + data.numInstances() + " " + numKeep + " " + sb.toString());
      System.out.println("indices " + indices.length + " " + Arrays.toString(indices));
    }
    return sb.toString();
  }

  public Instances getSubset(Instances data) throws Exception {
    m_remove = null;
    if (data.numAttributes() - 1 < getNumattrs()) return data;
    if ((getNumattrs() == 0) && (data.numInstances() >= 2*(data.numAttributes()-1))) return data;
    m_remove = new Remove();
    String toKeep = keepIndices(data);
    m_remove.setOptions(new String[]{"-R",toKeep,"-V"});
    m_remove.setInputFormat(data);
    return Filter.useFilter(data,m_remove);
  }


  /**
   * Builds the classifier.
   *
   * @param data	the training data
   * @throws Exception	if something goes wrong
   */
  public void buildClassifier(Instances data) throws Exception {
    //m_classIndex = data.classIndex();
    //System.out.print("kernelize " + data.numInstances() + " k " + getNumattrs() + " seed " + getSeed());
    //Instances train = kernelizeTraining(data, getNumattrs(), new Random(getSeed()));
    //System.out.println(" done");

    Instances train = getSubset(data);

    //if (getDebug()) System.out.println(train);
    //if (getDebug()) saveObject(m_remove);

    m_Classifier.buildClassifier(train);

    if (m_Classifier instanceof LinearRegressionJ) {
      double[] coeffs = ((LinearRegressionJ)m_Classifier).coefficients();
      if (getDebug())
	System.out.println("coeffs " + coeffs.length + " " + Arrays.toString(coeffs));
      if (getDebug())
	System.out.println(m_Classifier);
      m_coeffs = new double[coeffs.length-1];
      int offset = 0;
      for(int i = 0; i < m_coeffs.length; i++) {
	if (i != train.classIndex()) {
	  m_coeffs[offset++] = coeffs[i];
	}
      }
      // copy bias ...
      m_coeffs[offset] = coeffs[m_coeffs.length];
      //System.out.println("c " + Arrays.toString(m_coeffs));
    }
  }

  public void saveObject(Object o) throws Exception {
    FileOutputStream	fos;
    ObjectOutputStream 	oos;

    fos = new FileOutputStream("xxx.ser");
    oos = new ObjectOutputStream(fos);
    oos.writeObject(o);

    FileUtils.closeQuietly(oos);
    FileUtils.closeQuietly(fos);
  }

  /**
   * Returns the prediction.
   *
   * @param instance	the instance to classify
   * @return		the prediction
   * @throws Exception	if prediction fails
   */
  public double classifyInstance(Instance instance) throws Exception {

    if (m_coeffs != null) {
      if (m_subset != null) {
	double sum = m_coeffs[m_coeffs.length-1];
	for(int i = 0; i < m_subset.length; i++) {
	  sum += m_coeffs[i] * instance.value(m_subset[i]);
	}
	return sum;
      } else {
	int offset = 0;
	double sum = 0.0;
	for(int i = 0; i < instance.numAttributes(); i++) {
	  if (i != instance.classIndex()) {
	    sum += m_coeffs[offset++] * instance.value(i);
	  }
	}
	sum += m_coeffs[offset];
	return sum;
      }
    }
    if (m_remove != null) {
      m_remove.input(instance);
      m_remove.batchFinished();
      instance = m_remove.output();
    }
    return m_Classifier.classifyInstance(instance);
  }

  public double[] getCoeffs() {
    return (double[]) m_coeffs.clone();
  }

  public int[] getSubset() {
    if (m_subset == null) return null;
    return (int[]) m_subset.clone();
  }

  /**
   * Returns description of classifier.
   *
   * @return		the model
   */
  public String toString() {
    return m_Classifier.toString();
  }

  public String getRevision() {
    return "$Revision$";
  }

  /**
   * Main method for testing this class.
   *
   * @param argv the options
   */
  public static void main(String [] argv) throws Exception {
    runClassifier(new Corr(), argv);
  }
}
