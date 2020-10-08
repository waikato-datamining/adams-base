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
 * BaseHyperlinkListener.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * Default handler for hyperlinks. Opens URL in default browser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseHyperlinkListener
  implements HyperlinkListener {

  /**
   * Called when a hypertext link is updated.
   *
   * @param e the event responsible for the update
   */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    String	url;

    url = null;
    if (e.getURL() != null)
      url = "" + e.getURL();

    if (e instanceof HTMLFrameHyperlinkEvent) {
      if (e.getSource() instanceof JEditorPane) {
	JEditorPane editor = (JEditorPane) e.getSource();
	HTMLDocument doc = (HTMLDocument) editor.getDocument();
	doc.processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
      }
    }
    else if (e.getEventType() == EventType.ACTIVATED) {
      try {
	if (url != null)
	  BrowserHelper.openURL(url);
      }
      catch (Exception ex) {
        ConsolePanel.getSingleton().append("Failed to open URL: " + url, ex);
      }
    }
  }
}
