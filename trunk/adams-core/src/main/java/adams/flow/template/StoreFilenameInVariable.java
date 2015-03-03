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
 * StoreFilenameInVariable.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.VariableName;
import adams.data.conversion.ReplaceFileExtension;
import adams.flow.control.Tee;
import adams.flow.core.AbstractActor;
import adams.flow.transformer.BaseName;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SetVariable;

/**
 <!-- globalinfo-start -->
 * Creates a sub-flow that stores the filename passing through in a user-specified variable.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-variable &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to store the report value in.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 * <pre>-strip-path (property: stripPath)
 * &nbsp;&nbsp;&nbsp;If enabled, the path gets stripped from the filename.
 * </pre>
 * 
 * <pre>-strip-extension (property: stripExtension)
 * &nbsp;&nbsp;&nbsp;If enabled, the extension gets stripped from the filename.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StoreFilenameInVariable
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2083397920389605009L;

  /** the variable to store the value in. */
  protected VariableName m_VariableName;

  /** whether to strip the path. */
  protected boolean m_StripPath;

  /** whether to strip the extension. */
  protected boolean m_StripExtension;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a sub-flow that stores the filename passing through in a user-specified variable.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "variable", "variableName",
	    new VariableName());

    m_OptionManager.add(
	    "strip-path", "stripPath",
	    false);

    m_OptionManager.add(
	    "strip-extension", "stripExtension",
	    false);
  }

  /**
   * Sets the variable to set.
   *
   * @param value	the variable
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the variable to set.
   *
   * @return		the variable
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNameTipText() {
    return "The variable to store the report value in.";
  }

  /**
   * Sets whether to strip the path from the filename.
   *
   * @param value	true if to strip
   */
  public void setStripPath(boolean value) {
    m_StripPath = value;
    reset();
  }

  /**
   * Returns whether to strip the path from the filename.
   *
   * @return		true if stripped
   */
  public boolean getStripPath() {
    return m_StripPath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String stripPathTipText() {
    return "If enabled, the path gets stripped from the filename.";
  }

  /**
   * Sets whether to strip the extension from the filename.
   *
   * @param value	true if to strip
   */
  public void setStripExtension(boolean value) {
    m_StripExtension = value;
    reset();
  }

  /**
   * Returns whether to strip the extension from the filename.
   *
   * @return		true if stripped
   */
  public boolean getStripExtension() {
    return m_StripExtension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String stripExtensionTipText() {
    return "If enabled, the extension gets stripped from the filename.";
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated actor
   */
  @Override
  protected AbstractActor doGenerate() {
    Tee				result;
    BaseName			base;
    SetVariable			setvar;
    Convert			convert;
    ReplaceFileExtension	ext;
    
    result = new Tee();
    result.setName("Store filename in " + m_VariableName.paddedValue());
    
    if (m_StripPath) {
      base = new BaseName();
      base.setRemoveExtension(m_StripExtension);
      result.add(base);
    }
    else {
      if (m_StripExtension) {
	ext = new ReplaceFileExtension();
	ext.setExtension("");
	convert = new Convert();
	convert.setConversion(ext);
	result.add(convert);
      }
    }

    setvar = new SetVariable();
    setvar.setVariableName(m_VariableName);
    result.add(setvar);
    
    return result;
  }
}
