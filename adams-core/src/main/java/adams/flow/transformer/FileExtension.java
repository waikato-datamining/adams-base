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
 * FileExtension.java
 * Copyright (C) 2012-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Extracts the file extension from the file (the part after the '.').<br>
 * If no dot is in the file name, then an empty string gets forwarded.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.AppendName<br>
 * adams.flow.transformer.PrependDir<br>
 * adams.flow.transformer.RelativeDir<br>
 * adams.flow.transformer.DirName<br>
 * adams.flow.transformer.BaseName
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: FileExtension
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
 * <pre>-include-dot &lt;boolean&gt; (property: includeDot)
 * &nbsp;&nbsp;&nbsp;If true, then the dot gets returned as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileExtension
  extends AbstractTransformer
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = -2838258525424174583L;
  
  /** whether to return the dot or not. */
  protected boolean m_IncludeDot;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Extracts the file extension from the file (the part after the '.').\n"
	+ "If no dot is in the file name, then an empty string gets forwarded.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "include-dot", "includeDot",
	    false);
  }

  /**
   * Sets whether to return the dot as well.
   *
   * @param value	if true then the dot gets returned as well
   */
  public void setIncludeDot(boolean value) {
    m_IncludeDot = value;
    reset();
  }

  /**
   * Returns whether the dot gets returned as well.
   *
   * @return 		true if the dot gets returned as well
   */
  public boolean getIncludeDot() {
    return m_IncludeDot;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String includeDotTipText() {
    return "If true, then the dot gets returned as well.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "includeDot", m_IncludeDot, "incl. dot");
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{AppendName.class, PrependDir.class, RelativeDir.class, DirName.class, BaseName.class};
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.lang.String[].class, java.io.File.class, java.io.File[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class, java.lang.String[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		strings;
    PlaceholderFile[]	files;
    int			i;
    boolean		array;
    int			pos;

    result = null;

    array = m_InputToken.isArray();
    files = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    strings = new String[files.length];
    for (i = 0; i < files.length; i++) {
      strings[i] = files[i].getName();
      strings[i] = FileUtils.removeIgnoredExtensionSuffixes(strings[i]);
      pos        = strings[i].lastIndexOf('.');
      if (pos > -1) {
	if (m_IncludeDot)
	  strings[i] = files[i].getName().substring(pos, files[i].getName().length());
	else
	  strings[i] = files[i].getName().substring(pos + 1, files[i].getName().length());
      }
      else {
	strings[i] = "";
      }
    }

    if (array)
      m_OutputToken = new Token(strings);
    else
      m_OutputToken = new Token(strings[0]);

    return result;
  }
}
