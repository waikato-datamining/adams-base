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
 * NewArchive.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.io.ArchiveManager;
import adams.core.io.PlaceholderFile;
import adams.core.io.ZipArchiveManager;
import adams.flow.core.Token;
import adams.flow.sink.CloseArchive;
import adams.flow.transformer.AppendArchive;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NewArchive
  extends AbstractSimpleSource
  implements ClassCrossReference {

  private static final long serialVersionUID = 2935393981135891127L;

  /** the archive manager to use. */
  protected ArchiveManager m_Manager;

  /** the filename of the archive output. */
  protected PlaceholderFile m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Initializes the archive and forwards the data structure.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "manager", "manager",
      new ZipArchiveManager());

    m_OptionManager.add(
      "output", "output",
      new PlaceholderFile("."));
  }

  /**
   * Sets the archive manager to use.
   *
   * @param value	the manager
   */
  public void setManager(ArchiveManager value) {
    m_Manager = value;
    reset();
  }

  /**
   * Returns the current archive manager in use.
   *
   * @return 		the size in bytes
   */
  public ArchiveManager getManager() {
    return m_Manager;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String managerTipText() {
    return "The manager to use for creating the compressed archive.";
  }

  /**
   * Sets the archive output filename.
   *
   * @param value	the filename
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the archive output filename.
   *
   * @return 		the filename
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The file for the archive manager to create.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "manager", m_Manager, "manager: ");
    result += QuickInfoHelper.toString(this, "output", m_Output, ", output: ");

    return result;
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{AppendArchive.class, CloseArchive.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{ArchiveManager.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_Output.isDirectory())
	result = "Output file points to a directory: " + m_Output;
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ArchiveManager	manager;

    manager = ObjectCopyHelper.copyObject(m_Manager);
    result  = manager.initialize(m_Output);
    if (result == null)
      m_OutputToken = new Token(manager);

    return result;
  }
}
