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
 * LogTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Just displays the log messages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -94945456385486233L;

  /** the text area for the log. */
  protected BaseTextAreaWithButtons m_TextLog;

  /** the button for emptying the log. */
  protected JButton m_ButtonClear;

  /** the button for copying the text. */
  protected JButton m_ButtonCopy;

  /** the button for saving the text. */
  protected JButton m_ButtonSave;

  /** the filechooser for saving the log. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter		filter;

    super.initialize();

    m_FileChooser = new BaseFileChooser();
    filter = ExtensionFileFilter.getLogFileFilter();
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.addChoosableFileFilter(ExtensionFileFilter.getTextFileFilter());
    m_FileChooser.setFileFilter(filter);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextLog = new BaseTextAreaWithButtons();
    m_TextLog.setTextFont(Fonts.getMonospacedFont());
    m_TextLog.setEditable(false);
    m_TextLog.setLineWrap(true);
    m_TextLog.setWrapStyleWord(true);
    add(m_TextLog, BorderLayout.CENTER);

    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.addActionListener((ActionEvent e) -> m_Owner.clearLog());
    m_TextLog.addToButtonsPanel(m_ButtonClear);

    m_ButtonCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonClear.addActionListener((ActionEvent e) -> {
      if (m_TextLog.getSelectedText() != null)
	ClipboardHelper.copyToClipboard(m_TextLog.getSelectedText());
      else
	ClipboardHelper.copyToClipboard(m_TextLog.getText());
    });
    m_TextLog.addToButtonsPanel(m_ButtonCopy);

    m_ButtonSave = new JButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener((ActionEvent e) -> {
      int retVal = m_FileChooser.showSaveDialog(LogTab.this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;
      String msg = FileUtils.writeToFileMsg(
	m_FileChooser.getSelectedFile().getAbsolutePath(),
	m_TextLog.getText(),
	false,
	null);
      if (msg != null)
	GUIHelper.showErrorMessage(LogTab.this, msg);
    });
    m_TextLog.addToButtonsPanel(m_ButtonSave);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    updateButtons();
  }

  /**
   * Sets the owner for this tab.
   *
   * @param value	the owner
   */
  @Override
  public void setOwner(InvestigatorPanel value) {
    super.setOwner(value);
    m_TextLog.setText(value.getLog().toString());
    updateButtons();
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Log";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "log.gif";
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    boolean	hasText;

    hasText = (m_TextLog.getText().length() > 0);

    m_ButtonClear.setEnabled(hasText);
    m_ButtonCopy.setEnabled(hasText);
    m_ButtonSave.setEnabled(hasText);
  }

  /**
   * Clears the log.
   */
  public void clearLog() {
    m_TextLog.setText("");
    updateButtons();
  }

  /**
   * Appends the message to the log.
   *
   * @param msg		the message
   */
  public void append(String msg) {
    m_TextLog.append(msg);
    m_TextLog.append("\n");
    m_TextLog.setCaretPositionLast();
    updateButtons();
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
  }
}
