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
 * ObjectAnnotationsToImageSegmentationLayers.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.flow.container.ImageSegmentationContainer;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Converts the annotations to image segmentation layers.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix of fields in the report to identify the type.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotationsToImageSegmentationLayers
  extends AbstractConversion {

  private static final long serialVersionUID = -461975749250150031L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the type suffix. */
  protected String m_TypeSuffix;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the annotations to image segmentation layers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
	"type-suffix", "typeSuffix",
	".type");
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
   *
   * @return		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use.";
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_TypeSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return "The suffix of fields in the report to identify the type.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return AbstractImageContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return ImageSegmentationContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    AbstractImageContainer	cont;
    ImageSegmentationContainer	result;
    int				width;
    int				height;
    String			name;
    Report			report;
    LocatedObjects		objects;
    Set<String>			types;
    String			key;
    String			value;
    Map<String,BufferedImage> 	layers;
    BufferedImage		image;
    Graphics2D			g2d;

    cont    = (AbstractImageContainer) m_Input;
    report  = cont.getReport();
    width   = cont.getWidth();
    height  = cont.getHeight();
    objects = m_Finder.findObjects(report);

    // name
    name = "unknown";
    if (report.hasValue("File"))
      name = new PlaceholderFile(report.getStringValue("File")).getName();
    else if (report.hasValue("Name"))
      name = new PlaceholderFile(report.getStringValue("Name")).getName();
    if (isLoggingEnabled())
      getLogger().info("Name: " + name);

    // determine types
    key   = m_TypeSuffix;
    if (key.startsWith("."))
      key = key.substring(1);
    types = new HashSet<>();
    for (LocatedObject obj: objects) {
      if (obj.getMetaData().containsKey(key))
        types.add("" + obj.getMetaData().get(key));
    }
    if (types.size() == 0)
      throw new IllegalStateException("No types identified using suffix: " + m_TypeSuffix);
    if (isLoggingEnabled())
      getLogger().info("Types: " + Utils.flatten(types.toArray(), ", "));

    // init layers
    layers = new HashMap<>();
    for (String type: types) {
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      layers.put(type, image);
    }

    // draw objects
    for (String type: types) {
      image = layers.get(type);
      g2d   = image.createGraphics();
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0, 0, width, height);
      g2d.setColor(Color.WHITE);
      for (LocatedObject obj : objects) {
	if (obj.getMetaData().containsKey(key)) {
	  value = "" + obj.getMetaData().get(key);
	  if (value.equals(type)) {
	    if (obj.hasPolygon())
	      g2d.fillPolygon(obj.getPolygon());
	    else
	      g2d.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
	  }
	}
      }
      g2d.dispose();
    }

    // create container
    result = new ImageSegmentationContainer(name, cont.toBufferedImage(), layers);

    return result;
  }
}
