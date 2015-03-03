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
 * MultiLineValueDialog.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import adams.core.Utils;
import adams.gui.core.TextEditorPanel;
import adams.gui.dialog.ApprovalDialog;

/**
 * Dialog for entering multiple values (one per line) for MultiSelectionEditor
 * editors that can parse objects from text.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see MultiSelectionEditor
 */
public class MultiLineValueDialog
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = -3749635663753848815L;

  /** encloses all panels. */
  protected JPanel m_PanelAll;
  
  /** the panel for the info text. */
  protected JPanel m_PanelInfo;

  /** the panel at the bottom for additional options. */
  protected JPanel m_PanelBottom;
  
  /** the label with the info text. */
  protected JLabel m_LabelInfo;
  
  /** the count prefix. */
  protected String m_PrefixCount;
  
  /** the label with count of items. */
  protected JLabel m_LabelCount;
  
  /** the editor for entering the text. */
  protected TextEditorPanel m_Editor;

  /**
   * Default constructor.
   */
  public MultiLineValueDialog() {
    super((Frame) null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PrefixCount = "Value count: ";
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();

    m_Editor = new TextEditorPanel();
    m_Editor.getTextArea().getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateCount();
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateCount();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateCount();
      }
    });

    m_LabelInfo = new JLabel("Enter the values, one per line:");
    m_PanelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelInfo.add(m_LabelInfo);

    m_PanelBottom = new JPanel(new BorderLayout());
    m_PanelBottom.setVisible(true);
    
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelBottom.add(panel, BorderLayout.SOUTH);
    m_LabelCount = new JLabel("");
    panel.add(m_LabelCount);

    m_PanelAll = new JPanel(new BorderLayout());
    m_PanelAll.add(m_PanelInfo, BorderLayout.NORTH);
    m_PanelAll.add(m_Editor, BorderLayout.CENTER);
    m_PanelAll.add(m_PanelBottom, BorderLayout.SOUTH);

    getContentPane().add(m_PanelAll, BorderLayout.CENTER);
    setModalityType(ModalityType.DOCUMENT_MODAL);
    setSize(400, 300);
  }
  
  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    updateCount();
  }
  
  /**
   * Updates the count display.
   */
  protected void updateCount() {
    String		text;
    ArrayList<String>	list;
    
    list = new ArrayList(Arrays.asList(m_Editor.getTextArea().getText().split("\n")));
    Utils.removeEmptyLines(list, true);
    text = m_PrefixCount + list.size();

    m_LabelCount.setText(text);
  }
  
  /**
   * Sets the info text. 
   * 
   * @param value	the info text
   */
  public void setInfoText(String value) {
    m_LabelInfo.setText(value);
  }
  
  /**
   * Returns the current info text.
   * 
   * @return		the info text
   */
  public String getInfoText() {
    return m_LabelInfo.getText();
  }
  
  /**
   * Returns the panel at the bottom (using {@link BorderLayout}), which 
   * displays the count of values in SOUTH. Can be used for further options.
   * 
   * @return		the panel
   */
  public JPanel getBottomPanel() {
    return m_PanelBottom;
  }
  
  /**
   * Sets the prefix to use for the count display.
   * 
   * @param value	the new prefix
   */
  public void setPrefixCount(String value) {
    m_PrefixCount = value;
    updateCount();
  }
  
  /**
   * Returns the prefix in use for the count display.
   * 
   * @return		the current prefix
   */
  public String getPrefixCount() {
    return m_PrefixCount;
  }
  
  /**
   * Sets the string content to display.
   * 
   * @param value	the text to display
   */
  public void setContent(String value) {
    m_Editor.setContent(value);
  }
  
  /**
   * Returns the string content that was entered.
   * 
   * @return		the entered text
   */
  public String getContent() {
    return m_Editor.getContent();
  }
}
