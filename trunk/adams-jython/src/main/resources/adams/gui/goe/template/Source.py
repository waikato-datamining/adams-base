import adams.flow.core.Token as Token
import adams.flow.core.Unknown as Unknown
import adams.flow.source.AbstractScript as AbstractScript

import java.lang.Class as Class
import java.util.Random as Random

class TemplateSource(AbstractScript):
    """
    Template of a Jython source.

    @author: FracPete (fracpete at waikato dot ac dot nz)
    @version: $Revision$
    """

    def __init__(self):
        """
        Initializes the actor.
        """

        AbstractScript.__init__(self)

        self._Tokens = None
        """ contains all the tokens to send. """

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        @rtype: str
        """

        return "FIXME."

    def setUp(self):
        """
        Initializes the item for flow execution.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        result = AbstractScript.setUp(self)
        if (result == None):
            self._Tokens = []
        return result

    def generates(self):
        """
        Returns the class of objects that it generates.

        @return: the classes
        @rtype: list
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return [Class.forName("java.lang.Object")]  # FIXME

    def doExecute(self):
        """
        Executes the flow item.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        # FIXME
        return None

    def output(self):
        """
        Returns the generated token.

        @return: the generated token
        @rtype: Token
        """

        result = self._Tokens[0]
        del self._Tokens[0]
        return result

    def hasPendingOutput(self):
        """
        Checks whether there is pending output to be collected after
        executing the flow item.

        @return: true if there is pending output
        @rtype: bool
        """

        return (len(self._Tokens) > 0)

    def wrapUp(self):
        """
        Cleans up after the execution has finished.
        """

        AbstractScript.wrapUp(self)
        self._Tokens = None
