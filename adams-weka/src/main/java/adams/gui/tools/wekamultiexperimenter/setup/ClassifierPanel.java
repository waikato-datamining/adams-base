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
 * ClassifierPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.setup;

import adams.core.option.OptionUtils;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.ConsolePanel;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Panel for listing datasets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifierPanel
  extends AbstractSetupOptionPanel {

  /** for serialization. */
  private static final long serialVersionUID = -832431512063524253L;

  /** the GOE for setting up classifiers. */
  protected WekaGenericObjectEditorPanel m_PanelGOE;
  
  /** for listing the classifiers. */
  protected BaseListWithButtons m_List;
  
  /** the button for adding classifiers. */
  protected JButton m_ButtonAdd;
  
  /** the button for editing a classifier. */
  protected JButton m_ButtonEdit;
  
  /** the button for removing classifiers. */
  protected JButton m_ButtonRemove;
  
  /** the button for removing all classifiers. */
  protected JButton m_ButtonRemoveAll;
  
  /** the button for moving classifiers up. */
  protected JButton m_ButtonUp;
  
  /** the button for moving classifiers down. */
  protected JButton m_ButtonDown;
  
  /** the model. */
  protected DefaultListModel<String> m_Model;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Model = new DefaultListModel<>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_PanelGOE = new WekaGenericObjectEditorPanel(Classifier.class, new ZeroR(), true);
    add(m_PanelGOE, BorderLayout.NORTH);
    
    m_List = new BaseListWithButtons(m_Model);
    add(m_List, BorderLayout.CENTER);
    
    m_ButtonAdd = new JButton("Add");
    m_ButtonAdd.addActionListener((ActionEvent e) -> {
      m_Model.addElement(OptionUtils.getCommandLine(m_PanelGOE.getCurrent()));
      modified();
      update();
    });
    
    m_ButtonEdit = new JButton("Edit");
    m_ButtonEdit.addActionListener((ActionEvent e) -> {
      try {
        m_PanelGOE.setCurrent(OptionUtils.forAnyCommandLine(Classifier.class, (String) m_List.getSelectedValue()));
        m_PanelGOE.choose();
        m_Model.setElementAt(OptionUtils.getCommandLine(m_PanelGOE.getCurrent()), m_List.getSelectedIndex());
        modified();
      }
      catch (Exception ex) {
        System.err.println("Failed to instantiate classifier: " + m_List.getSelectedValue());
        ex.printStackTrace();
      }
      update();
    });
    
    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.addActionListener((ActionEvent e) -> {
      int[] indices = m_List.getSelectedIndices();
      Arrays.sort(indices);
      for (int i = indices.length - 1; i >= 0; i--)
        m_Model.remove(indices[i]);
      modified();
      update();
    });

    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.addActionListener((ActionEvent e) -> {
      m_Model.clear();
      modified();
      update();
    });

    m_ButtonUp = new JButton("Up");
    m_ButtonUp.addActionListener((ActionEvent e) -> {
      m_List.moveUp();
      modified();
      update();
    });

    m_ButtonDown = new JButton("Down");
    m_ButtonDown.addActionListener((ActionEvent e) -> {
      m_List.moveDown();
      modified();
      update();
    });

    m_List.addToButtonsPanel(m_ButtonAdd);
    m_List.addToButtonsPanel(m_ButtonEdit);
    m_List.addToButtonsPanel(m_ButtonRemove);
    m_List.addToButtonsPanel(m_ButtonRemoveAll);
    m_List.addToButtonsPanel(new JLabel(""));
    m_List.addToButtonsPanel(m_ButtonUp);
    m_List.addToButtonsPanel(m_ButtonDown);
    m_List.setDoubleClickButton(m_ButtonEdit);
    
    m_List.addListSelectionListener((ListSelectionEvent e) -> update());
  }
  
  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }
  
  /**
   * Updates the buttons.
   */
  @Override
  protected void update() {
    super.update();
    m_ButtonEdit.setEnabled(m_List.getSelectedIndices().length == 1);
    m_ButtonRemove.setEnabled(m_List.getSelectedIndices().length > 0);
    m_ButtonRemoveAll.setEnabled(m_Model.getSize() > 0);
    m_ButtonUp.setEnabled(m_List.canMoveUp());
    m_ButtonDown.setEnabled(m_List.canMoveDown());
  }
  
  /**
   * Sets the classifiers to use.
   * 
   * @param value	the classifiers
   */
  public void setClassifiers(Classifier[] value) {
    m_IgnoreChanges = true;
    
    m_Model.clear();
    for (Classifier c: value)
      m_Model.addElement(OptionUtils.getCommandLine(c));
    
    m_IgnoreChanges = false;
  }
  
  /**
   * Returns the current classifiers.
   * 
   * @return		the classifiers
   */
  public Classifier[] getClassifiers() {
    Classifier[]	result;
    int			i;
    
    result = new Classifier[m_Model.getSize()];
    for (i = 0; i < m_Model.getSize(); i++) {
      try {
	result[i] = (Classifier) OptionUtils.forAnyCommandLine(Classifier.class, m_Model.getElementAt(i));
      }
      catch (Exception e) {
        ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate classifier: " + m_Model.getElementAt(i), e);
	result[i] = new ZeroR();
      }
    }
    
    return result;
  }
}
