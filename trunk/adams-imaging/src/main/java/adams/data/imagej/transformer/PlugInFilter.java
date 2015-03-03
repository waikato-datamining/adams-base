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
 * PlugInFilter.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2011 Albert Cardona (automating ImageJ dialog)
 */

package adams.data.imagej.transformer;

import ij.IJ;
import ij.Macro;
import ij.WindowManager;
import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.data.imagej.ImagePlusContainer;

/**
 <!-- globalinfo-start -->
 * A transformer that allows the use of ImageJ's plug-in filters.<br/>
 * Notes:<br/>
 * - the filter must implement 'ij.plugin.filter.PlugInFilter'<br/>
 * - the filter can pop-up a window to enter additional parameters,<br/>
 *   the first time it is executed, depending on the filter implementation<br/>
 *   (you can suppress this with supplying the options manually)<br/>
 * - the manual options string can be obtained when recording macros<br/>
 *   in ImageJ<br/>
 * - the filter must be in the classpath of the application
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
 * <pre>-filter &lt;java.lang.String&gt; (property: plugInFilter)
 * &nbsp;&nbsp;&nbsp;The classname of the plug-in filter to use.
 * &nbsp;&nbsp;&nbsp;default: ij.plugin.filter.FFTFilter
 * </pre>
 *
 * <pre>-suppress-plugin-dialog (property: suppressPlugInDialog)
 * &nbsp;&nbsp;&nbsp;If enabled, the plugin dialog is suppressed using the supplied plugin options.
 * </pre>
 *
 * <pre>-plugin-options &lt;java.lang.String&gt; (property: plugInOptions)
 * &nbsp;&nbsp;&nbsp;The options for the plug-in if the dialog gets suppressed.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2011 Albert Cardona",
    author = "Albert Cardona",
    license = License.PUBLIC_DOMAIN,
    url = "http://albert.rierol.net/imagej_programming_tutorials.html#How%20to%20automate%20an%20ImageJ%20dialog"
)
public class PlugInFilter
  extends AbstractImageJTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2700141722155551567L;

  /** the classname of the plugin-filter. */
  protected String m_PlugInFilter;

  /** the actual filter. */
  protected ij.plugin.filter.PlugInFilter m_ActualFilter;

  /** whether to suppress the plugin dialog. */
  protected boolean m_SuppressPlugInDialog;

  /** the options for the plugin (if dialog suppressed). */
  protected String m_PlugInOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A transformer that allows the use of ImageJ's plug-in filters.\n"
      + "Notes:\n"
      + "- the filter must implement 'ij.plugin.filter.PlugInFilter'\n"
      + "- the filter can pop-up a window to enter additional parameters,\n"
      + "  the first time it is executed, depending on the filter implementation\n"
      + "  (you can suppress this with supplying the options manually)\n"
      + "- the manual options string can be obtained when recording macros\n"
      + "  in ImageJ\n"
      + "- the filter must be in the classpath of the application";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "plugInFilter",
	    "ij.plugin.filter.FFTFilter");

    m_OptionManager.add(
	    "suppress-plugin-dialog", "suppressPlugInDialog",
	    false);

    m_OptionManager.add(
	    "plugin-options", "plugInOptions",
	    "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualFilter = null;
  }

  /**
   * Sets the classname of the plugin filter to use.
   *
   * @param value 	the classname
   */
  public void setPlugInFilter(String value) {
    m_PlugInFilter = value;
    reset();
  }

  /**
   * Returns the classname of the plugin filter in use.
   *
   * @return 		the classname
   */
  public String getPlugInFilter() {
    return m_PlugInFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plugInFilterTipText() {
    return "The classname of the plug-in filter to use.";
  }

  /**
   * Sets whether to suppress the plugin dialog.
   *
   * @param value 	true if to suppress the dialog
   */
  public void setSuppressPlugInDialog(boolean value) {
    m_SuppressPlugInDialog = value;
    reset();
  }

  /**
   * Returns whether to suppress the plugin dialog.
   *
   * @return 		true if the dialog is suppressed
   */
  public boolean getSuppressPlugInDialog() {
    return m_SuppressPlugInDialog;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppressPlugInDialogTipText() {
    return "If enabled, the plugin dialog is suppressed using the supplied plugin options.";
  }

  /**
   * Sets the options for the plug-in if the dialog gets suppressed.
   *
   * @param value 	the options
   */
  public void setPlugInOptions(String value) {
    m_PlugInOptions = value;
    reset();
  }

  /**
   * Returns the options for the plug-in if the dialog gets suppressed.
   *
   * @return 		the options
   */
  public String getPlugInOptions() {
    return m_PlugInOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plugInOptionsTipText() {
    return "The options for the plug-in if the dialog gets suppressed.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "plugInFilter", m_PlugInFilter);
  }

  /**
   * Returns the filter to use.
   *
   * @param img		the image used for setting up
   * @return		the filter
   */
  protected ij.plugin.filter.PlugInFilter getFilter(ImagePlusContainer img) {
    int		ijResult;

    if (m_ActualFilter == null) {
      try {
	m_ActualFilter = (ij.plugin.filter.PlugInFilter) Class.forName(m_PlugInFilter).newInstance();
	ijResult = m_ActualFilter.setup(m_PlugInOptions, img.getImage());
	if (ijResult != 0)
	  img.getNotes().addError(
	      getClass(),
	      "ImageJ call of '" + m_PlugInFilter + ".setup(...)' returned " + ijResult);
      }
      catch (Exception e) {
	throw new IllegalStateException(
	    "Failed to instantiate ImageJ plugin filter '" + m_PlugInFilter + "'!", e);
      }
    }

    return m_ActualFilter;
  }

  /**
   * Performs no transformation at all, just returns the input.
   * <p/>
   * Automating an ImageJ dialog taken from
   * <a href="http://albert.rierol.net/imagej_programming_tutorials.html#How%20to%20automate%20an%20ImageJ%20dialog" target="_blank">here</a>.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected ImagePlusContainer[] doTransform(ImagePlusContainer img) {
    ImagePlusContainer[]		result;
    ij.plugin.filter.PlugInFilter	filter;
    String				threadName;
    Thread				thread;

    if (m_SuppressPlugInDialog) {
      thread     = Thread.currentThread();
      threadName = thread.getName();
      thread.setName("Run$_" + hashCode());
      Macro.setOptions(thread, m_PlugInOptions);
      IJ.runPlugIn(img.getImage(), m_PlugInFilter, "");
      Macro.setOptions(thread, null);
      thread.setName(threadName);
    }
    else {
      filter = getFilter(img);
      filter.run(img.getImage().getProcessor());
    }

    result    = new ImagePlusContainer[1];
    result[0] = (ImagePlusContainer) img.getHeader();
    result[0].setImage(img.getImage());
    
    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    WindowManager.closeAllWindows();

    super.cleanUp();
  }
}
