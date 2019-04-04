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
 * VotedPairs.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaclassifiersetupprocessor;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Vote;
import weka.core.SelectedTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates an array of classifiers that contains the original ones, but also
 * all possible classifier pairs encapsulated in the Vote meta-classifier.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class VotedPairs
  extends AbstractClassifierSetupProcessor {

  private static final long serialVersionUID = -2999477464567917216L;

  /**
   * How the voting is done.
   */
  public enum VotingType {
    AVERAGE_RULE(Vote.AVERAGE_RULE),
    PRODUCT_RULE(Vote.PRODUCT_RULE),
    MAJORITY_VOTING_RULE(Vote.MAJORITY_VOTING_RULE),
    MIN_RULE(Vote.MIN_RULE),
    MAX_RULE(Vote.MAX_RULE),
    MEDIAN_RULE(Vote.MEDIAN_RULE);

    /** the corresponding Vote type. */
    private int m_Type;

    private VotingType(int type) {
      m_Type = type;
    }

    /**
     * Returns the corresponding Vote type.
     *
     * @return		the type
     */
    public int getType() {
      return m_Type;
    }

    /**
     * Returns the corresponding Vote selected tag.
     *
     * @return		the tag
     */
    public SelectedTag getSelectedTag() {
      return new SelectedTag(m_Type, Vote.TAGS_RULES);
    }
  }

  /** the type of voting to perform . */
  protected VotingType m_VotingType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an array of classifiers that contains the original ones, "
      + "but also all possible classifier pairs encapsulated in the "
      + Utils.classToString(Vote.class) + " meta-classifier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "voting-type", "votingType",
      VotingType.AVERAGE_RULE);
  }

  /**
   * Sets the type of voting to use.
   *
   * @param value	the type
   */
  public void setVotingType(VotingType value){
    m_VotingType = value;
    reset();
  }

  /**
   * Returns the type of voting in use.
   *
   * @return		the type
   */
  public VotingType getVotingType(){
    return m_VotingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String votingTypeTipText() {
    return "The type of voting to use.";
  }

  /**
   * Processes the classifier array.
   *
   * @param classifiers	the classifiers to process
   * @return		the processed classifiers
   */
  @Override
  protected Classifier[] doProcess(Classifier[] classifiers) {
    List<Classifier> 	result;
    int			i;
    int			n;
    Vote		vote;

    result = new ArrayList<>();

    // input
    for (Classifier cls: classifiers)
      result.add(ObjectCopyHelper.copyObject(cls));

    // create pairs
    for (i = 0; i < classifiers.length - 1; i++) {
      for (n = i + 1; n < classifiers.length; n++) {
        vote = new Vote();
        vote.setClassifiers(new Classifier[]{
          ObjectCopyHelper.copyObject(classifiers[i]),
          ObjectCopyHelper.copyObject(classifiers[n]),
	});
        vote.setCombinationRule(m_VotingType.getSelectedTag());
        result.add(vote);
      }
    }

    return result.toArray(new Classifier[0]);
  }
}
