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
 * ListStorageNames.java.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.flow.control.StorageName;

/**
 <!-- globalinfo-start -->
 * Outputs the names of the currently available storage items.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ListStorageNames
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
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the storage item names in an array rather than one by 
 * &nbsp;&nbsp;&nbsp;one.
 * </pre>
 * 
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to retrieve the storage item names; uses the regular 
 * &nbsp;&nbsp;&nbsp;storage if left empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the storage item names.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListStorageNames
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3232942192233897676L;

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the names of the currently available storage items.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cache", "cache",
	    "");

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "regExp", m_RegExp, (m_Invert ? "! " : ""));
    result += QuickInfoHelper.toString(this, "cache", (m_Cache.isEmpty() ? "-none-" : m_Cache), ", cache: ");

    // further options
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", getOutputArray(), "array"));
    result += QuickInfoHelper.flatten(options);

    
    return result;
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
    return "Whether to output the storage item names in an array rather than one by one.";
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to retrieve the storage item names; uses the regular storage if left empty.";
  }

  /**
   * Sets the regular expression to match the variable names against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the variable names against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the storage item names.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Set<StorageName>	set;
    String		name;
    
    m_Queue.clear();
    
    if (m_Cache.length() > 0)
      set = getStorageHandler().getStorage().keySet(m_Cache);
    else
      set = getStorageHandler().getStorage().keySet();
    
    if (m_RegExp.isMatchAll()) {
      if (!m_Invert) {
	for (StorageName sname: set) {
	  name = sname.getValue();
	  m_Queue.add(name);
	}
      }
    }
    else {
      for (StorageName sname: set) {
	name = sname.getValue();
	if (m_Invert && !m_RegExp.isMatch(name))
	  m_Queue.add(name);
	else if (!m_Invert && m_RegExp.isMatch(name))
	  m_Queue.add(name);
      }
    }
    
    Collections.sort(m_Queue);
    
    return null;
  }
}
