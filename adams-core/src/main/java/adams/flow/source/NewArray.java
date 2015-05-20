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
 * NewArray.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.lang.reflect.Array;

import adams.core.QuickInfoHelper;
import adams.core.base.ArrayDimensions;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Generates a new array of the specified class with the given dimensions.<br>
 * Dimensions are given as follows: [x], with x being the number of elements. You can have multi-dimensional arrays: [x][y][z]. Variables can be used as well to specify the dimensions: [&#64;{x}].
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: NewArray
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-array-class &lt;java.lang.String&gt; (property: arrayClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the array.
 * &nbsp;&nbsp;&nbsp;default: java.lang.String
 * </pre>
 * 
 * <pre>-dimensions &lt;adams.core.base.ArrayDimensions&gt; (property: dimensions)
 * &nbsp;&nbsp;&nbsp;The dimensions of the array; eg '[2]', '[2][3][7]', '[&#64;{x}]'.
 * &nbsp;&nbsp;&nbsp;default: [2]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewArray
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = 7272049518765623563L;

  /** the class for the array. */
  protected String m_ArrayClass;
  
  /** the dimensions. */
  protected ArrayDimensions m_Dimensions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a new array of the specified class with the given dimensions.\n"
	+ "Dimensions are given as follows: [x], with x being the number of elements. "
	+ "You can have multi-dimensional arrays: [x][y][z]. "
	+ "Variables can be used as well to specify the dimensions: [@{x}].";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "array-class", "arrayClass",
	    String.class.getName());

    m_OptionManager.add(
	    "dimensions", "dimensions",
	    new ArrayDimensions("[2]"));
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "arrayClass", m_ArrayClass, "Class: ");
    result += QuickInfoHelper.toString(this, "dimensions", m_Dimensions, ", Dimensions: ");

    return result;
  }

  /**
   * Sets the class for the array.
   *
   * @param value	the classname, use empty string to use class of first
   * 			element
   */
  public void setArrayClass(String value) {
    m_ArrayClass = value;
    reset();
  }

  /**
   * Returns the class for the array.
   *
   * @return		the classname, empty string if class of first element
   * 			is used
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
    return "The class to use for the array.";
  }

  /**
   * Sets the dimensions of the array.
   *
   * @param value	the dimensions
   */
  public void setDimensions(ArrayDimensions value) {
    m_Dimensions = value;
    reset();
  }

  /**
   * Returns the dimensions of the array.
   *
   * @return		the dimensions
   */
  public ArrayDimensions getDimensions() {
    return m_Dimensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dimensionsTipText() {
    return "The dimensions of the array; eg '[2]', '[2][3][7]', '[@{x}]'.";
  }

  /**
   * Parses the dimensions.
   * 
   * @param dim		the dimension string
   * @return		the dimension array, null if failed to parse
   */
  protected int[] parseDimensions(String dim) {
    ArrayDimensions	dims;
    
    dim  = getVariables().expand(dim);
    dims = new ArrayDimensions();
    if (!dims.isValid(dim))
      return null;
    dims.setValue(dim);
    return dims.dimensionsValue();
  }
  
  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    Class	cls;
    
    try {
      cls = Class.forName(m_ArrayClass);
      if (parseDimensions(m_Dimensions.getValue()) == null)
	return new Class[]{Unknown.class};
      else
	return new Class[]{Array.newInstance(cls, parseDimensions(m_Dimensions.getValue())).getClass()};
    }
    catch (Exception e) {
      return new Class[]{Unknown.class};
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Class	cls;
    Object	array;
    
    result = null;
    
    if (parseDimensions(m_Dimensions.getValue()) == null)
      result = "Failed to parse dimensions: " + m_Dimensions.getValue();
    
    if (result == null) {
      try {
	cls           = Class.forName(m_ArrayClass);
	array         = Array.newInstance(cls, parseDimensions(m_Dimensions.getValue()));
	m_OutputToken = new Token(array);
      }
      catch (Exception e) {
	result = handleException("Failed to create array!", e);
      }
    }
    
    return result;
  }
}
