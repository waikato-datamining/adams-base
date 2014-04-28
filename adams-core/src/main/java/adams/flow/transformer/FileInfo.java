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
 * FileInfo.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.Date;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseDateTime;
import adams.core.io.PlaceholderFile;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs information for a path (file&#47;directory).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Long<br/>
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
 * &nbsp;&nbsp;&nbsp;default: FileInfo
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-type &lt;SIZE|LAST_MODIFIED|IS_READABLE|IS_WRITEABLE|IS_EXECUTABLE|IS_HIDDEN|IS_DIRECTORY|IS_FILE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: SIZE
 * </pre>
 * 
 * <pre>-value-true &lt;java.lang.String&gt; (property: valueTrue)
 * &nbsp;&nbsp;&nbsp;The string to output in case query types starting with 'IS_' evaluated to 
 * &nbsp;&nbsp;&nbsp;'true'.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-value-false &lt;java.lang.String&gt; (property: valueFalse)
 * &nbsp;&nbsp;&nbsp;The string to output in case query types starting with 'IS_' evaluated to 
 * &nbsp;&nbsp;&nbsp;'false'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileInfo
  extends AbstractTransformer
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** the size. */
    SIZE,
    /** modified timestamp. */
    LAST_MODIFIED,
    /** whether file is readable. */
    IS_READABLE,
    /** whether file is writeable. */
    IS_WRITEABLE,
    /** whether executable. */
    IS_EXECUTABLE,
    /** whether the file is hidden. */
    IS_HIDDEN,
    /** whether the path represents a directory. */
    IS_DIRECTORY,
    /** whether the path representts a file. */
    IS_FILE
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /** the string to output in case of "IS_" queries evaluate to TRUE. */
  protected String m_ValueTrue;

  /** the string to output in case of "IS_" queries evaluate to FALSE. */
  protected String m_ValueFalse;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information for a path (file/directory).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    InfoType.SIZE);

    m_OptionManager.add(
	    "value-true", "valueTrue",
	    "true");

    m_OptionManager.add(
	    "value-false", "valueFalse",
	    "false");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Sets the string to output if query evaluates to "true'.
   *
   * @param value	the string
   */
  public void setValueTrue(String value) {
    m_ValueTrue = value;
    reset();
  }

  /**
   * Returns the string to output if query evaluates to "true'.
   *
   * @return		the string
   */
  public String getValueTrue() {
    return m_ValueTrue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTrueTipText() {
    return "The string to output in case query types starting with 'IS_' evaluated to 'true'.";
  }

  /**
   * Sets the string to output if query evaluates to "false'.
   *
   * @param value	the string
   */
  public void setValueFalse(String value) {
    m_ValueFalse = value;
    reset();
  }

  /**
   * Returns the string to output if query evaluates to "false'.
   *
   * @return		the string
   */
  public String getValueFalse() {
    return m_ValueFalse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueFalseTipText() {
    return "The string to output in case query types starting with 'IS_' evaluated to 'false'.";
  }

  /**
   * Returns the class of the items that the actor generates.
   *
   * @return		the class
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case SIZE:
	return new Class[]{Long.class};
	
      case LAST_MODIFIED:
	return new Class[]{BaseDateTime.class};
	
      case IS_READABLE:
      case IS_WRITEABLE:
      case IS_EXECUTABLE:
      case IS_HIDDEN:
      case IS_DIRECTORY:
      case IS_FILE:
	return new Class[]{String.class};

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.io.File.class, java.lang.String.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{File.class, String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		file;

    result = null;

    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    
    switch (m_Type) {
      case SIZE:
	m_OutputToken = new Token(file.length());
	break;
      
      case LAST_MODIFIED:
	m_OutputToken = new Token(new BaseDateTime(new Date(file.lastModified())));
	break;
	
      case IS_READABLE:
	m_OutputToken = new Token(file.canRead() ? m_ValueTrue : m_ValueFalse);
	break;
	
      case IS_WRITEABLE:
	m_OutputToken = new Token(file.canWrite() ? m_ValueTrue : m_ValueFalse);
	break;
	
      case IS_EXECUTABLE:
	m_OutputToken = new Token(file.canExecute() ? m_ValueTrue : m_ValueFalse);
	break;
	
      case IS_HIDDEN:
	m_OutputToken = new Token(file.isHidden() ? m_ValueTrue : m_ValueFalse);
	break;
	
      case IS_FILE:
	m_OutputToken = new Token(file.isFile() ? m_ValueTrue : m_ValueFalse);
	break;
	
      case IS_DIRECTORY:
	m_OutputToken = new Token(file.isDirectory() ? m_ValueTrue : m_ValueFalse);
	break;

      default:
	result = "Unhandled info type: " + m_Type;
    }

    return result;
  }
}
