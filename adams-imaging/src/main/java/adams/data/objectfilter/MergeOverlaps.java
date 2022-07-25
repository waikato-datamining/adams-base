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
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfilter;

import adams.core.Utils;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.statistics.StatUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Merges overlapping objects into single object.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MergeOverlaps
    extends AbstractObjectFilter {

  private static final long serialVersionUID = 7648379268675475506L;

  /**
   * How to calculate the merged score.
   */
  public enum MergedScoreCalculation {
    MIN,
    MEAN,
    MEDIAN,
    MAX,
  }

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
   * @return a description suitable for displaying in the gui
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
   * Determines the objects to merge.
   *
   * @param obj		the objects for which matches were located
   * @param matches	the matching objects
   * @param merged	the objects that have been merged already
   * @return		the list of objects to merge
   */
  protected List<LocatedObject> findObjectsToMerge(LocatedObject obj, Map<LocatedObject,Double> matches, Set<LocatedObject> merged) {
    List<LocatedObject>		result;
    Set<LocatedObject>		matchesSet;

    result = new ArrayList<>();
    result.add(obj);
    matchesSet = new HashSet<>(matches.keySet());
    matchesSet.remove(obj);
    for (LocatedObject match: matchesSet) {
      // does label match?
      if (!m_LabelKey.isEmpty()) {
	if (!obj.getMetaData().containsKey(m_LabelKey))
	  continue;
	if (!match.getMetaData().containsKey(m_LabelKey))
	  continue;
	if (!obj.getMetaData().get(m_LabelKey).equals(match.getMetaData().get(m_LabelKey)))
	  continue;
      }
      result.add(match);
    }

    return result;
  }

  /**
   * Merges the objects into a single on.
   *
   * @param objs	the objects to merge
   * @return		the merged object
   */
  protected LocatedObject mergeObjects(List<LocatedObject> objs) {
    LocatedObject	result;
    Geometry 		resultGeo;
    LocatedObject	other;
    Geometry 		otherGeo;
    LocatedObject	merged;
    Geometry 		mergedGeo;
    int			i;
    Polygon		bbox;
    Coordinate[]	coords;
    int[]		polyX;
    int[]		polyY;
    int			n;
    double[]		scores;
    double		scoreNew;

    result = objs.get(0);

    if (objs.size() > 1) {
      // merge polygons
      resultGeo = result.toGeometry();
      for (i = 1; i < objs.size(); i++) {
        other     = objs.get(i);
        otherGeo  = other.toGeometry();
        mergedGeo = resultGeo.union(otherGeo);
        if (mergedGeo instanceof Polygon) {
          bbox   = (Polygon) mergedGeo.getEnvelope();
          coords = bbox.getCoordinates();
          // bbox
          merged = new LocatedObject(
              (int) coords[0].x,
	      (int) coords[0].y,
	      (int) (coords[2].x - coords[0].x + 1),
	      (int) (coords[2].y - coords[0].y + 1));
          // polygon
          coords = mergedGeo.getCoordinates();
          polyX = new int[coords.length];
	  polyY = new int[coords.length];
	  for (n = 0; n < coords.length; n++) {
	    polyX[n] = (int) coords[n].x;
	    polyY[n] = (int) coords[n].y;
	  }
          merged.setPolygon(new java.awt.Polygon(polyX, polyY, polyX.length));
	  // for next merge
          result    = merged;
          resultGeo = mergedGeo;
	}
        else {
          getLogger().severe("Expected polygon geometry, but got: " + Utils.classToString(mergedGeo));
	}
      }

      // merged score
      scoreNew  = Double.NaN;
      if (!m_ScoreKey.isEmpty()) {
	scores = new double[objs.size()];
	for (i = 0; i < objs.size(); i++)
	  scores[i] = (Double) objs.get(i).getMetaData().get(m_ScoreKey);
	switch (m_MergedScoreCalculation) {
	  case MIN:
	    scoreNew = StatUtils.min(scores);
	    break;
	  case MEAN:
	    scoreNew = StatUtils.mean(scores);
	    break;
	  case MEDIAN:
	    scoreNew = StatUtils.median(scores);
	    break;
	  case MAX:
	    scoreNew = StatUtils.max(scores);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled score calculation: " + m_MergedScoreCalculation);
	}
      }

      // meta-data
      if (!m_LabelKey.isEmpty() && objs.get(0).getMetaData().containsKey(m_LabelKey))
	result.getMetaData().put(m_LabelKey, objs.get(0).getMetaData().get(m_LabelKey));
      if (!m_ScoreKey.isEmpty())
	result.getMetaData().put(m_ScoreKey, scoreNew);
      result.getMetaData().put("num_merged_objects", objs.size());
      // TODO other meta-data?
    }

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects the objects to filter
   * @return the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects					result;
    Map<LocatedObject, Map<LocatedObject,Double>>	matches;
    List<LocatedObject>					toMerge;
    Set<LocatedObject> 					merged;
    LocatedObject					mergedObj;

    result  = new LocatedObjects();
    matches = m_Algorithm.matches(objects, objects);
    merged  = new HashSet<>();
    for (LocatedObject obj: matches.keySet()) {
      if (merged.contains(obj))
        continue;
      // find/record objects to merge
      toMerge   = findObjectsToMerge(obj, matches.get(obj), merged);
      merged.addAll(toMerge);
      // merge objects
      mergedObj = mergeObjects(toMerge);
      result.add(mergedObj);
    }
    return result;
  }
}
