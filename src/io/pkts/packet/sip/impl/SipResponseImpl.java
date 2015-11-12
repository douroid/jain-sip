/**
 * 
 */
package io.pkts.packet.sip.impl;

import io.pkts.buffer.Buffer;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.header.CSeqHeader;
import io.pkts.packet.sip.header.SipHeader;
import io.pkts.packet.sip.header.ViaHeader;
import java.text.ParseException;
import java.util.ListIterator;
import javax.sip.SipException;
import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentEncodingHeader;
import javax.sip.header.ContentLanguageHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.Header;

/**
 * @author jonas@jonasborjesson.com
 */
public final class SipResponseImpl extends SipMessageImpl implements SipResponse {

    private CSeqHeader cseq;

    /**
     * @param initialLine
     * @param headers
     * @param payload
     */
    public SipResponseImpl(final Buffer initialLine, final Buffer headers,
            final Buffer payload) {
        super(initialLine, headers, payload);
    }

    public SipResponseImpl(final SipResponseLine initialLine, final Buffer headers,
            final Buffer payload) {
        super(initialLine, headers, payload);
    }
    
    @Override
    public Buffer getReasonPhraseIO() {
        return getResponseLine().getReason().slice();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SipParseException
     */
    @Override
    public Buffer getMethodIO() throws SipParseException {
        return getCSeqHeader().getMethodIO();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return getResponseLine().getStatusCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProvisional() {
        return getStatus() / 100 == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return getStatus() >= 200;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccess() {
        return getStatus() / 100 == 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRedirect() {
        return getStatus() / 100 == 3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClientError() {
        return getStatus() / 100 == 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isServerError() {
        return getStatus() / 100 == 5;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGlobalError() {
        return getStatus() / 100 == 6;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean is100Trying() {
        return getStatus() == 100;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRinging() {
        return getStatus() == 180 || getStatus() == 183;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTimeout() {
        return getStatus() == 480;
    }

    @Override
    public SipResponse toResponse() throws ClassCastException {
        return this;
    }

    @Override
    public SipResponse clone() {
        throw new RuntimeException("Sorry, not implemented right now");
    }

    @Override
    public ViaHeader popViaHeader() throws SipParseException {
        final SipHeader header = popHeader(ViaHeader.NAME);
        if (header instanceof ViaHeader) {
            return (ViaHeader) header;
        }

        if (header == null) {
            return null;
        }


        final Buffer buffer = header.getValue();
        return ViaHeader.frame(buffer);
    }

    @Override
    public void setStatusCode(int statusCode) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStatusCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setReasonPhrase(String reasonPhrase) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getReasonPhrase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
