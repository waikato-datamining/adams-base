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
 * OptionsConversionPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionProducer;
import adams.core.option.ArrayConsumer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionConsumer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionProducer;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * Helper panel for converting options from format into another.
 * Also outputs a string that can be used in Java source code.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionsConversionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -2881786410361917678L;

  /** the GOE panel for the input. */
  protected GenericObjectEditorPanel m_InputFormat;

  /** the text area for the input. */
  protected BaseTextAreaWithButtons m_TextAreaInput;

  /** the button for copying the input. */
  protected JButton m_ButtonInputCopy;

  /** the button for pasting the input. */
  protected JButton m_ButtonInputPaste;

  /** the button for loading the input from file. */
  protected JButton m_ButtonInputOpen;

  /** the GOE panel for the output. */
  protected GenericObjectEditorPanel m_OutputFormat;

  /** the text area for the output. */
  protected BaseTextAreaWithButtons m_TextAreaOutput;

  /** the button for copying the output. */
  protected JButton m_ButtonOutputCopy;

  /** the button for pasting the output. */
  protected JButton m_ButtonOutputPaste;

  /** the button for saving the output to a file. */
  protected JButton m_ButtonOutputSave;

  /** the text area for the code output. */
  protected BaseTextAreaWithButtons m_TextAreaCodeOutput;

  /** the button for copying the code output. */
  protected JButton m_ButtonCodeOutputCopy;

  /** the button for pasting the code output. */
  protected JButton m_ButtonCodeOutputPaste;

  /** the button for saving the code output to a file. */
  protected JButton m_ButtonCodeOutputSave;

  /** the button initiating the conversion. */
  protected JButton m_ButtonConvert;

  /** the file chooser for loading the options. */
  protected BaseFileChooser m_FileChooserInput;

  /** the file chooser for saving the options. */
  protected BaseFileChooser m_FileChooserOutput;

  /** the file chooser for saving the code options. */
  protected BaseFileChooser m_FileChooserCodeOutput;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooserInput      = new BaseFileChooser();
    m_FileChooserOutput     = new BaseFileChooser();
    m_FileChooserCodeOutput = new BaseFileChooser();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelAll;
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    panelAll = new JPanel(new GridLayout(3, 1));
    add(panelAll, BorderLayout.CENTER);

    // input
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Input"));
    panelAll.add(panel);

    m_InputFormat = new GenericObjectEditorPanel(OptionConsumer.class, new ArrayConsumer(), true);
    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2, BorderLayout.NORTH);
    panel2.add(m_InputFormat);

    m_TextAreaInput = new BaseTextAreaWithButtons();
    panel.add(m_TextAreaInput, BorderLayout.CENTER);

    m_ButtonInputCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonInputCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextAreaInput.getComponent().copy();
      }
    });
    m_TextAreaInput.addToButtonsPanel(m_ButtonInputCopy);

    m_ButtonInputPaste = new JButton("Paste", GUIHelper.getIcon("paste.gif"));
    m_ButtonInputPaste.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextAreaInput.getComponent().paste();
      }
    });
    m_TextAreaInput.addToButtonsPanel(m_ButtonInputPaste);

    m_ButtonInputOpen = new JButton("Open...", GUIHelper.getIcon("open.gif"));
    m_ButtonInputOpen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	openInput(m_FileChooserInput, m_TextAreaInput);
      }
    });
    m_TextAreaInput.addToButtonsPanel(m_ButtonInputOpen);

    // output
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Output"));
    panelAll.add(panel);

    m_OutputFormat = new GenericObjectEditorPanel(OptionProducer.class, new NestedProducer(), true);
    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2, BorderLayout.NORTH);
    panel2.add(m_OutputFormat);

    m_TextAreaOutput = new BaseTextAreaWithButtons();
    panel.add(m_TextAreaOutput, BorderLayout.CENTER);

    m_ButtonOutputCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonOutputCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextAreaOutput.getComponent().copy();
      }
    });
    m_TextAreaOutput.addToButtonsPanel(m_ButtonOutputCopy);

    m_ButtonOutputPaste = new JButton("Paste", GUIHelper.getIcon("paste.gif"));
    m_ButtonOutputPaste.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextAreaOutput.getComponent().paste();
      }
    });
    m_TextAreaOutput.addToButtonsPanel(m_ButtonOutputPaste);

    m_ButtonOutputSave = new JButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonOutputSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	saveOutput(m_FileChooserOutput, m_TextAreaOutput);
      }
    });
    m_TextAreaOutput.addToButtonsPanel(m_ButtonOutputSave);

    // code output
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Code output"));
    panelAll.add(panel);

    m_TextAreaCodeOutput = new BaseTextAreaWithButtons();
    panel.add(m_TextAreaCodeOutput, BorderLayout.CENTER);

    m_ButtonCodeOutputCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonCodeOutputCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextAreaCodeOutput.getComponent().copy();
      }
    });
    m_TextAreaCodeOutput.addToButtonsPanel(m_ButtonCodeOutputCopy);

    m_ButtonCodeOutputPaste = new JButton("Paste", GUIHelper.getIcon("paste.gif"));
    m_ButtonCodeOutputPaste.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextAreaCodeOutput.getComponent().paste();
      }
    });
    m_TextAreaCodeOutput.addToButtonsPanel(m_ButtonCodeOutputPaste);

    m_ButtonCodeOutputSave = new JButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonCodeOutputSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	saveOutput(m_FileChooserCodeOutput, m_TextAreaCodeOutput);
      }
    });
    m_TextAreaCodeOutput.addToButtonsPanel(m_ButtonCodeOutputSave);

    // conversion
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonConvert = new JButton("Convert");
    m_ButtonConvert.setMnemonic('C');
    m_ButtonConvert.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	convert();
      }
    });
    panel.add(m_ButtonConvert);
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
	  OptionsConversionPanel.this,
	  "Failed to write output to '" + filename + "'!");
    }
  }
  
  /**
   * Performs the conversion.
   */
  protected void convert() {
    OptionConsumer 	consumer;
    OptionHandler 	handler;
    OptionProducer 	producer;
    
    try {
      consumer = (OptionConsumer) m_InputFormat.getCurrent();
      handler  = AbstractOptionConsumer.fromString(consumer.getClass(), m_TextAreaInput.getText());
      producer = (OptionProducer) m_OutputFormat.getCurrent();
      m_TextAreaOutput.setText(AbstractOptionProducer.toString(producer.getClass(), handler));
      m_TextAreaOutput.getComponent().setCaretPosition(0);
      m_TextAreaCodeOutput.setText("\"" + Utils.backQuoteChars(m_TextAreaOutput.getText()) + "\"");
      m_TextAreaCodeOutput.getComponent().setCaretPosition(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      GUIHelper.showErrorMessage(
	  OptionsConversionPanel.this,
	  "Failed to convert options:\n" + ex);
    }
  }
}
