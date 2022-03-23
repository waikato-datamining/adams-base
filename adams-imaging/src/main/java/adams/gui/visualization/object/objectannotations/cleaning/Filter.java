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
 * Filter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.cleaning;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Applies the object finder to clean the annotations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Filter
    extends AbstractAnnotationCleaner {

  private static final long serialVersionUID = -3683007880321873968L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the object finder to clean the annotations.";
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
  }

  /**
   * Sets the finder to use for filtering the annotations.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for filtering the annotations.
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
    return "The object finder to apply to filter the data.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    return QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
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
    return m_Finder.findObjects(objects);
  }
}
