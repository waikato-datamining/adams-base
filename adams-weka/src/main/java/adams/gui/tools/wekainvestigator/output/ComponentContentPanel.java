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
 * ComponentContentPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Panel for exporting the graphical component as image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ComponentContentPanel
  extends AbstractBasePanelWithPopupMenu<JComponentWriterFileChooser> {

  private static final long serialVersionUID = 8183731075946484533L;

  /** the actual component. */
  protected JComponent m_Component;

  /**
   * Initializes the panel with the specified component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public ComponentContentPanel(JComponent comp, boolean useScrollPane) {
    super();

    m_Component = comp;

    setLayout(new BorderLayout());
    if (useScrollPane)
      add(new BaseScrollPane(m_Component), BorderLayout.CENTER);
    else
      add(m_Component, BorderLayout.CENTER);
  }

  /**
   * Returns the embedded component.
   *
   * @return		the component
   */
  public JComponent getComponent() {
    return m_Component;
  }

  /**
   * Creates the filechooser to use.
   *
   * @return		the filechooser
   */
  @Override
  protected JComponentWriterFileChooser createFileChooser() {
    return new JComponentWriterFileChooser();
  }

  /**
   * Saves the content to the specified file.
   *
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String save(File file) {
    String		result;
    JComponentWriter writer;

    result = null;

    writer = m_FileChooser.getWriter();
    writer.setComponent(m_Component);
    writer.setFile(new PlaceholderFile(file));
    try {
      writer.toOutput();
    }
    catch (Exception e) {
      result = Utils.handleException(null, "Failed to save content to: " + file, e);
    }

    return result;
  }
}
