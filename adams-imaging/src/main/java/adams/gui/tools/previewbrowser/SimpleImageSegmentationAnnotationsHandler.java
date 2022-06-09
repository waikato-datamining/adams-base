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
 * SimpleImageSegmentationAnnotationsHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.MessageCollection;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.transformer.GrayOrIndexedColorizer;
import adams.data.image.transformer.ImageColorizer;
import adams.data.image.transformer.ImageColorizerWithColorProvider;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.ApacheCommonsImageReader;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.segmentation.ImageUtils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Overlays image segmentation annotations from a PNG over the original JPG image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleImageSegmentationAnnotationsHandler
    extends AbstractContentHandler {

  private static final long serialVersionUID = 3014911887476525380L;

  /** the image reader to use. */
  protected AbstractImageReader m_Reader;

  /** the image reader to use for the overlay. */
  protected AbstractImageReader m_OverlayReader;

  /** the image colorizer to use. */
  protected ImageColorizer m_Colorizer;

  /** the alpha value to use for the overlay (0: transparent, 255: opaque). */
  protected int m_Alpha;

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
	new ApacheCommonsImageReader());

    m_OptionManager.add(
	"overlay-reader", "overlayReader",
	new ApacheCommonsImageReader());

    m_OptionManager.add(
	"colorizer", "colorizer",
	new GrayOrIndexedColorizer());

    m_OptionManager.add(
	"alpha", "alpha",
	128, 0, 255);
  }

  /**
   * Sets the image reader to use.
   *
   * @param value	the image reader
   */
  public void setReader(AbstractImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the image reader to use.
   *
   * @return		the image reader
   */
  public AbstractImageReader getReader() {
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
   * Sets the image reader to use for the overlay.
   *
   * @param value	the image reader
   */
  public void setOverlayReader(AbstractImageReader value) {
    m_OverlayReader = value;
    reset();
  }

  /**
   * Returns the image reader to use for the overlay.
   *
   * @return		the image reader
   */
  public AbstractImageReader getOverlayReader() {
    return m_OverlayReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overlayReaderTipText() {
    return "The image reader to use for the overlay.";
  }

  /**
   * Sets the image colorizer to use.
   *
   * @param value	the image colorizer
   */
  public void setColorizer(ImageColorizer value) {
    m_Colorizer = value;
    reset();
  }

  /**
   * Returns the image colorizer to use.
   *
   * @return		the colorizer
   */
  public ImageColorizer getColorizer() {
    return m_Colorizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorizerTipText() {
    return "The image colorizer to use.";
  }

  /**
   * Sets the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @param value	the alphae value
   */
  public void setAlpha(int value) {
    m_Alpha = value;
    reset();
  }

  /**
   * Returns the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @return		the alpha value
   */
  public int getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String alphaTipText() {
    return "The alpha value to use for the overlay: 0=transparent, 255=opaque.";
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
   * Generates the overlay image.
   *
   * @param file	the file to generate the overlay for
   * @param errors	for collecting errors
   * @return		the overlay, null in case of errors
   */
  protected BufferedImage generateOverlay(File file, MessageCollection errors) {
    BufferedImage 		result;
    File    			annotationFile;
    BufferedImageContainer 	ovl;
    BufferedImageContainer[]	colorized;
    BufferedImage 		baseImg;
    BufferedImage 		ovlImg;
    Graphics2D 			g2;
    AlphaComposite alpha;
    ImagePanel			panel;
    ColorProvider		colorProvider;
    Color			background;

    annotationFile = locateAnnotation(file);
    if (annotationFile == null) {
      errors.add("Failed to locate annotation file for:\n" + file.getAbsolutePath());
      return null;
    }

    // base image
    baseImg = m_Reader.read(new PlaceholderFile(file)).toBufferedImage();

    // colorize overlay
    ovl       = (BufferedImageContainer) m_OverlayReader.read(new PlaceholderFile(annotationFile));
    colorized = m_Colorizer.transform(ovl);
    if (colorized.length != 1) {
      errors.add("Failed to colorized image:\n" + annotationFile.getAbsolutePath());
      return null;
    }
    ovlImg = colorized[0].toBufferedImage();
    ovlImg = BufferedImageHelper.convert(ovlImg, BufferedImage.TYPE_INT_ARGB);
    // remove background color, ie first color generated by color provider
    if (m_Colorizer instanceof ImageColorizerWithColorProvider) {
      colorProvider = ((ImageColorizerWithColorProvider) m_Colorizer).getColorProvider().shallowCopy();
      background = colorProvider.next();
      ImageUtils.replaceColor(ovlImg, background, new Color(0, 0, 0, 0));
    }

    // combine images
    result = new BufferedImage(baseImg.getWidth(), baseImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
    g2     = result.createGraphics();
    g2.drawImage(baseImg, 0, 0, null);
    alpha  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) m_Alpha / 255);
    g2.setComposite(alpha);
    g2.drawImage(ovlImg, 0, 0, null);
    g2.dispose();

    return result;
  }

  /**
   * Creates the actual preview.
   *
   * @param file the file to create the view for
   * @return the preview
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    BufferedImage 	overlay;
    ImagePanel		panel;
    MessageCollection	errors;

    errors  = new MessageCollection();
    overlay = generateOverlay(file, errors);
    if (!errors.isEmpty())
      return new NoPreviewAvailablePanel(errors.toString());

    // display
    panel = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.setCurrentImage(overlay);

    return new PreviewPanel(panel, panel.getPaintPanel());
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
    BufferedImage 	overlay;
    ImagePanel		panel;
    MessageCollection	errors;

    errors  = new MessageCollection();
    overlay = generateOverlay(file, errors);
    if (!errors.isEmpty())
      return new NoPreviewAvailablePanel(errors.toString());

    // display
    panel = (ImagePanel) lastPreview.getComponent();
    panel.setCurrentImage(overlay, panel.getScale());

    return lastPreview;
  }
}
