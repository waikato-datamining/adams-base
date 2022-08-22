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
 * BoundingBoxBased.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.areaoverlap;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.util.Map;
import java.util.logging.Level;

/**
 * Uses bounding boxes for the calculation.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BoundingBoxBased
    extends AbstractAreaOverlap {

  private static final long serialVersionUID = 610938412287576516L;

  public final static String KEY_AREA_OLD = "area-old";

  public final static String KEY_AREA_NEW = "area-new";

  public final static String KEY_AREA_RATIO = "area-ratio";

  /** how to calculate the area. */
  protected AreaType m_AreaType;

  /** whether to fallback on bounding box. */
  protected boolean m_Fallback;

  /** the ratio used for determining whether to fall back from polygon on bbox. */
  protected double m_BoundingBoxFallbackRatio;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses bounding boxes as basis.\n"
	+ "Stores the old and new area in the following meta-data values: " + KEY_AREA_OLD + ", " + KEY_AREA_NEW + "\n"
	+ "The ratio old/new is stored in: " + KEY_AREA_RATIO + " (uses -1 if failed to compute)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"area-type", "areaType",
	AreaType.INTERSECT);
  }

  /**
   * Sets what area to use.
   *
   * @param value 	the type
   */
  public void setAreaType(AreaType value) {
    m_AreaType = value;
    reset();
  }

  /**
   * Returns what area to use.
   *
   * @return 		the type
   */
  public AreaType getAreaType() {
    return m_AreaType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String areaTypeTipText() {
    return "The type of area to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "areaType", m_AreaType, "area: ");

    return result;
  }

  /**
   * Computes the overlapping areas between the matches.
   *
   * @param matches the computed matches
   * @param errors  for collecting errors
   * @return the overlapping areas, null in case of error
   */
  @Override
  protected LocatedObjects doCalculate(Map<LocatedObject, Map<LocatedObject, Double>> matches, MessageCollection errors) {
    LocatedObjects 		result;
    Map<LocatedObject, Double>	subset;
    Geometry			keyGeo;
    Geometry			subGeo;
    Geometry 			combined;
    LocatedObject		newObj;

    result = new LocatedObjects();

    for (LocatedObject key: matches.keySet()) {
      keyGeo = LocatedObject.toGeometry(key.getRectangle());
      subset = matches.get(key);
      for (LocatedObject sub: subset.keySet()) {
	if (key.equals(sub))
	  continue;
	subGeo = LocatedObject.toGeometry(sub.getRectangle());
	try {
	  switch (m_AreaType) {
	    case INTERSECT:
	      combined = keyGeo.intersection(subGeo);
	      break;
	    case UNION:
	      combined = keyGeo.union(subGeo);
	      break;
	    default:
	      throw new IllegalStateException("Unhandled area type: " + m_AreaType);
	  }
	  if (combined instanceof Polygon) {
	    newObj = new LocatedObject(null, LocatedObject.polygonBounds((Polygon) combined), key.getMetaData(true));
	    newObj.setPolygon((Polygon) combined);
	    if ((newObj.getWidth() > 0) && (newObj.getHeight() > 0)) {
	      newObj.getMetaData().put(KEY_AREA_OLD, keyGeo.getArea());
	      newObj.getMetaData().put(KEY_AREA_NEW, combined.getArea());
	      try {
		newObj.getMetaData().put(KEY_AREA_RATIO, combined.getArea() / keyGeo.getArea());
	      }
	      catch (Exception e) {
		newObj.getMetaData().put(KEY_AREA_RATIO, -1.0);
	      }
	      result.add(newObj);
	    }
	  }
	  else {
	    // TODO other shapes?
	    getLogger().warning(m_AreaType + " of " + key + " and " + sub + " failed to generate polygon, instead got: " + Utils.classToString(combined));
	  }
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to compute " + m_AreaType + " of " + key + " and " + sub + ": ", e);
	}
      }
    }

    return result;
  }
}
