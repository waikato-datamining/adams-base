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
 * ChangeDatabaseConnection.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.lang.reflect.Array;

import adams.core.base.BasePassword;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActor;

/**
 * Processor that updates database connections.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ChangeDatabaseConnection
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;
  
  /** the old database URL. */
  protected String m_OldURL;
  
  /** whether the old database URL is a regular expression. */
  protected boolean m_OldURLIsRegExp;
  
  /** the new database URL. */
  protected String m_NewURL;
  
  /** the new database user. */
  protected String m_NewUser;
  
  /** the new database password. */
  protected BasePassword m_NewPassword;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates all database connections that match the user-provided URL.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "old-url", "oldURL",
	    "");

    m_OptionManager.add(
	    "old-url-is-regexp", "oldURLIsRegExp",
	    false);

    m_OptionManager.add(
	    "new-url", "newURL",
	    "");

    m_OptionManager.add(
	    "new-user", "newUser",
	    "");

    m_OptionManager.add(
	    "new-password", "newPassword",
	    new BasePassword());
  }

  /**
   * Sets the old URL to replace.
   *
   * @param value	the old URL
   */
  public void setOldURL(String value) {
    m_OldURL = value;
    reset();
  }

  /**
   * Returns the old URL to replace.
   *
   * @return		the old URL
   */
  public String getOldURL() {
    return m_OldURL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldURLTipText() {
    return "The old JDBC URL to replace.";
  }

  /**
   * Sets whether the old URL represents a regular expression.
   *
   * @param value	true if the old URL is a regular expression
   */
  public void setOldURLIsRegExp(boolean value) {
    m_OldURLIsRegExp = value;
    reset();
  }

  /**
   * Returns whether the old URL represents a regular expression.
   *
   * @return		true if the old URL is a regular expression
   */
  public boolean getOldURLIsRegExp() {
    return m_OldURLIsRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldURLIsRegExpTipText() {
    return "If enabled, the old URL gets interpreted as regular expression instead of a plain string.";
  }

  /**
   * Sets the new URL to replace.
   *
   * @param value	the new URL
   */
  public void setNewURL(String value) {
    m_NewURL = value;
    reset();
  }

  /**
   * Returns the new URL to replace.
   *
   * @return		the new URL
   */
  public String getNewURL() {
    return m_NewURL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newURLTipText() {
    return "The new JDBC URL to replace.";
  }

  /**
   * Sets the new database user to use.
   *
   * @param value	the new user
   */
  public void setNewUser(String value) {
    m_NewUser = value;
    reset();
  }

  /**
   * Returns the new database user to use.
   *
   * @return		the new user
   */
  public String getNewUser() {
    return m_NewUser;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newUserTipText() {
    return "The new database user to use.";
  }

  /**
   * Sets the new password.
   *
   * @param value	the new password
   */
  public void setNewPassword(BasePassword value) {
    m_NewPassword = value;
    reset();
  }

  /**
   * Returns the new password.
   *
   * @return		the new password
   */
  public BasePassword getNewPassword() {
    return m_NewPassword;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newPasswordTipText() {
    return "The new database password to use.";
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
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected boolean isMatch(String url) {
	if (m_OldURLIsRegExp)
	  return url.matches(m_OldURL);
	else
	  return url.equals(m_OldURL);
      }
      protected void process(Object obj) {
	if (obj instanceof adams.flow.standalone.AbstractDatabaseConnection) {
	  adams.flow.standalone.AbstractDatabaseConnection conn = (adams.flow.standalone.AbstractDatabaseConnection) obj;
	  if (!isMatch(conn.getURL()))
	    return;
	  conn.setURL(m_NewURL);
	  conn.setUser(m_NewUser);
	  conn.setPassword(m_NewPassword);
	  m_Modified = true;
	}
	else if (obj instanceof adams.db.AbstractDatabaseConnection) {
	  adams.db.AbstractDatabaseConnection conn = (adams.db.AbstractDatabaseConnection) obj;
	  if (!isMatch(conn.getURL()))
	    return;
	  conn.setURL(m_NewURL);
	  conn.setUser(m_NewUser);
	  conn.setPassword(m_NewPassword);
	  m_Modified = true;
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  for (int i = 0; i < Array.getLength(current); i++)
	    process(Array.get(current, i));
	}
	else {
	  process(current);
	}
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// ignored
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
