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
 * MOARegressorSetup.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import moa.classifiers.trees.FIMTDD;
import moa.options.ClassOption;
import weka.core.MOAUtils;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOARegressorSetup
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = 1357925227105730412L;

  /** the weka classifier. */
  protected ClassOption m_Regressor;

  /** the output token. */
  protected Token m_OutputToken;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs an instance of the specified MOA regressor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regressor", "regressor",
	    getDefaultOption());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Regressor = getDefaultOption();
  }
  
  /**
   * Returns the default classifier.
   *
   * @return		the classifier
   */
  protected moa.classifiers.Classifier getDefaultRegressor() {
    return new FIMTDD();
  }

  /**
   * Returns the default class option.
   *
   * @return		the option
   */
  protected ClassOption getDefaultOption() {
    return new ClassOption(
	"regressor",
	'r',
	"The MOA regressor to use from within ADAMS.",
	moa.classifiers.Regressor.class,
	getDefaultRegressor().getClass().getName().replace("moa.classifiers.", ""),
	getDefaultRegressor().getClass().getName());
  }

  /**
   * Sets the regressor to use.
   *
   * @param value	the regressor
   */
  public void setRegressor(ClassOption value) {
    m_Regressor.setValueViaCLIString(value.getValueAsCLIString());
    reset();
  }

  /**
   * Returns the regressor in use.
   *
   * @return		the regressor
   */
  public ClassOption getRegressor() {
    return m_Regressor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The MOA regressor to output.";
  }

  /**
   * Returns the current regressor, based on the class option.
   *
   * @return		the regressor
   * @see		#getRegressor()
   */
  protected moa.classifiers.Regressor getCurrentRegressor() {
    return (moa.classifiers.Regressor) MOAUtils.fromOption(m_Regressor);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regressor", getCurrentRegressor().getClass());
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start --><!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{moa.classifiers.Regressor.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    moa.classifiers.Regressor	cls;

    result = null;

    try {
      cls           = (moa.classifiers.Regressor) MOAUtils.fromOption(m_Regressor);
      m_OutputToken = new Token(cls);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to create copy of regressor:", e);
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
