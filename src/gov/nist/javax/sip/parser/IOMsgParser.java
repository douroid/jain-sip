/*
 * Conditions Of Use
 *
 * This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 Untied States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 *
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof, including but
 * not limited to the correctness, accuracy, reliability or usefulness of
 * the software.
 *
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement
 *
 * .
 *
 */

/*******************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD)        *
 ******************************************************************************/

package gov.nist.javax.sip.parser;

import gov.nist.core.CommonLogger;
import gov.nist.core.Host;
import gov.nist.core.HostNameParser;
import gov.nist.core.StackLogger;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.address.TelephoneNumber;
import gov.nist.javax.sip.header.ExtensionHeaderImpl;
import gov.nist.javax.sip.header.NameMap;
import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.StatusLine;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPRequestImpl;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.message.SIPResponseImpl;
import io.pkts.buffer.Buffer;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.impl.SipInitialLine;
import io.pkts.packet.sip.impl.SipRequestImpl;
import io.pkts.packet.sip.impl.SipRequestLine;
import io.pkts.packet.sip.impl.SipResponseImpl;
import io.pkts.packet.sip.impl.SipResponseLine;
import io.sipstack.netty.codec.sip.RawMessage;
import static io.sipstack.netty.codec.sip.SipMessageStreamDecoder.MAX_ALLOWED_CONTENT_LENGTH;
import static io.sipstack.netty.codec.sip.SipMessageStreamDecoder.MAX_ALLOWED_HEADERS_SIZE;
import static io.sipstack.netty.codec.sip.SipMessageStreamDecoder.MAX_ALLOWED_INITIAL_LINE_SIZE;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
/*
 * Acknowledgement: 1/12/2007: Yanick Belanger rewrote the parsing loops to make them
 * simpler and quicker.
 */


/**
 * Parse SIP message and parts of SIP messages such as URI's etc from memory and
 * return a structure. Intended use: UDP message processing. This class is used
 * when you have an entire SIP message or SIPHeader or SIP URL in memory and you
 * want to generate a parsed structure from it. For SIP messages, the payload
 * can be binary or String. If you have a binary payload, use
 * parseSIPMessage(byte[]) else use parseSIPMessage(String) The payload is
 * accessible from the parsed message using the getContent and getContentBytes
 * methods provided by the SIPMessage class. If SDP parsing is enabled using the
 * parseContent method, then the SDP body is also parsed and can be accessed
 * from the message using the getSDPAnnounce method. Currently only eager
 * parsing of the message is supported (i.e. the entire message is parsed in one
 * feld swoop).
 *
 *
 * @version 1.2 $Revision: 1.28 $ $Date: 2010-05-06 14:07:44 $
 *
 * @author M. Ranganathan <br/>
 *
  *
 */
public class IOMsgParser implements MessageParser {
    
    private static StackLogger logger = CommonLogger.getLogger(IOMsgParser.class);

    /**
     * @since v0.9
     */
    public IOMsgParser() {
        super();
    }

    /**
     * Parse a buffer containing a single SIP Message where the body is an array
     * of un-interpreted bytes. This is intended for parsing the message from a
     * memory buffer when the buffer. Incorporates a bug fix for a bug that was
     * noted by Will Sullin of Callcast
     *
     * @param msgBuffer
     *            a byte buffer containing the messages to be parsed. This can
     *            consist of multiple SIP Messages concatenated together.
     * @return a SIPMessage[] structure (request or response) containing the
     *         parsed SIP message.
     * @exception ParseException
     *                is thrown when an illegal message has been encountered
     *                (and the rest of the buffer is discarded).
     * @see ParseExceptionListener
     */
    @Override
    public SIPMessage parseSIPMessage(byte[] msgBuffer, boolean readBody, boolean strict, ParseExceptionListener parseExceptionListener) throws ParseException {
        RawMessage rawMessage = new RawMessage(MAX_ALLOWED_INITIAL_LINE_SIZE, MAX_ALLOWED_HEADERS_SIZE,
                MAX_ALLOWED_CONTENT_LENGTH);   
        try {
            for (int i = 0 ; i < msgBuffer.length; i++) {
                rawMessage.write(msgBuffer[i]);
            }
            return toSipMessage(rawMessage);
        } catch (Exception e) {
            throw new ParseException(null, MAX_ALLOWED_HEADERS_SIZE);
        }
    }
    
    private SIPMessage toSipMessage(final RawMessage raw) {
        final SipInitialLine initialLine = SipInitialLine.parse(raw.getInitialLine());
        final Buffer headers = raw.getHeaders();
        final Buffer payload = raw.getPayload();
        SIPMessage msg =  null;
        if (initialLine.isRequestLine()) {
            SipRequestImpl sipRequestImpl = new SipRequestImpl((SipRequestLine) initialLine, headers, payload);
            msg = new SIPRequestImpl(sipRequestImpl);
        } else {
            SipResponseImpl sipResponseImpl = new SipResponseImpl((SipResponseLine) initialLine, headers, payload);
            msg = new  SIPResponseImpl(sipResponseImpl);
        }
        return msg;
    }    


}
