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
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  private static final long serialVersionUID = 5230406420788469572L;

  /** The keyword to replace with the dataset name in attribute renaming. */
  protected static final String DATASET_KEYWORD = "{DATASET}";

  /** The constant value for datasets that do not have an input row for this output row. */
  protected static final int ROW_MISSING = -1;

  /** The method to use to determine the class attributes across the datasets. */
  protected ClassAttributeMatchingMethod m_ClassAttributeMatchingMethod;

  /** The regex to use for matching class attributes (for method REGEXP). */
  protected BaseRegExp m_ClassAttributeMatchingRegex;

  /**
   * Whether the class-matching regex should consider matches or non-matches
   * as class attributes.
   */
  protected boolean m_InvertClassAttributeMatchingSense;

  /** The name of each dataset to use in attribute renaming. */
  protected BaseString[] m_DatasetNames;

  /** The regexs to use to find attributes that require renaming. */
  protected BaseRegExp[] m_AttributeRenameFindRegex;

  /** The format strings specifying how to rename attributes. */
  protected BaseString[] m_AttributeRenameFormatString;

  /** The name to give the resulting dataset. */
  protected String m_MergedDatasetName;

  /** Whether to check attributes with multiple sources for equal values among those sources. */
  protected boolean m_EnsureEqualValues;

  /** The source datasets we are merging. */
  protected Instances[] m_Datasets;

  /** The set of class names for the given datasets. */
  protected Set<String> m_ClassNames;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "class-match-method", "classMatchMethod",
      ClassAttributeMatchingMethod.USE_EXISTING);

    m_OptionManager.add(
      "class-match-exp", "classMatchExpression",
      new BaseRegExp(""));

    m_OptionManager.add(
      "class-match-invert-sense", "classMatchInvertSense",
      false);

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
   * Gets the method to use for finding class attributes in the
   * source datasets.
   *
   * @return The class-matching method to use.
   */
  public ClassAttributeMatchingMethod getClassMatchMethod() {
    return m_ClassAttributeMatchingMethod;
  }

  /**
   * Sets the method to use for finding class attributes in the
   * source datasets.
   *
   * @param value The method to use.
   */
  public void setClassMatchMethod(ClassAttributeMatchingMethod value) {
    m_ClassAttributeMatchingMethod = value;
    reset();
  }

  /**
   * Gets the tip-text for the class-matching method option.
   *
   * @return The tip-text as a String.
   */
  public String classMatchMethodTipText() {
    return "The method to use to find class attributes in the datasets.";
  }

  /**
   * Gets the regex to use to find class attributes (if the matching
   * method is REGEXP).
   *
   * @return The regex to use.
   */
  public BaseRegExp getClassMatchExpression() {
    return m_ClassAttributeMatchingRegex;
  }

  /**
   * Sets the regex to use to find class attributes (if the matching
   * method is REGEXP).
   *
   * @param value The regex to use.
   */
  public void setClassMatchExpression(BaseRegExp value) {
    m_ClassAttributeMatchingRegex = value;
    reset();
  }

  /**
   * Gets the tip-text for the class-matching regex option.
   *
   * @return The tip-text as a String.
   */
  public String classMatchExpressionTipText() {
    return "The expression to use to identify class attributes in the source datasets.";
  }

  /**
   * Gets whether the class-matching regex is matching for or against class attributes.
   *
   * @return True if the regex matches class attributes, false if it matches non-class attributes.
   */
  public boolean getClassMatchInvertSense() {
    return m_InvertClassAttributeMatchingSense;
  }

  /**
   * Sets whether the class-matching regex is matching for or against class attributes.
   *
   * @param value True if the regex matches class attributes, false if it matches non-class attributes.
   */
  public void setClassMatchInvertSense(boolean value) {
    m_InvertClassAttributeMatchingSense = value;
    reset();
  }

  /**
   * Gets the tip-text for the class-matching sense-inversion option.
   *
   * @return The tip-text as a String.
   */
  public String classMatchInvertSenseTipText() {
    return "Whether the class-matching regex selects classes or non-classes.";
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
    return m_AttributeRenameFindRegex;
  }

  /**
   * Sets the array of attribute rename expressions.
   *
   * @param value The array of regexs.
   */
  public void setAttributeRenamesExp(BaseRegExp[] value) {
    m_AttributeRenameFindRegex = value;
    reset();
  }

  /**
   * Gets the tip-text for the attribute-renaming regexs option.
   *
   * @return The tip-text as a String.
   */
  public String attributeRenamesExpTipText() {
    return "The expressions to use to select attribute names for renaming.";
  }

  /**
   * Gets the array of format strings used for attribute renaming.
   *
   * @return The array of format strings.
   */
  public BaseString[] getAttributeRenamesFormat() {
    return m_AttributeRenameFormatString;
  }

  /**
   * Sets the array of format strings used for attribute renaming.
   *
   * @param value The array of format strings.
   */
  public void setAttributeRenamesFormat(BaseString[] value) {
    m_AttributeRenameFormatString = value;
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

    // All checks passed
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
    Map<String, List<AttributeMappingElement>> attributeMapping = createAttributeMapping();

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
	Attribute attribute = mergedDataset.attribute(attributeIndex);

	// Find the source(s) of the attribute's data
	List<AttributeMappingElement> sourceAttributeElements = attributeMapping.get(attribute.name());

	// Get the value of this attribute from it's source(s)
	Object value = m_EnsureEqualValues ?
	  getValueEnsureEqual(rowSet, sourceAttributeElements) :
	  getValueFirstAvailable(rowSet, sourceAttributeElements);

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
   * @param sourceAttributeElements The source attribute mapping elements.
   * @return The value of the merged attribute.
   */
  protected Object getValueFirstAvailable(int[] rowSet, List<AttributeMappingElement> sourceAttributeElements) {
    // Try each source in turn
    for (AttributeMappingElement element : sourceAttributeElements) {
      // Get the row from the row-set
      int rowIndex = rowSet[element.datasetIndex];

      // Skip datasets that don't have source data for this attribute
      if (rowIndex == ROW_MISSING) continue;

      // Get the source dataset instance
      Instance instance = m_Datasets[element.datasetIndex].instance(rowIndex);

      // Skip datasets that don't have source data for this attribute
      if (instance.isMissing(element.attributeIndex)) continue;

      // Get the value of the source data for this attribute
      Object value = getValue(instance, element.attributeIndex);

      // If we found a value, return it
      return value;
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
  protected Object getValueEnsureEqual(int[] rowSet, List<AttributeMappingElement> sourceAttributeElements) {
    Object value = null;
    AttributeMappingElement valueElement = null;
    int valueRowIndex = ROW_MISSING;

    for (AttributeMappingElement element : sourceAttributeElements) {
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
  protected Map<String, List<AttributeMappingElement>> createAttributeMapping() {
    // Create the mapping
    Map<String, List<AttributeMappingElement>> mapping = new HashMap<>();

    // Process each input attribute in turn
    int originalOrdering = 0;
    for (int datasetIndex = 0; datasetIndex < m_Datasets.length; datasetIndex++) {
      // Get the next dataset to process
      Instances dataset = m_Datasets[datasetIndex];

      // Go through each attribute of the dataset in turn
      for (int attributeIndex = 0; attributeIndex < dataset.numAttributes(); attributeIndex++) {
	// Get the next attribute to process
	Attribute attribute = dataset.attribute(attributeIndex);

	// Get the attribute's mapped name in the merged dataset
	String mappedAttributeName = getMappedAttributeName(datasetIndex, attribute.name());

	// Create the mapping element
	AttributeMappingElement mappingElement = new AttributeMappingElement(datasetIndex, attributeIndex, attribute.name(), originalOrdering);

	// Initialise the mapping list if there isn't one already
	if (!mapping.containsKey(mappedAttributeName))
	  mapping.put(mappedAttributeName, new LinkedList<>());

	// Put the mapping into the return value
	mapping.get(mappedAttributeName).add(mappingElement);

	// Increment the counters
	originalOrdering++;
      }
    }

    // Return the mapping
    return mapping;
  }

  /**
   * Whether the given attribute name is the name of a class attribute.
   *
   * @param attributeName The name to check.
   * @return True if the given attribute name is the name of a class attribute,
   * false otherwise.
   */
  protected boolean isClassName(String attributeName) {
    if (m_ClassNames == null) recordClassNames();

    return m_ClassNames.contains(attributeName);
  }

  /**
   * Scans the datasets for attributes that should be considered classes,
   * and keeps a record of them.
   */
  protected void recordClassNames() {
    // Create the set of class names
    m_ClassNames = new HashSet<>();

    // Process each dataset for class names
    for (Instances dataset : m_Datasets) {
      switch (m_ClassAttributeMatchingMethod) {
	case USE_EXISTING:
	  // Skip this dataset if it doesn't have a class attribute
	  if (dataset.classIndex() < 0) continue;

	  // Get the class attribute for this dataset
	  Attribute classAttribute = dataset.classAttribute();

	  // Just add the name of the existing class attribute
	  m_ClassNames.add(classAttribute.name());

	  break;
	case REGEXP:
	  // Check all the attributes for a regex match
	  for (int attributeIndex = 0; attributeIndex < dataset.numAttributes(); attributeIndex++) {
	    // Get the attribute's name
	    String attributeName = dataset.attribute(attributeIndex).name();

	    // See if it matches the matching regex
	    boolean regexMatches = m_ClassAttributeMatchingRegex.isMatch(attributeName);

	    // Invert the matching sense if requested
	    regexMatches = m_InvertClassAttributeMatchingSense ^ regexMatches;

	    // If it's a match, add it to the set
	    if (regexMatches) m_ClassNames.add(attributeName);
	  }

	  break;
      }
    }
  }

  /**
   * Creates the resultant dataset, ready to be filled with data.
   *
   * @param attributeMapping The mapping from merged attribute names to their
   *                         original names.
   * @return The empty Instances object for the merged dataset.
   */
  protected Instances createEmptyResultantDataset(Map<String, List<AttributeMappingElement>> attributeMapping) {
    // Create a list for ordering the attributes, and a mapping to the actual
    // attributes
    List<AttributeMappingElement> orderingList = new LinkedList<>();
    Map<AttributeMappingElement, Attribute> attributes = new HashMap<>();

    // Copy the mapped attributes from their respective datasets
    for (String mappedAttributeName : attributeMapping.keySet()) {
      Attribute mappedAttribute = createMappedAttribute(mappedAttributeName, attributeMapping.get(mappedAttributeName));
      AttributeMappingElement orderingElement = attributeMapping.get(mappedAttributeName).get(0);
      attributes.put(orderingElement, mappedAttribute);
      orderingList.add(orderingElement);
    }

    // Order the new attributes
    orderingList.sort(this::compare);

    // Create the ordered array-list of the attributes
    ArrayList<Attribute> orderedAttributes = new ArrayList<>(orderingList.size());
    for (AttributeMappingElement element : orderingList) {
      orderedAttributes.add(attributes.get(element));
    }

    // Create the dataset
    return new Instances(m_MergedDatasetName, orderedAttributes, 0);
  }

  /**
   * Creates the attribute for the output merged dataset for the given attribute mapping.
   *
   * @param name The name of the mapped attribute.
   * @param from The list of mappings that the attribute maps to.
   * @return The attribute for the merged dataset.
   */
  protected Attribute createMappedAttribute(String name, List<AttributeMappingElement> from) {
    // Just return a copy of the first attribute we map to
    AttributeMappingElement firstElement = from.get(0);
    Attribute sourceAttribute = m_Datasets[firstElement.datasetIndex].attribute(firstElement.attributeIndex);
    Attribute mappedAttribute = sourceAttribute.copy(name);
    return mappedAttribute;
  }

  /**
   * Compares two AttributeMappingElements to determine the order in which their
   * mapped attributes should appear in the merged dataset.
   *
   * @param element1 The first element to compare.
   * @param element2 The second element to compare.
   * @return element1 < element2 => -1,
   * element1 > element2 => 1,
   * otherwise 0;
   */
  protected int compare(AttributeMappingElement element1, AttributeMappingElement element2) {
    // Check if either attribute is a class attribute
    boolean className1 = isClassName(element1.attributeName);
    boolean className2 = isClassName(element2.attributeName);

    // Put class attributes after everything else
    if (className1 && !className2) {
      return 1;
    }
    else if (!className1 && className2) {
      return -1;
    }
    else {
      // Otherwise, just keep the original ordering
      return element1.originalOrdering - element2.originalOrdering;
    }
  }

  /**
   * Gets the name of the attribute in the merged dataset that the given
   * attribute (given by name) maps to.
   *
   * @param datasetIndex  The index of the dataset in the input array that
   *                      the given attribute belongs to.
   * @param attributeName The name of the attribute in the input datasets.
   * @return The name of the mapped attribute in the merged dataset.
   */
  protected String getMappedAttributeName(int datasetIndex, String attributeName) {
    // Try the rename expressions in order
    for (int renameRegexIndex = 0; renameRegexIndex < m_AttributeRenameFindRegex.length; renameRegexIndex++) {
      BaseRegExp renameRegex = m_AttributeRenameFindRegex[renameRegexIndex];

      // Get the regex matcher for the attribute name
      Matcher attributeNameMatcher = renameRegex.patternValue().matcher(attributeName);

      // Rename the attribute if it is matched
      if (attributeNameMatcher.matches()) {
	// Initialise the mapped name with the format string
	String mappedString = m_AttributeRenameFormatString[renameRegexIndex].stringValue();

	// Replace the {DATASET} keyword with the dataset name
	mappedString = mappedString.replace(DATASET_KEYWORD, m_DatasetNames[datasetIndex].stringValue());

	// Replace any group identifiers with the corresponding group match string
	for (int groupIndex = attributeNameMatcher.groupCount(); groupIndex >= 0; groupIndex--) {
	  String groupMatch = attributeNameMatcher.group(groupIndex);

	  mappedString = mappedString.replace("$" + groupIndex, groupMatch);
	}

	// Return the renamed attribute name
	return mappedString;
      }
    }

    // No expression matched, return the original attribute name
    return attributeName;
  }

  /**
   * Resets the internal state of the merge method when new datasets are supplied.
   *
   * @param datasets The datasets being merged.
   */
  protected void resetInternalState(Instances[] datasets) {
    m_Datasets = datasets;
    m_ClassNames = null;
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
   * Enumeration of the methods available for determining
   * which attributes are class attributes.
   */
  public enum ClassAttributeMatchingMethod {
    USE_EXISTING,
    REGEXP
  }

  /**
   * Helper class for determining the mapping from input attributes in the
   * source datasets to output attributes in the merged dataset.
   */
  protected static class AttributeMappingElement {

    /** The index of the source dataset. */
    public final int datasetIndex;

    /** The index of the source attribute in the source dataset. */
    public final int attributeIndex;

    /** The name of the source attribute in the source dataset. */
    public final String attributeName;

    /** The original order in which the source attribute was encountered during search. */
    public final int originalOrdering;

    /**
     * Standard constructor.
     *
     * @param datasetIndex     The index of the source dataset.
     * @param attributeIndex   The index of the source attribute in the source dataset.
     * @param attributeName    The name of the source attribute in the source dataset.
     * @param originalOrdering The original order in which the source attribute was encountered during search.
     */
    public AttributeMappingElement(int datasetIndex, int attributeIndex, String attributeName, int originalOrdering) {
      this.datasetIndex = datasetIndex;
      this.attributeIndex = attributeIndex;
      this.attributeName = attributeName;
      this.originalOrdering = originalOrdering;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append('(')
	.append(datasetIndex)
	.append(',')
	.append(attributeIndex)
	.append(',')
	.append(attributeName)
	.append(',')
	.append(originalOrdering)
	.append(')');

      return builder.toString();
    }
  }
}
