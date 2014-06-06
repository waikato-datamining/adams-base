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
 * ImageViewer.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.core.ShallowCopySupporter;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.NullOverlay;
import adams.gui.visualization.image.selection.AbstractSelectionProcessor;
import adams.gui.visualization.image.selection.NullProcessor;

/**
 <!-- globalinfo-start -->
 * Actor for displaying an image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageViewer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: -1
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
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * &nbsp;&nbsp;&nbsp;maximum: 1600.0
 * </pre>
 * 
 * <pre>-background-color &lt;java.awt.Color&gt; (property: backgroundColor)
 * &nbsp;&nbsp;&nbsp;The background color to use.
 * &nbsp;&nbsp;&nbsp;default: #eeeeee
 * </pre>
 * 
 * <pre>-show-properties &lt;boolean&gt; (property: showProperties)
 * &nbsp;&nbsp;&nbsp;If enabled then the image properties get displayed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-properties-width &lt;int&gt; (property: propertiesWidth)
 * &nbsp;&nbsp;&nbsp;The width of the properties, if displayed.
 * &nbsp;&nbsp;&nbsp;default: 150
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-selection-processor &lt;adams.gui.visualization.image.selection.AbstractSelectionProcessor&gt; (property: selectionProcessor)
 * &nbsp;&nbsp;&nbsp;The selection processor to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.selection.NullProcessor
 * </pre>
 * 
 * <pre>-selection-box-color &lt;java.awt.Color&gt; (property: selectionBoxColor)
 * &nbsp;&nbsp;&nbsp;The color of the selection box.
 * &nbsp;&nbsp;&nbsp;default: #808080
 * </pre>
 * 
 * <pre>-image-overlay &lt;adams.gui.visualization.image.ImageOverlay&gt; (property: imageOverlay)
 * &nbsp;&nbsp;&nbsp;The image overlay to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.NullOverlay
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageViewer
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1523870513962160664L;

  /**
   * Custom {@link DisplayPanel}.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ImageViewerDisplayPanel
    extends AbstractComponentDisplayPanel
    implements UpdateableDisplayPanel {

    /** blah. */
    private static final long serialVersionUID = -3054275069984068238L;

    /** the owner. */
    protected ImageViewer m_Owner;
    
    /** for displaying the image. */
    protected ImagePanel m_ImagePanel;

    /**
     * Initializes the panel.
     *
     * @param owner	the owning actor
     */
    public ImageViewerDisplayPanel(ImageViewer owner) {
      super(owner.getClass().getName());
      m_Owner = owner;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();
      setLayout(new BorderLayout());
      m_ImagePanel = new ImagePanel();
      m_ImagePanel.setShowLog(false);
      add(m_ImagePanel, BorderLayout.CENTER);
    }

    /**
     * Displays the token.
     * 
     * @param token	the token to display
     */
    @Override
    public void display(Token token) {
      if (token.getPayload() instanceof String)
	m_ImagePanel.load(new PlaceholderFile((String) token.getPayload()));
      else if (token.getPayload() instanceof File)
	m_ImagePanel.load((File) token.getPayload());
      else if (token.getPayload() instanceof BufferedImage)
	m_ImagePanel.setCurrentImage((BufferedImage) token.getPayload());
      else if (token.getPayload() instanceof AbstractImage)
	m_ImagePanel.setCurrentImage((AbstractImage) token.getPayload());
      m_ImagePanel.setShowProperties(m_Owner.getShowProperties());
      if (m_Owner.getShowProperties())
	m_ImagePanel.getSplitPane().getRightComponent().setMinimumSize(new Dimension(m_Owner.getPropertiesWidth(), 0));
      m_ImagePanel.setBackgroundColor(m_Owner.getBackgroundColor());
      if (!(m_Owner.getSelectionProcessor() instanceof NullProcessor)) {
	m_ImagePanel.addSelectionListener(m_Owner.getSelectionProcessor().shallowCopy(true));
	m_ImagePanel.setSelectionEnabled(true);
	m_ImagePanel.setSelectionBoxColor(m_Owner.getSelectionBoxColor());
      }
      if (!(m_Owner.getImageOverlay() instanceof NullOverlay)) {
	if (m_Owner.getImageOverlay() instanceof ShallowCopySupporter)
	  m_ImagePanel.addImageOverlay((ImageOverlay) ((ShallowCopySupporter) m_Owner.getImageOverlay()).shallowCopy(true));
	else
	  m_ImagePanel.addImageOverlay(m_Owner.getImageOverlay());
      }
      if (m_Owner.getZoom() == -1)
	m_ImagePanel.setScale(m_Owner.getZoom());
      else
	m_ImagePanel.setScale(m_Owner.getZoom() / 100.0);
    }
    
    /**
     * Performs clean up operations.
     */
    @Override
    public void cleanUp() {
    }
    
    /**
     * Clears the panel.
     */
    @Override
    public void clearPanel() {
      m_ImagePanel.clear();
    }
    
    /**
     * Returns the image panel.
     * 
     * @return		the panel
     */
    @Override
    public JComponent supplyComponent() {
      return m_ImagePanel;
    }

    /**
     * Generates a new token.
     * 
     * @return		the token, null if not availabel
     */
    @Override
    public Token getUpdatedToken() {
      Report			report;
      BufferedImageContainer	cont;
      
      if (m_ImagePanel.getCurrentImage() == null)
	return null;
      
      report = m_ImagePanel.getImageProperties().getClone();
      if (m_ImagePanel.getAdditionalProperties() != null)
	report.mergeWith(m_ImagePanel.getAdditionalProperties().getClone());
      
      cont = new BufferedImageContainer();
      cont.setImage(m_ImagePanel.getCurrentImage());
      cont.setReport(report);
      
      return new Token(cont);
    }
  }
  
  /** the panel with the image. */
  protected ImagePanel m_ImagePanel;

  /** the zoom level. */
  protected double m_Zoom;

  /** the background color. */
  protected Color m_BackgroundColor;

  /** whether to display the image properties. */
  protected boolean m_ShowProperties;

  /** the width of the image properties. */
  protected int m_PropertiesWidth;

  /** the selection processor to apply. */
  protected AbstractSelectionProcessor m_SelectionProcessor;
  
  /** the color for the selection box. */
  protected Color m_SelectionBoxColor;

  /** the image overlay to use. */
  protected ImageOverlay m_ImageOverlay;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying an image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "zoom", "zoom",
	    100.0, -1.0, 1600.0);

    m_OptionManager.add(
	    "background-color", "backgroundColor",
	    getDefaultBackgroundColor());

    m_OptionManager.add(
	    "show-properties", "showProperties",
	    false);

    m_OptionManager.add(
	    "properties-width", "propertiesWidth",
	    150, 1, null);

    m_OptionManager.add(
	    "selection-processor", "selectionProcessor",
	    new NullProcessor());

    m_OptionManager.add(
	    "selection-box-color", "selectionBoxColor",
	    Color.GRAY);

    m_OptionManager.add(
	    "image-overlay", "imageOverlay",
	    new NullOverlay());
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 640;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 480;
  }

  /**
   * Returns the default background color.
   *
   * @return		the default color
   */
  protected Color getDefaultBackgroundColor() {
    return new JPanel().getBackground();
  }

  /**
   * Sets the zoom level in percent (0-1600).
   *
   * @param value 	the zoom, -1 to fit window, or 0-1600
   */
  public void setZoom(double value) {
    if ((value == -1) || ((value > 0) && (value <= 1600))) {
      m_Zoom = value;
      reset();
    }
    else {
      getLogger().warning("Zoom must -1 to fit window or 0 < x < 1600, provided: " + value);
    }
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
   * Sets the background color.
   *
   * @param value 	the color
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    reset();
  }

  /**
   * Returns the background color.
   *
   * @return 		the color
   */
  public Color getBackgroundColor() {
    return m_BackgroundColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundColorTipText() {
    return "The background color to use.";
  }

  /**
   * Sets whether to display the image properties.
   *
   * @param value 	if true then the properties are displayed
   */
  public void setShowProperties(boolean value) {
    m_ShowProperties = value;
    reset();
  }

  /**
   * Returns whether to display the image properties.
   *
   * @return 		true if the properties are displayed
   */
  public boolean getShowProperties() {
    return m_ShowProperties;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showPropertiesTipText() {
    return "If enabled then the image properties get displayed.";
  }

  /**
   * Sets the width of the properties.
   *
   * @param value 	the width of the properties
   */
  public void setPropertiesWidth(int value) {
    m_PropertiesWidth = value;
    reset();
  }

  /**
   * Returns the width of the properties.
   *
   * @return 		the width of the properties
   */
  public int getPropertiesWidth() {
    return m_PropertiesWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propertiesWidthTipText() {
    return "The width of the properties, if displayed.";
  }

  /**
   * Sets the selection processor to use.
   *
   * @param value 	the processor
   */
  public void setSelectionProcessor(AbstractSelectionProcessor value) {
    m_SelectionProcessor = value;
    reset();
  }

  /**
   * Returns the selection processor in use.
   *
   * @return 		the processor
   */
  public AbstractSelectionProcessor getSelectionProcessor() {
    return m_SelectionProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String selectionProcessorTipText() {
    return "The selection processor to use.";
  }

  /**
   * Sets the color for the selection box.
   *
   * @param value 	the color
   */
  public void setSelectionBoxColor(Color value) {
    m_SelectionBoxColor = value;
    reset();
  }

  /**
   * Returns the color of the selection box.
   *
   * @return 		the color
   */
  public Color getSelectionBoxColor() {
    return m_SelectionBoxColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String selectionBoxColorTipText() {
    return "The color of the selection box.";
  }

  /**
   * Sets the image overlay to use.
   *
   * @param value 	the image overlay
   */
  public void setImageOverlay(ImageOverlay value) {
    m_ImageOverlay = value;
    reset();
  }

  /**
   * Returns the image overlay in use.
   *
   * @return 		the image overlay
   */
  public ImageOverlay getImageOverlay() {
    return m_ImageOverlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageOverlayTipText() {
    return "The image overlay to use.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_ImagePanel != null)
      m_ImagePanel.clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_ImagePanel = new ImagePanel();
    m_ImagePanel.setShowProperties(m_ShowProperties);
    m_ImagePanel.setShowLog(false);
    if (m_ShowProperties)
      m_ImagePanel.getSplitPane().getRightComponent().setMinimumSize(new Dimension(m_PropertiesWidth, 0));
    return m_ImagePanel;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class, java.awt.image.BufferedImage.class, adams.data.image.AbstractImage.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, BufferedImage.class, AbstractImage.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    if (token.getPayload() instanceof String)
      m_ImagePanel.load(new PlaceholderFile((String) token.getPayload()));
    else if (token.getPayload() instanceof File)
      m_ImagePanel.load((File) token.getPayload());
    else if (token.getPayload() instanceof BufferedImage)
      m_ImagePanel.setCurrentImage((BufferedImage) token.getPayload());
    else if (token.getPayload() instanceof AbstractImage)
      m_ImagePanel.setCurrentImage((AbstractImage) token.getPayload());
    m_ImagePanel.setScale(m_Zoom / 100.0);
    m_ImagePanel.setBackgroundColor(m_BackgroundColor);
    if (!(m_SelectionProcessor instanceof NullProcessor)) {
      m_ImagePanel.addSelectionListener(m_SelectionProcessor.shallowCopy(true));
      m_ImagePanel.setSelectionEnabled(true);
      m_ImagePanel.setSelectionBoxColor(m_SelectionBoxColor);
    }
    if (!(m_ImageOverlay instanceof NullOverlay)) {
      if (m_ImageOverlay instanceof ShallowCopySupporter)
	m_ImagePanel.addImageOverlay((ImageOverlay) ((ShallowCopySupporter) m_ImageOverlay).shallowCopy(true));
      else
	m_ImagePanel.addImageOverlay(m_ImageOverlay);
    }
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_ImagePanel != null)
      m_ImagePanel.clear();
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    DisplayPanel	result;

    result = new ImageViewerDisplayPanel(this);
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
}
