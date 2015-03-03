import adams.core.Utils as Utils
import adams.flow.core.Token as Token
import adams.flow.core.Unknown as Unknown
import adams.flow.transformer.AbstractScript as AbstractScript

import java.lang.Class as Class

class SimpleTransformer(AbstractScript):
    """
    A simple Groovy transformer that just adds a user-supplied integer to the
    integers that pass through.

    Expects an additional option called "add" with the number to add.

    @author FracPete (fracpete at waikato dot ac dot nz)
    @version $Revision$
    """

    def __init__(self):
        """
        Initializes the transformer.
        """

        AbstractScript.__init__(self)

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        """

        return "Just adds a user-supplied integer to the integers passing through."

    def accepts(self):
        """
        Returns the class of objects that it accepts.

        @return: Integer.class
        @rtype: list
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return [Class.forName("java.lang.Integer")]

    def generates(self):
        """
        Returns the class of objects that it generates.

        @return: Integer.class
        @rtype: list
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return [Class.forName("java.lang.Integer")]

    def doExecute(self):
        """
        Executes the flow item.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        input = self.m_InputToken.getPayload()
        self.m_OutputToken = Token(input + self.getAdditionalOptions().getInteger("add", 1))
        return None

