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
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import adams.core.io.PdfFont;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.dialog.DialogWithButtons;
import adams.gui.goe.PdfFontEditor;

/**
 * A PDF font selection dialog.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PdfFontChooser
  extends DialogWithButtons {

  /** for serialization. */
  private static final long serialVersionUID = 4228582248866956387L;

  /** the panel for selecting the font. */
  protected PdfFontChooserPanel m_FontPanel;

  /** the OK button. */
  protected JButton m_ButtonOK;

  /** the Cancel button. */
  protected JButton m_ButtonCancel;

  /** The font the user has chosen. */
  protected PdfFont m_Current;

  /**
   * Construct a FontChooser.
   *
   * @param owner	the parent frame
   */
  public PdfFontChooser(Frame owner) {
    super(owner, "PDF Font Chooser", true);
  }

  /**
   * Construct a FontChooser.
   *
   * @param owner	the parent dialog
   */
  public PdfFontChooser(Dialog owner) {
    super(owner, "PDF Font Chooser", ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_FontPanel = new PdfFontChooserPanel();
    getContentPane().add(m_FontPanel, BorderLayout.CENTER);

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_PanelButtonsRight.add(m_ButtonOK);
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Current = m_FontPanel.getCurrent();
        dispose();
        setVisible(false);
      }
    });

    m_ButtonCancel = new JButton("Cancel");
    m_ButtonCancel.setMnemonic('C');
    m_PanelButtonsRight.add(m_ButtonCancel);
    m_ButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_FontPanel.setCurrent(m_Current);
        dispose();
        setVisible(false);
      }
    });
  }
  
  /**
   * finishes the initialization, by setting size/location.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    
    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Sets the selected font. If null is provided, the default font/size will
   * be used.
   *
   * @param value	the font, can be null
   */
  public void setCurrent(PdfFont value) {
    m_Current = value;
    m_FontPanel.setCurrent(m_Current);
  }

  /**
   * Retrieve the selected font, or null.
   *
   * @return		the selected font
   */
  public PdfFont getCurrent() {
    return m_Current;
  }

  /**
   * Simple main program to start it running.
   *
   * @param args		ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    final BaseFrame frame = new BaseFrame("PdfFontChooser Startup");
    final PdfFontChooser chooser = new PdfFontChooser(frame);
    chooser.setCurrent(null);
    frame.getContentPane().setLayout(new GridLayout(0, 1));

    JButton button = new JButton("Change font");
    frame.getContentPane().add(button);

    final JLabel label = new JLabel("Java is great!", JLabel.CENTER);
    frame.getContentPane().add(label);

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chooser.setVisible(true);
        PdfFont myNewFont = chooser.getCurrent();
        System.out.println("You chose " + PdfFontEditor.toString(null, myNewFont));
        label.setFont(myNewFont.toJavaFont());
        frame.pack();
        chooser.dispose();
      }
    });

    frame.setSize(150, 100);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
