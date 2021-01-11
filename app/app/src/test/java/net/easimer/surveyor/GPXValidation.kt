package net.easimer.surveyor

import org.xml.sax.ContentHandler
import org.xml.sax.ErrorHandler
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.Reader
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class GPXValidation(
    private val input: Reader,
    private val errorHandler: ErrorHandler
) {
    private val GPX_SCHEMA_URI = "https://www.topografix.com/GPX/1/1/gpx.xsd"

    fun validate() {
        val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schemaLocation = ClassLoader.getSystemClassLoader().getResource("schema/gpx.xsd")
        val schema = factory.newSchema(schemaLocation)

        val spf = SAXParserFactory.newInstance()
        spf.isValidating = false
        spf.schema = schema
        spf.isNamespaceAware = true

        val xmlReader = spf.newSAXParser().xmlReader
        xmlReader.errorHandler = errorHandler
        xmlReader.parse(InputSource(input))
    }
}