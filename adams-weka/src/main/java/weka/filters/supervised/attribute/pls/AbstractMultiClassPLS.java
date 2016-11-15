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
 * AbstractMultiClassPLS.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.filters.supervised.attribute.pls;

import adams.core.base.BaseRegExp;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Option;
import weka.core.WekaOptionUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Center;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Ancestor for schemes that predict multiple classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiClassPLS
  extends AbstractPLS {

  private static final long serialVersionUID = 5649007256147616278L;

  public final static String OPTION_CLASS_ATTRIBUTES = "class-attributes";

  public static final String PARAM_CLASSVALUES = "classValues";

  /** the regular expression for identifying class attributes (besides an explicitly set one). */
  protected BaseRegExp m_ClassAttributes = getDefaultClassAttributes();

  /** for replacing missing values */
  protected Filter m_Missing = null;

  /** for centering the data */
  protected Filter m_Filter = null;

  /** the class attribute indices. */
  protected TIntList m_ClassAttributeIndices;

  /** the class mean. */
  protected Map<Integer,Double> m_ClassMean;

  /** the class stddev. */
  protected Map<Integer,Double> m_ClassStdDev;

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_Missing               = null;
    m_Filter                = null;
    m_ClassAttributeIndices = null;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, classAttributesTipText(), "" + getDefaultClassAttributes(), OPTION_CLASS_ATTRIBUTES);
    WekaOptionUtils.add(result, super.listOptions());

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setClassAttributes(new BaseRegExp(WekaOptionUtils.parse(options, OPTION_CLASS_ATTRIBUTES, getDefaultClassAttributes().getValue())));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, OPTION_CLASS_ATTRIBUTES, getNumComponents());
    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the default regular expression for the class attributes.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultClassAttributes() {
    return new BaseRegExp("");
  }

  /**
   * Sets the regular expression for identifying the class attributes
   * (besides an explicitly set one).
   *
   * @param value 	the regular expression
   */
  public void setClassAttributes(BaseRegExp value) {
    m_ClassAttributes = value;
    reset();
  }

  /**
   * Returns the regular expression for identifying the class attributes
   * (besides an explicitly set one).
   *
   * @return 		the regular expression
   */
  public BaseRegExp getClassAttributes() {
    return m_ClassAttributes;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for displaying in the
   *         		explorer/experimenter gui
   */
  public String classAttributesTipText() {
    return "The regular expression for identifying the class attributes (besides an explicitly set one).";
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
    ArrayList<Attribute> 	atts;
    String 			prefix;
    int 			i;
    Instances 			result;
    List<String>		classes;

    // collect classes
    m_ClassAttributeIndices = new TIntArrayList();
    classes                 = new ArrayList<>();
    for (i = 0; i < input.numAttributes(); i++) {
      if (m_ClassAttributes.isMatch(input.attribute(i).name())) {
	classes.add(input.attribute(i).name());
	m_ClassAttributeIndices.add(i);
      }
    }
    if (!classes.contains(input.classAttribute().name())) {
      classes.add(input.classAttribute().name());
      m_ClassAttributeIndices.add(input.classAttribute().index());
    }

    // generate header
    atts   = new ArrayList<>();
    prefix = getClass().getSimpleName();
    for (i = 0; i < getNumComponents(); i++)
      atts.add(new Attribute(prefix + "_" + (i + 1)));
    for (String cls: classes)
      atts.add(new Attribute(cls));
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
    Map<Integer,double[]> 	classValues;
    int				i;
    int				index;

    switch (m_PredictionType) {
      case ALL:
	classValues = null;
	break;
      default:
	classValues = new HashMap<>();
	for (i = 0; i < m_ClassAttributeIndices.size(); i++) {
	  index = m_ClassAttributeIndices.get(i);
	  classValues.put(index, instances.attributeToDoubleArray(index));
	}
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

      m_ClassMean   = new HashMap<>();
      m_ClassStdDev = new HashMap<>();
      for (i = 0; i < m_ClassAttributeIndices.size(); i++) {
	index = m_ClassAttributeIndices.get(i);
	switch (m_PreprocessingType) {
	  case CENTER:
	    m_ClassMean.put(index, instances.meanOrMode(index));
	    m_ClassStdDev.put(index, 1.0);
	    m_Filter = new Center();
	    ((Center) m_Filter).setIgnoreClass(true);
	    break;
	  case STANDARDIZE:
	    m_ClassMean.put(index, instances.meanOrMode(index));
	    m_ClassStdDev.put(index, StrictMath.sqrt(instances.variance(index)));
	    m_Filter = new Standardize();
	    ((Standardize) m_Filter).setIgnoreClass(true);
	    break;
	  case NONE:
	    m_ClassMean.put(index, 0.0);
	    m_ClassStdDev.put(index, 1.0);
	    m_Filter = null;
	    break;
	  default:
	    throw new IllegalStateException("Unhandled preprocessing type; " + m_PreprocessingType);
	}
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
    int				i;
    int 			n;
    Map<Integer,double[]> 	classValues;
    double 			classValue;
    int				index;

    classValues = (Map<Integer,double[]>) params.get(PARAM_CLASSVALUES);

    // add the mean to the class again if predictions are to be performed,
    // otherwise restore original class values
    for (i = 0; i < m_ClassAttributeIndices.size(); i++) {
      index = m_ClassAttributeIndices.get(i);
      for (n = 0; n < instances.numInstances(); n++) {
	if (classValues != null) {
	  instances.instance(n).setClassValue(classValues.get(index)[n]);
	}
	else {
	  classValue = instances.instance(n).classValue();
	  instances.instance(n).setClassValue(classValue * m_ClassStdDev.get(index) + m_ClassMean.get(index));
	}
      }
    }

    return instances;
  }
}