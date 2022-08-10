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
 * MergeOverlaps.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.overlappingobjectremoval;

import adams.data.objectfilter.MergeOverlapsHelper;
import adams.data.objectfilter.MergedScoreCalculation;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Merges overlapping objects into single one.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MergeOverlaps
    extends AbstractOverlappingObjectRemoval {

  private static final long serialVersionUID = 2003246733816658910L;

  /** the object overlap calculation to use. */
  protected ObjectOverlap m_Algorithm;

  /** the label meta-data key - ignored if empty. */
  protected String m_LabelKey;

  /** the score meta-data key - ignored if empty. */
  protected String m_ScoreKey;

  /** how to calculate the merged score. */
  protected MergedScoreCalculation m_MergedScoreCalculation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges overlapping objects into single one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"algorithm", "algorithm",
	new AreaRatio());

    m_OptionManager.add(
	"label-key", "labelKey",
	"");

    m_OptionManager.add(
	"score-key", "scoreKey",
	"");

    m_OptionManager.add(
	"merged-score-calculation", "mergedScoreCalculation",
	MergedScoreCalculation.MIN);
  }

  /**
   * Sets the algorithm for determining the overlapping objects
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(ObjectOverlap value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The algorithm to use for determining the overlapping objects.";
  }

  /**
   * Sets the (optional) key for a string label in the meta-data. Only objects with matching labels get merged.
   *
   * @param value	the key, ignored if empty
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the (optional) key for a string label in the meta-data. Only objects with matching labels get merged.
   *
   * @return		the key, ignored if empty
   */
  public String getLabelKey() {
    return m_LabelKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelKeyTipText() {
    return "The (optional) key for a string label in the meta-data, only objects with matching labels get merged.";
  }

  /**
   * Sets the (optional) key for a numeric prediction score in the meta-data.
   *
   * @param value	the key, ignored if empty
   */
  public void setScoreKey(String value) {
    m_ScoreKey = value;
    reset();
  }

  /**
   * Returns the (optional) key for a numeric prediction score in the meta-data.
   *
   * @return		the key, ignored if empty
   */
  public String getScoreKey() {
    return m_ScoreKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scoreKeyTipText() {
    return "The (optional) key for a numeric prediction score in the meta-data.";
  }

  /**
   * Sets how to calculate the score for the merged object.
   *
   * @param value	the type of calculation
   */
  public void setMergedScoreCalculation(MergedScoreCalculation value) {
    m_MergedScoreCalculation = value;
    reset();
  }

  /**
   * Returns how to calculate the score for the merged object.
   *
   * @return		the type of calculation
   */
  public MergedScoreCalculation getMergedScoreCalculation() {
    return m_MergedScoreCalculation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mergedScoreCalculationTipText() {
    return "How to calculate the score for the merged object.";
  }

  /**
   * Removes overlapping image objects.
   *
   * @param objects	the objects to clean up
   * @param matches	the matches that were determined by an algorithm, used as basis for removal
   * @return		the updated objects
   */
  @Override
  public LocatedObjects removeOverlaps(LocatedObjects objects, Map<LocatedObject, Map<LocatedObject,Double>> matches) {
    LocatedObjects		result;
    List<LocatedObject> 	toMerge;
    Set<LocatedObject> 		merged;
    List<LocatedObject> 	mergedObjs;

    result  = new LocatedObjects();
    merged  = new HashSet<>();
    for (LocatedObject obj: matches.keySet()) {
      if (merged.contains(obj))
	continue;
      // find/record objects to merge
      toMerge   = MergeOverlapsHelper.findObjectsToMerge(obj, matches.get(obj), merged, m_LabelKey);
      merged.addAll(toMerge);
      // merge objects
      mergedObjs = MergeOverlapsHelper.mergeObjects(this, toMerge, m_Algorithm, m_ScoreKey, m_LabelKey, m_MergedScoreCalculation);
      result.addAll(mergedObjs);
    }
    return result;
  }
}
