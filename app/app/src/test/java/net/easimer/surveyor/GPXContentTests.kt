package net.easimer.surveyor

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Assert.*
import org.junit.Test

class GPXContentTests {
    private val docGen = TestGPXGenerator()

    @Test
    fun emptyDocumentHasMetadataElement() {
        val doc = docGen.makeEmptyDocument()
        val rootElement = assertRootElement(doc)
        val ns = rootElement.namespace

        val metadataElement = rootElement.getChild("metadata", ns)
        assertNotEquals(metadataElement, null)
        assertElementHasName(metadataElement, "metadata")
    }

    @Test
    fun emptyDocumentHasNameElement() {
        val doc = docGen.makeEmptyDocument()
        val rootElement = assertRootElement(doc)
        val ns = rootElement.namespace

        val metadataElement = rootElement.getChild("metadata", ns)
        assertNotEquals(metadataElement, null)
        assertElementHasName(metadataElement, "metadata")

        val nameElement = metadataElement.getChild("name", ns)
        assertNotEquals(nameElement, null)
        assertElementHasName(nameElement, "name")
    }

    private fun assertRootElement(doc: Document): Element {
        assertNotEquals(doc.rootElement, null)
        assertElementHasName(doc.rootElement, "gpx")
        return doc.rootElement
    }

    private fun assertElementHasName(elem: Element, name: String) {
        assertEquals(elem.name, name)
    }
}