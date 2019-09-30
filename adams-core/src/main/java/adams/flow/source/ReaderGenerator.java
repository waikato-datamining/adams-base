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
 * ReaderGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;
import adams.flow.sink.CloseReader;
import adams.flow.source.readersource.AbstractReaderGenerator;
import adams.flow.source.readersource.FromFile;

import java.io.InputStream;
import java.io.Reader;

/**
 <!-- globalinfo-start -->
 * Uses the specified generator to instantiate a reader instance.<br>
 * <br>
 * See also:<br>
 * adams.flow.sink.CloseReader
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.io.InputStream<br>
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
 * &nbsp;&nbsp;&nbsp;default: ReaderGenerator
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-generator &lt;adams.flow.source.readersource.AbstractReaderGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.readersource.FromFile
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReaderGenerator
  extends AbstractSimpleSource
  implements ClassCrossReference {

  private static final long serialVersionUID = 7558795661303788547L;

  /** the generator. */
  protected AbstractReaderGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified generator to instantiate a reader instance.\n"
      + "May have to be closed again, using " + Utils.classToString(CloseReader.class);
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{CloseReader.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new FromFile());
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractReaderGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
   *
   * @return		the generator
   */
  public AbstractReaderGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{InputStream.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Reader 	reader;

    result = null;
    try {
      reader = m_Generator.generate();
      m_OutputToken = new Token(reader);
    }
    catch (Exception e) {
      result = handleException("Failed to instantiate reader!", e);
    }

    return result;
  }
}
