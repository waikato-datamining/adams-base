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
 * ObjectFinder.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.objectfinder;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

/**
 * Interface for finders that locate objects in the report of an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ObjectFinder
  extends OptionHandler, QuickInfoSupporter, ObjectPrefixHandler {

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value);

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText();

  /**
   * Finds the objects in the list of objects.
   *
   * @param objects	the list of objects to process
   * @return		the indices
   */
  public int[] find(LocatedObjects objects);

  /**
   * Finds the objects in the report.
   *
   * @param report	the report to process
   * @return		the indices
   */
  public int[] find(Report report);

  /**
   * Finds the objects in the list of objects.
   *
   * @param objects	the list of objects to process
   * @return		the indices
   */
  public LocatedObjects findObjects(LocatedObjects objects);

  /**
   * Finds the objects in the report.
   *
   * @param report	the report to process
   * @return		the indices
   */
  public LocatedObjects findObjects(Report report);
}
