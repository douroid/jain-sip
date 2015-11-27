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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.function.Supplier;

/**
 * @author jonas@jonasborjesson.com
 */
public abstract class ParametersImpl extends SipHeaderImpl implements Parameters, javax.sip.header.Parameters {

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
    public final void setParameter(final Buffer name, final Buffer value) throws SipParseException,
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
    public final String getParameter(String name) {
        try {
            Buffer paramValue = getParameterIO(Buffers.wrap(name));
            if (paramValue != null) {
                return paramValue.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void setParameter(String name, String value) throws ParseException {
        try {
            setParameter(Buffers.wrap(name), Buffers.wrap(value));
        } catch (Exception e ) {
            throw new ParseException(name, -1);
        }
    }

    @Override
    public final Iterator getParameterNames() {
        Set<Map.Entry<Buffer, Buffer>> allParameters = support.getAllParameters();
        List<String> paramNames = new ArrayList();
        for (Map.Entry<Buffer, Buffer> mEntry : allParameters){
            paramNames.add(mEntry.getKey().toString());
        }
        return paramNames.iterator();
    }

    @Override
    public final void removeParameter(String name) {
        throw new UnsupportedOperationException();
    }

}
