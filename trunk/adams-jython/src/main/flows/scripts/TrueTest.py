import adams.flow.condition.test.AbstractScript as AbstractScript

class TrueTest(AbstractScript):
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

    def performTest(self):
        """
        Returns always None, i.e., the test succeeds.

        @return: always None
        @rtype: str
        """

        return None

