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
 * PDFViewer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.io.JPod;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.pdf.PDFPanel;
import de.intarsys.pdf.pd.PDDocument;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Actor for displaying PDF files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: PDFViewer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 *
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFViewer
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1523870513962160664L;

  /** the panel with the PDF. */
  protected PDFPanel m_PDFPanel;

  /** the zoom level. */
  protected double m_Zoom;

  /** the filedialog for saving the PDF file. */
  protected transient BaseFileChooser m_PDFFileChooser;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying PDF files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "zoom", "zoom",
	    100.0);
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets the zoom level in percent.
   *
   * @param value 	the zoom
   */
  public void setZoom(double value) {
    m_Zoom = value;
    reset();
  }

  /**
   * Returns the zoom level in percent.
   *
   * @return 		the zoom
   */
  public double getZoom() {
    return m_Zoom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomTipText() {
    return "The zoom level in percent.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_PDFPanel != null) {
      JPod.close(m_PDFPanel.getDocument());
      m_PDFPanel.setDocument(null);
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_PDFPanel = new PDFPanel();
    return m_PDFPanel;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns (and initializes if necessary) the file chooser for the text.
   *
   * @return		the file chooser
   */
  protected BaseFileChooser getPDFFileChooser() {
    BaseFileChooser	fileChooser;
    ExtensionFileFilter filter;

    if (m_PDFFileChooser == null) {
      fileChooser = new BaseFileChooser();
      filter = ExtensionFileFilter.getPdfFileFilter();
      fileChooser.addChoosableFileFilter(filter);
      fileChooser.setFileFilter(filter);
      fileChooser.setDefaultExtension(filter.getExtensions()[0]);
      m_PDFFileChooser = fileChooser;
    }

    return m_PDFFileChooser;
  }

  /**
   * Saves the panel as picture.
   */
  protected void saveAs() {
    int			retVal;

    retVal = getPDFFileChooser().showSaveDialog(m_Panel);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    if (!JPod.save(m_PDFPanel.getDocument(), getPDFFileChooser().getSelectedFile()))
      GUIHelper.showErrorMessage(getParentComponent(), "Failed to save PDF document to: " + getPDFFileChooser().getSelectedFile());
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    clearPanel();
    if (token.getPayload() instanceof String)
      m_PDFPanel.setDocument(JPod.load(new PlaceholderFile((String) token.getPayload())));
    else if (token.getPayload() instanceof File)
      m_PDFPanel.setDocument(JPod.load((File) token.getPayload()));
    m_PDFPanel.setScale(m_Zoom / 100.0);
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_PDFPanel != null)
      JPod.close(m_PDFPanel.getDocument());
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 7384093089760722339L;
      protected PDFPanel m_PDFPanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_PDFPanel = new PDFPanel();
	add(m_PDFPanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	if (token.getPayload() instanceof String)
	  m_PDFPanel.setDocument(JPod.load(new PlaceholderFile((String) token.getPayload())));
	else if (token.getPayload() instanceof File)
	  m_PDFPanel.setDocument(JPod.load((File) token.getPayload()));
	m_PDFPanel.setScale(m_Zoom / 100.0);
      }
      public void cleanUp() {
	JPod.close(m_PDFPanel.getDocument());
      }
      @Override
      public void clearPanel() {
	m_PDFPanel.closeDocument();
      }
      @Override
      public JComponent supplyComponent() {
	return m_PDFPanel;
      }
    };
    
    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }

  /**
   * Returns the class that the supporter generates.
   *
   * @return		the class
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, PDDocument.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(PlaceholderFile.class, cls) || SendToActionUtils.isAvailable(PDDocument.class, cls))
           && (m_PDFPanel.getDocument() != null);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (m_PDFPanel.getDocument() != null) {
	result = SendToActionUtils.nextTmpFile("pdfviewer", "pdf");
	if (!JPod.save(m_PDFPanel.getDocument(), (File) result)) {
	  getLogger().severe("Failed to save PDF to '" + result + "'!");
	  result = null;
	}
      }
    }
    else if (SendToActionUtils.isAvailable(PDDocument.class, cls)) {
      result = m_PDFPanel.getDocument();
    }

    return result;
  }
}
