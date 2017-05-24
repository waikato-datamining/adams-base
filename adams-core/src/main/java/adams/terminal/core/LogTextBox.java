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
 * LogTextBox.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.core;

import adams.core.logging.LoggingLevel;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextBox;

/**
 * Simple log text box.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTextBox
  extends TextBox {

  /**
   * Default constructor, this creates a single-line {@code LogTextBox} of size 10 which is initially empty
   */
  public LogTextBox() {
    super();
  }

  /**
   * Constructor that creates a {@code LogTextBox} with an initial content and attempting to be big enough to display
   * the whole text at once without scrollbars
   * @param initialContent Initial content of the {@code LogTextBox}
   */
  public LogTextBox(String initialContent) {
    super(initialContent);
  }

  /**
   * Creates a {@code LogTextBox} that has an initial content and attempting to be big enough to display the whole text
   * at once without scrollbars.
   *
   * @param initialContent Initial content of the {@code LogTextBox}
   * @param style Forced style instead of auto-detecting
   */
  public LogTextBox(String initialContent, Style style) {
    super(initialContent, style);
  }

  /**
   * Creates a new empty {@code LogTextBox} with a specific size
   * @param preferredSize Size of the {@code LogTextBox}
   */
  public LogTextBox(TerminalSize preferredSize) {
    super(preferredSize);
  }

  /**
   * Creates a new empty {@code LogTextBox} with a specific size and style
   * @param preferredSize Size of the {@code LogTextBox}
   * @param style Style to use
   */
  public LogTextBox(TerminalSize preferredSize, Style style) {
    super(preferredSize, style);
  }

  /**
   * Creates a new empty {@code LogTextBox} with a specific size and initial content
   * @param preferredSize Size of the {@code LogTextBox}
   * @param initialContent Initial content of the {@code LogTextBox}
   */
  public LogTextBox(TerminalSize preferredSize, String initialContent) {
    super(preferredSize, initialContent);
  }

  /**
   * Main constructor of the {@code LogTextBox} which decides size, initial content and style
   * @param preferredSize Size of the {@code LogTextBox}
   * @param initialContent Initial content of the {@code LogTextBox}
   * @param style Style to use for this {@code LogTextBox}, instead of auto-detecting
   */
  public LogTextBox(TerminalSize preferredSize, String initialContent, Style style) {
    super(preferredSize, initialContent, style);
  }

  /**
   * Appends the given string.
   *
   * @param level	the logging level
   * @param msg		the message to append
   */
  public void append(LoggingLevel level, String msg) {
    addLine(msg);
  }

  /**
   * Adds a single line to the {@code TextBox} at the end, this only works when in multi-line mode
   * @param line Line to add at the end of the content in this {@code TextBox}
   * @return Itself
   */
  public synchronized TextBox addLine(String line) {
    TerminalPosition  	pos;
    boolean		moveToEnd;

    pos       = getCaretPosition();
    moveToEnd = (pos.getRow() == getLineCount() - 1);

    super.addLine(line);

    if (moveToEnd)
      setCaretPosition(getLineCount() - 1, 0);

    return this;
  }

  /**
   * Clears the log.
   */
  public void clear() {
    setText("");
  }
}
