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
 * AbstractObjectOverlap.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.objectoverlap;

import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.HashMap;
import java.util.Map;

/**
 * Ancestor for schemes that calculate image overlaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectOverlap
    extends AbstractOptionHandler
    implements ObjectOverlap {

  private static final long serialVersionUID = -6700493470621873334L;

  /** whether to skip identical objects, i.e., not count them as overlaps. */
  protected boolean m_ExcludeIdentical;

  /** whether to copy meta-data. */
  protected boolean m_CopyMetaData;

  /** the meta-data keys to copy. */
  protected BaseString[] m_MetaDataKeys;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"exclude-identical", "excludeIdentical",
	false);

    m_OptionManager.add(
	"copy-meta-data", "copyMetaData",
	false);

    m_OptionManager.add(
	"meta-data-key", "metaDataKeys",
	new BaseString[0]);
  }

  /**
   * Sets whether to exclude identical objects from the comparison.
   *
   * @param value	true if to exclude
   */
  public void setExcludeIdentical(boolean value) {
    m_ExcludeIdentical = value;
    reset();
  }

  /**
   * Returns whether to exclude identical objects from the comparison.
   *
   * @return		true if to exclude
   */
  public boolean getExcludeIdentical() {
    return m_ExcludeIdentical;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String excludeIdenticalTipText() {
    return "If enabled, identical objects are not compared with each other; "
	+ "e.g., when looking for overlaps within the same set of objects rather "
	+ "than a different set.";
  }

  /**
   * Sets whether to copy meta-data values across.
   *
   * @param value	true if to copy
   */
  public void setCopyMetaData(boolean value) {
    m_CopyMetaData = value;
    reset();
  }

  /**
   * Returns whether to copy meta-data values across.
   *
   * @return		true if to copy
   */
  public boolean getCopyMetaData() {
    return m_CopyMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String copyMetaDataTipText() {
    return "If enabled, the specified meta-data values get copied across to the "
	+ "object that gets returned.";
  }

  /**
   * Sets the keys of the meta-data values to copy across.
   *
   * @param value	the keys
   */
  public void setMetaDataKeys(BaseString[] value) {
    m_MetaDataKeys = value;
    reset();
  }

  /**
   * Returns the keys of the meta-data values to copy across.
   *
   * @return		the keys
   */
  public BaseString[] getMetaDataKeys() {
    return m_MetaDataKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeysTipText() {
    return "The keys of the meta-data values to copy across.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  protected String check(LocatedObjects annotations, LocatedObjects predictions) {
    if (annotations == null)
      return "No annotations provided!";
    if (predictions == null)
      return "No predictions provided!";
    return null;
  }

  /**
   * Initializes the matches for the object.
   *
   * @param matches	for storing the matches
   * @param thisObj	the object to initialize the matches for
   */
  protected void initMatch(Map<LocatedObject, Map<LocatedObject,Double>> matches, LocatedObject thisObj) {
    if (!matches.containsKey(thisObj))
      matches.put(thisObj, new HashMap<>());
  }

  /**
   * Records the match for the object.
   *
   * @param matches	for storing the matches
   * @param thisObj	the object that a match was found for
   * @param otherObj	the match that was found
   * @param score	the score of the match
   */
  protected void addMatch(Map<LocatedObject, Map<LocatedObject,Double>> matches, LocatedObject thisObj, LocatedObject otherObj, double score) {
    initMatch(matches, thisObj);
    matches.get(thisObj).put(otherObj, score);
  }

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @param matches 	for collecting the matches
   * @return		the overlapping objects
   */
  protected abstract LocatedObjects doCalculate(LocatedObjects annotations, LocatedObjects predictions, Map<LocatedObject, Map<LocatedObject,Double>> matches);

  /**
   * Computes the overlapping objects between the annotations and the predictions.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the overlapping objects
   */
  @Override
  public LocatedObjects calculate(LocatedObjects annotations, LocatedObjects predictions) {
    String	msg;

    msg = check(annotations, predictions);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doCalculate(annotations, predictions, new HashMap<>());
  }

  /**
   * Computes the overlapping objects between the annotations and the predictions
   * and returns the matches.
   *
   * @param annotations the annotations (ground truth)
   * @param predictions the predictions to compare with
   * @return		the matches
   */
  @Override
  public Map<LocatedObject, Map<LocatedObject,Double>> matches(LocatedObjects annotations, LocatedObjects predictions) {
    Map<LocatedObject, Map<LocatedObject,Double>>	result;
    String					msg;

    msg = check(annotations, predictions);
    if (msg != null)
      throw new IllegalStateException(msg);
    result = new HashMap<>();
    doCalculate(annotations, predictions, result);
    return result;
  }

  /**
   * Copies the specified meta-data values (if available) from source to target.
   *
   * @param source	the source object with the values
   * @param target	the target object to receive the values
   */
  protected void copyMetaData(LocatedObject source, LocatedObject target) {
    for (BaseString key: m_MetaDataKeys) {
      if (source.getMetaData().containsKey(key.getValue()))
	target.getMetaData().put(key.getValue(), source.getMetaData().get(key.getValue()));
    }
  }
}
