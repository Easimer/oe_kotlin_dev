package net.easimer.surveyor

import net.easimer.surveyor.data.disk.entities.PointOfInterest
import net.easimer.surveyor.data.disk.entities.Trackpoint
import net.easimer.surveyor.gpx.GPXSerializer
import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import org.junit.Test
import org.junit.Assert.*
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.io.StringReader
import java.io.StringWriter
import java.time.Instant
import java.util.*

class GPXSerializationUnitTests {
    @Test
    fun trackpointsOnlyValidation() {

    }

    @Test
    fun emptyValidation() {
        val empty = object : IRecordingGPXAdapter {
            override val name: String
                get() = "Empty Test"
            override val time: Date
                get() = Date.from(Instant.now())

            override fun forEachTrackpoint(callback: (trackpoint: Trackpoint) -> Unit) {}
            override fun forEachPointOfInterest(callback: (poi: PointOfInterest) -> Unit) {}
        }

        serializeAndValidate(empty)
    }

    @Test
    fun waypointsOnlyValidation() {

    }

    private fun serializeAndValidate(a: IRecordingGPXAdapter) {
        val ser = GPXSerializer(a)
        val str = StringWriter()
        ser.serialize(str)

        val errorHandler = ValidationErrorHandler()
        val input = StringReader(str.buffer.toString())
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