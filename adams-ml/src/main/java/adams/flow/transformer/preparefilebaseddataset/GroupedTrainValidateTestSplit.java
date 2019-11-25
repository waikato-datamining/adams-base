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
 * GroupedTrainValidateTestSplit.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableFiles;
import adams.data.binning.BinnableGroup;
import adams.data.binning.operation.Grouping;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;
import adams.data.splitgenerator.generic.core.Subset;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.PassThrough;
import adams.data.splitgenerator.generic.randomsplit.RandomSplitGenerator;
import adams.data.splitgenerator.generic.randomsplit.SplitPair;
import adams.data.splitgenerator.generic.splitter.DefaultSplitter;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;

import java.util.Arrays;
import java.util.List;

/**
 * Generates a train/validate/test split using the specified grouping.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GroupedTrainValidateTestSplit
  extends TrainValidateTestSplit {

  private static final long serialVersionUID = 7027794624748574933L;

  /** the regular expression for the nominal/string attribute. */
  protected BaseRegExp m_RegExp;

  /** the group expression. */
  protected String m_Group;

  /** whether to only use the name, not the path inthe grouping. */
  protected boolean m_UseOnlyName;

  /** whether to remove the extension. */
  protected boolean m_RemoveExtension;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a train/test split.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "group", "group",
      "$0");

    m_OptionManager.add(
      "use-only-name", "useOnlyName",
      true);

    m_OptionManager.add(
      "remove-extension", "removeExtension",
      false);
  }

  /**
   * Sets the regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').";
  }

  /**
   * Sets the replacement string to use as group (eg '$2').
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the replacement string to use as group (eg '$2').
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The replacement string to use as group (eg '$2').";
  }

  /**
   * Sets whether to use only the file name, not the path, in the grouping.
   *
   * @param value	true if to use only the name
   */
  public void setUseOnlyName(boolean value) {
    m_UseOnlyName = value;
    reset();
  }

  /**
   * Returns whether to use only the file name, not the path, in the grouping..
   *
   * @return		true if to use only the name
   */
  public boolean getUseOnlyName() {
    return m_UseOnlyName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useOnlyNameTipText() {
    return "If enabled, only the file name, not the path, will be used in the grouping.";
  }

  /**
   * Sets whether to remove the extension from the filename before applying
   * the regular expression.
   *
   * @param value	true if to remove the extension
   */
  public void setRemoveExtension(boolean value) {
    m_RemoveExtension = value;
    reset();
  }

  /**
   * Returns whether to remove the extension from the filename before applying
   * the regular expression.
   *
   * @return		true if to remove the extension
   */
  public boolean getRemoveExtension() {
    return m_RemoveExtension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeExtensionTipText() {
    return "If enabled, the extension gets removed before applying the regular expression.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String 	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    result += QuickInfoHelper.toString(this, "group", m_Group, ", group: ");
    result += QuickInfoHelper.toString(this, "useOnlyName", m_UseOnlyName, "only name", ", ");
    result += QuickInfoHelper.toString(this, "removeExtension", m_RemoveExtension, "no ext", ", ");

    return result;
  }

  /**
   * Returns the class that the preparation scheme accepts as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return String[].class;
  }

  /**
   * Hook method for checking the data.
   *
   * @param data	the data to check
   * @return		null if succesful, otherwise error message
   */
  @Override
  protected String check(String[] data) {
    String	result;

    result = super.check(data);

    if (result == null) {
      if (data.length < 2)
	result = "At least two files required, provided: " + data.length;
    }

    return result;
  }

  /**
   * Splits the data using the specified percentage.
   *
   * @param data	the data to use
   * @return		the generated container
   */
  protected Struct2<String[],String[]> split(String[] data, double percentage) {
    Struct2<String[],String[]>			result;
    String[]					train;
    String[]					test;
    DefaultSplitter				splitter;
    RandomSplitGenerator 			generator;
    DefaultRandomization			defRand;
    List<Binnable<String>> 			binnableFiles;
    List<BinnableGroup<String>> 		groupedFiles;
    List<Binnable<BinnableGroup<String>>> 	binnableGroups;
    SplitPair<Binnable<BinnableGroup<String>>>	splitGroups;
    Struct2<TIntList,List<Binnable<String>>> 	subsetTrain;
    Struct2<TIntList,List<Binnable<String>>>	subsetTest;

    generator = new RandomSplitGenerator();
    splitter  = new DefaultSplitter();
    splitter.setPercentage(percentage);
    generator.setSplitter(splitter);
    if (!m_PreserveOrder) {
      defRand = new DefaultRandomization();
      defRand.setSeed(m_Seed);
      defRand.setLoggingLevel(m_LoggingLevel);
      generator.setRandomization(defRand);
    }
    else {
      generator.setRandomization(new PassThrough());
    }

    try {
      binnableFiles  = Wrapping.wrap(Arrays.asList(data), new IndexedBinValueExtractor<>());
      binnableFiles  = Wrapping.addTmpIndex(binnableFiles);  // adding the original index
      groupedFiles   = Grouping.groupAsList(binnableFiles, new BinnableFiles.FileGroupExtractor(m_UseOnlyName, m_RemoveExtension, m_RegExp.getValue(), m_Group));
      binnableGroups = Wrapping.wrap(groupedFiles, new IndexedBinValueExtractor<>());  // wrap for split generator
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to wrap file names in Binnable objects!");
    }

    splitGroups = generator.generate(binnableGroups);
    subsetTrain = Subset.extractIndicesAndBinnable(splitGroups.getTrain());
    subsetTest  = Subset.extractIndicesAndBinnable(splitGroups.getTest());
    train       = BinnableFiles.toStringArray(subsetTrain.value2);
    test        = BinnableFiles.toStringArray(subsetTest.value2);

    return new Struct2<>(train, test);
  }
}
