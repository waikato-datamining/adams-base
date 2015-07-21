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

/**
 * BaseMeasureCollection.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.base;

import moa.evaluation.F1;
import moa.evaluation.MeasureCollection;

/**
 * Wrapper for MOA's {@link MeasureCollection} classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseMeasureCollection
  extends AbstractBaseString {

  private static final long serialVersionUID = -3018821041309299898L;

  /** the default package for collections. */
  public final static String DEFAULT_PACKAGE = "moa.evaluation";

  /**
   * Initializes the collection with the default collection.
   */
  public BaseMeasureCollection() {
    this(new F1());
  }

  /**
   * Initializes the collection the specified collection.
   *
   * @param coll	the collection to use
   */
  public BaseMeasureCollection(MeasureCollection coll) {
    super(coll.getClass().getSimpleName());
  }

  /**
   * Initializes the collection the specified collection name.
   *
   * @param coll	the collection to use
   */
  public BaseMeasureCollection(String coll) {
    super(coll);
  }

  /**
   * Initializes the internal object.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void initialize() {
    m_Internal = F1.class.getSimpleName();
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    try {
      if (!value.contains("."))
	Class.forName(DEFAULT_PACKAGE + "." + value);
      else
        Class.forName(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the collection as collection object.
   *
   * @return		the collection
   */
  public MeasureCollection collectionValue() {
    try {
      if (getValue().contains("."))
	return (MeasureCollection) Class.forName(getValue()).newInstance();
      else
	return (MeasureCollection) Class.forName(DEFAULT_PACKAGE + "." + getValue()).newInstance();
    }
    catch (Exception e) {
      return new F1();
    }
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Wrapper for MOA's MeasureCollectio classes.";
  }
}
