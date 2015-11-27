/**
 * 
 */
package io.pkts.packet.sip.header.impl;

import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.header.SipHeader;
import io.pkts.packet.sip.impl.SipParser;

import java.util.function.Function;


/**
 * @author jonas@jonasborjesson.com
 */
public class SipHeaderImpl implements SipHeader, javax.sip.header.Header {

    private final Buffer name;

    private final Buffer value;

    /**
     * 
     */
    public SipHeaderImpl(final Buffer name, final Buffer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Subclasses may override this one and are in fact encourage to do so
     * 
     * {@inheritDoc}
     */
    @Override
    public Buffer getNameIO() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getNameIO().toString() + ": " + getValue();
    }

    @Override
    public void verify() throws SipParseException {
        // by default, everything is assumed to be correct.
        // Subclasses should override this method and
        // check that everything is ok...

    }

    /**
     * If this method actually gets called it means that we are the {@inheritDoc}
     */
    @Override
    public SipHeader ensure() {
        final Function<SipHeader, ? extends SipHeader> framer = SipParser.framers.get(this.name);
        if (framer != null) {
            return framer.apply(this);
        }
        return this;
    }

    @Override
    public void getBytes(final Buffer dst) {
        this.name.getBytes(0, dst);
        dst.write(SipParser.COLON);
        dst.write(SipParser.SP);
        transferValue(dst);
    }

    /**
     * Transfer the bytes of the value into the destination. Sub-classes should
     * override this method.
     * 
     * @param dst
     */
    protected void transferValue(final Buffer dst) {
        final Buffer value = getValue();
        value.getBytes(0, dst);
    }

    @Override
    public SipHeader clone() {
        final Buffer buffer = Buffers.createBuffer(DEFAULT_BUFFER_SIZE);
        transferValue(buffer);
        return new SipHeaderImpl(this.name.clone(), buffer);
    }

    @Override
    public String getName() {
        return getNameIO().toString();
    }

}
