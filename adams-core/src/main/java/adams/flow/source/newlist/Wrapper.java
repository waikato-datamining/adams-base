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
 * Wrapper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.newlist;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Wraps the list items produced by the specified generator with user-supplied prefix and suffix.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-generator &lt;adams.flow.source.newlist.AbstractListGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The list generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.newlist.FixedList
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Wrapper
  extends AbstractListGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4623795710416726074L;

  /** the generator to use. */
  protected AbstractListGenerator m_Generator;

  /** the prefix to use. */
  protected String m_Prefix;
  
  /** the suffix to use. */
  protected String m_Suffix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Wraps the list items produced by the specified generator with "
	+ "user-supplied prefix and suffix.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new FixedList());

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "suffix", "suffix",
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

    result  = QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
    result += QuickInfoHelper.toString(this, "prefix", (m_Prefix.isEmpty() ? "-none-" : m_Prefix), ", prefix: ");
    result += QuickInfoHelper.toString(this, "suffix", (m_Suffix.isEmpty() ? "-none-" : m_Suffix), ", suffix: ");

    return result;
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractListGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
   *
   * @return		the generator
   */
  public AbstractListGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The list generator to use.";
  }

  /**
   * Sets the prefix to use.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use.";
  }

  /**
   * Sets the suffix to use.
   *
   * @param value	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix to use.
   *
   * @return 		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix to use.";
  }
  
  /**
   * Generates the actual list.
   * 
   * @return		the list of elements
   * @throws Exception	if generation fails
   */
  @Override
  protected List<String> doGenerate() throws Exception {
    List<String>	result;
    int			i;
    
    result = new ArrayList<String>();
    result.addAll(m_Generator.generate());
    for (i = 0; i < result.size(); i++)
      result.set(i, m_Prefix + result.get(i) + m_Suffix);
    
    return result;
  }
}
