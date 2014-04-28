import adams.flow.core.Token as Token
import adams.flow.core.Unknown as Unknown
import adams.flow.standalone.AbstractScript as AbstractScript

import java.lang.Class as Class
import java.util.Random as Random

class TemplateStandalone(AbstractScript):
    """
    Template of a Jython standalone.

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

        return "FIXME."

    def setUp(self):
        """
        Initializes the item for flow execution.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        result = AbstractScript.setUp(self)
        return result

    def doExecute(self):
        """
        Executes the flow item.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        # FIXME
        return None
