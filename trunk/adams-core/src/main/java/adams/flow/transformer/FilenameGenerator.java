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
 * FilenameGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.AbstractFilenameGenerator;
import adams.core.io.FixedFilenameGenerator;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Passes the incoming token through the generator to generate and forward a filename.<br/>
 * <br/>
 * See also:<br/>
 * adams.flow.source.FilenameGenerator
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: FilenameGenerator
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.core.io.AbstractFilenameGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the filename.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.FixedFilenameGenerator
 * </pre>
 * 
 * <pre>-absolute &lt;boolean&gt; (property: absolute)
 * &nbsp;&nbsp;&nbsp;Whether to output absolute or placeholder filenames.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilenameGenerator
  extends AbstractTransformer
  implements ClassCrossReference {
  
  /** for serialization. */
  private static final long serialVersionUID = -5969795295785887230L;

  /** the generator to use. */
  protected AbstractFilenameGenerator m_Generator;

  /** whether to output absolute or placeholder filenames. */
  protected boolean m_Absolute;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Passes the incoming token through the generator to generate and forward a filename.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new FixedFilenameGenerator());

    m_OptionManager.add(
	    "absolute", "absolute",
	    true);
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.source.FilenameGenerator.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "generator", m_Generator);
    result += QuickInfoHelper.toString(this, "absolute", (m_Absolute ? ", absolute" : ", placeholder"));
    
    return result;
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractFilenameGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
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
    return "The generator to use for generating the filename.";
  }

  /**
   * Sets whether to output absolute or placeholder filenames.
   *
   * @param value	true if to output absolute filenames
   */
  public void setAbsolute(boolean value) {
    m_Absolute = value;
    reset();
  }

  /**
   * Returns whether to output absolute or placeholder filenames.
   *
   * @return		true if to output absolute filenames
   */
  public boolean getAbsolute() {
    return m_Absolute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String absoluteTipText() {
    return "Whether to output absolute or placeholder filenames.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    String		result;
    String		filename;
    PlaceholderFile	file;

    result = null;
    
    try {
      filename = m_Generator.generate(m_InputToken.getPayload());
      file     = new PlaceholderFile(filename);
      if (m_Absolute)
	m_OutputToken = new Token(file.getAbsolutePath());
      else
	m_OutputToken = new Token(file.toString());
    }
    catch (Exception e) {
      result = handleException("Failed to generate filename!", e);
    }
    
    return result;
  }
}
