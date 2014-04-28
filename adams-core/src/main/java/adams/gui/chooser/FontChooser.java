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
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.env.Environment;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseFrame;

/**
 * A font selection dialog.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FontChooser
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 4228582248866956387L;

  /** the panel for selecting the font. */
  protected FontChooserPanel m_FontPanel;

  /** the OK button. */
  protected JButton m_ButtonOK;

  /** the Cancel button. */
  protected JButton m_ButtonCancel;

  /** The font the user has chosen. */
  protected Font m_Current;

  /**
   * Construct a FontChooser.
   *
   * @param owner	the parent frame
   */
  public FontChooser(Frame owner) {
    super(owner, "Font Chooser", true);
  }

  /**
   * Construct a FontChooser.
   *
   * @param owner	the parent dialog
   */
  public FontChooser(Dialog owner) {
    super(owner, "Font Chooser", ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel 	panelButtons;

    super.initGUI();

    getContentPane().setLayout(new BorderLayout());

    m_FontPanel = new FontChooserPanel();
    getContentPane().add(m_FontPanel, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    panelButtons.add(m_ButtonOK);
    m_ButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Current = m_FontPanel.getCurrent();
        dispose();
        setVisible(false);
      }
    });

    m_ButtonCancel = new JButton("Cancel");
    m_ButtonCancel.setMnemonic('C');
    panelButtons.add(m_ButtonCancel);
    m_ButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_FontPanel.setCurrent(m_Current);
        dispose();
        setVisible(false);
      }
    });

    getContentPane().add(panelButtons, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Sets the selected font. If null is provided, the default font/size will
   * be used.
   *
   * @param value	the font, can be null
   */
  public void setCurrent(Font value) {
    m_Current = value;
    m_FontPanel.setCurrent(m_Current);
  }

  /**
   * Retrieve the selected font, or null.
   *
   * @return		the selected font
   */
  public Font getCurrent() {
    return m_Current;
  }

  /**
   * Simple main program to start it running.
   *
   * @param args		ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    final BaseFrame frame = new BaseFrame("FontChooser Startup");
    final FontChooser chooser = new FontChooser(frame);
    chooser.setCurrent(null);
    frame.getContentPane().setLayout(new GridLayout(0, 1));

    JButton button = new JButton("Change font");
    frame.getContentPane().add(button);

    final JLabel label = new JLabel("Java is great!", JLabel.CENTER);
    label.setFont(chooser.getCurrent());
    frame.getContentPane().add(label);

    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chooser.setVisible(true);
        Font myNewFont = chooser.getCurrent();
        System.out.println("You chose " + myNewFont);
        label.setFont(myNewFont);
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
