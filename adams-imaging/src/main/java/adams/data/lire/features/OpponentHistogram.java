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
 * OpponentHistogram.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
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
 * Generates features using net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram.<br>
 * For more information, see:<br>
 * van de Sande, K.E.A., Gevers, T., Snoek, C.G.M. (2010). Evaluating Color Descriptors for Object and Scene Recognition. Pattern Analysis and Machine Intelligence, IEEE Transactions on. 32(9):1582-1596.<br>
 * For more information on the LIRE project, see:<br>
 * http:&#47;&#47;www.lire-project.net&#47;
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{vandeSande2010,
 *    author = {van de Sande, K.E.A. and Gevers, T. and Snoek, C.G.M.},
 *    journal = {Pattern Analysis and Machine Intelligence, IEEE Transactions on},
 *    month = {September},
 *    number = {9},
 *    pages = {1582-1596},
 *    title = {Evaluating Color Descriptors for Object and Scene Recognition},
 *    volume = {32},
 *    year = {2010},
 *    ISSN = {0162-8828},
 *    HTTP = {http:&#47;&#47;doi.ieeecomputersociety.org&#47;10.1109&#47;TPAMI.2009.154}
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
public class OpponentHistogram
  extends AbstractBufferedImageFeatureGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5934074718170214988L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates features using " + net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram.class.getName() + ".\n"
        + "For more information, see:\n"
        + getTechnicalInformation().toString() + "\n"
        + "For more information on the LIRE project, see:\n"
        + "http://www.lire-project.net/";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "van de Sande, K.E.A. and Gevers, T. and Snoek, C.G.M.");
    result.setValue(Field.TITLE, "Evaluating Color Descriptors for Object and Scene Recognition");
    result.setValue(Field.JOURNAL, "Pattern Analysis and Machine Intelligence, IEEE Transactions on");
    result.setValue(Field.YEAR, "2010");
    result.setValue(Field.MONTH, "September");
    result.setValue(Field.VOLUME, "32");
    result.setValue(Field.NUMBER, "9");
    result.setValue(Field.PAGES, "1582-1596");
    result.setValue(Field.ISSN, "0162-8828");
    result.setValue(Field.HTTP, "http://doi.ieeecomputersociety.org/10.1109/TPAMI.2009.154");

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
    net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram	features;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram();
    features.extract(image);
    histo    = features.getFeatureVector();
    result   = new HeaderDefinition();
    for (i = 0; i < histo.length; i++)
      result.add("OpponentHistogram-" + (i+1), DataType.NUMERIC);

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
    net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram	features;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.features.global.OpponentHistogram();
    features.extract(image);
    histo     = features.getFeatureVector();
    result    = new List[1];
    result[0] = new ArrayList<>();
    result[0].addAll(Arrays.asList(StatUtils.toNumberArray(histo)));

    return result;
  }
}
