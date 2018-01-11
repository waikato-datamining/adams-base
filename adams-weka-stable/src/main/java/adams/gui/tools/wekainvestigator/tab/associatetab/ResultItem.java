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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.associatetab;

import adams.core.DateUtils;
import adams.core.Shortening;
import adams.data.spreadsheet.MetaData;
import adams.gui.tools.wekainvestigator.output.AbstractResultItem;
import weka.associations.Associator;
import weka.associations.AssociatorEvaluation;
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
  protected AssociatorEvaluation m_Evaluation;

  /** the template. */
  protected Associator m_Template;

  /** the model. */
  protected Associator m_Model;

  /** the run information. */
  protected MetaData m_RunInformation;

  /**
   * Initializes the item.
   *
   * @param template	the template
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Associator template, Instances header) {
    super(header);

    if (template == null)
      throw new IllegalArgumentException("Template associator cannot be null!");

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
   * @param model	the model, can be null
   * @param runInfo	the run information, can be null
   */
  public void update(AssociatorEvaluation evaluation, Associator model, MetaData runInfo) {
    m_Model          = model;
    m_Evaluation     = evaluation;
    m_RunInformation = runInfo;
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
  public AssociatorEvaluation getEvaluation() {
    return m_Evaluation;
  }

  /**
   * Returns the stored template object.
   *
   * @return		the template, null if not present
   */
  public Associator getTemplate() {
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
  public Associator getModel() {
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
      + ", runInfo=" + hasRunInformation()
      + ", header=" + hasHeader();

    return result;
  }
}
