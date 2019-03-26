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
 * Scripted.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.pdfgenerate;

import adams.core.scripting.AbstractScriptingHandler;
import adams.core.scripting.Dummy;
import adams.flow.core.Unknown;

import java.io.File;

/**
 * A PDF generator that uses any scripting handler for processing the
 * objects with a script located in the specified file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Scripted
  extends AbstractScriptedPDFGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient AbstractPDFGenerator m_GeneratorObject;

  /** the scripting handler to use. */
  protected AbstractScriptingHandler m_Handler;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"A PDF generator that uses any scripting handler for processing the "
	+ "objects with a script located in the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      new Dummy());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
        "The options for the script; must consist of 'key=value' pairs "
      + "separated by blanks; the value of 'key' can be accessed via the "
      + "'getAdditionalOptions().getXYZ(\"key\")' method in the script actor.";
  }

  /**
   * Sets the handler to use for scripting.
   *
   * @param value 	the handler
   */
  public void setHandler(AbstractScriptingHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Gets the handler to use for scripting.
   *
   * @return 		the handler
   */
  public AbstractScriptingHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String handlerTipText() {
    return "The handler to use for scripting.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;
    
    result = m_Handler.loadScriptObject(
	AbstractPDFGenerator.class,
	m_ScriptFile, 
	m_ScriptOptions, 
	getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * The type of data the generator accepts.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    if (m_GeneratorObject != null)
      return m_GeneratorObject.accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    // TODO checks?
    return null;
  }

  /**
   * Hook method for checking the objects before processing them.
   *
   * @param objects	the objects to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(Object[] objects) {
    String	result;

    result = super.check(objects);

    if (result == null)
      m_GeneratorObject = (AbstractPDFGenerator) m_ScriptObject;

    return result;
  }

  /**
   * Processes the objects to generate the PDF.
   *
   * @param objects	the objects to process
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doProcess(Object[] objects, File outputFile) {
    return m_GeneratorObject.process(objects, outputFile);
  }
  
  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();
    
    m_GeneratorObject = null;
  }
}
