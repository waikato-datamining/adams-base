import adams.flow.template.AbstractScript as AbstractScript
import adams.flow.core.AbstractActor as AbstractActor
import adams.flow.source.StringConstants as StringConstants

class Template(AbstractScript):
    """
    A simple Jython template that just generates a subflow with a StringConstants actor.

    @author: FracPete (fracpete at waikato dot ac dot nz)
    @version: $Revision$
    """

    def __init__(self):
        """
        Initializes the template.
        """

        AbstractScript.__init__(self)

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        @rtype: str
        """

        return "Just generates a subflow consisting of a StringConstants actor."

    def doGenerate(self):
        """
        Returns the subflow.

        @return: the subflow
        @rtype: AbstractActor.class
        """

        return StringConstants()
