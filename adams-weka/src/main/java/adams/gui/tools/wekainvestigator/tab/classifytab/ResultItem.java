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
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
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

  /** the template. */
  protected Classifier m_Template;

  /** the model. */
  protected Classifier m_Model;

  /** the fold models. */
  protected Classifier[] m_FoldModels;

  /** the run information. */
  protected MetaData m_RunInformation;

  /** the original indices. */
  protected int[] m_OriginalIndices;

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
   * @param evaluation	the evaluation, can be null
   * @param model	the model, can be null
   */
  public void update(Evaluation evaluation, Classifier model) {
    update(evaluation, model, null);
  }

  /**
   * Updates the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param model	the model, can be null
   * @param runInfo	the meta-data for the run
   */
  public void update(Evaluation evaluation, Classifier model, MetaData runInfo) {
    update(evaluation, model, runInfo, null, null);
  }

  /**
   * Updates the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param model	the model, can be null
   * @param runInfo	the meta-data for the run
   * @param original	the original indices, can be null
   * @param additional 	the additional attributes, can be null
   */
  public void update(Evaluation evaluation, Classifier model, MetaData runInfo, int[] original, SpreadSheet additional) {
    update(evaluation, null, model, null, runInfo, original, additional);
  }

  /**
   * Updates the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param foldEvaluations the evaluations per fold, can be null
   * @param model	the model, can be null
   * @param foldModels 	the models per fold, can be null
   * @param runInfo	the meta-data for the run
   * @param original	the original indices, can be null
   * @param additional 	the additional attributes, can be null
   */
  public void update(Evaluation evaluation, Evaluation[] foldEvaluations, Classifier model, Classifier[] foldModels, MetaData runInfo, int[] original, SpreadSheet additional) {
    m_Evaluation           = evaluation;
    m_FoldEvaluations      = foldEvaluations;
    m_FoldModels           = foldModels;
    m_Model                = model;
    m_RunInformation       = runInfo;
    m_OriginalIndices      = original;
    m_AdditionalAttributes = additional;

    invalidateName();
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
