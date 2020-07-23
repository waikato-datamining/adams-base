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
 * RandomRegions.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.core.Randomizable;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.overlappingobjectremoval.AbstractOverlappingObjectRemoval;
import adams.data.overlappingobjectremoval.OverlappingObjectRemoval;
import adams.data.overlappingobjectremoval.PassThrough;
import adams.data.overlappingobjectremoval.RemoveAll;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Random;
import java.util.logging.Level;

/**
 * Generates specified number of random regions and then prunes ones that overlap with other regions or annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RandomRegions
  extends AbstractNegativeRegionsGenerator
  implements Randomizable {

  private static final long serialVersionUID = -904202231629949668L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the seed value. */
  protected long m_Seed;

  /** the minimum width. */
  protected int m_MinWidth;

  /** the maximum width. */
  protected int m_MaxWidth;

  /** the minimum height. */
  protected int m_MinHeight;

  /** the maximum height. */
  protected int m_MaxHeight;

  /** the number of regions to generate (before removing overlapping ones). */
  protected int m_NumRegions;

  /** the object overlap calculation to use. */
  protected ObjectOverlap m_OverlapDetection;

  /** the object removal algorithm. */
  protected OverlappingObjectRemoval m_OverlapRemoval;

  /** the random number generator. */
  protected transient Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates specified number of random regions and then prunes ones that overlap with other regions or annotations.";
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
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "min-width", "minWidth",
      -1, -1, null);

    m_OptionManager.add(
      "max-width", "maxWidth",
      -1, -1, null);

    m_OptionManager.add(
      "min-height", "minHeight",
      -1, -1, null);

    m_OptionManager.add(
      "max-height", "maxHeight",
      -1, -1, null);

    m_OptionManager.add(
      "num-regions", "numRegions",
      100, 1, null);

    m_OptionManager.add(
      "overlap-detection", "overlapDetection",
      new AreaRatio());

    m_OptionManager.add(
      "overlap-removal", "overlapRemoval",
      new PassThrough());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Sets the object finder to use.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the object finder in use.
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
    return "The object finder to use for locating objects in the report.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed to use for the random number generator.";
  }

  /**
   * Sets the minimum width a negative region must have.
   *
   * @param value	the minimum width, ignored if <1
   */
  public void setMinWidth(int value) {
    if (getOptionManager().isValid("minWidth", value)) {
      m_MinWidth = value;
      reset();
    }
  }

  /**
   * Returns the minimum width a negative region must have.
   *
   * @return		the minimum width, ignored if <1
   */
  public int getMinWidth() {
    return m_MinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minWidthTipText() {
    return "The minimum width that a negative region must have, ignored if <1.";
  }

  /**
   * Sets the maximum width a negative region can have.
   *
   * @param value	the maximum width, ignored if <1
   */
  public void setMaxWidth(int value) {
    if (getOptionManager().isValid("maxWidth", value)) {
      m_MaxWidth = value;
      reset();
    }
  }

  /**
   * Returns the maximum width a negative region can have.
   *
   * @return		the maximum width, ignored if <1
   */
  public int getMaxWidth() {
    return m_MaxWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxWidthTipText() {
    return "The maximum width that a negative region can have, ignored if <1.";
  }

  /**
   * Sets the minimum height a negative region must have.
   *
   * @param value	the minimum height, ignored if <1
   */
  public void setMinHeight(int value) {
    if (getOptionManager().isValid("minHeight", value)) {
      m_MinHeight = value;
      reset();
    }
  }

  /**
   * Returns the minimum height a negative region must have.
   *
   * @return		the minimum height, ignored if <1
   */
  public int getMinHeight() {
    return m_MinHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minHeightTipText() {
    return "The minimum height that a negative region must have, ignored if <1.";
  }

  /**
   * Sets the maximum height a negative region can have.
   *
   * @param value	the maximum height, ignored if <1
   */
  public void setMaxHeight(int value) {
    if (getOptionManager().isValid("maxHeight", value)) {
      m_MaxHeight = value;
      reset();
    }
  }

  /**
   * Returns the maximum height a negative region can have.
   *
   * @return		the maximum height, ignored if <1
   */
  public int getMaxHeight() {
    return m_MaxHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxHeightTipText() {
    return "The maximum height that a negative region can have, ignored if <1.";
  }

  /**
   * Sets the number of regions to generate, before removing ones that overlap or overlap with annotations.
   *
   * @param value	the number of regions
   */
  public void setNumRegions(int value) {
    if (getOptionManager().isValid("numRegions", value)) {
      m_NumRegions = value;
      reset();
    }
  }

  /**
   * Returns the number of regions to generate, before removing ones that overlap or overlap with annotations.
   *
   * @return		the number of regions
   */
  public int getNumRegions() {
    return m_NumRegions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRegionsTipText() {
    return "The number of regions to generate, before removing ones that overlap or overlap with annotations.";
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
   * Sets the algorithm for determining the overlapping objects
   *
   * @param value 	the algorithm
   */
  public void setOverlapRemoval(OverlappingObjectRemoval value) {
    m_OverlapRemoval = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
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
    return "The algorithm to use for removing the overlapping objects.";
  }

  /**
   * Generates the negative regions.
   *
   * @param cont	the image container to generate the regions for
   * @return		the generated regions
   */
  @Override
  protected LocatedObjects doGenerateRegions(AbstractImageContainer cont) {
    LocatedObjects 	result;
    LocatedObjects 	candidates;
    LocatedObjects	annotations;
    int			i;
    int			width;
    int			height;
    int			minWidth;
    int			maxWidth;
    int 		minHeight;
    int			maxHeight;
    int			rangeWidth;
    int			rangeHeight;
    int			x;
    int			y;
    int			w;
    int 		h;
    Report		candReport;
    Report		annReport;

    result     = new LocatedObjects();
    candidates = new LocatedObjects();

    if (m_Random == null)
      m_Random = new Random(m_Seed);

    // generate candidates
    width       = cont.getWidth();
    height      = cont.getHeight();
    minWidth    = (m_MinWidth == -1) ? 1 : m_MinWidth;
    maxWidth    = (m_MaxWidth == -1) ? width : m_MaxWidth;
    minHeight   = (m_MinHeight == -1) ? 1 : m_MinHeight;
    maxHeight   = (m_MaxHeight == -1) ? height : m_MaxHeight;
    rangeWidth  = maxWidth - minWidth + 1;
    rangeHeight = maxHeight - minHeight + 1;
    for (i = 0; i < m_NumRegions; i++) {
      x = m_Random.nextInt(width - minWidth);
      y = m_Random.nextInt(height - minHeight);
      w = m_Random.nextInt(rangeWidth) + minWidth;
      h = m_Random.nextInt(rangeHeight) + minHeight;
      candidates.add(new LocatedObject(x, y, w, h));
    }
    candReport = candidates.toReport(m_Finder.getPrefix());

    // select relevant annotations
    annotations = m_Finder.findObjects(cont.getReport());
    annReport   = annotations.toReport(m_Finder.getPrefix());

    // prune candidates
    try {
      // overlaps within candidates
      candReport = AbstractOverlappingObjectRemoval.remove(candReport, candReport, new AllFinder(), m_OverlapDetection, m_OverlapRemoval);
      // overlaps with annotations
      candReport = AbstractOverlappingObjectRemoval.remove(candReport, annReport, new AllFinder(), m_OverlapDetection, new RemoveAll());
      // generate report
      result = LocatedObjects.fromReport(candReport, m_Finder.getPrefix());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to remove overlaps!", e);
      result.clear();
    }

    if (isStopped())
      result.clear();

    return result;
  }
}
