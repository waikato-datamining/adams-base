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
import adams.gui.tools.wekainvestigator.data.DataGeneratorContainer;
import weka.datagenerators.classifiers.classification.LED24;
import weka.gui.explorer.DataGeneratorPanel;

import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
    final DataGeneratorPanel	generatorPanel;
    final JDialog 		dialog;
    final JButton 		generateButton;

    if (m_Generator == null)
      m_Generator = new LED24();

    generatorPanel = new DataGeneratorPanel();
    dialog = new JDialog();
    generateButton = new JButton("Generate");
    generatorPanel.setGenerator(m_Generator);
    generatorPanel.setPreferredSize(new Dimension(300,
      (int) generatorPanel.getPreferredSize().getHeight()));
    generateButton.setMnemonic('G');
    generateButton.setToolTipText("Generates the dataset according the settings.");
    generateButton.addActionListener((ActionEvent evt) -> {
        generatorPanel.execute();
        boolean generated = (generatorPanel.getInstances() != null);
        if (generated) {
	  m_Generator = generatorPanel.getGenerator();
	  addData(new DataGeneratorContainer((weka.datagenerators.DataGenerator) OptionUtils.shallowCopy(m_Generator)));
	}
        dialog.dispose();
    });
    dialog.setTitle("DataGenerator");
    dialog.getContentPane().add(generatorPanel, BorderLayout.CENTER);
    dialog.getContentPane().add(generateButton, BorderLayout.EAST);
    dialog.pack();
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
  }
}
