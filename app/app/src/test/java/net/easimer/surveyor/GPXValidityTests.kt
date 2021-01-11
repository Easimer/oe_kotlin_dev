package net.easimer.surveyor

import org.jdom2.Document
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.junit.Test
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.io.StringReader
import java.io.StringWriter

class GPXValidityTests {
    private val docGen = TestGPXGenerator()

    @Test
    fun trackpointsOnlyValidation() {

    }

    @Test
    fun emptyValidation() {
        val doc = docGen.makeEmptyDocument()
        serializeAndValidate(doc)
    }

    @Test
    fun waypointsOnlyValidation() {

    }

    private fun serializeAndValidate(doc: Document) {
        val strWriter = StringWriter()
        val outter = XMLOutputter()
        outter.format = Format.getPrettyFormat()
        outter.output(doc, strWriter)

        val errorHandler = ValidationErrorHandler()
        val input = StringReader(strWriter.buffer.toString())
        val validator = GPXValidation(input, errorHandler)
        validator.validate()
    }

    private class ValidationErrorHandler : ErrorHandler {
        override fun warning(p0: SAXParseException?) {
            p0?.let {
                p0.printStackTrace()
            }
        }

        override fun error(p0: SAXParseException?) {
            p0?.let {
                throw p0
            }
        }

        override fun fatalError(p0: SAXParseException?) {
            p0?.let {
                throw p0
            }
        }
    }

}