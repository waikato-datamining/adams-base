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
 * ChangePath.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.ClassLocator;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActor;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

/**
 * Processor that updates paths of classes that are derived from {@link File}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ChangePath
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;
  
  /** the old path. */
  protected String m_OldPath;
  
  /** the old path (with forward slashes). */
  protected String m_OldPathLinux;
  
  /** whether the old path is a regular expression. */
  protected boolean m_OldPathIsRegExp;

  /** whether to use lowercase for matching. */
  protected boolean m_UseLowerCase;

  /** the new path. */
  protected String m_NewPath;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Updates all paths that match the provided old path.\n"
	+ "If no regular expression matching is enabled, then the paths must "
	+ "start with the provided old path in order to trigger the replacement.\n"
	+ "If regular expression matching is enabled, the old paths must match "
	+ "the provided regexp. The replacement string can then also contain "
	+ "group references (eg $1).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "old-path", "oldPath",
	    "");

    m_OptionManager.add(
	    "old-path-is-regexp", "oldPathIsRegExp",
	    false);

    m_OptionManager.add(
	    "use-lower-case", "useLowerCase",
	    false);

    m_OptionManager.add(
	    "new-path", "newPath",
	    "");
  }

  /**
   * Sets the old Path to replace.
   *
   * @param value	the old Path
   */
  public void setOldPath(String value) {
    m_OldPath = value;
    reset();
  }

  /**
   * Returns the old Path to replace.
   *
   * @return		the old Path
   */
  public String getOldPath() {
    return m_OldPath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldPathTipText() {
    return "The old JDBC Path to replace.";
  }

  /**
   * Sets whether the old Path represents a regular expression.
   *
   * @param value	true if the old Path is a regular expression
   */
  public void setOldPathIsRegExp(boolean value) {
    m_OldPathIsRegExp = value;
    reset();
  }

  /**
   * Returns whether the old Path represents a regular expression.
   *
   * @return		true if the old Path is a regular expression
   */
  public boolean getOldPathIsRegExp() {
    return m_OldPathIsRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldPathIsRegExpTipText() {
    return "If enabled, the old path gets interpreted as regular expression; otherwise, only prefix matching is used.";
  }

  /**
   * Sets whether to lowercase the paths before matching.
   *
   * @param value	true if to lowercase paths
   */
  public void setUseLowerCase(boolean value) {
    m_UseLowerCase = value;
    reset();
  }

  /**
   * Returns whether to lowercase the paths before matching.
   *
   * @return		true if to lowercase paths
   */
  public boolean getUseLowerCase() {
    return m_UseLowerCase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String useLowerCaseTipText() {
    return "If enabled, the paths are converted to lowercase before attempting the matching.";
  }

  /**
   * Sets the new Path to replace with.
   *
   * @param value	the new Path
   */
  public void setNewPath(String value) {
    m_NewPath = value;
    reset();
  }

  /**
   * Returns the new Path to replace with.
   *
   * @return		the new Path
   */
  public String getNewPath() {
    return m_NewPath;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newPathTipText() {
    return "The new path to replace the old one with.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    // UNC path?
    if (m_OldPath.startsWith("\\\\") || m_OldPath.startsWith("//"))
      m_OldPathLinux = "\\\\" + m_OldPath.substring(2).replace("\\", "/");
    else
      m_OldPathLinux = m_OldPath;
    
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected boolean isMatch(String path) {
	if (m_UseLowerCase)
	  path = path.toLowerCase();
	if (m_OldPathIsRegExp)
	  return path.matches(m_OldPath) || path.matches(m_OldPathLinux);
	else
	  return path.startsWith(m_OldPath) || path.startsWith(m_OldPathLinux);
      }
      protected Object update(Object obj) {
	Object result = null;
	Class cls = obj.getClass();
	String str = ((File) obj).toString();
	String newStr = null;
	if (m_OldPathIsRegExp) {
	  if (str.matches(m_OldPath))
	    newStr = str.replaceFirst(m_OldPath, m_NewPath);
	  else
	    newStr = str.replaceFirst(m_OldPathLinux, m_NewPath);
	}
	else {
	  if (str.startsWith(m_OldPath))
	    newStr = str.replace(m_OldPath, m_NewPath);
	  else
	    newStr = str.replace(m_OldPathLinux, m_NewPath);
	}
	try {
	  Constructor constr = cls.getConstructor(new Class[]{String.class});
	  result = constr.newInstance(new Object[]{newStr});
	}
	catch (Exception e) {
	  System.err.println("Failed to create instance of " + cls.getName() + ":");
	  e.printStackTrace();
	  result = obj;
	}
	return result;
      }
      protected Object process(Object obj) {
	if (obj instanceof File) {
	  File file = (File) obj;
	  if (!isMatch(file.toString()))
	    return obj;
	  m_Modified = true;
	  return update(file);
	}
	else {
	  return obj;
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (ClassLocator.isSubclass(File.class, option.getBaseClass())) {
	  Object current = option.getCurrentValue();
	  if (option.isMultiple()) {
	    for (int i = 0; i < Array.getLength(current); i++)
	      Array.set(current, i, process(Array.get(current, i)));
	    option.setCurrentValue(current);
	  }
	  else {
	    option.setCurrentValue(process(current));
	  }
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
	return true;
      }
      public boolean canRecurse(Object obj) {
	return canRecurse(obj.getClass());
      }
    });
  }
}
