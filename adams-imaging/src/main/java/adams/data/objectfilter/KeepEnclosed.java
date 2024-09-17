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
 * KeepEnclosed.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.objectfilter;

import adams.core.QuickInfoHelper;
import adams.data.geometry.PolygonUtils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the outlines of annotations with the specified label to filter out any objects that aren't enclosed by these.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-key &lt;java.lang.String&gt; (property: key)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data containing the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-label &lt;java.lang.String&gt; (property: label)
 * &nbsp;&nbsp;&nbsp;The label of the enclosing objects.
 * &nbsp;&nbsp;&nbsp;default: object
 * </pre>
 *
 * <pre>-allow-partial &lt;boolean&gt; (property: allowPartial)
 * &nbsp;&nbsp;&nbsp;Whether to keep only partially enclosed objects as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class KeepEnclosed
  extends AbstractObjectFilter {

  private static final long serialVersionUID = -7256337273782956847L;

  /** the key in the meta-data containing the labels. */
  protected String m_Key;

  /** the label of the enclosing annotations. */
  protected String m_Label;

  /** whether to allow partially enclosed objects. */
  protected boolean m_AllowPartial;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the outlines of annotations with the specified label to filter out any objects that aren't enclosed by these.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key", "key",
      "type");

    m_OptionManager.add(
      "label", "label",
      "object");

    m_OptionManager.add(
      "allow-partial", "allowPartial",
      false);
  }

  /**
   * Sets the key in the meta-data containing the label.
   *
   * @param value	the key
   */
  public void setKey(String value) {
    m_Key = value;
    reset();
  }

  /**
   * Returns the key in the meta-data containing the label.
   *
   * @return		the key
   */
  public String getKey() {
    return m_Key;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyTipText() {
    return "The key in the meta-data containing the label.";
  }

  /**
   * Sets the label of the enclosing objects.
   *
   * @param value	the label
   */
  public void setLabel(String value) {
    m_Label = value;
    reset();
  }

  /**
   * Returns the label of the enclosing objects.
   *
   * @return		the label
   */
  public String getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTipText() {
    return "The label of the enclosing objects.";
  }

  /**
   * Sets whether to keep partially enclosed objects as well.
   *
   * @param value	true if to allow
   */
  public void setAllowPartial(boolean value) {
    m_AllowPartial = value;
    reset();
  }

  /**
   * Returns whether to keep partially enclosed objects as well.
   *
   * @return		true if to allow
   */
  public boolean getAllowPartial() {
    return m_AllowPartial;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowPartialTipText() {
    return "Whether to keep only partially enclosed objects as well.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "key", m_Key);
    result += " = ";
    result += QuickInfoHelper.toString(this, "label", m_Label);
    result += QuickInfoHelper.toString(this, "allowPartial", m_AllowPartial, "allow-partial", ", ");

    return result;
  }

  /**
   * Filters the image objects.
   *
   * @param objects the objects to filter
   * @return the updated object list
   */
  @Override
  protected LocatedObjects doFilter(LocatedObjects objects) {
    LocatedObjects	result;
    List<Polygon> 	enclosing;
    Polygon		poly;
    int			count;

    result = new LocatedObjects();

    // find the enclosing objects
    enclosing = new ArrayList<>();
    for (LocatedObject obj: objects) {
      if (obj.getMetaData().containsKey(m_Key) && obj.getMetaData().get(m_Key).equals(m_Label))
	enclosing.add(obj.getPolygon());
    }

    // filter the remaining objects
    for (LocatedObject obj: objects) {
      // add enclosing objects and move on
      if (obj.getMetaData().containsKey(m_Key) && obj.getMetaData().get(m_Key).equals(m_Label)) {
	result.add(obj.getClone());
	continue;
      }

      // get polygon
      if (obj.hasPolygon())
	poly = obj.getPolygon();
      else
	poly = PolygonUtils.toPolygon(obj.getRectangle());

      // check whether enclosed
      for (Polygon enc: enclosing) {
	count = PolygonUtils.inside(enc, poly);
	if ((count == poly.npoints) || (m_AllowPartial && (count > 0))) {
	  result.add(obj);
	  break;
	}
      }
    }

    return result;
  }
}
