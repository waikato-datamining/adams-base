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
 * SpectralAngleMapperUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.statistics;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;

/**
 * Utility class for performing spectral angle mapping between spectra.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class SpectralAngleMapperUtils {

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public static TechnicalInformation getTechnicalInformation() {
    TechnicalInformation tech = new TechnicalInformation(Type.ARTICLE);

    tech.setValue(Field.TITLE, "The spectral image processing system (SIPS)—interactive visualization and analysis of imaging spectrometer data");
    tech.setValue(Field.AUTHOR, "Kruse, Fred A and Lefkoff, AB and Boardman, JW and Heidebrecht, KB and Shapiro, AT and Barloon, PJ and Goetz, AFH");
    tech.setValue(Field.JOURNAL, "Remote sensing of environment");
    tech.setValue(Field.VOLUME, "44");
    tech.setValue(Field.NUMBER, "2-3");
    tech.setValue(Field.PAGES, "145--163");
    tech.setValue(Field.YEAR, "1993");
    tech.setValue(Field.PUBLISHER, "Elsevier");

    TechnicalInformation tech2 = tech.add(Type.ARTICLE);
    tech2.setValue(Field.TITLE, "Mineralogical mapping of southern Namibia by application of continuum-removal MSAM method to the HyMap data");
    tech2.setValue(Field.AUTHOR, "Oshigami, Shoko and Yamaguchi, Yasushi and Uezato, Tatsumi and Momose, Atsushi and Arvelyna, Yessy and Kawakami, Yuu and Yajima, Taro and Miyatake, Shuichi and Nguno, Anna");
    tech2.setValue(Field.JOURNAL, "International journal of remote sensing");
    tech2.setValue(Field.VOLUME, "34");
    tech2.setValue(Field.NUMBER, "15");
    tech2.setValue(Field.PAGES, "5282--5295");
    tech2.setValue(Field.YEAR, "2013");
    tech2.setValue(Field.PUBLISHER, "Taylor & Francis");

    return tech;
  }

  /**
   * Calculates the angle between two spectra.
   *
   * @param spectrum1	The first spectrum.
   * @param spectrum2	The second spectrum.
   * @param modified	Whether to use the modified algorithm.
   * @return	The angle between the two spectra.
   */
  public static double sam(double[] spectrum1, double[] spectrum2, boolean modified) {
    // Wrap the second spectrum as a reference array of size 1
    double[][] references = new double[][] { spectrum2 };

    // Perform SAM
    double[] angles = sam(spectrum1, references, modified);

    // Return the only element of the resultant array
    return angles[0];
  }

  /**
   * Calculates the angles between a test spectrum and an array of reference
   * spectra.
   *
   * @param test	The test spectrum.
   * @param references	The reference spectra.
   * @param modified	Whether to use the modified algorithm.
   * @return	The array of angles.
   */
  public static double[] sam(double[] test, double[][] references, boolean modified) {
    // Modify the input arrays if requested
    if (modified) {
      // Modify the test array
      test = modify(test);

      // Modify all the reference arrays
      double[][] modifiedReferences = new double[references.length][];
      for(int i = 0; i < references.length; i++) {
        modifiedReferences[i] = modify(references[i]);
      }
      references = modifiedReferences;
    }

    // Initialise the sigma accumulators
    double[] dotProductSums = new double[references.length];
    double[] referenceLengthSums = new double[references.length];
    double[] testLengthSums = new double[references.length];

    // Perform the sums
    for (int sample = 0; sample < test.length; sample++) {
      // Get the amplitude of the next wave-number
      double t_i = test[sample];

      for (int reference = 0; reference < references.length; reference++) {
	// Get the amplitude of the next wave-number
	double r_i = references[reference][sample];

	// Update the sigma accumulators
	dotProductSums[reference] += t_i * r_i;
	referenceLengthSums[reference] += r_i * r_i;
	testLengthSums[reference] += t_i * t_i;
      }
    }

    // Calculate the spectral angles from the accumulated values
    double[] angles = new double[references.length];
    for (int i = 0; i < references.length; i++) {
      // Check for zero-length vectors
      if (referenceLengthSums[i] == 0.0 || testLengthSums[i] == 0.0) {
        angles[i] = Double.NaN;
        continue;
      }

      // Calculate the cosine of the angle
      double cosAngle = dotProductSums[i] /
        (Math.sqrt(referenceLengthSums[i]) * Math.sqrt(testLengthSums[i]));

      // Check for rounding errors
      if (cosAngle > 1.0) {
        cosAngle = 1.0;
      } else if (cosAngle < -1.0) {
        cosAngle = -1.0;
      }

      // Calculate the angle
      double angle = Math.acos(cosAngle);

      // Set the angle inside the return array
      angles[i] = angle;
    }

    return angles;
  }

  /**
   * Performs modification of array as in Oshigami et al. by subtracting
   * the mean of the array from each element.
   *
   * @param input The spectrum array to modify.
   * @return The modified array.
   */
  public static double[] modify(double[] input) {
    // Get the mean value
    double mean = StatUtils.mean(input);

    // Create a copy of the input array
    double[] result = new double[input.length];

    // Subtract the mean from each element
    for (int i = 0; i < result.length; i++) {
      result[i] = input[i] - mean;
    }

    return result;
  }
}
