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
 * DatabaseActorExecutionClassProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.ArrayList;
import java.util.List;

import adams.core.base.BasePassword;
import adams.db.DatabaseConnection;

/**
 * Generates a wrapper for an actor that works in conjunction with a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseActorExecutionClassProducer
  extends ActorExecutionClassProducer {

  /** for serialization. */
  private static final long serialVersionUID = -8854002881512935904L;

  /** the database URL. */
  protected String m_URL;

  /** the database user. */
  protected String m_User;

  /** the database password. */
  protected BasePassword m_Password;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a wrapper class for executing an actor that requires database access.";
  }

  /**
   * Initializes the visitor.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_URL      = DatabaseConnection.getSingleton().getURL();
    m_User     = DatabaseConnection.getSingleton().getUser();
    m_Password = DatabaseConnection.getSingleton().getPassword();  // TODO: leave empty?
  }

  /**
   * Sets the database URL.
   *
   * @param value	the URL
   */
  public void setURL(String value) {
    m_URL = value;
  }

  /**
   * Returns the database URL.
   *
   * @return		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The JDBC URL of the database connection.";
  }

  /**
   * Sets the database user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
  }

  /**
   * Returns the database user.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The user used for connecting to the database.";
  }

  /**
   * Sets the database password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
  }

  /**
   * Returns the database URL.
   *
   * @return		the URL
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password to use for connecting to the database.";
  }

  /**
   * Returns other necessary imports.
   *
   * @return		the class names
   */
  @Override
  protected List<String> getRequiredImports() {
    List<String>	result;

    result = new ArrayList<String>(super.getRequiredImports());

    result.add(DatabaseConnection.class.getName());
    result.add(BasePassword.class.getName());

    return result;
  }

  /**
   * Adds a method that gets executed just prior to the actors setup and
   * execution.
   */
  @Override
  protected void addPreExecuteMethod() {
    m_OutputBuffer.append("  /**\n");
    m_OutputBuffer.append("   * Hook method before the actor is executed.\n");
    m_OutputBuffer.append("   * <br><br>\n");
    m_OutputBuffer.append("   * Establishes the database connection.\n");
    m_OutputBuffer.append("   *\n");
    m_OutputBuffer.append("   * @param actor 		the actor that will get executed.\n");
    m_OutputBuffer.append("   * @throws Exception 	if an error occurs.\n");
    m_OutputBuffer.append("   */\n");
    m_OutputBuffer.append("  protected void preExecute(AbstractActor actor) throws Exception {\n");
    m_OutputBuffer.append("    DatabaseConnection.getSingleton().reconnect(DatabaseConnection.getSingleton().getDriver(), \"" + getURL() + "\", \"" + getUser() + "\", new BasePassword(\"" + getPassword().stringValue() + "\"));\n");
    m_OutputBuffer.append("  }\n");
    m_OutputBuffer.append("\n");
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(DatabaseActorExecutionClassProducer.class, args);
  }
}
