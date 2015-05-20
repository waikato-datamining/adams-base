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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import weka.classifiers.timeseries.AbstractForecaster;
import adams.flow.core.Token;
import adams.flow.source.wekaforecastersetup.AbstractForecasterGenerator;
import adams.flow.source.wekaforecastersetup.WekaForecasterGenerator;

/**
 <!-- globalinfo-start -->
 * Outputs a configured instance of a Weka Forecaster.
 * <br><br>
 <!-- globalinfo-end -->
 * 
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.timeseries.AbstractForecaster<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaForecasterSetup
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-generator &lt;adams.flow.source.wekaforecastersetup.AbstractForecasterGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The forecaster generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.wekaforecastersetup.WekaForecasterGenerator -classifier \"weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8\" -lag-maker adams.flow.core.LagMakerOptions
 * </pre>
 * 
 <!-- options-end -->
 * 
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecasterSetup
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the generator. */
  protected AbstractForecasterGenerator m_Generator;

  /**
   * Returns a string describing the object.
   * 
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs a configured instance of a Weka Forecaster.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"generator", "generator", 
	new WekaForecasterGenerator());
  }

  /**
   * Sets the generator to use.
   * 
   * @param value	the generator
   */
  public void setGenerator(AbstractForecasterGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator in use.
   * 
   * @return 		the generator
   */
  public AbstractForecasterGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String generatorTipText() {
    return "The forecaster generator to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * 
   * @return 		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result = m_Generator.getQuickInfo();
    if (result != null)
      result = m_Generator.getClass().getSimpleName() + ": " + result;
    else
      result = m_Generator.getClass().getSimpleName();
    
    return result;
  }

  /**
   * Returns the class of objects that it generates.
   * 
   * @return 		<!-- flow-generates-start -->weka.classifiers.timeseries.AbstractForecaster.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{AbstractForecaster.class};
  }

  /**
   * Executes the flow item.
   * 
   * @return 		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 		result;
    AbstractForecaster 	forecaster;

    result = null;

    try {
      forecaster = m_Generator.generate();
      m_OutputToken = new Token(forecaster);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to configure Forecaster:", e);
    }

    return result;
  }
}
