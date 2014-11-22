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
  *    JComponentWriter.java
  *    Copyright (C) 2005-2014 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JComponent;

import adams.core.ClassLister;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * This class takes any JComponent and outputs it to a file. Scaling is by
 * default enabled. Derived classes only need to override the following
 * methods:
 * <ul>
 *   <li><code>getDescription()</code></li>
 *   <li><code>getExtension()</code></li>
 *   <li><code>generateOutput()</code></li>
 *   <li><code></code></li>
 * </ul>
 * <p/>
 * Based on weka.gui.visualize.JComponentWriter
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class JComponentWriter
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3764183253552888494L;

  /** the component to print in the output format. */
  protected transient JComponent m_Component;

  /** whether the writer is enabled. */
  protected boolean m_Enabled;
  
  /** the file to write the output stream to. */
  protected PlaceholderFile m_OutputFile;

  /** whether to use custom dimensions. */
  protected boolean m_UseCustomDimensions;

  /** the custom width. */
  protected int m_CustomWidth;

  /** the custom height. */
  protected int m_CustomHeight;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "enabled", "enabled",
	    true);

    m_OptionManager.add(
	    "file", "file",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "custom-dimensions", "useCustomDimensions",
	    false);

    m_OptionManager.add(
	    "custom-width", "customWidth",
	    -1);

    m_OptionManager.add(
	    "custom-height", "customHeight",
	    -1);
  }

  /**
   * sets the component to print to an output format.
   *
   * @param c 		the component to print
   */
  public void setComponent(JComponent c) {
    m_Component = c;
  }

  /**
   * returns the component that is stored in the output format.
   *
   * @return 		the component to print
   */
  public JComponent getComponent() {
    return m_Component;
  }

  /**
   * Sets whether the writer is enabled.
   *
   * @param value 	true if to enable writer
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the writer is enabled.
   *
   * @return 		true if writer is enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "Whether the writer is enabled.";
  }

  /**
   * sets the file to store the output in.
   *
   * @param value 	the file to store the output in
   */
  public void setFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * returns the file being used for storing the output.
   *
   * @return 		the file to store the output in
   */
  public PlaceholderFile getFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileTipText() {
    return "The file to save the image to.";
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   * must be overridden in the derived class.
   *
   * @return 		the name of the writer
   */
  public abstract String getDescription();

  /**
   * returns the extensions (incl. ".") of the output format, to use in the
   * FileChooser. Must be overridden in the derived class.
   *
   * @return 		the file extensions
   */
  public abstract String[] getExtensions();

  /**
   * sets whether to use custom dimensions for the image.
   *
   * @param value 	whether custom dimensions are used
   */
  public void setUseCustomDimensions(boolean value) {
    m_UseCustomDimensions = value;
    reset();
  }

  /**
   * whether custom dimensions are to used for the size of the image.
   *
   * @return 		true if custom dimensions are used
   */
  public boolean getUseCustomDimensions() {
    return m_UseCustomDimensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomDimensionsTipText() {
    return "Whether to use custom dimensions or use the component's ones.";
  }

  /**
   * sets the custom width to use.
   *
   * @param value 	the width to use
   * @see 		#m_UseCustomDimensions
   */
  public void setCustomWidth(int value) {
    m_CustomWidth = value;
    reset();
  }

  /**
   * gets the custom width currently used.
   *
   * @return 		the custom width currently used
   * @see 		#m_UseCustomDimensions
   */
  public int getCustomWidth() {
    return m_CustomWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customWidthTipText() {
    return "The custom width.";
  }

  /**
   * sets the custom height to use.
   *
   * @param value 	the height to use
   * @see 		#m_UseCustomDimensions
   */
  public void setCustomHeight(int value) {
    m_CustomHeight = value;
    reset();
  }

  /**
   * gets the custom height currently used.
   *
   * @return 		the custom height currently used
   * @see 		#m_UseCustomDimensions
   */
  public int getCustomHeight() {
    return m_CustomHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customHeightTipText() {
    return "The custom height.";
  }

  /**
   * generates the actual output.
   *
   * @throws Exception	if something goes wrong
   */
  protected abstract void generateOutput() throws Exception;

  /**
   * saves the current component to the currently set file.<p>
   * <b>Note:</b> this method calls <code>generateOutput()</code> which needs
   * to be overriden in subclasses!
   * @throws Exception      if either the file or the component is <code>null</code>
   */
  public void toOutput() throws Exception {
    int		oldWidth;
    int		oldHeight;

    if (!m_Enabled)
      return;
    
    if (getFile() == null)
      throw new Exception("The file is not set!");
    if (getFile().isDirectory())
      throw new Exception("The file points to a directory: " + getFile());
    if (getComponent() == null)
      throw new Exception("The component is not set!");

    // backup original dimensions and set custom ones if necessary
    oldWidth  = getComponent().getWidth();
    oldHeight = getComponent().getHeight();
    if (getUseCustomDimensions())
      getComponent().setSize(getCustomWidth(), getCustomHeight());

    generateOutput();

    // restore original dimensions
    if (getUseCustomDimensions())
      getComponent().setSize(oldWidth, oldHeight);
  }

  /**
   * outputs the given component with the given writer in the specified file.
   *
   * @param writer	the writer to use
   * @param comp	the component to output
   * @param file	the file to store the output in
   * @throws Exception  if component of file are <code>null</code>
   */
  public static void toOutput(JComponentWriter writer, JComponent comp, File file) throws Exception {
    toOutput(writer, comp, file, -1, -1);
  }

  /**
   * outputs the given component with the given writer in the specified file.
   * If width and height are different from -1 then these sizes are used,
   * otherwise the current ones of the component
   *
   * @param writer	the writer to use
   * @param comp	the component to output
   * @param file	the file to store the output in
   * @param width	custom width, -1 uses the component's one
   * @param height	custom height, -1 uses the component's one
   * @throws Exception  if component or file are <code>null</code>
   */
  public static void toOutput(JComponentWriter writer, JComponent comp, File file, int width, int height) throws Exception {
    writer.setComponent(comp);
    writer.setFile(new PlaceholderFile(file));

    // custom dimensions?
    if ((width != -1) && (height != -1)) {
      writer.setUseCustomDimensions(true);
      writer.setCustomWidth(width);
      writer.setCustomHeight(height);
    }

    writer.toOutput();
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return		the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(JComponentWriter.class);
  }

  /**
   * Returns an array with all the available extension file filters.
   * Only adds writers that produce actual output.
   *
   * @return		the filters
   */
  public static JComponentWriterFileFilter[] getFileFilters() {
    Vector<JComponentWriterFileFilter>	result;
    String[]				writerNames;
    int					i;
    Class				cls;
    JComponentWriter			writer;
    JComponentWriterFileFilter		filter;

    result = new Vector<JComponentWriterFileFilter>();

    // determine all available writers and add them to the filechooser
    writerNames = JComponentWriter.getWriters();
    Arrays.sort(writerNames);
    for (i = 0; i < writerNames.length; i++) {
      try {
        cls    = Class.forName(writerNames[i]);
        writer = (JComponentWriter) cls.newInstance();
        if (writer.getExtensions().length > 0) {
          filter = new JComponentWriterFileFilter(
              writer.getExtensions(),
              writer.getDescription(),
              writer);
          result.add(filter);
        }
      }
      catch (Exception e) {
        System.err.println(writerNames[i] + ": " + e);
      }
    }

    return result.toArray(new JComponentWriterFileFilter[result.size()]);
  }

  /**
   * Returns the appropriate writer for the given file. Defaults to first
   * writer it finds, if extension cannot be matched.
   *
   * @param file	the file to get writer for
   * @return		the writer
   */
  public static JComponentWriter getWriterForFile(File file) {
    return getWriterForFile(file.getAbsolutePath());
  }

  /**
   * Returns the appropriate writer for the given filename. Defaults to first
   * writer it finds, if extension cannot be matched.
   *
   * @param filename	the filename to get writer for
   * @return		the writer
   */
  public static JComponentWriter getWriterForFile(String filename) {
    return getWriterForExtension(filename.replaceAll("^.*\\.", ""));
  }

  /**
   * Returns the appropriate writer for the given extension. Defaults to first
   * writer it finds, if extension cannot be matched.
   *
   * @param ext		the file extension (with or without ".")
   * @return		the writer
   */
  public static JComponentWriter getWriterForExtension(String ext) {
    JComponentWriter			result;
    JComponentWriterFileFilter[]	filters;
    int					i;
    int					n;
    String[]				extensions;

    ext     = ext.replaceAll("^\\.", "").toLowerCase();
    filters = getFileFilters();
    result  = null;

    // try find matching extension
    for (i = 0; i < filters.length; i++) {
      extensions = filters[i].getExtensions();
      for (n = 0; n < extensions.length; n++) {
	if (extensions[n].toLowerCase().equals(ext)) {
	  result = filters[i].getWriter();
	  break;
	}
      }
      if (result != null)
	break;
    }

    // no match found?
    if (result == null)
      result = filters[0].getWriter();

    return result;
  }

  /**
   * Instantiates the writer with the given options.
   *
   * @param classname	the classname of the writer to instantiate
   * @param options	the options for the writer
   * @return		the instantiated writer or null if an error occurred
   */
  public static JComponentWriter forName(String classname, String[] options) {
    JComponentWriter	result;

    try {
      result = (JComponentWriter) OptionUtils.forName(JComponentWriter.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the writer from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			writer to instantiate
   * @return		the instantiated writer
   * 			or null if an error occurred
   */
  public static JComponentWriter forCommandLine(String cmdline) {
    return (JComponentWriter) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    String[] writers = getWriters();
    for (int i = 0; i < writers.length; i++)
      System.out.println((i+1) + ". " + writers[i]);
    String filename = "hello.png";
    System.out.println(filename + " -> " + getWriterForFile(filename).getClass().getName());
    filename = "hello.JPG";
    System.out.println(filename + " -> " + getWriterForFile(filename).getClass().getName());
  }
}
