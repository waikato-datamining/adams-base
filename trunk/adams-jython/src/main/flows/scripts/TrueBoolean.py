import adams.flow.condition.bool.AbstractScript as AbstractScript
import adams.flow.core.AbstractActor as AbstractActor
import adams.flow.core.Token as Token

class TrueBoolean(AbstractScript):
    """
    Dummy condition that always succeeds.

    @author: fracpete (fracpete at waikato dot ac dot nz)
    @version: $Revision$
    """

    def __init__(self):
        """
        Initializes the condition.
        """

        AbstractScript.__init__(self)

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        @rtype: str
        """

        return "Dummy condition that always succeeds."

    def getQuickInfo(self):
        """
        Returns the quick info string to be displayed in the flow editor.

        @return: the quick info
        @rtype: str
        """

        return "true"

    def accepts(self):
        """
        Returns the class of objects that it accepts.

        @return: list of classes
        @rtype: list
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return [Class.forName("adams.flow.core.Unknown")]

    def setUp(self):
        """
        Configures the condition.

        @return: always None
        @rtype: str
        """

        return None

    def doEvaluate(self, owner, token):
        """
        Evaluates the condition.

        @return: always True
        @rtype: bool
        """

        return True

