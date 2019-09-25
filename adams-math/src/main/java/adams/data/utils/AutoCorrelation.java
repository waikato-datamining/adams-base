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

/**
 * AutoCorrelation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 * Copyright (C) 2012 Gene on Stackoverflow
 */

package adams.data.utils;

import adams.core.License;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.annotation.MixedCopyright;
import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Contains methods related to autocorrelation.
 * See <a href="https://en.wikipedia.org/wiki/Autocorrelation" target="_blank">WikiPedia</a>.
 *
 * @author Gene (Stackoverflow)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Gene - http://stackoverflow.com/users/1161878/gene",
  license = License.CC_BY_SA_3,
  url = "http://stackoverflow.com/a/12453487/4698227"
)
public class AutoCorrelation {

  /**
   * This is a "wrapped" signal processing-style autocorrelation.
   * For "true" autocorrelation, the data must be zero padded.
   *
   * @param x		the sigal, must be even length
   * @return		the autocorrelation
   */
  public static double[] bruteForce(double[] x) {
    double[] 	result;
    int 	n;
    int		j;
    int		i;

    if (x.length % 2 != 0)
      throw new IllegalStateException("Signal array must be of even length, provided: " + x.length);

    n      = x.length;
    result = new double[n];
    for (j = 0; j < n; j++) {
      for (i = 0; i < n; i++)
	result[j] += x[i] * x[(n + i - j) % n];
    }

    return result;
  }

  /**
   * Helper method for calculating square.
   *
   * @param x		the number to square
   * @return		the result of x*x
   */
  private static double sqr(double x) {
    return x * x;
  }

  /**
   * Uses FFT to perform autocorrelation.
   *
   * @param x		the sigal, must be even length
   * @param normalize	whether to normalize the output
   * @return		the autocorrelation
   */
  public static double[] fft(double[] x, boolean normalize) {
    double[] 		result;
    int 		n;
    int			i;
    DoubleFFT_1D 	fft;
    DoubleFFT_1D 	ifft;

    if (x.length % 2 != 0)
      throw new IllegalStateException("Signal array must be of even length, provided: " + x.length);

    n      = x.length;
    result = new double[n];

    fft = new DoubleFFT_1D(n);
    fft.realForward(x);
    result[0] = sqr(x[0]);
    if (normalize)
      result[0] = 0;  // For statistical convention, zero out the mean
    result[1] = sqr(x[1]);
    for (i = 2; i < n; i += 2) {
      result[i] = sqr(x[i]) + sqr(x[i+1]);
      result[i+1] = 0;
    }

    ifft = new DoubleFFT_1D(n);
    ifft.realInverse(result, true);
    // For statistical convention, normalize by dividing through with variance
    if (normalize) {
      for (i = 1; i < n; i++)
          result[i] /= result[0];
      result[0] = 1;
    }

    return result;
  }

  /**
   * Returns technical information on autocorrelation.
   *
   * @return		the technical information
   */
  public static TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Autocorrelation");
    result.setValue(Field.HTTP, "https://en.wikipedia.org/wiki/Autocorrelation");

    return result;
  }
}
