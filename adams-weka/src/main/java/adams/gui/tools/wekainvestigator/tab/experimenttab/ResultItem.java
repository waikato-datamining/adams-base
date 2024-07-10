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
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.experimenttab;

import adams.core.DateUtils;
import adams.core.Shortening;
import adams.data.spreadsheet.MetaData;
import adams.flow.container.WekaExperimentContainer;
import adams.gui.tools.wekainvestigator.output.AbstractNestableResultItem;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Container for an experiment run. Used in the result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ResultItem
  extends AbstractNestableResultItem {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the experiment container. */
  protected WekaExperimentContainer m_Experiment;

  /** the template. */
  protected Classifier m_Template;

  /** the run information. */
  protected MetaData m_RunInformation;

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
   * @param experiment	the experiment, can be null
   */
  public void update(WekaExperimentContainer experiment) {
    update(experiment, null);
  }

  /**
   * Updates the item.
   *
   * @param experiment	the evaluation, can be null
   * @param runInfo	the meta-data for the run
   */
  public void update(WekaExperimentContainer experiment, MetaData runInfo) {
    m_Experiment     = experiment;
    m_RunInformation = runInfo;

    invalidateName();
  }

  /**
   * Returns whether an Evaluation object is present.
   * 
   * @return		true if available
   */
  public boolean hasExperiment() {
    return (m_Experiment != null);
  }

  /**
   * Returns the stored Evaluation object.
   * 
   * @return		the evaluation, null if not present
   */
  public WekaExperimentContainer getExperiment() {
    return m_Experiment;
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
    result += ", experiment=" + hasExperiment()
      + ", template=" + getTemplate().getClass().getName()
      + ", header=" + hasHeader()
      + ", runInfo=" + hasRunInformation();

    return result;
  }
}
