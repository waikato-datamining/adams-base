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
 * TemplateStandalone.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.flow.core.AbstractTemplate;
import adams.flow.core.ActorUtils;
import adams.flow.template.AbstractActorTemplate;
import adams.flow.template.DummyStandalone;

/**
 <!-- globalinfo-start -->
 * Generates a standalone actor&#47;sub-flow from a template.
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TemplateStandalone
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
public class TemplateStandalone
  extends AbstractTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2906214610648390363L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Generates a standalone actor/sub-flow from a template.";
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
   * Initializes the template for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpTemplate() {
    String		result;
   
    result = super.setUpTemplate();
    
    if (result == null) {
      if (!ActorUtils.isStandalone(m_Actor))
	result = "Template '" + m_Template + "' does not generate a standalone actor: " + m_Actor.getClass().getName();
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    String		result;

    result = null;
    
    if (m_Actor == null)
      result = setUpTemplate();
    
    if (result == null)
      result = m_Actor.execute();
    
    return result;
  }
}
