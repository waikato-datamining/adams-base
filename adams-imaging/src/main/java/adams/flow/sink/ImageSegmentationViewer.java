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
 * ImageSegmentationViewer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.ObjectCopyHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.flow.container.ImageSegmentationContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.segmentation.SegmentationPanel;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Displays image segmentation containers.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ImageSegmentationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.ImageSegmentationContainer: name, base, layers
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageSegmentationViewer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1200
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
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
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider in use for generating the colors for the various layers.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-alpha &lt;float&gt; (property: alpha)
 * &nbsp;&nbsp;&nbsp;The alpha value to use (fully transparent=0.0, fully opaque=1.0).
 * &nbsp;&nbsp;&nbsp;default: 0.5
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-left-divider-location &lt;int&gt; (property: leftDividerLocation)
 * &nbsp;&nbsp;&nbsp;The position for the left divider in pixels.
 * &nbsp;&nbsp;&nbsp;default: 280
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * </pre>
 *
 * <pre>-best-fit &lt;boolean&gt; (property: bestFit)
 * &nbsp;&nbsp;&nbsp;If enabled, the image gets fitted into the viewport.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-separate-layers &lt;boolean&gt; (property: useSeparateLayers)
 * &nbsp;&nbsp;&nbsp;If enabled, support for multiple layers is enabled (eg for annotating objects
 * &nbsp;&nbsp;&nbsp;that do not touch&#47;overlap).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-layer-visibility &lt;ALL|NONE|PREVIOUSLY_VISIBLE&gt; (property: layerVisibility)
 * &nbsp;&nbsp;&nbsp;What layers will be visible when annotating the next image (when using separate
 * &nbsp;&nbsp;&nbsp;layers).
 * &nbsp;&nbsp;&nbsp;default: ALL
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageSegmentationViewer
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  private static final long serialVersionUID = 2969826320438810202L;

  /**
   * Custom {@link DisplayPanel}.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class ImageSegmentationViewerDisplayPanel
    extends AbstractComponentDisplayPanel
    implements UpdateableDisplayPanel {

    /** blah. */
    private static final long serialVersionUID = -3054275069984068238L;

    /** the owner. */
    protected ImageSegmentationViewer m_Owner;

    /** for displaying the image. */
    protected SegmentationPanel m_PanelSegmentation;

    /**
     * Initializes the panel.
     *
     * @param owner	the owning actor
     */
    public ImageSegmentationViewerDisplayPanel(ImageSegmentationViewer owner) {
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
      m_PanelSegmentation = new SegmentationPanel();
      m_PanelSegmentation.setToolPanelVisible(false);
      m_PanelSegmentation.setAutomaticUndoEnabled(false);
      m_PanelSegmentation.getUndo().setEnabled(false);
      add(m_PanelSegmentation, BorderLayout.CENTER);
    }

    /**
     * Displays the token.
     *
     * @param token	the token to display
     */
    @Override
    public void display(Token token) {
      ImageSegmentationContainer	segcont;

      m_PanelSegmentation.setZoom(m_Owner.getZoom());
      m_PanelSegmentation.getManager().setSplitLayers(m_Owner.getUseSeparateLayers());
      m_PanelSegmentation.setLeftDividerLocation(m_Owner.getLeftDividerLocation());

      segcont = token.getPayload(ImageSegmentationContainer.class);

      m_PanelSegmentation.fromContainer(
	segcont,
	BaseObject.toStringArray(m_Owner.getLabels()),
	m_Owner.getUseSeparateLayers(),
	ObjectCopyHelper.copyObject(m_Owner.getColorProvider()),
	m_Owner.getAlpha(),
	false,
	false,
	m_Owner.getLayerVisibility(),
	null,
	null);
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
      m_PanelSegmentation.getManager().clear();
    }

    /**
     * Returns the panel.
     *
     * @return		the panel
     */
    @Override
    public JComponent supplyComponent() {
      return m_PanelSegmentation;
    }

    /**
     * Generates a new token.
     *
     * @return		the token, null if not availabel
     */
    @Override
    public Token getUpdatedToken() {
      ImageSegmentationContainer	cont;

      cont = m_PanelSegmentation.toContainer(m_Owner.getUseSeparateLayers());

      return new Token(cont);
    }
  }

  /** the labels to use. */
  protected BaseString[] m_Labels;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the alpha value. */
  protected float m_Alpha;

  /** the position for the left divider. */
  protected int m_LeftDividerLocation;

  /** the zoom level. */
  protected double m_Zoom;

  /** whether to use best fit. */
  protected boolean m_BestFit;

  /** whether to use separate layers. */
  protected boolean m_UseSeparateLayers;

  /** what layers to have visible (when using separate layers). */
  protected SegmentationPanel.LayerVisibility m_LayerVisibility;

  /** the panel. */
  protected SegmentationPanel m_PanelSegmentation;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays image segmentation containers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "alpha", "alpha",
      0.5f, 0.0f, 1.0f);

    m_OptionManager.add(
      "left-divider-location", "leftDividerLocation",
      280, 1, null);

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, 1.0, null);

    m_OptionManager.add(
      "best-fit", "bestFit",
      false);

    m_OptionManager.add(
      "use-separate-layers", "useSeparateLayers",
      true);

    m_OptionManager.add(
      "layer-visibility", "layerVisibility",
      SegmentationPanel.LayerVisibility.ALL);
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 1200;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 800;
  }

  /**
   * Sets the labels to use.
   *
   * @param value 	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to use.
   *
   * @return 		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to use.";
  }

  /**
   * Sets the color provider to use.
   *
   * @param value 	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider in use.
   *
   * @return 		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider in use for generating the colors for the various layers.";
  }

  /**
   * Sets the alpha value to use.
   *
   * @param value 	the alpha (fully transparent=0.0, fully opaque=1.0)
   */
  public void setAlpha(float value) {
    m_Alpha = value;
    reset();
  }

  /**
   * Returns the alpha value to use.
   *
   * @return 		the alpha (fully transparent=0.0, fully opaque=1.0)
   */
  public float getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alphaTipText() {
    return "The alpha value to use (fully transparent=0.0, fully opaque=1.0).";
  }

  /**
   * Sets the position for the left divider in pixels.
   *
   * @param value 	the position
   */
  public void setLeftDividerLocation(int value) {
    if (getOptionManager().isValid("leftDividerLocation", value)) {
      m_LeftDividerLocation = value;
      reset();
    }
  }

  /**
   * Returns the position for the left divider in pixels.
   *
   * @return 		the position
   */
  public int getLeftDividerLocation() {
    return m_LeftDividerLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftDividerLocationTipText() {
    return "The position for the left divider in pixels.";
  }

  /**
   * Sets the zoom level in percent (1-inf).
   *
   * @param value 	the zoom
   */
  public void setZoom(double value) {
    if (getOptionManager().isValid("zoom", value)) {
      m_Zoom = value;
      reset();
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
   * Sets whether to use best fit for the image or not.
   *
   * @param value 	true if to use
   */
  public void setBestFit(boolean value) {
    m_BestFit = value;
    reset();
  }

  /**
   * Returns whether to use best fit for the image or not.
   *
   * @return 		true if to use
   */
  public boolean getBestFit() {
    return m_BestFit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bestFitTipText() {
    return "If enabled, the image gets fitted into the viewport.";
  }

  /**
   * Sets whether to use separate layers or just one.
   *
   * @param value 	true if to use
   */
  public void setUseSeparateLayers(boolean value) {
    m_UseSeparateLayers = value;
    reset();
  }

  /**
   * Returns whether to use separate layers or just one.
   *
   * @return 		true if to use
   */
  public boolean getUseSeparateLayers() {
    return m_UseSeparateLayers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useSeparateLayersTipText() {
    return "If enabled, support for multiple layers is enabled (eg for annotating objects that do not touch/overlap).";
  }

  /**
   * Sets the type of visibility to use when annotating the next image (when using separate layers).
   *
   * @param value 	the visibility
   */
  public void setLayerVisibility(SegmentationPanel.LayerVisibility value) {
    m_LayerVisibility = value;
    reset();
  }

  /**
   * Returns the type of visibility to use when annotating the next image (when using separate layers).
   *
   * @return 		the visibility
   */
  public SegmentationPanel.LayerVisibility getLayerVisibility() {
    return m_LayerVisibility;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerVisibilityTipText() {
    return "What layers will be visible when annotating the next image (when using separate layers).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ImageSegmentationContainer.class};
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return true;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_PanelSegmentation != null)
      m_PanelSegmentation.getManager().clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_PanelSegmentation = new SegmentationPanel();
    m_PanelSegmentation.setZoom(m_Zoom);
    m_PanelSegmentation.getManager().setSplitLayers(m_UseSeparateLayers);
    m_PanelSegmentation.setLeftDividerLocation(m_LeftDividerLocation);
    m_PanelSegmentation.setToolPanelVisible(false);
    m_PanelSegmentation.setAutomaticUndoEnabled(false);
    m_PanelSegmentation.getUndo().setEnabled(false);

    return m_PanelSegmentation;
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token the token to display
   */
  @Override
  protected void display(Token token) {
    ImageSegmentationContainer	segcont;

    segcont = token.getPayload(ImageSegmentationContainer.class);

    m_PanelSegmentation.fromContainer(
      segcont,
      BaseObject.toStringArray(m_Labels),
      m_UseSeparateLayers,
      ObjectCopyHelper.copyObject(m_ColorProvider),
      m_Alpha,
      false,
      false,
      m_LayerVisibility,
      null,
      this);
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    DisplayPanel	result;

    result = new ImageSegmentationViewerDisplayPanel(this);
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
