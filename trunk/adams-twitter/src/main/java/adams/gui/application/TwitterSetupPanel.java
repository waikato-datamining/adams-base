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
 * TwitterSetupPanel.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.net.TwitterHelper;
import adams.gui.core.ParameterPanel;

/**
 * Panel for configuring the system-wide twitter settings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TwitterSetupPanel
  extends AbstractPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7937644706618374284L;

  /** the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the consumer key. */
  protected JTextField m_TextConsumerKey;

  /** the consumer secret. */
  protected JPasswordField m_TextConsumerSecret;

  /** Whether to show the consumer secret. */
  protected JCheckBox m_CheckBoxShowConsumerSecret;

  /** the access token. */
  protected JTextField m_TextAccessToken;

  /** the access token secret. */
  protected JPasswordField m_TextAccessTokenSecret;

  /** Whether to show the access token secret. */
  protected JCheckBox m_CheckBoxShowAccessTokenSecret;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelParameters = new ParameterPanel();
    add(m_PanelParameters, BorderLayout.CENTER);

    m_TextConsumerKey = new JTextField(20);
    m_TextConsumerKey.setText(TwitterHelper.getConsumerKey());
    m_PanelParameters.addParameter("Consumer _key", m_TextConsumerKey);

    m_TextConsumerSecret = new JPasswordField(20);
    m_TextConsumerSecret.setText(TwitterHelper.getConsumerSecret().getValue());
    m_TextConsumerSecret.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelParameters.addParameter("Consumer _secret", m_TextConsumerSecret);

    m_CheckBoxShowConsumerSecret = new JCheckBox();
    m_CheckBoxShowConsumerSecret.setSelected(false);
    m_CheckBoxShowConsumerSecret.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_CheckBoxShowConsumerSecret.isSelected())
	  m_TextConsumerSecret.setEchoChar((char) 0);
	else
	  m_TextConsumerSecret.setEchoChar(Constants.PASSWORD_CHAR);
      }
    });
    m_PanelParameters.addParameter("Show consumer secret", m_CheckBoxShowConsumerSecret);

    m_TextAccessToken = new JTextField(20);
    m_TextAccessToken.setText(TwitterHelper.getAccessToken());
    m_PanelParameters.addParameter("_Access token", m_TextAccessToken);

    m_TextAccessTokenSecret = new JPasswordField(20);
    m_TextAccessTokenSecret.setText(TwitterHelper.getAccessTokenSecret().getValue());
    m_TextAccessTokenSecret.setEchoChar(Constants.PASSWORD_CHAR);
    m_PanelParameters.addParameter("Access _token secret", m_TextAccessTokenSecret);

    m_CheckBoxShowAccessTokenSecret = new JCheckBox();
    m_CheckBoxShowAccessTokenSecret.setSelected(false);
    m_CheckBoxShowAccessTokenSecret.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_CheckBoxShowAccessTokenSecret.isSelected())
	  m_TextAccessTokenSecret.setEchoChar((char) 0);
	else
	  m_TextAccessTokenSecret.setEchoChar(Constants.PASSWORD_CHAR);
      }
    });
    m_PanelParameters.addParameter("Show access token secret", m_CheckBoxShowAccessTokenSecret);
  }

  /**
   * Turns the parameters in the GUI into a properties object.
   *
   * @return		the properties
   */
  protected Properties toProperties() {
    Properties	result;

    result = new Properties();

    result.setProperty(TwitterHelper.CONSUMER_KEY, m_TextConsumerKey.getText());
    result.setProperty(TwitterHelper.CONSUMER_SECRET, m_TextConsumerSecret.getText());
    result.setProperty(TwitterHelper.ACCESS_TOKEN, m_TextAccessToken.getText());
    result.setProperty(TwitterHelper.ACCESS_TOKEN_SECRET, m_TextAccessTokenSecret.getText());

    return result;
  }

  /**
   * The title of the preference panel.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Twitter";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return true;
  }
  
  /**
   * Activates the twitter setup.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    boolean	result;

    result = TwitterHelper.writeProperties(toProperties());
    if (result)
      return null;
    else
      return "Failed to save twitter setup to " + TwitterHelper.FILENAME + "!";
  }
}
