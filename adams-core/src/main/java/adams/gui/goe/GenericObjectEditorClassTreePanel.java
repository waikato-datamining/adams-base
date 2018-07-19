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
 * GenericObjectEditorClassTreePanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe;

import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.goe.classtree.ClassTree;
import adams.gui.goe.classtree.StrictClassTreeFilter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Creates a panel with a class tree.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Xin Xu (xx5@cs.waikato.ac.nz)
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditorClassTreePanel
  extends BasePanel {

  /** for serialization. */
  static final long serialVersionUID = -3404546329655057387L;

  /** the popup itself. */
  protected GenericObjectEditorClassTreePanel m_Self;

  /** the info panel at the top. */
  protected JPanel m_PanelInfo;

  /** the info text at the top. */
  protected JLabel m_LabelInfo;

  /** The tree. */
  protected ClassTree m_Tree;

  /** The scroll pane. */
  protected BaseScrollPane m_ScrollPane;

  /** The search field. */
  protected JTextField m_TextSearch;

  /** The button for closing the popup again. */
  protected JButton m_CloseButton;

  /** the panel for the filter. */
  protected JPanel m_PanelFilter;

  /** The checkbox for enabling/disabling the class tree filter. */
  protected JCheckBox m_CheckBoxFilter;

  /** The checkbox for enabling/disabling strict filtering. */
  protected JCheckBox m_CheckBoxStrict;

  /** the minimum number of characters before triggering search events. */
  protected int m_MinimumChars;

  /**
   * Constructs a new popup menu.
   *
   * @param tree 	the tree to put in the menu
   */
  public GenericObjectEditorClassTreePanel(ClassTree tree) {
    super();

    m_Self = this;
    m_Tree = tree;

    initGUI();
    finishInit();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	bottomPanel;
    JPanel	panel;
    JPanel 	treeView;

    if (m_Tree == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(200, 400));

    m_LabelInfo = new JLabel("");
    m_LabelInfo.setLabelFor(m_Tree);
    m_PanelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelInfo.setVisible(false);
    m_PanelInfo.add(m_LabelInfo);
    add(m_PanelInfo, BorderLayout.NORTH);

    bottomPanel = new JPanel(new BorderLayout());
    add(bottomPanel, BorderLayout.SOUTH);

    // search
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bottomPanel.add(panel, BorderLayout.WEST);
    m_TextSearch = new JTextField(15);
    m_TextSearch.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
	update();
      }
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      public void removeUpdate(DocumentEvent e) {
	update();
      }
      protected void update() {
	if (m_TextSearch.getText().length() >= getMinimumChars())
	  m_Tree.setSearch(m_TextSearch.getText());
	else
	  m_Tree.setSearch("");
      }
    });
    JLabel labelSearch = new JLabel("Search");
    labelSearch.setDisplayedMnemonic('S');
    labelSearch.setLabelFor(m_TextSearch);
    panel.add(labelSearch);
    panel.add(m_TextSearch);

    // filter
    m_PanelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bottomPanel.add(m_PanelFilter, BorderLayout.SOUTH);

    m_CheckBoxFilter = new JCheckBox("Filtering");
    m_CheckBoxFilter.setMnemonic('F');
    m_CheckBoxFilter.addActionListener((ActionEvent e) -> {
      AbstractItemFilter filter = m_Tree.getFilter();
      filter.setEnabled(m_CheckBoxFilter.isSelected());
      m_Tree.setFilter(filter);
      m_CheckBoxStrict.setEnabled(
	m_CheckBoxFilter.isEnabled()
	  && m_CheckBoxFilter.isSelected()
	  && (m_Tree.getFilter() instanceof StrictClassTreeFilter));
    });
    m_PanelFilter.add(m_CheckBoxFilter);

    m_CheckBoxStrict = new JCheckBox("Strict mode");
    m_CheckBoxStrict.setMnemonic('m');
    m_CheckBoxStrict.addActionListener((ActionEvent e) -> {
      ((StrictClassTreeFilter) m_Tree.getFilter()).setStrict(
	!((StrictClassTreeFilter) m_Tree.getFilter()).isStrict());
      m_Tree.setFilter(m_Tree.getFilter());
    });
    m_PanelFilter.add(m_CheckBoxStrict);

    // close
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(panel, BorderLayout.EAST);
    m_CloseButton = new JButton("Close");
    m_CloseButton.setMnemonic('C');
    m_CloseButton.setVisible(false);
    m_CloseButton.addActionListener((ActionEvent e) -> {
      if (e.getSource() == m_CloseButton) {
        JPopupMenu menu = (JPopupMenu) GUIHelper.getParent(GenericObjectEditorClassTreePanel.this, JPopupMenu.class);
	menu.setVisible(false);
      }
    });
    panel.add(m_CloseButton);

    // tree
    treeView = new JPanel();
    treeView.setLayout(new BorderLayout());
    treeView.add(m_Tree, BorderLayout.NORTH);
    treeView.setBackground(m_Tree.getBackground());
    m_ScrollPane = new BaseScrollPane(treeView);
    m_ScrollPane.setPreferredSize(new Dimension(300, 400));
    add(m_ScrollPane, BorderLayout.CENTER);
  }

  /**
   * Sets the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @param value	the minimum number of characters (>= 1)
   */
  public void setMinimumChars(int value) {
    if (value >= 1)
      m_MinimumChars = value;
  }

  /**
   * Returns the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @return		the minimum number of characters (>= 1)
   */
  public int getMinimumChars() {
    return m_MinimumChars;
  }

  /**
   * Sets the info text to display at the top.
   * Use "_" before the character to use as the mnemonic for jumping into the
   * tree via the keyboard.
   *
   * @param value	the info text, null or empty to remove
   */
  public void setInfoText(String value) {
    if (value == null)
      value = "";
    if (GUIHelper.hasMnemonic(value)) {
      m_LabelInfo.setDisplayedMnemonic(GUIHelper.getMnemonic(value));
      value = GUIHelper.stripMnemonic(value);
    }
    else {
      m_LabelInfo.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
    }
    m_LabelInfo.setText(value);
    m_PanelInfo.setVisible(!value.isEmpty());
  }

  /**
   * Returns the current info text, if any.
   *
   * @return		the text, empty if none displayed
   */
  public String getInfoText() {
    return m_LabelInfo.getText();
  }

  /**
   * Returns the scroll pane.
   *
   * @return		the scroll pane
   */
  public BaseScrollPane getScrollPane() {
    return m_ScrollPane;
  }

  /**
   * Sets whether the close button is visible or not.
   *
   * @param value	true if visible
   */
  public void setCloseButtonVisible(boolean value) {
    m_CloseButton.setVisible(value);
  }

  /**
   * Returns whether the close button is visible.
   *
   * @return		true if visible
   */
  public boolean isCloseButtonVisible() {
    return m_CloseButton.isVisible();
  }

  /**
   * Focus the search text field.
   */
  public void focusSearch() {
    SwingUtilities.invokeLater(() -> m_TextSearch.requestFocus());
  }

  /**
   * Updates whether the filter panel is visible.
   */
  public void updateFilterPanel() {
    m_PanelFilter.setVisible((m_Tree != null) && (m_Tree.getFilter() != null));
    if (m_PanelFilter.isVisible()) {
      m_CheckBoxFilter.setSelected(m_Tree.getFilter().isEnabled());
      m_CheckBoxStrict.setEnabled(
	m_CheckBoxFilter.isEnabled()
	  && m_CheckBoxFilter.isSelected()
	  && (m_Tree.getFilter() instanceof StrictClassTreeFilter));
      m_CheckBoxStrict.setSelected(
	m_CheckBoxStrict.isEnabled()
	  && ((StrictClassTreeFilter) m_Tree.getFilter()).isStrict());
    }
  }

  /**
   * Sets the readonly state.
   *
   * @param value	true if readonly
   */
  public void setReadOnly(boolean value) {
    m_Tree.setEditable(!value);
    m_TextSearch.setEditable(!value);
    m_CheckBoxFilter.setEnabled(!value);
    m_CheckBoxStrict.setEnabled(!value);
  }

  /**
   * Returns the readonly state.
   *
   * @return		true if readonly
   */
  public boolean isReadOnly() {
    return m_TextSearch.isEditable();
  }
}
