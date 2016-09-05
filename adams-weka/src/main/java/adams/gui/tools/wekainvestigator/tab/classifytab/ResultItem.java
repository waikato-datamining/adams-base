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

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.DateUtils;
import adams.data.spreadsheet.MetaData;
import adams.gui.tools.wekainvestigator.output.AbstractResultItem;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
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
  protected Evaluation m_Evaluation;

  /** the template. */
  protected Classifier m_Template;

  /** the model. */
  protected Classifier m_Model;

  /** the run information. */
  protected MetaData m_RunInformation;

  /**
   * Initializes the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param template	the template
   * @param model	the model, can be null
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Evaluation evaluation, Classifier template, Classifier model, Instances header) {
    this(evaluation, template, model, header, null);
  }

  /**
   * Initializes the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param model	the model, can be null
   * @param header	the header of the training set, can be null
   * @param runInfo	the meta-data for the run
   */
  public ResultItem(Evaluation evaluation, Classifier template, Classifier model, Instances header, MetaData runInfo) {
    super(header);

    if (template == null)
      throw new IllegalArgumentException("Template classifier cannot be null!");

    m_Evaluation     = evaluation;
    m_Template       = template;
    m_Model          = model;
    m_RunInformation = runInfo;
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
  public Evaluation getEvaluation() {
    return m_Evaluation;
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
   * Returns the stored model object.
   * 
   * @return		the model, null if not present
   */
  public Classifier getModel() {
    return m_Model;
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
      + ", header=" + hasHeader()
      + ", runInfo=" + hasRunInformation();

    return result;
  }
}
