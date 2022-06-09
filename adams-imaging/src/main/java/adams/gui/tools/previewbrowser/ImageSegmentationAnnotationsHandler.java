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
 * ImageSegmentationAnnotationsHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.BlueChannelImageSegmentationReader;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.flow.container.ImageSegmentationContainer;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderHandler;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.segmentation.SegmentationPanel;

import java.io.File;
import java.util.Arrays;

/**
 * Overlays image segmentation annotations obtained from the specified reader over the original JPG image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageSegmentationAnnotationsHandler
    extends AbstractContentHandler
    implements ColorProviderHandler {

  private static final long serialVersionUID = 3014911887476525380L;

  /** the annotation reader to use. */
  protected ImageSegmentationAnnotationReader m_Reader;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the alpha value (0=transparent, 1=opaque). */
  protected float m_Alpha;

  /** displayed labels. */
  protected BaseString[] m_DisplayedLabels;

  /** the zoom level. */
  protected double m_Zoom;

  /** whether to use best fit. */
  protected boolean m_BestFit;

  /** whether to use separate layers. */
  protected boolean m_UseSeparateLayers;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays image segmentation annotations from a PNG over the original JPG image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"reader", "reader",
	new BlueChannelImageSegmentationReader());

    m_OptionManager.add(
	"color-provider", "colorProvider",
	new DefaultColorProvider());

    m_OptionManager.add(
	"alpha", "alpha",
	0.5f, 0f, 1.0f);

    m_OptionManager.add(
	"displayed-labels", "displayedLabels",
	new BaseString[0]);

    m_OptionManager.add(
	"zoom", "zoom",
	100.0, 1.0, null);

    m_OptionManager.add(
	"best-fit", "bestFit",
	false);

    m_OptionManager.add(
	"use-separate-layers", "useSeparateLayers",
	true);
  }

  /**
   * Sets the image annotation reader to use.
   *
   * @param value	the reader
   */
  public void setReader(ImageSegmentationAnnotationReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the image annotation reader to use.
   *
   * @return		the reader
   */
  public ImageSegmentationAnnotationReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String readerTipText() {
    return "The image reader to use.";
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for coloring in the indexed image.";
  }

  /**
   * Sets the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @param value	the alpha value
   */
  public void setAlpha(float value) {
    if (getOptionManager().isValid("alpha", value)) {
      m_Alpha = value;
      reset();
    }
  }

  /**
   * Returns the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @return		the alpha value
   */
  public float getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String alphaTipText() {
    return "The alpha value to use for the overlay: 0.0=transparent, 1.0=opaque.";
  }

  /**
   * Sets the displayed labels.
   *
   * @param value	the labels
   */
  public void setDisplayedLabels(BaseString[] value) {
    m_DisplayedLabels = value;
    reset();
  }

  /**
   * Returns the displayed labels.
   *
   * @return		the labels
   */
  public BaseString[] getDisplayedLabels() {
    return m_DisplayedLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayedLabelsTipText() {
    return "The labels to display, leave empty for all.";
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
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"jpg"};
  }

  /**
   * Locates the annotation PNG file.
   *
   * @param file	the JPG file
   * @return		the annotation file or null if failed to locate
   */
  protected File locateAnnotation(File file) {
    File	result;

    result = FileUtils.replaceExtension(file, ".png");
    if (result.exists())
      return result;

    result = FileUtils.replaceExtension(file, ".PNG");
    if (result.exists())
      return result;

    return null;
  }

  /**
   * Creates the actual preview.
   *
   * @param file the file to create the view for
   * @return the preview
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    SegmentationPanel		panel;
    ImageSegmentationContainer  cont;
    String[]			labels;

    cont = m_Reader.read(new PlaceholderFile(file));

    if (m_DisplayedLabels.length == 0) {
      labels = cont.getLayers().keySet().toArray(new String[0]);
      Arrays.sort(labels);
    }
    else {
      labels = BaseString.toStringArray(m_DisplayedLabels);
    }

    panel = new SegmentationPanel();
    panel.getManager().setSplitLayers(m_UseSeparateLayers);
    panel.setToolPanelVisible(false);
    panel.setZoom(m_Zoom);
    panel.fromContainer(
	cont,
	labels,
	m_UseSeparateLayers,
	m_ColorProvider,
	m_Alpha,
	false,
	false,
	SegmentationPanel.LayerVisibility.ALL,
	null,
	this);
    if (m_BestFit)
      panel.bestFitZoom();

    return new PreviewPanel(panel, panel.getCanvasPanel());
  }

  /**
   * Reuses the last preview, if possible.
   * <br>
   * Default implementation just creates a new preview.
   *
   * @param file	the file to create the view for
   * @return		the preview
   */
  public PreviewPanel reusePreview(File file, PreviewPanel lastPreview) {
    SegmentationPanel		panel;
    ImageSegmentationContainer  cont;
    String[]			labels;

    panel = (SegmentationPanel) lastPreview.getComponent();

    // can't reuse panel when switching separate/combined layers
    if (panel.getManager().getSplitLayers() != m_UseSeparateLayers)
      return createPreview(file);

    cont = m_Reader.read(new PlaceholderFile(file));

    if (m_DisplayedLabels.length == 0) {
      labels = cont.getLayers().keySet().toArray(new String[0]);
      Arrays.sort(labels);
    }
    else {
      labels = BaseString.toStringArray(m_DisplayedLabels);
    }

    panel.fromContainer(
	cont,
	labels,
	m_UseSeparateLayers,
	m_ColorProvider,
	m_Alpha,
	false,
	false,
	SegmentationPanel.LayerVisibility.ALL,
	null,
	this);

    return lastPreview;
  }
}
