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
 * IntersectOverUnionRatio.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.data.objectoverlap;

import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Computes the Intersect Over Union (IOU) between annotations and predictions.
 *
 * @author Hisham (habdelqa at waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IntersectOverUnionRatio
  extends AbstractObjectOverlap
  implements LabelAwareObjectOverlap{

  private static final long serialVersionUID = 490981014539796857L;

  /** the highest IOU percentage. */
  public final static String IOU_PERCENTAGE_HIGHEST = "iou_highest";

  /** the label of the highest IOU. */
  public final static String IOU_LABEL_HIGHEST = "iou_label_highest";

  /** whether the labels of the highest IOU match. */
  public final static String IOU_LABEL_HIGHEST_MATCH = "iou_label_highest_match";

  /** the IOU count. */
  public final static String IOU_COUNT = "iou_count";

  /** the minimum IOU ratio to use. */
  protected double m_MinIntersectOverUnionRatio;

  /** the label meta-data key - ignored if empty. */
  protected String m_LabelKey;

  /** whether to use the other object in the output in case of an overlap. */
  protected boolean m_UseOtherObject;

  /** whether to check for additional predicted objects not present in actual. */
  protected boolean m_AdditionalObject;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the Intersect Over Union (IOU) between annotations and predictions.\n"
      + "It stores the IOU percentage of the highest IOU found (" + IOU_PERCENTAGE_HIGHEST + ") and the "
      + "total number of IOU greater than the specified minimum (" + IOU_COUNT + ").\n"
      + "If a label key (located object meta-data) has been supplied, then the label of the object with "
      + "the highest IOU gets stored as well (" + IOU_LABEL_HIGHEST + ") and whether the "
      + "labels match (" + IOU_LABEL_HIGHEST_MATCH + ")";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-iou-ratio", "minIntersectOverUnionRatio",
      0.0, 0.0, 1.0);

    m_OptionManager.add(
      "label-key", "labelKey",
      "");

    m_OptionManager.add(
      "use-other-object", "useOtherObject",
      false);

    m_OptionManager.add(
      "additional-object", "additionalObject",
      false);
  }

  /**
   * Sets the minimum IOU ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setMinIntersectOverUnionRatio(double value) {
    if (getOptionManager().isValid("minIntersectOverUnionRatio", value)) {
      m_MinIntersectOverUnionRatio = value;
      reset();
    }
  }

  /**
   * Returns the minimum IOU ratio to use.
   *
   * @return 		the minimum ratio
   */
  public double getMinIntersectOverUnionRatio() {
    return m_MinIntersectOverUnionRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minIntersectOverUnionRatioTipText() {
    return "The minimum ratio that an IOU must have before being considered an actual overlap.";
  }

  /**
   * Sets the (optional) key for a string label in the meta-data; if supplied
   * the value of the object with the highest overlap gets stored in the
   * report using {@link #IOU_LABEL_HIGHEST}, {@link #IOU_LABEL_HIGHEST_MATCH}
   * stores whether the labels match.
   *
   * @param value	the key, ignored if empty
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the (optional) key for a string label in the meta-data; if supplied
   * the value of the object with the highest overlap gets stored in the
   * report using {@link #IOU_LABEL_HIGHEST}, {@link #IOU_LABEL_HIGHEST_MATCH}
   * stores whether the labels match.
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
    return "The (optional) key for a string label in the meta-data; if supplied "
      + "the value of the object with the highest IOU gets stored in the "
      + "report using " + IOU_LABEL_HIGHEST + ", "
      + IOU_LABEL_HIGHEST_MATCH + " stores whether the labels match.";
  }

  /**
   * Sets whether to use/forward other object data.
   *
   * @param value	true if to use other object
   */
  public void setUseOtherObject(boolean value) {
    m_UseOtherObject = value;
    reset();
  }

  /**
   * Returns whether to use/forward other object data.
   *
   * @return		true if to use other object
   */
  public boolean getUseOtherObject() {
    return m_UseOtherObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useOtherObjectTipText() {
    return "If enabled, the object data from the other report is used/forwarded in case of an overlap.";
  }

  /**
   * Sets whether to count additional predicted objects.
   *
   * @param value	true if to count additional predicted objects
   */
  public void setAdditionalObject(boolean value) {
    m_AdditionalObject = value;
    reset();
  }

  /**
   * Returns whether to count additional predicted objects.
   *
   * @return		true if to count additional predicted objects
   */
  public boolean getAdditionalObject() {
    return m_AdditionalObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalObjectTipText() {
    return "If enabled, the additional predicted objects not present in actual objects will be checked.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result = QuickInfoHelper.toString(this, "minIntersectOverUnionRatio", m_MinIntersectOverUnionRatio, "ratio: ");
    result += QuickInfoHelper.toString(this, "labelKey", (m_LabelKey.isEmpty() ? "-none-" : m_LabelKey), ", label key: ");
    result += QuickInfoHelper.toString(this, "useOtherObject", m_UseOtherObject, "use other obj", ", ");
    result += QuickInfoHelper.toString(this, "additionalObject", m_AdditionalObject, "additional obj", ", ");

    return result;
  }

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  @Override
  protected LocatedObjects doCalculate(LocatedObjects annotations, LocatedObjects predictions, Map<LocatedObject, Set<LocatedObject>> matches) {
    LocatedObjects 	result;
    int			count;
    double		iouHighest;
    String		labelHighest;
    String		thisLabel;
    LocatedObject 	actObj;
    LocatedObject	tmpObj;
    LocatedObject	otherObjectHighest;
    double		ratio;
    double		thisObjArea;
    double		intersectArea;
    double		otherObjArea;
    double		iou;

    result = new LocatedObjects();
    if (annotations.size() == 0) {
      result = predictions;
    }
    else {
      Set<LocatedObject> matchingObjects = new HashSet<>();
      for (LocatedObject thisObj : annotations) {
	count          = 0;
	iouHighest     = 0.0;
	labelHighest   = UNKNOWN_LABEL;
	thisLabel      = UNKNOWN_LABEL;
	if (!m_LabelKey.isEmpty() && thisObj.getMetaData().containsKey(m_LabelKey))
	  thisLabel = "" + thisObj.getMetaData().get(m_LabelKey);
	actObj = thisObj;
	otherObjectHighest = null;
	for (LocatedObject otherObj : predictions) {
	  initMatch(matches, thisObj);
	  if (m_ExcludeIdentical && thisObj.equals(otherObj))
	    continue;
	  ratio = thisObj.overlapRatio(otherObj);
	  thisObjArea = thisObj.getHeight() * thisObj.getWidth();
	  intersectArea = thisObjArea * ratio;
	  otherObjArea = otherObj.getHeight() * otherObj.getWidth();
	  iou = intersectArea / (thisObjArea + otherObjArea - intersectArea);
	  if (isLoggingEnabled())
	    getLogger().info(thisObj + " : " + otherObj + " -> IOU = " + iou);
	  if (iou >= m_MinIntersectOverUnionRatio) {
	    count++;
	    if (iou > iouHighest) {
	      addMatch(matches, thisObj, otherObj);
	      if (m_UseOtherObject) {
	        tmpObj   = actObj;
		actObj   = otherObj;
		otherObj = tmpObj;
	      }
	      iouHighest = iou;
	      otherObjectHighest = otherObj;
	      if (!m_LabelKey.isEmpty()) {
		if (otherObj.getMetaData().containsKey(m_LabelKey)) {
		  labelHighest = "" + otherObj.getMetaData().get(m_LabelKey);
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
	if (!m_LabelKey.isEmpty()) {
	  actObj.getMetaData().put(IOU_LABEL_HIGHEST, labelHighest);
	  actObj.getMetaData().put(IOU_LABEL_HIGHEST_MATCH, thisLabel.equals(labelHighest));
	}
	if (m_AdditionalObject)
	  actObj.getMetaData().put(ADDITIONAL_OBJ, false);
	if (m_CopyMetaData && (otherObjectHighest != null))
	  copyMetaData(otherObjectHighest, actObj);
	result.add(actObj);
      }

      if (m_AdditionalObject) {
	for (LocatedObject otherObj : predictions) {
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
   * Splits the overlapping objects into subsets of matching labels and mismatching ones.
   *
   * @param overlaps	all overlaps, to split
   * @return		split into matching/mismatching subsets
   */
  public Struct2<LocatedObjects,LocatedObjects> splitOverlaps(LocatedObjects overlaps) {
    LocatedObjects	match;
    LocatedObjects	mismatch;

    match    = new LocatedObjects();
    mismatch = new LocatedObjects();
    for (LocatedObject overlap: overlaps) {
      if (overlap.getMetaData().getOrDefault(IOU_LABEL_HIGHEST_MATCH, false).toString().equalsIgnoreCase("true"))
        match.add(overlap.getClone());
      else
        mismatch.add(overlap.getClone());
    }

    return new Struct2<>(match, mismatch);
  }
}
