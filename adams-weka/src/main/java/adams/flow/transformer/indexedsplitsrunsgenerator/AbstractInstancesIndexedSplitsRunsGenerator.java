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
 * AbstractInstancesIndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.MetaData;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Ancestor for generators that process Instances objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInstancesIndexedSplitsRunsGenerator
  extends AbstractIndexedSplitsRunsGenerator
  implements InstancesIndexedSplitsRunsGenerator {

  private static final long serialVersionUID = -3421372018638798691L;

  /** whether to added basic dataset information to the meta-data as well. */
  protected boolean m_AddDatasetInformation;

  /** whether to added attribute information to the meta-data as well. */
  protected boolean m_AddAttributeInformation;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "add-dataset-information", "addDatasetInformation",
      false);

    m_OptionManager.add(
      "add-attribute-information", "addAttributeInformation",
      false);
  }

  /**
   * Sets whether to add dataset information to the metadata.
   *
   * @param value	true if to add
   */
  public void setAddDatasetInformation(boolean value) {
    m_AddDatasetInformation = value;
    reset();
  }

  /**
   * Returns whether to add dataset information to the metadata.
   *
   * @return		true if to add
   */
  public boolean getAddDatasetInformation() {
    return m_AddDatasetInformation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addDatasetInformationTipText() {
    return "If enabled, dataset name and dimensions get added to the meta-data.";
  }

  /**
   * Sets whether to add attribute information to the metadata.
   *
   * @param value	true if to add
   */
  public void setAddAttributeInformation(boolean value) {
    m_AddAttributeInformation = value;
    reset();
  }

  /**
   * Returns whether to add attribute information to the metadata.
   *
   * @return		true if to add
   */
  public boolean getAddAttributeInformation() {
    return m_AddAttributeInformation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addAttributeInformationTipText() {
    return "If enabled, attribute names and types get added to the meta-data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "addDatasetInformation", m_AddDatasetInformation, "dataset info", ", ");
    result += QuickInfoHelper.toString(this, "addAttributeInformation", m_AddAttributeInformation, "att. info", ", ");

    return result;
  }

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * For post-processing successfully generated splits.
   *
   * @param data	the input data
   * @param runs	the generated runs
   * @param errors	for storing errors
   * @return		the runs, null if failed to post-process
   */
  protected IndexedSplitsRuns postGenerate(Object data, IndexedSplitsRuns runs, MessageCollection errors) {
    Instances	inst;
    MetaData 	meta;
    int		i;

    runs = super.postGenerate(data, runs, errors);

    if (runs != null) {
      inst = (Instances) data;
      meta = runs.getMetaData();

      if (m_AddDatasetInformation || m_AddAttributeInformation)
	meta.put(DATASET_NUMATTRIBUTES, inst.numAttributes());

      if (m_AddDatasetInformation) {
        meta.put(DATASET_NAME, inst.relationName());
        meta.put(DATASET_NUMINSTANCES, inst.numInstances());
      }

      if (m_AddAttributeInformation) {
        for (i = 0; i < inst.numAttributes(); i++) {
          meta.put(PREFIX_DATASET_ATTRIBUTE + i + SUFFIX_NAME, inst.attribute(i).name());
          meta.put(PREFIX_DATASET_ATTRIBUTE + i + SUFFIX_TYPE, Attribute.typeToStringShort(inst.attribute(i).type()));
	}
      }
    }

    return runs;
  }
}
