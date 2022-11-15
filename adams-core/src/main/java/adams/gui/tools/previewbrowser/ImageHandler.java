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
 * ImageHandler.java
 * Copyright (C) 2011-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.JAIImageReader;
import adams.gui.visualization.image.ImagePanel;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Displays the following image types: jpg,tif,tiff,bmp,gif,png,jpeg,wbmp
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-image-reader &lt;adams.data.io.input.AbstractImageReader&gt; (property: imageReader)
 * &nbsp;&nbsp;&nbsp;The image reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.JAIImageReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /** the image reader to use. */
  protected AbstractImageReader m_ImageReader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the following image types: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-reader", "imageReader",
      getDefaultImageReader());
  }

  /**
   * Returns the default reader.
   *
   * @return		the default
   */
  protected AbstractImageReader getDefaultImageReader() {
    return new JAIImageReader();
  }

  /**
   * Sets the image reader to use.
   *
   * @param value	the reader
   */
  public void setImageReader(AbstractImageReader value) {
    m_ImageReader = value;
    reset();
  }

  /**
   * Returns the image reader to use.
   *
   * @return		the reader
   */
  public AbstractImageReader getImageReader() {
    return m_ImageReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageReaderTipText() {
    return "The image reader to use.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return getImageReader().getFormatExtensions();
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    ImagePanel		panel;

    panel = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.load(file, ObjectCopyHelper.copyObject(getImageReader()), -1.0);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel previewPanel) {
    ImagePanel 	panel;

    panel  = (ImagePanel) previewPanel.getComponent();
    panel.load(file, ObjectCopyHelper.copyObject(getImageReader()), panel.getScale());

    return previewPanel;
  }
}
