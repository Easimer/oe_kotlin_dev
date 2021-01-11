package net.easimer.surveyor.gpx

import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*

class GPXSerializer(
    private val adapter: IRecordingGPXAdapter
) {
    private val ns = Namespace.getNamespace("http://www.topografix.com/GPX/1/1")
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

    fun makeDocumentAndSerialize(output: Writer) {
        val doc = makeDocument()
        serialize(doc, output)
    }

    fun serialize(doc: Document, output: Writer) {
        val outter = XMLOutputter()
        outter.format = Format.getPrettyFormat()
        outter.output(doc, output)
    }

    fun makeDocument(): Document {
        val result = Document()
        val root = makeElement("gpx")
        root.run {
            attributes.add(Attribute("version", "1.1"))
            attributes.add(Attribute("creator", "http://github.com/easimer/oe_kotlin_dev/"))
        }

        addMetadata(root)
        addWaypoints(root)
        addTrack(root)

        result.addContent(root)

        return result
    }

    private fun addTrack(root: Element) {
        val trackElement = Element("trk", ns)
        val trackSegElement = Element("trkseg", ns)

        adapter.trackpoints.sortedBy { it.date }.forEach {
            appendWaypointTo(trackSegElement, "trkpt", it.latitude, it.longitude, it.date)
        }

        trackElement.addContent(trackSegElement)
        root.addContent(trackElement)
    }

    private fun addWaypoints(root: Element) {
        adapter.pointsOfInterest.sortedBy { it.date }.forEach {
            appendWaypointTo(root, "wpt", it.latitude, it.longitude, it.date)
        }
    }

    private fun appendWaypointTo(root: Element, name: String, lat: Double, lon: Double, time: Date) {
        val elem = Element(name, ns)
        elem.attributes.run {
            add(Attribute("lat", lat.toString()))
            add(Attribute("lon", lon.toString()))
        }
        val dateElem = Element("time", ns)
        dateElem.text = dateFmt.format(time)
        elem.addContent(dateElem)

        root.addContent(elem)
    }

    private fun addMetadata(root: Element) {
        makeElement("metadata").let { metadata ->
            makeElement("name").run {
                addContent(adapter.name)
                metadata.addContent(this)
            }
            makeElement("time").run {
                addContent(dateFmt.format(adapter.time))
                metadata.addContent(this)
            }
            root.addContent(metadata)
        }
    }

    /**
     * Make an XML element in the GPX namespace.
     */
    private fun makeElement(name: String): Element {
        val ret = Element(name)
        ret.namespace = ns
        return ret
    }
}