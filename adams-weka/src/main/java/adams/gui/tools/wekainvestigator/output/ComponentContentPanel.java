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
import adams.data.image.BufferedImageSupporter;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Panel for exporting the graphical component as image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ComponentContentPanel
  extends AbstractOutputPanelWithPopupMenu<JComponentWriterFileChooser>
  implements BufferedImageSupporter {

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

    if (useScrollPane)
      getContentPanel().add(new BaseScrollPane(m_Component), BorderLayout.CENTER);
    else
      getContentPanel().add(m_Component, BorderLayout.CENTER);
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

  /**
   * Returns a buffered image.
   *
   * @return		the buffered image
   */
  public BufferedImage toBufferedImage() {
    BufferedImage	result;
    Graphics g;

    result = new BufferedImage(getComponent().getWidth(), getComponent().getHeight(), BufferedImage.TYPE_INT_RGB);
    g = result.getGraphics();
    g.setPaintMode();
    g.setColor(getBackground());
    g.fillRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
    getComponent().printAll(g);

    return result;
  }

  /**
   * Returns whether copying to the clipboard is supported.
   *
   * @return		true if copy to clipboard is supported
   * @see		#copyToClipboard()
   */
  public boolean canCopyToClipboard() {
    return true;
  }

  /**
   * Copies the content to the clipboard.
   *
   * @see 		#canCopyToClipboard()
   */
  public void copyToClipboard() {
    ClipboardHelper.copyToClipboard(toBufferedImage());
  }
}
