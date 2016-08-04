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

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.env.Environment;
import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;

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
    Panel 	panel;
    Button	button;

    super.initTerminal();

    // Create panel to hold components
    panel = new Panel();
    panel.setLayoutManager(new GridLayout(1));

    button = new Button("Exit", () -> stop()).addTo(panel);
    panel.addComponent(button);

    // Create window to hold the panel
    m_MainWindow = new BasicWindow();
    m_MainWindow.setComponent(panel);
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
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
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
