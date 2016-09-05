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
 * ResultItem.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab;

import adams.core.DateUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.tools.wekainvestigator.output.AbstractResultItem;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 * Container for an evaluation, model, training set header. Used in the
 * result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
  protected Object m_SupplementaryData;

  /** the run information. */
  protected MetaData m_RunInformation;

  /**
   * Initializes the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param template	the template
   * @param model	the model, can be null
   * @param header	the header of the training set, can be null
   * @param runInfo	the run information, can be null
   */
  public ResultItem(ClusterEvaluation evaluation, Clusterer template, Clusterer model, Instances header, MetaData runInfo) {
    this(evaluation, null, null, template, model, header, runInfo);
  }

  /**
   * Initializes the item.
   *
   * @param supplementaryName	the name for the supplementary data, can be null
   * @param supplementaryData	the supplementary data, can be null
   * @param template	the template
   * @param model	the model, can be null
   * @param header	the header of the training set, can be null
   * @param runInfo	the run information, can be null
   */
  public ResultItem(String supplementaryName, Object supplementaryData, Clusterer template, Clusterer model, Instances header, MetaData runInfo) {
    this(null, supplementaryName, supplementaryData, template, model, header, runInfo);
  }

  /**
   * Initializes the item.
   *
   * @param evaluation		the evaluation, can be null
   * @param supplementaryName	the name for the supplementary data, can be null
   * @param supplementaryData	the supplementary data, can be null
   * @param template	the template
   * @param model	the model, can be null
   * @param header	the header of the training set, can be null
   * @param runInfo	the run information, can be null
   */
  public ResultItem(ClusterEvaluation evaluation, String supplementaryName, Object supplementaryData, Clusterer template, Clusterer model, Instances header, MetaData runInfo) {
    super(header);
    m_Model             = model;
    m_Template          = template;
    m_SupplementaryName = supplementaryName;
    m_SupplementaryData = supplementaryData;
    m_Evaluation        = evaluation;
    m_RunInformation    = runInfo;
  }

  /**
   * Creates the name from the members.
   *
   * @return		the name
   */
  protected String createName() {
    return DateUtils.getTimeFormatterMsecs().format(m_Timestamp) + " - " + m_Template.getClass().getSimpleName();
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
