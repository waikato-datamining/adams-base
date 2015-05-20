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
  *    PostscriptWriter.java
  *    Copyright (C) 2005,2009,2015 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import adams.core.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 <!-- globalinfo-start -->
 * Outputs EPS files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 *         The file to save the image to.
 *         default: .
 * </pre>
 *
 * <pre>-scaling (property: scalingEnabled)
 *         If set to true, then scaling will be used.
 * </pre>
 *
 * <pre>-scale-x &lt;double&gt; (property: XScale)
 *         The scaling factor for the X-axis.
 *         default: 1.0
 * </pre>
 *
 * <pre>-scale-y &lt;double&gt; (property: YScale)
 *         The scaling factor for the Y axis.
 *         default: 1.0
 * </pre>
 *
 * <pre>-custom-dimensions (property: useCustomDimensions)
 *         Whether to use custom dimensions or use the component's ones.
 * </pre>
 *
 * <pre>-custom-width &lt;int&gt; (property: customWidth)
 *         The custom width.
 *         default: -1
 * </pre>
 *
 * <pre>-custom-height &lt;int&gt; (property: customHeight)
 *         The custom height.
 *         default: -1
 * </pre>
 *
 <!-- options-end -->
 * <br><br>
 * <b>Note:</b><br>
 * This writer does not work with Components that rely on clipping, like e.g.
 * scroll lists. Here the complete list is printed, instead of only in the
 * borders of the scroll list (may overlap other components!). This is due to
 * the way, clipping is handled in Postscript. There was no easy way around
 * this issue. :-(
 * <br><br>
 * Based on weka.gui.visualize.PostscriptWriter
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see PostscriptGraphics
 */
public class PostscriptWriter
  extends ScalableComponentWriter {

  /** for serialization. */
  private static final long serialVersionUID = -8501713979661829063L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Outputs EPS files.";
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   *
   * @return		the description
   */
  public String getDescription() {
    return "Postscript-File";
  }

  /**
   * returns the extensions (incl. ".") of the output format, to use in the
   * FileChooser.
   *
   * @return		the extensions
   */
  public String[] getExtensions() {
    return new String[]{".eps"};
  }

  /**
   * generates the actual output.
   *
   * @throws Exception	if something goes wrong
   */
  public void generateOutput() throws Exception {
    BufferedOutputStream	ostrm;
    FileOutputStream		fos;
    PostscriptGraphics		psg;

    ostrm = null;
    fos = null;
    try {
      fos = new FileOutputStream(getFile().getAbsoluteFile());
      ostrm = new BufferedOutputStream(fos);
      psg = new PostscriptGraphics(getComponent().getHeight(), getComponent().getWidth(), ostrm);
      psg.setFont(getComponent().getFont());
      psg.scale(getXScale(), getYScale());
      getComponent().printAll(psg);
      psg.finished();
    }
    catch (Exception e) {
      System.err.println(e);
    }
    finally {
      FileUtils.closeQuietly(ostrm);
      FileUtils.closeQuietly(fos);
    }
  }
}
