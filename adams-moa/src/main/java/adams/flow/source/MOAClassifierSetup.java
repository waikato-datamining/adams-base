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
 * MOAClassifierSetup.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import moa.classifiers.trees.DecisionStump;
import moa.options.ClassOption;
import weka.core.MOAUtils;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs an instance of the specified MOA classifier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;moa.classifiers.Classifier<br>
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
 * &nbsp;&nbsp;&nbsp;default: MOAClassifierSetup
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
 * <pre>-classifier &lt;moa.options.ClassOption&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The MOA classifier to output.
 * &nbsp;&nbsp;&nbsp;default: moa.classifiers.trees.DecisionStump
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAClassifierSetup
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = 1357925227105730412L;

  /** the weka classifier. */
  protected ClassOption m_Classifier;

  /** the output token. */
  protected Token m_OutputToken;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs an instance of the specified MOA classifier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "classifier", "classifier",
	    getDefaultOption());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Classifier = getDefaultOption();
  }
  
  /**
   * Returns the default classifier.
   *
   * @return		the classifier
   */
  protected moa.classifiers.Classifier getDefaultClassifier() {
    return new DecisionStump();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
	"classifier",
	'c',
	"The MOA classifier to use from within ADAMS.",
	moa.classifiers.Classifier.class,
	getDefaultClassifier().getClass().getName().replace("moa.classifiers.", ""),
	getDefaultClassifier().getClass().getName());
  }

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(ClassOption value) {
    m_Classifier.setValueViaCLIString(value.getValueAsCLIString());
    reset();
  }

  /**
   * Returns the classifier in use.
   *
   * @return		the classifier
   */
  public ClassOption getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The MOA classifier to output.";
  }

  /**
   * Returns the current classifier, based on the class option.
   *
   * @return		the classifier
   * @see		#getClassifier()
   */
  protected moa.classifiers.Classifier getCurrentClassifier() {
    return (moa.classifiers.Classifier) MOAUtils.fromOption(m_Classifier);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classifier", getCurrentClassifier().getClass());
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->moa.classifiers.Classifier.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{moa.classifiers.Classifier.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    moa.classifiers.Classifier	cls;

    result = null;

    try {
      cls           = (moa.classifiers.Classifier) MOAUtils.fromOption(m_Classifier);
      m_OutputToken = new Token(cls);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to create copy of classifier:", e);
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;
    
    result        = m_OutputToken;
    m_OutputToken = null;
    
    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <br><br>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
  }
}
