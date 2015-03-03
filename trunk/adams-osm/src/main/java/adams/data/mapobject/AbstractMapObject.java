/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/**
 * Copyright (C) OpenStreetMap
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */

package adams.data.mapobject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.swing.UIManager;

import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapObjectImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Adapted {@link MapObjectImpl} code.
 *
 * @author OpenStreetMap (original code: {@link MapObjectImpl})
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "OpenStreetMap",
    license = License.GPL2,
    url = "http://svn.openstreetmap.org/applications/viewer/jmapviewer/releases/1.02/JMapViewer-1.02-Source.zip",
    note = "Original class: org.openstreetmap.gui.jmapviewer.MapObjectImpl"
)
public abstract class AbstractMapObject 
  implements MapObject, MetaDataSupporter, TimestampSupporter {
  
  protected Layer layer;
  
  protected String name;
  
  protected Style style;
  
  protected Boolean visible;

  /** the timestamp. */
  protected Date timestamp;
  
  /** for storing meta-data. */
  protected HashMap<String,Object> metaData;
  
  public AbstractMapObject(String name) {
    this(null, name, null);
  }
  
  public AbstractMapObject(Layer layer) {
    this(layer, null, null);
  }
  
  public AbstractMapObject(Layer layer, String name, Style style) {
    super();
    this.layer = layer;
    this.name = name;
    this.style = style;
    metaData = new HashMap<String,Object>();
    timestamp = new Date();
  }
  
  public Layer getLayer() {
    return layer;
  }
  
  public void setLayer(Layer layer) {
    this.layer = layer;
  }
  
  public Style getStyle(){
    return style;
  }
  
  public Style getStyleAssigned(){
    return style == null ? (layer == null ? null : layer.getStyle()) : style;
  }
  
  public void setStyle(Style style){
    this.style = style;
  }
  
  public Color getColor() {
    Style styleAssigned = getStyleAssigned();
    return styleAssigned == null ? null : getStyleAssigned().getColor();
  }
  
  public void setColor(Color color) {
    if(style==null&&color!=null) style=new Style();
    if(style!=null) style.setColor(color);
  }

  public Color getBackColor() {
    Style styleAssigned = getStyleAssigned();
    return styleAssigned == null ? null : getStyleAssigned().getBackColor();
  }
  
  public void setBackColor(Color backColor) {
    if(style==null&&backColor!=null) style=new Style();
    if(style!=null) style.setBackColor(backColor);
  }

  public Stroke getStroke() {
    Style styleAssigned = getStyleAssigned();
    return styleAssigned == null ? null : getStyleAssigned().getStroke();
  }
  
  public void setStroke(Stroke stroke) {
    if(style==null&&stroke!=null) style=new Style();
    if(style!=null) style.setStroke(stroke);
  }

  public Font getFont() {
    Style styleAssigned = getStyleAssigned();
    return styleAssigned == null ? null : getStyleAssigned().getFont();
  }
  
  public void setFont(Font font) {
    if(style==null&&font!=null) style=new Style();
    if(style!=null) style.setFont(font);
  }
  
  private boolean isVisibleLayer(){
    return layer==null||layer.isVisible()==null?true:layer.isVisible();
  }
  
  public boolean isVisible() {
    return visible==null?isVisibleLayer():visible.booleanValue();
  }
  
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String txt) {
    this.name = txt;
  }
  
  /**
   * Returns the current timestamp.
   * 
   * @return		the timestamp
   */
  public Date getTimestamp() {
    return timestamp;
  }
  
  /**
   * Sets the timestamp.
   * 
   * @param value	the new timestamp
   */
  public void setTimestamp(Date value) {
    timestamp = value;
  }

  /**
   * Adds a key-value pair to the meta-data store.
   * 
   * @param key		the key to store the value for
   * @param value	the value to store
   * @return		any previously stored object for this key, otherwise null
   */
  public Object addMetaData(String key, Object value) {
    return metaData.put(key, value);
  }

  /**
   * Removes a key-value pair from the meta-data store.
   * 
   * @param key		the key to remove the value for
   * @return		any previously stored object for this key, null if none was present
   */
  public Object removeMetaData(String key) {
    return metaData.remove(key);
  }

  /**
   * Returns the specified value from the meta-data store.
   * 
   * @param key		the key to retrieve the value for
   * @return		stored object for this key, null if none was present
   */
  public Object getMetaData(String key) {
    return metaData.get(key);
  }
  
  /**
   * Returns the meta-data store.
   * 
   * @return		the meta-data
   */
  public HashMap<String,Object> getMetaData() {
    return metaData;
  }
  
  /**
   * Returns the set of meta-data keys.
   * 
   * @return		the meta-data keys
   */
  public Set<String> metaDataKeys() {
    return metaData.keySet();
  }
  
  public static Font getDefaultFont(){
    Font f = UIManager.getDefaults().getFont("TextField.font");
    return new Font(f.getName(), Font.BOLD, f.getSize());
  }
  
  public void paintText(Graphics g, Point position) {
    if(name!=null && g!=null && position!=null){
      if(getFont()==null){
	Font f = getDefaultFont(); // g.getFont();
	setFont(new Font(f.getName(), Font.BOLD, f.getSize()));
      }
      g.setColor(Color.DARK_GRAY);
      g.setFont(getFont());
      g.drawString(name, position.x+SimpleMapMarkerDot.DOT_RADIUS+2, position.y+SimpleMapMarkerDot.DOT_RADIUS);
    }
  }
}
