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
 * PDFPanel.java
 * Copyright (C) 2011-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.pdf;

import adams.core.CleanUpHandler;
import adams.core.io.IcePDF;
import adams.gui.core.BasePanel;
import org.icepdf.core.pobjects.Document;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.FontPropertiesManager;
import org.icepdf.ri.util.ViewerPropertiesManager;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Panel for displaying a PDF file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PDFPanel
    extends BasePanel
    implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1994121429485824895L;

  /** the component controller. */
  protected SwingController m_Controller;

  /** the viewer itself. */
  protected JPanel m_ViewerComponentPanel;

  /** whether a document is present. */
  protected boolean m_HasDocument;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_HasDocument = false;
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    ViewerPropertiesManager 	properties;
    SwingViewBuilder 		factory;

    super.initGUI();

    setLayout(new BorderLayout());

    // build a component controller
    m_Controller = new SwingController();
    m_Controller.setIsEmbeddedComponent(true);

    // read stored system font properties.
    FontPropertiesManager.getInstance().loadOrReadSystemFonts();

    properties = ViewerPropertiesManager.getInstance();
    properties.getPreferences().putFloat(ViewerPropertiesManager.PROPERTY_DEFAULT_ZOOM_LEVEL, 1.25f);

    factory = new SwingViewBuilder(m_Controller, properties);

    // add interactive mouse link annotation support via callback
    m_Controller.getDocumentViewController().setAnnotationCallback(
	new org.icepdf.ri.common.MyAnnotationCallback(m_Controller.getDocumentViewController()));
    m_ViewerComponentPanel = factory.buildViewerPanel();

    add(m_ViewerComponentPanel, BorderLayout.CENTER);
  }

  /**
   * Sets the document to display.
   *
   * @param filename	the PDF document to display
   */
  public void setDocument(String filename) {
    m_Controller.closeDocument();
    m_Controller.openDocument(filename);
    m_HasDocument = true;
  }

  /**
   * Sets the document to display.
   *
   * @param file	the PDF document to display
   */
  public void setDocument(File file) {
    setDocument(file.getAbsolutePath());
  }

  /**
   * Returns the currently displayed document.
   *
   * @return		the PDF document, can be null if none set yet
   */
  public Document getDocument() {
    return m_Controller.getDocument();
  }

  /**
   * Closes the document.
   */
  public void closeDocument() {
    m_HasDocument = false;
    m_Controller.closeDocument();
  }

  /**
   * Sets the scaling factor (1 = default).
   *
   * @param value	the scaling factor
   */
  public void setScale(double value) {
    m_Controller.setZoom((float) value);
  }

  /**
   * Returns the scaling factor (1 = default).
   *
   * @return		the scaling factor
   */
  public double getScale() {
    return m_Controller.getDocumentViewController().getZoom();
  }

  /**
   * Whether a document is present.
   *
   * @return		true if document present
   */
  public boolean hasDocument() {
    return m_HasDocument;
  }

  /**
   * Saves the document to the specified file.
   *
   * @param file	the output file
   * @return		null if successful, otherwise error message
   */
  public String saveTo(File file) {
    if (!m_HasDocument)
      return null;

    return IcePDF.saveTo(getDocument(), file);
  }

  /**
   * Prints the PDF.
   *
   * @param withDialog 	whether to show the print dialog
   */
  public void print(boolean withDialog) {
    m_Controller.print(withDialog);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    closeDocument();
  }
}
