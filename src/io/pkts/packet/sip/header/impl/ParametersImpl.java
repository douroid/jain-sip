/**
 * 
 */
package io.pkts.packet.sip.header.impl;

import static io.pkts.packet.sip.impl.PreConditions.assertNotNull;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.header.Parameters;
import java.text.ParseException;
import java.util.Iterator;

import java.util.function.Supplier;


/**
 * @author jonas@jonasborjesson.com
 */
public abstract class ParametersImpl extends SipHeaderImpl implements Parameters,javax.sip.header.Parameters {

    private final ParametersSupport support;

    /**
     * 
     * @param name
     * @param params
     */
    protected ParametersImpl(final Buffer name, final Buffer params) {
        super(name, null);
        this.support = new ParametersSupport(params);
    }

    @Override
    public Buffer getParameterIO(final Buffer name) throws SipParseException {
        return this.support.getParameter(name);
    }

    @Override
    public Buffer getParameterIO(final String name) throws SipParseException {
        return this.support.getParameter(name);
    }

    @Override
    public void setParameter(final Buffer name, final Buffer value) throws SipParseException,
    IllegalArgumentException {
        this.support.setParameter(name, value);
    }

    @Override
    public void setParameter(final Buffer name, final Supplier<Buffer> value) throws SipParseException,
    IllegalArgumentException {
        assertNotNull(value);
        this.support.setParameter(name, value.get());
    }

    /**
     * Will only return the parameters. Sub-classes will have to build up the
     * rest of the buffer {@inheritDoc}
     */
    @Override
    public Buffer getValue() {
        return this.support.toBuffer();
    }

    @Override
    protected void transferValue(final Buffer dst) {
        this.support.transferValue(dst);
    }
    

    @Override
    public String getParameter(String name)
    {
        return getParameterIO(name).toString();
    }


    @Override
    public void setParameter(String name, String value) throws ParseException
    {
        setParameter(Buffers.wrap(name), Buffers.wrap(value));
    }


    public Iterator getParameterNames() {
        throw new UnsupportedOperationException();
    }


    public void removeParameter(String name) {
        throw new UnsupportedOperationException();        
    } 

}
