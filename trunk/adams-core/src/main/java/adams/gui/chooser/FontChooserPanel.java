/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 *
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java
 * language and environment is gratefully acknowledged.
 *
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */

package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;

/**
 * A font selection panel.
 * <p/>
 * Original code taken from
 * <a href="http://www.java2s.com/Code/Java/Tiny-Application/Afontselectiondialog.htm" target="_blank">here</a>.
 *
 * @author Ian Darwin -- original code
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    author = "Ian Darwin",
    copyright = "1996-2002 Ian F. Darwin, http://www.darwinsys.com/",
    license = License.BSD2,
    url = "http://www.java2s.com/Code/Java/Tiny-Application/Afontselectiondialog.htm"
)
public class FontChooserPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 4228582248866956387L;

  /** The list of font sizes. */
  public final static Integer FONT_SIZES[] = {8, 10, 11, 12, 14, 16, 18, 20, 24, 30, 36, 40, 48, 60, 72 };

  /** the default font. */
  public final static String DEFAULT_FONT = "Serif";

  /** The index of the default size. */
  public final static Integer DEFAULT_SIZE = 12;

  /** The font the user has chosen. */
  protected Font m_Current;

  /** The font name chooser. */
  protected JList m_ListFontName;

  /** The font name chooser model. */
  protected DefaultListModel m_ModelFontName;

  /** The font size chooser. */
  protected JList m_ListFontSize;

  /** The bold chooser. */
  protected JCheckBox m_CheckBoxBold;

  /** The italic chooser. */
  protected JCheckBox m_CheckBoxItalic;

  /** The display area. */
  protected JTextArea m_TextSample;

  /** whether to ignore updates in the GUI temporarily. */
  protected boolean m_IgnoreUpdates;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel 		panelFonts;
    JPanel		panelSize;
    JPanel 		panelAttributes;
    JPanel		panel;
    JPanel		panel2;
    JPanel		panel3;
    BaseScrollPane	scrollPane;

    super.initGUI();

    setLayout(new BorderLayout());

    panelFonts = new JPanel(new BorderLayout(5, 5));
    panelFonts.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    add(panelFonts, BorderLayout.NORTH);

    m_ModelFontName = new DefaultListModel();
    for (String name: GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
      m_ModelFontName.addElement(name);
    m_ListFontName  = new JList(m_ModelFontName);
    m_ListFontName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListFontName.setSelectedIndex(0);
    m_ListFontName.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_IgnoreUpdates)
	  return;
	previewFont();
      }
    });
    panelFonts.add(new BaseScrollPane(m_ListFontName), BorderLayout.CENTER);

    panel = new JPanel(new BorderLayout());
    panelFonts.add(panel, BorderLayout.EAST);

    panelSize = new JPanel(new BorderLayout());
    panel.add(panelSize, BorderLayout.WEST);

    m_ListFontSize = new JList(FONT_SIZES);
    m_ListFontSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListFontSize.setSelectedValue(DEFAULT_SIZE, true);
    m_ListFontSize.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_IgnoreUpdates)
	  return;
	previewFont();
      }
    });
    scrollPane = new BaseScrollPane(m_ListFontSize);
    scrollPane.setPreferredSize(new Dimension(50, 0));
    panelSize.add(scrollPane);

    panel2 = new JPanel(new BorderLayout());
    panel.add(panel2, BorderLayout.CENTER);

    m_CheckBoxBold = new JCheckBox("Bold", false);
    m_CheckBoxBold.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_IgnoreUpdates)
	  return;
	previewFont();
      }
    });
    m_CheckBoxItalic = new JCheckBox("Italic", false);
    m_CheckBoxItalic.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_IgnoreUpdates)
	  return;
	previewFont();
      }
    });
    panel3 = new JPanel(new BorderLayout());
    panel2.add(panel3, BorderLayout.WEST);
    panelAttributes = new JPanel(new GridLayout(0, 1));
    panel3.add(panelAttributes, BorderLayout.NORTH);
    panelAttributes.add(m_CheckBoxBold);
    panelAttributes.add(m_CheckBoxItalic);

    m_TextSample = new JTextArea("The quick brown fox jumps over the lazy dog.");
    m_TextSample.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    m_TextSample.setColumns(30);
    m_TextSample.setRows(5);
    m_TextSample.setLineWrap(true);
    m_TextSample.setWrapStyleWord(true);
    m_TextSample.setEditable(false);
    add(new BaseScrollPane(m_TextSample), BorderLayout.CENTER);

    previewFont(); // ensure view is up to date!
  }

  /**
   * Determines the font based on the current settings and returns it.
   *
   * @return		the generate font
   */
  protected Font createFont() {
    String 	name;
    int 	size;
    int 	attrs;

    name  = (String) m_ListFontName.getSelectedValue();
    size  = (Integer) m_ListFontSize.getSelectedValue();
    attrs = m_CheckBoxBold.isSelected() ? Font.BOLD : Font.PLAIN;
    if (m_CheckBoxItalic.isSelected())
      attrs |= Font.ITALIC;

    return new Font(name, attrs, size);
  }

  /**
   * Called from the action handlers to get the font info, build a font, and
   * set it.
   */
  protected void previewFont() {
    m_Current = createFont();
    m_TextSample.setFont(m_Current);
  }

  /**
   * Sets the selected font. If null is provided, the default font/size will
   * be used.
   *
   * @param value	the font, can be null
   * @see		#DEFAULT_FONT
   * @see		#DEFAULT_SIZE
   */
  public void setCurrent(Font value) {
    int		index;

    m_IgnoreUpdates = true;

    if (value == null)
      value = new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_SIZE);

    index = m_ModelFontName.indexOf(value.getName());
    if (index == -1)
      index = m_ModelFontName.indexOf(value.getFamily());
    if (index == -1)
      m_ListFontName.setSelectedValue(DEFAULT_FONT, true);
    else
      m_ListFontName.setSelectedIndex(index);
    m_ListFontName.ensureIndexIsVisible(m_ListFontName.getSelectedIndex());

    m_ListFontSize.setSelectedValue(value.getSize(), true);
    if (m_ListFontSize.getSelectedIndex() == -1)
      m_ListFontSize.setSelectedValue(DEFAULT_SIZE, true);

    m_CheckBoxBold.setSelected(value.isBold());
    m_CheckBoxItalic.setSelected(value.isItalic());

    m_IgnoreUpdates = false;

    previewFont();
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
