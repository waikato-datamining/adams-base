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
 * FileInfoHandler.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.ByteFormat;
import adams.core.DateUtils;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.KeyValuePairTableModel;
import adams.gui.core.SortableAndSearchableTable;

import java.awt.BorderLayout;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Displays basic information about files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileInfoHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8930638838922218410L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays basic information about files.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{MATCH_ALL};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel createPreview(File file) {
    BasePanel			result;
    KeyValuePairTableModel	model;
    SortableAndSearchableTable	table;
    Object[][]			data;
    Path 			path;
    String			hidden;
    String			owner;
    String			posix;

    result = new BasePanel(new BorderLayout());
    path   = file.toPath();
    try {
      hidden = "" + Files.isHidden(path);
    }
    catch (Exception e) {
      hidden = "N/A";
    }
    try {
      owner = "" + Files.getOwner(path, LinkOption.NOFOLLOW_LINKS);
    }
    catch (Exception e) {
      owner = "N/A";
    }
    try {
      posix = "" + Files.getPosixFilePermissions(path, LinkOption.NOFOLLOW_LINKS);
    }
    catch (Exception e) {
      posix = "N/A";
    }
    data   = new Object[][]{
      {"Name", file.getName()},
      {"Path", file.getParent()},
      {"Size", ByteFormat.toBestFitBytes(file.length(), 1) + " (" + new DecimalFormat("###,###").format(file.length()) + " bytes)"},
      {"Last modified", DateUtils.getTimestampFormatter().format(new Date(file.lastModified()))},
      {"Hidden", hidden},
      {"Executable", Files.isExecutable(path)},
      {"Readable", Files.isReadable(path)},
      {"Writeable", Files.isWritable(path)},
      {"Regular file", Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)},
      {"Symbolic link", Files.isSymbolicLink(path)},
      {"Owner", owner},
      {"POSIX file permissions", posix},
    };
    model  = new KeyValuePairTableModel(data);
    table  = new SortableAndSearchableTable(model);
    table.setShowSimplePopupMenus(true);
    table.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    result.add(new BaseScrollPane(table));

    return new PreviewPanel(result, table);
  }
}
