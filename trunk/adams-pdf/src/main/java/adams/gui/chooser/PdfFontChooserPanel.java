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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import adams.core.io.PdfFont;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.goe.PdfFontEditor;

import com.itextpdf.text.Font;

/**
 * A font selection panel.
 * <p/>
 * Original code taken from
 * <a href="http://www.java2s.com/Code/Java/Tiny-Application/Afontselectiondialog.htm" target="_blank">here</a>.
 *
 * @author Ian Darwin -- initial code
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    author = "Ian Darwin",
    copyright = "1996-2002 Ian F. Darwin, http://www.darwinsys.com/",
    license = License.BSD2,
    url = "http://www.java2s.com/Code/Java/Tiny-Application/Afontselectiondialog.htm"
)
public class PdfFontChooserPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 4228582248866956387L;

  /** The list of font sizes. */
  public final static Integer FONT_SIZES[] = {8, 10, 11, 12, 14, 16, 18, 20, 24, 30, 36, 40, 48, 60, 72};

  /** The list of font families. */
  public final static String FONT_FAMILIES[] = {PdfFont.COURIER, PdfFont.HELVETICA, PdfFont.SYMBOL, PdfFont.TIMES_ROMAN, PdfFont.ZAPFDINGBATS};

  /** The list of font faces. */
  public final static String FONT_FACES[] = {PdfFont.ITALIC, PdfFont.BOLD, PdfFont.UNDERLINE, PdfFont.STRIKETHRU};

  /** the default font. */
  public final static String DEFAULT_FONT = PdfFont.HELVETICA;

  /** The index of the default size. */
  public final static Integer DEFAULT_SIZE = 12;

  /** The font the user has chosen. */
  protected PdfFont m_Current;

  /** The font name chooser. */
  protected JList m_ListFontName;

  /** The font size chooser. */
  protected JList m_ListFontSize;

  /** The face chooser. */
  protected JList m_ListFontFace;

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
    BaseScrollPane	scrollPane;

    super.initGUI();

    setLayout(new BorderLayout());

    panelFonts = new JPanel(new BorderLayout(5, 5));
    panelFonts.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    add(panelFonts, BorderLayout.NORTH);

    m_ListFontName = new JList(FONT_FAMILIES);
    m_ListFontName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListFontName.setSelectedIndex(0);
    m_ListFontName.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_IgnoreUpdates)
	  return;
	if (m_ListFontName.getSelectedIndex() == -1)
	  m_ListFontName.setSelectedIndex(0);
	else
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
	if (m_ListFontSize.getSelectedIndices().length == 0)
	  m_ListFontSize.setSelectedValue(DEFAULT_SIZE, true);
	else
	  previewFont();
      }
    });
    scrollPane = new BaseScrollPane(m_ListFontSize);
    scrollPane.setPreferredSize(new Dimension(50, 0));
    panelSize.add(scrollPane);

    panel2 = new JPanel(new BorderLayout());
    panel.add(panel2, BorderLayout.CENTER);

    panelAttributes = new JPanel(new BorderLayout());
    panelAttributes.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    panel2.add(panelAttributes, BorderLayout.WEST);

    m_ListFontFace = new JList(FONT_FACES);
    m_ListFontFace.setSelectedValue(DEFAULT_FONT, true);
    m_ListFontFace.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_ListFontFace.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (m_IgnoreUpdates)
	  return;
	previewFont();
      }
    });
    scrollPane = new BaseScrollPane(m_ListFontFace);
    scrollPane.setPreferredSize(new Dimension(100, 0));
    panelAttributes.add(scrollPane, BorderLayout.CENTER);

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
  protected PdfFont createFont() {
    int		family;
    int		style;
    Object[]	styles;
    int		i;

    family = PdfFont.getFontFamily((String) m_ListFontName.getSelectedValue());
    style  = Font.NORMAL;
    styles = m_ListFontFace.getSelectedValues();
    for (i = 0; i < styles.length; i++)
      style |= PdfFont.getFontFace((String) styles[i]);

    return new PdfFont(family, style, (Integer) m_ListFontSize.getSelectedValue());
  }

  /**
   * Called from the action handlers to get the font info, build a font, and
   * set it.
   */
  protected void previewFont() {
    m_Current = createFont();
    m_TextSample.setFont(m_Current.toJavaFont());
  }

  /**
   * Sets the selected font. If null is provided, the default font/size will
   * be used.
   *
   * @param value	the font, can be null
   */
  public void setCurrent(PdfFont value) {
    String[]	styles;
    int[]	indices;
    int		i;

    m_IgnoreUpdates = true;

    if (value == null)
      value = new PdfFont();

    m_ListFontName.setSelectedValue(value.getFontFamilyName(), true);
    if (m_ListFontName.getSelectedIndex() == -1)
      m_ListFontName.setSelectedValue(DEFAULT_FONT, true);

    if ((value.getFontFace() != Font.NORMAL) && (value.getFontFace() != Font.UNDEFINED)) {
      styles  = value.getFontFaces();
      indices = new int[styles.length];
      for (i = 0; i < styles.length; i++)
	indices[i] = Arrays.binarySearch(FONT_FACES, styles[i]);
    }
    else {
      indices = new int[0];
    }
    m_ListFontFace.setSelectedIndices(indices);

    m_ListFontSize.setSelectedValue((int) value.getSize(), true);
    if (m_ListFontSize.getSelectedIndex() == -1)
      m_ListFontSize.setSelectedValue(DEFAULT_SIZE, true);

    m_IgnoreUpdates = false;

    previewFont();
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
    final BaseFrame frame = new BaseFrame("PDF FontChooser Startup");
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
