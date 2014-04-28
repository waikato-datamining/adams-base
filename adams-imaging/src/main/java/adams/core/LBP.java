/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * LBP.java - Helper class for LBPModel
 * Copyright (C) 2011 Florian Brucker, http://www.florianbrucker.de
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.Serializable;
import java.util.Arrays;

import adams.core.annotation.MixedCopyright;
import adams.data.image.AbstractImage;

/**
 * Class for calculating locally binary patterns, default or uniform.
 * 
 * @author Florian Brucker
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2011 Florian Brucker, http://www.florianbrucker.de",
    license = License.GPL3,
    url = "http://www.florianbrucker.de/content/lbp/lbp.zip",
    note = "Original classname: de.florianbrucker.ml.lbp.LBPSubModel"
)
public class LBP
  implements Serializable {
  
  /** for serialization. */
  private static final long serialVersionUID = 7341236906408210443L;

  /** Radius */
  protected int r;

  /** Number of neighbors */
  protected int p;

  /** Flag showing whether this model uses local variance data */
  protected int b;

  /** Normalized pattern histogram */
  protected float patternHist[] = null;

  /** Normalized variance histogram */
  protected float varHist[] = null;	

  /** Number of images incorporated into this model */
  protected int imageCount = 0;

  /**
   * Creates a Locally Binary Pattern model with no variance and radius 1.
   * 
   * @param p Number of neighbors
   * @param r Radius
   * @param b Number of bins for variance histogram
   */
  public LBP(int p) {
    this(p, 1);
  }

  /**
   * Creates a Locally Binary Pattern model with no variance.
   * 
   * @param p Number of neighbors
   * @param r Radius
   */
  public LBP(int p, int r) {
    this(p, r, 0);
  }

  /**
   * Creates a Locally Binary Pattern model.
   * <p>
   * If you do not want to use local variance data, set the number of bins to 0.
   * 
   * @param p Number of neighbors
   * @param r Radius
   * @param b Number of bins for variance histogram
   */
  public LBP(int p, int r, int b) {
    this.p = p;
    this.r = r;
    this.b = b;
  }

  /**
   * Updates the model by incorporating information from an image.
   * 
   * @param image	the image to incorporate
   * @param uniform	if true, rotation invariant, and uniform calculation is used
   */
  public void incorporate(AbstractImage image, boolean uniform) {
    incorporate(image.toBufferedImage().getData(), uniform);
  }

  /**
   * Updates the model by incorporating information from an image.
   * 
   * @param image	the image to incorporate
   * @param uniform	if true, rotation invariant, and uniform calculation is used
   */
  public void incorporate(BufferedImage image, boolean uniform) {
    incorporate(image.getData(), uniform);
  }

  /**
   * Updates the model by incorporating information from an image.
   * 
   * @param raster 	Raster
   * @param uniform	if true, rotation invariant, and uniform calculation is used
   */
  public void incorporate(Raster raster, boolean uniform) {
    if (uniform)
      incorporateUniform(raster);
    else
      incorporateSimple(raster);
  }

  /**
   * Updates the model by incorporating information from an image, using 
   * rotation invariant, and uniform calculation.
   * 
   * @param raster Raster
   */
  protected void incorporateUniform(Raster raster) {

    // Initialize variables
    int width = raster.getWidth();
    int height = raster.getHeight();

    float[] vars = null;

    if (b > 0) {
      int numPixels = (width - 2 * r) * (height - 2 * r);
      vars = new float[numPixels];
    }

    float g[] = new float[p];
    int s[] = new int[p];

    long rawPatternHist[] = new long[p + 2];
    Arrays.fill(rawPatternHist, 0);

    // Calculate information
    for (int x = r; x < width - r; x++) {
      for (int y = r; y < height - r; y++) {

	float gc = raster.getSampleFloat(x, y, 0) / 255;			

	loadCircle(r, raster, x, y, g);
	rawPatternHist[lbpriu2(gc, g, s)]++;

	if (b > 0) {
	  int i = (x - r) * (height - 2 * r) + (y - r);					
	  vars[i] = var(g);
	}
      }
    }

    // Update model
    updatePatternHistogram(rawPatternHist);
    if (b > 0) {
      updateVarianceHistogram(vars);
    }		
    imageCount++;

  }

  /**
   * Updates the model by incorporating information from an image.
   * 
   * @param raster Raster
   */
  protected void incorporateSimple(Raster raster) {

    // Initialize variables
    int width = raster.getWidth();
    int height = raster.getHeight();

    float[] vars = null;

    if (b > 0) {
      int numPixels = (width - 2 * r) * (height - 2 * r);
      vars = new float[numPixels];
    }

    float g[] = new float[p];
    int s[] = new int[p];

    long rawPatternHist[] = new long[p];
    Arrays.fill(rawPatternHist, 0);

    // Calculate information
    for (int x = r; x < width - r; x++) {
      for (int y = r; y < height - r; y++) {

	float gc = raster.getSampleFloat(x, y, 0) / 255;			

	loadCircle(r, raster, x, y, g);
	rawPatternHist[lbp(gc, g, s)]++;

	if (b > 0) {
	  int i = (x - r) * (height - 2 * r) + (y - r);					
	  vars[i] = var(g);
	}
      }
    }

    // Update model
    updatePatternHistogram(rawPatternHist);
    if (b > 0) {
      updateVarianceHistogram(vars);
    }		
    imageCount++;

  }

  /**
   * Updates the internal pattern histogram.
   * 
   * @param rawHist Raw pattern data
   */
  protected void updatePatternHistogram(long rawHist[]) {
    long sum = 0;
    for (int i = 0; i < p + 2; i++) {
      sum += rawHist[i];
    }

    if (imageCount == 0) {			
      // This is the first time we add histogram data
      patternHist = new float[p + 2];
      for (int i = 0; i < p + 2; i++) {
	patternHist[i] = (float) (rawHist[i] / ((double) sum));
      }
    } else {
      for (int i = 0; i < p + 2; i++) {
	float x = (float) (rawHist[i] / ((double) sum));
	patternHist[i] = (imageCount * patternHist[i] + x) / (imageCount + 1);
      }
    }
  }

  /**
   * Creates a variance histogram from scratch.
   * <p>
   * This method creates a histogram for the variance values passed via parameter.
   * <p>
   * We use a logarithmically spaced histogram for the variance that has
   * fixed bin edges (in contrast to the paper, where variable bin edges
   * are used). Fixed bin edges have the advantage that comparing and
   * updating models is much simpler. The logarithmical spacing is due
   * to experiments with the variable bin size algorithm from the paper,
   * which produces approximately logarithmically spaced bins.
   * 
   * @param vars Variance values
   * @return Variance histogram
   */
  protected float[] createVarianceHistogram(float vars[]) {
    float h[] = new float[b];

    for (int i = 0; i < vars.length; i++) {		
      float f = (float) (Math.max(Math.log10(vars[i]), -6) + 6) / 6;
      int bin = (int) (b * f - 0.000001);
      h[bin]++;
    }
    for (int i = 0; i < b; i++) {
      h[i] /= vars.length;
    }

    return h;
  }

  /**
   * Updates the internal variance histogram.
   * 
   * @param vars Variance data
   */
  protected void updateVarianceHistogram(float[] vars) {

    if (imageCount == 0) {
      // This is the first time we add histogram data
      varHist = createVarianceHistogram(vars);
    } else {
      float[] newHist = createVarianceHistogram(vars);
      for (int i = 0; i < b; i++) {
	varHist[i] = (imageCount * varHist[i] + newHist[i]) / (imageCount + 1);
      }
    }
  }	

  /**
   * Returns the number of images processed.
   * 
   * @return		the number of images
   */
  public int getImageCount() {
    return imageCount;
  }
  
  /**
   * Returns the current pattern histogram.
   * 
   * @return		the histogram
   */
  public float[] getPatternHistogram() {
    return patternHist;
  }
  
  /**
   * Returns the current variance histogram.
   * 
   * @return		the histogram
   */
  public float[] getVarianceHistogram() {
    return varHist;
  }
  
  /**
   * Returns a string representation of this sub-model.
   * 
   * @return A string representation of this sub-model. 
   */
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("");
    for (int i = 0; i < p + 2; i++) {
      s.append(patternHist[i]);
      if (i < p + 1) {
	s.append("/");
      }		
    }
    s.append(":");
    for (int i = 0; i < b; i++) {
      s.append(varHist[i]);
      if (i < b - 1) {
	s.append("/");
      }
    }		
    return s.toString();
  }
  
  /**
   * Returns the length of the histogram being generated.
   * 
   * @param p		the number of neighbors
   * @param uniform	whether uniform or simple is used
   */
  public static int getHistogramLength(int p, boolean uniform) {
    if (uniform)
      return p + 2;
    else
      return p;
  }

  /**
   * Bilinear interpolation for Raster data.
   * <p>
   * Note that no out-of-bounds checking is performed.
   * 
   * @param raster Raster
   * @param x x-coordinate
   * @param y y-coordinate
   * @param b Band number
   * @return Bilinearly interpolated pixel value of the given band at the given location
   */
  protected static float interpolate(Raster raster, float x, float y, int b) {

    int x1, x2, y1, y2;

    /*
     * We need to check whether the coordinates lie directly on a pixel
     * center. Otherwise we might run into out-of-bounds errors at the
     * edges of the raster.
     */
    int xr = Math.round(x);
    if (Math.abs(x - xr) < 1e-4) {
      x1 = xr;
      x2 = xr;
      x = 0;
    } else {
      x1 = (int) Math.floor(x);
      x2 = x1 + 1;
      x = x - x1;
    }
    int yr = Math.round(y);
    if (Math.abs(y - yr) < 1e-4) {
      y1 = yr;
      y2 = yr;
      y = 0;
    } else {
      y1 = (int) Math.floor(y);
      y2 = y1 + 1;
      y = y - y1;
    }

    float ll = raster.getSampleFloat(x1, y1, b) / 255;
    float ul = raster.getSampleFloat(x1, y2, b) / 255;
    float lr = raster.getSampleFloat(x2, y1, b) / 255;
    float ur = raster.getSampleFloat(x2, y2, b) / 255;					

    return (
	ll * (1 - x) * (1 - y) 
	+ lr * x * (1 - y) 
	+ ul * (1 - x) * y 
	+ ur * x * y
	);		
  }

  /**
   * Loads a circle of pixels from an image.
   * <p>
   * Note that no out-of-bounds checking is performed.
   * 
   * @param r Radius
   * @param raster Raster
   * @param x x-coordinate of the center
   * @param y y-coordinate of the center
   * @param g Pre-allocated array of neighbors
   */
  protected static void loadCircle(int r, Raster raster, int x, int y, float g[]) {
    int p = g.length;
    float f = 2 * (float) Math.PI / p;		
    for (int i = 0; i < p; i++) {
      float gx =  x - r * (float) Math.sin(i * f);
      float gy = y + r * (float) Math.cos(i * f);
      g[i] = interpolate(raster, gx, gy, 0);				
    }		
  }

  /**
   * Calculates the local binary pattern for a single pixel. 
   *  
   * @param gc Center pixel value
   * @param g Circle pixels as returned from loadCircle
   * @param s Pre-allocated integer array of length p
   * @return The local binary pattern
   */
  protected static int lbp(float gc, float g[], int s[]) {		
    int p = g.length;
    for (int i = 0; i < p; i++) {
      s[i] = g[i] - gc >= 0 ? 1 : 0;			
    }
    int sum = 0;
    for (int i = 0; i < p; i++) {
      sum += s[i];
    }
    return sum;
  }

  /**
   * Calculates the local, rotation invariant, and uniform binary pattern for a single
   * pixel. 
   *  
   * @param gc Center pixel value
   * @param g Circle pixels as returned from loadCircle
   * @param s Pre-allocated integer array of length p
   * @return The local binary pattern
   */
  protected static int lbpriu2(float gc, float g[], int s[]) {		
    int p = g.length;
    for (int i = 0; i < p; i++) {
      s[i] = g[i] - gc >= 0 ? 1 : 0;			
    }		

    // Equation (10) in the paper
    int u = 0;
    for (int i = 0; i < p; i++) {
      u += s[i] == s[(i + 1) % p] ? 0 : 1;
    }

    // Equation (9) in the paper
    if (u <= 2) {
      int sum = 0;
      for (int i = 0; i < p; i++) {
	sum += s[i];
      }
      return sum;
    } else {
      return p + 1;
    }		
  }

  /**
   * Calculates the rotation invariant measure of local variance.
   * 
   * @param g Circle pixels as returned from loadCircle
   * @return The local variance
   */
  protected static float var(float g[]) {
    float mu = 0;
    int p = g.length;
    for (int i = 0; i < p; i++) {
      mu += g[i];
    }
    mu /= p;		

    float v = 0;
    for (int i = 0; i < p; i++) {
      v += (g[i] - mu) * (g[i] - mu);			
    }
    return v / p;
  }
}
