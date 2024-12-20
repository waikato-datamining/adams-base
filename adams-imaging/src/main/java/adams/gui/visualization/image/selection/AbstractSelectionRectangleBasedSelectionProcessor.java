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
 * AbstractSelectionRectangleBasedSelectionProcessor.java
 * Copyright (C) 2017-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import adams.data.report.AnnotationHelper;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.visualization.image.ImagePanel.PaintPanel;
import adams.gui.visualization.image.SelectionRectangle;

import java.util.List;
import java.util.Map;

/**
 * Ancestor for selection processors that make use of the {@link SelectionRectangle}
 * class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSelectionRectangleBasedSelectionProcessor
  extends AbstractPaintingSelectionProcessor
  implements ObjectPrefixHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5879410661391670242L;

  /** the key for the X location. */
  public final static String KEY_X = LocatedObjects.KEY_X;

  /** the key for the Y location. */
  public final static String KEY_Y = LocatedObjects.KEY_Y;

  /** the key for the width. */
  public final static String KEY_WIDTH = LocatedObjects.KEY_WIDTH;

  /** the key for the height. */
  public final static String KEY_HEIGHT = LocatedObjects.KEY_HEIGHT;

  /** the key for the Xs of the polygon. */
  public final static String KEY_POLY_X = LocatedObjects.KEY_POLY_X;

  /** the key for the Ys of the polygon. */
  public final static String KEY_POLY_Y = LocatedObjects.KEY_POLY_Y;

  /** the prefix for the objects. */
  protected String m_Prefix;

  /** the number of digits to use for left-padding the index. */
  protected int m_NumDigits;

  /** the current rectangles. */
  protected List<SelectionRectangle> m_Locations;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      getDefaultPrefix());

    m_OptionManager.add(
      "num-digits", "numDigits",
      getDefaultNumDigits(), 0, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Locations = null;
  }

  /**
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  protected String getDefaultPrefix() {
    return LocatedObjects.DEFAULT_PREFIX;
  }

  /**
   * Sets the prefix to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the fields in the report.";
  }

  /**
   * Returns the default number of digits to use.
   *
   * @return		the default
   */
  protected int getDefaultNumDigits() {
    return 4;
  }

  /**
   * Sets the number of digits to use for the left-padded index.
   *
   * @param value 	the number of digits
   */
  public void setNumDigits(int value) {
    m_NumDigits = value;
    reset();
  }

  /**
   * Returns the number of digits to use for the left-padded index.
   *
   * @return 		the number of digits
   */
  public int getNumDigits() {
    return m_NumDigits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDigitsTipText() {
    return "The number of digits to use for left-padding the index with zeroes.";
  }

  /**
   * Returns all the values stored in the report under this index.
   *
   * @param report	the report to look up the index in
   * @param index	the index to retrieve the values for
   * @return		the values
   */
  protected Map<String,Object> valuesForIndex(Report report, int index) {
    return AnnotationHelper.valuesForIndex(report, m_Prefix, index);
  }

  /**
   * Removes the specified index from the report.
   *
   * @return		true if successfully removed
   */
  protected boolean removeIndex(Report report, int index) {
    return AnnotationHelper.removeIndex(report, m_Prefix, index);
  }

  /**
   * Determines the last index used with the given prefix.
   */
  protected int findLastIndex(Report report) {
    return AnnotationHelper.findLastIndex(report, m_Prefix);
  }

  /**
   * Returns all currently stored locations.
   *
   * @param report	the report to get the locations from
   * @return		the locations
   */
  protected List<SelectionRectangle> getLocations(Report report) {
    return AnnotationHelper.getLocations(report, m_Prefix);
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
    m_Locations = null;
  }
}
