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
 * RangeCheck.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import weka.classifiers.RangeCheckClassifier;
import weka.classifiers.RangeCheckHelper;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Keeps track of the ranges in case of numeric attributes. It then allows checks whether an instance is for one or more attributes outside the training range.
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
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RangeCheck
  extends SingleClassifierEnhancer
  implements RangeCheckClassifier {

  /** for serialization. */
  private static final long serialVersionUID = 735384379601515402L;
  
  /** the ranges (attribute index &lt;-&gt; double[]). */
  protected Hashtable<Integer,double[]> m_Ranges = new Hashtable<Integer,double[]>();

  /** the training header. */
  protected Instances m_Header;
  
  /**
   * Returns a string describing this classifier.
   *
   * @return 		a description of the classifier suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Keeps track of the ranges in case of numeric attributes. It then "
	+ "allows checks whether an instance is for one or more attributes "
        + "outside the training range.";
  }

  /**
   * Build the classifier on the filtered data.
   *
   * @param data 	the training data
   * @throws Exception 	if the classifier could not be built successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    int			i;
    AttributeStats	stats;
    double[]		range;

    data = new Instances(data);
    data.deleteWithMissingClass();

    // obtain ranges
    m_Header = null;
    m_Ranges.clear();
    for (i = 0; i < data.numAttributes(); i++) {
      // only numeric attributes
      if (!data.attribute(i).isNumeric())
	continue;
      // skip class
      if (i == data.classIndex())
	continue;
      stats = data.attributeStats(i);
      range = new double[]{stats.numericStats.min, stats.numericStats.max};
      m_Ranges.put(i, range);
    }

    // can classifier handle the data?
    getClassifier().getCapabilities().testWithFail(data);

    m_Classifier.buildClassifier(data);

    if (m_Ranges.size() > 0)
      m_Header = new Instances(data, 0);
  }

  /**
   * Classifies a given instance after filtering.
   *
   * @param instance 	the instance to be classified
   * @return 		the class distribution for the given instance
   * @throws Exception 	if instance could not be classified successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    return m_Classifier.distributionForInstance(instance);
  }

  /**
   * Checks the range for the instance. The array contains an entry for each
   * attribute that exceeded the stored ranges.
   * 
   * @param inst	the instance to check
   * @return		the failed checks
   */
  @Override
  public List<String> checkRangeForInstance(Instance inst) {
    ArrayList<String>	result;
    int			i;
    double[]		range;
    String		msg;
    
    result = new ArrayList<String>();
    
    for (i = 0; i < inst.numAttributes(); i++) {
      // only numeric attributes
      if (!inst.attribute(i).isNumeric())
	continue;
      // skip class
      if (i == inst.classIndex())
	continue;
      range = m_Ranges.get(i);
      msg   = RangeCheckHelper.isOutside(inst, i, range[0], range[1]);
      if (msg != null)
	result.add(msg);
    }

    return result;
  }

  /**
   * Output a representation of this classifier
   *
   * @return a representation of this classifier
   */
  @Override
  public String toString() {
    StringBuilder	result;
    ArrayList<Integer>	indices;
    double[]		range;
    
    result = new StringBuilder();

    if (m_Header != null) {
      if (m_Ranges.size() > 0) {
	indices = new ArrayList<Integer>(m_Ranges.keySet());
	Collections.sort(indices);
	result.append("Recorded ranges\n");
	result.append("---------------\n\n");
	for (Integer index: indices) {
	  range = m_Ranges.get(index);
	  result.append(m_Header.attribute(index).name() + ": [" + range[0] + "," + range[1] + "]");
	  result.append("\n");
	}
	result.append("\n");
      }
    }
    
    result.append(m_Classifier.toString());
    
    return result.toString();
  }
}
