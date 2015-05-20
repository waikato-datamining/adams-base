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
 * BrowserHelper.java
 * Copyright (C) 2006-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.codehaus.plexus.util.FileUtils;

import adams.core.Properties;
import adams.core.management.OS;
import adams.env.BrowserDefinition;
import adams.env.Environment;

/**
 * A little helper class for browser related stuff.
 * <br><br>
 * The <code>openURL</code> method was originally based on
 * <a href="http://www.centerkey.com/java/browser/" target="_blank">Bare Bones Browser Launch</a>,
 * which has been placed in the public domain.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BrowserHelper {
  
  /** the file for the properties. */
  public final static String FILENAME = "Browser.props";

  /** the general properties. */
  protected static Properties m_Properties;

  /**
   * Default handler for hyperlinks. Opens URL in default browser.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DefaultHyperlinkListener
    implements HyperlinkListener {
    
    /**
     * Called when a hypertext link is updated.
     *
     * @param e the event responsible for the update
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e instanceof HTMLFrameHyperlinkEvent) {
	if (e.getSource() instanceof JEditorPane) {
	  JEditorPane editor = (JEditorPane) e.getSource();
	  HTMLDocument doc = (HTMLDocument) editor.getDocument();
	  doc.processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
	}
      } 
      else if (e.getEventType() == EventType.ACTIVATED) {
	try {
	  BrowserHelper.openURL(e.getURL().toString());
	} 
	catch (Exception ex) {
	  ex.printStackTrace();
	}
      }
    }
  }

  /**
   * Opens the URL in the system's default browser.
   *
   * @param url		the URL to open
   * @return		null if executed OK, otherwise error message
   */
  public static synchronized String openURL(String url) {
    return openURL(null, url);
  }

  /**
   * Opens the URL in the system's default browser.
   *
   * @param parent	the parent component
   * @param url		the URL to open
   * @return		null if executed OK, otherwise error message
   */
  public static synchronized String openURL(Component parent, String url) {
    return openURL(parent, url, true);
  }

  /**
   * Opens the URL in the system's default browser.
   *
   * @param parent	the parent component
   * @param url		the URL to open
   * @param showDialog	whether to display a dialog in case of an error or
   * 			just print the error to the console
   * @return		null if executed OK, otherwise error message
   */
  public static synchronized String openURL(Component parent, String url, boolean showDialog) {
    String	result;
    String	defBrowser;
    String[]	browsers;

    result = null;

    try {
      // do we have an explicit browser?
      defBrowser = getProperties().getPath("DefaultBrowser", "");
      if ((defBrowser.trim().length() > 0) && (FileUtils.fileExists(defBrowser))) {
	Runtime.getRuntime().exec(new String[]{defBrowser, url});
      }
      else {
	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
	  Desktop.getDesktop().browse(new URI(url));
	}
	else {
	  System.err.println("Desktop or browse action not supported, using fallback to determine browser.");

	  // Mac OS
	  if (OS.isMac()) {
	    Class fileMgr = Class.forName("com.apple.eio.FileManager");
	    Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
	    openURL.invoke(null, new Object[] {url});
	  }
	  // Windows
	  else if (OS.isWindows()) {
	    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
	  }
	  // assume Unix or Linux
	  else {
	    String browser = null;
	    browsers = getProperties().getProperty("LinuxBrowsers", "firefox").replaceAll(" ", "").split(",");
	    for (int count = 0; count < browsers.length && browser == null; count++) {
	      // look for binaries and take first that's available
	      if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor() == 0) {
		browser = browsers[count];
		break;
	      }
	    }
	    if (browser == null)
	      throw new Exception("Could not find web browser");
	    else
	      Runtime.getRuntime().exec(new String[]{browser, url});
	  }
	}
      }
    }
    catch (Exception e) {
      result = "Error attempting to launch web browser:\n" + e.getMessage();

      if (showDialog)
	GUIHelper.showErrorMessage(parent, result);
      else
	System.err.println(result);
    }

    return result;
  }

  /**
   * Opens the URL in a custom browser.
   *
   * @param parent	the parent component
   * @param cmd		the browser command-line
   * @param url		the URL to open
   * @param showDialog	whether to display a dialog in case of an error or
   * 			just print the error to the console
   * @return		null if executed OK, otherwise error message
   */
  public static synchronized String openURL(Component parent, String cmd, String url, boolean showDialog) {
    String	result;

    result = null;

    try {
      Runtime.getRuntime().exec(cmd + " " + url);
    }
    catch (Exception e) {
      result = "Error attempting to launch web browser '" + cmd + "':\n" + e.getMessage();

      if (showDialog)
	GUIHelper.showErrorMessage(parent, result);
      else
	System.err.println(result);
    }

    return result;
  }

  /**
   * Generates a label with a clickable link.
   *
   * @param url		the url of the link
   * @param text	the text to display instead of URL. if null or of
   * 			length 0 then the URL is used
   * @return		the generated label
   */
  public static JLabel createLink(String url, String text) {
    final String urlF = url;
    final JLabel result = new JLabel();
    result.setText((text == null) || (text.length() == 0) ? url : text);
    result.setToolTipText("Click to open link in browser");
    result.setForeground(Color.BLUE);
    result.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1) {
	  BrowserHelper.openURL(urlF);
	}
	else {
	  super.mouseClicked(e);
	}
      }
      @Override
      public void mouseEntered(MouseEvent e) {
	result.setForeground(Color.RED);
      }
      @Override
      public void mouseExited(MouseEvent e) {
	result.setForeground(Color.BLUE);
      }
    });

    return result;
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(BrowserDefinition.KEY);

    return m_Properties;
  }
}
