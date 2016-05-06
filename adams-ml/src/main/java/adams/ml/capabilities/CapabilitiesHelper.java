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
 * CapabilitiesHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.capabilities;

import adams.data.spreadsheet.Cell.ContentType;
import adams.ml.data.Dataset;

import java.util.Collection;

/**
 * Helper class for capabilities.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CapabilitiesHelper {

  /**
   * Returns capabilities that are required for the specified dataset.
   *
   * @param data	the dataset to get the capabilities for
   * @return		the capabilities
   */
  public static Capabilities forDataset(Dataset data) {
    Capabilities	result;
    int			i;

    result = new Capabilities();

    for (i = 0; i < data.getColumnCount(); i++)
      result.mergeWith(forDataset(data, i));

    return result;
  }

  /**
   * Returns capabilities that are required for the specified dataset column.
   *
   * @param data	the dataset to get the capabilities for
   * @param col		the column to generate the capabilities for
   * @return		the capabilities
   */
  public static Capabilities forDataset(Dataset data, int col) {
    Capabilities		result;
    Collection<ContentType>	types;

    result = new Capabilities();

    // regular attribute?
    if (!data.isClassAttribute(col)) {
      types = data.getContentTypes(col);
      for (ContentType type: types) {
	switch (type) {
	  case DATE:
	  case DATETIME:
	  case DATETIMEMSEC:
	  case TIME:
	  case TIMEMSEC:
	    result.enable(Capability.DATETYPE_ATTRIBUTE);
	    break;
	  case MISSING:
	    result.enable(Capability.MISSING_ATTRIBUTE_VALUE);
	    break;
	  case BOOLEAN:
	  case STRING:
	  case OBJECT:
	    result.enable(Capability.CATEGORICAL_ATTRIBUTE);
	    break;
	  case LONG:
	  case DOUBLE:
	    result.enable(Capability.NUMERIC_ATTRIBUTE);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled cell content type: " + type);
	}
      }
    }
    else {
      types = data.getContentTypes(col);
      for (ContentType type: types) {
	switch (type) {
	  case DATE:
	  case DATETIME:
	  case DATETIMEMSEC:
	  case TIME:
	  case TIMEMSEC:
	    result.enable(Capability.DATETYPE_CLASS);
	    break;
	  case MISSING:
	    result.enable(Capability.MISSING_CLASS_VALUE);
	    break;
	  case BOOLEAN:
	  case STRING:
	  case OBJECT:
	    result.enable(Capability.CATEGORICAL_CLASS);
	    break;
	  case LONG:
	  case DOUBLE:
	    result.enable(Capability.NUMERIC_CLASS);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled cell content type: " + type);
	}
      }
    }

    return result;
  }

  /**
   * Returns whether the capabilities handler handles the dataset.
   *
   * @param handler	the handler to check
   * @param data	the dataset to check against
   * @return		null if OK, otherwise error message
   */
  public static String handles(CapabilitiesHandler handler, Dataset data) {
    return handles(handler.getCapabilities(), data);
  }

  /**
   * Returns whether the capabilities can handle the dataset.
   *
   * @param caps	the capabilities to use as basis
   * @param data	the dataset to check against
   * @return		null if OK, otherwise error message
   */
  public static String handles(Capabilities caps, Dataset data) {
    String		result;
    Capabilities 	capsData;

    result   = null;
    capsData = forDataset(data);

    for (Capability cap: capsData.capabilities()) {
      if (!caps.isEnabled(cap)) {
	result = "Cannot handle " + cap;
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether the capabilities can handle the dataset column.
   *
   * @param caps	the capabilities to use as basis
   * @param data	the dataset to check against
   * @param col		the column to check
   * @return		null if OK, otherwise error message
   */
  public static String handles(Capabilities caps, Dataset data, int col) {
    String		result;
    Capabilities 	capsData;

    result   = null;
    capsData = forDataset(data, col);

    for (Capability cap: capsData.capabilities()) {
      if (!caps.isEnabled(cap)) {
	result = "Cannot handle " + cap + ", column " + (col+1);
	break;
      }
    }

    return result;
  }

  /**
   * Tries to adjust the dataset to the capabilities of the handler.
   *
   * @param handler	the handler to adjust the dataset for
   * @param data	the dataset to adjust
   * @return		the adjusted dataset
   * @throws Exception	if failed to adjust
   */
  public static Dataset adjust(CapabilitiesHandler handler, Dataset data) throws Exception {
    return adjust(handler.getCapabilities(), data);
  }

  /**
   * Tries to adjust the dataset to the capabilities.
   *
   * @param caps	the capabilities to adjust the dataset for
   * @param data	the dataset to adjust
   * @return		the adjusted dataset
   * @throws Exception	if failed to adjust
   */
  public static Dataset adjust(Capabilities caps, Dataset data) throws Exception {
    Capabilities 	capsData;

    capsData = forDataset(data);

    // TODO create view with columns turned off
    return data;
  }
}
