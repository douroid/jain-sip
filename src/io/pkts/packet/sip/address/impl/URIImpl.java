/**
 * 
 */
package io.pkts.packet.sip.address.impl;

import io.pkts.buffer.Buffer;
import io.pkts.packet.sip.address.URI;


/**
 * @author jonas@jonasborjesson.com
 */
public abstract class URIImpl implements URI {

    private final Buffer scheme;

    /**
     * 
     */
    public URIImpl(final Buffer scheme) {
        this.scheme = scheme;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Buffer getSchemeIO() {
        return this.scheme;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSipURI() {
        return false;
    }

    @Override
    public abstract URI clone();

    @Override
    public void getBytes(Buffer dst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getScheme() {
        return getSchemeIO().toString();
    }


}
