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
 * DataGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.source;

import adams.core.option.OptionUtils;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.WekaGenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.data.DataGeneratorContainer;
import weka.datagenerators.classifiers.classification.LED24;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * For generating data using a data generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataGenerator
  extends AbstractSource {

  private static final long serialVersionUID = 5646388990155938153L;

  /** the last filechooser. */
  protected weka.datagenerators.DataGenerator m_Generator;

  /**
   * Instantiates the action.
   */
  public DataGenerator() {
    super();
    setName("Data generator...");
    setIcon("copy_table.gif");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    WekaGenericObjectEditorDialog	dialog;

    if (m_Generator == null)
      m_Generator = new LED24();

    if (getOwner().getParentDialog() != null)
      dialog = new WekaGenericObjectEditorDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new WekaGenericObjectEditorDialog(getOwner().getParentFrame(), true);
    dialog.setTitle("Data generator");
    dialog.setEditor(new weka.gui.GenericObjectEditor(true));
    dialog.getGOEEditor().setClassType(weka.datagenerators.DataGenerator.class);
    dialog.setCurrent(m_Generator);
    dialog.pack();
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      dialog.dispose();
      logMessage("Data generation cancelled!");
      return;
    }
    m_Generator = (weka.datagenerators.DataGenerator) dialog.getCurrent();
    dialog.dispose();

    addData(new DataGeneratorContainer((weka.datagenerators.DataGenerator) OptionUtils.shallowCopy(m_Generator)));
  }
}
