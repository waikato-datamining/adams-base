/*
 * A simple Groovy sink that just dumps all the incoming data in a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.core.Placeholders
import adams.flow.core.Unknown
import adams.flow.sink.AbstractScript

import java.io.BufferedWriter
import java.io.FileWriter

class SimpleSink
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Just dumps the incoming data in a file."
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		Unknown.class
   */
  public Class[] accepts() {
    def result = new Object[1]
    result[0] = Unknown.class
    return result
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    String		result
    String		defValue
    String		filename
    BufferedWriter	writer

	result = null

    try {
      defValue = "\${TMP}/dumpfile.txt"
      filename = getAdditionalOptions().getString("output", defValue)
      filename = Placeholders.expandStr(filename)
      writer = new BufferedWriter(new FileWriter(filename, true));
      writer.write("" + m_InputToken.getPayload())
      writer.newLine()
      writer.flush()
      writer.close()
    }
    catch (Exception e) {
      result = "Failed to write data to '" + filename + "':\n" + e.toString()
      getSystemErr().printStackTrace(e);
    }

    return result
  }
}