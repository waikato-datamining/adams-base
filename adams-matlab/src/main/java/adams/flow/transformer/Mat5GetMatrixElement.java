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
 * Mat5GetMatrixElement.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.Mat5ArrayElementIndex;
import adams.data.matlab.ArrayElementType;
import adams.data.matlab.MatlabArrayIndexSupporter;
import adams.data.matlab.MatlabUtils;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Matrix;

/**
 <!-- globalinfo-start -->
 * Retrieves the specified matrix element.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;us.hebi.matlab.mat.types.Array<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Mat5GetMatrixElement
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-index &lt;adams.core.base.Mat5ArrayElementIndex&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the single value (all dimensions specified) or array subset
 * &nbsp;&nbsp;&nbsp;to retrieve (some dimensions left empty).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-zero-based-index &lt;boolean&gt; (property: zeroBasedIndex)
 * &nbsp;&nbsp;&nbsp;If true, the index is treated as 0-based (eg 0;0;0 for first value) rather
 * &nbsp;&nbsp;&nbsp;than 1-based ones (eg 1;1;1 for first value).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-element-type &lt;BOOLEAN|BYTE|SHORT|INTEGER|LONG|FLOAT|DOUBLE&gt; (property: elementType)
 * &nbsp;&nbsp;&nbsp;Specifies the type of the value being retrieved.
 * &nbsp;&nbsp;&nbsp;default: DOUBLE
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5GetMatrixElement
  extends AbstractTransformer
  implements MatlabArrayIndexSupporter {

  private static final long serialVersionUID = -1300622968182850650L;

  /** the element index to use. */
  protected Mat5ArrayElementIndex m_Index;

  /** whether to interpret the indices as 0-based or 1-based. */
  protected boolean m_ZeroBasedIndex;

  /** the element type. */
  protected ArrayElementType m_ElementType;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Retrieves the specified matrix element.";
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

    m_OptionManager.add(
      "element-type", "elementType",
      ArrayElementType.DOUBLE);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "index", m_Index, "index: ");
    result += QuickInfoHelper.toString(this, "zeroBasedIndex", (m_ZeroBasedIndex ? "0-based" : "1-based"), ", ");
    result += QuickInfoHelper.toString(this, "elementType", m_ElementType, ", element: ");

    return result;
  }

  /**
   * Sets the index to obtain.
   *
   * @param value	the index
   */
  @Override
  public void setIndex(Mat5ArrayElementIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index to obtain.
   *
   * @return		the index
   */
  @Override
  public Mat5ArrayElementIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String indexTipText() {
    return "The index of the single value to set.";
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
    return "If true, the index is treated as 0-based (eg 0;0;0 for first value) rather than 1-based ones (eg 1;1;1 for first value).";
  }

  /**
   * Sets the type of the element to retrieve.
   *
   * @param value	the type
   */
  public void setElementType(ArrayElementType value) {
    m_ElementType = value;
    reset();
  }

  /**
   * Returns the type of the element to retrieve.
   *
   * @return		the type
   */
  public ArrayElementType getElementType() {
    return m_ElementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elementTypeTipText() {
    return "Specifies the type of the value being retrieved.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Array.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (getOptionManager().hasVariableForProperty("elementType"))
      return new Class[]{Unknown.class};

    return new Class[]{m_ElementType.getType()};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Array	array;
    Matrix	matrix;
    int[]	index;

    result = null;
    array  = m_InputToken.getPayload(Array.class);
    if (!(array instanceof Matrix))
      result = "Input is not of type " + Utils.classToString(Matrix.class) + " but " + Utils.classToString(array) + "!";

    if (m_Index.openDimensions().length > 0)
      result = "Index not fully specified: " + m_Index;

    if (result == null) {
      matrix        = (Matrix) array;
      index         = m_Index.indexValue(!m_ZeroBasedIndex);
      m_OutputToken = new Token(MatlabUtils.getElement(matrix, index, m_ElementType));
    }

    return result;
  }
}
