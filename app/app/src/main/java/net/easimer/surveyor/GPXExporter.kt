package net.easimer.surveyor

import net.easimer.surveyor.gpx.GPXSerializer
import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import java.io.OutputStream

class GPXExporter(
    private val outputStream: OutputStream,
    private val adapter: IRecordingGPXAdapter) {
    fun export() {
        val ser = GPXSerializer(adapter)
        ser.makeDocumentAndSerialize(outputStream.writer())
        outputStream.close()
    }
}