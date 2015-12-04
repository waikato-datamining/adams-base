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
 * HelpDialog.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.BrowserHelper.DefaultHyperlinkListener;
import adams.gui.core.Fonts;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dialog for displaying help text (HTML or plain text).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HelpDialog
  extends DialogWithButtons {

  /** for serialization. */
  private static final long serialVersionUID = 8716599240055591957L;

  /** for displaying the help text. */
  protected JEditorPane m_TextArea;
  
  /** the button for closing the dialog. */
  protected JButton m_ButtonClose;

  /**
   * Initializes the dialog.
   *
   * @param parent	the parent window
   */
  public HelpDialog(Dialog parent) {
    super(parent);
  }

  /**
   * Initializes the dialog.
   *
   * @param parent	the parent window
   */
  public HelpDialog(Frame parent) {
    super(parent);
  }

  /**
   * Initializes the dialog.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Help");

    m_TextArea = new JEditorPane();
    m_TextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_TextArea.setEditable(false);
    m_TextArea.setFont(Fonts.getMonospacedFont());
    m_TextArea.setAutoscrolls(true);
    m_TextArea.addHyperlinkListener(new DefaultHyperlinkListener());
    m_TextArea.addKeyListener(getKeyListener());
    getContentPane().add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);

    // buttons
    m_ButtonClose = new JButton("Close");
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addKeyListener(getKeyListener());
    m_ButtonClose.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	close();
      }
    });
    m_PanelButtonsRight.add(m_ButtonClose);
    
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	close();
      }
    });
  }
  
  /**
   * finishes the initialization, by setting size/location.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    pack();
    m_ButtonClose.requestFocusInWindow();
  }

  /**
   * Returns the {@link KeyListener} to use for text and button.
   * 
   * @return		the listener
   */
  protected KeyListener getKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if ((e.getKeyCode() == KeyEvent.VK_W) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
	  e.consume();
	  close();
	}
	else {
	  super.keyPressed(e);
	}
      }
    };
  }
  
  /**
   * Closes the dialog.
   */
  protected void close() {
    dispose();
  }

  /**
   * Sets the help text.
   *
   * @param value	the help text
   * @param isHtml	whether the text is html or plain text
   */
  public void setHelp(String value, boolean isHtml) {
    if (isHtml)
      m_TextArea.setContentType("text/html");
    else
      m_TextArea.setContentType("text/plain");
    m_TextArea.setText(value);
    m_TextArea.setCaretPosition(0);
 }
}
