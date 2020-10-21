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
 * ImageSegmentationAnnotator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.base.BaseString;
import adams.data.image.AbstractImageContainer;
import adams.flow.container.ImageSegmentationContainer;
import adams.flow.core.Token;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.segmentation.SegmentationPanel;
import adams.gui.visualization.segmentation.layer.OverlayLayer;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * User interface for annotating images for image segmentation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ImageSegmentationContainer<br>
 * - generates:<br>
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
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageSegmentationAnnotator
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
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
 * &nbsp;&nbsp;&nbsp;default: 1200
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
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
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * </pre>
 *
 * <pre>-allow-layer-remove &lt;boolean&gt; (property: allowLayerRemoval)
 * &nbsp;&nbsp;&nbsp;If enabled, the user can remove layers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageSegmentationAnnotator
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -761517109077084448L;

  /** the labels to use. */
  protected BaseString[] m_Labels;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the panel. */
  protected SegmentationPanel m_PanelSegmentation;

  /** the alpha value. */
  protected float m_Alpha;

  /** the zoom level. */
  protected double m_Zoom;

  /** whether layers can be deleted. */
  protected boolean m_AllowLayerRemoval;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "User interface for annotating images for image segmentation.";
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
      "zoom", "zoom",
      100.0, 1.0, null);

    m_OptionManager.add(
      "allow-layer-remove", "allowLayerRemoval",
      false);
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  @Override
  protected int getDefaultX() {
    return -2;
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  @Override
  protected int getDefaultY() {
    return -2;
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
   * Sets whether removal of layers is allowed.
   *
   * @param value 	true if allowed
   */
  public void setAllowLayerRemoval(boolean value) {
    m_AllowLayerRemoval = value;
    reset();
  }

  /**
   * Returns whether removal of layers is allowed.
   *
   * @return 		true if allowed
   */
  public boolean getAllowLayerRemoval() {
    return m_AllowLayerRemoval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowLayerRemovalTipText() {
    return "If enabled, the user can remove layers.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{BufferedImage.class, AbstractImageContainer.class, ImageSegmentationContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{ImageSegmentationContainer.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_PanelSegmentation != null) {
      m_PanelSegmentation.getManager().clear();
      m_PanelSegmentation.update();
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_PanelSegmentation = new SegmentationPanel();
    m_PanelSegmentation.setZoom(m_Zoom);
    return m_PanelSegmentation;
  }

  /**
   * Hook method after the dialog got created.
   *
   * @param dialog	the dialog that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateDialog(final BaseDialog dialog, BasePanel panel) {
    BaseButton buttonOK;
    BaseButton	buttonCancel;
    JPanel panelButtons;

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    dialog.getContentPane().add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new BaseButton("OK");
    buttonOK.addActionListener((ActionEvent e) -> {
      m_Accepted = true;
      dialog.setVisible(false);
    });
    panelButtons.add(buttonOK);

    buttonCancel = new BaseButton("Cancel");
    buttonCancel.addActionListener((ActionEvent e) -> {
      m_Accepted = false;
      dialog.setVisible(false);
    });
    panelButtons.add(buttonCancel);
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    BufferedImage		img;
    AbstractImageContainer	imgcont;
    ImageSegmentationContainer	segcont;
    Map<String,BufferedImage> 	layers;
    OverlayLayer		layer;

    m_Accepted = false;

    if (m_InputToken.hasPayload(BufferedImage.class)) {
      img     = m_InputToken.getPayload(BufferedImage.class);
      segcont = new ImageSegmentationContainer("image", img);
    }
    else if (m_InputToken.hasPayload(AbstractImageContainer.class)) {
      imgcont = m_InputToken.getPayload(AbstractImageContainer.class);
      segcont = new ImageSegmentationContainer("image", imgcont.toBufferedImage());
    }
    else {
      segcont = m_InputToken.getPayload(ImageSegmentationContainer.class);
    }

    // annotate
    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_PanelSegmentation.getManager().clear();
    m_PanelSegmentation.getManager().setImage(
      segcont.getValue(ImageSegmentationContainer.VALUE_NAME, String.class),
      segcont.getValue(ImageSegmentationContainer.VALUE_BASE, BufferedImage.class));
    layers = (Map<String,BufferedImage>) segcont.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    for (BaseString label: m_Labels) {
      if (layers != null) {
        if (layers.containsKey(label.getValue())) {
	  layer = m_PanelSegmentation.getManager().addOverlay(label.getValue(), m_ColorProvider.next(), m_Alpha, layers.get(label.getValue()));
	}
	else {
          getLogger().warning("Label '" + label + "' not present in layers, using empty layer!");
	  layer = m_PanelSegmentation.getManager().addOverlay(label.getValue(), m_ColorProvider.next(), m_Alpha);
	}
      }
      else {
        layer = m_PanelSegmentation.getManager().addOverlay(label.getValue(), m_ColorProvider.next(), m_Alpha);
      }
      layer.setRemovable(m_AllowLayerRemoval);
    }
    m_PanelSegmentation.update();
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      layers = new HashMap<>();
      for (OverlayLayer l: m_PanelSegmentation.getManager().getOverlays())
        layers.put(l.getName(), l.getIndexedImage());
      segcont = new ImageSegmentationContainer();
      segcont.setValue(ImageSegmentationContainer.VALUE_BASE, m_PanelSegmentation.getManager().getImageLayer().getImage());
      segcont.setValue(ImageSegmentationContainer.VALUE_LAYERS, layers);
      m_OutputToken = new Token(segcont);
    }

    return m_Accepted;
  }
}