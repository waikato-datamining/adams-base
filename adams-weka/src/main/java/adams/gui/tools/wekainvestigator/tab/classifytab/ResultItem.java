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

  /** the model. */
  protected Classifier m_Classifier;

  /**
   * Initializes the item with no evaluation.
   *
   * @param classifier	the model
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Classifier classifier, Instances header) {
    this(null, classifier, header);
  }

  /**
   * Initializes the item.
   *
   * @param evaluation	the evaluation, can be null
   * @param classifier	the model, can be null
   * @param header	the header of the training set, can be null
   */
  public ResultItem(Evaluation evaluation, Classifier classifier, Instances header) {
    super(header);
    m_Evaluation = evaluation;
    m_Classifier = classifier;
  }

  /**
   * Creates the name from the members.
   *
   * @return		the name
   */
  protected String createName() {
    return DateUtils.getTimeFormatterMsecs().format(m_Timestamp) + " - " + (hasClassifier() ? m_Classifier.getClass().getSimpleName() : "???");
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
   * Returns whether an Classifier object is present.
   * 
   * @return		true if available
   */
  public boolean hasClassifier() {
    return (m_Classifier != null);
  }

  /**
   * Returns the stored Classifier object.
   * 
   * @return		the model, null if not present
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns a short description of the container.
   *
   * @return		the description
   */
  public String toString() {
    String	result;

    result = m_Name;
    result += ", evaluation=" + hasEvaluation() + ", classifier=" + hasClassifier() + ", header=" + hasHeader();

    return result;
  }
}
