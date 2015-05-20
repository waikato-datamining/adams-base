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
 * FileExists.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.io.AbstractFilenameGenerator;
import adams.core.io.NullFilenameGenerator;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the file exists.<br>
 * If a filename generator other than adams.core.io.NullFilenameGenerator is specified, then this takes precedence over the supplied filename (uses the token passing through).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to look for.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-generator &lt;adams.core.io.AbstractFilenameGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the filename; uses the token passing 
 * &nbsp;&nbsp;&nbsp;through.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.NullFilenameGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileExists
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -6986050060604039765L;

  /** the file to look for. */
  protected PlaceholderFile m_File;

  /** the filename generator. */
  protected AbstractFilenameGenerator m_Generator;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates to 'true' if the file exists.\n"
	+ "If a filename generator other than "
        + NullFilenameGenerator.class.getName() + " is specified, then this "
        + "takes precedence over the supplied filename (uses the token passing through).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "file",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "generator", "generator",
	    new NullFilenameGenerator());
  }

  /**
   * Sets the file to look for.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the file to look for.
   *
   * @return		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTipText() {
    return "The file to look for.";
  }

  /**
   * Sets the generator to use (ignored if {@link NullFilenameGenerator}).
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractFilenameGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use (ignored if {@link NullFilenameGenerator}).
   *
   * @return		the generator
   */
  public AbstractFilenameGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for generating the filename; uses the token passing through.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "file", m_File, "file: ");
    result += QuickInfoHelper.toString(this, "generator", m_Generator, ", generator: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (m_File == null)
	result = "No file provided!";
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    PlaceholderFile	file;
    
    if (!(m_Generator instanceof NullFilenameGenerator))
      file = new PlaceholderFile(m_Generator.generate(token.getPayload()));
    else
      file = m_File;
    
    return (file.exists() && !file.isDirectory());
  }
}
