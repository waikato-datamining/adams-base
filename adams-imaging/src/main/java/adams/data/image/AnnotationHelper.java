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
 * AnnotationHelper.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.image;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.HashSet;
import java.util.Set;

/**
 * Methods for managing annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AnnotationHelper {

  /** the highest overlap percentage. */
  public final static String OVERLAP_PERCENTAGE_HIGHEST = "overlap_highest";

  /** the label of the highest overlap. */
  public final static String OVERLAP_LABEL_HIGHEST = "overlap_label_highest";

  /** whether the labels of the highest overlap match. */
  public final static String OVERLAP_LABEL_HIGHEST_MATCH = "overlap_label_highest_match";

  /** the overlap count. */
  public final static String OVERLAP_COUNT = "overlap_count";

  /** the additional objects boolean. */
  public final static String ADDITIONAL_OBJ = "additional_object";

  /** the placeholder for unknown label. */
  public static final String UNKNOWN_LABEL = "???";

  /** the highest iou percentage. */
  public final static String IOU_PERCENTAGE_HIGHEST = "iou_highest";

  /** the label of the highest iou. */
  public final static String IOU_LABEL_HIGHEST = "iou_label_highest";

  /** whether the labels of the highest iou match. */
  public final static String IOU_LABEL_HIGHEST_MATCH = "iou_label_highest_match";

  /** the iou count. */
  public final static String IOU_COUNT = "iou_count";

  /**
   * Finds the overlapping objects between "thisObjs" (ground truth) and
   * "otherObjs" (predictions). Uses area ratio.
   *
   * @param thisObjs	the ground truth
   * @param otherObjs	the predictions
   * @param minOverlap	the minimum overlap ratio (0-1)
   * @param labelKey 	the key in the object's meta-data with the label
   * @param useOther	whether to use the other object in the output in case of an overlap
   * @param additional 	whether to check for additional predicted objects not present in actual
   * @param avgRatio 	whether to use average overlap ratio instead of just this->other
   * @return		the overlaps
   */
  public static LocatedObjects imageOverlap(LocatedObjects thisObjs, LocatedObjects otherObjs, double minOverlap, String labelKey, boolean useOther, boolean additional, boolean avgRatio) {
    LocatedObjects 	result;
    int			count;
    double		overlapHighest;
    String		labelHighest;
    String		thisLabel;
    LocatedObject	actObj;
    double		ratio;
    double		ratio2;

    result = new LocatedObjects();
    if (thisObjs.size() == 0) {
      result = otherObjs;
    }
    else {
      Set<LocatedObject> matchingObjects = new HashSet<>();
      for (LocatedObject thisObj : thisObjs) {
	count          = 0;
	overlapHighest = 0.0;
	labelHighest   = UNKNOWN_LABEL;
	thisLabel      = UNKNOWN_LABEL;
	if (!labelKey.isEmpty() && thisObj.getMetaData().containsKey(labelKey))
	  thisLabel = "" + thisObj.getMetaData().get(labelKey);
	actObj = thisObj;
	for (LocatedObject otherObj : otherObjs) {
	  ratio = thisObj.overlapRatio(otherObj);
	  if (avgRatio) {
	    ratio2 = otherObj.overlapRatio(thisObj);
	    ratio = (ratio + ratio2) / 2;
	  }
	  if (ratio >= minOverlap) {
	    count++;
	    if (ratio > overlapHighest) {
	      if (useOther)
		actObj = otherObj;
	      overlapHighest = ratio;
	      if (!labelKey.isEmpty()) {
		if (otherObj.getMetaData().containsKey(labelKey)) {
		  labelHighest = "" + otherObj.getMetaData().get(labelKey);
		  matchingObjects.add(otherObj);
		}
		else
		  labelHighest = UNKNOWN_LABEL;
	      }
	      else {
		matchingObjects.add(otherObj);
	      }
	    }
	  }
	}
	actObj = actObj.getClone();
	actObj.getMetaData().put(OVERLAP_COUNT, count);
	actObj.getMetaData().put(OVERLAP_PERCENTAGE_HIGHEST, overlapHighest);
	if (!labelKey.isEmpty()) {
	  actObj.getMetaData().put(OVERLAP_LABEL_HIGHEST, labelHighest);
	  actObj.getMetaData().put(OVERLAP_LABEL_HIGHEST_MATCH, thisLabel.equals(labelHighest));
	}
	if (additional)
	  actObj.getMetaData().put(ADDITIONAL_OBJ, false);
	result.add(actObj);
      }

      if (additional) {
	for (LocatedObject otherObj : otherObjs) {
	  if (!matchingObjects.contains(otherObj)) {
	    otherObj = otherObj.getClone();
	    otherObj.getMetaData().put(ADDITIONAL_OBJ, true);
	    result.add(otherObj);
	  }
	}
      }
    }

    return result;
  }

  /**
   * Finds the overlapping objects between "thisObjs" (ground truth) and
   * "otherObjs" (predictions). Uses intersect/union ratio.
   *
   * @param thisObjs	the ground truth
   * @param otherObjs	the predictions
   * @param minIOU	the minimum iou ratio to use (0-1)
   * @param labelKey 	the key in the object's meta-data with the label
   * @param useOther	whether to use the other object in the output in case of an overlap
   * @param additional 	whether to check for additional predicted objects not present in actual
   * @return		the overlaps
   */
  public static LocatedObjects intersectOverUnion(LocatedObjects thisObjs, LocatedObjects otherObjs, double minIOU, String labelKey, boolean useOther, boolean additional) {
    LocatedObjects 	result;
    int			count;
    double		iouHighest;
    String		labelHighest;
    String		thisLabel;
    LocatedObject	actObj;
    double		ratio;
    double		thisObjArea;
    double		intersectArea;
    double		otherObjArea;
    double		iou;

    result = new LocatedObjects();
    if (thisObjs.size() == 0) {
      result = otherObjs;
    }
    else {
      Set<LocatedObject> matchingObjects = new HashSet<>();
      for (LocatedObject thisObj : thisObjs) {
	count          = 0;
	iouHighest     = 0.0;
	labelHighest   = UNKNOWN_LABEL;
	thisLabel      = UNKNOWN_LABEL;
	if (!labelKey.isEmpty() && thisObj.getMetaData().containsKey(labelKey))
	  thisLabel = "" + thisObj.getMetaData().get(labelKey);
	actObj = thisObj;
	for (LocatedObject otherObj : otherObjs) {
	  ratio = thisObj.overlapRatio(otherObj);
	  thisObjArea = thisObj.getHeight() * thisObj.getWidth();
	  intersectArea = thisObjArea * ratio;
	  otherObjArea = otherObj.getHeight() * otherObj.getWidth();
	  iou = intersectArea / (thisObjArea + otherObjArea - intersectArea);
	  if (iou >= minIOU) {
	    count++;
	    if (iou > iouHighest) {
	      if (useOther)
		actObj = otherObj;
	      iouHighest = iou;
	      if (!labelKey.isEmpty()) {
		if (otherObj.getMetaData().containsKey(labelKey)) {
		  labelHighest = "" + otherObj.getMetaData().get(labelKey);
		  matchingObjects.add(otherObj);
		}
		else
		  labelHighest = UNKNOWN_LABEL;
	      }
	      else {
		matchingObjects.add(otherObj);
	      }
	    }
	  }
	}
	actObj = actObj.getClone();
	actObj.getMetaData().put(IOU_COUNT, count);
	actObj.getMetaData().put(IOU_PERCENTAGE_HIGHEST, iouHighest);
	if (!labelKey.isEmpty()) {
	  actObj.getMetaData().put(IOU_LABEL_HIGHEST, labelHighest);
	  actObj.getMetaData().put(IOU_LABEL_HIGHEST_MATCH, thisLabel.equals(labelHighest));
	}
	if (additional)
	  actObj.getMetaData().put(ADDITIONAL_OBJ, false);
	result.add(actObj);
      }

      if (additional) {
	for (LocatedObject otherObj : otherObjs) {
	  if (!matchingObjects.contains(otherObj)) {
	    otherObj = otherObj.getClone();
	    otherObj.getMetaData().put(ADDITIONAL_OBJ, true);
	    result.add(otherObj);
	  }
	}
      }
    }

    return result;
  }
}
