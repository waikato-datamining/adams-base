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
 * AbstractSubImagesGenerator.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.BufferedImageContainer;
import adams.data.objectfinder.ObjectsInRegion;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Rectangle;
import java.util.List;

/**
 * Ancestor for classes that generate subimages from a single image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSubImagesGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 2258244755943306047L;

  /** the prefix to use when generating a report. */
  protected String m_Prefix;

  /** whether to include partial hits. */
  protected boolean m_Partial;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "partial", "partial",
      false);
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Sets whether to include partial hits.
   *
   * @param value 	true if to include partial hits
   */
  public void setPartial(boolean value) {
    m_Partial = value;
    reset();
  }

  /**
   * Returns whether to include partial hits.
   *
   * @return 		true if to count partial hits
   */
  public boolean getPartial() {
    return m_Partial;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String partialTipText() {
    return "If enabled, partial hits are included as well.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "prefix", m_Prefix, "prefix: ");
    result += QuickInfoHelper.toString(this, "partial", m_Partial, "incl. partial", ", ");

    return result;
  }

  /**
   * Checks whether the image can be processed.
   * <br><br>
   * Default implementation only ensures that an image is present.
   * 
   * @param image	the image to check
   */
  protected void check(BufferedImageContainer image) {
    if (image == null)
      throw new IllegalArgumentException("No image provided!");
  }

  /**
   * Generates a new report with only the objects that fall within the region.
   *
   * @param oldReport	the old report to use as basis
   * @param x		the left position of the region
   * @param y 		the top position of the region
   * @param width	the width of the region
   * @param height 	the height of the region
   * @return		the new report with the subset of objects in the region
   */
  protected Report transferObjects(Report oldReport, int x, int y, int width, int height) {
    return transferObjects(oldReport, new Rectangle(x, y, width, height));
  }

  /**
   * Generates a new report with only the objects that fall within the region.
   *
   * @param oldReport	the old report to use as basis
   * @param region	the region that the
   * @return		the new report with the subset of objects in the region
   */
  protected Report transferObjects(Report oldReport, Rectangle region) {
    Report		result;
    ObjectsInRegion	finder;
    LocatedObjects	objects;
    LocatedObjects	newObjects;
    TIntSet		indices;
    boolean		anyObjects;

    try {
      result = oldReport.getClass().newInstance();
    }
    catch (Exception e) {
      result = new Report();
    }

    // transfer all other fields
    anyObjects = false;
    for (AbstractField field: oldReport.getFields()) {
      if (field.getName().startsWith(m_Prefix)) {
        anyObjects = true;
	continue;
      }
      result.addField(field);
      result.setValue(field, oldReport.getValue(field));
    }

    if (anyObjects) {
      // locate objects
      finder = new ObjectsInRegion();
      finder.setPrefix(m_Prefix);
      finder.setPartial(m_Partial);
      finder.setLeft((int) region.getX());
      finder.setTop((int) region.getY());
      finder.setWidth((int) region.getWidth());
      finder.setHeight((int) region.getHeight());
      indices = new TIntHashSet(finder.find(oldReport));

      objects    = LocatedObjects.fromReport(oldReport, m_Prefix);
      newObjects = new LocatedObjects();
      for (LocatedObject obj: objects) {
        if (indices.contains(obj.getIndex()))
          newObjects.add(obj);
      }

      // transfer objects
      result.mergeWith(newObjects.toReport(m_Prefix));
    }

    return result;
  }

  /**
   * Performs the actual generation of the subimages.
   * 
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  protected abstract List<BufferedImageContainer> doProcess(BufferedImageContainer image);
  
  /**
   * Generates subimages from the provided image.
   * 
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  public List<BufferedImageContainer> process(BufferedImageContainer image) {
    check(image);
    return doProcess(image);
  }
}
