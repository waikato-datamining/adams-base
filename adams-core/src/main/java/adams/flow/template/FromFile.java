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
 * FromFile.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Merely loads an actor from a file.
 * <br><br>
 <!-- globalinfo-end -->
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
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-template &lt;adams.core.io.PlaceholderFile&gt; (property: templateFile)
 * &nbsp;&nbsp;&nbsp;The template file to load.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FromFile
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = -2578720795833851960L;

  /** the file to load. */
  protected PlaceholderFile m_TemplateFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Merely loads an actor from a file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "template", "templateFile",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the template file to load.
   *
   * @param value	the file
   */
  public void setTemplateFile(PlaceholderFile value) {
    m_TemplateFile = value;
    reset();
  }

  /**
   * Returns the template file currently to load.
   *
   * @return		the file
   */
  public PlaceholderFile getTemplateFile() {
    return m_TemplateFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String templateFileTipText() {
    return "The template file to load.";
  }

  /**
   * Hook before generating the actor.
   * <br><br>
   * Checks whether the template file exists.
   */
  protected void preGenerate() {
    String	variable;
    
    super.preGenerate();

    variable = getOptionManager().getVariableForProperty("templateFile");
    if (variable == null) {
      if (!m_TemplateFile.isFile())
	throw new IllegalStateException(
	    "'" + m_TemplateFile + "' is not a file!");
    }
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  protected Actor doGenerate() {
    return ActorUtils.read(m_TemplateFile.getAbsolutePath());
  }
}
