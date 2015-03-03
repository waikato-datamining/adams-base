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
 * Diff.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import adams.core.DiffUtils;
import adams.core.DiffUtils.SideBySideDiff;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Compares two files to two string arrays and generates a diff representation.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[][]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Diff
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
 * <pre>-type &lt;BRIEF|UNIFIED|SIDE_BY_SIDE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of diff string to generate.
 * &nbsp;&nbsp;&nbsp;default: UNIFIED
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Diff
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5101920243595168374L;

  /** 
   * The type of diff string to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum DiffType {
    /** brief. */
    BRIEF,
    /** unified. */
    UNIFIED,
    /** side-by-side. */
    SIDE_BY_SIDE
  }
  
  /** the type of diff to generate. */
  protected DiffType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Compares two files to two string arrays and generates a diff representation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    DiffType.UNIFIED);
  }

  /**
   * Sets the type of diff string to generate.
   *
   * @param value	the type
   */
  public void setType(DiffType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of diff string to generate.
   *
   * @return		the type
   */
  public DiffType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of diff string to generate.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String[].class, java.io.File[].class, java.lang.String[][].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class, String[][].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the class that gets generated
   */
  public Class[] generates() {
    switch (m_Type) {
      case UNIFIED:
	return new Class[]{String.class};
      case SIDE_BY_SIDE:
	return new Class[]{SideBySideDiff.class};
      case BRIEF:
	return new Class[]{Boolean.class};
      default:
	throw new IllegalStateException("Unhandled diff type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[][]		strings;
    File[]		files;
    String[]		filesStr;
    List<String>	left;
    List<String>	right;

    result = null;

    files    = null;
    filesStr = null;
    strings  = null;
    left     = null;
    right    = null;
    if (m_InputToken.getPayload() instanceof File[]) {
      files = (File[]) m_InputToken.getPayload();
      if (files.length != 2) {
	result = "Diff requires exactly two files, provided: " + files.length;
      }
      else {
	left  = FileUtils.loadFromFile(files[0]);
	right = FileUtils.loadFromFile(files[1]);
      }
    }
    else if (m_InputToken.getPayload() instanceof String[]) {
      filesStr = (String[]) m_InputToken.getPayload();
      if (filesStr.length != 2) {
	result = "Diff requires exactly two files, provided: " + filesStr.length;
      }
      else {
	left  = FileUtils.loadFromFile(new PlaceholderFile(filesStr[0]));
	right = FileUtils.loadFromFile(new PlaceholderFile(filesStr[1]));
      }
    }
    else if (m_InputToken.getPayload() instanceof String[][]) {
      strings = (String[][]) m_InputToken.getPayload();
      if (strings.length != 2) {
	result = "Diff requires exactly two string arrays, provided: " + strings.length;
      }
      else {
	left  = Arrays.asList(strings[0]);
	right = Arrays.asList(strings[1]);
      }
    }
    else {
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());
    }

    if (result == null) {
      switch (m_Type) {
	case UNIFIED:
	  m_OutputToken = new Token(DiffUtils.unified(left, right));
	  break;
	case SIDE_BY_SIDE:
	  m_OutputToken = new Token(DiffUtils.sideBySide(left, right));
	  break;
	case BRIEF:
	  m_OutputToken = new Token(DiffUtils.isDifferent(left, right));
	  break;
	default:
	  throw new IllegalStateException("Unhandled diff type: " + m_Type);
      }
    }

    return result;
  }
}
