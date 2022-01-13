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
 * KeepHighestMetaDataValue.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.overlappingobjectremoval;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;
import java.util.Set;

/**
 * Keeps object with the highest (numeric) meta-data value.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class KeepHighestMetaDataValue
  extends AbstractOverlappingObjectRemoval {

  private static final long serialVersionUID = -895136411948961806L;

  /** the score meta-data key. */
  protected String m_ScoreKey;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Keeps object with the highest (numeric) meta-data value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "score-key", "scoreKey",
      "");
  }

  /**
   * Sets the key for the score in the meta-data.
   *
   * @param value	the key
   */
  public void setScoreKey(String value) {
    m_ScoreKey = value;
    reset();
  }

  /**
   * Returns the key for the score in the meta-data.
   *
   * @return		the key
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
  public String labelKeyTipText() {
    return "The key for the (numeric) score in the meta-data.";
  }

  /**
   * Removes overlapping image objects.
   *
   * @param objects	the objects to clean up
   * @param matches	the matches that were determined by an algorithm, used as basis for removal
   * @return		the updated objects
   */
  @Override
  public LocatedObjects removeOverlaps(LocatedObjects objects, Map<LocatedObject, Set<LocatedObject>> matches) {
    LocatedObjects	result;
    Set<LocatedObject> 	others;
    LocatedObject 	keep;
    double		thisScore;
    double		otherScore;
    double		currScore;

    result = new LocatedObjects();
    for (LocatedObject thisObj : objects) {
      others = matches.get(thisObj);
      if ((others != null) && (others.size() > 0)) {
        keep      = thisObj;
	thisScore = (Double) thisObj.getMetaData().getOrDefault(m_ScoreKey, 0.0);
	currScore = thisScore;
        for (LocatedObject otherObj : others) {
	  otherScore = (Double) otherObj.getMetaData().getOrDefault(m_ScoreKey, 0.0);
	  if (otherScore > currScore) {
	    keep      = otherObj;
	    currScore = otherScore;
	  }
	}
        if (!result.contains(keep))
          result.add(keep.getClone());
      }
      else {
        if (!result.contains(thisObj))
          result.add(thisObj.getClone());
      }
    }

    return result;
  }
}
