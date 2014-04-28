import adams.data.conversion.AbstractScript as AbstractScript

import java.lang.Class as Class

class TemplateConversion(AbstractScript):
    """
    Template of a Jython Conversion scheme.

    @author: FracPete (fracpete at waikato dot ac dot nz)
    @version: $Revision$
    """

    def globalInfo(self):
        """
        Returns a string describing the object.

        @return: a description suitable for displaying in the gui
        @rtype: str
        """

        return "FIXME."

    def accepts(self):
        """
        Returns the class of objects that it accepts.

        @return: the class
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return Class.forName("java.lang.Object")  # FIXME

    def generates(self):
        """
        Returns the class of objects that it generates.

        @return: the class
        """

        # very in-elegant, but works
        # http://www.prasannatech.net/2009/02/class-object-name-java-interface-jython.html
        return Class.forName("java.lang.Object")  # FIXME

    def doConvert(self):
        """
        Performs the conversion.

        @return: the converted object
        @rtype: object
        """

        return self.m_Input
