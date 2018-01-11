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
 * WekaClassifierSetup.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs an instance of the specified classifier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierSetup
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
 * <pre>-property &lt;adams.core.base.BaseString&gt; [-property ...] (property: properties)
 * &nbsp;&nbsp;&nbsp;The properties to update with the values associated with the specified values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; [-variable ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The names of the variables to update the properties with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 * 
 * <pre>-classifier &lt;weka.classifiers.Classifier&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The Weka classifier to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.rules.ZeroR
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierSetup
  extends AbstractSimpleSourceWithPropertiesUpdating {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the weka classifier. */
  protected weka.classifiers.Classifier m_Classifier;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs an instance of the specified classifier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    new weka.classifiers.rules.ZeroR());
  }

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(weka.classifiers.Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the classifier in use.
   *
   * @return		the classifier
   */
  public weka.classifiers.Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The Weka classifier to train on the input data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    if (!result.isEmpty())
      result += ", ";
    result += QuickInfoHelper.toString(this, "classifier", Shortening.shortenEnd(OptionUtils.getShortCommandLine(m_Classifier), 40));

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.classifiers.Classifier.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{weka.classifiers.Classifier.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    weka.classifiers.Classifier	cls;

    try {
      cls    = (weka.classifiers.Classifier) OptionUtils.shallowCopy(m_Classifier);
      result = setUpContainersIfNecessary(cls);
      if (result == null)
        result = updateObject(cls);
      if (result == null)
	m_OutputToken = new Token(cls);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to create copy of classifier:", e);
    }

    return result;
  }
}
