import adams.flow.standalone.AbstractScript as AbstractScript

class SimpleStandalone(AbstractScript):
    """
    A simple Jython standalone that just outputs some stuff on commandline.

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

        return "Just outputs some stuff on the commandline."

    def doExecute(self):
        """
        Executes the flow item.

        @return: None if everything is fine, otherwise error message
        @rtype: str
        """

        print "Hello World!"
        return None
