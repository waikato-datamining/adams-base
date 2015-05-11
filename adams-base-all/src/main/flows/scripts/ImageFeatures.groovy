/*
 * A simple Groovy feature generator that generates a histogram.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6612 $
 */


import adams.data.featureconverter.HeaderDefinition
import adams.data.image.BufferedImageContainer
import adams.data.image.BufferedImageHelper
import adams.data.image.features.AbstractScript
import adams.data.report.DataType
import adams.data.statistics.StatUtils

import javax.media.jai.JAI
import javax.media.jai.PlanarImage
import java.awt.image.BufferedImage
import java.awt.image.renderable.ParameterBlock

class ImageFeatures
        extends AbstractScript {

    /**
     * Returns a string describing the object.
     * Number of bins can be supplied via the additional option "numbins".
     *
     * @return 			a description suitable for displaying in the gui
     */
    public String globalInfo() {
        return \
         "Groovy feature generator that generates a histogram.\n" \
        + "Number of bins can be supplied via the additional option \"numbins\"."
    }

    /**
     * Creates the header from a template image.
     *
     * @param img		the image to act as a template
     * @return		the generated header
     */
    public HeaderDefinition createHeader(BufferedImageContainer img) {
        HeaderDefinition result = new HeaderDefinition()
        int numbins = getAdditionalOptions().getInteger("numbins", 256)
        for (int i = 0; i < numbins; i++) {
            result.add("histo_r_" + (i+1), DataType.NUMERIC)
            result.add("histo_g_" + (i+1), DataType.NUMERIC)
            result.add("histo_b_" + (i+1), DataType.NUMERIC)
        }

        return result
    }

    /**
     * Performs the actual feature genration.
     *
     * @param img		the image to process
     * @return		the generated features
     */
    protected List[] doGenerateRows(BufferedImageContainer img) {
        BufferedImage image = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR)
        int numbins = getAdditionalOptions().getInteger("numbins", 256)
        double[] values = new double[numbins * 3]
        int[] bins = [numbins, numbins, numbins]             // The number of bins.
        double[] low = [0.0D, 0.0D, 0.0D]        // The low value.
        double[] high = [(double) numbins, (double) numbins, (double) numbins] // The high value.

        // Create the parameter block.
        ParameterBlock pb = new ParameterBlock()
        pb.addSource(PlanarImage.wrapRenderedImage(image)) // Specify the source image
        pb.add(null)                      // No ROI
        pb.add(1)                         // xPeriod
        pb.add(1)                         // yPeriod
        pb.add(bins)
        pb.add(low)
        pb.add(high)

        // Perform the histogram operation.
        PlanarImage dst = (PlanarImage) JAI.create("histogram", pb, null)

        // Retrieve the histogram data.
        javax.media.jai.Histogram hist = dst.getProperty("histogram")
        for (int i = 0; i < numbins; i++) {
            values[i*3 + 0] = hist.getBinSize(0, i)
            if (hist.getNumBands() > 1) {
                values[i*3 + 1] = hist.getBinSize(1, i)
                values[i*3 + 2] = hist.getBinSize(2, i)
            }
        }

        result    = new List[1]
        result[0] = new ArrayList()
        result[0].addAll(Arrays.asList(StatUtils.toNumberArray(values)))

        return result
    }
}
