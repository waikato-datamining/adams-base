/*
 * SimulatedURLEntity.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.twitter;

import twitter4j.URLEntity;

import java.io.Serializable;

/**
 * Encapsulates a URL entity for simulating tweets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimulatedURLEntity
  implements URLEntity, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -2761614263115585919L;

  /** the URL. */
  protected String m_URL;

  /** the expaneded URL. */
  protected String m_ExpandedURL;

  /** the display URL. */
  protected String m_DisplayURL;

  /** the start index in the tweet. */
  protected int m_Start;

  /** the end index in the tweet. */
  protected int m_End;

  /**
   * Initializes the entity.
   */
  public SimulatedURLEntity() {
    super();
    initialize();
  }

  /**
   * Initializes the entity.
   *
   * @param url		the URL to be used for all URLs
   */
  public SimulatedURLEntity(String url) {
    this();
    setURL(url);
    setExpandedURL(url);
    setDisplayURL(url);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_URL         = null;
    m_ExpandedURL = null;
    m_DisplayURL  = null;
    m_Start       = -1;
    m_End         = -1;
  }

  /**
   * Sets the URL mentioned in the tweet.
   *
   * @param value the mentioned URL
   */
  public void setURL(String value) {
    m_URL = value;
  }

  /**
   * Returns the URL mentioned in the tweet.
   *
   * @return the mentioned URL
   */
  @Override
  public String getURL() {
    return m_URL;
  }

  /**
   * Sets the expanded URL.
   *
   * @param value	the expanded URL
   */
  public void setExpandedURL(String value) {
    m_ExpandedURL = value;
  }

  /**
   * Returns the expanded URL.
   *
   * @return		the expanded URL
   */
  @Override
  public String getExpandedURL() {
    return m_ExpandedURL;
  }

  /**
   * Sets the display URL if mentioned URL is shorten.
   *
   * @param value the display URL if mentioned URL is shorten, or null if no shorten URL was mentioned.
   */
  public void setDisplayURL(String value) {
    m_DisplayURL = value;
  }

  /**
   * Returns the display URL if mentioned URL is shorten.
   *
   * @return the display URL if mentioned URL is shorten, or null if no shorten URL was mentioned.
   */
  @Override
  public String getDisplayURL() {
    return m_DisplayURL;
  }

  /**
   * Sets the index of the start character of the URL mentioned in the tweet.
   *
   * @param value the index of the start character of the URL mentioned in the tweet
   */
  public void setStart(int value) {
    m_Start = value;
  }

  /**
   * Returns the index of the start character of the URL mentioned in the tweet.
   *
   * @return the index of the start character of the URL mentioned in the tweet
   */
  @Override
  public int getStart() {
    return m_Start;
  }

  /**
   * Sets the index of the end character of the URL mentioned in the tweet.
   *
   * @param value the index of the end character of the URL mentioned in the tweet
   */
  public void setEnd(int value) {
    m_End = value;
  }

  /**
   * Returns the index of the end character of the URL mentioned in the tweet.
   *
   * @return the index of the end character of the URL mentioned in the tweet
   */
  @Override
  public int getEnd() {
    return m_End;
  }

  /**
   * Returns the URL.
   *
   * @return		the URL
   * @see		#getURL()
   */
  @Override
  public String toString() {
    return getURL();
  }
}
