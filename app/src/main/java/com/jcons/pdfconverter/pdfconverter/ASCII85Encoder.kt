package com.jcons.pdfconverter.pdfconverter

import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * This class encodes a binary stream into a text stream.
 *
 *
 *
 * The ASCII85encoding is suitable when binary data needs to be transmitted or stored as text. It has been defined by
 * Adobe for the PostScript and PDF formats (see PDF Reference, section 3.3 Details of Filtered Streams).
 *
 *
 *
 *
 * The encoded stream is about 25% larger than the corresponding binary stream (32 binary bits are converted into 40
 * encoded bits, and there may be start/end of line markers).
 *
 *
 */
class ASCII85Encoder : FilterOutputStream {
    /**
     * Create an encoder wrapping a sink of binary data.
     *
     *
     * Calling this constructor is equivalent to calling [ ASCII85Encoder(&lt;code&gt;out&lt;/code&gt;, &lt;code&gt;-1&lt;/code&gt;, &lt;code&gt;null&lt;/code&gt;, &lt;code&gt;null&lt;/code&gt;)][.ASCII85Encoder].
     *
     *
     * @param out sink of binary data to filter
     */
    constructor(out: OutputStream?) : super(out) {
        lineLength = -1
        c1 = -1
        phase = 4
    }

    /**
     * Closes this output stream and releases any system resources associated with the stream.
     *
     * @throws IOException if the underlying stream throws one
     */
    @Throws(IOException::class)
    override fun close() {
        if (c1 >= 0) {
            c4 += c5 / 85
            c3 += c4 / 85
            c2 += c3 / 85
            c1 += c2 / 85

            // output only the required number of bytes
            putByte(33 + c1)
            putByte(33 + c2 % 85)
            if (phase > 1) {
                putByte(33 + c3 % 85)
                if (phase > 2) {
                    putByte(33 + c4 % 85)
                    if (phase > 3) {
                        putByte(33 + c5 % 85)
                    }
                }
            }

            // output the end marker
            putByte('~'.toInt())
            putByte('>'.toInt())
        }

        // end the last line properly
        if (length != 0) {
            out.write(eol, 0, eol!!.size)
        }

        // close the underlying stream
        out.close()
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b byte to write (only the 8 low order bits are used)
     */
    @Throws(IOException::class)
    override fun write(b: Int) {
        var b = b
        b = b and 0xff
        when (phase) {
            1 -> {
                c3 += 9 * b
                c4 += 6 * b
                c5 += b
                phase = 2
            }
            2 -> {
                c4 += 3 * b
                c5 += b
                phase = 3
            }
            3 -> {
                c5 += b
                phase = 4
            }
            else -> {
                if (c1 >= 0) {
                    // there was a preceding quantum, we now know it was not the last
                    if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 0 && c5 == 0) {
                        putByte('z'.toInt())
                    } else {
                        c4 += c5 / 85
                        c3 += c4 / 85
                        c2 += c3 / 85
                        c1 += c2 / 85
                        putByte(33 + c1)
                        putByte(33 + c2 % 85)
                        putByte(33 + c3 % 85)
                        putByte(33 + c4 % 85)
                        putByte(33 + c5 % 85)
                    }
                }
                c1 = 0
                c2 = 27 * b
                c3 = c2
                c4 = 9 * b
                c5 = b
                phase = 1
            }
        }
    }

    /**
     * Put a byte in the underlying stream, inserting line breaks as needed.
     *
     * @param b byte to put in the underlying stream (only the 8 low order bits are used)
     * @throws IOException if the underlying stream throws one
     */
    @Throws(IOException::class)
    private fun putByte(b: Int) {
        if (lineLength >= 0) {
            // split encoded lines if needed
            if (length == 0 && sol != null) {
                out.write(sol, 0, sol!!.size)
                length = sol!!.size
            }
            out.write(b)
            if (++length >= lineLength) {
                out.write(eol, 0, eol!!.size)
                length = 0
            }
        } else {
            out.write(b)
        }
    }

    /**
     * Line length (not counting eol).
     */
    private var lineLength: Int

    /**
     * Start of line marker (indentation).
     */
    private var sol: ByteArray? = null

    /**
     * End Of Line marker.
     */
    private var eol: ByteArray? = null

    /**
     * Coefficients of the 32-bits quantum in base 85.
     */
    private var c1: Int
    private var c2 = 0
    private var c3 = 0
    private var c4 = 0
    private var c5 = 0

    /**
     * Phase (between 1 and 4) of raw bytes.
     */
    private var phase: Int

    /**
     * Current length of the line being written.
     */
    private var length = 0
}