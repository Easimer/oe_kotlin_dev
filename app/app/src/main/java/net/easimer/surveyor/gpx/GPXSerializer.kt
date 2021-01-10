package net.easimer.surveyor.gpx

import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.Writer

class GPXSerializer(
    private val adapter: IRecordingGPXAdapter
) {
    fun serialize(output: Writer) {
        val doc = makeDocument()

        val outter = XMLOutputter()
        outter.format = Format.getPrettyFormat()
        outter.output(doc, output)
    }

    private fun makeDocument(): Document {
        val result = Document()
        val root = Element("gpx")
        root.namespace = Namespace.getNamespace("g", "http://www.topografix.com/GPX/1/1")
        root.run {
            attributes.add(Attribute("version", "1.1"))
        }

        addMetadata(root)

        result.addContent(root)

        return result
    }

    private fun addMetadata(root: Element) {
        Element("name").run {
            addContent(adapter.name)
            root.addContent(this)
        }
        Element("time").run {
            addContent(adapter.time.toString())
            root.addContent(this)
        }
    }
}