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
 * AbstractSingleClassPLS.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.filters.supervised.attribute.pls;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Center;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.ArrayList;
import java.util.Map;

/**
 * Ancestor for schemes that predict a single class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSingleClassPLS
  extends AbstractPLS {

  private static final long serialVersionUID = 5649007256147616278L;

  public static final String PARAM_CLASSVALUES = "classValues";

  public static final String PARAM_CLASSMEAN = "classMean";

  public static final String PARAM_CLASSSTDEV = "classStdev";

  /** for replacing missing values */
  protected Filter m_Missing = null;

  /** for centering the data */
  protected Filter m_Filter = null;

  /** the class mean. */
  protected double m_ClassMean;

  /** the class stddev. */
  protected double m_ClassStdDev;

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_Missing = null;
    m_Filter  = null;
  }

  /**
   * Determines the output format based on the input format and returns this.
   *
   * @param input 	the input format to base the output format on
   * @return 		the output format
   * @throws Exception 	in case the determination goes wrong
   */
  @Override
  public Instances determineOutputFormat(Instances input) throws Exception {
    ArrayList<Attribute> atts;
    String 			prefix;
    int 			i;
    Instances 			result;

    // generate header
    atts = new ArrayList<>();
    prefix = getClass().getSimpleName();
    for (i = 0; i < getNumComponents(); i++)
      atts.add(new Attribute(prefix + "_" + (i + 1)));
    atts.add(new Attribute("Class"));
    result = new Instances(prefix, atts, 0);
    result.setClassIndex(result.numAttributes() - 1);

    m_OutputFormat = result;

    return result;
  }

  /**
   * Preprocesses the data.
   *
   * @param instances the data to process
   * @return the preprocessed data
   */
  protected Instances preTransform(Instances instances, Map<String,Object> params) throws Exception {
    double[] 	classValues;

    switch (m_PredictionType) {
      case ALL:
	classValues = null;
	break;
      default:
	classValues = instances.attributeToDoubleArray(instances.classIndex());
    }

    if (classValues != null)
      params.put(PARAM_CLASSVALUES, classValues);

    if (!isInitialized()) {
      if (m_ReplaceMissing) {
	m_Missing = new ReplaceMissingValues();
	m_Missing.setInputFormat(instances);
      }
      else {
	m_Missing = null;
      }

      switch (m_PreprocessingType) {
	case CENTER:
	  m_ClassMean   = instances.meanOrMode(instances.classIndex());
	  m_ClassStdDev = 1;
	  m_Filter      = new Center();
	  ((Center) m_Filter).setIgnoreClass(true);
	  break;
	case STANDARDIZE:
	  m_ClassMean   = instances.meanOrMode(instances.classIndex());
	  m_ClassStdDev = StrictMath.sqrt(instances.variance(instances.classIndex()));
	  m_Filter      = new Standardize();
	  ((Standardize) m_Filter).setIgnoreClass(true);
	  break;
	case NONE:
	  m_ClassMean   = 0;
	  m_ClassStdDev = 1;
	  m_Filter      = null;
	  break;
	default:
	  throw new IllegalStateException("Unhandled preprocessing type; " + m_PreprocessingType);
      }
      if (m_Filter != null)
	m_Filter.setInputFormat(instances);
    }

    // filter data
    if (m_Missing != null)
      instances = Filter.useFilter(instances, m_Missing);
    if (m_Filter != null)
      instances = Filter.useFilter(instances, m_Filter);

    return instances;
  }

  /**
   * Postprocesses the data.
   *
   * @param instances	the data to process
   * @return		the postprocessed data
   */
  protected Instances postTransform(Instances instances, Map<String,Object> params) throws Exception {
    int		i;
    double[] 	classValues;
    Double 	classValue;

    classValues = (double[]) params.get(PARAM_CLASSVALUES);

    // add the mean to the class again if predictions are to be performed,
    // otherwise restore original class values
    for (i = 0; i < instances.numInstances(); i++) {
      if (classValues != null) {
        instances.instance(i).setClassValue(classValues[i]);
      }
      else {
        classValue = instances.instance(i).classValue();
        instances.instance(i).setClassValue(classValue * m_ClassStdDev + m_ClassMean);
      }
    }

    return instances;
  }
}