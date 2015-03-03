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
 * ReplaceFieldDialog.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.data.report.AbstractField;
import adams.data.report.FieldType;
import adams.gui.chooser.FieldChooserPanel;

/**
 * A dialog that allows the user to select a field to find and replace
 * with another one.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReplaceFieldDialog
  extends AbstractReplaceDialog<AbstractField> {

  /** for serialization. */
  private static final long serialVersionUID = 2899497954651703559L;

  /** the field to find. */
  protected FieldChooserPanel m_PanelFind;

  /** the field to replace with. */
  protected FieldChooserPanel m_PanelReplace;

  /** the type label. */
  protected JLabel m_LabelType;

  /** the type combobox. */
  protected JComboBox m_ComboBoxType;

  /**
   * Creates a modal dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public ReplaceFieldDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a modal dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public ReplaceFieldDialog(Frame owner) {
    super(owner);
  }

  /**
   * Returns the title of the dialog.
   *
   * @return		the title
   */
  protected String getDefaultTitle() {
    return "Replace field";
  }

  /**
   * Creates and returns the panel that is placed in the CENTER, containing
   * the find and replace fields. The panel must use the GridLayout layout
   * manager.
   *
   * @return		the generated panel
   */
  protected JPanel setupPanel() {
    JPanel	result;
    JPanel	panel;

    result = new JPanel(new GridLayout(4, 1));

    // find
    m_PanelFind = new FieldChooserPanel();
    m_PanelFind.setPrefix("_Find");
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelFind);
    result.add(panel);

    // replace
    m_PanelReplace = new FieldChooserPanel();
    m_PanelReplace.setPrefix("_Replace");
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelReplace);
    result.add(panel);

    // type
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    result.add(panel);
    m_ComboBoxType = new JComboBox(FieldType.values());
    m_ComboBoxType.setSelectedItem(FieldType.FIELD);
    m_ComboBoxType.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setType((FieldType) m_ComboBoxType.getSelectedItem());
      }
    });
    m_LabelType = new JLabel("Type");
    m_LabelType.setDisplayedMnemonic('T');
    m_LabelType.setLabelFor(m_ComboBoxType);
    panel.add(m_LabelType);
    panel.add(m_ComboBoxType);

    return result;
  }

  /**
   * Method for adjusting the label sizes.
   */
  protected void adjustLabels() {
    m_PanelReplace.getPrefixLabel().setPreferredSize(m_LabelRecursive.getPreferredSize());
    m_PanelReplace.getPrefixLabel().setSize(m_LabelRecursive.getSize());
    m_PanelFind.getPrefixLabel().setPreferredSize(m_LabelRecursive.getPreferredSize());
    m_PanelFind.getPrefixLabel().setSize(m_LabelRecursive.getSize());
    m_LabelType.setPreferredSize(m_LabelRecursive.getPreferredSize());
    m_LabelType.setSize(m_LabelRecursive.getSize());
  }

  /**
   * Sets the type of fields to allow. Resets the find and replace field.
   *
   * @param value	the type
   */
  public void setType(FieldType value) {
    m_PanelFind.setFieldType(value);
    m_PanelReplace.setFieldType(value);
  }

  /**
   * Sets the field to find. Performs some checks against the currently set
   * type.
   *
   * @param value	the field to find
   * @see		#m_Type
   */
  public void setFind(AbstractField value) {
    m_PanelFind.setCurrent(value);
  }

  /**
   * Returns the currently set field to look for.
   *
   * @return		the field, can be null
   */
  public AbstractField getFind() {
    return m_PanelFind.getCurrent();
  }

  /**
   * Sets the field to replace with. Performs some checks against the currently
   * set type.
   *
   * @param value	the field to replace with
   * @see		#m_Type
   */
  public void setReplace(AbstractField value) {
    m_PanelReplace.setCurrent(value);
  }

  /**
   * Returns the currently set field to replace with.
   *
   * @return		the field, can be null
   */
  public AbstractField getReplace() {
    return m_PanelReplace.getCurrent();
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    super.beforeShow();

    // initialize type
    setType((FieldType) m_ComboBoxType.getSelectedItem());
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();

    m_PanelFind.cleanUp();
    m_PanelReplace.cleanUp();
  }
}
