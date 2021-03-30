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
 * IndividualImageSegmentationLayerReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.lister.LocalDirectoryLister;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.transformer.AbstractBufferedImageTransformer;
import adams.data.image.transformer.PassThrough;
import adams.data.io.output.ImageSegmentationAnnotationWriter;
import adams.data.io.output.IndividualImageSegmentationLayerWriter;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses a JPG as base image and indexed PNG files for the individual layers (0 = background, 1 = annotation).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndividualImageSegmentationLayerReader
  extends AbstractImageSegmentationAnnotationReader {

  private static final long serialVersionUID = -7333525229208134545L;

  /** for processing the layers. */
  protected AbstractBufferedImageTransformer m_LayerTransformer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a JPG as base image and indexed PNG files for the individual layers (0 = background, 1 = annotation).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "layer-transformer", "layerTransformer",
      new PassThrough());
  }

  /**
   * Sets the image transformer to apply to the layer images.
   *
   * @param value	the image width
   */
  public void setLayerTransformer(AbstractBufferedImageTransformer value) {
    m_LayerTransformer = value;
    reset();
  }

  /**
   * Returns the image transformer to apply to the layer images.
   *
   * @return		the transformer
   */
  public AbstractBufferedImageTransformer getLayerTransformer() {
    return m_LayerTransformer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerTransformerTipText() {
    return "The image transformer to apply to the layer images.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Individual image segmentation layers";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"jpg", "jpeg"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public ImageSegmentationAnnotationWriter getCorrespondingWriter() {
    return new IndividualImageSegmentationLayerWriter();
  }

  /**
   * Reads the image segmentation annotations.
   *
   * @param file	the file to read from
   * @return		the annotations
   */
  @Override
  protected ImageSegmentationContainer doRead(PlaceholderFile file) {
    ImageSegmentationContainer	result;
    BufferedImage 		base;
    String			name;
    String			layer;
    Map<String,BufferedImage>	layers;
    LocalDirectoryLister	lister;
    String[]			files;
    PlaceholderFile		phFile;
    PNGImageReader		reader;
    BufferedImageContainer 	cont;
    BufferedImageContainer[]	conts;

    base   = BufferedImageHelper.read(file).toBufferedImage();
    name   = FileUtils.replaceExtension(file.getName(), "");
    layers = new HashMap<>();
    result = new ImageSegmentationContainer(name, base, layers);
    lister = new LocalDirectoryLister();
    lister.setListFiles(true);
    lister.setListDirs(false);
    lister.setRecursive(false);
    lister.setWatchDir(file.getParentFile().getAbsolutePath());
    lister.setRegExp(new BaseRegExp(name + "-.*\\.(png|PNG)"));
    files = lister.list();
    if (isLoggingEnabled())
      getLogger().info("Found # layer PNG files: " + files.length);
    if (files.length > 0) {
      reader = new PNGImageReader();
      for (String f : files) {
        if (isLoggingEnabled())
          getLogger().info("Reading: " + f);
        phFile = new PlaceholderFile(f);
        cont   = reader.read(phFile);
        if (cont == null) {
	  getLogger().severe("Failed to read: " + f);
	  continue;
	}
	layer = FileUtils.replaceExtension(phFile.getName(), "").replaceAll(".*-", "");
        if (isLoggingEnabled())
          getLogger().info("Layer name: " + layer);
        if (!(m_LayerTransformer instanceof PassThrough)) {
          conts = m_LayerTransformer.transform(cont);
          if (conts.length != 1)
            getLogger().warning("Image transformer did not generate just one image, but " + conts.length + " (" + file + ")");
          if (conts.length > 0)
            cont = conts[0];
          else
            getLogger().warning("Image transformer did not generate any output, falling back to original data (" + file + ")!");
	}
	layers.put(layer, cont.toBufferedImage());
      }
    }

    return result;
  }
}
