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
 * ExtExperiment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.experiment;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.gui.tools.wekamultiexperimenter.experiment.ExperimentWithCustomizableRelationNames;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;

import javax.swing.DefaultListModel;
import java.io.File;

/**
 * Extended version of the Weka {@link Experiment} class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExtExperiment
  extends Experiment
  implements ExperimentWithCustomizableRelationNames {

  private static final long serialVersionUID = 6600722909231586798L;

  /** whether to use the filename (w/o path) instead of relationname. */
  protected boolean m_UseFilename = false;

  /** whether to prefix the relation names with the index. */
  protected boolean m_PrefixDatasetsWithIndex = false;

  /**
   * Default constructor.
   */
  public ExtExperiment() {
    super();
  }

  /**
   * Assigns the values from the given experiment.
   *
   * @param exp		the experiment to get the values from
   */
  public ExtExperiment(Experiment exp) {
    super();
    assign(exp);
  }

  /**
   * Assigns the values from the other experiment.
   *
   * @param exp		the other experiment
   */
  public void assign(Experiment exp) {
    int		i;

    m_ResultListener = (ResultListener) OptionUtils.shallowCopy(exp.getResultListener());
    m_ResultProducer = (ResultProducer) OptionUtils.shallowCopy(exp.getResultProducer());
    m_RunLower = exp.getRunLower();
    m_RunUpper = exp.getRunUpper();
    m_Datasets = new DefaultListModel();
    for (i = 0; i < exp.getDatasets().size(); i++)
      m_Datasets.addElement(exp.getDatasets().get(i));
    m_UsePropertyIterator = exp.getUsePropertyIterator();
    m_PropertyPath = null;
    if (exp.getPropertyPath() != null)
      m_PropertyPath = (PropertyNode[]) Utils.deepCopy(exp.getPropertyPath());
    m_PropertyArray = null;
    if (exp.getPropertyArray() != null)
      m_PropertyArray = Utils.deepCopy(exp.getPropertyArray());
    m_Notes = exp.getNotes();
    m_AdditionalMeasures = null;
    m_ClassFirst = exp.m_ClassFirst;
    m_AdvanceDataSetFirst = exp.getAdvanceDataSetFirst();
  }

  /**
   * Returns whether the class is the first attribute.
   *
   * @return		true if first
   */
  public boolean isClassFirst() {
    return m_ClassFirst;
  }

  /**
   * Sets whether to use the filename (w/o path) instead of the relationname.
   *
   * @param value	true if to use filename
   */
  public void setUseFilename(boolean value) {
    m_UseFilename = value;
  }

  /**
   * Returns whether to use the filename (w/o path) instead of the relationname.
   *
   * @return		true if to use the filename
   */
  public boolean getUseFilename() {
    return m_UseFilename;
  }

  /**
   * Sets whether to prefix the datasets with the index.
   *
   * @param value	true if to prefix
   */
  public void setPrefixDatasetsWithIndex(boolean value) {
    m_PrefixDatasetsWithIndex = value;
  }

  /**
   * Returns whether to prefix the datasets with the index.
   *
   * @return		true if to prefix
   */
  public boolean getPrefixDatasetsWithIndex() {
    return m_PrefixDatasetsWithIndex;
  }

  /**
   * Carries out the next iteration of the experiment.
   *
   * @throws Exception if an error occurs
   */
  public void nextIteration() throws Exception {

    if (m_UsePropertyIterator) {
      if (m_CurrentProperty != m_PropertyNumber) {
        setProperty(0, m_ResultProducer);
        m_CurrentProperty = m_PropertyNumber;
      }
    }

    if (m_CurrentInstances == null) {
      File currentFile = (File) getDatasets().elementAt(m_DatasetNumber);
      AbstractFileLoader loader = ConverterUtils.getLoaderForFile(currentFile);
      loader.setFile(currentFile);
      Instances data = new Instances(loader.getDataSet());
      // only set class attribute if not already done by loader
      if (data.classIndex() == -1) {
        if (m_ClassFirst) {
          data.setClassIndex(0);
        } else {
          data.setClassIndex(data.numAttributes() - 1);
        }
      }

      // modify relation name?
      if (m_UseFilename)
	data.setRelationName(FileUtils.replaceExtension(currentFile, "").getName());
      if (m_PrefixDatasetsWithIndex)
	data.setRelationName((m_DatasetNumber+1) + ":" + data.relationName());

      m_CurrentInstances = data;
      m_ResultProducer.setInstances(m_CurrentInstances);
    }

    m_ResultProducer.doRun(m_RunNumber);

    advanceCounters();
  }
}
