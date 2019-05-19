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
 * HeapDump.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.SwingWorker;

/**
 * Generates a heapdump.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see adams.core.management.HeapDump
 */
public class HeapDump
  extends AbstractJDKMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1502903491659697700L;

  /**
   * Initializes the menu item with no owner.
   */
  public HeapDump() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public HeapDump(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  protected void doLaunch() {
    PropertiesParameterPanel	panel;
    Properties			props;
    final Properties		fProps;
    ApprovalDialog		dialog;

    props = new Properties();
    props.setPath("output", new PlaceholderFile("${HOME}/heapdump.hprof").getAbsolutePath());
    props.setBoolean("live", true);

    panel = new PropertiesParameterPanel();
    panel.addPropertyType("output", PropertyType.FILE_ABSOLUTE);
    panel.setLabel("output", "Output file (.hprof extension)");
    panel.addPropertyType("live", PropertyType.BOOLEAN);
    panel.setLabel("live", "Only live objects?");
    panel.setPropertyOrder(new String[]{"output", "live"});
    panel.setProperties(props);

    dialog = ApprovalDialog.getDialog(m_Owner);
    dialog.setTitle(getTitle());
    dialog.getContentPane().add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    fProps = panel.getProperties();
    SwingWorker worker = new SwingWorker() {
      protected String m_Error = null;
      @Override
      protected Object doInBackground() throws Exception {
        m_Error = adams.core.management.HeapDump.generate(new PlaceholderFile(fProps.getPath("output", ".")), fProps.getBoolean("live", true));
	return null;
      }
      @Override
      protected void done() {
	super.done();
        if (m_Error != null)
          GUIHelper.showErrorMessage(null, m_Error);
        else
          GUIHelper.showInformationMessage(null, "Successfully generated heapdump:\n" + fProps.getProperty("output"));
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
    return "HeapDump";
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