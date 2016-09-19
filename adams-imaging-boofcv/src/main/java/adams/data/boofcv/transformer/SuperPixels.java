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
 * SuperPixels.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.core.io.PlaceholderFile;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.BoofCVImageReader;
import adams.env.Environment;
import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.factory.segmentation.ConfigSlic;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.gui.feature.VisualizeRegions;
import boofcv.struct.image.*;
import org.ddogleg.struct.FastQueue;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class SuperPixels extends AbstractBoofCVTransformer{

  private final int R = 0;
  private final int G = 1;
  private final int B = 2;

  protected int m_NumberOfSegments;
  protected float m_SpacialWeight;

  protected boolean m_AverageSegments;
  protected boolean m_OutlineSegments;
  protected boolean m_RandomColours;

  /**
   * Performs the actual transforming of the image.
   *
   * @param img the image to transform (can be modified, since it is a copy)
   * @return the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[] result = new BoofCVImageContainer[1];
    result[0] = new BoofCVImageContainer();
    ImageSuperpixels alg 	= FactoryImageSegmentation.slic(new ConfigSlic(m_NumberOfSegments, m_SpacialWeight), img.getImage().getImageType());
    ImageSInt32 output 		= new ImageSInt32(img.getWidth(), img.getHeight());
    alg.segment(img.getImage(),output);
    BufferedImage image = img.toBufferedImage();
    try {
      ImageIO.write(image, "png", new File("/home/sjb90/Pictures/Binary test/test2.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    int numSegments = alg.getTotalSuperpixels();
    if(m_AverageSegments)
      averageSegmentColour(output,numSegments,image);
    if(m_RandomColours && !m_AverageSegments)
      VisualizeRegions.regions(output,numSegments,image);
    if(m_OutlineSegments)
      VisualizeRegions.regionBorders(output,0xff0000,image);


    result[0].setImage(BoofCVHelper.toBoofCVImage(image));
    result[0].setReport(img.getReport().getClone());
    result[0].getNotes().mergeWith(img.getNotes());
    return result;
  }

  private void outlineSegments(ImageSInt32 pixelMap, int numSegments, BufferedImage image) {

  }

  /**
   * Takes a super pixel map and an image and averages the colours in every super pixel region defined by the map.
   *
   * @param pixelMap The map of the super pixel regions
   * @param numSegments the number of super pixels
   * @param image The image to be transformed
   */
  private void averageSegmentColour(ImageSInt32 pixelMap, int numSegments, BufferedImage image) {
    // Set the colour of the pixels to the average of the super pixel
    int[] count 	= new int[numSegments];
    int[][] runningSum 	= new int[numSegments][4];
    for (int i = 0; i < count.length; i++) {
      count[i] = 1;
    }
    // Count up the number of pixels for each segment and track the value of the RGB channels
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	int segment = pixelMap.get(x,y);
	count[segment]++;
	int[] rgb = BufferedImageHelper.split(image.getRGB(x, y));
	runningSum[segment][R] += rgb[R];
	runningSum[segment][G] += rgb[G];
	runningSum[segment][B] += rgb[B];
      }
    }
    // Find average value of the RGB channels by dividing the RGB values by the number of pixels in the corresponding
    // segment.
    int[] averageRgbForSegment = new int[numSegments];
    for (int segment = 0; segment < numSegments; segment++) {
      averageRgbForSegment[segment] = BufferedImageHelper.combine(runningSum[segment][R] / count[segment],
	runningSum[segment][G] / count[segment], runningSum[segment][B] / count[segment], 255);
    }

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	int segment = pixelMap.get(x,y);
	image.setRGB(x,y,averageRgbForSegment[segment]);
      }
    }
  }

  public boolean isRandomColours() {
    return m_RandomColours;
  }

  public void setRandomColours(boolean m_RandomColours) {
    this.m_RandomColours = m_RandomColours;
  }

  public int getNumberOfSegments() {
    return m_NumberOfSegments;
  }

  public void setNumberOfSegments(int m_NumberOfSegments) {
    this.m_NumberOfSegments = m_NumberOfSegments;
  }

  public float getSpacialWeight() {
    return m_SpacialWeight;
  }

  public void setSpacialWeight(float m_SpacialWeight) {
    this.m_SpacialWeight = m_SpacialWeight;
  }

  public boolean isAverageSegments() {
    return m_AverageSegments;
  }

  public void setAverageSegments(boolean m_AverageSegments) {
    this.m_AverageSegments = m_AverageSegments;
  }

  public boolean isOutlineSegments() {
    return m_OutlineSegments;
  }

  public void setOutlineSegments(boolean m_OutlineSegments) {
    this.m_OutlineSegments = m_OutlineSegments;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("numberOfSegments", "numberOfSegments", 100);
    m_OptionManager.add("spacialweight", "spacialWeight", 200.0f);
    m_OptionManager.add("outlinesegments", "outlineSegments", false);
    m_OptionManager.add("averagesegments", "averageSegments", false);
    m_OptionManager.add("randomcolours", "randomColours", false);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Takes an image and divides it into super pixels.";
  }

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);


    BoofCVImageReader reader = new BoofCVImageReader();
    BoofCVImageContainer cont = reader.read(new PlaceholderFile("/home/sjb90/Desktop/Datasets/stupidface.jpg"));
    SuperPixels sp = new SuperPixels();
    try {
      ImageIO.write(sp.doTransform(cont)[0].toBufferedImage(), "png", new File("/home/sjb90/Desktop/Datasets/super.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
