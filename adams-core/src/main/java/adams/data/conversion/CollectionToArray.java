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
 * CollectionToArray.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.lang.reflect.Array;
import java.util.Collection;

import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Turns a collection into an array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-array-class &lt;java.lang.String&gt; (property: arrayClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the elements in the array.
 * &nbsp;&nbsp;&nbsp;default: java.lang.Object
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CollectionToArray
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 1028362547528503041L;
  
  /** the class for the array. */
  protected String m_ArrayClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a collection into an array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "array-class", "arrayClass",
	    Object.class.getName());
  }

  /**
   * Sets the class for the array.
   *
   * @param value	the classname
   */
  public void setArrayClass(String value) {
    if (value.length() > 0) {
      m_ArrayClass = value;
      reset();
    }
    else {
      getLogger().severe("Class cannot be empty!");
    }
  }

  /**
   * Returns the class for the collection.
   *
   * @return		the classname
   */
  public String getArrayClass() {
    return m_ArrayClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String arrayClassTipText() {
    return "The class to use for the elements in the array.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Collection.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    try {
      return Utils.newArray(m_ArrayClass, 0).getClass();
    }
    catch (Exception e) {
      return Object[].class;
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Object	result;
    Collection	coll;
    int		i;
    
    coll   = (Collection) m_Input;
    result = Utils.newArray(m_ArrayClass, coll.size());
    i      = 0;
    for (Object item: coll) {
      Array.set(result, i, item);
      i++;
    }
    
    return result;
  }
}
