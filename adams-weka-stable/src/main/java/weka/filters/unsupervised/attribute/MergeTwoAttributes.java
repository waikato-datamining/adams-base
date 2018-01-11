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
 * MergeTwoAttributes.java
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

/**
 <!-- globalinfo-start -->
 * Merges two attributes, offers various strategies if values differ or not present.<br>
 * Uses the common subsequence (either from start or end) of the two attributes as name of the merged attribute, otherwise the concatenation of the both (separated by '-'). If this new name should already be present, then a number is appended to the name to make it unique.<br>
 * The merged attribute can either be left at the default position (either first or second attribute, whichever comes first) or moved to a specific one.<br>
 * If one of the two attributes to be merged is the current class attribute, the newly created merged attribute will become the new class attribute.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -first &lt;att name&gt;
 *  The name of the first attribute.
 *  (default: att1)</pre>
 * 
 * <pre> -second &lt;att name&gt;
 *  The name of the second attribute.
 *  (default: att2)</pre>
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
 * <pre> -differ &lt;MISSING|AVERAGE|FIRST|SECOND&gt;
 *  The strategy to apply in case the values of the two attributes differ.
 *  (default: MISSING)</pre>
 * 
 * <pre> -one-missing &lt;MISSING|PRESENT&gt;
 *  The strategy to apply in case one of the two values is missing.
 *  (default: MISSING)</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeTwoAttributes
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -8596728919861340618L;

  /** how to handle differing values: missing. */
  public static final int VALUESDIFFER_MISSING = 0;
  /** how to handle differing values: average. */
  public static final int VALUESDIFFER_AVERAGE = 1;
  /** how to handle differing values: first. */
  public static final int VALUESDIFFER_FIRST = 2;
  /** how to handle differing values: SECOND. */
  public static final int VALUESDIFFER_SECOND = 3;
  /** the types of how to handle differing values. */
  public static final Tag[] TAGS_VALUESDIFFER = {
    new Tag(VALUESDIFFER_MISSING, "MISSING", "Set to missing"),
    new Tag(VALUESDIFFER_AVERAGE, "AVERAGE", "Take average (numeric only)"),
    new Tag(VALUESDIFFER_FIRST, "FIRST", "Use value from 1st attribute"),
    new Tag(VALUESDIFFER_SECOND, "SECOND", "Use value from 2nd attribute"),
  };

  /** what to do if one is missing: missing. */
  public static final int ONEMISSING_MISSING = 0;
  /** what to do if one is missing: use present value. */
  public static final int ONEMISSING_USE_PRESENT = 1;
  public static final Tag[] TAGS_ONEMISSING = {
    new Tag(ONEMISSING_MISSING, "MISSING", "Set to missing"),
    new Tag(ONEMISSING_USE_PRESENT, "PRESENT", "Use present value"),
  };

  /** characters to remove from start/end of the merged name. */
  public final static String DEFAULT_REMOVE_CHARS = " -_.";
  
  /** the default first attribute name. */
  public final static String DEFAULT_NAME_FIRST = "att1";
  
  /** the default second attribute name. */
  public final static String DEFAULT_NAME_SECOND = "att2";
  
  /** the name of the first attribute. */
  protected String m_FirstAttribute = DEFAULT_NAME_FIRST;

  /** the name of the second attribute. */
  protected String m_SecondAttribute = DEFAULT_NAME_SECOND;

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
          "Merges two attributes, offers various strategies if values differ or not present.\n"
	+ "Uses the common subsequence (either from start or end) of the two attributes as name "
	+ "of the merged attribute, otherwise the concatenation of the both (separated by '-'). "
	+ "If this new name should already be present, then a number is appended to the name to "
	+ "make it unique.\n"
	+ "The merged attribute can either be left at the default position (either first or "
	+ "second attribute, whichever comes first) or moved to a specific one.\n"
	+ "If one of the two attributes to be merged is the current class attribute, the newly "
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
	"\tThe name of the first attribute.\n"
	+ "\t(default: " + DEFAULT_NAME_FIRST + ")",
	"first", 1, "-first <att name>"));

    result.addElement(new Option(
	"\tThe name of the second attribute.\n"
	+ "\t(default: " + DEFAULT_NAME_SECOND + ")",
	"second", 1, "-second <att name>"));

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
	"\tThe strategy to apply in case the values of the two attributes differ.\n"
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
	"\tThe strategy to apply in case one of the two values is missing.\n"
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

    result.add("-first");
    result.add("" + getFirstAttribute());

    result.add("-second");
    result.add("" + getSecondAttribute());

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
   * Parses the options for this object. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   * 
   * <pre> -first &lt;att name&gt;
   *  The name of the first attribute.
   *  (default: att1)</pre>
   * 
   * <pre> -second &lt;att name&gt;
   *  The name of the second attribute.
   *  (default: att2)</pre>
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
   * <pre> -differ &lt;MISSING|AVERAGE|FIRST|SECOND&gt;
   *  The strategy to apply in case the values of the two attributes differ.
   *  (default: MISSING)</pre>
   * 
   * <pre> -one-missing &lt;MISSING|PRESENT&gt;
   *  The strategy to apply in case one of the two values is missing.
   *  (default: MISSING)</pre>
   * 
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    super.setOptions(options);

    tmpStr = Utils.getOption("first", options);
    if (tmpStr.length() != 0)
      setFirstAttribute(tmpStr);
    else
      setFirstAttribute(DEFAULT_NAME_FIRST);

    tmpStr = Utils.getOption("second", options);
    if (tmpStr.length() != 0)
      setSecondAttribute(tmpStr);
    else
      setSecondAttribute(DEFAULT_NAME_SECOND);

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
   * Sets the name of the first attribute.
   *
   * @param value 	the name
   */
  public void setFirstAttribute(String value) {
    m_FirstAttribute = value;
  }

  /**
   * Gets the name of the first attribute.
   *
   * @return 		the name
   */
  public String getFirstAttribute() {
    return m_FirstAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String firstAttributeTipText() {
    return "The name of the first attribute to merge.";
  }

  /**
   * Sets the name of the second attribute.
   *
   * @param value 	the name
   */
  public void setSecondAttribute(String value) {
    m_SecondAttribute = value;
  }

  /**
   * Gets the name of the second attribute.
   *
   * @return 		the name
   */
  public String getSecondAttribute() {
    return m_SecondAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String secondAttributeTipText() {
    return "The name of the second attribute to merge.";
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
    return "The strategy to apply if the two values differ.";
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
    Attribute			oldAtt1;
    Attribute			oldAtt2;
    HashSet<String>		labels;
    ArrayList<String>		values;
    boolean			done;
    Enumeration			enm;
    int				current;
    
    if (inputFormat.attribute(m_FirstAttribute) == null)
      throw new WekaException("First attribute '" + m_FirstAttribute + "' not found!");
    
    if (inputFormat.attribute(m_SecondAttribute) == null)
      throw new WekaException("Second attribute '" + m_SecondAttribute + "' not found!");
    
    // determine name of merged attribute
    common = commonSubsequence(m_FirstAttribute, m_SecondAttribute, true);
    if (common.length() == 0)
      common = commonSubsequence(m_FirstAttribute, m_SecondAttribute, false);
    if (common.length() == 0)
      common = m_FirstAttribute + "-" + m_SecondAttribute;
    m_Merged = common;
    i = 1;
    while (inputFormat.attribute(m_Merged) != null) {
      i++;
      m_Merged = common + "-" + i;
    }
    
    atts    = new ArrayList<Attribute>();
    oldAtt1 = inputFormat.attribute(m_FirstAttribute);
    oldAtt2 = inputFormat.attribute(m_SecondAttribute);
    done    = false;
    current = -1;
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      oldAtt = inputFormat.attribute(i);
      if (oldAtt.name().equals(m_FirstAttribute) || oldAtt.name().equals(m_SecondAttribute)) {
	if (!done) {
	  switch (oldAtt.type()) {
	    case Attribute.NUMERIC:
	      newAtt = new Attribute(m_Merged);
	      done   = true;
	      break;
	    case Attribute.NOMINAL:
	      labels = new HashSet<String>();
	      enm = oldAtt1.enumerateValues();
	      while (enm.hasMoreElements())
		labels.add(enm.nextElement().toString());
	      enm = oldAtt2.enumerateValues();
	      while (enm.hasMoreElements())
		labels.add(enm.nextElement().toString());
	      values = new ArrayList<String>(labels);
	      Collections.sort(values);
	      newAtt = new Attribute(m_Merged, values);
	      done   = true;
	      break;
	    case Attribute.STRING:
	      newAtt = new Attribute(m_Merged, (List<String>) null);
	      done   = true;
	      break;
	    default:
	      throw new IllegalStateException("Cannot merged attributes of type " + Attribute.typeToString(oldAtt));
	  }
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
    Instances	result;
    int		indexAtt1;
    int		indexAtt2;
    int		indexMerged;
    int		i;
    int		indexOld;
    int		indexNew;
    double[]	valuesOld;
    double[]	valuesNew;
    Instance	instOld;
    Instance	instNew;
    boolean	isNumeric;
    boolean	isString;
    
    result      = getOutputFormat();
    indexAtt1   = instances.attribute(m_FirstAttribute).index();
    indexAtt2   = instances.attribute(m_SecondAttribute).index();
    indexMerged = result.attribute(m_Merged).index();
    isNumeric   = result.attribute(indexMerged).isNumeric();
    isString    = result.attribute(indexMerged).isString();
    
    for (i = 0; i < instances.numInstances(); i++) {
      instOld   = instances.instance(i);
      valuesOld = instOld.toDoubleArray();
      valuesNew = new double[result.numAttributes()];
      
      // merge attributes
      if (Utils.isMissingValue(valuesOld[indexAtt1]) || Utils.isMissingValue(valuesOld[indexAtt2])) {
	switch (m_OneMissing) {
	  case ONEMISSING_MISSING:
	    valuesNew[indexMerged] = Utils.missingValue();
	    break;
	  case ONEMISSING_USE_PRESENT:
	    if (Utils.isMissingValue(valuesOld[indexAtt1]) && Utils.isMissingValue(valuesOld[indexAtt2])) {
	      valuesNew[indexMerged] = Utils.missingValue();
	    }
	    else if (Utils.isMissingValue(valuesOld[indexAtt1])) {
	      if (isNumeric)
		valuesNew[indexMerged] = valuesOld[indexAtt2];
	      else if (isString)
		valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(indexAtt2));
	      else
		valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(indexAtt2));
	    }
	    else {
	      if (isNumeric)
		valuesNew[indexMerged] = valuesOld[indexAtt1];
	      else if (isString)
		valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(indexAtt1));
	      else
		valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(indexAtt1));
	    }
	    break;
	  default:
	    throw new IllegalStateException("Unhandled type (oneMissing): " + m_OneMissing);
	}
      }
      else {
	if (    ( isNumeric && (valuesOld[indexAtt1] != valuesOld[indexAtt2])) 
	     || (!isNumeric && !instOld.stringValue(indexAtt1).equals(instOld.stringValue(indexAtt2))) ) {
	  switch (m_Differ) {
	    case VALUESDIFFER_MISSING:
	      valuesNew[indexMerged] = Utils.missingValue();
	      break;
	    case VALUESDIFFER_AVERAGE:
	      if (isNumeric)
		valuesNew[indexMerged] = (valuesOld[indexAtt1] + valuesOld[indexAtt2]) / 2.0;
	      else
		valuesNew[indexMerged] = Utils.missingValue();
	      break;
	    case VALUESDIFFER_FIRST:
	      if (isNumeric)
		valuesNew[indexMerged] = valuesOld[indexAtt1];
	      else if (isString)
		valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(indexAtt1));
	      else
		valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(indexAtt1));
	      break;
	    case VALUESDIFFER_SECOND:
	      if (isNumeric)
		valuesNew[indexMerged] = valuesOld[indexAtt2];
	      else if (isString)
		valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(indexAtt2));
	      else
		valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(indexAtt2));
	      break;
	    default:
	      throw new IllegalStateException("Unhandled type (differ): " + m_Differ);
	  }
	}
	else {
	  if (isNumeric)
	    valuesNew[indexMerged] = valuesOld[indexAtt1];
	  else if (isString)
	    valuesNew[indexMerged] = result.attribute(indexMerged).addStringValue(instOld.stringValue(indexAtt1));
	  else
	    valuesNew[indexMerged] = result.attribute(indexMerged).indexOfValue(instOld.stringValue(indexAtt1));
	}
      }

      // remaining values
      indexOld = 0;
      indexNew = 0;
      while (indexOld < instOld.numAttributes()) {
	if ((indexOld == indexAtt1) || (indexOld == indexAtt2)) {
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
    runFilter(new MergeTwoAttributes(), args);
  }
}
