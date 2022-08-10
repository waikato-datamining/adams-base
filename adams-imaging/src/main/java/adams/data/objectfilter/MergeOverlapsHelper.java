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
 * MergeOverlapsHelper.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfilter;

import adams.core.Utils;
import adams.core.logging.LoggingObject;
import adams.data.objectoverlap.BoundingBoxFallbackSupporter;
import adams.data.objectoverlap.GeometryType;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.objectoverlap.OptionalBoundingBoxFallbackSupporter;
import adams.data.statistics.StatUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Helper class for merging object overlaps.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MergeOverlapsHelper {

  /**
   * Determines the objects to merge.
   *
   * @param obj		the objects for which matches were located
   * @param matches	the matching objects
   * @param merged	the objects that have been merged already
   * @param labelKey	the key in the metadata containing the label
   * @return		the list of objects to merge
   */
  public static List<LocatedObject> findObjectsToMerge(LocatedObject obj, Map<LocatedObject,Double> matches, Set<LocatedObject> merged, String labelKey) {
    List<LocatedObject>		result;
    Set<LocatedObject>		matchesSet;

    result = new ArrayList<>();
    result.add(obj);
    matchesSet = new HashSet<>(matches.keySet());
    matchesSet.remove(obj);
    for (LocatedObject match: matchesSet) {
      // does label match?
      if (!labelKey.isEmpty()) {
	if (!obj.getMetaData().containsKey(labelKey))
	  continue;
	if (!match.getMetaData().containsKey(labelKey))
	  continue;
	if (!obj.getMetaData().get(labelKey).equals(match.getMetaData().get(labelKey)))
	  continue;
      }
      result.add(match);
    }

    return result;
  }

  /**
   * Turns the located object's polygon or bbox into a Geometry instance. Takes potential bbox fallback into account.
   *
   * @param obj		the object to convert
   * @param algorithm 	the overlap algorithm to use
   * @return		the generated Gemoetry
   */
  public static Geometry toGeometry(LocatedObject obj, ObjectOverlap algorithm) {
    Geometry		result;
    GeometryType geometry;
    boolean		fallback;

    if (obj.hasValidPolygon())
      geometry = GeometryType.POLYGON;
    else
      geometry = GeometryType.BBOX;

    if (geometry == GeometryType.POLYGON) {
      if (algorithm instanceof OptionalBoundingBoxFallbackSupporter) {
	fallback = ((OptionalBoundingBoxFallbackSupporter) algorithm).getFallback()
	    && obj.boundingBoxFallback(((OptionalBoundingBoxFallbackSupporter) algorithm).getBoundingBoxFallbackRatio());
	if (fallback)
	  geometry = GeometryType.BBOX;
      }
      else if (algorithm instanceof BoundingBoxFallbackSupporter) {
	fallback = obj.boundingBoxFallback(((BoundingBoxFallbackSupporter) algorithm).getBoundingBoxFallbackRatio());
	if (fallback)
	  geometry = GeometryType.BBOX;
      }
    }

    switch (geometry) {
      case BBOX:
	result = LocatedObject.toGeometry(obj.getRectangle());
	break;
      case POLYGON:
	result = LocatedObject.toGeometry(obj.getPolygon());
	break;
      default:
	throw new IllegalStateException("Unhandled geometry type: " + geometry);
    }

    return result;
  }

  /**
   * Merges the objects into a single on.
   *
   * @param loggingObject 	the project use for logging errors etc, can be null
   * @param objs	the objects to merge
   * @param algorithm 	the overlap algorithm to use
   * @param scoreKey 	the meta-data key with the score
   * @param labelKey 	the meta-data key with the label
   * @param mergedScoreCalculation 	how to calculate the merged score
   * @return		the merged object
   */
  public static List<LocatedObject> mergeObjects(LoggingObject loggingObject, List<LocatedObject> objs, ObjectOverlap algorithm, String scoreKey, String labelKey, MergedScoreCalculation mergedScoreCalculation) {
    List<LocatedObject>	result;
    Geometry 		resultGeo;
    LocatedObject	other;
    Geometry 		otherGeo;
    LocatedObject	merged;
    Geometry 		mergedGeo;
    int			i;
    Polygon bbox;
    Coordinate[]	coords;
    int[]		polyX;
    int[]		polyY;
    int			n;
    double[]		scores;
    double		scoreNew;

    if (objs.size() <= 1)
      return objs;

    result = new ArrayList<>();
    result.add(objs.get(0));

    resultGeo = toGeometry(objs.get(0), algorithm);
    for (i = 1; i < objs.size(); i++) {
      // merge polygons
      other    = objs.get(i);
      otherGeo = toGeometry(other, algorithm);
      try {
	mergedGeo = resultGeo.union(otherGeo);
      }
      catch (Exception e) {
        if (loggingObject != null)
	  loggingObject.getLogger().log(Level.SEVERE, "Failed to combine polygons, keeping object as is: " + other, e);
	result.add(objs.get(i));
	continue;
      }

      // interpret merge
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
	result.set(0, merged);
	resultGeo = mergedGeo;
      }
      else {
	if (loggingObject != null)
	  loggingObject.getLogger().severe("Expected polygon geometry, but got " + Utils.classToString(mergedGeo) + ", leaving unmerged: " + other);
	result.add(objs.get(i));
      }
    }

    // merged score
    scoreNew  = Double.NaN;
    if (!scoreKey.isEmpty()) {
      scores = new double[objs.size()];
      for (i = 0; i < objs.size(); i++)
	scores[i] = (Double) objs.get(i).getMetaData().get(scoreKey);
      switch (mergedScoreCalculation) {
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
	  throw new IllegalStateException("Unhandled score calculation: " + mergedScoreCalculation);
      }
    }

    // meta-data
    for (i = 0; i < result.size(); i++) {
      if (!labelKey.isEmpty() && objs.get(0).getMetaData().containsKey(labelKey))
	result.get(i).getMetaData().put(labelKey, objs.get(0).getMetaData().get(labelKey));
      if (!scoreKey.isEmpty())
	result.get(i).getMetaData().put(scoreKey, scoreNew);
      result.get(i).getMetaData().put("num_merged_objects", objs.size());
      // TODO other meta-data?
    }

    return result;
  }
}
