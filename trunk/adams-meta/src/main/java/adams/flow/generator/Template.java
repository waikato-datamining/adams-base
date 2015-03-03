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
 * Template.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.generator;

import adams.flow.core.AbstractActor;
import adams.flow.template.AbstractActorTemplate;
import adams.flow.template.DummyStandalone;

/**
 <!-- globalinfo-start -->
 * Uses the specified flow template to generate the sub-flow.
 * <p/>
 <!-- globalinfo-end -->
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
 * <pre>-template &lt;adams.flow.template.AbstractActorTemplate&gt; (property: template)
 * &nbsp;&nbsp;&nbsp;The template to use for generating the actual actor.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.template.DummyStandalone
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Template
  extends AbstractFlowGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 1332232224632995724L;
  
  /** the template. */
  protected AbstractActorTemplate m_Template;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified flow template to generate the sub-flow.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "template", "template",
	    getDefaultTemplate());
  }

  /**
   * Returns the default template to use.
   * 
   * @return		the template
   */
  protected AbstractActorTemplate getDefaultTemplate() {
    return new DummyStandalone();
  }
  
  /**
   * Sets the name of the global actor to use.
   *
   * @param value 	the global name
   */
  public void setTemplate(AbstractActorTemplate value) {
    m_Template = value;
    reset();
  }

  /**
   * Returns the name of the global actor in use.
   *
   * @return 		the global name
   */
  public AbstractActorTemplate getTemplate() {
    return m_Template;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String templateTipText() {
    return "The template to use for generating the actual actor.";
  }

  /**
   * Generates the flow.
   *
   * @return		the flow
   */
  @Override
  protected AbstractActor doGenerate() {
    return m_Template.generate();
  }
}
