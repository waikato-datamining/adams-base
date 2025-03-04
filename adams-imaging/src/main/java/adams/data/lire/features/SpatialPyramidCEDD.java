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
 * SpatialPyramidCEDD.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.lire.features;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.features.AbstractBufferedImageFeatureGenerator;
import adams.data.report.DataType;
import adams.data.statistics.StatUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates features using net.semanticmetadata.lire.imageanalysis.features.global.spatialpyramid.SPCEDD.<br>
 * For more information, see:<br>
 * Savvas A. Chatzichristofis, Yiannis S. Boutalis: CEDD: Color and Edge Directivity Descriptor. A Compact Descriptor for Image Indexing and Retrieval. In: 6th International Conference on Computer Vision Systems, 312-322, 2008.<br>
 * For more information on the LIRE project, see:<br>
 * http:&#47;&#47;www.lire-project.net&#47;
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{Chatzichristofis2008,
 *    author = {Savvas A. Chatzichristofis and Yiannis S. Boutalis},
 *    booktitle = {6th International Conference on Computer Vision Systems},
 *    editor = {A. Gasteratos, M. Vincze, and J.K. Tsotsos},
 *    pages = {312-322},
 *    publisher = {Springer},
 *    title = {CEDD: Color and Edge Directivity Descriptor. A Compact Descriptor for Image Indexing and Retrieval},
 *    year = {2008}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 * 
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9196 $
 */
public class SpatialPyramidCEDD
  extends AbstractBufferedImageFeatureGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -218939305183964139L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates features using " + net.semanticmetadata.lire.imageanalysis.features.global.spatialpyramid.SPCEDD.class.getName() + ".\n"
        + "For more information, see:\n"
        + getTechnicalInformation().toString() + "\n"
        + "For more information on the LIRE project, see:\n"
        + "http://www.lire-project.net/";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   * 
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.INPROCEEDINGS);
    result.setValue(Field.AUTHOR, "Savvas A. Chatzichristofis and Yiannis S. Boutalis");
    result.setValue(Field.TITLE, "CEDD: Color and Edge Directivity Descriptor. A Compact Descriptor for Image Indexing and Retrieval");
    result.setValue(Field.BOOKTITLE, "6th International Conference on Computer Vision Systems");
    result.setValue(Field.EDITOR, "A. Gasteratos, M. Vincze, and J.K. Tsotsos");
    result.setValue(Field.YEAR, "2008");
    result.setValue(Field.PAGES, "312-322");
    result.setValue(Field.PUBLISHER, "Springer");

    return result;
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition		result;
    BufferedImage		image;
    double[]			histo;
    int				i;
    net.semanticmetadata.lire.imageanalysis.features.global.spatialpyramid.SPCEDD	features;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.features.global.spatialpyramid.SPCEDD();
    features.extract(image);
    histo    = features.getFeatureVector();

    result   = new HeaderDefinition();
    for (i = 0; i < histo.length; i++)
      result.add("SPCEDD-" + (i+1), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    BufferedImage		image;
    double[]			histo;
    net.semanticmetadata.lire.imageanalysis.features.global.spatialpyramid.SPCEDD	features;

    image     = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features  = new net.semanticmetadata.lire.imageanalysis.features.global.spatialpyramid.SPCEDD();
    features.extract(image);
    histo     = features.getFeatureVector();
    result    = new List[1];
    result[0] = new ArrayList<>();
    result[0].addAll(Arrays.asList(StatUtils.toNumberArray(histo)));

    return result;
  }
}
