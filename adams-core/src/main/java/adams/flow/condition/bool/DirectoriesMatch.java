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
 * DirectoriesMatch.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.PlaceholderDirectory;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if at least one sub-directory matches the regular expression in the specified directory.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-directory &lt;adams.core.io.PlaceholderDirectory&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The directory to search.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the sub-directories must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirectoriesMatch
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 912094591109587908L;

  /** the directory to search. */
  protected PlaceholderDirectory m_Directory;

  /** the expression the files must match. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates to 'true' if at least one sub-directory matches the regular "
	+ "expression in the specified directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "directory", "directory",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the directory to search.
   *
   * @param value	the directory
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Directory = value;
    reset();
  }

  /**
   * Returns the directory to search.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getDirectory() {
    return m_Directory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoryTipText() {
    return "The directory to search.";
  }

  /**
   * Sets the regular expression that the sub-directories must match.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the sub-directories must match.
   *
   * @return		the expression
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
    return "The regular expression that the sub-directories must match.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "directory", m_Directory, "dir: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (m_Directory == null)
	result = "No directory provided!";
      else if (m_RegExp == null)
	result = "No regular expression provided!";
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    DirectoryLister	lister;
    String[]		list;
    
    lister = new DirectoryLister();
    lister.setWatchDir(m_Directory);
    lister.setRegExp(m_RegExp);
    lister.setListFiles(false);
    lister.setListDirs(true);
    lister.setRecursive(false);
    lister.setMaxItems(1);
    list = lister.list();
    
    return (list.length > 0);
  }
}
