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
 * TextualContentPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.core.TextSupporter;
import adams.core.io.FileUtils;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.BaseTextPaneWithButtons;
import adams.gui.core.GUIHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

/**
 * Panel for exporting the textual component as text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextualContentPanel
  extends AbstractOutputPanelWithPopupMenu<TextFileChooser>
  implements TextSupporter {

  private static final long serialVersionUID = 8183731075946484533L;

  /** the actual component. */
  protected JComponent m_Component;

  /**
   * Initializes the panel with the specified textual component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public TextualContentPanel(JTextComponent comp, boolean useScrollPane) {
    super();
    initGUI(comp, useScrollPane);
  }

  /**
   * Initializes the panel with the specified textual component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public TextualContentPanel(BaseTextAreaWithButtons comp, boolean useScrollPane) {
    super();
    initGUI(comp, useScrollPane);
  }

  /**
   * Initializes the panel with the specified textual component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public TextualContentPanel(BaseTextPaneWithButtons comp, boolean useScrollPane) {
    super();
    initGUI(comp, useScrollPane);
  }

  /**
   * Initializes the panel with the specified component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  protected void initGUI(JComponent comp, boolean useScrollPane) {
    m_Component = comp;

    setPreferredSize(new Dimension(GUIHelper.getDefaultTinyDialogDimension()));
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
  protected TextFileChooser createFileChooser() {
    TextFileChooser	result;

    result = new TextFileChooser();
    result.setAcceptAllFileFilterUsed(true);
    result.setAutoAppendExtension(false);

    return result;
  }

  /**
   * Saves the content to the specified file.
   *
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String save(File file) {
    String	result;
    String	content;
    String	msg;

    result = null;

    content = supplyText();
    if (content == null)
      result = "Unhandled component: " + m_Component.getClass().getName();

    if (result == null) {
      msg = FileUtils.writeToFileMsg(file.getAbsolutePath(), content, false, getFileChooser().getEncoding());
      if (msg != null)
	result = msg;
    }

    return result;
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    String result;

    if (m_Component instanceof JTextComponent)
      result = ((JTextComponent) m_Component).getText();
    else if (m_Component instanceof BaseTextAreaWithButtons)
      result = ((BaseTextAreaWithButtons) m_Component).getText();
    else if (m_Component instanceof BaseTextPaneWithButtons)
      result = ((BaseTextPaneWithButtons) m_Component).getText();
    else
      result = null;

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
    ClipboardHelper.copyToClipboard(supplyText());
  }
}
