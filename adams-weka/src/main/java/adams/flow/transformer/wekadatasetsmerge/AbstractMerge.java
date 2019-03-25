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
 * AbstractMerge.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekadatasetsmerge;

import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.data.weka.columnfinder.Class;
import adams.data.weka.columnfinder.ColumnFinder;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 * <p>
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 * <p>
 <!-- options-start -->
 <!-- options-end -->
 * <p>
 * Ancestor for merge schemes.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractMerge
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 87541569847452058L;

  /** The keyword to replace with the dataset name in attribute renaming. */
  protected static final String DATASET_KEYWORD = "{DATASET}";

  /** The constant value for datasets that do not have an input row for this output row. */
  protected static final int ROW_MISSING = -1;

  /** The column finder for selecting class attributes. */
  protected ColumnFinder m_ClassFinder;

  /** The name of each dataset to use in attribute renaming. */
  protected BaseString[] m_DatasetNames;

  /** The regexs to use to find attributes that require renaming. */
  protected BaseRegExp[] m_AttributeRenameFindRegexs;

  /** The format strings specifying how to rename attributes. */
  protected BaseString[] m_AttributeRenameFormatStrings;

  /** The name to give the resulting dataset. */
  protected String m_MergedDatasetName;

  /** Whether to check attributes with multiple sources for equal values among those sources. */
  protected boolean m_EnsureEqualValues;

  /** The source datasets we are merging. */
  protected Instances[] m_Datasets;

  /** The set of class attributes for the given datasets. */
  protected int[][] m_ClassAttributes;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "class-finder", "classFinder",
      new Class());

    m_OptionManager.add(
      "dataset-names", "datasetNames",
      new BaseString[0]);

    m_OptionManager.add(
      "attr-renames-exp", "attributeRenamesExp",
      new BaseRegExp[0]);

    m_OptionManager.add(
      "attr-renames-format", "attributeRenamesFormat",
      new BaseString[0]);

    m_OptionManager.add(
      "output-name", "outputName",
      "output");

    m_OptionManager.add(
      "ensure-equal-values", "ensureEqualValues",
      false);
  }

  /**
   * Gets the finder to use for finding class attributes in the
   * source datasets.
   *
   * @return The class-attribute finder.
   */
  public ColumnFinder getClassFinder() {
    return m_ClassFinder;
  }

  /**
   * Sets the finder to use for finding class attributes in the
   * source datasets.
   *
   * @param value The class-attribute finder.
   */
  public void setClassFinder(ColumnFinder value) {
    m_ClassFinder = value;
    reset();
  }

  /**
   * Gets the tip-text for the classFinder option.
   *
   * @return The tip-text as a String.
   */
  public String classFinderTipText() {
    return "The column finder to use to find class attributes in the datasets.";
  }

  /**
   * Gets the list of names to use in attribute renaming in place of the
   * {DATASET} keyword.
   *
   * @return The list of dataset names.
   */
  public BaseString[] getDatasetNames() {
    return m_DatasetNames;
  }

  /**
   * Sets the list of names to use in attribute renaming in place of the
   * {DATASET} keyword.
   *
   * @param value The list of dataset names.
   */
  public void setDatasetNames(BaseString[] value) {
    if (value == null) value = new BaseString[0];

    if (m_AttributeRenameFindRegexs != null && value.length < m_AttributeRenameFindRegexs.length) {
      BaseString[] expandedValue = new BaseString[m_AttributeRenameFindRegexs.length];

      for (int i = 0; i < m_AttributeRenameFindRegexs.length; i++) {
        if (i < value.length) {
	  expandedValue[i] = value[i];
	} else {
	  expandedValue[i] = new BaseString("Dataset" + i);
	}
      }

      value = expandedValue;
    }

    m_DatasetNames = value;

    reset();
  }

  /**
   * Gets the tip-text for the dataset names option.
   *
   * @return The tip-text as a String.
   */
  public String datasetNamesTipText() {
    return "The list of dataset names to use in attribute renaming.";
  }

  /**
   * Gets the array of attribute rename expressions.
   *
   * @return The array of regexs.
   */
  public BaseRegExp[] getAttributeRenamesExp() {
    return m_AttributeRenameFindRegexs;
  }

  /**
   * Sets the array of attribute rename expressions.
   *
   * @param value The array of regexs.
   */
  public void setAttributeRenamesExp(BaseRegExp[] value) {
    m_AttributeRenameFindRegexs = value;
    setAttributeRenamesFormat(m_AttributeRenameFormatStrings);
    setDatasetNames(m_DatasetNames);
    reset();
  }

  /**
   * Gets the tip-text for the attribute-renaming regexs option.
   *
   * @return The tip-text as a String.
   */
  public String attributeRenamesExpTipText() {
    return "The expressions to use to select attribute names for renaming (one per dataset).";
  }

  /**
   * Gets the array of format strings used for attribute renaming.
   *
   * @return The array of format strings.
   */
  public BaseString[] getAttributeRenamesFormat() {
    return m_AttributeRenameFormatStrings;
  }

  /**
   * Sets the array of format strings used for attribute renaming.
   *
   * @param value The array of format strings.
   */
  public void setAttributeRenamesFormat(BaseString[] value) {
    if (value == null) value = new BaseString[0];

    if (m_AttributeRenameFindRegexs != null && value.length < m_AttributeRenameFindRegexs.length) {
      value = (BaseString[]) Utils.adjustArray(value, m_AttributeRenameFindRegexs.length, new BaseString("$0"));
    }

    m_AttributeRenameFormatStrings = value;

    reset();
  }

  /**
   * Gets the tip-text for the attribute renaming format strings option.
   *
   * @return The tip-text as a String.
   */
  public String attributeRenamesFormatTipText() {
    return "One format string for each renaming expression to specify how to rename the attribute. " +
      "Can contain the " + DATASET_KEYWORD + " keyword which will be replaced by the dataset name, " +
      "and also group identifiers which will be replaced by groups from the renaming regex.";
  }

  /**
   * Gets the name to use for the merged dataset.
   *
   * @return The name to use.
   */
  public String getOutputName() {
    return m_MergedDatasetName;
  }

  /**
   * Sets the name to use for the merged dataset.
   *
   * @param value The name to use.
   */
  public void setOutputName(String value) {
    m_MergedDatasetName = value;
    reset();
  }

  /**
   * Gets the tip-text for the output name option.
   *
   * @return The tip-text as a String.
   */
  public String outputNameTipText() {
    return "The name to use for the merged dataset.";
  }

  /**
   * Gets whether to check all data-sources for a merged attribute have
   * the same value.
   *
   * @return True if value equality should be checked, false if not.
   */
  public boolean getEnsureEqualValues() {
    return m_EnsureEqualValues;
  }

  /**
   * Sets whether to check all data-sources for a merged attribute have
   * the same value.
   *
   * @param value True if value equality should be checked, false if not.
   */
  public void setEnsureEqualValues(boolean value) {
    m_EnsureEqualValues = value;
    reset();
  }

  /**
   * Gets the tip-text for the ensure-equal-values option.
   *
   * @return The tip-text as a String.
   */
  public String ensureEqualValuesTipText() {
    return "Whether multiple attributes being merged into a single attribute require equal values " +
      "from all sources.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns just null.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Sets the value of the given attribute in the given instance to
   * the given value (handles object conversion).
   *
   * @param toSet          The instance against which the value should be set.
   * @param attributeIndex The index of the attribute against which to
   *                       set the value.
   * @param value          The value to set the attribute to.
   */
  protected void setValue(Instance toSet, int attributeIndex, Object value) {
    if (toSet == null) return;

    Attribute attribute = toSet.attribute(attributeIndex);

    if (attribute.isString()) {
      toSet.setValue(attributeIndex, (String) value);
    }
    else if (attribute.isRelationValued()) {
      int relationIndex = attribute.addRelation((Instances) value);
      toSet.setValue(attributeIndex, (double) relationIndex);
    }
    else {
      toSet.setValue(attributeIndex, (Double) value);
    }
  }

  /**
   * Gets the value of the specified attribute from the given Instance.
   *
   * @param toGetFrom      The instance to get a value from.
   * @param attributeIndex The index of the value's attribute.
   * @return The value of the instance at the given index.
   */
  protected Object getValue(Instance toGetFrom, int attributeIndex) {
    if (toGetFrom == null) return null;

    Attribute attribute = toGetFrom.attribute(attributeIndex);

    if (attribute.isString()) {
      return toGetFrom.stringValue(attributeIndex);
    }
    else if (attribute.isRelationValued()) {
      return toGetFrom.relationalValue(attributeIndex);
    }
    else {
      return toGetFrom.value(attributeIndex);
    }
  }

  /**
   * Creates a new dense instance of the size expected by the given dataset.
   *
   * @param dataset The dataset to create a new instance for.
   * @return The created dataset.
   */
  protected Instance newInstanceForDataset(Instances dataset) {
    Instance instance = new DenseInstance(dataset.numAttributes());
    instance.setDataset(dataset);
    return instance;
  }

  /**
   * Hook method for performing checks before attempting the merge.
   *
   * @param datasets the datasets to merge
   * @return null if successfully checked, otherwise error message
   */
  protected String check(Instances[] datasets) {
    // Check the source datasets were provided
    if (datasets == null)
      return "No datasets to merge!";

    // Check that at least 2 datasets are available to merge
    if (datasets.length < 2)
      return "Require at least 2 datasets to merge, but " + datasets.length + " were provided.";

    // Make sure an output name is provided
    if (m_MergedDatasetName.length() == 0)
      return "Must provide a name for the output dataset.";

    // Check that there are enough dataset names provided
    if (m_DatasetNames.length < m_AttributeRenameFindRegexs.length) {
      return "Not enough dataset names supplied for attribute renaming (require " +
	m_AttributeRenameFindRegexs.length + ", have " + m_DatasetNames.length + ").";
    }

    // Check that there are enough renaming format strings provided
    if (m_AttributeRenameFormatStrings.length < m_AttributeRenameFindRegexs.length) {
      return "Not enough format strings supplied for attribute renaming (require " +
	m_AttributeRenameFindRegexs.length + ", have " + m_AttributeRenameFormatStrings.length + ").";
    }

    // All checks passed
    return null;
  }

  /**
   * Makes sure the source data for each mapped attribute is the same type.
   *
   * @param attributeMapping  The attribute mapping.
   * @return  Null if all mappings are okay, or an error message if not.
   */
  protected String checkAttributeMapping(Map<String, List<SourceAttribute>> attributeMapping) {
    // Check each mapped attribute in turn
    for (String mappedName : attributeMapping.keySet()) {
      // Get the list of source attributes
      List<SourceAttribute> sources = attributeMapping.get(mappedName);

      // Use the type of the first source as a reference
      int referenceType = sources.get(0).getSource().type();

      // Check each source against the reference
      for (SourceAttribute source : sources) {
        // If this source has a different type, report the error
        if (source.getSource().type() != referenceType) {
          return "Source data mismatch for mapped attribute " + mappedName + "! " +
            m_DatasetNames[sources.get(0).datasetIndex] +
            ":" +
            sources.get(0).attributeName +
            " = " +
            Attribute.typeToString(referenceType) +
            ", " +
            m_DatasetNames[source.datasetIndex] +
            ":" +
            source.attributeName +
            " = " +
            Attribute.typeToString(source.getSource().type());
        }
      }
    }

    // All sources checked out
    return null;
  }

  /**
   * Merges the datasets.
   *
   * @param datasets the datasets to merge
   * @return the merged dataset
   */
  public Instances merge(Instances[] datasets) {
    // Reset the internal state
    resetInternalState(datasets);

    // Validate the input
    String msg = check(datasets);
    if (msg != null) throw new IllegalStateException(msg);

    // Create the attribute mapping
    Map<String, List<SourceAttribute>> attributeMapping = createAttributeMapping();

    // Make sure the mapping is valid
    msg = checkAttributeMapping(attributeMapping);
    if (msg != null) throw new IllegalStateException(msg);

    // Create the empty resulting dataset
    Instances mergedDataset = createEmptyResultantDataset(attributeMapping);

    // Get the row-set iterator over the datasets
    Enumeration<int[]> rowSetEnumeration = getRowSetEnumeration();

    // Add the data to the merged dataset
    while (rowSetEnumeration.hasMoreElements()) {
      // Get the row-set to work from
      int[] rowSet = rowSetEnumeration.nextElement();

      // Create a new instance for the merged dataset
      Instance mergedInstance = newInstanceForDataset(mergedDataset);

      // Process each attribute of the merged dataset in turn
      for (int attributeIndex = 0; attributeIndex < mergedDataset.numAttributes(); attributeIndex++) {
	// Get the next attribute to copy
	Attribute mergedAttribute = mergedDataset.attribute(attributeIndex);

	// Find the source(s) of the attribute's data
	List<SourceAttribute> sourceAttributes = attributeMapping.get(mergedAttribute.name());

	// Get the value of this attribute from it's source(s)
	Object value = m_EnsureEqualValues ?
	  getValueEnsureEqual(rowSet, sourceAttributes) :
	  getValueFirstAvailable(rowSet, sourceAttributes);

	// Copy the value to the merged dataset if it's found
	if (value != null) setValue(mergedInstance, attributeIndex, value);
      }

      // Add the completed instance to the merged dataset
      mergedDataset.add(mergedInstance);
    }

    // Return the resulting merged dataset
    return mergedDataset;
  }

  /**
   * Gets the first encountered source value for a merged attribute.
   *
   * @param rowSet                  The row-set of source data.
   * @param sourceAttributes The source attribute mapping elements.
   * @return The value of the merged attribute.
   */
  protected Object getValueFirstAvailable(int[] rowSet, List<SourceAttribute> sourceAttributes) {
    // Try each source in turn
    for (SourceAttribute source : sourceAttributes) {
      // Get the row from the row-set
      int rowIndex = rowSet[source.datasetIndex];

      // Skip datasets that don't have source data for this attribute
      if (rowIndex == ROW_MISSING) continue;

      // Get the source dataset instance
      Instance instance = m_Datasets[source.datasetIndex].instance(rowIndex);

      // Skip datasets that don't have source data for this attribute
      if (instance.isMissing(source.attributeIndex)) continue;

      // Return the value of the source data for this attribute
      return getValue(instance, source.attributeIndex);
    }

    // No value found
    return null;
  }

  /**
   * Gets the value of the mapped attribute, ensuring that all possible sources either provide
   * a missing value or the same value as each other.
   *
   * @param rowSet                  The row-set of source data.
   * @param sourceAttributeElements The source attribute mapping elements.
   * @return The value of the merged attribute.
   */
  protected Object getValueEnsureEqual(int[] rowSet, List<SourceAttribute> sourceAttributeElements) {
    Object value = null;
    SourceAttribute valueElement = null;
    int valueRowIndex = ROW_MISSING;

    for (SourceAttribute element : sourceAttributeElements) {
      // Get the row from the row-set
      int rowIndex = rowSet[element.datasetIndex];

      // Skip datasets that don't have source data for this attribute
      if (rowIndex == ROW_MISSING) continue;

      // Get the source dataset instance
      Instance instance = m_Datasets[element.datasetIndex].instance(rowIndex);

      // Skip datasets that don't have source data for this attribute
      if (instance.isMissing(element.attributeIndex)) continue;

      // Get the value of the source data for this attribute element
      Object currentValue = getValue(instance, element.attributeIndex);

      // Make sure it equals any previously found values
      if (value == null) {
	value = currentValue;
	valueElement = element;
	valueRowIndex = rowIndex;
      }
      else if (!value.equals(currentValue)) {
	throw new IllegalStateException("Merging attributes have multiple different source values! " +
	  "(" + currentValue + " in " + m_DatasetNames[element.datasetIndex] + ", attribute " + element.attributeName + ", row " + rowIndex + " " +
	  "instead of " + value + " in " + m_DatasetNames[valueElement.datasetIndex] + ", attribute " + valueElement.attributeName + ", row " + valueRowIndex + ")");
      }
    }

    return value;
  }

  /**
   * Creates a mapping from the attributes in each input dataset to the corresponding
   * attribute in the merged dataset.
   *
   * @return The mapping from input attribute names to output attribute names.
   */
  protected Map<String, List<SourceAttribute>> createAttributeMapping() {
    // Create the mapping
    Map<String, List<SourceAttribute>> mapping = new HashMap<>();

    // Process each input attribute in turn
    for (int datasetIndex = 0; datasetIndex < m_Datasets.length; datasetIndex++) {
      // Get the next dataset to process
      Instances dataset = m_Datasets[datasetIndex];

      // Go through each attribute of the dataset in turn
      for (int attributeIndex = 0; attributeIndex < dataset.numAttributes(); attributeIndex++) {
	// Get the name of the next attribute to process
	String attributeName = dataset.attribute(attributeIndex).name();

        // Create the source attribute element
        SourceAttribute source = new SourceAttribute(datasetIndex, attributeIndex, attributeName);

        // Get the attribute's mapped name in the merged dataset
	String mappedAttributeName = getMappedAttributeName(source);

	// Initialise the mapping list if there isn't one already
        // for this mapped attribute
	if (!mapping.containsKey(mappedAttributeName))
	  mapping.put(mappedAttributeName, new LinkedList<>());

	// Put the mapping into the return value
	mapping.get(mappedAttributeName).add(source);
      }
    }

    // Return the mapping
    return mapping;
  }

  /**
   * Checks if any of the source attributes in the given list is a class
   * attribute.
   *
   * @param sources The source attributes to check.
   * @return  True if a source attribute is a class, false if none are.
   */
  protected boolean isAnyClassAttribute(List<SourceAttribute> sources) {
    // Check each source in turn
    for (SourceAttribute source : sources) {
      // If this source is a class attribute, return a positive hit
      if (isClassAttribute(source)) return true;
    }

    // No sources were class attributes
    return false;
  }

  /**
   * Checks if the given source attribute is a class attribute.
   *
   * @param source  The source attribute to check.
   * @return  True if the source is a class attribute, false if not.
   */
  protected boolean isClassAttribute(SourceAttribute source) {
    // Defer
    return isClassAttribute(source.datasetIndex, source.attributeIndex);
  }

  /**
   * Whether the given attribute is a class attribute.
   *
   * @param datasetIndex  The dataset the attribute is in.
   * @param attributeIndex  The index of the attribute in the dataset.
   * @return True if the given attribute is a class attribute,
   * false if not.
   */
  protected boolean isClassAttribute(int datasetIndex, int attributeIndex) {
    // Initialise the class map once
    if (m_ClassAttributes == null) recordClassAttributes();

    // See if the given attribute is in the class map
    return Arrays.binarySearch(m_ClassAttributes[datasetIndex], attributeIndex) >= 0;
  }

  /**
   * Scans the datasets for attributes that should be considered classes,
   * and keeps a record of them.
   */
  protected void recordClassAttributes() {
    // Create the set of class attributes
    m_ClassAttributes = new int[m_Datasets.length][];

    // Process each dataset for class attributes
    for (int i = 0; i < m_Datasets.length; i++) {
      // Get the next dataset
      Instances dataset = m_Datasets[i];

      // Find the class attributes
      int[] classAttributes = m_ClassFinder.findColumns(dataset);

      // Make sure the array is sorted (so we can do binary search)
      Arrays.sort(classAttributes);

      // Record the class attributes
      m_ClassAttributes[i] = classAttributes;
    }
  }

  /**
   * Creates the resultant dataset, ready to be filled with data.
   *
   * @param attributeMapping The mapping from merged attribute names to their
   *                         original names.
   * @return The empty Instances object for the merged dataset.
   */
  protected Instances createEmptyResultantDataset(Map<String, List<SourceAttribute>> attributeMapping) {
    // Create a list for ordering the attributes, and a mapping to the actual
    // attributes
    List<List<SourceAttribute>> orderingList = new LinkedList<>();
    Map<List<SourceAttribute>, Attribute> attributes = new HashMap<>();

    // Copy the mapped attributes from their respective datasets
    for (String mappedAttributeName : attributeMapping.keySet()) {
      List<SourceAttribute> sources = attributeMapping.get(mappedAttributeName);
      Attribute mappedAttribute = createMappedAttribute(mappedAttributeName, sources);
      attributes.put(sources, mappedAttribute);
      orderingList.add(sources);
    }

    // Order the new attributes
    orderingList.sort(this::compare);

    // Create the ordered array-list of the attributes
    ArrayList<Attribute> orderedAttributes = new ArrayList<>(orderingList.size());
    for (List<SourceAttribute> orderingElement : orderingList) {
      orderedAttributes.add(attributes.get(orderingElement));
    }

    // Create the dataset
    return new Instances(m_MergedDatasetName, orderedAttributes, 0);
  }

  /**
   * Creates the attribute for the output merged dataset for the given attribute mapping.
   *
   * @param name The name of the mapped attribute.
   * @param sources The list of mappings that the attribute maps to.
   * @return The attribute for the merged dataset.
   */
  protected Attribute createMappedAttribute(String name, List<SourceAttribute> sources) {
    // Just return a copy of the first attribute we map to
    return sources.get(0).getSource().copy(name);
  }

  /**
   * Compares two lists of source attributes to determine the order in which their
   * mapped attributes should appear in the merged dataset.
   *
   * @param sources1 The source attributes of the first mapped attribute.
   * @param sources2 The source attributes of the second mapped attribute.
   * @return sources1 < sources2 => -1,
   * sources1 > sources2 => 1,
   * otherwise 0;
   */
  protected int compare(List<SourceAttribute> sources1, List<SourceAttribute> sources2) {
    // Check if either mapped attribute is a class attribute
    boolean className1 = isAnyClassAttribute(sources1);
    boolean className2 = isAnyClassAttribute(sources2);

    // Put class attributes after everything else
    if (className1 && !className2) {
      return 1;
    }
    else if (!className1 && className2) {
      return -1;
    }
    else {
      // Otherwise, just order by first source
      return sources1.get(0).compareTo(sources2.get(0));
    }
  }

  /**
   * Gets the name of the attribute in the merged dataset that the given
   * source attribute maps to.
   *
   * @param source  The source attribute.
   * @return The name of the mapped attribute in the merged dataset.
   */
  protected String getMappedAttributeName(SourceAttribute source) {
    // Can't rename class attributes
    if (isClassAttribute(source)) return source.attributeName;

    // See if we have a rename expression for the dataset
    if (source.datasetIndex < m_AttributeRenameFindRegexs.length) {
      // Get the rename expression for the dataset
      BaseRegExp renameRegex = m_AttributeRenameFindRegexs[source.datasetIndex];

      // Get the regex matcher for the attribute name
      Matcher attributeNameMatcher = renameRegex.patternValue().matcher(source.attributeName);

      // Rename the attribute if it is matched
      if (attributeNameMatcher.matches()) {
	// Initialise the mapped name with the format string
	String mappedString = m_AttributeRenameFormatStrings[source.datasetIndex].stringValue();

	// Replace the {DATASET} keyword with the dataset name
	mappedString = mappedString.replace(DATASET_KEYWORD, m_DatasetNames[source.datasetIndex].stringValue());

	// Replace any group identifiers with the corresponding group match string
	for (int groupIndex = attributeNameMatcher.groupCount(); groupIndex >= 0; groupIndex--) {
	  String groupMatch = attributeNameMatcher.group(groupIndex);

	  mappedString = mappedString.replace("$" + groupIndex, groupMatch);
	}

	// Return the renamed attribute name
	return mappedString;
      }
    }

    // Couldn't rename, return the original attribute name
    return source.attributeName;
  }

  /**
   * Resets the internal state of the merge method when new datasets are supplied.
   *
   * @param datasets The datasets being merged.
   */
  protected void resetInternalState(Instances[] datasets) {
    m_Datasets = datasets;
    m_ClassAttributes = null;
  }

  /**
   * Allows specific merge methods to specify the order in which rows are placed
   * into the merged dataset, and which rows from the source datasets are used
   * for the source data.
   *
   * @return An enumeration of the source rows, one row for each dataset.
   */
  protected abstract Enumeration<int[]> getRowSetEnumeration();

  /**
   * Helper class for determining the mapping from input attributes in the
   * source datasets to output attributes in the merged dataset.
   */
  protected class SourceAttribute implements Comparable<SourceAttribute> {

    /** The index of the source dataset. */
    public final int datasetIndex;

    /** The index of the source attribute in the source dataset. */
    public final int attributeIndex;

    /** The name of the source attribute in the source dataset. */
    public final String attributeName;

    /**
     * Standard constructor.
     *
     * @param datasetIndex     The index of the source dataset.
     * @param attributeIndex   The index of the source attribute in the source dataset.
     * @param attributeName    The name of the source attribute in the source dataset.
     */
    public SourceAttribute(int datasetIndex, int attributeIndex, String attributeName) {
      this.datasetIndex = datasetIndex;
      this.attributeIndex = attributeIndex;
      this.attributeName = attributeName;
    }

    /**
     * Gets the actual source attribute from the source datasets.
     *
     * @return  The source attribute.
     */
    public Attribute getSource() {
      return m_Datasets[datasetIndex].attribute(attributeIndex);
    }

    @Override
    public String toString() {
      return attributeName +
        '[' +
        datasetIndex +
        ", " +
        attributeIndex +
        ']';
    }

    @Override
    public int compareTo(SourceAttribute o) {
      if (datasetIndex < o.datasetIndex) {
        return -1;
      } else if (datasetIndex > o.datasetIndex) {
        return 1;
      } else {
        return Integer.compare(attributeIndex, o.attributeIndex);
      }
    }
  }
}
