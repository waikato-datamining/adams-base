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

  /** the model. */
  protected Clusterer m_Clusterer;

  /** the supplementary name. */
  protected String m_SupplementaryName;

  /** supplementary data. */
  protected Object m_SupplementaryData;

  /**
   * Initializes the item with no evaluation.
   *
   * @param clusterer	the model
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Clusterer clusterer, Instances header) {
    this(null, clusterer, header);
  }

  /**
   * Initializes the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param clusterer	the model, can be null
   * @param header	the header of the training set, can be null
   */
  public ResultItem(ClusterEvaluation evaluation, Clusterer clusterer, Instances header) {
    this(evaluation, null, null, clusterer, header);
  }

  /**
   * Initializes the item.
   *
   * @param supplementaryData	the supplementary data, can be null
   * @param clusterer	the model, can be null
   * @param header	the header of the training set, can be null
   */
  public ResultItem(String supplementaryName, Object supplementaryData, Clusterer clusterer, Instances header) {
    this(null, supplementaryName, supplementaryData, clusterer, header);
  }

  /**
   * Initializes the item.
   *
   * @param supplementaryData	the supplementary data, can be null
   * @param clusterer	the model, can be null
   * @param header	the header of the training set, can be null
   */
  public ResultItem(ClusterEvaluation evaluation, String supplementaryName, Object supplementaryData, Clusterer clusterer, Instances header) {
    super(header);
    m_Clusterer         = clusterer;
    m_SupplementaryName = supplementaryName;
    m_SupplementaryData = supplementaryData;
    m_Evaluation        = evaluation;
  }

  /**
   * Creates the name from the members.
   *
   * @return		the name
   */
  protected String createName() {
    return DateUtils.getTimeFormatterMsecs().format(m_Timestamp) + " - " + (hasClusterer() ? m_Clusterer.getClass().getSimpleName() : "???");
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
   * Returns whether an Clusterer object is present.
   * 
   * @return		true if available
   */
  public boolean hasClusterer() {
    return (m_Clusterer != null);
  }

  /**
   * Returns the stored Clusterer object.
   * 
   * @return		the model, null if not present
   */
  public Clusterer getClusterer() {
    return m_Clusterer;
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
   * Returns a short description of the container.
   *
   * @return		the description
   */
  public String toString() {
    String	result;

    result = getName();
    result += ", evaluation=" + hasEvaluation() + ", clusterer=" + hasClusterer() + ", header=" + hasHeader();

    return result;
  }
}
