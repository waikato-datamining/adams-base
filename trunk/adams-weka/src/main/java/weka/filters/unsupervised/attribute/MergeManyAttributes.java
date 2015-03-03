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
 * MergeManyAttributes.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.SingleIndex;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.WekaException;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;
import adams.core.base.BaseString;

/**
 <!-- globalinfo-start -->
 * Merges two or more attributes, offers various strategies if values differ or not present.<br/>
 * Uses the common subsequence (either from start or end) of the attributes as name of the merged attribute, otherwise the concatenation of them (separated by '-'). If this new name should already be present, then a number is appended to the name to make it unique.<br/>
 * The merged attribute can either be left at the default position (whichever one of the attributes that comes first) or moved to a specific one.<br/>
 * If one of the attributes to be merged is the current class attribute, the newly created merged attribute will become the new class attribute.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -att-name &lt;att name&gt;
 *  The name of the attribute, can be supplied multiple times.</pre>
 * 
 * <pre> -remove-chars &lt;chars&gt;
 *  The characters to remove from the start/end of the
 *  generated name for the merged attribute.
 *  (default:  -_.)</pre>
 * 
 * <pre> -merged-index &lt;position&gt;
 *  The new position for the merged attribute.
 *  Empty string is default position, i.e., either the position
 *  of the first or second attribute (whichever comes first)
 *  (default: )</pre>
 * 
 * <pre> -differ &lt;MISSING|AVERAGE&gt;
 *  The strategy to apply in case the values of the attributes differ.
 *  (default: MISSING)</pre>
 * 
 * <pre> -one-missing &lt;MISSING|PRESENT&gt;
 *  The strategy to apply in case one of the values is missing.
 *  (default: MISSING)</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeManyAttributes
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -8596728919861340618L;

  /** how to handle differing values: missing. */
  public static final int VALUESDIFFER_MISSING = 0;
  /** how to handle differing values: average. */
  public static final int VALUESDIFFER_AVERAGE = 1;
  /** the types of how to handle differing values. */
  public static final Tag[] TAGS_VALUESDIFFER = {
    new Tag(VALUESDIFFER_MISSING, "MISSING", "Set to missing"),
    new Tag(VALUESDIFFER_AVERAGE, "AVERAGE", "Take average (numeric only)"),
  };

  /** what to do if one is missing: missing. */
  public static final int ONEMISSING_MISSING = 0;
  /** what to do if one is missing: use first present value. */
  public static final int ONEMISSING_USE_FIRST_PRESENT = 1;
  public static final Tag[] TAGS_ONEMISSING = {
    new Tag(ONEMISSING_MISSING, "MISSING", "Set to missing"),
    new Tag(ONEMISSING_USE_FIRST_PRESENT, "FIRST", "Use first present value"),
  };

  /** characters to remove from start/end of the merged name. */
  public final static String DEFAULT_REMOVE_CHARS = " -_.";

  /** the attribute names. */
  protected BaseString[] m_AttributeNames = new BaseString[]{
      new BaseString("att1"), 
      new BaseString("att2")
  };
  
  /** how to handle differing values. */
  protected int m_Differ = VALUESDIFFER_MISSING;

  /** what to do if one value is missing. */
  protected int m_OneMissing = ONEMISSING_MISSING;

  /** the characters to remove from the merged name (start/end). */
  protected String m_RemoveChars = DEFAULT_REMOVE_CHARS;
  
  /** the position for the merged attribute (empty = leave at default position). */
  protected SingleIndex m_MergedIndex = new SingleIndex("");
  
  /** the name of the merged attribute. */
  protected String m_Merged;
  
  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
          "Merges two or more attributes, offers various strategies if values differ or not present.\n"
	+ "Uses the common subsequence (either from start or end) of the attributes as name "
	+ "of the merged attribute, otherwise the concatenation of them (separated by '-'). "
	+ "If this new name should already be present, then a number is appended to the name to "
	+ "make it unique.\n"
	+ "The merged attribute can either be left at the default position (whichever one of "
	+ "the attributes that comes first) or moved to a specific one.\n"
	+ "If one of the attributes to be merged is the current class attribute, the newly "
	+ "created merged attribute will become the new class attribute.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector		result;
    Enumeration		enm;
    String		param;
    SelectedTag		tag;
    int			i;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
	"\tThe name of the attribute, can be supplied multiple times.",
	"att-name", 1, "-att-name <att name>"));

    result.addElement(new Option(
	"\tThe characters to remove from the start/end of the\n"
	+ "\tgenerated name for the merged attribute.\n"
	+ "\t(default: " + DEFAULT_REMOVE_CHARS + ")",
	"remove-chars", 1, "-remove-chars <chars>"));

    result.addElement(new Option(
	"\tThe new position for the merged attribute.\n"
	+ "\tEmpty string is default position, i.e., either the position\n"
	+ "\tof the first or second attribute (whichever comes first)\n"
	+ "\t(default: )",
	"merged-index", 1, "-merged-index <position>"));

    param = "";
    for (i = 0; i < TAGS_VALUESDIFFER.length; i++) {
      if (i > 0)
	param += "|";
      tag = new SelectedTag(TAGS_VALUESDIFFER[i].getID(), TAGS_VALUESDIFFER);
      param += tag.getSelectedTag().getIDStr();
    }
    result.addElement(new Option(
	"\tThe strategy to apply in case the values of the attributes differ.\n"
	+ "\t(default: " + new SelectedTag(VALUESDIFFER_MISSING, TAGS_VALUESDIFFER) + ")",
	"differ", 1, "-differ <" + param + ">"));

    param = "";
    for (i = 0; i < TAGS_ONEMISSING.length; i++) {
      if (i > 0)
	param += "|";
      tag = new SelectedTag(TAGS_ONEMISSING[i].getID(), TAGS_ONEMISSING);
      param += tag.getSelectedTag().getIDStr();
    }
    result.addElement(new Option(
	"\tThe strategy to apply in case one of the values is missing.\n"
	+ "\t(default: " + new SelectedTag(ONEMISSING_MISSING, TAGS_ONEMISSING) + ")",
	"one-missing", 1, "-one-missing <" + param + ">"));

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return      the current options
   */
  @Override
  public String[] getOptions() {
    int       i;
    Vector    result;
    String[]  options;

    result = new Vector();
    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    for (i = 0; i < m_AttributeNames.length; i++) {
      result.add("-att-name");
      result.add("" + m_AttributeNames[i].stringValue());
    }

    if (getMergedIndex().length() > 0) {
      result.add("-merged-index");
      result.add("" + getMergedIndex());
    }

    result.add("-remove-chars");
    result.add("" + getRemoveChars());

    result.add("-differ");
    result.add("" + getDiffer().getSelectedTag().getIDStr());

    result.add("-one-missing");
    result.add("" + getOneMissing().getSelectedTag().getIDStr());

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   * 
   * <pre> -att-name &lt;att name&gt;
   *  The name of the attribute, can be supplied multiple times.</pre>
   * 
   * <pre> -remove-chars &lt;chars&gt;
   *  The characters to remove from the start/end of the
   *  generated name for the merged attribute.
   *  (default:  -_.)</pre>
   * 
   * <pre> -merged-index &lt;position&gt;
   *  The new position for the merged attribute.
   *  Empty string is default position, i.e., either the position
   *  of the first or second attribute (whichever comes first)
   *  (default: )</pre>
   * 
   * <pre> -differ &lt;MISSING|AVERAGE&gt;
   *  The strategy to apply in case the values of the attributes differ.
   *  (default: MISSING)</pre>
   * 
   * <pre> -one-missing &lt;MISSING|PRESENT&gt;
   *  The strategy to apply in case one of the values is missing.
   *  (default: MISSING)</pre>
   * 
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String		tmpStr;
    List<BaseString>	list;

    super.setOptions(options);

    list = new ArrayList<BaseString>();
    do {
      tmpStr = Utils.getOption("att-name", options);
      if (tmpStr.length() > 0)
	list.add(new BaseString(tmpStr));
    }
    while (tmpStr.length() > 0);
    while (list.size() < 2)
      list.add(new BaseString("att" + (list.size() + 1)));
    setAttributeNames(list.toArray(new BaseString[list.size()]));

    tmpStr = Utils.getOption("merged-index", options);
    setMergedIndex(tmpStr);

    tmpStr = Utils.getOption("remove-chars", options);
    if (tmpStr.length() != 0)
      setRemoveChars(tmpStr);
    else
      setRemoveChars(DEFAULT_REMOVE_CHARS);

    tmpStr = Utils.getOption("differ", options);
    if (tmpStr.length() != 0)
      setDiffer(new SelectedTag(tmpStr, TAGS_VALUESDIFFER));
    else
      setDiffer(new SelectedTag(VALUESDIFFER_MISSING, TAGS_VALUESDIFFER));

    tmpStr = Utils.getOption("one-missing", options);
    if (tmpStr.length() != 0)
      setOneMissing(new SelectedTag(tmpStr, TAGS_ONEMISSING));
    else
      setOneMissing(new SelectedTag(ONEMISSING_MISSING, TAGS_ONEMISSING));
  }

  /**
   * Sets the names of the attributes.
   *
   * @param value 	the names
   */
  public void setAttributeNames(BaseString[] value) {
    m_AttributeNames = value;
  }

  /**
   * Gets the names of the attributes.
   *
   * @return 		the names
   */
  public BaseString[] getAttributeNames() {
    return m_AttributeNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String attributeNamesTipText() {
    return "The names of the attributes to merge.";
  }

  /**
   * Sets the position for the merged attribute.
   *
   * @param value 	the position, empty string for default
   */
  public void setMergedIndex(String value) {
    m_MergedIndex.setSingleIndex(value);
  }

  /**
   * Gets the position for the merged attribute.
   *
   * @return 		the position, empty string for default
   */
  public String getMergedIndex() {
    return m_MergedIndex.getSingleIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String mergedIndexTipText() {
    return "The new index of the merged attribute, leave empty for default position; 'first' and 'last' are accepted as well.";
  }

  /**
   * Sets the characters to remove from start/end of the generated name.
   *
   * @param value 	the characters
   */
  public void setRemoveChars(String value) {
    m_RemoveChars = value;
  }

  /**
   * Gets the characters to remove from start/end of the generated name.
   *
   * @return 		the characters
   */
  public String getRemoveChars() {
    return m_RemoveChars;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String removeCharsTipText() {
    return "The characters to remove from the start/end of the generated name for the merged attribute.";
  }

  /**
   * Sets the type of strategy to apply if the two values differ.
   *
   * @param value 	the strategy
   */
  public void setDiffer(SelectedTag value) {
    if (value.getTags() == TAGS_VALUESDIFFER) {
      m_Differ = value.getSelectedTag().getID();
    }
  }

  /**
   * Gets the type of strategy to apply if the two values differ.
   *
   * @return 		the strategy
   */
  public SelectedTag getDiffer() {
    return new SelectedTag(m_Differ, TAGS_VALUESDIFFER);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String differTipText() {
    return "The strategy to apply if the values differ.";
  }

  /**
   * Sets the type of strategy to apply if one of the values is missing.
   *
   * @param value 	the strategy
   */
  public void setOneMissing(SelectedTag value) {
    if (value.getTags() == TAGS_ONEMISSING) {
      m_OneMissing = value.getSelectedTag().getID();
    }
  }

  /**
   * Gets the type of strategy to apply if one of the values is missing.
   *
   * @return 		the strategy
   */
  public SelectedTag getOneMissing() {
    return new SelectedTag(m_OneMissing, TAGS_ONEMISSING);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String oneMissingTipText() {
    return "Sets the strategy to apply if one of the values is missing.";
  }

  /**
   * Determines the common subsequence of the two strings.
   * 
   * @param s1		the first string
   * @param s2		the second string
   * @param forward	if false, the strings are search from back to front
   */
  protected String commonSubsequence(String s1, String s2, boolean forward) {
    StringBuilder	result;
    int			i;
    
    result = new StringBuilder();

    for (i = 0; i < Math.min(s1.length(), s2.length()); i++) {
      if (forward) {
	if (s1.charAt(i) == s2.charAt(i))
	  result.append(s1.charAt(i));
	else
	  break;
      }
      else {
	if (s1.charAt(s1.length() - i - 1) == s2.charAt(s2.length() - i - 1))
	  result.insert(0, s1.charAt(s1.length() - i - 1));
	else
	  break;
      }
    }

    // remove unwanted characters
    if (m_RemoveChars.length() > 0) {
      while ((result.length() > 0) && (m_RemoveChars.indexOf(result.charAt(0)) > -1))
	result.delete(0, 1);
      while ((result.length() > 0) && (m_RemoveChars.indexOf(result.charAt(result.length() - 1)) > -1))
	result.delete(result.length() - 1, result.length());
    }
    
    return result.toString();
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
   * @see   #hasImmediateOutputFormat()
   * @see   #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    String			common;
    ArrayList<Attribute>	atts;
    int				i;
    Attribute			oldAtt;
    Attribute			newAtt;
    HashSet<String>		labels;
    ArrayList<String>		values;
    boolean			done;
    Enumeration			enm;
    int				current;
    HashSet<String>		attNames;
    
    if (m_AttributeNames.length < 2)
      throw new IllegalStateException("At least two attribute names must be defined!");
    
    attNames = new HashSet<String>();
    for (i = 0; i < m_AttributeNames.length; i++) {
      if (inputFormat.attribute(m_AttributeNames[i].stringValue()) == null)
	throw new WekaException("Attribute #" + (i+1) + " '" + m_AttributeNames[i] + "' not found!");
      attNames.add(m_AttributeNames[i].stringValue());
    }
    
    // determine name of merged attribute
    m_Merged = m_AttributeNames[0].stringValue();
    for (i = 1; i < m_AttributeNames.length; i++) {
      common = commonSubsequence(m_Merged, m_AttributeNames[i].stringValue(), true);
      if (common.length() == 0)
	common = commonSubsequence(m_Merged, m_AttributeNames[i].stringValue(), false);
      if (common.length() == 0)
	common = m_Merged + "-" + m_AttributeNames[i].stringValue();
      m_Merged = common;
    }
    i      = 1;
    common = m_Merged;
    while (inputFormat.attribute(m_Merged) != null) {
      i++;
      m_Merged = common + "-" + i;
    }

    // collect nominal labels (if any)
    labels = new HashSet<String>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      oldAtt = inputFormat.attribute(i);
      if (attNames.contains(oldAtt.name())) {
	switch (oldAtt.type()) {
	  case Attribute.NOMINAL:
	    enm = oldAtt.enumerateValues();
	    while (enm.hasMoreElements())
	      labels.add(enm.nextElement().toString());
	    break;
	}
      }
    }
    values = new ArrayList<String>(labels);
    Collections.sort(values);
    
    // create new attributes
    atts    = new ArrayList<Attribute>();
    done    = false;
    current = -1;
    newAtt  = null;
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      oldAtt = inputFormat.attribute(i);
      if (attNames.contains(oldAtt.name())) {
	if (!done) {
	  switch (oldAtt.type()) {
	    case Attribute.NUMERIC:
	      newAtt = new Attribute(m_Merged);
	      break;
	    case Attribute.NOMINAL:
	      newAtt = new Attribute(m_Merged, values);
	      break;
	    case Attribute.STRING:
	      newAtt = new Attribute(m_Merged, (List<String>) null);
	      break;
	    default:
	      throw new IllegalStateException("Cannot merged attributes of type " + Attribute.typeToString(oldAtt));
	  }
	  done    = true;
	  current = atts.size();
	  atts.add(newAtt);
	}
      }
      else {
	newAtt = (Attribute) oldAtt.copy();
	atts.add(newAtt);
      }
    }
    
    // move merged attribute?
    if ((m_MergedIndex.getSingleIndex().length() > 0) && (current > -1)) {
      m_MergedIndex.setUpper(atts.size() - 1);
      newAtt = atts.remove(current);
      atts.add(m_MergedIndex.getIndex(), newAtt);
    }
    
    result = new Instances(inputFormat.relationName(), atts, 0);
    if (inputFormat.classIndex() > -1) {
      if (result.attribute(inputFormat.classAttribute().name()) != null)
	result.setClass(result.attribute(inputFormat.classAttribute().name()));
      else
	result.setClass(result.attribute(m_Merged));
    }
    
    return result;
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attribute
    result.enable(Capability.STRING_ATTRIBUTES);
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   * @see               #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances		result;
    int			indexMerged;
    int			i;
    int			n;
    int			indexOld;
    int			indexNew;
    double[]		valuesOld;
    double[]		valuesNew;
    Instance		instOld;
    Instance		instNew;
    boolean		isNumeric;
    boolean		isString;
    HashSet<Integer>	indicesOld;
    boolean		oneMissing;
    boolean		allMissing;
    boolean		different;
    int			firstIndex;
    
    result = getOutputFormat();
    
    // collect old indices
    indicesOld = new HashSet<Integer>();
    for (i = 0; i < m_AttributeNames.length; i++)
      indicesOld.add(instances.attribute(m_AttributeNames[i].stringValue()).index());
    
    indexMerged = result.attribute(m_Merged).index();
    isNumeric   = result.attribute(indexMerged).isNumeric();
    isString    = result.attribute(indexMerged).isString();
    for (i = 0; i < instances.numInstances(); i++) {
      instOld   = instances.instance(i);
      valuesOld = instOld.toDoubleArray();
      valuesNew = new double[result.numAttributes()];
      
      // merge attributes
      oneMissing = false;
      allMissing = true;
      for (Integer index: indicesOld) {
	if (Utils.isMissingValue(valuesOld[index]))
	  oneMissing = true;
	else
	  allMissing = false;
      }
      if (oneMissing) {
	switch (m_OneMissing) {
	  case ONEMISSING_MISSING:
	    valuesNew[indexMerged] = Utils.missingValue();
	    break;
	  case ONEMISSING_USE_FIRST_PRESENT:
	    if (allMissing) {
	      valuesNew[indexMerged] = Utils.missingValue();
	    }
	    else {
	      for (n = 0; n < instances.numAttributes(); n++) {
		if (!indicesOld.contains(n))
		  continue;
		if (Utils.isMissingValue(valuesOld[n]))
		  continue;
		if (isNumeric)
		  valuesNew[indexMerged] = valuesOld[n];
		else if (isString)
		  valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(n));
		else
		  valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(n));
		break;
	      }
	    }
	    break;
	  default:
	    throw new IllegalStateException("Unhandled type (oneMissing): " + m_OneMissing);
	}
      }
      else {
	// are the values different?
	different  = false;
	firstIndex = -1;
	for (Integer index: indicesOld) {
	  if (firstIndex == -1) {
	    firstIndex = index;
	    continue;
	  }
	  else {
	    if (    ( isNumeric && (valuesOld[firstIndex] != valuesOld[index])) 
		|| (!isNumeric && !instOld.stringValue(firstIndex).equals(instOld.stringValue(index))) ) {
	      different = true;
	      break;
	    }
	  }
	}
	    
	if (different) {
	  switch (m_Differ) {
	    case VALUESDIFFER_MISSING:
	      valuesNew[indexMerged] = Utils.missingValue();
	      break;
	    case VALUESDIFFER_AVERAGE:
	      if (isNumeric) {
		valuesNew[indexMerged] = 0;
		for (Integer index: indicesOld)
		  valuesNew[indexMerged] += valuesOld[index];
		if (indicesOld.size() > 0)
		  valuesNew[indexMerged] /= indicesOld.size();
		else
		  valuesNew[indexMerged] = Utils.missingValue();
	      }
	      else {
		valuesNew[indexMerged] = Utils.missingValue();
	      }
	      break;
	    default:
	      throw new IllegalStateException("Unhandled type (differ): " + m_Differ);
	  }
	}
	else {
	  if (isNumeric)
	    valuesNew[indexMerged] = valuesOld[firstIndex];
	  else if (isString)
	    valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(firstIndex));
	  else
	    valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(firstIndex));
	}
      }

      // remaining values
      indexOld = 0;
      indexNew = 0;
      while (indexOld < instOld.numAttributes()) {
	if (indicesOld.contains(indexOld)) {
	  indexOld++;
	  continue;
	}
	if (indexNew == indexMerged) {
	  indexNew++;
	  continue;
	}
	if (Utils.isMissingValue(valuesOld[indexOld])) {
	  valuesNew[indexNew] = valuesOld[indexOld];
	}
	else {
	  if (result.attribute(indexNew).isString())
	    valuesNew[indexNew] = result.attribute(indexNew).addStringValue(instOld.stringValue(indexOld));
	  else
	    valuesNew[indexNew] = valuesOld[indexOld];
	}
	indexNew++;
	indexOld++;
      }
      
      // create new instance
      instNew = new DenseInstance(instOld.weight(), valuesNew);
      result.add(instNew);
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

  /**
   * runs the filter with the given arguments.
   *
   * @param args      the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new MergeManyAttributes(), args);
  }
}
