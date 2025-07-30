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

package adams.gui.tools.wekainvestigator.tab.clustertab;

import adams.core.DateUtils;
import adams.core.Shortening;
import adams.data.spreadsheet.MetaData;
import adams.gui.tools.wekainvestigator.output.AbstractResultItem;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Instances;

import java.io.Serializable;

/**
 * Container for an evaluation, model, training set header. Used in the
 * result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ResultItem
  extends AbstractResultItem {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the evaluation object. */
  protected ClusterEvaluation m_Evaluation;

  /** the template. */
  protected Clusterer m_Template;

  /** the model. */
  protected Clusterer m_Model;

  /** the supplementary name. */
  protected String m_SupplementaryName;

  /** supplementary data. */
  protected Serializable m_SupplementaryData;

  /** the run information. */
  protected MetaData m_RunInformation;

  /**
   * Initializes the item.
   *
   * @param template	the template
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Clusterer template, Instances header) {
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

    if (hasHeader())
      result += " - " + Shortening.shortenEnd(m_Header.relationName(), MAX_RELATIONNAME_LENGTH);

    return result;
  }

  /**
   * Updates the item.
   *
   * @param evaluation	the evaluation, can be null
   * @return		itself
   */
  public ResultItem update(ClusterEvaluation evaluation) {
    m_Evaluation = evaluation;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param model	the model, can be null
   * @return		itself
   */
  public ResultItem update(Clusterer model) {
    m_Model = model;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param runInfo	the run information, can be null
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
   * @param supplementaryName	the name for the supplementary data, can be null
   * @return			itself
   */
  public ResultItem updateSupplementary(String supplementaryName) {
    m_SupplementaryName = supplementaryName;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param supplementaryData	the supplementary data, can be null
   * @return			itself
   */
  public ResultItem updateSupplementary(Serializable supplementaryData) {
    m_SupplementaryData = supplementaryData;
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
  public ClusterEvaluation getEvaluation() {
    return m_Evaluation;
  }

  /**
   * Sets the stored template object.
   *
   * @param value	the template
   */
  public void setTemplate(Clusterer value) {
    m_Template = value;
  }

  /**
   * Returns the stored template object.
   *
   * @return		the template, null if not present
   */
  public Clusterer getTemplate() {
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
   * Returns the stored model object.
   * 
   * @return		the model, null if not present
   */
  public Clusterer getModel() {
    return m_Model;
  }

  /**
   * Returns whether a name for the Supplementary object is present.
   * 
   * @return		true if available
   */
  public boolean hasSupplementaryName() {
    return (m_SupplementaryName != null);
  }

  /**
   * Returns the stored Supplementary name.
   * 
   * @return		the model, null if not present
   */
  public String getSupplementaryName() {
    return m_SupplementaryName;
  }

  /**
   * Returns whether an Supplementary object is present.
   * 
   * @return		true if available
   */
  public boolean hasSupplementaryData() {
    return (m_SupplementaryData != null);
  }

  /**
   * Returns the stored Supplementary object.
   * 
   * @return		the model, null if not present
   */
  public Object getSupplementaryData() {
    return m_SupplementaryData;
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
      + ", supplementary=" + (hasSupplementaryName() && hasSupplementaryData())
      + ", runInfo=" + hasRunInformation()
      + ", header=" + hasHeader();

    return result;
  }
}
