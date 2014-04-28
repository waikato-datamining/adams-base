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
 * SimulatedUser.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.twitter;

import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

import java.net.URL;
import java.util.Date;

/**
 * For simulating tweets without using Twitter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedUser
  extends AbstractSimulatedTwitterResponse
  implements User {

  /** for serialization. */
  private static final long serialVersionUID = -6657582426914467670L;

  /** the ID. */
  protected long m_Id;

  /** the name of the user. */
  protected String m_Name;

  /** the screen name. */
  protected String m_ScreenName;

  /** the description. */
  protected String m_Description;

  /** the language. */
  protected String m_Lang;

  /** the created at. */
  protected Date m_CreatedAt;

  /** the profile image URL. */
  protected String m_BiggerProfileImageURL;

  /** the profile image URL (https). */
  protected String m_BiggerProfileImageURLHttps;

  /** the description URL entities. */
  protected URLEntity[] m_DescriptionURLEntities;

  /** the favorites count. */
  protected int m_FavouritesCount;

  /** the followers count. */
  protected int m_FollowersCount;

  /** the friends count. */
  protected int m_FriendsCount;

  /** the listed count. */
  protected int m_ListedCount;

  /** the location. */
  protected String m_Location;

  /** the profile image URL. */
  protected String m_ProfileImageURL;

  /** the profile image URL (https). */
  protected String m_ProfileImageURLHttps;

  /** the mini profile image URL. */
  protected String m_MiniProfileImageURL;

  /** the mini profile image URL (https). */
  protected String m_MiniProfileImageURLHttps;

  /** the original profile image URL. */
  protected String m_OriginalProfileImageURL;

  /** the original profile image URL (https). */
  protected String m_OriginalProfileImageURLHttps;

  /** the background color. */
  protected String m_ProfileBackgroundColor;

  /** the background image URL. */
  protected String m_ProfileBackgroundImageURL;

  /** the background image URL (https). */
  protected String m_ProfileBackgroundImageUrlHttps;

  /** the profile banner (IPad retina) URL. */
  protected String m_ProfileBannerIPadRetinaURL;

  /** the profile banner (IPad) URL. */
  protected String m_ProfileBannerIPadURL;

  /** the profile banner (mobile retina) URL. */
  protected String m_ProfileBannerMobileRetinaURL;

  /** the profile banner (mobile) URL. */
  protected String m_ProfileBannerMobileURL;

  /** the profile banner (retina) URL. */
  protected String m_ProfileBannerRetinaURL;

  /** the profile banner URL. */
  protected String m_ProfileBannerURL;

  /** the profile link color. */
  protected String m_ProfileLinkColor;

  /** the profile sidebar color. */
  protected String m_ProfileSidebarBorderColor;

  /** the profile sidebar fill color. */
  protected String m_ProfileSidebarFillColor;

  /** the profile text color. */
  protected String m_ProfileTextColor;

  /** the status. */
  protected Status m_Status;

  /** the status count. */
  protected int m_StatusesCount;

  /** the timezone. */
  protected String m_TimeZone;

  /** the UTC offset. */
  protected int m_UtcOffset;

  /** the URL. */
  protected String m_URL;

  /** the URL entity. */
  protected URLEntity m_URLEntity;

  /** whether contributors are enabled. */
  protected boolean m_ContributorsEnabled;

  /** whether follow request sent. */
  protected boolean m_FollowRequestSent;

  /** whether geolocation is enabled. */
  protected boolean m_GeoEnabled;

  /** whether the profile background is tiled. */
  protected boolean m_ProfileBackgroundTiled;

  /** whether the profile uses background image. */
  protected boolean m_ProfileUseBackgroundImage;

  /** whether the user is protected. */
  protected boolean m_Protected;

  /** whether to show all inline media. */
  protected boolean m_ShowAllInlineMedia;

  /** whether translator. */
  protected boolean m_Translator;

  /** whether verified. */
  protected boolean m_Verified;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Id                             = -1;
    m_Name                           = null;
    m_ScreenName                     = null;
    m_Description                    = null;
    m_Lang                           = null;
    m_CreatedAt                      = null;
    m_DescriptionURLEntities         = null;
    m_FavouritesCount                = 0;
    m_FollowersCount                 = 0;
    m_FriendsCount                   = 0;
    m_ListedCount                    = 0;
    m_Location                       = null;
    m_ProfileImageURL                = null;
    m_ProfileImageURLHttps           = null;
    m_BiggerProfileImageURL          = null;
    m_BiggerProfileImageURLHttps     = null;
    m_MiniProfileImageURL            = null;
    m_MiniProfileImageURLHttps       = null;
    m_OriginalProfileImageURL        = null;
    m_OriginalProfileImageURLHttps   = null;
    m_ProfileBackgroundColor         = null;
    m_ProfileBackgroundImageURL      = null;
    m_ProfileBackgroundImageUrlHttps = null;
    m_ProfileBannerIPadRetinaURL     = null;
    m_ProfileBannerIPadURL           = null;
    m_ProfileBannerMobileRetinaURL   = null;
    m_ProfileBannerMobileURL         = null;
    m_ProfileBannerRetinaURL         = null;
    m_ProfileBannerURL               = null;
    m_ProfileLinkColor               = null;
    m_ProfileSidebarBorderColor      = null;
    m_ProfileSidebarFillColor        = null;
    m_ProfileTextColor               = null;
    m_Status                         = null;
    m_StatusesCount                  = 0;
    m_TimeZone                       = null;
    m_UtcOffset                      = 0;
    m_URL                            = null;
    m_URLEntity                      = null;
    m_ContributorsEnabled            = false;
    m_FollowRequestSent              = false;
    m_GeoEnabled                     = false;
    m_ProfileBackgroundTiled         = false;
    m_ProfileUseBackgroundImage      = false;
    m_Protected                      = false;
    m_ShowAllInlineMedia             = false;
    m_Translator                     = false;
    m_Verified                       = false;
  }

  /**
   * Compares this user with the other one.
   * Uses: {@link #getId()}, {@link #getName()}
   *
   * @param o		the user to compare with
   * @return		less than 0, equal to 0, greater than 0 if this
   * 			user is less, equal or larger than the other one
   */
  @Override
  public int compareTo(User o) {
    int		result;

    result = new Long(getId()).compareTo(o.getId());

    if (result == 0) {
      if ((getName() == null) && (o.getName() == null))
	result = 0;
      else if (getName() == null)
	result = -1;
      else if (o.getName() != null)
	result = +1;
      else
	result = getName().compareTo(o.getName());
    }

    return result;
  }

  /**
   * Sets profile image URL.
   *
   * @param value	the URL
   */
  public void setBiggerProfileImageURL(String value) {
    m_BiggerProfileImageURL = value;
  }

  /**
   * Returns profile image URL.
   *
   * @return		the URL, null if not set
   */
  @Override
  public String getBiggerProfileImageURL() {
    return m_BiggerProfileImageURL;
  }

  /**
   * Sets the profile image URL (https).
   *
   * @param value	the URL
   */
  public void setBiggerProfileImageURLHttps(String value) {
    m_BiggerProfileImageURLHttps = value;
  }

  /**
   * Returns the profile image URL (https).
   *
   * @return		the URL, null if not set
   */
  @Override
  public String getBiggerProfileImageURLHttps() {
    return m_BiggerProfileImageURLHttps;
  }

  /**
   * Sets the date user was created.
   *
   * @param value	the date
   */
  public void getCreatedAt(Date value) {
    m_CreatedAt = value;
  }

  /**
   * Returns the date user was created.
   *
   * @return		the date, null if not set
   */
  @Override
  public Date getCreatedAt() {
    return m_CreatedAt;
  }

  /**
   * Sets the user description.
   *
   * @param value	the description
   */
  public void setDescription(String value) {
    m_Description = value;
  }

  /**
   * Returns the user description.
   *
   * @return		the description
   */
  @Override
  public String getDescription() {
    return m_Description;
  }

  /**
   * Returns the URLs in the description.
   *
   * @return		the URLs
   */
  @Override
  public URLEntity[] getDescriptionURLEntities() {
    return m_DescriptionURLEntities;
  }

  /**
   * Sets the favorites count.
   *
   * @param value	the count
   */
  public void setFavouritesCount(int value) {
    m_FavouritesCount = value;
  }

  /**
   * Returns the favorites count.
   *
   * @return		the count
   */
  @Override
  public int getFavouritesCount() {
    return m_FavouritesCount;
  }

  /**
   * Sets the followers count.
   *
   * @param value	the count
   */
  public void setFollowersCount(int value) {
    m_FollowersCount = value;
  }

  /**
   * Returns the followers count.
   *
   * @return		the count
   */
  @Override
  public int getFollowersCount() {
    return m_FollowersCount;
  }

  /**
   * Sets the friends count.
   *
   * @param value	the count
   */
  public void setFriendsCount(int value) {
    m_FriendsCount = value;
  }

  /**
   * Returns the friends count.
   *
   * @return		the count
   */
  @Override
  public int getFriendsCount() {
    return m_FriendsCount;
  }

  /**
   * Sets the ID of the user.
   *
   * @param value	the ID
   */
  public void setId(long value) {
    m_Id = value;
  }

  /**
   * Returns the ID of the user.
   *
   * @return		the ID
   */
  @Override
  public long getId() {
    return m_Id;
  }

  /**
   * Sets the language of the user.
   *
  * @param value		the language
   */
  public void setLang(String value) {
    m_Lang = value;
  }

  /**
   * Returns the language of the user.
   *
   * @return		the language
   */
  @Override
  public String getLang() {
    return m_Lang;
  }

  /**
   * Sets the listed count.
   *
   * @param value	the count
   */
  public void setListedCount(int value) {
    m_ListedCount = value;
  }

  /**
   * Return the listed count.
   *
   * @return		the count
   */
  @Override
  public int getListedCount() {
    return m_ListedCount;
  }

  /**
   * Sets the location.
   *
   * @param value	the location
   */
  public void setLocation(String value) {
    m_Location = value;
  }

  /**
   * Returns the location.
   *
   * @return		the location
   */
  @Override
  public String getLocation() {
    return m_Location;
  }

  /**
   * Sets the URL of the mini profile image.
   *
   * @param value	the URL
   */
  public void setMiniProfileImageURL(String value) {
    m_MiniProfileImageURL = value;
  }

  /**
   * Returns the URL of the mini profile image.
   *
   * @return		the URL
   */
  @Override
  public String getMiniProfileImageURL() {
    return m_MiniProfileImageURL;
  }

  /**
   * Sets the URL of the mini profile image (https).
   *
   * @parram value	the URL
   */
  public void setMiniProfileImageURLHttps(String value) {
    m_MiniProfileImageURLHttps = value;
  }

  /**
   * Returns the URL of the mini profile image (https).
   *
   * @return		the URL
   */
  @Override
  public String getMiniProfileImageURLHttps() {
    return m_MiniProfileImageURLHttps;
  }

  /**
   * Returns the name of the user.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return m_Name;
  }

  /**
   * Sets the name of the user.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
  }

  /**
   * Returns the URL of the original profile image.
   *
   * @return		the URL
   */
  @Override
  public String getOriginalProfileImageURL() {
    return m_OriginalProfileImageURL;
  }

  /**
   * Sets the URL of the original profile image.
   *
   * @param value	the URL
   */
  public void setOriginalProfileImageURL(String value) {
    m_OriginalProfileImageURL = value;
  }

  /**
   * Sets the URL of the original profile image (https).
   *
   * @param value	the URL
   */
  public void setOriginalProfileImageURLHttps(String value) {
    m_OriginalProfileImageURLHttps = value;
  }

  /**
   * Returns the URL of the original profile image (https).
   *
   * @return		the URL
   */
  @Override
  public String getOriginalProfileImageURLHttps() {
    return m_OriginalProfileImageURLHttps;
  }

  /**
   * Sets the profile background color.
   *
   * @param value	the color
   */
  public void setProfileBackgroundColor(String value) {
    m_ProfileBackgroundColor = value;
  }

  /**
   * Returns the profile background color.
   *
   * @return		the color
   */
  @Override
  public String getProfileBackgroundColor() {
    return m_ProfileBackgroundColor;
  }

  /**
   * Sets the URL of the background image.
   *
   * @param value	the URL
   */
  public void setProfileBackgroundImageURL(String value) {
    m_ProfileBackgroundImageURL = value;
  }

  /**
   * Returns the URL of the background image.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBackgroundImageURL() {
    return m_ProfileBackgroundImageURL;
  }

  /**
   * @see #getProfileBackgroundImageURL()
   */
  @Override
  public String getProfileBackgroundImageUrl() {
    return getProfileBackgroundImageURL();
  }

  /**
   * Sets the background image URL (https).
   *
   * @param value	the URL
   */
  public void setProfileBackgroundImageUrlHttps(String value) {
    m_ProfileBackgroundImageUrlHttps = value;
  }

  /**
   * Returns the background image URL (https).
   *
   * @return		the URL
   */
  @Override
  public String getProfileBackgroundImageUrlHttps() {
    return m_ProfileBackgroundImageUrlHttps;
  }

  /**
   * Sets the IPad retina profile banner URL.
   *
   * @param value	the URL
   */
  public void setProfileBannerIPadRetinaURL(String value) {
    m_ProfileBannerIPadRetinaURL = value;
  }

  /**
   * Returns the IPad retina profile banner URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBannerIPadRetinaURL() {
    return m_ProfileBannerIPadRetinaURL;
  }

  /**
   * Sets the IPad profile banner URL.
   *
   * @param value	the URL
   */
  public void setProfileBannerIPadURL(String value) {
    m_ProfileBannerIPadURL = value;
  }

  /**
   * Returns the IPad profile banner URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBannerIPadURL() {
    return m_ProfileBannerIPadURL;
  }

  /**
   * Sets the profile banner (mobile retina) URL.
   *
   * @param value	the URL
   */
  public void setProfileBannerMobileRetinaURL(String value) {
    m_ProfileBannerMobileRetinaURL = value;
  }

  /**
   * Returns the profile banner (mobile retina) URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBannerMobileRetinaURL() {
    return m_ProfileBannerMobileRetinaURL;
  }

  /**
   * Sets the profile banner (mobile) URL.
   *
   * @param value	the URL
   */
  public void setProfileBannerMobileURL(String value) {
    m_ProfileBannerMobileURL = value;
  }

  /**
   * Returns the profile banner (mobile) URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBannerMobileURL() {
    return m_ProfileBannerMobileURL;
  }

  /**
   * Sets the profile banner (retina) URL.
   *
   * @param value	the URL
   */
  public void setProfileBannerRetinaURL(String value) {
    m_ProfileBannerRetinaURL = value;
  }

  /**
   * Returns the profile banner (retina) URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBannerRetinaURL() {
    return m_ProfileBannerRetinaURL;
  }

  /**
   * Sets the profile banner URL.
   *
   * @param value	the URL
   */
  public void setProfileBannerURL(String value) {
    m_ProfileBannerURL = value;
  }

  /**
   * Returns the profile banner URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileBannerURL() {
    return m_ProfileBannerURL;
  }

  /**
   * Sets the profile image URL.
   *
   * @param value	the URL
   */
  public void setProfileImageURL(String value) {
    m_ProfileImageURL = value;
  }

  /**
   * Returns the profile image URL.
   *
   * @return		the URL
   */
  @Override
  public String getProfileImageURL() {
    return m_ProfileImageURL;
  }

  /**
   * Returns the profile image URL (https).
   *
   * @return		the URL
   */
  @Override
  public String getProfileImageURLHttps() {
    return m_ProfileImageURLHttps;
  }

  /**
   * @return		always null
   * @see		#getProfileImageURLHttps()
   */
  @Override
  public URL getProfileImageUrlHttps() {
    return null;
  }

  /**
   * Sets the profile link color.
   *
   * @param value	the color
   */
  public void setProfileLinkColor(String value) {
    m_ProfileLinkColor = value;
  }

  /**
   * Returns the profile link color.
   *
   * @return		the color
   */
  @Override
  public String getProfileLinkColor() {
    return m_ProfileLinkColor;
  }

  /**
   * Sets the profile sidebar color.
   *
   * @param value	the color
   */
  public void setProfileSidebarBorderColor(String value) {
    m_ProfileSidebarBorderColor = value;
  }

  /**
   * Returns the profile sidebar color.
   *
   * @return		the color
   */
  @Override
  public String getProfileSidebarBorderColor() {
    return m_ProfileSidebarBorderColor;
  }

  /**
   * Sets the sidebar fill color.
   *
   * @param value	the color
   */
  public void setProfileSidebarFillColor(String value) {
    m_ProfileSidebarFillColor = value;
  }

  /**
   * Returns the sidebar fill color.
   *
   * @return		the color
   */
  @Override
  public String getProfileSidebarFillColor() {
    return m_ProfileSidebarFillColor;
  }

  /**
   * Sets the profile text color.
   *
   * @param value	the color
   */
  public void setProfileTextColor(String value) {
    m_ProfileTextColor = value;
  }

  /**
   * Returns the profile text color.
   *
   * @return		the color
   */
  @Override
  public String getProfileTextColor() {
    return m_ProfileTextColor;
  }

  /**
   * Sets the screen name.
   *
   * @param value	the screen name
   */
  public void setScreenName(String value) {
    m_ScreenName = value;
  }

  /**
   * Returns the screen name.
   *
   * @return		the screen name
   */
  @Override
  public String getScreenName() {
    return m_ScreenName;
  }

  /**
   * Sets the status.
   *
   * @param value	the status
   */
  public void setStatus(Status value) {
    m_Status = value;
  }

  /**
   * Returns the status.
   *
   * @return		the status
   */
  @Override
  public Status getStatus() {
    return m_Status;
  }

  /**
   * Sets the status count.
   *
   * @param value	the count
   */
  public void setStatusesCount(int value) {
    m_StatusesCount = value;
  }

  /**
   * Return the status count.
   *
   * @return		the count
   */
  @Override
  public int getStatusesCount() {
    return m_StatusesCount;
  }

  /**
   * Sets the timezone.
   *
   * @param value	the timezone
   */
  public void setTimeZone(String value) {
    m_TimeZone = value;
  }

  /**
   * Returns the timezone.
   *
   * @return		the timezone
   */
  @Override
  public String getTimeZone() {
    return m_TimeZone;
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
   * @return		the URL
   */
  @Override
  public String getURL() {
    return m_URL;
  }

  /**
   * Sets the URL entity.
   *
   * @param value	the entity
   */
  public void setURLEntity(URLEntity value) {
    m_URLEntity = value;
  }

  /**
   * Returns the URL entity.
   *
   * @return		the entity
   */
  @Override
  public URLEntity getURLEntity() {
    return m_URLEntity;
  }

  /**
   * Sets the UTC offset.
   *
   * @param value	the offset
   */
  public void setUtcOffset(int value) {
    m_UtcOffset = value;
  }

  /**
   * Returns the UTC offset.
   *
   * @return		the offset
   */
  @Override
  public int getUtcOffset() {
    return m_UtcOffset;
  }

  /**
   * Sets whether contributors are enabled.
   *
   * @param value	true if enabled
   */
  public void setContributorsEnabled(boolean value) {
    m_ContributorsEnabled = value;
  }

  /**
   * Returns whether contributors are enabled.
   *
   * @return		true if enabled
   */
  @Override
  public boolean isContributorsEnabled() {
    return m_ContributorsEnabled;
  }

  /**
   * Sets whether follow request was sent.
   *
   * @param value	true if sent
   */
  public void setFollowRequestSent(boolean value) {
    m_FollowRequestSent = value;
  }

  /**
   * Returns whether follow request was sent.
   *
   * @return		true if sent
   */
  @Override
  public boolean isFollowRequestSent() {
    return m_FollowRequestSent;
  }

  /**
   * Sets whether geolocation is enabled.
   *
   * @param value	true if enabled
   */
  public void setGeoEnabled(boolean value) {
    m_GeoEnabled = value;
  }

  /**
   * Returns whether geolocation is enabled.
   *
   * @return		true if enabled
   */
  @Override
  public boolean isGeoEnabled() {
    return m_GeoEnabled;
  }

  /**
   * Sets whether the profile background is tiled.
   *
   * @param value	true if tiled
   */
  public void setProfileBackgroundTiled(boolean value) {
    m_ProfileBackgroundTiled = value;
  }

  /**
   * Returns whether the profile background is tiled.
   *
   * @return		true if tiled
   */
  @Override
  public boolean isProfileBackgroundTiled() {
    return m_ProfileBackgroundTiled;
  }

  /**
   * Sets whether the profile uses a background image.
   *
   * @param value	true if image in use
   */
  public void setProfileUseBackgroundImage(boolean value) {
    m_ProfileUseBackgroundImage = value;
  }

  /**
   * Returns whether the profile uses a background image.
   *
   * @return		true if image in use
   */
  @Override
  public boolean isProfileUseBackgroundImage() {
    return m_ProfileUseBackgroundImage;
  }

  /**
   * Sets whether the user is protected.
   *
   * @param value	true if protected
   */
  public void setProtected(boolean value) {
    m_Protected = value;
  }

  /**
   * Returns whether the user is protected.
   *
   * @return		true if protected
   */
  @Override
  public boolean isProtected() {
    return m_Protected;
  }

  /**
   * Sets whether all inline media is shown.
   *
   * @param value	true if shown
   */
  public void setShowAllInlineMedia(boolean value) {
    m_ShowAllInlineMedia = value;
  }

  /**
   * Returns whether all inline media is shown.
   *
   * @return		true if shown
   */
  @Override
  public boolean isShowAllInlineMedia() {
    return m_ShowAllInlineMedia;
  }

  /**
   * Sets whether translator.
   *
   * @param value	true if translator
   */
  public void setTranslator(boolean value) {
    m_Translator = value;
  }

  /**
   * Returns whether translator.
   *
   * @return		true if translator
   */
  @Override
  public boolean isTranslator() {
    return m_Translator;
  }

  /**
   * Returns whether user is verified.
   *
   * @return		true if verified
   */
  @Override
  public boolean isVerified() {
    return m_Verified;
  }

  /**
   * Returns ID and name of user.
   *
   * @return		ID and name
   */
  @Override
  public String toString() {
    return getId() + ": " + getName();
  }
}
