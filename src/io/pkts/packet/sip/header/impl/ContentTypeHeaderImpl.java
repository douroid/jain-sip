/**
 * 
 */
package io.pkts.packet.sip.header.impl;

import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.header.ContentTypeHeader;
import java.text.ParseException;
import java.util.Iterator;


/**
 * @author jonas@jonasborjesson.com
 */
public final class ContentTypeHeaderImpl extends MediaTypeHeaderImpl implements ContentTypeHeader {

    /**
     * @param name
     * @param params
     */
    public ContentTypeHeaderImpl(final Buffer mType, final Buffer subType, final Buffer params) {
        super(ContentTypeHeader.NAME, mType, subType, params);
    }

    @Override
    public ContentTypeHeader clone() {
        final Buffer buffer = Buffers.createBuffer(1024);
        transferValue(buffer);
        try {
            return ContentTypeHeader.frame(buffer);
        } catch (final SipParseException e) {
            throw new RuntimeException("Unable to clone the ContentType-header", e);
        }
    }

    @Override
    public ContentTypeHeader ensure() {
        return this;
    }

    @Override
    public void setContentType(String contentType) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentType() {
        return getContentTypeIO().toString();
    }

    @Override
    public void setContentSubType(String contentSubType) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentSubType() {
        return getContentSubTypeIO().toString();
    }

}
