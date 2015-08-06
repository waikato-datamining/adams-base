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
 * PDFPanel.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.pdf;

import adams.core.Utils;
import adams.core.io.JPod;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import de.intarsys.pdf.pd.PDDocument;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel for displaying a PDF file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -1994121429485824895L;

  /** the zoom levels. */
  public final static int[] ZOOMS = {
    25,
    50,
    66,
    75,
    100,
    150,
    200,
    400,
    800
  };


  /** the actual display panel. */
  protected PDFCanvas m_PDFCanvas;

  /** the scrollpane around the pdf panel. */
  protected BaseScrollPane m_ScrollPane;

  /** the panel with the navigation. */
  protected BasePanel m_PanelNavigation;

  /** the edit field with the page number. */
  protected JTextField m_TextPage;

  /** the previous page button. */
  protected JButton m_ButtonPrevious;

  /** the next page button. */
  protected JButton m_ButtonNext;

  /** the PDF to display. */
  protected PDDocument m_Document;

  /** the total number of pages label. */
  protected JLabel m_LabelPages;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Document = null;
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    try {
      m_PDFCanvas  = new PDFCanvas();
      m_PDFCanvas.addMouseListener(new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isRightClick(e)) {
	    e.consume();
	    showPopup(e);
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
      });
      m_ScrollPane = new BaseScrollPane(m_PDFCanvas);
      add(m_ScrollPane, BorderLayout.CENTER);
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate PDFPagePanel:");
      e.printStackTrace();
    }

    m_PanelNavigation = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelNavigation, BorderLayout.SOUTH);

    m_ButtonPrevious = new JButton(GUIHelper.getIcon("arrow_left.gif"));
    m_ButtonPrevious.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	previousPage();
      }
    });
    m_PanelNavigation.add(m_ButtonPrevious);

    m_ButtonNext = new JButton(GUIHelper.getIcon("arrow_right.gif"));
    m_ButtonNext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	nextPage();
      }
    });
    m_PanelNavigation.add(m_ButtonNext);

    m_TextPage = new JTextField(5);
    m_TextPage.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  if (Utils.isInteger(m_TextPage.getText()))
	    showPage(Integer.parseInt(m_TextPage.getText()) - 1);
	}
      }
    });
    m_PanelNavigation.add(m_TextPage);
    m_LabelPages = new JLabel();
    m_PanelNavigation.add(m_LabelPages);

    updateButtons();
  }

  /**
   * Displays the previous page.
   */
  protected void previousPage() {
    if (m_Document == null)
      return;

    m_PDFCanvas.selectPreviousPage();
    m_TextPage.setText("" + (m_PDFCanvas.getPageIndex() + 1));

    updateButtons();
  }

  /**
   * Displays the next page.
   */
  protected void nextPage() {
    if (m_Document == null)
      return;

    m_PDFCanvas.selectNextPage();
    m_TextPage.setText("" + (m_PDFCanvas.getPageIndex() + 1));

    updateButtons();
  }

  /**
   * Displays the specified page.
   *
   * @param pageNo	the page to display (0-based)
   */
  protected void showPage(int pageNo) {
    if ((pageNo >= 0) && (pageNo < m_PDFCanvas.getPageCount())) {
      m_TextPage.setText("" + (pageNo + 1));
      m_PDFCanvas.selectPage(pageNo);
    }

    updateButtons();
  }

  /**
   * Sets the document to display.
   *
   * @param value	the PDF document to display
   */
  public void setDocument(PDDocument value) {
    try {
      if (m_Document != null)
	m_Document.close();
    }
    catch (Exception e) {
      System.err.println("Failed to close PDF document:");
      e.printStackTrace();
    }
    m_Document = value;
    if (m_Document != null) {
      m_PDFCanvas.setDoc(m_Document);
      m_LabelPages.setText(" of " + m_PDFCanvas.getPageCount());
      showPage(0);
    }
    else {
      m_LabelPages.setText("");
      m_TextPage.setText("");
      updateButtons();
    }
  }

  /**
   * Returns the currently displayed document.
   *
   * @return		the PDF document, can be null if none set yet
   */
  public PDDocument getDocument() {
    return m_Document;
  }

  /**
   * Closes the document.
   */
  public void closeDocument() {
    JPod.close(m_Document);
  }

  /**
   * Sets the scaling factor (1 = default).
   *
   * @param value	the scaling factor
   */
  public void setScale(double value) {
    m_PDFCanvas.setScale(value);
  }

  /**
   * Returns the scaling factor (1 = default).
   *
   * @return		the scaling factor
   */
  public double getScale() {
    return m_PDFCanvas.getScale();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    boolean	loaded;

    loaded = (m_Document != null) && (m_PDFCanvas.getPageCount() > 0);
    m_ButtonPrevious.setEnabled(loaded && (m_PDFCanvas.getPageIndex() > 0));
    m_ButtonNext.setEnabled(loaded && (m_PDFCanvas.getPageIndex() < m_PDFCanvas.getPageCount() - 1));
  }

  /**
   * Displays popup menu.
   *
   * @param e		the event that triggered the popup
   */
  protected void showPopup(MouseEvent e) {
    BasePopupMenu 	menu;
    JMenuItem		menuitem;
    int			i;

    menu = new BasePopupMenu();

    //View/Zoom/Zoom in
    menuitem = new JMenuItem("Zoom in");
    menu.add(menuitem);
    menuitem.setMnemonic('i');
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setScale(getScale() * 1.5);
      }
    });

    //View/Zoom/Zoom out
    menuitem = new JMenuItem("Zoom out");
    menu.add(menuitem);
    menuitem.setMnemonic('o');
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setScale(getScale() / 1.5);
      }
    });

    // zoom levels
    // TODO: add "fit" zoom
    menu.addSeparator();
    for (i = 0; i < ZOOMS.length; i++) {
      final int fZoom = ZOOMS[i];
      menuitem = new JMenuItem(ZOOMS[i] + "%");
      menu.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  setScale((double) fZoom / 100.0);
	}
      });
    }

    menu.showAbsolute(e.getComponent(), e);
  }
}
