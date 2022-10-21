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
 * ArrayToCollection.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseClassname;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 <!-- globalinfo-start -->
 * Turns an array into a collection.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Collection<br>
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
 * &nbsp;&nbsp;&nbsp;default: ArrayToCollection
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
 * <pre>-collection-class &lt;adams.core.base.BaseClassname&gt; (property: collectionClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the collection.
 * &nbsp;&nbsp;&nbsp;default: java.util.ArrayList
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayToCollection
  extends AbstractTransformer
  implements ClassCrossReference {

  private static final long serialVersionUID = 558755282327118795L;

  /** the class for the array. */
  protected BaseClassname m_CollectionClass;

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
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{CollectionToArray.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "collection-class", "collectionClass",
      new BaseClassname(ArrayList.class));
  }

  /**
   * Sets the class for the collection.
   *
   * @param value	the classname
   */
  public void setCollectionClass(BaseClassname value) {
    m_CollectionClass = value;
    reset();
  }

  /**
   * Returns the class for the collection.
   *
   * @return		the classname
   */
  public BaseClassname getCollectionClass() {
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
   * Returns the classes that are accepted as input.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Returns the classes that are generated as output.
   *
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Collection.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "collectionClass", m_CollectionClass, "collection: ");
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	array;
    Collection	coll;
    int		i;
    int		len;

    result = null;
    array  = m_InputToken.getPayload();
    try {
      coll = (Collection) m_CollectionClass.classValue().getDeclaredConstructor().newInstance();
      len = Array.getLength(array);
      for (i = 0; i < len; i++)
	coll.add(Array.get(array, i));
      m_OutputToken = new Token(coll);
    }
    catch (Exception e) {
      result = handleException("Failed to convert array to collection (" + m_CollectionClass + ")", e);
    }

    return result;
  }
}
