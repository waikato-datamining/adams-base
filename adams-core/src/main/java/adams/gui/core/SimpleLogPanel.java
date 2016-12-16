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
 * SimpleLogPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.gui.chooser.BaseFileChooser;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Simple log panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleLogPanel
  extends BasePanel
  implements LogPanel {

  private static final long serialVersionUID = -3366953177421000811L;

  /** the text area for the log. */
  protected BaseTextAreaWithButtons m_TextLog;

  /** the button for emptying the log. */
  protected JButton m_ButtonClear;

  /** the button for copying the text. */
  protected JButton m_ButtonCopy;

  /** the button for saving the text. */
  protected JButton m_ButtonSave;

  /** the checkbox for linewrap. */
  protected JCheckBox m_CheckBoxLineWrap;

  /** the filechooser for saving the log. */
  protected transient BaseFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = createFileChooser();
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
    m_TextLog.setLineWrap(false);
    m_TextLog.setWrapStyleWord(true);
    add(m_TextLog, BorderLayout.CENTER);

    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.addActionListener((ActionEvent e) -> clear());
    m_TextLog.addToButtonsPanel(m_ButtonClear);

    m_ButtonCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.addActionListener((ActionEvent e) -> copy());
    m_TextLog.addToButtonsPanel(m_ButtonCopy);

    m_ButtonSave = new JButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener((ActionEvent e) -> saveAs());
    m_TextLog.addToButtonsPanel(m_ButtonSave);

    m_CheckBoxLineWrap = new JCheckBox("Line wrap");
    m_CheckBoxLineWrap.setSelected(false);
    m_CheckBoxLineWrap.addActionListener((ActionEvent e) -> setLineWrap(m_CheckBoxLineWrap.isSelected()));
    m_TextLog.addToButtonsPanel(m_CheckBoxLineWrap);
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
   * Creates the filechooser.
   *
   * @return		the filechooser
   */
  protected BaseFileChooser createFileChooser() {
    ExtensionFileFilter		filter;
    BaseFileChooser 		result;

    result = new BaseFileChooser();
    filter = ExtensionFileFilter.getLogFileFilter();
    result.addChoosableFileFilter(filter);
    result.addChoosableFileFilter(ExtensionFileFilter.getTextFileFilter());
    result.setFileFilter(filter);

    return result;
  }

  /**
   * Returns the filechooser to use.
   *
   * @return		the filechooser
   */
  protected BaseFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = createFileChooser();
    return m_FileChooser;
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
   * Appends the given string.
   *
   * @param level	the logging level
   * @param msg		the message to append
   */
  @Override
  public void append(LoggingLevel level, String msg) {
    boolean 	caretIsLast;

    caretIsLast = (m_TextLog.getCaretPosition() == m_TextLog.getDocument().getLength());

    m_TextLog.append(msg);
    m_TextLog.append("\n");

    // only move cursor if at end of document
    if (caretIsLast)
      m_TextLog.setCaretPositionLast();

    updateButtons();
  }

  /**
   * Clears the text.
   */
  @Override
  public void clear() {
    m_TextLog.setText("");
    updateButtons();
  }

  /**
   * Copies the text to the clipboard.
   */
  @Override
  public void copy() {
    if (m_TextLog.getSelectedText() != null)
      ClipboardHelper.copyToClipboard(m_TextLog.getSelectedText());
    else
      ClipboardHelper.copyToClipboard(m_TextLog.getText());
  }

  /**
   * Saves the current content to a file.
   */
  @Override
  public void saveAs() {
    int 	retVal;
    String 	msg;

    retVal = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    msg = FileUtils.writeToFileMsg(
      getFileChooser().getSelectedFile().getAbsolutePath(),
      m_TextLog.getText(),
      false,
      null);

    if (msg != null)
      GUIHelper.showErrorMessage(this, msg);
  }

  /**
   * Sets the line wrap flag.
   *
   * @param value	if true line wrap is enabled
   */
  @Override
  public void setLineWrap(boolean value) {
    m_TextLog.setLineWrap(value);
  }

  /**
   * Returns the current line wrap setting.
   *
   * @return		true if line wrap is enabled
   */
  @Override
  public boolean getLineWrap() {
    return m_TextLog.getLineWrap();
  }

  /**
   * Sets the current text.
   *
   * @param value	the text
   */
  public void setText(String value) {
    m_TextLog.setText(value);
    updateButtons();
  }

  /**
   * Returns the current text.
   *
   * @return		the text
   */
  public String getText() {
    return m_TextLog.getText();
  }
}
