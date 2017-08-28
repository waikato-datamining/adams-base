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
 * AbstractMetaObjectFinder.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;

/**
 * Ancestor for object finders that use a base finder.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMetaObjectFinder<T extends AbstractImageContainer>
  extends AbstractObjectFinder<T>{

  private static final long serialVersionUID = -9221045219962890073L;

  /** the ObjectFinder to apply. */
  protected ObjectFinder m_ObjectFinder;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "object-finder", "objectFinder",
      getDefaultObjectFinder());
  }

  /**
   * Returns the default finder to use.
   *
   * @return		the default
   */
  protected ObjectFinder getDefaultObjectFinder() {
    return new NullFinder();
  }

  /**
   * Sets the object finder to use.
   *
   * @param value	the object finder
   */
  public void setObjectFinder(ObjectFinder value) {
    m_ObjectFinder = value;
    reset();
  }

  /**
   * Returns the object finder in use.
   *
   * @return		the object finder
   */
  public ObjectFinder getObjectFinder() {
    return m_ObjectFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String objectFinderTipText() {
    return "The object finder to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "objectFinder", m_ObjectFinder, "object finder: ");
  }
}
