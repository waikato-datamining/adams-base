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
 * HexReplace.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;
import gnu.trove.list.array.TByteArrayList;

/**
 <!-- globalinfo-start -->
 * Allows to find and replace characters in hex notation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: HexReplace
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The bytes in hexadecimal notation to find (eg 0D0A for CRLF).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-replace &lt;java.lang.String&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;The string in hexadecimal notation to replace the occurrences with (eg 0A
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HexReplace
  extends AbstractTransformer {

  private static final long serialVersionUID = -8624369481983809607L;

  /** the search string in hex. */
  protected String m_Find;

  /** the replacement string in hex. */
  protected String m_Replace;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to find and replace characters in hex notation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "find", "find",
      "");

    m_OptionManager.add(
      "replace", "replace",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	replace;
    String	find;

    replace = QuickInfoHelper.toString(this, "replace", m_Replace);
    if (replace == null)
      replace = "";
    find    = QuickInfoHelper.toString(this, "find", m_Find);
    if (find == null)
      find = "";

    if (QuickInfoHelper.hasVariable(this, "replace") || (replace.length() > 0))
      result = "replace ";
    else
      result = "remove ";
    result += "'" + find + "'";

    if (QuickInfoHelper.hasVariable(this, "replace") || (replace.length() > 0))
      result += " with '" + replace + "'";

    return result;
  }

  /**
   * Sets the hex string to find.
   *
   * @param value	the hex string
   */
  public void setFind(String value) {
    try {
      Utils.fromHexArray(value);
    }
    catch (Exception e) {
      getLogger().warning("Invalid hex string: " + value);
      return;
    }

    m_Find = value;
    reset();
  }

  /**
   * Returns the hex string to find.
   *
   * @return		the hex string
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The bytes in hexadecimal notation to find (eg 0D0A for CRLF).";
  }

  /**
   * Sets the string to replace the occurrences with.
   *
   * @param value	the string
   */
  public void setReplace(String value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns the string to replace the occurences with.
   *
   * @return		the string
   */
  public String getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The string in hexadecimal notation to replace the occurrences with (eg 0A).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, byte[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class, byte[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    boolean		isString;
    TByteArrayList	oldData;
    TByteArrayList	newData;
    byte[] 		find;
    byte[]		repl;
    int			i;
    int			n;
    boolean		match;

    result = null;
    find   = Utils.fromHexArray(m_Find);
    repl   = Utils.fromHexArray(m_Replace);

    if (find.length == 0)
      result = "No search string!";

    if (result == null) {
      if (find.length == repl.length) {
	match = true;
	for (i = 0; i < find.length; i++) {
	  if (find[i] != repl[i]) {
	    match = false;
	    break;
	  }
	}
	if (match)
	  result = "Find and replace are the same: '" + m_Find + "'";
      }
    }

    if (result == null) {
      isString = (m_InputToken.getPayload() instanceof String);
      if (isString)
	newData = new TByteArrayList(((String) m_InputToken.getPayload()).getBytes());
      else
	newData = new TByteArrayList((byte[]) m_InputToken.getPayload());

      oldData = newData;
      newData = new TByteArrayList();
      i       = 0;
      while (i < oldData.size()) {
	if (oldData.get(i) == find[0]) {
	  match = true;
	  for (n = 1; n < find.length && i + n < oldData.size(); n++) {
	    if (oldData.get(i + n) != find[n]) {
	      match = false;
	      break;
	    }
	  }
	  if (match) {
	    for (n = 0; n < repl.length; n++)
	      newData.add(repl[n]);
	    i += find.length;
	  }
	  else {
	    newData.add(oldData.get(i));
	    i++;
	  }
	}
	else {
	  newData.add(oldData.get(i));
	  i++;
	}

	// flow stopped?
	if (m_Stopped)
	  break;
      }

      if (isString)
	m_OutputToken = new Token(new String(newData.toArray()));
      else
	m_OutputToken = new Token(newData.toArray());
    }

    return result;
  }
}
