import adams.flow.condition.test.AbstractScript as AbstractScript

class SimpleCondition(AbstractScript):
    """
    A simple Jython condition that always succceeds.

    @author: FracPete (fracpete at waikato dot ac dot nz)
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

        return "The condition always succeeds."

    def performTest(self):
        """
        Performs the actual testing of the condition.

        @return: the test result, null if everything OK, otherwise the error message
        @rtype: str
        """

        return None
