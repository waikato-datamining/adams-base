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
 * BaseLogPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple log panel with Clear/Copy buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseLogPanel
  extends BasePanel {

  /** the text area for the log. */
  protected BaseTextArea m_TextLog;

  /** the button for clearing the log. */
  protected JButton m_ButtonClearLog;

  /** the button for copy the log. */
  protected JButton m_ButtonCopyLog;

  /** the button for saving the log. */
  protected JButton m_ButtonSaveLog;

  /** the file chooser for saving the log. */
  protected BaseFileChooser m_FileChooser;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel panel;

    super.initGUI();

    m_TextLog = new BaseTextArea();
    m_TextLog.setLineWrap(false);
    m_TextLog.setEditable(false);
    m_TextLog.setFont(Fonts.getMonospacedFont());
    m_TextLog.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
      public void updateButtons() {
	boolean hasContent = (m_TextLog.getDocument().getLength() > 0);
	m_ButtonClearLog.setEnabled(hasContent);
	m_ButtonCopyLog.setEnabled(hasContent);
	m_ButtonSaveLog.setEnabled(hasContent);
      }
    });
    add(new BaseScrollPane(m_TextLog), BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonClearLog = new JButton(GUIHelper.getIcon("new.gif"));
    m_ButtonClearLog.setEnabled(false);
    m_ButtonClearLog.setToolTipText("Clear log");
    m_ButtonClearLog.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    panel.add(m_ButtonClearLog);

    m_ButtonCopyLog = new JButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonCopyLog.setEnabled(false);
    m_ButtonCopyLog.setToolTipText("Copy log");
    m_ButtonCopyLog.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	copy();
      }
    });
    panel.add(m_ButtonCopyLog);

    m_ButtonSaveLog = new JButton(GUIHelper.getIcon("save.gif"));
    m_ButtonSaveLog.setEnabled(false);
    m_ButtonSaveLog.setToolTipText("Save log");
    m_ButtonSaveLog.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	save();
      }
    });
    panel.add(m_ButtonSaveLog);
  }

  /**
   * Sets the number rows in the text area.
   *
   * @param value	the number of rows
   */
  public void setRows(int value) {
    m_TextLog.setRows(value);
  }

  /**
   * Returns the currently number of rows in the text area.
   *
   * @return		the number of rows
   */
  public int getRows() {
    return m_TextLog.getRows();
  }

  /**
   * Sets the number columns in the text area.
   *
   * @param value	the number of columns
   */
  public void setColumns(int value) {
    m_TextLog.setColumns(value);
  }

  /**
   * Returns the currently number of columns in the text area.
   *
   * @return		the number of columns
   */
  public int getColumns() {
    return m_TextLog.getColumns();
  }

  /**
   * Returns the current log content.
   *
   * @return		the current log
   */
  public String getLog() {
    return m_TextLog.getText();
  }

  /**
   * Clears the log content.
   */
  public void clear() {
    m_TextLog.setText("");
  }

  /**
   * Copies the log content (selected or all) to the clipboard.
   */
  public void copy() {
    if (m_TextLog.getSelectedText() == null)
      ClipboardHelper.copyToClipboard(m_TextLog.getText());
    else
      ClipboardHelper.copyToClipboard(m_TextLog.getSelectedText());
  }

  /**
   * Saves the log to a file that the user chooses.
   */
  public void save() {
    ExtensionFileFilter		filter;
    int				retVal;

    if (m_FileChooser == null) {
      m_FileChooser = new BaseFileChooser();
      m_FileChooser.setAutoAppendExtension(true);
      filter = ExtensionFileFilter.getLogFileFilter();
      m_FileChooser.addChoosableFileFilter(filter);
      m_FileChooser.addChoosableFileFilter(ExtensionFileFilter.getTextFileFilter());
      m_FileChooser.setFileFilter(filter);
    }

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    if (!FileUtils.writeToFile(m_FileChooser.getSelectedFile().getAbsolutePath(), getLog(), false))
      GUIHelper.showErrorMessage(this, "Failed to write log content to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Appens the log message. Automatically appends a newline.
   *
   * @param msg		the message to append
   */
  public void append(String msg) {
    if (!msg.endsWith("\n"))
      msg += "\n";
    m_TextLog.append(msg);
  }
}
