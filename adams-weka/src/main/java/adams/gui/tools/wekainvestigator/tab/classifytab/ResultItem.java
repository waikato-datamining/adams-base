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
import adams.core.logging.LoggingObject;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.OutputTabbedPane;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.Date;

/**
 * Container for an evaluation, model, training set header. Used in the
 * result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ResultItem
  extends LoggingObject {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the timestamp. */
  protected Date m_Timestamp;

  /** the name of the item. */
  protected String m_Name;

  /** the evaluation object. */
  protected Evaluation m_Evaluation;

  /** the model. */
  protected Classifier m_Classifier;

  /** the header. */
  protected Instances m_Header;

  /** the tabbed pane with the generated output. */
  protected OutputTabbedPane m_TabbedPane;

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
    m_Evaluation = evaluation;
    m_Classifier = classifier;
    m_Header     = header;
    m_TabbedPane = new OutputTabbedPane();
    m_TabbedPane.setShowCloseTabButton(true);
    m_TabbedPane.setCloseTabsWithMiddelMouseButton(true);
    m_Timestamp  = new Date();
    m_Name       = DateUtils.getTimeFormatterMsecs().format(m_Timestamp) + " - " + classifier.getClass().getSimpleName();
  }

  /**
   * Returns the name of the item.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
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
   * Returns whether an training set header is present.
   * 
   * @return		true if available
   */
  public boolean hasHeader() {
    return (m_Header != null);
  }

  /**
   * Returns the stored training set header.
   * 
   * @return		the header, null if not present
   */
  public Instances getHeader() {
    return m_Header;
  }

  /**
   * The tabbed pane for the results.
   *
   * @return		the tabbed pane
   */
  public OutputTabbedPane getTabbedPane() {
    return m_TabbedPane;
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
