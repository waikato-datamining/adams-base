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
 * ColorProvider.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.awt.Color;

import adams.core.QuickInfoHelper;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;

/**
 <!-- globalinfo-start -->
 * Uses the configured color provider to generate a number of colors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.awt.Color<br/>
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
 * &nbsp;&nbsp;&nbsp;default: ColorProvider
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
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the colors in an array rather than one-by-one.
 * </pre>
 * 
 * <pre>-provider &lt;adams.gui.visualization.core.AbstractColorProvider&gt; (property: provider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for generating the colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 * <pre>-num-colors &lt;int&gt; (property: numColors)
 * &nbsp;&nbsp;&nbsp;The number of colors to generate with the color provider.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ColorProvider
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3505768725369077351L;

  /** the color provider to use. */
  protected AbstractColorProvider m_Provider;
  
  /** the number of colors to generate. */
  protected int m_NumColors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the configured color provider to generate a number of colors.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "provider", "provider",
	    new DefaultColorProvider());

    m_OptionManager.add(
	    "num-colors", "numColors",
	    10, 1, null);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs the colors in an array rather than one-by-one.";
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the provider
   */
  public void setProvider(AbstractColorProvider value) {
    m_Provider = value;
    reset();
  }

  /**
   * Returns the color provider in use.
   *
   * @return		the provider
   */
  public AbstractColorProvider getProvider() {
    return m_Provider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String providerTipText() {
    return "The color provider to use for generating the colors.";
  }

  /**
   * Sets the number of colors to generate.
   *
   * @param value	the number of colors
   */
  public void setNumColors(int value) {
    if (value > 0) {
      m_NumColors = value;
      reset();
    }
    else {
      getLogger().severe("Must at least generate 1 color, provided: " + value);
    }
  }

  /**
   * Returns the number of colors to generate.
   *
   * @return		the number of colors
   */
  public int getNumColors() {
    return m_NumColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColorsTipText() {
    return "The number of colors to generate with the color provider.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "provider", m_Provider);
    result += QuickInfoHelper.toString(this, "numColors", m_NumColors, ", # colors: ");
    result += QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array", ", ");
    
    return result;
  }
  
  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Color.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    int		i;
    
    result = null;
    
    m_Queue.clear();
    m_Provider.resetColors();
    
    for (i = 0; i < m_NumColors; i++)
      m_Queue.add(m_Provider.next());
    
    return result;
  }
}
