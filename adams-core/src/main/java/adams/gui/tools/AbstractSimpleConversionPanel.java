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
 * AbstractSimpleConversionPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.GUIHelper;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Helper panel for converting data from one format into another.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimpleConversionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -2881786410361917678L;

  /** the text area for the input. */
  protected BaseTextAreaWithButtons m_TextAreaInput;

  /** the button for copying the input. */
  protected JButton m_ButtonInputCopy;

  /** the button for pasting the input. */
  protected JButton m_ButtonInputPaste;

  /** the button for loading the input from file. */
  protected JButton m_ButtonInputOpen;

  /** the text area for the output. */
  protected BaseTextAreaWithButtons m_TextAreaOutput;

  /** the button for copying the output. */
  protected JButton m_ButtonOutputCopy;

  /** the button for pasting the output. */
  protected JButton m_ButtonOutputPaste;

  /** the button for saving the output to a file. */
  protected JButton m_ButtonOutputSave;

  /** the button initiating the conversion. */
  protected JButton m_ButtonConvert;

  /** the file chooser for loading the options. */
  protected BaseFileChooser m_FileChooserInput;

  /** the file chooser for saving the options. */
  protected BaseFileChooser m_FileChooserOutput;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooserInput  = newInputFileChooser();
    m_FileChooserOutput = newOutputFileChooser();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelAll;
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    panelAll = new JPanel(new GridLayout(2, 1));
    add(panelAll, BorderLayout.CENTER);

    // input
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Input"));
    panelAll.add(panel);

    m_TextAreaInput = new BaseTextAreaWithButtons();
    panel.add(m_TextAreaInput, BorderLayout.CENTER);

    m_ButtonInputCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonInputCopy.addActionListener((ActionEvent e) -> m_TextAreaInput.getComponent().copy());
    m_TextAreaInput.addToButtonsPanel(m_ButtonInputCopy);

    m_ButtonInputPaste = new JButton("Paste", GUIHelper.getIcon("paste.gif"));
    m_ButtonInputPaste.addActionListener((ActionEvent e) -> m_TextAreaInput.getComponent().paste());
    m_TextAreaInput.addToButtonsPanel(m_ButtonInputPaste);

    m_ButtonInputOpen = new JButton("Open...", GUIHelper.getIcon("open.gif"));
    m_ButtonInputOpen.addActionListener((ActionEvent e) -> openInput(m_FileChooserInput, m_TextAreaInput));
    m_TextAreaInput.addToButtonsPanel(m_ButtonInputOpen);

    // output
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Output"));
    panelAll.add(panel);

    m_TextAreaOutput = new BaseTextAreaWithButtons();
    panel.add(m_TextAreaOutput, BorderLayout.CENTER);

    m_ButtonOutputCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonOutputCopy.addActionListener((ActionEvent e) -> m_TextAreaOutput.getComponent().copy());
    m_TextAreaOutput.addToButtonsPanel(m_ButtonOutputCopy);

    m_ButtonOutputPaste = new JButton("Paste", GUIHelper.getIcon("paste.gif"));
    m_ButtonOutputPaste.addActionListener((ActionEvent e) -> m_TextAreaOutput.getComponent().paste());
    m_TextAreaOutput.addToButtonsPanel(m_ButtonOutputPaste);

    m_ButtonOutputSave = new JButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonOutputSave.addActionListener((ActionEvent e) -> saveOutput(m_FileChooserOutput, m_TextAreaOutput));
    m_TextAreaOutput.addToButtonsPanel(m_ButtonOutputSave);

    // conversion
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonConvert = new JButton("Convert");
    m_ButtonConvert.setMnemonic('C');
    m_ButtonConvert.addActionListener((ActionEvent e) -> convert());
    panel.add(m_ButtonConvert);
  }

  /**
   * Returns the file chooser to use for the input.
   *
   * @return		the file chooser
   */
  protected BaseFileChooser newInputFileChooser() {
    return new BaseFileChooser();
  }

  /**
   * Returns the file chooser to use for the output.
   *
   * @return		the file chooser
   */
  protected BaseFileChooser newOutputFileChooser() {
    return new BaseFileChooser();
  }

  /**
   * Loads the data from a file.
   * 
   * @param filechooser	the file chooser to use
   * @param textarea	where to store the content
   */
  protected void openInput(BaseFileChooser filechooser, BaseTextAreaWithButtons textarea) {
    int 		retVal;
    List<String> 	lines;
    
    retVal = filechooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;
    
    lines = FileUtils.loadFromFile(filechooser.getSelectedFile());
    textarea.setText(Utils.flatten(lines, "\n"));
    textarea.setCaretPosition(0);
  }

  /**
   * Saves the output to a file.
   * 
   * @param filechooser	the file chooser to use
   * @param textarea	the text to save
   */
  protected void saveOutput(BaseFileChooser filechooser, BaseTextAreaWithButtons textarea) {
    int 	retVal;
    String 	filename;
    
    retVal = filechooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;
    
    filename = filechooser.getSelectedFile().getAbsolutePath();
    if (!FileUtils.writeToFile(filename, textarea.getText(), false)) {
      GUIHelper.showErrorMessage(
	  AbstractSimpleConversionPanel.this,
	  "Failed to write output to '" + filename + "'!");
    }
  }
  
  /**
   * Performs the conversion.
   */
  protected abstract void convert();
}
