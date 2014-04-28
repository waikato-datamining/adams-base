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
 * Convert.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.conversion.Conversion;
import adams.data.conversion.ConversionWithInitialization;
import adams.data.conversion.UnknownToUnknown;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Converts data from one format into another.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Convert
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
 * <pre>-conversion &lt;adams.data.conversion.Conversion&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The type of conversion to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.UnknownToUnknown
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Convert
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1704879993786242375L;

  /** the type of conversion. */
  protected Conversion m_Conversion;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts data from one format into another.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "conversion", "conversion",
	    new UnknownToUnknown());
  }

  /**
   * Sets the type of conversion to perform.
   *
   * @param value	the type of conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
    m_Conversion.setOwner(this);
    reset();
  }

  /**
   * Returns the type of conversion to perform.
   *
   * @return		the type of conversion
   */
  public Conversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The type of conversion to perform.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "conversion", m_Conversion);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    if (getOptionManager().getVariableForProperty("conversion") != null)
      return new Class[]{Unknown.class};
    else
      return new Class[]{m_Conversion.accepts()};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated output
   */
  public Class[] generates() {
    if (getOptionManager().getVariableForProperty("conversion") != null)
      return new Class[]{Unknown.class};
    else
      return new Class[]{m_Conversion.generates()};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (m_Conversion instanceof ConversionWithInitialization)
	result = ((ConversionWithInitialization) m_Conversion).setUp();
    }
    
    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    m_Conversion.setInput(m_InputToken.getPayload());
    result = m_Conversion.convert();
    if (result != null)
      result = getFullName() + ": " + result;
    if ((result == null) && (m_Conversion.getOutput() != null))
      m_OutputToken = new Token(m_Conversion.getOutput());
    m_Conversion.cleanUp();

    if (isLoggingEnabled())
      getLogger().info(m_InputToken.getPayload() + " -> " + m_OutputToken.getPayload());
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    m_Conversion.stopExecution();
  }
}
