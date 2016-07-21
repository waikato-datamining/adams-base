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
 * JsonTreeWithPreview.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.json;

import adams.core.JsonSupporter;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel with a JSON tree and a text area for previewing the data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonTreeWithPreview
  extends BasePanel 
  implements JsonSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7380711856972284896L;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the JSON tree. */
  protected JsonTree m_Tree;
  
  /** the preview. */
  protected BaseTextArea m_TextArea;

  /** the button for copying. */
  protected JButton m_ButtonCopy;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelPreview;
    JPanel	panelButtons;
    
    super.initGUI();
    
    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);

    // tree
    m_Tree = new JsonTree();
    m_Tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
	updateButtons();
	JsonNode node = (JsonNode) e.getPath().getLastPathComponent();
	if (node.hasValue())
	  m_TextArea.setText("" + node.getValue());
	else
	  m_TextArea.setText("");
	m_TextArea.setCaretPosition(0);
      }
    });
    m_SplitPane.setTopComponent(new BaseScrollPane(m_Tree));

    // preview
    panelPreview = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(panelPreview);

    m_TextArea = new BaseTextArea();
    m_TextArea.setEditable(false);
    m_TextArea.setLineWrap(true);
    m_TextArea.setWrapStyleWord(true);
    m_TextArea.setFont(Fonts.getMonospacedFont());
    panelPreview.add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
    
    panelButtons = new JPanel(new BorderLayout());
    panelPreview.add(panelButtons, BorderLayout.EAST);
    
    m_ButtonCopy = new JButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	ClipboardHelper.copyToClipboard(m_TextArea.getText());
      }
    });
    panelButtons.add(m_ButtonCopy, BorderLayout.NORTH);
  }
  
  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    updateButtons();
  }
  
  /**
   * Returns the underlying tree.
   * 
   * @return		the tree
   */
  public JsonTree getTree() {
    return m_Tree;
  }
  
  /**
   * Returns the underlying text area.
   * 
   * @return		the text area
   */
  public BaseTextArea getTextArea() {
    return m_TextArea;
  }
  
  /**
   * Sets the JSON object to display.
   * 
   * @param value	the JSON object to display
   */
  public void setJSON(JSONAware value) {
    m_Tree.setJSON(value);
  }
  
  /**
   * Returns the JSON object on display.
   * 
   * @return		the JSON object, null if none displayed
   */
  public JSONAware getJSON() {
    return m_Tree.getJSON();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonCopy.setEnabled(m_Tree.getSelectionCount() > 0);
  }
  
  /**
   * Sets whether to sort the keys of {@link JSONObject} objects.
   * Triggers a re-build of the tree.
   * 
   * @param value	true if to sort the keys
   */
  public void setSortKeys(boolean value) {
    m_Tree.setSortKeys(value);
  }
  
  /**
   * Returns whether the keys of {@link JSONObject} objects are sorted.
   * 
   * @return		true if keys get sorted
   */
  public boolean getSortKeys() {
    return m_Tree.getSortKeys();
  }
}
