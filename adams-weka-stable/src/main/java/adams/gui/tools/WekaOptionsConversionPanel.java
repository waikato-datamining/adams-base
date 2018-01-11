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
 * WekaOptionsConversionPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import weka.core.OptionHandler;
import weka.core.Utils;
import adams.core.option.WekaCommandLineHandler;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.GUIHelper;

/**
 * Helper panel that turns Weka commandline strings into quoted strings
 * suitable to be placed into code.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaOptionsConversionPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -5846361688841154052L;

  /** the text area for the input. */
  protected BaseTextAreaWithButtons m_TextAreaInput;

  /** the button for copying the input. */
  protected JButton m_ButtonInputCopy;

  /** the button for pasting the input. */
  protected JButton m_ButtonInputPaste;

  /** the text area for the output. */
  protected BaseTextAreaWithButtons m_TextAreaCodeOutput;

  /** the button for copying the code output. */
  protected JButton m_ButtonCodeOutputCopy;

  /** the button for pasting the code output. */
  protected JButton m_ButtonCodeOutputPaste;

  /** the button initiating the conversion. */
  protected JButton m_ButtonConvert;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
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
   * Performs the conversion.
   */
  protected void convert() {
    WekaCommandLineHandler	cmd;
    Object			obj;
    StringBuilder		buf;
    OptionHandler		handler;
    
    try {
      cmd = new WekaCommandLineHandler();
      obj = cmd.fromCommandLine(m_TextAreaInput.getText());
      
      buf = new StringBuilder();
      buf.append("--> String:\n");
      buf.append("\n");
      buf.append("\"" + Utils.backQuoteChars(cmd.toCommandLine(obj)) + "\"\n");
      buf.append("\n");
      buf.append("\n");
      buf.append("--> Test class:\n");
      buf.append("\n");
      buf.append("public class OptionsTest {\n");
      buf.append("\n");
      buf.append("  public static void main(String[] args) throws Exception {\n");
      buf.append("    // create new instance of scheme\n");
      buf.append("    " + obj.getClass().getName() + " scheme = new " + obj.getClass().getName() + "();\n");
      if (obj instanceof OptionHandler) {
        handler = (OptionHandler) obj;
        buf.append("    \n");
        buf.append("    // set options\n");
        buf.append("    scheme.setOptions(weka.core.Utils.splitOptions(\"" + Utils.backQuoteChars(Utils.joinOptions(handler.getOptions())) + "\"));\n");
        buf.append("  }\n");
      }
      buf.append("}\n");
      
      m_TextAreaCodeOutput.setText(buf.toString());
      m_TextAreaCodeOutput.getComponent().setCaretPosition(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      GUIHelper.showErrorMessage(
	  this,
	  "Failed to convert options:\n" + ex);
    }
  }
}
