package net.easimer.surveyor

import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat

class GPXContentTests {
    private val docGen = TestGPXGenerator()
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

    @Test
    fun emptyDocumentHasMetadataElement() {
        val (doc, _) = docGen.makeEmptyDocument()
        val rootElement = assertRootElement(doc)
        val ns = rootElement.namespace

        val metadataElement = rootElement.getChild("metadata", ns)
        assertNotNull(metadataElement)
        assertElementHasName(metadataElement, "metadata")
    }

    @Test
    fun emptyDocumentHasNameElement() {
        val (doc, _) = docGen.makeEmptyDocument()
        val rootElement = assertRootElement(doc)
        val ns = rootElement.namespace

        val metadataElement = rootElement.getChild("metadata", ns)
        assertNotNull(metadataElement)
        assertElementHasName(metadataElement, "metadata")

        val nameElement = metadataElement.getChild("name", ns)
        assertNotNull(nameElement)
        assertElementHasName(nameElement, "name")
    }

    @Test
    fun trackpointsOnlyDocumentHasTrackpoints() {
        val (doc, rec) = docGen.makeTrackpointsOnlyDocument()
        val rootElement = assertRootElement(doc)
        val ns = rootElement.namespace

        val trackElement = rootElement.getChild("trk", ns)
        assertNotNull(trackElement)

        val trackSegElement = trackElement.getChild("trkseg", ns)
        assertNotNull(trackSegElement)

        val trackpointElements = trackSegElement.getChildren("trkpt", ns)
        assertNotNull(trackpointElements)
        assertTrue(trackpointElements.size > 0)

        trackpointElements.forEach {
            assertWptElement(it, ns)
            assertTrue(wptElementFoundInTrackpoints(it, rec, ns))
        }
    }

    @Test
    fun waypointsOnlyDocumentHasWaypoints() {
        val (doc, rec) = docGen.makeTrackpointsOnlyDocument()
        val rootElement = assertRootElement(doc)
        val ns = rootElement.namespace

        val waypointElements = rootElement.getChildren("wpt", ns)
        assertNotNull(waypointElements)
        assertEquals(waypointElements.size, rec.pointsOfInterest.size)

        waypointElements.forEach {
            assertWptElement(it, ns)
            assertTrue(wptElementFoundInPOIs(it, rec, ns))
        }
    }

    private fun assertRootElement(doc: Document): Element {
        assertNotNull(doc.rootElement)
        assertElementHasName(doc.rootElement, "gpx")
        return doc.rootElement
    }

    private fun assertElementHasName(elem: Element, name: String) {
        assertEquals(elem.name, name)
    }

    private fun assertWptElement(elem: Element, ns: Namespace) {
        assertNotNull(elem.getAttribute("lat"))
        assertNotNull(elem.getAttribute("lon"))
        assertNotNull(elem.getChild("time", ns))
    }

    private fun wptElementFoundInTrackpoints(wpt: Element, rec: IRecordingGPXAdapter, ns: Namespace): Boolean {
        val lat = wpt.getAttributeValue("lat").toDouble()
        val lon = wpt.getAttributeValue("lon").toDouble()
        val time = wpt.getChild("time", ns)

        return rec.trackpoints.any {
            val latMatches = lat.compareTo(it.latitude) == 0
            val lonMatches = lon.compareTo(it.longitude) == 0
            val timeMatches = time.text == dateFmt.format(it.date)
            (latMatches && lonMatches && timeMatches)
        }
    }

    private fun wptElementFoundInPOIs(wpt: Element, rec: IRecordingGPXAdapter, ns: Namespace): Boolean {
        val lat = wpt.getAttributeValue("lat").toDouble()
        val lon = wpt.getAttributeValue("lon").toDouble()
        val time = wpt.getChild("time", ns)

        return rec.pointsOfInterest.any {
            val latMatches = lat.compareTo(it.latitude) == 0
            val lonMatches = lon.compareTo(it.longitude) == 0
            val timeMatches = time.text == dateFmt.format(it.date)
            (latMatches && lonMatches && timeMatches)
        }
    }
}