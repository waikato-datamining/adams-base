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
 * OverlapRemoval.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.cleaning;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.overlappingobjectremoval.OverlappingObjectRemoval;
import adams.data.overlappingobjectremoval.PassThrough;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Map;

/**
 * Applies the overlapping object removal algorithm to clean the annotations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OverlapRemoval
  extends AbstractAnnotationCleaner {

  private static final long serialVersionUID = -3683007880321873968L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the object overlap calculation to use. */
  protected ObjectOverlap m_OverlapDetection;

  /** the removal algorithm to use. */
  protected OverlappingObjectRemoval m_OverlapRemoval;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the overlapping object removal algorithm to clean the annotations.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "overlap-detection", "overlapDetection",
      new AreaRatio());

    m_OptionManager.add(
      "overlap-removal", "overlapRemoval",
      new PassThrough());
  }

  /**
   * Sets the object finder for locating the objects.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns object finder for locating the objects.
   *
   * @return 		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder for locating the objects of interest.";
  }

  /**
   * Sets the algorithm for determining the overlapping objects
   *
   * @param value 	the algorithm
   */
  public void setOverlapDetection(ObjectOverlap value) {
    m_OverlapDetection = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getOverlapDetection() {
    return m_OverlapDetection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapDetectionTipText() {
    return "The algorithm to use for determining the overlapping objects.";
  }

  /**
   * Sets the overlap removal algorithm to clean the annotations.
   *
   * @param value 	the algorithm
   */
  public void setOverlapRemoval(OverlappingObjectRemoval value) {
    m_OverlapRemoval = value;
    reset();
  }

  /**
   * Returns the overlap removal algorithm to clean the annotations.
   *
   * @return 		the algorithm
   */
  public OverlappingObjectRemoval getOverlapRemoval() {
    return m_OverlapRemoval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapRemovalTipText() {
    return "The overlap removal algorithm to apply.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    return QuickInfoHelper.toString(this, "overlapRemoval", m_OverlapRemoval, "removal: ");
  }

  /**
   * Cleans the annotations.
   *
   * @param objects the annotations to clean
   * @param errors  for recording errors
   * @return the (potentially) cleaned annotations
   */
  @Override
  protected LocatedObjects doCleanAnnotations(LocatedObjects objects, MessageCollection errors) {
    LocatedObjects					result;
    LocatedObjects 					filtered;
    Map<LocatedObject, Map<LocatedObject,Double>>	matches;

    filtered = m_Finder.findObjects(objects);
    matches  = m_OverlapDetection.matches(filtered, filtered);
    result   = m_OverlapRemoval.removeOverlaps(filtered, matches);

    return result;
  }
}
