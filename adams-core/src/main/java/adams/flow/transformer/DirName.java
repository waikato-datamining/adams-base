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
 * DirName.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;
import adams.flow.core.Token;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Extracts the directory part of the file&#47;directory passing through, i.e., any trailing name part gets stripped.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.AppendName<br>
 * adams.flow.transformer.PrependDir<br>
 * adams.flow.transformer.RelativeDir<br>
 * adams.flow.transformer.BaseName<br>
 * adams.flow.transformer.FileExtension
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
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: DirName
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
 * <pre>-use-forward-slashes &lt;boolean&gt; (property: useForwardSlashes)
 * &nbsp;&nbsp;&nbsp;If enabled and receiving string(s) as input, forward slashes are used in 
 * &nbsp;&nbsp;&nbsp;the output (but the '\\' prefix of UNC paths is not converted).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DirName
  extends AbstractTransformer
  implements ClassCrossReference, ForwardSlashSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Extracts the directory part of the file/directory passing through, i.e., any "
      + "trailing name part gets stripped.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
	    false);
  }

  /**
   * Sets whether to use forward slashes in the output.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns whether to use forward slashes in the output.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return
	"If enabled and receiving string(s) as input, forward slashes are used in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "useForwardSlashes", (m_UseForwardSlashes ? "forward slashes" : "platform-specific slashes"));
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{AppendName.class, PrependDir.class, RelativeDir.class, BaseName.class, FileExtension.class};
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
   * @return		<!-- flow-generates-start -->java.lang.String.class, java.lang.String[].class, java.io.File.class, java.io.File[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String[]	strings;
    File[]	files;
    int		i;
    boolean	array;
    boolean	string;

    result = null;

    array  = m_InputToken.isArray();
    string = m_InputToken.hasPayload(String.class) || m_InputToken.hasPayload(String[].class);
    files  = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    strings = new String[files.length];
    for (i = 0; i < files.length; i++) {
      if (string)
	strings[i] = files[i].getAbsoluteFile().getParent();
      else
	files[i] = files[i].getAbsoluteFile().getParentFile();
    }

    if (string) {
      if (m_UseForwardSlashes) {
        for (i = 0; i < strings.length; i++)
          strings[i] = FileUtils.useForwardSlashes(strings[i]);
      }
      if (array)
	m_OutputToken = new Token(strings);
      else
	m_OutputToken = new Token(strings[0]);
    }
    else {
      if (array)
	m_OutputToken = new Token(files);
      else
	m_OutputToken = new Token(files[0]);
    }

    return result;
  }
}
