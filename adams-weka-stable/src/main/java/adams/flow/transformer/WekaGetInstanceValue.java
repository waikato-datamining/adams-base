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
 * WekaGetInstanceValue.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Attribute;
import weka.core.Instance;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Retrieves a value from a WEKA Instance object.<br>
 * Notes:<br>
 * - date and relational values are forwarded as strings<br>
 * - missing values are output as '?' (without the single quotes)<br>
 * - the 'attribute name' option overrides the 'index' option
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaGetInstanceValue
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
 * <pre>-index &lt;java.lang.String&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The 1-based index of the attribute value to retrieve from the WEKA Instance.
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 *
 * <pre>-attribute-name &lt;java.lang.String&gt; (property: attributeName)
 * &nbsp;&nbsp;&nbsp;The name of the attribute to get the value for.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGetInstanceValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3057815118900209749L;

  /** the index of the attribute to get the value from the Instance. */
  protected Index m_Index;

  /** the name of the attribute to get the value from the Instance. */
  protected String m_AttributeName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Retrieves a value from a WEKA Instance object.\n"
      + "Notes:\n"
      + "- date and relational values are forwarded as strings\n"
      + "- missing values are output as '?' (without the single quotes)\n"
      + "- the 'attribute name' option overrides the 'index' option";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "index",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "attribute-name", "attributeName",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Index = new Index("last");
  }

  /**
   * Sets the 1-based index of the attribute value to retrieve from the Instance.
   *
   * @param value	the 1-based index
   */
  public void setIndex(Index value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the 1-based index of the attribuate value to retrieve from the
   * Instance.
   *
   * @return		the 1-based index
   */
  public Index getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The 1-based index of the attribute value to retrieve from the WEKA Instance.";
  }

  /**
   * Sets the name of the attribute to get the value for.
   *
   * @param value	the name
   */
  public void setAttributeName(String value) {
    m_AttributeName = value;
    reset();
  }

  /**
   * Returns the name of the attribute to get the value for.
   *
   * @return		the name
   */
  public String getAttributeName() {
    return m_AttributeName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeNameTipText() {
    return "The name of the attribute to get the value for.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "attributeName", (m_AttributeName.length() > 0 ? m_AttributeName : null));
    if (result != null)
      return result;

    return QuickInfoHelper.toString(this, "index", m_Index);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Double.class, java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Double.class, String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instance	inst;
    int		index;

    result = null;

    inst = (Instance) m_InputToken.getPayload();

    try {
      if (m_AttributeName.length() > 0) {
	index = inst.dataset().attribute(m_AttributeName).index();
      }
      else {
	m_Index.setMax(inst.numAttributes());
	index = m_Index.getIntIndex();
      }
      if (inst.isMissing(index)) {
	m_OutputToken = new Token("?");
      }
      else {
	switch (inst.attribute(index).type()) {
	  case Attribute.NUMERIC:
	    m_OutputToken = new Token(inst.value(index));
	    break;

	  case Attribute.DATE:
	  case Attribute.NOMINAL:
	  case Attribute.STRING:
	  case Attribute.RELATIONAL:
	    m_OutputToken = new Token(inst.stringValue(index));
	    break;

	  default:
	    result = "Unhandled attribute type: " + inst.attribute(index).type();
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to obtain value from instance:\n" + inst, e);
    }

    return result;
  }
}
