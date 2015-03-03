import adams.core.Placeholders as Placeholders
import adams.flow.core.Unknown as Unknown
import adams.flow.sink.AbstractScript as AbstractScript

import java.lang.Class as Class

class SimpleSink(AbstractScript):
    """
    A simple Jython sink that just dumps the incoming data to a file.

    @author: FracPete (fracpete at waikato dot ac dot nz)
    @version: $Revision$
    """

    def __init__(self):
        """
        Initializes the actor.
        """

        AbstractScript.__init__(self)

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        @rtype: str
        """

        return "Just dumps the incoming data into a file."

    def accepts(self):
        """
        Returns the class of objects that it accepts.

        @return: Unknown.class
        @rtype: list
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return [Class.forName("adams.flow.core.Unknown")]

    def doExecute(self):
        """
        Executes the flow item.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        defValue = "${TMP}/dumpfile.txt"
        filename = self.getAdditionalOptions().getString("output", defValue)
        filename = Placeholders.expandStr(filename)
        f = open(filename, "a+")
        f.write(str(self.m_InputToken.getPayload()) + "\n")
        f.close()

        return None
