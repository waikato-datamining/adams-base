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
 * RemoveFiles.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.postflowexecution;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderDirectory;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.source.FileSystemSearch;
import adams.flow.source.filesystemsearch.LocalFileSearch;
import adams.flow.transformer.DeleteFile;

/**
 * Removes files in the specified directory that match the regular expression.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveFiles
  extends AbstractPostFlowExecution {

  private static final long serialVersionUID = -5816582392372151789L;

  /** the directory to clean up. */
  protected PlaceholderDirectory m_Dir;

  /** the regexp for matching files to delete. */
  protected BaseRegExp m_RegExp;

  /** whether to delete recursively. */
  protected boolean m_Recursive;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes files in the specified directory that match the regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dir", "dir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "recursive", "recursive",
      false);
  }

  /**
   * Sets the directory to clean up.
   *
   * @param value 	the dir
   */
  public void setDir(PlaceholderDirectory value) {
    m_Dir = value;
    reset();
  }

  /**
   * Returns the directory to clean up.
   *
   * @return 		the dir
   */
  public PlaceholderDirectory getDir() {
    return m_Dir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dirTipText() {
    return "The directory to clean up.";
  }

  /**
   * Sets the regexp for matching files to delete.
   *
   * @param value 	the regexp
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regexp for matching files to delete.
   *
   * @return 		the regexp
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
    return "The regular expression for matching files to delete.";
  }

  /**
   * Sets whether to delete files recursively.
   *
   * @param value 	true if recursively
   */
  public void setRecursive(boolean value) {
    m_Recursive = value;
    reset();
  }

  /**
   * Returns whether to delete files recursively.
   *
   * @return 		true if recursively
   */
  public boolean getRecursive() {
    return m_Recursive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recursiveTipText() {
    return "The regular expression for matching files to delete.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "dir", m_Dir, "dir: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");
    result += QuickInfoHelper.toString(this, "recursive", m_Recursive, "recursive", ", ");

    return result;
  }

  /**
   * Configures the actor to execute after the flow has run (without calling setUp()).
   *
   * @param errors for collecting errors during configuration
   * @return the actor, null if none generated
   */
  @Override
  protected Actor doConfigureExecution(MessageCollection errors) {
    Flow 		result;
    FileSystemSearch	search;
    LocalFileSearch	local;

    result = new Flow();

    local = new LocalFileSearch();
    local.setDirectory(m_Dir);
    local.setRegExp(ObjectCopyHelper.copyObject(m_RegExp));
    local.setRecursive(m_Recursive);
    search = new FileSystemSearch();
    search.setSearch(local);
    result.add(search);
    result.add(new DeleteFile());

    return result;
  }
}
