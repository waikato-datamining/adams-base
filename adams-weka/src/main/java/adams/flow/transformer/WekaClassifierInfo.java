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
 * WekaClassifierInfo.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;
import weka.classifiers.Classifier;
import weka.classifiers.Sourcable;
import weka.core.Drawable;

/**
 <!-- globalinfo-start -->
 * Outputs information of a trained weka.classifiers.Classifier object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
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
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierInfo
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;MODEL|GRAPH|SOURCE_CODE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: MODEL
 * </pre>
 * 
 * <pre>-source-code-class &lt;java.lang.String&gt; (property: sourceCodeClass)
 * &nbsp;&nbsp;&nbsp;The name of the Java class when generating source code.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierModel
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierInfo
  extends AbstractTransformer
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** model. */
    MODEL,
    /** graph (if available). */
    GRAPH,
    /** source code (if available). */
    SOURCE_CODE,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /** the name of the source code class. */
  protected String m_SourceCodeClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Outputs information of a trained weka.classifiers.Classifier object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.MODEL);

    m_OptionManager.add(
	    "source-code-class", "sourceCodeClass",
	    "WekaClassifierModel");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "sourceCodeClass", m_SourceCodeClass, ", class: ");

    return result;
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Sets the class name for the generated source code.
   *
   * @param value	the name
   */
  public void setSourceCodeClass(String value) {
    m_SourceCodeClass = value;
    reset();
  }

  /**
   * Returns the class name for the generated source code.
   *
   * @return		the name
   */
  public String getSourceCodeClass() {
    return m_SourceCodeClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceCodeClassTipText() {
    return "The name of the Java class when generating source code.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Classifier.class, adams.flow.container.WekaModelContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Classifier.class, WekaModelContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    switch (m_Type) {
      case MODEL:
      case GRAPH:
      case SOURCE_CODE:
	return new Class[]{String.class};

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Classifier		cls;

    result = null;

    cls = null;
    if (m_InputToken.getPayload() instanceof Classifier)
      cls = (Classifier) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof  WekaModelContainer)
      cls = (Classifier) ((WekaModelContainer) m_InputToken.getPayload()).getValue(WekaModelContainer.VALUE_MODEL);
    else
      result = "Unhandled class: " + Utils.classToString(m_InputToken.getPayload().getClass());

    if (result == null) {
      switch (m_Type) {
	case MODEL:
	  m_OutputToken = new Token(cls.toString());
	  break;

	case GRAPH:
	  try {
	    if (cls instanceof Drawable)
	      m_OutputToken = new Token(((Drawable) cls).graph());
	  }
	  catch (Exception e) {
	    result = handleException("Failed to obtain graph from classifier!", e);
	  }
	  break;

	case SOURCE_CODE:
	  try {
	    if (cls instanceof Sourcable)
	      m_OutputToken = new Token(((Sourcable) cls).toSource(m_SourceCodeClass));
	  }
	  catch (Exception e) {
	    result = handleException("Failed to obtain source code from classifier!", e);
	  }
	  break;

	default:
	  result = "Unhandled info type: " + m_Type;
      }
    }

    return result;
  }
}
