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
 * Objects.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Extracts sub-images defined by the objects that the object finder locates.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-partial &lt;boolean&gt; (property: partial)
 * &nbsp;&nbsp;&nbsp;If enabled, partial hits are included as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Objects
  extends AbstractSubImagesGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2488185528644078539L;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Extracts sub-images defined by the objects that the object finder locates.";
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");

    return result;
  }

  /**
   * Performs the actual generation of the subimages.
   *
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  @Override
  protected List<BufferedImageContainer> doProcess(BufferedImageContainer image) {
    List<BufferedImageContainer>	result;
    BufferedImageContainer		cont;
    BufferedImage			bimage;
    LocatedObjects			objs;

    result = new ArrayList<>();

    objs   = LocatedObjects.fromReport(image.getReport(), m_Finder.getPrefix());
    bimage = image.getImage();
    for (LocatedObject obj: objs) {
      cont = (BufferedImageContainer) image.getHeader();
      cont.setReport(transferObjects(cont.getReport(), obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()));
      cont.setImage(bimage.getSubimage(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()));
      result.add(cont);
    }

    return result;
  }
}
