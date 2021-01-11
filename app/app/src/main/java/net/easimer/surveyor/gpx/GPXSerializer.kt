package net.easimer.surveyor.gpx

import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.Writer
import java.text.SimpleDateFormat

class GPXSerializer(
    private val adapter: IRecordingGPXAdapter
) {
    private val ns = Namespace.getNamespace("g", "http://www.topografix.com/GPX/1/1")
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

    fun serialize(output: Writer) {
        val doc = makeDocument()

        val outter = XMLOutputter()
        outter.format = Format.getPrettyFormat()
        outter.output(doc, output)
    }

    private fun makeDocument(): Document {
        val result = Document()
        val root = makeElement("gpx")
        root.run {
            attributes.add(Attribute("version", "1.1"))
            attributes.add(Attribute("creator", "http://github.com/easimer/oe_kotlin_dev/"))
        }

        addMetadata(root)
        result.addContent(root)

        return result
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