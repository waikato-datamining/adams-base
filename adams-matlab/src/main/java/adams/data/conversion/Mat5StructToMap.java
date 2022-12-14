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
 * Mat5StructToMap.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.Mat5ArrayElementIndex;
import adams.data.matlab.MatlabArrayIndexSupporter;
import us.hebi.matlab.mat.types.Struct;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns the Matlab struct into a map.<br>
 * In case of multi-dimensional (outermost) structs, an index can be supplied to retrieve just a single element instead of all of them. Appends [x] or [y,x] to the field names, using 0-based indices.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-index &lt;adams.core.base.Mat5ArrayElementIndex&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The (optional) index for multi-dimensional structs to retrieve a single
 * &nbsp;&nbsp;&nbsp;element instead of all.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-zero-based-index &lt;boolean&gt; (property: zeroBasedIndex)
 * &nbsp;&nbsp;&nbsp;If true, the index is treated as 0-based (eg 0;0 for first value) rather
 * &nbsp;&nbsp;&nbsp;than 1-based ones (eg 1;1 for first value).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mat5StructToMap
  extends AbstractConversion
  implements MatlabArrayIndexSupporter {

  private static final long serialVersionUID = -2006396004849089721L;

  /** the index to retrieve. */
  protected Mat5ArrayElementIndex m_Index;

  /** whether to interpret the indices as 0-based or 1-based. */
  protected boolean m_ZeroBasedIndex;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the Matlab struct into a map.\n"
      + "In case of multi-dimensional (outermost) structs, an index can be supplied to "
      + "retrieve just a single element instead of all of them. Appends [x] or [y,x] to the field names, using 0-based indices.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "index", "index",
      new Mat5ArrayElementIndex());

    m_OptionManager.add(
      "zero-based-index", "zeroBasedIndex",
      false);
  }

  /**
   * Sets the (optional) index for multi-dimensional structs to retrieve just the specified single element instead of all.
   *
   * @param value	the index
   */
  public void setIndex(Mat5ArrayElementIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the (optional) index for multi-dimensional structs to retrieve just the specified single element instead of all.
   *
   * @return		the index
   */
  public Mat5ArrayElementIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The (optional) index for multi-dimensional structs to retrieve a single element instead of all.";
  }

  /**
   * Sets whether the index is 0-based or 1-based.
   *
   * @param value	true if 0-based
   */
  @Override
  public void setZeroBasedIndex(boolean value) {
    m_ZeroBasedIndex = value;
    reset();
  }

  /**
   * Returns whether the index is 0-based or 1-based.
   *
   * @return		true if 0-based
   */
  @Override
  public boolean getZeroBasedIndex() {
    return m_ZeroBasedIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String zeroBasedIndexTipText() {
    return "If true, the index is treated as 0-based (eg 0;0 for first value) rather than 1-based ones (eg 1;1 for first value).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "index", (m_Index.isEmpty() ? "-none-" : m_Index.getValue()), "index: ");
    result += QuickInfoHelper.toString(this, "zeroBasedIndex", (m_ZeroBasedIndex ? "0-based" : "1-based"), ", ");

    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return Struct.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Adds the Struct recursively.
   *
   * @param map		the map to add to
   * @param struct	the struct to add
   * @param index 	the index to retrieve
   */
  protected void addStruct(Map map, Struct struct, int[] index) {
    Object	obj;
    Map		submap;
    int[]	dims;
    int		y;
    int		x;
    int[]	rangeY;
    int[]	rangeX;
    String	suffix;

    if (struct.getNumDimensions() > 2) {
      getLogger().warning("Cannot handle structs with more than 2 dimensions, skipping: " + struct);
      return;
    }

    dims = struct.getDimensions();
    if ((index != null) && (index.length != dims.length))
      throw new IllegalStateException("Dimensions of index and struct differ: " + index.length + " != " + dims.length);

    suffix = "";
    switch (dims.length) {
      case 1:
	for (String field: struct.getFieldNames()) {
	  if (index != null) {
	    obj = struct.get(field, index[0]);
	    if (dims[0] > 1)
	      suffix = "" + index[0];
	  }
	  else {
	    obj = struct.get(field);
	  }
	  if (obj instanceof Struct) {
	    submap = new HashMap();
	    map.put(field + suffix, submap);
	    addStruct(submap, (Struct) obj, null);
	  }
	  else {
	    map.put(field + suffix, obj);
	  }
	}
	break;

      case 2:
	if (index != null) {
	  rangeY = new int[]{index[0], index[0] + 1};
	  rangeX = new int[]{index[1], index[1] + 1};
	}
	else {
	  rangeY = new int[]{0, dims[0]};
	  rangeX = new int[]{0, dims[1]};
	}
	for (y = rangeY[0]; y < rangeY[1]; y++) {
	  for (x = rangeX[0]; x < rangeX[1]; x++) {
	    for (String field: struct.getFieldNames()) {
	      obj = struct.get(field, y, x);
	      if ((dims[0] > 1) || (dims[1] > 1))
		suffix = "[" + y + "," + x + "]";
	      if (obj instanceof Struct) {
		submap = new HashMap();
		map.put(field + suffix, submap);
		addStruct(submap, (Struct) obj, null);
	      }
	      else {
		map.put(field + suffix, obj);
	      }
	    }
	  }
	}
	break;

      default:
	throw new IllegalStateException("Unhandled number of dimensions: " + dims.length);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Map		result;
    Struct	struct;
    int[]	index;

    result = new HashMap<>();
    struct = (Struct) m_Input;
    if (m_Index.isEmpty())
      index = null;
    else
      index = m_Index.indexValue(!m_ZeroBasedIndex);
    addStruct(result, struct, index);

    return result;
  }
}
