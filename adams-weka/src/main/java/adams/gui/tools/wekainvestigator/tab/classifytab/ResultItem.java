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
 * ResultItem.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.DateUtils;
import adams.core.Shortening;
import adams.data.spreadsheet.MetaData;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.tools.wekainvestigator.output.AbstractNestableResultItem;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Container for an evaluation, model, training set header. Used in the
 * result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ResultItem
  extends AbstractNestableResultItem {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the evaluation object. */
  protected Evaluation m_Evaluation;

  /** the evaluation objects from the folds. */
  protected Evaluation[] m_FoldEvaluations;

  /** the evaluation objects from the runs. */
  protected Evaluation[] m_RunEvaluations;

  /** the template. */
  protected Classifier m_Template;

  /** the model. */
  protected Classifier m_Model;

  /** the fold models. */
  protected Classifier[] m_FoldModels;

  /** the run models. */
  protected Classifier[] m_RunModels;

  /** the run information. */
  protected MetaData m_RunInformation;

  /** the original indices. */
  protected int[] m_OriginalIndices;

  /** the original indices (folds). */
  protected int[][] m_FoldOriginalIndices;

  /** the original indices (runs). */
  protected int[][] m_RunOriginalIndices;

  /** additional attributes. */
  protected SpreadSheet m_AdditionalAttributes;

  /**
   * Initializes the item.
   *
   * @param template	the template
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Classifier template, Instances header) {
    super(header);

    if (template == null)
      throw new IllegalArgumentException("Template classifier cannot be null!");

    m_Template = template;
  }

  /**
   * Creates the name from the members.
   *
   * @return		the name
   */
  protected String createName() {
    String	result;

    result = DateUtils.getTimeFormatterMsecs().format(m_Timestamp)
      + " - "
      + m_Template.getClass().getSimpleName();

    if (m_NameSuffix != null)
      result += " - " + m_NameSuffix;

    if (hasHeader())
      result += " - " + m_Header.classAttribute().name() + " of " + Shortening.shortenEnd(m_Header.relationName(), MAX_RELATIONNAME_LENGTH);

    return result;
  }

  /**
   * Updates the item.
   *
   * @param runInfo	the meta-data for the run
   * @return		itself
   */
  public ResultItem update(MetaData runInfo) {
    m_RunInformation = runInfo;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param evaluation	the evaluation
   * @return		itself
   */
  public ResultItem update(Evaluation evaluation) {
    m_Evaluation = evaluation;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param model	the model
   * @return		itself
   */
  public ResultItem update(Classifier model) {
    m_Model = model;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param original	the original indices
   * @return		itself
   */
  public ResultItem update(int[] original) {
    m_OriginalIndices = original;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param additional 	the additional attributes
   * @return		itself
   */
  public ResultItem update(SpreadSheet additional) {
    m_AdditionalAttributes = additional;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param foldEvaluations 	the evaluations per fold
   * @return			itself
   */
  public ResultItem updateFolds(Evaluation[] foldEvaluations) {
    m_FoldEvaluations = foldEvaluations;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param foldModels 	the models per fold
   * @return		itself
   */
  public ResultItem updateFolds(Classifier[] foldModels) {
    m_FoldModels = foldModels;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param foldOriginal 	the original indices per fold
   * @return			itself
   */
  public ResultItem updateFolds(int[][] foldOriginal) {
    m_FoldOriginalIndices = foldOriginal;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param runEvaluations 	the evaluations per run
   * @return			itself
   */
  public ResultItem updateRuns(Evaluation[] runEvaluations) {
    m_RunEvaluations = runEvaluations;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param runModels 	the models per run
   * @return		itself
   */
  public ResultItem updateRuns(Classifier[] runModels) {
    m_RunModels = runModels;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param runOriginal 	the original indices per run
   * @return			itself
   */
  public ResultItem updateRuns(int[][] runOriginal) {
    m_RunOriginalIndices = runOriginal;
    invalidateName();
    return this;
  }

  /**
   * Returns whether an Evaluation object is present.
   * 
   * @return		true if available
   */
  public boolean hasEvaluation() {
    return (m_Evaluation != null);
  }

  /**
   * Returns the stored Evaluation object.
   * 
   * @return		the evaluation, null if not present
   */
  public Evaluation getEvaluation() {
    return m_Evaluation;
  }

  /**
   * Returns whether Evaluation objects per fold are present.
   *
   * @return		true if available
   */
  public boolean hasFoldEvaluations() {
    return (m_FoldEvaluations != null);
  }

  /**
   * Returns the stored Evaluation objects per fold.
   *
   * @return		the evaluations per fold, null if not present
   */
  public Evaluation[] getFoldEvaluations() {
    return m_FoldEvaluations;
  }

  /**
   * Returns the stored Evaluation objects per specified fold.
   *
   * @param index 	the index of the fold to return
   * @return		the evaluation for the fold, null if not present
   */
  public Evaluation getFoldEvaluation(int index) {
    if (m_FoldEvaluations != null)
      return m_FoldEvaluations[index];
    else
      return null;
  }

  /**
   * Returns whether Evaluation objects per run are present.
   *
   * @return		true if available
   */
  public boolean hasRunEvaluations() {
    return (m_RunEvaluations != null);
  }

  /**
   * Returns the stored Evaluation objects per run.
   *
   * @return		the evaluations per run, null if not present
   */
  public Evaluation[] getRunEvaluations() {
    return m_RunEvaluations;
  }

  /**
   * Returns the stored Evaluation objects per specified run.
   *
   * @param index 	the index of the run to return
   * @return		the evaluation for the run, null if not present
   */
  public Evaluation getRunEvaluation(int index) {
    if (m_RunEvaluations != null)
      return m_RunEvaluations[index];
    else
      return null;
  }

  /**
   * Sets the stored template object.
   *
   * @param value	the template
   */
  public void setTemplate(Classifier value) {
    m_Template = value;
  }

  /**
   * Returns the stored template object.
   * 
   * @return		the template
   */
  public Classifier getTemplate() {
    return m_Template;
  }

  /**
   * Returns whether an model object is present.
   * 
   * @return		true if available
   */
  public boolean hasModel() {
    return (m_Model != null);
  }

  /**
   * Sets the model to make available.
   *
   * @param value	the model
   */
  public void setModel(Classifier value) {
    m_Model = value;
  }

  /**
   * Returns the stored model object.
   * 
   * @return		the model, null if not present
   */
  public Classifier getModel() {
    return m_Model;
  }

  /**
   * Returns whether Classifier objects per fold are present.
   *
   * @return		true if available
   */
  public boolean hasFoldModels() {
    return (m_FoldModels != null);
  }

  /**
   * Returns the stored Classifier objects per fold.
   *
   * @return		the classifiers per fold, null if not present
   */
  public Classifier[] getFoldModels() {
    return m_FoldModels;
  }

  /**
   * Returns the stored Classifier object for the specified fold.
   *
   * @param index 	the index of the fold
   * @return		the classifier for the fold, null if not present
   */
  public Classifier getFoldModel(int index) {
    if (m_FoldModels != null)
      return m_FoldModels[index];
    else
      return null;
  }

  /**
   * Returns whether Classifier objects per run are present.
   *
   * @return		true if available
   */
  public boolean hasRunModels() {
    return (m_RunModels != null);
  }

  /**
   * Returns the stored Classifier objects per run.
   *
   * @return		the classifiers per run, null if not present
   */
  public Classifier[] getRunModels() {
    return m_RunModels;
  }

  /**
   * Returns whether run information is present.
   * 
   * @return		true if available
   */
  public boolean hasRunInformation() {
    return (m_RunInformation != null);
  }

  /**
   * Returns the stored run information.
   * 
   * @return		the information, null if not present
   */
  public MetaData getRunInformation() {
    return m_RunInformation;
  }

  /**
   * Returns whether the original indices are present.
   *
   * @return		true if available
   */
  public boolean hasOriginalIndices() {
    return (m_OriginalIndices != null);
  }

  /**
   * Returns the stored original indices.
   *
   * @return		the indices, null if not present
   */
  public int[] getOriginalIndices() {
    return m_OriginalIndices;
  }

  /**
   * Returns whether the original indices per fold are present.
   *
   * @return		true if available
   */
  public boolean hasFoldOriginalIndices() {
    return (m_FoldOriginalIndices != null);
  }

  /**
   * Returns the stored original indices per fold.
   *
   * @return		the indices, null if not present
   */
  public int[][] getFoldOriginalIndices() {
    return m_FoldOriginalIndices;
  }

  /**
   * Returns the stored original indices for the specified fold.
   *
   * @param index 	the index of the fold to return
   * @return		the indices for the fold, null if not present
   */
  public int[] getFoldOriginalIndices(int index) {
    if (m_FoldOriginalIndices != null)
      return m_FoldOriginalIndices[index];
    else
      return null;
  }

  /**
   * Returns whether the original indices per run are present.
   *
   * @return		true if available
   */
  public boolean hasRunOriginalIndices() {
    return (m_RunOriginalIndices != null);
  }

  /**
   * Returns the stored original indices per run.
   *
   * @return		the indices, null if not present
   */
  public int[][] getRunOriginalIndices() {
    return m_RunOriginalIndices;
  }

  /**
   * Returns the stored original indices for the specified run.
   *
   * @param index 	the index of the run to return
   * @return		the indices for the run, null if not present
   */
  public int[] getRunOriginalIndices(int index) {
    if (m_RunOriginalIndices != null)
      return m_RunOriginalIndices[index];
    else
      return null;
  }

  /**
   * Returns whether additional attributes data is present.
   *
   * @return		true if available
   */
  public boolean hasAdditionalAttributes() {
    return (m_AdditionalAttributes != null);
  }

  /**
   * Sets the additional attributes data.
   *
   * @param value	the data, null if not present
   */
  public void setAdditionalAttributes(SpreadSheet value) {
    m_AdditionalAttributes = value;
  }

  /**
   * Returns the stored additional attributes data.
   *
   * @return		the data, null if not present
   */
  public SpreadSheet getAdditionalAttributes() {
    return m_AdditionalAttributes;
  }

  /**
   * Returns a short description of the container.
   *
   * @return		the description
   */
  public String toString() {
    String	result;

    result = getName();
    result += ", evaluation=" + hasEvaluation()
      + ", template=" + getTemplate().getClass().getName()
      + ", model=" + hasModel()
      + ", header=" + hasHeader()
      + ", runInfo=" + hasRunInformation();

    return result;
  }
}
