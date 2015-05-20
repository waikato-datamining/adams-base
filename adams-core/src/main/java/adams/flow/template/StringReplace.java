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
 * StringReplace.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import java.util.List;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.NestedConsumer;
import adams.flow.core.AbstractActor;

/**
 <!-- globalinfo-start -->
 * Replaces strings that match the specified regular expressions in the file before instantiating an actor from it.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-template &lt;adams.core.io.PlaceholderFile&gt; (property: templateFile)
 * &nbsp;&nbsp;&nbsp;The template file to load.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-find &lt;adams.core.base.BaseRegExp&gt; [-find ...] (property: find)
 * &nbsp;&nbsp;&nbsp;The regular expressions to use for matching.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-replace &lt;adams.core.base.BaseString&gt; [-replace ...] (property: replace)
 * &nbsp;&nbsp;&nbsp;The strings to replace the matching strings with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringReplace
  extends FromFile {

  /** for serialization. */
  private static final long serialVersionUID = -8975800423604842422L;

  /** the regular expressions to find. */
  protected BaseRegExp[] m_Find;

  /** the replacements. */
  protected BaseString[] m_Replace;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Replaces strings that match the specified regular expressions in the "
      + "file before instantiating an actor from it.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "find", "find",
	    new BaseRegExp[]{});

    m_OptionManager.add(
	    "replace", "replace",
	    new BaseString[]{});
  }

  /**
   * Sets the regular expressions to use for matching.
   *
   * @param value	the expressions
   */
  public void setFind(BaseRegExp[] value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the regular expressions to use for matching.
   *
   * @return		the file
   */
  public BaseRegExp[] getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String findTipText() {
    return "The regular expressions to use for matching.";
  }

  /**
   * Sets the strings to replace the matches with.
   *
   * @param value	the expressions
   */
  public void setReplace(BaseString[] value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns the strings to replace the matches with.
   *
   * @return		the file
   */
  public BaseString[] getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String replaceTipText() {
    return "The strings to replace the matching strings with.";
  }

  /**
   * Hook before generating the actor.
   * <br><br>
   * Checks whether find and replace arrays have the same length.
   */
  @Override
  protected void preGenerate() {
    super.preGenerate();

    if (m_Find.length != m_Replace.length)
      throw new IllegalStateException(
	  "'find' and 'replace' have different amounts of elements: "
	  + m_Find.length + " != " + m_Replace.length);

    if (m_Find.length == 0)
      getLogger().severe("Warning: no find/replace defined");
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected AbstractActor doGenerate() {
    AbstractActor	result;
    List<String>	lines;
    String		line;
    int			i;
    int			n;
    String		find;
    String		replace;

    result = null;

    lines = FileUtils.loadFromFile(m_TemplateFile);
    if (lines != null) {
      // replace strings
      for (i = 0; i < lines.size(); i++) {
	line = lines.get(i);
	if (line.length() > 0) {
	  for (n = 0; n < m_Find.length; n++) {
	    find    = m_Find[n].stringValue();
	    replace = m_Replace[n].stringValue();
	    line    = line.replaceAll(find, replace);
	  }
	  lines.set(i, line);
	}
      }


      // instantiate actor
      result = (AbstractActor) AbstractOptionConsumer.fromString(NestedConsumer.class, Utils.flatten(lines, "\n"));
      if (result == null)
	getLogger().severe("Failed to instantiate actor from: " + m_TemplateFile);
    }
    else {
      getLogger().severe("Failed to read content from: " + m_TemplateFile);
    }

    return result;
  }
}
