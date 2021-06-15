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
 * MavenArtifactExclusionEditor.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.base.BaseObject;
import adams.core.base.MavenArtifactExclusion;
import adams.core.option.parsing.MavenArtifactExclusionParsing;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Editor specifically designed for entering Maven artifact exclusions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MavenArtifactExclusionEditor
  extends BaseObjectEditor
  implements MultiSelectionEditor {

  /** The text field with the group ID. */
  protected JTextComponent m_TextGroupID;

  /**
   * Accepts the input and closes the dialog.
   */
  protected void acceptInput() {
    String 	group;
    String 	artifact;
    String 	full;

    group    = m_TextGroupID.getText();
    artifact = m_TextValue.getText();
    full     = group + MavenArtifactExclusion.SEPARATOR + artifact;
    if (isValid(full) && !isUnchanged(full))
      setValue(parse(full));
    closeDialog(APPROVE_OPTION);
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    ParameterPanel	panelPair;
    JPanel 		panelButtons;
    BaseButton 		buttonClear;
    BaseButton 		buttonOK;
    BaseButton 		buttonClose;

    panelAll  = new JPanel(new BorderLayout());
    panelPair = new ParameterPanel();
    panelAll.add(panelPair, BorderLayout.CENTER);

    m_TextGroupID    = new BaseTextField(30);
    m_TextValue      = new BaseTextField(30);

    panelPair.addParameter("_Group ID",   m_TextGroupID);
    panelPair.addParameter("Artifact ID", m_TextValue);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonClear = new BaseButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextGroupID.setText("");
	m_TextValue.setText("");
      }
    });
    panelButtons.add(buttonClear);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	acceptInput();
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	discardInput();
      }
    });
    panelButtons.add(buttonClose);

    return panelAll;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    MavenArtifactExclusion	value;

    resetChosenOption();
    
    value = (MavenArtifactExclusion) getValue();

    if (!m_TextGroupID.getText().equals(value.groupIdValue()))
      m_TextGroupID.setText(value.groupIdValue());
    if (!m_TextValue.getText().equals(value.artifactIdValue()))
      m_TextValue.setText(value.artifactIdValue());
    m_TextGroupID.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
    m_TextGroupID.grabFocus();
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  @Override
  public String toCustomStringRepresentation(Object obj) {
    return MavenArtifactExclusionParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  @Override
  public Object fromCustomStringRepresentation(String str) {
    return MavenArtifactExclusionParsing.valueOf(null, str);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    MavenArtifactExclusion[]	result;
    MultiLineValueDialog	dialog;
    List<String> 		lines;
    int				i;

    if (GUIHelper.getParentDialog(parent) != null)
      dialog = new MultiLineValueDialog(GUIHelper.getParentDialog(parent));
    else
      dialog = new MultiLineValueDialog(GUIHelper.getParentFrame(parent));
    dialog.setInfoText("Enter the artifact coordinates, one per line ('groupId:artifactId:version'):");
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    lines  = dialog.getValues();
    result = new MavenArtifactExclusion[lines.size()];
    for (i = 0; i < lines.size(); i++)
      result[i] = (MavenArtifactExclusion) parse(lines.get(i));

    return result;
  }
}
