/**
 *
 */
package io.pkts.packet.sip.header.impl;

import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.header.CSeqHeader;
import io.pkts.packet.sip.impl.SipParser;
import static java.lang.Math.toIntExact;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/**
 * @author jonas@jonasborjesson.com
 *
 */
public final class CSeqHeaderImpl extends SipHeaderImpl implements CSeqHeader {

    private final long cseqNumber;
    private final Buffer method;

    /**
     *
     */
    public CSeqHeaderImpl(final long cseqNumber, final Buffer method, final Buffer value) {
        super(CSeqHeader.NAME, value);
        this.cseqNumber = cseqNumber;
        this.method = method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer getMethodIO() {
        return this.method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSeqNumber() {
        return this.cseqNumber;
    }

    @Override
    public Buffer getValue() {
        if (super.getValue() != null) {
            return super.getValue();
        }

        final int size = Buffers.stringSizeOf(this.cseqNumber);
        final Buffer value = Buffers.createBuffer(size + 1 + this.method.getReadableBytes());
        value.writeAsString(this.cseqNumber);
        value.write(SipParser.SP);
        this.method.getBytes(value);
        return value;
    }

    @Override
    public CSeqHeader clone() {
        return new CSeqHeaderImpl(this.cseqNumber, this.method.clone(), getValue().clone());
    }

    @Override
    public CSeqHeader ensure() {
        return this;
    }

    @Override
    public void setMethod(String method) throws ParseException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String methodStr = null;

    @Override
    public String getMethod() {
        if (methodStr == null) {
            methodStr = getMethodIO().toString();
        }
        return methodStr;
    }

    @Override
    public void setSequenceNumber(int sequenceNumber) throws InvalidArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSequenceNumber() {
        return toIntExact(getSeqNumber());
    }

    @Override
    public void setSeqNumber(long sequenceNumber) throws InvalidArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
