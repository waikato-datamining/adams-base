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
 * JMap.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.management.ProcessUtils;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextDialog;

import javax.swing.SwingWorker;

/**
 * Runs jmap and displays the result.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.management.JMap
 */
public class JMap
  extends AbstractJDKMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1502903491659697700L;

  /**
   * Initializes the menu item with no owner.
   */
  public JMap() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public JMap(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  protected void doLaunch() {
    // query for options
    String options = GUIHelper.showInputDialog(
	null, "Enter the options for " + adams.core.management.JMap.EXECUTABLE + ":",
	adams.core.management.JMap.getDefaultOptions());
    if (options == null)
      return;

    // run jmap and display output
    final long fPid = ProcessUtils.getVirtualMachinePID();
    final String fOptions = options;
    SwingWorker worker = new SwingWorker() {
      protected String m_Output = null;
      @Override
      protected Object doInBackground() throws Exception {
	m_Output = adams.core.management.JMap.execute(fOptions, fPid);
	return null;
      }
      @Override
      protected void done() {
	TextDialog dialog = new TextDialog();
        dialog.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
	dialog.setTitle(adams.core.management.JMap.EXECUTABLE + " (" + fPid + ")");
        dialog.setUpdateParentTitle(false);
	dialog.setContent(m_Output);
	dialog.setLocationRelativeTo(null);
	dialog.setVisible(true);
	super.done();
      }
    };
    worker.execute();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "JMap";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }
}