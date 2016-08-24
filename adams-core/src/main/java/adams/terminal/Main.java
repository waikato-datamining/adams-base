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
 * Main.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal;

import adams.core.logging.AbstractLogHandler;
import adams.core.logging.LoggingHelper;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.Environment;
import adams.terminal.application.AbstractTerminalApplication;
import adams.terminal.application.ApplicationMenu;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Panel;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Main ADAMS application - terminal-based.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Main
  extends AbstractTerminalApplication {

  private static final long serialVersionUID = -6680605754144851435L;

  /**
   * The log handler for the application.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class LogHandler
    extends AbstractLogHandler {

    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * The <tt>Handler</tt>  is responsible for formatting the message, when and
     * if necessary.  The formatting should include localization.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    protected void doPublish(LogRecord record) {
      System.out.println(LoggingHelper.assembleMessage(record).toString());
    }
  }

  /** the menu bar. */
  protected Panel m_MenuBar;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "ADAMS Application";
  }

  /**
   * Initializes the terminal.
   */
  @Override
  protected void initTerminal() {
    ApplicationMenu   	menu;

    super.initTerminal();

    menu         = new ApplicationMenu(this);
    m_MenuBar    = menu.getMenuBar(m_GUI);
    m_MainWindow = new BasicWindow();
  }

  /**
   * Returns the default title of the application.
   *
   * @return		the default title
   */
  protected String getDefaultApplicationTitle() {
    return "ADAMS";
  }

  /**
   * Sets the title to use.
   *
   * @param value	the title
   */
  protected void setTitle(String value) {
    if (m_MenuBar != null)
      m_MainWindow.setComponent(m_MenuBar.withBorder(Borders.singleLine(value)));
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the log handler to use.
   *
   * @return		the handler
   */
  protected Handler createLogHandler() {
    return new LogHandler();
  }

  /**
   * Starts the terminal application from commandline.
   *
   * @param args	the arguments
   * @throws Exception	if start up fails
   */
  public static void main(String[] args) throws Exception {
    runApplication(Environment.class, Main.class, args);
    System.exit(0);
  }
}
