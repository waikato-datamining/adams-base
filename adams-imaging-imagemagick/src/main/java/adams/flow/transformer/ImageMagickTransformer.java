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
 * ImageMagickTransformer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Arrays;

import adams.core.Shortening;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.data.jai.JAIHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Converts an image using ImageMagick.<br>
 * In order to make entering of commands easier, they can be spread over multiple lines and line comments can be inserted as well (a line comment starts with '#').<br>
 * <br>
 * NB: Uses im4java, i.e., ImageMagick (http:&#47;&#47;www.imagemagick.org&#47;) executables must be available on the PATH.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageMagickTransformer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-commands &lt;adams.core.base.BaseText&gt; (property: commands)
 * &nbsp;&nbsp;&nbsp;The ImageMagick commands to execute.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagickTransformer
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the start of a line comment. */
  public final static String COMMENT = "#";

  /** the commands to execute. */
  protected BaseText m_Commands;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Converts an image using ImageMagick.\n"
      + "In order to make entering of commands easier, they can be spread over "
      + "multiple lines and line comments can be inserted as well (a line "
      + "comment starts with '" + COMMENT + "').\n\n"
      + "NB: Uses im4java, i.e., ImageMagick (http://www.imagemagick.org/) "
      + "executables must be available on the PATH.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "commands", "commands",
	    new BaseText());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "commands", Shortening.shortenEnd(m_Commands.stringValue(), 100));
  }

  /**
   * Sets the commands to execute.
   *
   * @param value	the commands
   */
  public void setCommands(BaseText value) {
    m_Commands = value;
    reset();
  }

  /**
   * Returns the commands in execute.
   *
   * @return		the commands
   */
  public BaseText getCommands() {
    return m_Commands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandsTipText() {
    return "The ImageMagick commands to execute.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.image.BufferedImageContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
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
      if (!ImageMagickHelper.isConvertAvailable())
	result = ImageMagickHelper.getMissingConvertErrorMessage();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    BufferedImageContainer	img;
    ConvertCmd			cmd;
    IMOperation			op;
    StringBuilder		cmds;
    String[]			lines;
    Stream2BufferedImage 	s2b;
    BufferedImageContainer	cont;

    result = null;

    try {
      img = JAIHelper.toBufferedImageContainer((AbstractImageContainer) m_InputToken.getPayload());

      op = new IMOperation();
      op.addImage();  // input
      if (!m_Commands.isEmpty()) {
	lines = m_Commands.getValue().split("\n");
	cmds  = new StringBuilder();
	for (String line: lines) {
	  line = line.trim();
	  if (line.length() == 0)
	    continue;
	  if (line.startsWith(COMMENT))
	    continue;
	  if (cmds.length() > 0)
	    cmds.append(" ");
	  cmds.append(line);
	}
	op.addRawArgs(Arrays.asList(OptionUtils.splitOptions(cmds.toString())));
      }
      op.addImage("-");  // output

      s2b = new Stream2BufferedImage();

      cmd = new ConvertCmd();
      cmd.setOutputConsumer(s2b);
      cmd.run(op, img.getImage());

      cont = (BufferedImageContainer) img.getHeader();
      cont.setImage(s2b.getImage());
      m_OutputToken = new Token(cont);
      updateProvenance(m_OutputToken);
    }
    catch (Exception e) {
      result = handleException("Failed to transform image: ", e);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }
}
