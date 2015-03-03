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
  *    PrintMouseListener.java
  *    Copyright (C) 2005-2013 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JComponent;

import adams.core.io.PlaceholderFile;
import adams.gui.core.MouseUtils;

/**
 * The listener to wait for Ctrl-Shft-Left Mouse Click.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrintMouseListener
  extends MouseAdapter {

  /** the listener's component. */
  protected JComponent m_Component;

  /** the filechooser for saving the panel. */
  protected static JComponentWriterFileChooser m_FileChooser;

  /**
   * Initializes the listener. Listening component is the same as the one
   * being printed.
   *
   * @param component 	the component for which to create the listener and
   * 			which to print then
   */
  public PrintMouseListener(JComponent component) {
    this(component, component);
  }

  /**
   * Initializes the listener. Listening component can be different from
   * one being printed.
   *
   * @param listener 	the component for which to create the listener
   * @param printed	the component that is being printed
   */
  public PrintMouseListener(JComponent listener, JComponent printed) {
    super();

    initFileChooser();

    m_Component = printed;
    listener.addMouseListener(this);
  }

  /**
   * initializes the filechooser, i.e. locates all the available writers in
   * the current package
   */
  protected synchronized void initFileChooser() {
    // already initialized?
    if (m_FileChooser != null)
      return;

    m_FileChooser = new JComponentWriterFileChooser();
  }

  /**
   * Invoked when the mouse has been clicked on a component.
   *
   * @param e	the event
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (MouseUtils.isPrintScreenClick(e)) {
      e.consume();
      saveComponent();
    }
  }

  /**
   * displays a save dialog for saving the panel to a file.
   */
  public void saveComponent() {
    int			result;
    JComponentWriter	writer;
    File		file;

    // display save dialog
    result = m_FileChooser.showSaveDialog(m_Component);
    if (result != JComponentWriterFileChooser.APPROVE_OPTION)
      return;

    // save the file
    try {
      file   = m_FileChooser.getSelectedFile().getAbsoluteFile();
      writer = m_FileChooser.getWriter();
      writer.setComponent(m_Component);
      writer.setFile(new PlaceholderFile(file));
      writer.toOutput();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}