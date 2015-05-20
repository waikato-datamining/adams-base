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
 * DatasetLabeler.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.filters.unsupervised.instance;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import adams.data.weka.rowfinder.AbstractRowFinder;

/**
 <!-- globalinfo-start -->
 * Adds an additional attribute to the dataset containing a label whether it was a match or not, i.e., whether the row finder selected a particular row or not.
 * <br><br>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -W &lt;row finder specification&gt;
 *  Full class name of row finder to use, followed
 *  by scheme options. eg:
 *   "adams.data.weka.rowfinder.NullFinder -D 1"
 *  (default: adams.data.weka.rowfinder.NullFinder)</pre>
 * 
 * <pre> -invert
 *  Whether to invert the found row indices.
 *  (default: off)</pre>
 * 
 * <pre> -name &lt;name&gt;
 *  Name of the label attribute.
 *  (default: 'Label')</pre>
 * 
 * <pre> -match &lt;label&gt;
 *  The label for matching rows.
 *  (default: 'yes')</pre>
 * 
 * <pre> -non-match &lt;label&gt;
 *  The label for non-matching rows.
 *  (default: 'no')</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetLabeler
  extends AbstractRowFinderApplier {

  /** for serialization. */
  private static final long serialVersionUID = -3519667295906912802L;

  /** the default label for a "match". */
  public final static String LABEL_MATCH = "yes";

  /** the default label for a "non-match". */
  public final static String LABEL_NONMATCH = "no";

  /** the default name of the attribute. */
  public final static String DEFAULT_NAME = "Label";
  
  /** the name of the attribute name to add. */
  protected String m_AttributeName = DEFAULT_NAME;

  /** the label to use for a match. */
  protected String m_LabelMatch = LABEL_MATCH;

  /** the label to use for a nonmatch. */
  protected String m_LabelNonMatch = LABEL_NONMATCH;

  /** the filter for adding the label attribute. */
  protected Add m_AddFilter = null;
  
  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
	"Adds an additional attribute to the dataset containing a label "
	+ "whether it was a match or not, i.e., whether the row finder "
	+ "selected a particular row or not.";
  }
  
  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;

    result = new Vector();

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    result.addElement(new Option(
	"\tName of the label attribute.\n"
	+"\t(default: '" + DEFAULT_NAME + "')",
	"name", 1,"-name <name>"));

    result.addElement(new Option(
	"\tThe label for matching rows.\n"
	+"\t(default: '" + LABEL_MATCH + "')",
	"match", 1,"-match <label>"));

    result.addElement(new Option(
	"\tThe label for non-matching rows.\n"
	+"\t(default: '" + LABEL_NONMATCH + "')",
	"non-match", 1,"-non-match <label>"));

    return result.elements();
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    
    tmpStr = Utils.getOption("name", options);
    if (tmpStr.length() == 0)
      setAttributeName(DEFAULT_NAME);
    else
      setAttributeName(tmpStr);
    
    tmpStr = Utils.getOption("match", options);
    if (tmpStr.length() == 0)
      setLabelMatch(LABEL_MATCH);
    else
      setLabelMatch(tmpStr);
    
    tmpStr = Utils.getOption("non-match", options);
    if (tmpStr.length() == 0)
      setLabelMatch(LABEL_NONMATCH);
    else
      setLabelMatch(tmpStr);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-name");
    result.add(getAttributeName());

    result.add("-match");
    result.add(getLabelMatch());

    result.add("-non-match");
    result.add(getLabelNonMatch());
    
    return result.toArray(new String[result.size()]);	  
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String rowFinderTipText() {
    return "The algorithm for locating rows to be labeled as a match (or non-match if inverted).";
  }

  /** 
   * Set the new attribute's name.
   *
   * @param name 	the new name
   */
  public void setAttributeName(String name) {
    m_AttributeName = name;
  }

  /**
   * Get the name of the attribute to be created.
   *
   * @return 		the new attribute name
   */
  public String getAttributeName() {
    return m_AttributeName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String attributeNameTipText() {
    return "Set the label attribute's name.";
  }

  /** 
   * Sets the label for the matching rows.
   *
   * @param name 	the label
   */
  public void setLabelMatch(String value) {
    m_LabelMatch = value;
  }

  /**
   * Returns the label for the matching rows.
   *
   * @return 		the label
   */
  public String getLabelMatch() {
    return m_LabelMatch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String labelMatchTipText() {
    return "The label for rows that were a match, ie located by the row finder.";
  }

  /** 
   * Sets the label for the non-matching rows.
   *
   * @param name 	the label
   */
  public void setLabelNonMatch(String value) {
    m_LabelNonMatch = value;
  }

  /**
   * Returns the label for the non-matching rows.
   *
   * @return 		the label
   */
  public String getLabelNonMatch() {
    return m_LabelNonMatch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String labelNonMatchTipText() {
    return "The label for rows that were not a match, ie not located by the row finder.";
  }

  /**
   * Determines the output format based on the input format and returns 
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    m_AddFilter = new Add();
    m_AddFilter.setAttributeName(m_AttributeName);
    m_AddFilter.setAttributeIndex("last");
    m_AddFilter.setNominalLabels(m_LabelMatch + "," + m_LabelNonMatch);
    m_AddFilter.setAttributeType(new SelectedTag(Attribute.NOMINAL, Add.TAGS_TYPE));
    m_AddFilter.setInputFormat(inputFormat);
    
    return m_AddFilter.getOutputFormat();
  }
  
  /**
   * Method that returns whether the filter may remove instances after
   * the first batch has been done.
   * 
   * @return		always true
   */
  @Override
  protected boolean mayRemoveInstances() {
    return true;
  }

  /**
   * Applies the indices to the data. In case inverting is enabled, the indices
   * have already been inverted.
   * 
   * @param data	the data to process
   * @param indices	the indices to use
   * @return		the processed data
   */
  @Override
  protected Instances apply(Instances data, int[] indices) {
    Instances		result;
    HashSet<Integer>	set;
    int			i;
    int			index;
    Instance		inst;
    
    try {
      result = Filter.useFilter(data, m_AddFilter);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to filter data, adding the label attribute!", e);
    }
    
    index = result.attribute(m_AttributeName).index();
    set   = AbstractRowFinder.arrayToHashSet(indices);
    for (i = 0; i < result.numInstances(); i++) {
      inst = result.instance(i);
      if (set.contains(i))
	inst.setValue(index, m_LabelMatch);
      else
	inst.setValue(index, m_LabelNonMatch);
    }
    
    return result;
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }
}
