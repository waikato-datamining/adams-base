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
 * Map.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.gps;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Vector;

import javax.imageio.ImageIO;

import adams.data.filter.AbstractDerivative.Point;

/**
 * ???
 * 
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Map {

  protected GPSBoundary m_boundary;

  protected int m_googleZoom;
  protected int m_x_pixels=640;
  protected int m_y_pixels=400;
  
  protected int centre_x;
  protected int centre_y;
  
  protected int m_scale=1;
  
  protected Image m_mapImage;

  protected MapType m_maptype=MapType.satellite;

  public enum MapType {
    terrain, 
    roadmap, 
    satellite, 
    hybrid
  }

  public int getWidth(){
    return(m_x_pixels);
  }
  
  public int getHeight(){
    return(m_y_pixels);
  }
  
  protected void setWidthHeight(int width, int height){
    m_x_pixels=width;
    m_y_pixels=height;
  }
  
  protected void setMapType(MapType maptype){
    m_maptype=maptype;
  }

  protected boolean generateMapImage(){
    try{
      String surl="http://maps.googleapis.com/maps/api/staticmap?"
	  + "center=" + m_boundary.getCentre().getLatitude().toDecimal()
	  + "," + m_boundary.getCentre().getLongitude().toDecimal()
	  + "&zoom=" + m_googleZoom
	  + "&size=" + m_x_pixels + "x" + m_y_pixels
	  + "&maptype=" + m_maptype.toString()
	  + "&sensor=true&scale=" + m_scale;
      URL url = new URL(surl);
      m_mapImage = ImageIO.read(url);
    } catch(Exception e){
      return(false);
    }
    return(true);
  }
  
  public static Map getMap(AbstractGPS centre, int zoomlevel,int width, int height, MapType maptype){
    AbstractGPS ne=Map.displayToCoordinate(new Point(width,0),zoomlevel);
    AbstractGPS sw=Map.displayToCoordinate(new Point(0,height),zoomlevel);
    return(Map.getMap(new GPSBoundary(ne,sw), width, height, maptype));
  }
  
  public static Map getMap(Vector<AbstractGPS> v, int width, int height, MapType maptype){
    GPSBoundary boundary=GPSBoundary.createGPSBoundary(v);
    return(Map.getMap(boundary, width, height, maptype));    
  }
  
  public static Map getMap(GPSBoundary boundary,int width, int height, MapType maptype){
    Map m=new Map();
    m.m_boundary=boundary;
    m.setWidthHeight(width, height);
    m.setZoom();
    m.setMapType(maptype);
    m.generateMapImage();
    return(m);
  }

  public void setZoom(){

    int mapdisplay = Math.min(m_x_pixels,m_y_pixels);
    double interval = 0;

    double minlat = m_boundary.getNE().getLatitude().toDecimal();
    double maxlng = m_boundary.getNE().getLongitude().toDecimal();

    double maxlat = m_boundary.getSW().getLatitude().toDecimal();
    double minlng = m_boundary.getSW().getLongitude().toDecimal();

    double ctrlat=(minlat+maxlat)/2.0;
    double ctrlng=(minlng+maxlng)/2.0;

    if ((maxlat - minlat) > (maxlng - minlng)) {
      interval = (maxlat - minlat) / 2.0;
      minlng = ctrlng - interval;
      maxlng = ctrlng + interval;
    } else {
      interval = (maxlng - minlng) / 2.0;
      minlat = ctrlat - interval;
      maxlat = ctrlat + interval;
    }

    double dist = (6371 * Math.acos(Math.sin(minlat / 57.2958) * Math.sin(maxlat / 57.2958) + (Math.cos(minlat / 57.2958) * Math.cos(maxlat / 57.2958) * Math.cos((maxlng / 57.2958) - (minlng / 57.2958)))));

    int zoom = (int)Math.round(Math.floor(8 - Math.log(1.6446 * dist / Math.sqrt(2 * (mapdisplay * mapdisplay))) / Math.log (2)));
    m_googleZoom=zoom+1;
  }

  public static AbstractGPS displayToCoordinate(Point point, int zoom) {
    // point relative to map (?)
    // longitude
    double longitude = (point.getX() * (360 / (Math.pow(2, zoom) * 256))) - 180;
    // latitude
    double latitude = point.getY() * (2 / (Math.pow(2, zoom) * 256));
    latitude = 1 - latitude;
    latitude = latitude * Math.PI;
    //latitude = rad_deg(Math.Atan(Math.Sinh(latitude)));
    latitude = rad_deg(Math.atan(Math.sinh(latitude)));

    AbstractGPS coord = new GPSDecimalDegrees(new Coordinate(latitude), new Coordinate(longitude));
    return coord;
  }

  protected static double rad_deg(double p) {
    return p * (180 / Math.PI);
  }

  public Point AbstractGPSToRawPixel(AbstractGPS gps){  
    double latitude = gps.getLatitude().toDecimal();
    double longitude = gps.getLongitude().toDecimal();

    double x = (longitude + 180) / 360; 
    double sinLatitude = Math.sin(latitude * Math.PI / 180);
    double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

    int mapSize = 256 << m_googleZoom;
    double xp = x * (double)mapSize + 0.5;
    double yp = y * (double)mapSize + 0.5;
    return(new Point(xp,yp));
  } 
  
  public Point GPSToPixel(AbstractGPS gps, int imgX, int imgY){  
    Point here=AbstractGPSToRawPixel(gps);
    Point centre=AbstractGPSToRawPixel(m_boundary.getCentre());

    double cxdiff=here.getX() - centre.getX();
    double cydiff=here.getY() - centre.getY();
    return(new Point((m_x_pixels/2.0)+cxdiff, (m_y_pixels/2.0)+cydiff));
  }

  public void setBoundary(Vector<AbstractGPS> vgps){
    m_boundary=GPSBoundary.createGPSBoundary(vgps);
  }

  public String generateGoogleURL(){
    return 
	"http://maps.googleapis.com/maps/api/staticmap?"
	+ "center=" + m_boundary.getCentre().getLatitude().toDecimal() 
	+ "," + m_boundary.getCentre().getLongitude().toDecimal()
	+ "&zoom=" + m_googleZoom
	+ "&size=" + m_x_pixels + "x" + m_y_pixels
	+ "&maptype=" + m_maptype.toString()
	+ "&sensor=true&scale=" + m_scale;
  }

  public Image getMapImage(){
    return(m_mapImage);
  }
  
  public BufferedImage getBufferedMapImage(){
    try{      
      Image image = m_mapImage;
      //BufferedImage cpimg=bufferImage(image);
      BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bufferedImage.createGraphics();
      g.drawImage(image, null, null);
      return(bufferedImage);
    }
    catch(Exception e) {
      System.out.println(e);
    }
    return(null);
  }
}
