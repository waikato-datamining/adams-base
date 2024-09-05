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
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.BufferedImageContainer;
import adams.data.objectfilter.Translate;
import adams.data.objectfinder.ObjectsInRegion;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

import java.awt.Rectangle;
import java.util.List;

/**
 * Ancestor for classes that generate subimages from a single image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSubImagesGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter, ObjectPrefixHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2258244755943306047L;

  /** the prefix to use when generating a report. */
  protected String m_Prefix;

  /** whether to include partial hits. */
  protected boolean m_Partial;

  /** whether to fix the shapes of partial hits. */
  protected boolean m_FixInvalid;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      LocatedObjects.DEFAULT_PREFIX);

    m_OptionManager.add(
      "partial", "partial",
      false);

    m_OptionManager.add(
      "fix-invalid", "fixInvalid",
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
   * @return 		true if to include partial hits
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
   * Sets whether to fix invalid shapes.
   *
   * @param value 	true if to fix invalid shapes
   */
  public void setFixInvalid(boolean value) {
    m_FixInvalid = value;
    reset();
  }

  /**
   * Returns whether to fix invalid shapes.
   *
   * @return 		true if to fix invalid shapes
   */
  public boolean getFixInvalid() {
    return m_FixInvalid;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fixInvalidTipText() {
    return "If enabled, objects that fall partially outside the image boundaries get fixed (eg when allowing partial hits).";
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
    result += QuickInfoHelper.toString(this, "fixInvalid", m_FixInvalid, "fix", ", ");

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
    return transferObjects(oldReport, new Rectangle(x, y, width, height), null);
  }

  /**
   * Generates a new report with only the objects that fall within the region.
   *
   * @param oldReport	the old report to use as basis
   * @param x		the left position of the region
   * @param y 		the top position of the region
   * @param width	the width of the region
   * @param height 	the height of the region
   * @param label 	the label to set, ignored if null
   * @return		the new report with the subset of objects in the region
   */
  protected Report transferObjects(Report oldReport, int x, int y, int width, int height, String label) {
    return transferObjects(oldReport, new Rectangle(x, y, width, height), label);
  }

  /**
   * Generates a new report with only the objects that fall within the region.
   *
   * @param oldReport	the old report to use as basis
   * @param region	the region in which to locate objects
   * @return		the new report with the subset of objects in the region
   */
  protected Report transferObjects(Report oldReport, Rectangle region) {
    return transferObjects(oldReport, region, null);
  }

  /**
   * Generates a new report with only the objects that fall within the region.
   *
   * @param oldReport	the old report to use as basis
   * @param region	the region in which to locate objects
   * @param label 	the label to set, ignored if null
   * @return		the new report with the subset of objects in the region
   */
  protected Report transferObjects(Report oldReport, Rectangle region, String label) {
    Report		result;
    ObjectsInRegion	finder;
    LocatedObjects	objects;
    LocatedObjects	newObjects;
    Translate		trans;
    boolean		anyObjects;

    try {
      result = oldReport.getClass().getDeclaredConstructor().newInstance();
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
      objects = LocatedObjects.fromReport(oldReport, m_Prefix);

      // locate objects in rectangle
      finder = new ObjectsInRegion();
      finder.setPrefix(m_Prefix);
      finder.setPartial(m_Partial);
      finder.setLeft((int) region.getX() + 1);
      finder.setTop((int) region.getY() + 1);
      finder.setWidth((int) region.getWidth());
      finder.setHeight((int) region.getHeight());
      newObjects = finder.findObjects(objects);

      // translate objects
      trans = new Translate();
      trans.setX((int) -region.getX());
      trans.setY((int) -region.getY());
      newObjects = trans.filter(newObjects);

      // fix invalid shapes
      if (m_FixInvalid) {
        for (LocatedObject obj: newObjects)
          obj.makeFit((int) region.getWidth(), (int) region.getHeight());
      }

      // force label?
      if (label != null) {
	for (LocatedObject obj: newObjects)
	  obj.getMetaData().put("type", label);
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
