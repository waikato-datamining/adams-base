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
 * BaseData.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.ml;

import java.util.Calendar;


/**
 * 
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseData {

  public static class Date {
    public Date(int d, int m, int y){
      day=d;
      month=m;
      year=y;
    }
    protected int day;
    protected int month;
    protected int year;

    public long toTimestamp(){
      Calendar thisd = Calendar.getInstance();
      thisd.set(year, month-1, day);
      return(thisd.getTimeInMillis());
    }       
    
    public Calendar toCalendar(){
      Calendar thisd = Calendar.getInstance();
      thisd.set(year, month-1, day);
      return(thisd);
    }
    
    public static Date toDate(Calendar thisd){
      return(new Date(thisd.get(Calendar.DAY_OF_MONTH), thisd.get(Calendar.MONTH)+1,thisd.get(Calendar.YEAR)));
    }
    
    public static void printCalendar(Calendar thisd){
      System.err.println(thisd.get(Calendar.DAY_OF_MONTH)+"/"+ (thisd.get(Calendar.MONTH)+1)+"/"+thisd.get(Calendar.YEAR));
    }
    
    // after equal to min, strict less than max
    public boolean between(Date min, Date max){
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      Calendar thisd = Calendar.getInstance();
      start.set(min.year, min.month-1, min.day);
      end.set(max.year, max.month-1, max.day);
      thisd.set(year, month-1, day);

      return (thisd.compareTo(start)>=0 && thisd.compareTo(end)<0);
    }

    public boolean before(Date dt){
      Calendar cdt = Calendar.getInstance();
      Calendar thisd = Calendar.getInstance();
      cdt.set(dt.year, dt.month-1, dt.day);
      thisd.set(year, month-1, day);

      return (thisd.compareTo(cdt)<0);
    }
    
    public boolean beforeOrEqual(Date dt){
      return(before(dt) ||equal(dt));
    }
    
    public boolean afterOrEqual(Date dt){
      return(after(dt) ||equal(dt));
    }

    public boolean after(Date dt){
      Calendar cdt = Calendar.getInstance();
      Calendar thisd = Calendar.getInstance();
      cdt.set(dt.year, dt.month-1, dt.day);
      thisd.set(year, month-1, day);

      return (thisd.compareTo(cdt)>0);
    }

    public boolean equal(Date dt){
      Calendar cdt = Calendar.getInstance();
      Calendar thisd = Calendar.getInstance();
      cdt.set(dt.year, dt.month-1, dt.day);
      thisd.set(year, month-1, day);

      return (thisd.compareTo(cdt)==0);
    }

    public int minusInDays(Date dt){
      Calendar cdt = Calendar.getInstance();
      Calendar thisd = Calendar.getInstance();
      cdt.set(dt.year, dt.month-1, dt.day);
      thisd.set(year, month-1, day);

      Calendar date = (Calendar) thisd.clone();  
      int daysBetween = 0;  
      while (date.after(cdt)) {  
	date.add(Calendar.DATE, -1);  
	daysBetween++;  
      }  
      return daysBetween;  
    }
    
    

    public Date addDays(int days){
      Calendar thisd = Calendar.getInstance();
      thisd.set(year, month-1, day);
      thisd.add(Calendar.DATE, days);
      return(new Date(thisd.get(Calendar.DAY_OF_MONTH), thisd.get(Calendar.MONTH)+1,thisd.get(Calendar.YEAR)));
    }

    @Override
    public String toString(){
      return(""+day+"/"+month+"/"+year);
    }
  }

  public static class Coordinate{
    public Coordinate(int d, int m, int s){
      degree=d;
      minute=m;
      second=s;
    }
    protected int degree;
    protected int minute;
    protected int second;

    protected double toDecimalDegree(){
      long seconds=(minute*60)+second;
      double frac=(double)seconds/3600.0;
      if (degree < 0){
	return(-((double)-degree+frac));
      } else {
	return((double)degree+frac);
      }
      
    }

    @Override
    public String toString(){
      return(degree+":"+minute+":"+second);
    }

    public static Coordinate decimalToDMS(double coord) {

      // gets the modulus the coordinate divided by one (MOD1).
      // in other words gets all the numbers after the decimal point.
      // e.g. mod = 87.728056 % 1 == 0.728056
      //
      // next get the integer part of the coord. On other words the whole number part.
      // e.g. intPart = 87

      double mod = coord % 1;
      int intPart = (int)coord;
      int degree = intPart;

      //set degrees to the value of intPart
      //e.g. degrees = "87"

      // next times the MOD1 of degrees by 60 so we can find the integer part for minutes.
      // get the MOD1 of the new coord to find the numbers after the decimal point.
      // e.g. coord = 0.728056 * 60 == 43.68336
      //      mod = 43.68336 % 1 == 0.68336
      //
      // next get the value of the integer part of the coord.
      // e.g. intPart = 43

      coord = Math.abs(mod * 60);
      mod = coord % 1;
      intPart = (int)coord;
      int minute = intPart;

      //do the same again for minutes
      //e.g. coord = 0.68336 * 60 == 41.0016
      //e.g. intPart = 41
      coord = mod * 60;
      intPart = (int)coord;
      int second = intPart;

      return new Coordinate(degree,minute,second);
    }
  }

  public static class Point {
    protected double xpos;
    protected double ypos;
    
    public Point(double x, double y){
      xpos=x;
      ypos=y;
    }
    
    public double getX(){
      return(xpos);
    }
    public double getY(){
      return(ypos);
    }
  }
  
  public static class GPS {
    public GPS(Coordinate lat,Coordinate lon ){
      longitude=lon;
      latitude=lat;
    }
    protected Coordinate longitude;
    protected Coordinate latitude;

    public double longitudeToDecimalDegree(){
      return(longitude.toDecimalDegree());
    }

    public double latitudeToDecimalDegree(){
      return(latitude.toDecimalDegree());
    }
    public static GPS toGPS(double latitude, double longitude){
      return(new GPS(Coordinate.decimalToDMS(latitude),Coordinate.decimalToDMS(longitude) ));
    }
  }

  public static class Time {
    public Time(int h, int m, int s, int ms){
      hour=h;
      minute=m;
      second=s;
      millisecond=ms;
    }
    protected int hour;
    protected int minute;
    protected int second;
    protected int millisecond;
  }

  protected Object m_data;
  public enum Type{
    NUMERIC,
    STRING,
    BOOLEAN,
    DATE,
    TIME,
    GPS,
    ARRAY
  }

  public static boolean isNumeric(Object o){
    if(o instanceof Number){
      return(true);
    }

    return(false);
  }

  public static boolean isDate(Object o){
    return(o!= null &&o instanceof Date);
  }

  public static boolean isGPS(Object o){
    return(o!= null &&o instanceof GPS);
  }

  public static boolean isTime(Object o){
    return(o!= null &&o instanceof Time);
  }

  public static boolean isString( Object o){
    return(o!= null &&o instanceof String);
  }
  public static boolean isArray(Object o){
    return(o!= null && o.getClass().isArray());
  }
  public static boolean isBoolean(Object o){
    return(o instanceof Boolean);
  }

  public BaseData(Object o){
    m_data=o;
  }
  public Object getData(){
    return(m_data);
  }
  public void setData(Object data){
    m_data=data;
  }
  public Object getData(Type type){
    return(m_data);
  }
  public void setData(Object data, Type type){
    m_data=data;
  }

  public static BaseData.Type getType(Object data){
    if (isNumeric(data)){
      return(BaseData.Type.NUMERIC);
    }
    if (isArray(data)){
      return(BaseData.Type.ARRAY);
    }
    if (isBoolean(data)){
      return(BaseData.Type.BOOLEAN);
    }
    if (isString(data)){
      return(BaseData.Type.STRING);
    }
    if (isDate(data)){
      return(BaseData.Type.DATE);
    }
    if (isTime(data)){
      return(BaseData.Type.TIME);
    }    
    if (isGPS(data)){
      return(BaseData.Type.GPS);
    }
    return(null);
  }
  public static boolean typeEquals(Object data, BaseData.Type typ){
    BaseData.Type nt=getType(data);
    int o1=nt.ordinal();
    int o2=typ.ordinal();
    return(o1==o2);
  }
  public boolean isNumeric(){
    return (isNumeric(m_data));
  }
  public boolean isArray(){
    return (isArray(m_data));
  }
  public boolean isString(){
    return (isString(m_data));
  }
  public boolean isDate(){
    return (isDate(m_data));
  }
  public boolean isTime(){
    return (isTime(m_data));
  }
  public boolean isGPS(){
    return (isGPS(m_data));
  }
  @Override
  public String toString(){
    return(m_data.toString());
  }
}
