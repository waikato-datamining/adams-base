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

package adams.gui.tools.wekainvestigator.tab.attseltab;

import adams.core.DateUtils;
import adams.core.Shortening;
import adams.data.spreadsheet.MetaData;
import adams.gui.tools.wekainvestigator.output.AbstractResultItem;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.core.Instances;

/**
 * Container for an attribute selection, evaluator and search method. Used in the
 * result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ResultItem
  extends AbstractResultItem {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the attsel object. */
  protected AttributeSelection m_AttributeSelection;

  /** the search algorithm. */
  protected ASSearch m_Search;

  /** the evaluation algorithm. */
  protected ASEvaluation m_Evaluator;

  /** the number of folds (in case of cross-validation). */
  protected int m_Folds;

  /** the full dataset. */
  protected Instances m_Full;

  /** the run information. */
  protected MetaData m_RunInformation;

  /**
   * Initializes the item.
   *
   * @param evaluator	the evaluation algorithm
   * @param search	the search algorithm
   * @param header	the header of the training set
   */
  public ResultItem(ASEvaluation evaluator, ASSearch search, Instances header) {
    super(header);

    if (evaluator == null)
      throw new IllegalArgumentException("Evaluator cannot be null!");
    if (search == null)
      throw new IllegalArgumentException("Search cannot be null!");

    m_Search    = search;
    m_Evaluator = evaluator;
  }

  /**
   * Creates the name from the members.
   *
   * @return		the name
   */
  protected String createName() {
    return DateUtils.getTimeFormatterMsecs().format(m_Timestamp)
      + " - "
      + m_Search.getClass().getSimpleName()  + "/" + m_Evaluator.getClass().getSimpleName()
      + " - "
      + Shortening.shortenEnd(m_Header.relationName(), MAX_RELATIONNAME_LENGTH);
  }

  /**
   * Updates the item.
   *
   * @param attsel	the attribute selection
   * @return		itself
   */
  public ResultItem update(AttributeSelection attsel) {
    m_AttributeSelection = attsel;
    invalidateName();
    return this;
  }

  /**
   * Updates the item.
   *
   * @param full	the full dataset
   * @return		itself
   */
  public ResultItem update(Instances full) {
    m_Full = full;
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
   * @param folds	the number of folds, ignored if < 2
   * @return		itself
   */
  public ResultItem update(int folds) {
    m_Folds = folds;
    invalidateName();
    return this;
  }

  /**
   * Returns the stored AttributeSelection object.
   * 
   * @return		the attsel, null if not present
   */
  public AttributeSelection getAttributeSelection() {
    return m_AttributeSelection;
  }

  /**
   * Returns the stored search object.
   *
   * @return		the search, null if not present
   */
  public ASSearch getSearch() {
    return m_Search;
  }

  /**
   * Returns the stored evaluator object.
   *
   * @return		the evaluator, null if not present
   */
  public ASEvaluation getEvaluator() {
    return m_Evaluator;
  }

  /**
   * Returns whether cross-valiation was used.
   *
   * @return		true if cross-validation
   */
  public boolean isCrossValidation() {
    return (m_Folds >= 2);
  }

  /**
   * Returns the number of folds.
   *
   * @return		the number of folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Checks whether a full dataset is present.
   *
   * @return		true if available
   */
  public boolean hasFull() {
    return (m_Full != null);
  }

  /**
   * Returns the full dataset if present.
   *
   * @return		the dataset, null if not available
   */
  public Instances getFull() {
    return m_Full;
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
    result += ", CV=" + isCrossValidation()
      + ", full=" + hasFull()
      + ", runInfo=" + hasRunInformation()
      + ", header=" + hasHeader();

    return result;
  }
}
