/**
 * 
 */
package io.pkts.packet.sip.header.impl;

import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.address.Address;
import io.pkts.packet.sip.header.FromHeader;
import java.text.ParseException;


/**
 * @author jonas@jonasborjesson.com
 */
public class FromHeaderImpl extends AddressParametersHeaderImpl implements FromHeader {

    /**
     * @param name
     * @param address
     * @param params
     */
    public FromHeaderImpl(final Address address, final Buffer params) {
        super(FromHeader.NAME, address, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer getTagIO() throws SipParseException {
        return getParameterIO(TAG);
    }

    @Override
    public FromHeader clone() {
        final Buffer buffer = Buffers.createBuffer(DEFAULT_BUFFER_SIZE);
        transferValue(buffer);
        try {
            return FromHeader.frame(buffer);
        } catch (final SipParseException e) {
            throw new RuntimeException("Unable to clone the From-header", e);
        }
    }

    @Override
    public FromHeader ensure() {
        return this;
    }

    @Override
    public void setTag(String tag) throws ParseException {
        setParameter(TAG, Buffers.wrap(tag));
    }

    private String tagStr = null;
    @Override
    public String getTag() {
        if (tagStr == null) {
        tagStr = getTagIO().toString();
        }
        return tagStr;
    }

}
