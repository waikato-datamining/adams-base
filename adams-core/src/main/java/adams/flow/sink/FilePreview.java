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
 * FilePreview.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.tools.previewbrowser.AbstractArchiveHandler;
import adams.gui.tools.previewbrowser.AbstractContentHandler;
import adams.gui.tools.previewbrowser.NoDataToPreviewPanel;
import adams.gui.tools.previewbrowser.NoPreviewAvailablePanel;
import adams.gui.tools.previewbrowser.PlainTextHandler;
import adams.gui.tools.previewbrowser.PreviewPanel;
import adams.gui.tools.previewbrowser.PropertiesManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Actor for previewing file contents. Skips archives (if handlers available).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: FilePreview
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-use-custom-preview &lt;boolean&gt; (property: useCustomPreview)
 * &nbsp;&nbsp;&nbsp;If enabled the specified preview handler is used for all files rather than 
 * &nbsp;&nbsp;&nbsp;'automagically' determined.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-preview &lt;adams.gui.tools.previewbrowser.AbstractContentHandler&gt; (property: preview)
 * &nbsp;&nbsp;&nbsp;The custom preview to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.tools.previewbrowser.PlainTextHandler
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9700 $
 */
public class FilePreview
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1523870513962160664L;

  /**
   * Custom {@link DisplayPanel}.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 9700 $
   */
  public static class FilePreviewDisplayPanel
    extends AbstractComponentDisplayPanel {

    /** blah. */
    private static final long serialVersionUID = -3054275069984068238L;

    /** the owner. */
    protected FilePreview m_Owner;
    
    /** for displaying the file. */
    protected PreviewPanel m_PreviewPanel;

    /**
     * Initializes the panel.
     *
     * @param owner	the owning actor
     */
    public FilePreviewDisplayPanel(FilePreview owner) {
      super(owner.getClass().getName());
      m_Owner = owner;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();
      setLayout(new BorderLayout());
      m_PreviewPanel = new PreviewPanel(new NoDataToPreviewPanel());
      add(m_PreviewPanel, BorderLayout.CENTER);
    }

    /**
     * Displays the token.
     * 
     * @param token	the token to display
     */
    @Override
    public void display(Token token) {
      File			file;
      JPanel			parent;
      AbstractContentHandler	preview;
      
      if (token.getPayload() instanceof String)
	file = new PlaceholderFile((String) token.getPayload());
      else if (token.getPayload() instanceof File)
	file = new PlaceholderFile((File) token.getPayload());
      else
	throw new IllegalStateException("Unhandled data type: " + Utils.classToString(token.getPayload().getClass()));

      parent = (JPanel) m_PreviewPanel.getParent();
      parent.remove(m_PreviewPanel);
      m_PreviewPanel = null;
      if (!AbstractArchiveHandler.hasHandler(file)) {
	if (m_Owner.getUseCustomPreview()) {
	  m_PreviewPanel = new PreviewPanel(m_Owner.getPreview().getPreview(file));
	  parent.add(m_PreviewPanel, BorderLayout.CENTER);
	}
	else {
	  preview = PropertiesManager.getPreferredContentHandler(file);
	  if (preview != null)
	    m_PreviewPanel = new PreviewPanel(preview.getPreview(file));
	}
      }
      if (m_PreviewPanel != null)
	parent.add(m_PreviewPanel, BorderLayout.CENTER);
    }
    
    /**
     * Performs clean up operations.
     */
    @Override
    public void cleanUp() {
    }
    
    /**
     * Clears the panel.
     */
    @Override
    public void clearPanel() {
      m_PreviewPanel.removeAll();
      m_PreviewPanel.getParent().invalidate();
      m_PreviewPanel.getParent().validate();
      m_PreviewPanel.repaint();
    }
    
    /**
     * Returns the image panel.
     * 
     * @return		the panel
     */
    @Override
    public JComponent supplyComponent() {
      return m_PreviewPanel;
    }
  }
  
  /** the panel with the image. */
  protected PreviewPanel m_PreviewPanel;

  /** whether to use a custom preview. */
  protected boolean m_UseCustomPreview;

  /** the width of the image properties. */
  protected AbstractContentHandler m_Preview;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for previewing file contents. Skips archives (if handlers available).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-custom-preview", "useCustomPreview",
	    false);

    m_OptionManager.add(
	    "preview", "preview",
	    new PlainTextHandler());
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets whether to use a specific preview handler rather than trying to
   * automatically determine handler.
   *
   * @param value 	if true then use custom preview
   */
  public void setUseCustomPreview(boolean value) {
    m_UseCustomPreview = value;
    reset();
  }

  /**
   * Returns whether to use a specific preview handler rather than trying to
   * automatically determine handler..
   *
   * @return 		true if to use custom preview
   */
  public boolean getUseCustomPreview() {
    return m_UseCustomPreview;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomPreviewTipText() {
    return 
	"If enabled the specified preview handler is used for all files "
	+ "rather than 'automagically' determined.";
  }

  /**
   * Sets the custom preview handler to use.
   *
   * @param value 	the preview handler
   */
  public void setPreview(AbstractContentHandler value) {
    m_Preview = value;
    reset();
  }

  /**
   * Returns the custom preview handler to use.
   *
   * @return 		the preview handler
   */
  public AbstractContentHandler getPreview() {
    return m_Preview;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String previewTipText() {
    return "The custom preview to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = null;

    if (QuickInfoHelper.hasVariable(this, "useCustomPreview") || m_UseCustomPreview)
      result = QuickInfoHelper.toString(this, "preview", Shortening.shortenEnd(OptionUtils.getShortCommandLine(getPreview()), 40));
    else
      result = "automatic";
 
    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_PreviewPanel != null) {
      m_PreviewPanel.removeAll();
      m_PreviewPanel.getParent().invalidate();
      m_PreviewPanel.getParent().validate();
      m_PreviewPanel.repaint();
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_PreviewPanel = new PreviewPanel(new NoPreviewAvailablePanel());
    return m_PreviewPanel;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class, java.awt.image.BufferedImage.class, adams.data.image.AbstractImageContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, BufferedImage.class, AbstractImageContainer.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    File			file;
    JPanel			parent;
    AbstractContentHandler	preview;

    if (token.getPayload() instanceof String)
      file = new PlaceholderFile((String) token.getPayload());
    else if (token.getPayload() instanceof File)
      file = new PlaceholderFile((File) token.getPayload());
    else
      throw new IllegalStateException("Unhandled data type: " + Utils.classToString(token.getPayload().getClass()));

    parent = (JPanel) m_PreviewPanel.getParent();
    parent.remove(m_PreviewPanel);
    m_PreviewPanel = null;
    if (!AbstractArchiveHandler.hasHandler(file)) {
      if (getUseCustomPreview()) {
	m_PreviewPanel = new PreviewPanel(getPreview().getPreview(file));
	parent.add(m_PreviewPanel, BorderLayout.CENTER);
      }
      else {
	preview = PropertiesManager.getPreferredContentHandler(file);
	if (preview != null)
	  m_PreviewPanel = new PreviewPanel(preview.getPreview(file));
      }
    }
    if (m_PreviewPanel != null)
      parent.add(m_PreviewPanel, BorderLayout.CENTER);
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public DisplayPanel createDisplayPanel(Token token) {
    DisplayPanel	result;

    result = new FilePreviewDisplayPanel(this);
    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }
}
