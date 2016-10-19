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
 * AbstractMultiObjectExport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.multiobjectexport;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for schemes that can export multiple objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiObjectExport
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -5344243845771403487L;

  /**
   * Ensures that the names are unique.
   *
   * @param names	the names
   * @return		the unique names
   */
  protected String[] disambiguateNames(String[] names) {
    String[]	result;
    int		i;
    int		count;
    Set<String>	check;
    String	name;

    result = new String[names.length];
    check  = new HashSet<>();
    for (i = 0; i < names.length; i++) {
      count     = 0;
      result[i] = null;
      do {
	if (count == 0)
	  name = names[i];
	else
	  name = names[i] + "-" + count;
	if (!check.contains(name)) {
	  check.add(name);
	  result[i] = name;
	}
	count++;
      }
      while (result[i] == null);
    }

    return result;
  }

  /**
   * Checks the data.
   *
   * @param names	the names for the objects
   * @param objects	the objects
   * @return		null if successful, otherwise error message
   */
  protected String check(String[] names, Object[] objects) {
    if (names.length != objects.length)
      return "Different number of names and objects supplied: " + names.length + " != " + objects.length;
    return null;
  }

  /**
   * Determines the exporter to use for the object.
   *
   * @param name	the name of the object
   * @param obj		the object to determine the exporter for
   * @param errors 	for storing errors
   * @return		the exporter
   */
  protected AbstractObjectExporter determineExporter(String name, Object obj, MessageCollection errors) {
    AbstractObjectExporter		result;
    List<AbstractObjectExporter> 	exporters;

    exporters = AbstractObjectExporter.getExporter(obj);
    if (exporters.size() == 0) {
      errors.add("Failed to find object exporter for '" + name + "'/" + Utils.classToString(obj.getClass()));
      return null;
    }
    result = exporters.get(0);

    return result;
  }

  /**
   * Returns the extension for the given exporter.
   *
   * @param exporter 	the exporter to get the extension for
   * @return		the extension to use
   */
  protected String determineExtension(AbstractObjectExporter exporter) {
    return exporter.getFormatExtensions()[0];
  }

  /**
   * Performs the actual export of the objects using the given names.
   *
   * @param names	the names for the objects
   * @param objects	the objects
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExport(String[] names, Object[] objects);

  /**
   * Exports the objects using the given names.
   *
   * @param names	the names for the objects
   * @param objects	the objects
   * @return		null if successful, otherwise error message
   */
  public String export(String[] names, Object[] objects) {
    String	result;

    result = check(names, objects);
    if (result == null)
      result = doExport(names, objects);

    return result;
  }
}
