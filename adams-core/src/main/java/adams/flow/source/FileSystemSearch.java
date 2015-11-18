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
 * FileSystemSearch.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.source.filesystemsearch.AbstractFileSystemSearchlet;
import adams.flow.source.filesystemsearch.FileSearch;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the specified search algorithm to perform a file system search and returns the located items.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: FileSystemSearch
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the files are output as array rather than as single strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-search &lt;adams.flow.source.filesystemsearch.AbstractFileSystemSearchlet&gt; (property: search)
 * &nbsp;&nbsp;&nbsp;The search algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.filesystemsearch.FileSearch
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileSystemSearch
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -1389714133778925327L;
  
  /** the search algorithm to use. */
  protected AbstractFileSystemSearchlet m_Search;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses the specified search algorithm to perform a file system "
	+ "search and returns the located items.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "search", "search",
	    new FileSearch());
  }

  /**
   * Sets the search algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setSearch(AbstractFileSystemSearchlet value) {
    m_Search = value;
    reset();
  }

  /**
   * Returns the search algorithm in use.
   *
   * @return		the algorithm
   */
  public AbstractFileSystemSearchlet getSearch() {
    return m_Search;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String searchTipText() {
    return "The search algorithm to use.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the files are output as array rather than as single strings.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "search", m_Search);
    result += QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"), ", ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    List<String>	items;
    
    result = null;
    
    m_Queue.clear();
    try {
      items = m_Search.search();
      m_Queue.addAll(items);
    }
    catch (Exception e) {
      result = handleException("Failed to perform search!", e);
    }
    
    return result;
  }
}
