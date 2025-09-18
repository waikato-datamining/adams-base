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
 * DiceCoefficient.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.data.objectoverlap;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import com.github.fracpete.javautils.struct.Struct2;
import org.locationtech.jts.geom.Polygon;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Computes the Dice coefficient between annotations and predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DiceCoefficient
    extends AbstractObjectOverlap
    implements LabelAwareObjectOverlap, OptionalBoundingBoxFallbackSupporter {

  private static final long serialVersionUID = 490981014539796857L;

  /** the highest DICE percentage. */
  public final static String DICE_PERCENTAGE_HIGHEST = "dice_highest";

  /** the label of the highest DICE. */
  public final static String DICE_LABEL_HIGHEST = "dice_label_highest";

  /** whether the labels of the highest DICE match. */
  public final static String DICE_LABEL_HIGHEST_MATCH = "dice_label_highest_match";

  /** the DICE count. */
  public final static String DICE_COUNT = "dice_count";

  /** the minimum DICE ratio to use. */
  protected double m_MinDice;

  /** the label meta-data key - ignored if empty. */
  protected String m_LabelKey;

  /** whether to use the other object in the output in case of an overlap. */
  protected boolean m_UseOtherObject;

  /** whether to check for additional predicted objects not present in actual. */
  protected boolean m_AdditionalObject;

  /** the geometry to use. */
  protected GeometryType m_Geometry;

  /** whether to fallback on bounding box. */
  protected boolean m_Fallback;

  /** the ratio used for determining whether to fall back from polygon on bbox. */
  protected double m_BoundingBoxFallbackRatio;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the Dice coefficient between annotations and predictions.\n"
	+ "It stores the Dice percentage of the highest Dice found (" + DICE_PERCENTAGE_HIGHEST + ") and the "
	+ "total number of Dice greater than the specified minimum (" + DICE_COUNT + ").\n"
	+ "If a label key (located object meta-data) has been supplied, then the label of the object with "
	+ "the highest Dice gets stored as well (" + DICE_LABEL_HIGHEST + ") and whether the "
	+ "labels match (" + DICE_LABEL_HIGHEST_MATCH + ")";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"min-dice-ratio", "minDice",
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

    m_OptionManager.add(
	"geometry", "geometry",
	GeometryType.BBOX);

    m_OptionManager.add(
	"fallback", "fallback",
	true);

    m_OptionManager.add(
	"bounding-box-fallback-ratio", "boundingBoxFallbackRatio",
	0.0, 0.0, 1.0);
  }

  /**
   * Sets the minimum DICE ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setMinDice(double value) {
    if (getOptionManager().isValid("minDice", value)) {
      m_MinDice = value;
      reset();
    }
  }

  /**
   * Returns the minimum DICE ratio to use.
   *
   * @return 		the minimum ratio
   */
  public double getMinDice() {
    return m_MinDice;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minDiceTipText() {
    return "The minimum the Dice coefficient must have before being considered an actual overlap.";
  }

  /**
   * Sets the (optional) key for a string label in the meta-data; if supplied
   * the value of the object with the highest overlap gets stored in the
   * report using {@link #DICE_LABEL_HIGHEST}, {@link #DICE_LABEL_HIGHEST_MATCH}
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
   * report using {@link #DICE_LABEL_HIGHEST}, {@link #DICE_LABEL_HIGHEST_MATCH}
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
	+ "the value of the object with the highest Dice gets stored in the "
	+ "report using " + DICE_LABEL_HIGHEST + ", "
	+ DICE_LABEL_HIGHEST_MATCH + " stores whether the labels match.";
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
   * Sets the geometry type to use for the calculations.
   *
   * @param value 	the type
   */
  public void setGeometry(GeometryType value) {
    m_Geometry = value;
    reset();
  }

  /**
   * Returns the geometry type to use for the calculations.
   *
   * @return 		the type
   */
  public GeometryType getGeometry() {
    return m_Geometry;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String geometryTipText() {
    return "The type of geometry to use for the calculations.";
  }

  /**
   * Sets whether to fall back on the bounding box if no polygon available.
   *
   * @param value 	true if to use
   */
  public void setFallback(boolean value) {
    m_Fallback = value;
    reset();
  }

  /**
   * Returns whether to fall back on the bounding box if no polygon available.
   *
   * @return 		true if to use
   */
  public boolean getFallback() {
    return m_Fallback;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fallbackTipText() {
    return "Whether to fall back on the bounding box if no polygon available.";
  }

  /**
   * Sets the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @param value 	the ratio
   */
  public void setBoundingBoxFallbackRatio(double value) {
    if (getOptionManager().isValid("boundingBoxFallbackRatio", value)) {
      m_BoundingBoxFallbackRatio = value;
      reset();
    }
  }

  /**
   * Returns the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @return 		the ratio
   */
  public double getBoundingBoxFallbackRatio() {
    return m_BoundingBoxFallbackRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String boundingBoxFallbackRatioTipText() {
    return "The threshold for the ratio between the areas (shape / bbox), below which the bounding box is used over the polygon (ie bad masks/shapes).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result = QuickInfoHelper.toString(this, "minDice", m_MinDice, "ratio: ");
    result += QuickInfoHelper.toString(this, "labelKey", (m_LabelKey.isEmpty() ? "-none-" : m_LabelKey), ", label key: ");
    result += QuickInfoHelper.toString(this, "useOtherObject", m_UseOtherObject, "use other obj", ", ");
    result += QuickInfoHelper.toString(this, "additionalObject", m_AdditionalObject, "additional obj", ", ");
    result += QuickInfoHelper.toString(this, "geometry", m_Geometry, ", geometry: ");
    if (m_Fallback)
      result += QuickInfoHelper.toString(this, "boundingBoxFallBackRatio", m_BoundingBoxFallbackRatio, ", bbox fallback ratio: ");

    return result;
  }

  /**
   * Calculates the Dice for the two objects.
   *
   * @param thisObj	first object
   * @param otherObj	second object
   * @return		the Dice
   */
  protected double calculateDice(LocatedObject thisObj, LocatedObject otherObj) {
    double 		result;
    GeometryType	geometry;
    boolean		fallback;
    double		ratio;
    double		thisObjArea;
    double		intersectArea;
    double		otherObjArea;
    Polygon		thisPoly;
    Polygon		otherPoly;
    boolean		calculated;

    result = 0.0;

    geometry = m_Geometry;
    fallback = m_Fallback && (thisObj.boundingBoxFallback(m_BoundingBoxFallbackRatio) || otherObj.boundingBoxFallback(m_BoundingBoxFallbackRatio));
    if (fallback)
      geometry = GeometryType.BBOX;

    calculated = false;
    if (geometry == GeometryType.POLYGON) {
      try {
	thisPoly  = thisObj.toGeometry();
	otherPoly = otherObj.toGeometry();
	if (thisPoly.intersects(otherPoly)) {
	  thisObjArea   = thisObj.toGeometry().getArea();
	  otherObjArea  = otherObj.toGeometry().getArea();
	  intersectArea = thisPoly.intersection(otherPoly).getArea();
	  result        = 2*intersectArea / (thisObjArea + otherObjArea);
	}
	calculated = true;
      }
      catch (Exception e) {
        geometry = GeometryType.BBOX;
      }
    }

    if (geometry == GeometryType.BBOX) {
      ratio         = thisObj.overlapRatio(otherObj);
      thisObjArea   = thisObj.getHeight() * thisObj.getWidth();
      intersectArea = thisObjArea * ratio;
      otherObjArea  = otherObj.getHeight() * otherObj.getWidth();
      result        = 2*intersectArea / (thisObjArea + otherObjArea);
      calculated    = true;
    }

    if (!calculated)
      throw new IllegalStateException("Unhandled geometry type: " + m_Geometry);

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
  protected LocatedObjects doCalculate(LocatedObjects annotations, LocatedObjects predictions, Map<LocatedObject, Map<LocatedObject,Double>> matches) {
    LocatedObjects 	result;
    int			count;
    double 		diceHighest;
    String		labelHighest;
    String		thisLabel;
    LocatedObject 	actObj;
    LocatedObject	tmpObj;
    LocatedObject	otherObjectHighest;
    double 		dice;

    if (isLoggingEnabled()) {
      getLogger().info("# annotations: " + annotations.size());
      getLogger().info("# predictions: " + predictions.size());
    }

    result = new LocatedObjects();
    if (annotations.isEmpty()) {
      result = predictions;
    }
    else {
      Set<LocatedObject> matchingObjects = new HashSet<>();
      for (LocatedObject thisObj : annotations) {
	if (isLoggingEnabled()) {
	  if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	    getLogger().fine("this: " + thisObj + " (" + thisObj.getMetaData() + ")");
	  else
	    getLogger().info("this: " + thisObj);
	}
	count          = 0;
	diceHighest = 0.0;
	labelHighest   = UNKNOWN_LABEL;
	thisLabel      = UNKNOWN_LABEL;
	if (!m_LabelKey.isEmpty() && thisObj.getMetaData().containsKey(m_LabelKey))
	  thisLabel = "" + thisObj.getMetaData().get(m_LabelKey);
	actObj = thisObj;
	otherObjectHighest = null;
	initMatch(matches, thisObj);
	for (LocatedObject otherObj : predictions) {
	  if (m_ExcludeIdentical && thisObj.equals(otherObj))
	    continue;
	  dice = calculateDice(thisObj, otherObj);
	  if (isLoggingEnabled()) {
	    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	      getLogger().fine(" + other: " + otherObj + " (" + otherObj.getMetaData() + ")" + " -> Dice = " + dice);
	    else
	      getLogger().info(" + other: " + otherObj + " -> Dice = " + dice);
	    getLogger().info("Dice (" + dice + ") >= min Dice (" + m_MinDice + ") = " + (dice >= m_MinDice));
	  }
	  if (dice >= m_MinDice) {
	    count++;
	    addMatch(matches, thisObj, otherObj, dice);
	    if (dice > diceHighest) {
	      tmpObj = null;
	      if (m_UseOtherObject) {
		tmpObj = actObj;
		actObj = otherObj;
	      }
	      diceHighest = dice;
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
	      if (m_UseOtherObject)
		otherObj = tmpObj;
	    }
	  }
	}
	actObj = actObj.getClone();
	actObj.getMetaData().put(DICE_COUNT, count);
	actObj.getMetaData().put(DICE_PERCENTAGE_HIGHEST, diceHighest);
	if (!m_LabelKey.isEmpty()) {
	  actObj.getMetaData().put(DICE_LABEL_HIGHEST, labelHighest);
	  actObj.getMetaData().put(DICE_LABEL_HIGHEST_MATCH, thisLabel.equals(labelHighest));
	}
	if (m_AdditionalObject)
	  actObj.getMetaData().put(ADDITIONAL_OBJ, false);
	if (m_CopyMetaData && (otherObjectHighest != null))
	  copyMetaData(otherObjectHighest, actObj);
	result.add(actObj);
	if (isLoggingEnabled()) {
	  getLogger().info(DICE_COUNT + ": " + count);
	  getLogger().info(DICE_PERCENTAGE_HIGHEST + ": " + diceHighest);
	  if (!m_LabelKey.isEmpty()) {
	    getLogger().info(DICE_LABEL_HIGHEST + ": " + labelHighest);
	    getLogger().info(DICE_LABEL_HIGHEST_MATCH + ", " + thisLabel.equals(labelHighest));
	  }
	}
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
      if (overlap.getMetaData().getOrDefault(DICE_LABEL_HIGHEST_MATCH, false).toString().equalsIgnoreCase("true"))
	match.add(overlap.getClone());
      else
	mismatch.add(overlap.getClone());
    }

    return new Struct2<>(match, mismatch);
  }
}
