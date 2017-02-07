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
 * WekaInstancesMerge.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Merges multiple datasets, either from file or using Instances&#47;Instance objects.<br>
 * If no 'ID' attribute is named, then all datasets must contain the same number of rows.<br>
 * Attributes can be excluded from ending up in the final dataset via a regular expression. They can also be prefixed with name and&#47;or index.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance[]<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaInstancesMerge
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-use-prefix &lt;boolean&gt; (property: usePrefix)
 * &nbsp;&nbsp;&nbsp;Whether to prefix the attribute names of each dataset with an index and 
 * &nbsp;&nbsp;&nbsp;an optional string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-add-index &lt;boolean&gt; (property: addIndex)
 * &nbsp;&nbsp;&nbsp;Whether to add the index of the dataset to the prefix.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-remove &lt;boolean&gt; (property: remove)
 * &nbsp;&nbsp;&nbsp;If true, only keep instances where data is available from each source.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The optional prefix string to prefix the index number with (in case prefixes 
 * &nbsp;&nbsp;&nbsp;are used); '&#64;' is a placeholder for the relation name.
 * &nbsp;&nbsp;&nbsp;default: dataset
 * </pre>
 * 
 * <pre>-prefix-separator &lt;java.lang.String&gt; (property: prefixSeparator)
 * &nbsp;&nbsp;&nbsp;The separator string between the generated prefix and the original attribute 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: -
 * </pre>
 * 
 * <pre>-exclude-atts &lt;java.lang.String&gt; (property: excludedAttributes)
 * &nbsp;&nbsp;&nbsp;The regular expression used on the attribute names, to determine whether 
 * &nbsp;&nbsp;&nbsp;an attribute should be excluded or not (matching sense can be inverted); 
 * &nbsp;&nbsp;&nbsp;leave empty to include all attributes.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-invert &lt;boolean&gt; (property: invertMatchingSense)
 * &nbsp;&nbsp;&nbsp;Whether to invert the matching sense of excluding attributes, ie, the regular 
 * &nbsp;&nbsp;&nbsp;expression is used for including attributes.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-unique-id &lt;java.lang.String&gt; (property: uniqueID)
 * &nbsp;&nbsp;&nbsp;The name of the attribute (string&#47;numeric) used for uniquely identifying 
 * &nbsp;&nbsp;&nbsp;rows among the datasets.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-keep-only-single-unique-id &lt;boolean&gt; (property: keepOnlySingleUniqueID)
 * &nbsp;&nbsp;&nbsp;If enabled, only a single instance of the unique ID attribute is kept.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesMerge
extends AbstractTransformer
implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -2923715594018710295L;

  /** whether to prefix the attribute names of each dataset with an index. */
  protected boolean m_UsePrefix;

  /** whether to add the index to the prefix. */
  protected boolean m_AddIndex;

  /** whether to remove when not all present. */
  protected boolean m_Remove;

  /** the additional prefix name to use, apart from the index. */
  protected String m_Prefix;

  /** the separator between index and actual attribute name. */
  protected String m_PrefixSeparator;

  /** regular expression for excluding attributes from the datasets. */
  protected String m_ExcludedAttributes;

  /** whether to invert the matching sense for excluding attributes. */
  protected boolean m_InvertMatchingSense;

  /** the string or numeric attribute to use as unique identifier for rows. */
  protected String m_UniqueID;

  /** whether to keep only a single instance of the unique ID attribute. */
  protected boolean m_KeepOnlySingleUniqueID;

  /** the attribute type of the ID attribute. */
  protected int m_AttType;

  /** the unique ID attributes. */
  protected List<String> m_UniqueIDAtts;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Merges multiple datasets, either from file or using Instances/Instance objects.\n"
	+ "If no 'ID' attribute is named, then all datasets must contain the same number of rows.\n"
	+ "Attributes can be excluded from ending up in the final dataset via "
	+ "a regular expression. They can also be prefixed with name and/or index.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"use-prefix", "usePrefix",
	false);

    m_OptionManager.add(
	"add-index", "addIndex",
	false);

    m_OptionManager.add(
	"remove", "remove",
	false);

    m_OptionManager.add(
	"prefix", "prefix",
	"dataset");

    m_OptionManager.add(
	"prefix-separator", "prefixSeparator",
	"-");

    m_OptionManager.add(
	"exclude-atts", "excludedAttributes",
	"");

    m_OptionManager.add(
	"invert", "invertMatchingSense",
	false);

    m_OptionManager.add(
	"unique-id", "uniqueID",
	"");

    m_OptionManager.add(
	"keep-only-single-unique-id", "keepOnlySingleUniqueID",
	false);
  }

  /**
   * Sets whether to remove if not all present
   *
   * @param value	if true then remove instance if not all there to merge
   */
  public void setRemove(boolean value) {
    m_Remove = value;
    reset();
  }

  /**
   * Returns whether to remove if not all present
   *
   * @return		if true then remove instance if not all there to merge
   */
  public boolean getRemove() {
    return m_Remove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeTipText() {
    return
	"If true, only keep instances where data is available from each source.";
  }

  /**
   * Sets whether to use prefixes.
   *
   * @param value	if true then the attributes will get prefixed
   */
  public void setUsePrefix(boolean value) {
    m_UsePrefix = value;
    reset();
  }

  /**
   * Returns whether to use prefixes.
   *
   * @return		true if the attributes will get prefixed
   */
  public boolean getUsePrefix() {
    return m_UsePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePrefixTipText() {
    return
	"Whether to prefix the attribute names of each dataset with an index "
	+ "and an optional string.";
  }

  /**
   * Sets whether to add the dataset index number to the prefix.
   *
   * @param value	if true then the index will be used in the prefix
   */
  public void setAddIndex(boolean value) {
    m_AddIndex = value;
    reset();
  }

  /**
   * Returns whether to add the dataset index number to the prefix.
   *
   * @return		true if the index will be used in the prefix
   */
  public boolean getAddIndex() {
    return m_AddIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addIndexTipText() {
    return "Whether to add the index of the dataset to the prefix.";
  }

  /**
   * Sets the optional prefix string.
   *
   * @param value	the optional prefix string
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the optional prefix string.
   *
   * @return		the optional prefix string
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return
	"The optional prefix string to prefix the index number with (in "
	+ "case prefixes are used); '@' is a placeholder for the relation name.";
  }

  /**
   * Sets the prefix separator string.
   *
   * @param value	the prefix separator string
   */
  public void setPrefixSeparator(String value) {
    m_PrefixSeparator = value;
    reset();
  }

  /**
   * Returns the prefix separator string.
   *
   * @return		the prefix separator string
   */
  public String getPrefixSeparator() {
    return m_PrefixSeparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixSeparatorTipText() {
    return
	"The separator string between the generated prefix and the original "
	+ "attribute name.";
  }

  /**
   * Sets the regular expression for excluding attributes.
   *
   * @param value	the regular expression
   */
  public void setExcludedAttributes(String value) {
    m_ExcludedAttributes = value;
    reset();
  }

  /**
   * Returns the prefix separator string.
   *
   * @return		the prefix separator string
   */
  public String getExcludedAttributes() {
    return m_ExcludedAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String excludedAttributesTipText() {
    return
	"The regular expression used on the attribute names, to determine whether "
	+ "an attribute should be excluded or not (matching sense can be inverted); "
	+ "leave empty to include all attributes.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	if true then matching sense gets inverted
   */
  public void setInvertMatchingSense(boolean value) {
    m_InvertMatchingSense = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if the attributes will get prefixed
   */
  public boolean getInvertMatchingSense() {
    return m_InvertMatchingSense;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertMatchingSenseTipText() {
    return
	"Whether to invert the matching sense of excluding attributes, ie, "
	+ "the regular expression is used for including attributes.";
  }

  /**
   * Sets the attribute (string/numeric) to use for uniquely identifying rows.
   *
   * @param value	the attribute name
   */
  public void setUniqueID(String value) {
    m_UniqueID = value;
    reset();
  }

  /**
   * Returns the attribute (string/numeric) to use for uniquely identifying rows.
   *
   * @return		the attribute name
   */
  public String getUniqueID() {
    return m_UniqueID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String uniqueIDTipText() {
    return
	"The name of the attribute (string/numeric) used for uniquely "
	+ "identifying rows among the datasets.";
  }

  /**
   * Sets whether to keep only a single instance of the unique ID attribute.
   *
   * @param value	true if to keep only single instance
   */
  public void setKeepOnlySingleUniqueID(boolean value) {
    m_KeepOnlySingleUniqueID = value;
    reset();
  }

  /**
   * Returns whether to keep only a single instance of the unique ID attribute.
   *
   * @return		true if to keep only single instance
   */
  public boolean getKeepOnlySingleUniqueID() {
    return m_KeepOnlySingleUniqueID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepOnlySingleUniqueIDTipText() {
    return
	"If enabled, only a single instance of the unique ID attribute is kept.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    String		value;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
    if (result == null)
      result = "";
    value = QuickInfoHelper.toString(this, "prefixSeparator", m_PrefixSeparator, ", separator: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "excludedAttributes", m_ExcludedAttributes, ", excluded: ");
    if (value != null)
      result += value;
    value = QuickInfoHelper.toString(this, "uniqueID", m_UniqueID, ", unique: ");
    if (value != null)
      result += value;
    if (result.startsWith(", "))
      result = result.substring(2);

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "addIndex", m_AddIndex, "index"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "usePrefix", m_UsePrefix, "prefix"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "invertMatchingSense", m_InvertMatchingSense, "invert"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "remove", m_Remove, "remove"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "keepOnlySingleUniqueID", m_KeepOnlySingleUniqueID, "single unique ID"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String[].class, java.io.File[].class, weka.core.Instance[].class, weka.core.Instances[].class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class, Instance[].class, Instances[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Excludes attributes from the data.
   *
   * @param inst	the data to process
   * @return		the processed data
   */
  protected Instances excludeAttributes(Instances inst) {
    Instances		result;
    StringBuilder	atts;
    int			i;
    Remove		filter;

    // determine attribute indices
    atts = new StringBuilder();
    for (i = 0; i < inst.numAttributes(); i++) {
      if (inst.attribute(i).name().matches(m_ExcludedAttributes)) {
	if (atts.length() > 0)
	  atts.append(",");
	atts.append((i+1));
      }
    }

    // filter data
    try {
      filter = new Remove();
      filter.setAttributeIndices(atts.toString());
      filter.setInvertSelection(m_InvertMatchingSense);
      filter.setInputFormat(inst);
      result = weka.filters.Filter.useFilter(inst, filter);
    }
    catch (Exception e) {
      result = inst;
      handleException("Error filtering data:", e);
    }

    return result;
  }

  /**
   * Generates the prefix for the dataset/index.
   *
   * @param inst	the current dataset
   * @param index	the index
   * @return		the prefix
   */
  protected String createPrefix(Instances inst, int index) {
    String 	result;

    // generate prefix
    if (m_Prefix.equals("@"))
      result = inst.relationName();
    else
      result = m_Prefix;
    if (m_AddIndex)
      result += ((result.isEmpty() || result.endsWith(m_PrefixSeparator)) ? "" : m_PrefixSeparator) + (index + 1);
    result += m_PrefixSeparator;

    return result;
  }

  /**
   * Prefixes the attributes.
   *
   * @param index	the index of the dataset
   * @param inst	the data to process
   * @return		the processed data
   */
  protected Instances prefixAttributes(Instances inst, int index) {
    Instances			result;
    String			prefix;
    ArrayList<Attribute>	atts;
    int				i;

    prefix = createPrefix(inst, index);

    // header
    atts = new ArrayList<>();
    for (i = 0; i < inst.numAttributes(); i++)
      atts.add(inst.attribute(i).copy(prefix + inst.attribute(i).name()));

    // data
    result = new Instances(inst.relationName(), atts, inst.numInstances());
    result.setClassIndex(inst.classIndex());
    for (i = 0; i < inst.numInstances(); i++)
      result.add((Instance) inst.instance(i).copy());

    return result;
  }

  /**
   * Prepares the data, prefixing attributes, removing columns, etc, before
   * merging it.
   *
   * @param inst	the data to process
   * @param index	the 0-based index of the dataset being processed
   * @return		the prepared data
   */
  protected Instances prepareData(Instances inst, int index) {
    Instances	result;

    result = inst;

    if (m_KeepOnlySingleUniqueID && !m_UniqueID.isEmpty() && (inst.attribute(m_UniqueID) != null)) {
      if (index > 0)
	m_UniqueIDAtts.add(createPrefix(inst, index) + m_UniqueID);
    }

    // exclude attributes
    if (m_ExcludedAttributes.length() > 0)
      result = excludeAttributes(result);

    // prefix
    if (m_UsePrefix)
      result = prefixAttributes(inst, index);

    return result;
  }

  /**
   * Updates the IDs in the hashset with the ones stored in the ID attribute
   * of the provided dataset.
   *
   * @param instIndex 	the dataset index
   * @param inst	the dataset to obtain the IDs from
   * @param ids		the hashset to store the IDs in
   */
  protected void updateIDs(int instIndex, Instances inst, HashSet ids) {
    Attribute	att;
    int		i;
    boolean 	numeric;
    HashSet 	current;
    Object	id;

    att = inst.attribute(m_UniqueID);
    if (att == null)
      throw new IllegalStateException(
	  "Attribute '" + m_UniqueID + "' not found in relation '" + inst.relationName() + "' (#" + (instIndex+1) + ")!");

    // determine/check type
    if (m_AttType == -1) {
      if ((att.type() == Attribute.NUMERIC) || (att.type() == Attribute.STRING))
	m_AttType = att.type();
      else
	throw new IllegalStateException(
	    "Attribute '" + m_UniqueID + "' must be either NUMERIC or STRING (#" + (instIndex+1) + ")!");
    }
    else {
      if (m_AttType != att.type())
	throw new IllegalStateException(
	    "Attribute '" + m_UniqueID + "' must have same attribute type in all the datasets (#" + (instIndex+1) + ")!");
    }

    // get IDs
    numeric = m_AttType == Attribute.NUMERIC;
    current = new HashSet();
    for (i = 0; i < inst.numInstances(); i++) {
      if (numeric)
	id = inst.instance(i).value(att);
      else
	id = inst.instance(i).stringValue(att);
      if (current.contains(id))
	throw new IllegalStateException("ID '" + id + "' is not unique in dataset #" + (instIndex+1) + "!");
      current.add(id);
    }
    ids.addAll(current);
  }

  /**
   * Merges the datasets based on the collected IDs.
   *
   * @param orig	the original datasets
   * @param inst	the processed datasets to merge into one
   * @param ids		the IDs for identifying the rows
   * @return		the merged dataset
   */
  protected Instances merge(Instances[] orig, Instances[] inst, HashSet ids) {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				n;
    int				m;
    int				index;
    String			relation;
    List			sortedIDs;
    Attribute			att;
    int[]			indexStart;
    double			value;
    double[]			values;
    HashMap<Integer,Integer> 	hashmap;
    HashSet<Instance> 		hs;

    // create header
    if (isLoggingEnabled())
      getLogger().info("Creating merged header...");
    atts       = new ArrayList<>();
    relation   = "";
    indexStart = new int[inst.length];
    for (i = 0; i < inst.length; i++) {
      indexStart[i] = atts.size();
      for (n = 0; n < inst[i].numAttributes(); n++)
	atts.add((Attribute) inst[i].attribute(n).copy());
      // assemble relation name
      if (i > 0)
	relation += "_";
      relation += inst[i].relationName();
    }
    result = new Instances(relation, atts, ids.size());

    // fill with missing values
    if (isLoggingEnabled())
      getLogger().info("Filling with missing values...");
    for (i = 0; i < ids.size(); i++) {
      if (isStopped())
	return null;
      // progress
      if (isLoggingEnabled() && ((i+1) % 1000 == 0))
	getLogger().info("" + (i+1));
      result.add(new DenseInstance(result.numAttributes()));
    }

    // sort IDs
    if (isLoggingEnabled())
      getLogger().info("Sorting indices...");
    sortedIDs = new ArrayList(ids);
    Collections.sort(sortedIDs);

    // generate rows
    hashmap = new HashMap<>();
    for (i = 0; i < inst.length; i++) {
      if (isStopped())
	return null;
      if (isLoggingEnabled())
	getLogger().info("Adding file #" + (i+1));
      att = orig[i].attribute(m_UniqueID);
      for (n = 0; n < inst[i].numInstances(); n++) {
	// progress
	if (isLoggingEnabled() && ((n+1) % 1000 == 0))
	  getLogger().info("" + (n+1));

	// determine index of row
	if (m_AttType == Attribute.NUMERIC)
	  index = Collections.binarySearch(sortedIDs, inst[i].instance(n).value(att));
	else
	  index = Collections.binarySearch(sortedIDs, inst[i].instance(n).stringValue(att));
	if (index < 0)
	  throw new IllegalStateException(
	      "Failed to determine index for row #" + (n+1) + " of dataset #" + (i+1) + "!");

	if (!hashmap.containsKey(index))
	  hashmap.put(index, 0);
	hashmap.put(index, hashmap.get(index) + 1);

	// use internal representation for faster access
	values = result.instance(index).toDoubleArray();

	// add attribute values
	for (m = 0; m < inst[i].numAttributes(); m++) {
	  // missing value?
	  if (inst[i].instance(n).isMissing(m))
	    continue;

	  switch (inst[i].attribute(m).type()) {
	  case Attribute.NUMERIC:
	  case Attribute.DATE:
	  case Attribute.NOMINAL:
	    values[indexStart[i] + m] = inst[i].instance(n).value(m);
	    break;

	  case Attribute.STRING:
	    value = result.attribute(indexStart[i] + m).addStringValue(inst[i].instance(n).stringValue(m));
	    values[indexStart[i] + m] = value;
	    break;

	  case Attribute.RELATIONAL:
	    value = result.attribute(indexStart[i] + m).addRelation(inst[i].instance(n).relationalValue(m));
	    values[indexStart[i] + m] = value;
	    break;

	  default:
	    throw new IllegalStateException(
		"Unhandled attribute type: " + inst[i].attribute(m).type());
	  }
	}

	// update row
	result.set(index, new DenseInstance(1.0, values));
      }
    }

    if (getRemove()) {
      hs = new HashSet<>();
      for (Integer x: hashmap.keySet()){
	if (hashmap.get(x) != inst.length)
	  hs.add(result.get(x));
      }
      result.removeAll(hs);
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String[]	filesStr;
    File[]	files;
    int		i;
    Instances	output;
    Instances[]	orig;
    Instances[]	inst;
    Instance[]	rows;
    HashSet	ids;
    int		max;
    TIntList	uniqueList;
    Remove	remove;

    result = null;

    // get filenames
    files = null;
    orig  = null;
    if (m_InputToken.getPayload() instanceof String[]) {
      filesStr = (String[]) m_InputToken.getPayload();
      files    = new File[filesStr.length];
      for (i = 0; i < filesStr.length; i++)
	files[i] = new PlaceholderFile(filesStr[i]);
    }
    else if (m_InputToken.getPayload() instanceof File[]) {
      files = (File[]) m_InputToken.getPayload();
    }
    else if (m_InputToken.getPayload() instanceof Instance[]) {
      rows = (Instance[]) m_InputToken.getPayload();
      orig = new Instances[rows.length];
      for (i = 0; i < rows.length; i++) {
	orig[i] = new Instances(rows[i].dataset(), 1);
	orig[i].add((Instance) rows[i].copy());
      }
    }
    else if (m_InputToken.getPayload() instanceof Instances[]) {
      orig = (Instances[]) m_InputToken.getPayload();
    }
    else {
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());
    }

    try {
      output = null;

      // simple merge
      if (m_UniqueID.length() == 0) {
	if (files != null) {
	  inst = new Instances[1];
	  for (i = 0; i < files.length; i++) {
	    if (isStopped())
	      break;
	    inst[0] = DataSource.read(files[i].getAbsolutePath());
	    inst[0] = prepareData(inst[0], i);
	    if (i == 0) {
	      output = inst[0];
	    }
	    else {
	      if (isLoggingEnabled())
		getLogger().info("Merging with file #" + (i+1) + ": " + files[i]);
	      output = Instances.mergeInstances(output, inst[0]);
	    }
	  }
	}
	else if (orig != null) {
	  inst = new Instances[1];
	  for (i = 0; i < orig.length; i++) {
	    if (isStopped())
	      break;
	    inst[0] = prepareData(orig[i], i);
	    if (i == 0) {
	      output = inst[0];
	    }
	    else {
	      if (isLoggingEnabled())
		getLogger().info("Merging with dataset #" + (i+1) + ": " + orig[i].relationName());
	      output = Instances.mergeInstances(output, inst[0]);
	    }
	  }
	}
      }
      // merge based on row IDs
      else {
	m_AttType      = -1;
	max            = 0;
	m_UniqueIDAtts = new ArrayList<>();
	if (files != null) {
	  orig = new Instances[files.length];
	  for (i = 0; i < files.length; i++) {
	    if (isStopped())
	      break;
	    if (isLoggingEnabled())
	      getLogger().info("Loading file #" + (i+1) + ": " + files[i]);
	    orig[i] = DataSource.read(files[i].getAbsolutePath());
	    max     = Math.max(max, orig[i].numInstances());
	  }
	}
	else if (orig != null) {
	  for (i = 0; i < orig.length; i++)
	    max = Math.max(max, orig[i].numInstances());
	}
	inst = new Instances[orig.length];
	ids  = new HashSet(max);
	for (i = 0; i < orig.length; i++) {
	  if (isStopped())
	    break;
	  if (isLoggingEnabled())
	    getLogger().info("Updating IDs #" + (i+1));
	  updateIDs(i, orig[i], ids);
	  if (isLoggingEnabled())
	    getLogger().info("Preparing dataset #" + (i+1));
	  inst[i] = prepareData(orig[i], i);
	}
	output = merge(orig, inst, ids);
	// remove unnecessary unique ID attributes
	if (m_KeepOnlySingleUniqueID) {
	  uniqueList = new TIntArrayList();
	  for (String att: m_UniqueIDAtts)
	    uniqueList.add(output.attribute(att).index());
	  if (uniqueList.size() > 0) {
	    if (isLoggingEnabled())
	      getLogger().info("Removing duplicate unique ID attributes: " + m_UniqueIDAtts);
	    remove = new Remove();
	    remove.setAttributeIndicesArray(uniqueList.toArray());
	    remove.setInputFormat(output);
	    output = Filter.useFilter(output, remove);
	  }
	}
      }

      if (!isStopped()) {
	m_OutputToken = new Token(output);
	updateProvenance(m_OutputToken);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to merge: ", e);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  @Override
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
  }
}
