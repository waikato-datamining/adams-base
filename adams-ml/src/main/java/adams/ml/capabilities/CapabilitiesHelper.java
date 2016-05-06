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
import adams.ml.data.DatasetView;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for capabilities.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CapabilitiesHelper {

  /**
   * Turns the content type of a cell into the corresponding capability.
   *
   * @param type	the type of the cell
   * @param isClass	whether the column represents a class column
   * @return		the capability
   */
  public static Capability contentTypeToCapability(ContentType type, boolean isClass) {
    if (!isClass) {
      switch (type) {
	case DATE:
	case DATETIME:
	case DATETIMEMSEC:
	case TIME:
	case TIMEMSEC:
	  return Capability.DATETYPE_ATTRIBUTE;
	case MISSING:
	  return Capability.MISSING_ATTRIBUTE_VALUE;
	case BOOLEAN:
	case STRING:
	case OBJECT:
	  return Capability.CATEGORICAL_ATTRIBUTE;
	case LONG:
	case DOUBLE:
	  return Capability.NUMERIC_ATTRIBUTE;
	default:
	  throw new IllegalStateException("Unhandled cell content type: " + type);
      }
    }
    else {
      switch (type) {
	case DATE:
	case DATETIME:
	case DATETIMEMSEC:
	case TIME:
	case TIMEMSEC:
	  return Capability.DATETYPE_CLASS;
	case MISSING:
	  return Capability.MISSING_CLASS_VALUE;
	case BOOLEAN:
	case STRING:
	case OBJECT:
	  return Capability.CATEGORICAL_CLASS;
	case LONG:
	case DOUBLE:
	  return Capability.NUMERIC_CLASS;
	default:
	  throw new IllegalStateException("Unhandled cell content type: " + type);
      }
    }
  }

  /**
   * Returns capabilities that are required for the specified dataset column.
   * Uses {@link Capability#UNKNOWN_ATTRIBUTE} and {@link Capability#UNKNOWN_CLASS}
   * for mixed columns.
   *
   * @param data	the dataset to get the capabilities for
   * @param col		the column to generate the capabilities for
   * @return		the capabilities
   */
  public static Capabilities forDataset(Dataset data, int col) {
    Capabilities		result;
    Collection<ContentType>	types;
    Set<Capability>		caps;

    result = new Capabilities();
    types  = data.getContentTypes(col);
    caps   = new HashSet<>();
    for (ContentType type: types)
      caps.add(contentTypeToCapability(type, data.isClassAttribute(col)));

    // uniform columns?
    if (caps.size() == 1) {
      result.enableAll(caps);
    }
    else if ((caps.size() == 2) && caps.contains(Capability.MISSING_ATTRIBUTE_VALUE)) {
      result.enableAll(caps);
    }
    else if ((caps.size() == 2) && caps.contains(Capability.MISSING_CLASS_VALUE)) {
      result.enableAll(caps);
    }
    else {
      if (data.isClassAttribute(col))
	result.enable(Capability.UNKNOWN_CLASS);
      else
	result.enable(Capability.UNKNOWN_ATTRIBUTE);
    }

    return result;
  }

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
   * Cannot adjust capabilities related to class columns
   *
   * @param handler	the handler to adjust the dataset for
   * @param data	the dataset to adjust
   * @return		the adjusted dataset
   * @throws Exception	if failed to adjust
   */
  public static Dataset adjust(Dataset data, CapabilitiesHandler handler) throws Exception {
    return adjust(data, handler.getCapabilities());
  }

  /**
   * Tries to adjust the dataset to the capabilities.
   * Cannot adjust capabilities related to class columns
   *
   * @param caps	the capabilities to adjust the dataset for
   * @param data	the dataset to adjust
   * @return		the adjusted dataset
   * @throws Exception	if failed to adjust
   */
  public static Dataset adjust(Dataset data, Capabilities caps) throws Exception {
    Capabilities 		capsData;
    Set<Capability>		hide;
    Collection<ContentType>	types;
    TIntArrayList		visibleCols;
    boolean			visible;
    int				i;

    capsData = forDataset(data);

    hide = new HashSet<>();
    for (Capability cap: capsData.capabilities()) {
      if (!caps.isEnabled(cap)) {
	if (cap.isClassRelated()) {
	  throw new Exception(
	    "Adjusting of class-related capabilities not possible: cannpot handle " + cap);
	}
	else {
	  switch (cap) {
	    case CATEGORICAL_ATTRIBUTE:
	    case NUMERIC_ATTRIBUTE:
	    case DATETYPE_ATTRIBUTE:
	      hide.add(cap);
	      break;
	    case MISSING_ATTRIBUTE_VALUE:
	      throw new Exception(
		"Cannot adjust data to handle missing attribute values!");
	    default:
	      throw new Exception(
		"Unhandled capability: " + cap);
	  }
	}
      }
    }

    // hide unsupported column types
    if (hide.size() > 0) {
      visibleCols = new TIntArrayList();
      for (i = 0; i < data.getColumnCount(); i++) {
	visible = true;
	types   = data.getContentTypes(i);
	for (ContentType type: types) {
	  if (hide.contains(contentTypeToCapability(type, data.isClassAttribute(i))))
	    visible = false;
	}
	if (visible)
	  visibleCols.add(i);
      }
      data = new DatasetView(data, null, visibleCols.toArray());
    }

    return data;
  }
}
