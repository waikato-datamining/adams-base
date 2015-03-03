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
 * SimulatedPlace.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.twitter;

import twitter4j.GeoLocation;
import twitter4j.Place;

/**
 * For simulating tweets without using Twitter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedPlace
  extends AbstractSimulatedTwitterResponse
  implements Place {

  /** for serialization. */
  private static final long serialVersionUID = -5986417397392380504L;

  /** the bounding box. */
  protected GeoLocation[][] m_BoundingBoxCoordinates;

  /** the bounding box type. */
  protected String m_BoundingBoxType;

  /** contained within. */
  protected Place[] m_ContainedWithIn;

  /** the country. */
  protected String m_Country;

  /** the country code. */
  protected String m_CountryCode;

  /** the full name. */
  protected String m_FullName;

  /** the coordinates. */
  protected GeoLocation[][] m_GeometryCoordinates;

  /** the geometry type. */
  protected String m_GeometryType;

  /** the Id. */
  protected String m_Id;

  /** the name. */
  protected String m_Name;

  /** place type. */
  protected String m_PlaceType;

  /** the street address. */
  protected String m_StreetAddress;

  /** the URL. */
  protected String m_URL;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_BoundingBoxCoordinates = null;
    m_BoundingBoxType        = null;
    m_ContainedWithIn        = null;
    m_Country                = null;
    m_CountryCode            = null;
    m_FullName               = null;
    m_GeometryCoordinates    = null;
    m_GeometryType           = null;
    m_Id                     = null;
    m_Name                   = null;
    m_PlaceType              = null;
    m_StreetAddress          = null;
    m_URL                    = null;
  }

  /**
   * Compares this place with the other one.
   * Uses: {@link #getId()}
   *
   * @param o		the place to compare with
   * @return		less than 0, equal to 0, greater than 0 if this
   * 			place is less, equal or larger than the other one
   */
  @Override
  public int compareTo(Place o) {
    if ((getId() == null) && (o.getId() == null))
	return 0;
    else if (getId() == null)
      return -1;
    else if (o.getId() == null)
      return +1;
    else
      return getId().compareTo(o.getId());
  }

  /**
   * Sets the bounding box coordinates.
   *
   * @parem value	the coordinates
   */
  public void setBoundingBoxCoordinates(GeoLocation[][] value) {
    m_BoundingBoxCoordinates = value;
  }

  /**
   * Returns the bounding box coordinates.
   *
   * @return		the coordinates, null if not set
   */
  @Override
  public GeoLocation[][] getBoundingBoxCoordinates() {
    return m_BoundingBoxCoordinates;
  }

  /**
   * Sets the type of bounding box.
   *
   * @param value	the type
   */
  public void setBoundingBoxType(String value) {
    m_BoundingBoxType = value;
  }

  /**
   * Returns the type of bounding box.
   *
   * @return		the type, null if not set
   */
  @Override
  public String getBoundingBoxType() {
    return m_BoundingBoxType;
  }

  /**
   * Sets the places this place is contained within.
   *
   * @param value	the places, null if not set
   */
  public void setContainedWithIn(Place[] value) {
    m_ContainedWithIn = value;
  }

  /**
   * Returns the places this place is contained within.
   *
   * @return		the places, null if not set
   */
  @Override
  public Place[] getContainedWithIn() {
    return m_ContainedWithIn;
  }

  /**
   * Sets the country.
   *
   * @param value	the country
   */
  public void setCountry(String value) {
    m_Country = value;
  }

  /**
   * Returns the country.
   *
   * @return		the country, null if not set
   */
  @Override
  public String getCountry() {
    return m_Country;
  }

  /**
   * Sets the country code.
   *
   * @param value	the country code
   */
  public void setCountryCode(String value) {
    m_CountryCode = value;
  }

  /**
   * Returns the country code.
   *
   * @return		the country code, null if not set
   */
  @Override
  public String getCountryCode() {
    return m_CountryCode;
  }

  /**
   * Sets the full name.
   *
   * @param value	the full name
   */
  public void setFullName(String value) {
    m_FullName = value;
  }

  /**
   * Returns the full name.
   *
   * @return		the full name, null if not set
   */
  @Override
  public String getFullName() {
    return m_FullName;
  }

  /**
   * Sets the coordinates.
   *
   * @param value	the coordinates
   */
  public void setGeometryCoordinates(GeoLocation[][] value) {
    m_GeometryCoordinates = value;
  }

  /**
   * Return the coordinates.
   *
   * @return		the coordinates, null if not set
   */
  @Override
  public GeoLocation[][] getGeometryCoordinates() {
    return m_GeometryCoordinates;
  }

  /**
   * Sets the geometry type.
   *
   * @param value	the type
   */
  public void setGeometryType(String value) {
    m_GeometryType = value;
  }

  /**
   * Returns the geometry type.
   *
   * @return		the type, null if not set
   */
  @Override
  public String getGeometryType() {
    return m_GeometryType;
  }

  /**
   * Sets the ID of the place.
   *
   * @param value	the ID
   */
  public void setId(String value) {
    m_Id = value;
  }

  /**
   * Returns the ID of the place.
   *
   * @return		the ID, null if not set
   */
  @Override
  public String getId() {
    return m_Id;
  }

  /**
   * Sets the name of the place.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
  }

  /**
   * Returns the name of the place.
   *
   * @return		the name, null if not set
   */
  @Override
  public String getName() {
    return m_Name;
  }

  /**
   * Sets the place type.
   *
   * @param value	the type
   */
  public void setPlaceType(String value) {
    m_PlaceType = value;
  }

  /**
   * Returns the place type.
   *
   * @return		the type, null if not set
   */
  @Override
  public String getPlaceType() {
    return m_PlaceType;
  }

  /**
   * Sets the street address.
   *
   * @param value	the address
   */
  public void setStreetAddress(String value) {
    m_StreetAddress = value;
  }

  /**
   * Returns the street address.
   *
   * @return		the address, null if not set
   */
  @Override
  public String getStreetAddress() {
    return m_StreetAddress;
  }

  /**
   * Sets the URL.
   *
   * @param value	the URL
   */
  public void setURL(String value) {
    m_URL = value;
  }

  /**
   * Returns the URL.
   *
   * @return		the URL, null if not set
   */
  @Override
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the ID + name.
   *
   * @return		the ID/Name
   */
  @Override
  public String toString() {
    return m_Id + ": " + m_Name;
  }
}
