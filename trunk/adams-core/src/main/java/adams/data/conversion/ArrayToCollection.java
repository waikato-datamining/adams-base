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
 * ArrayToCollection.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 <!-- globalinfo-start -->
 * Turns an array into a collection.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-collection-class &lt;java.lang.String&gt; (property: collectionClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the collection.
 * &nbsp;&nbsp;&nbsp;default: java.util.ArrayList
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayToCollection
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 1028362547528503041L;
  
  /** the class for the array. */
  protected String m_CollectionClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an array into a collection.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "collection-class", "collectionClass",
	    ArrayList.class.getName());
  }

  /**
   * Sets the class for the collection.
   *
   * @param value	the classname
   */
  public void setCollectionClass(String value) {
    if (value.length() > 0) {
      m_CollectionClass = value;
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
  public String getCollectionClass() {
    return m_CollectionClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String collectionClassTipText() {
    return "The class to use for the collection.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Object[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Collection.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Collection	result;
    int		i;
    int		len;
    
    result = (Collection) Class.forName(m_CollectionClass).newInstance();
    len    = Array.getLength(m_Input);
    for (i = 0; i < len; i++)
      result.add(Array.get(m_Input, i));
    
    return result;
  }
}
