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
 * TarArchiveHandler.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.io.File;
import java.util.List;

import adams.core.Utils;
import adams.core.io.TarUtils;

/**
 <!-- globalinfo-start -->
 * Offers access to tar files. Handles the following extensions: tar,tar.gz,tgz,tar.bz2
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-archive &lt;adams.core.io.PlaceholderFile&gt; (property: archive)
 * &nbsp;&nbsp;&nbsp;The archive to obtain the files from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TarArchiveHandler
  extends AbstractArchiveHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1421258971073361083L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Offers access to tar files. Handles the following extensions: "
      + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"tar", "tar.gz", "tgz", "tar.bz2"};
  }

  /**
   * Performs actual listing of files.
   *
   * @return		the stored files
   */
  @Override
  protected String[] listFiles() {
    String[]		result;
    List<File>	files;
    int			i;

    files  = TarUtils.listFiles(m_Archive, false);
    result = new String[files.size()];
    for (i = 0; i < result.length; i++)
      result[i] = files.get(i).getPath();

    return result;
  }

  /**
   * Extracts the specified file and saves it locally.
   *
   * @param archiveFile	the file in the archive to extract
   * @param outFile	the local file to store the content in
   * @return		true if successfully extracted
   */
  @Override
  protected boolean doExtract(String archiveFile, File outFile) {
    return TarUtils.decompress(m_Archive, archiveFile, outFile);
  }
}
