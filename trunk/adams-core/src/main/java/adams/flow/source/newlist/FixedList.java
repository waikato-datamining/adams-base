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
 * FixedList.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.newlist;

import java.util.ArrayList;
import java.util.List;

import adams.core.base.BaseString;

/**
 <!-- globalinfo-start -->
 * Simply outputs the supplied list elements.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-element &lt;adams.core.base.BaseString&gt; [-element ...] (property: elements)
 * &nbsp;&nbsp;&nbsp;The list elements to output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedList
  extends AbstractListGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4623795710416726074L;
  
  /** the list elements to output. */
  protected BaseString[] m_Elements;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply outputs the supplied list elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "element", "elements",
	    new BaseString[0]);
  }
  
  /**
   * Sets the list elements to output.
   *
   * @param value	the elements
   */
  public void setElements(BaseString[] value) {
    m_Elements = value;
    reset();
  }

  /**
   * Returns the list elements to output.
   *
   * @return 		the elements
   */
  public BaseString[] getElements() {
    return m_Elements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String elementsTipText() {
    return "The list elements to output.";
  }

  /**
   * Hook method for checks.
   * <p/>
   * Ensures that list elements have been supplied.
   * 
   * @return		the list of elements
   * @throws Exception	if check fails
   */
  @Override
  protected void check() throws Exception {
    super.check();
    
    if (m_Elements.length == 0)
      throw new IllegalStateException("No list elements supplied!");
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
    
    result = new ArrayList<String>();
    for (BaseString element: m_Elements)
      result.add(element.getValue());
    
    return result;
  }
}
