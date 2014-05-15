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
 * TwitterField.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.twitter;

/**
 * The available fields for generating the output.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum TwitterField {
  /** the ID of the tweet/status. */
  ID,
  /** the user ID. */
  USER_ID,
  /** the user name. */
  USER_NAME,
  /** the source. */
  SOURCE,
  /** the text of the tweet. */
  TEXT,
  /** the creation date. */
  CREATED,
  /** whether tweet was favorited. */
  FAVORITED,
  /** whether tweet is a retweet. */
  RETWEET,
  /** the retweet count. */
  RETWEET_COUNT,
  /** whether tweet was retweeted by me. */
  RETWEET_BY_ME,
  /** whether tweet is possibly sensitive. */
  POSSIBLY_SENSITIVE,
  /** the associated latitude. */
  GEO_LATITUDE,
  /** the associated longitude. */
  GEO_LONGITUDE,
  /** the language code. */
  LANGUAGE_CODE,
  /** the place. */
  PLACE,
  /** the place type. */
  PLACE_TYPE,
  /** the place URL. */
  PLACE_URL,
  /** the street address. */
  STREET_ADDRESS,
  /** the country. */
  COUNTRY,
  /** the country code. */
  COUNTRY_CODE,
  /** reply to status ID. */
  IN_REPLY_TO_STATUS_ID,
  /** reply to user ID. */
  IN_REPLY_TO_USER_ID,
  /** retweeted status ID. */
  RETWEETED_STATUS_ID,
  /** retweeted user ID. */
  RETWEETED_STATUS_USER_ID,
  /** retweeted timestamp. */
  RETWEETED_STATUS_CREATED,
  /** expanded URLs. */
  EXPANDED_URLS,
  /** symbol entities. */
  SYMBOL_ENTITIES,
  /** whether the status was retweeted. */
  IS_RETWEETED,
  /** how often the tweet was favorited. */
  FAVORITE_COUNT,
  /** the language of the status. */
  STATUS_LANG,
  /** the scopes. */
  SCOPES
}