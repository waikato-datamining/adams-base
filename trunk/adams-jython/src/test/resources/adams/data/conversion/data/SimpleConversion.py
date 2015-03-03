import adams.data.conversion.AbstractScript as AbstractScript

import java.lang.Class as Class

class SimpleConversion(AbstractScript):
    """
    A simple Jython conversion that just divides the incoming doubles by 100.

    @author: FracPete (fracpete at waikato dot ac dot nz)
    @version: $Revision$
    """

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        @rtype: str
        """

        return "Just divides the incoming doubles by 100."

    def accepts(self):
        """
        Returns the class of objects that it accepts.

        @return: Double.class
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return Class.forName("java.lang.Double")

    def generates(self):
        """
        Returns the class of objects that it generates.

        @return: Double.class
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return Class.forName("java.lang.Double")

    def doConvert(self):
        """
        Performs the conversion.

        @return: the converted object
        @rtype: object
        """

        return self.m_Input / 100
