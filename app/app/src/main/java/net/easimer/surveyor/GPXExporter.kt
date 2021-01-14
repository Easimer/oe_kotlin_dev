package net.easimer.surveyor

import net.easimer.surveyor.gpx.GPXSerializer
import net.easimer.surveyor.gpx.IRecordingGPXAdapter
import java.io.OutputStream

/**
 * Takes an [IRecordingGPXAdapter], serializes it to GPX format and writes it to a stream.
 * @param outputStream Stream to write the GPX data to.
 * @param adapter Recording-GPX adapter; see source of [ReplayMapTrackpointSource] for an example
 */
class GPXExporter(
    private val outputStream: OutputStream,
    private val adapter: IRecordingGPXAdapter) {
    /**
     * Serialize the recording and write it to the stream. This will close the stream.
     */
    fun export() {
        val ser = GPXSerializer(adapter)
        ser.makeDocumentAndSerialize(outputStream.writer())
        outputStream.close()
    }
}