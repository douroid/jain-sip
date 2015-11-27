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
/**
 * *****************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD) *
 ******************************************************************************
 */
package gov.nist.javax.sip.message;

import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.stack.SIPTransactionStack;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.SipException;

/*
 * Acknowledgements: Mark Bednarek made a few fixes to this code. Jeff Keyser added two methods
 * that create responses and generate cancel requests from incoming orignial requests without the
 * additional overhead of encoding and decoding messages. Bruno Konik noticed an extraneous
 * newline added to the end of the buffer when encoding it. Incorporates a bug report from Andreas
 * Bystrom. Szabo Barna noticed a contact in a cancel request - this is a pointless header for
 * cancel. Antonis Kyardis contributed bug fixes. Jeroen van Bemmel noted that method names are
 * case sensitive, should use equals() in getting CannonicalName
 * 
 */
/**
 * The SIP Request structure.
 *
 * @version 1.2 $Revision: 1.57 $ $Date: 2010-09-17 20:06:57 $
 * @since 1.1
 *
 * @author M. Ranganathan <br/>
 *
 *
 *
 */
public interface SIPRequest extends SIPMessageInt, javax.sip.message.Request, RequestExt {

    static final String DEFAULT_USER = "ip";

    static final String DEFAULT_TRANSPORT = "udp";

    /**
     * Set of target refresh methods, currently: INVITE, UPDATE, SUBSCRIBE,
     * NOTIFY, REFER
     *
     * A target refresh request and its response MUST have a Contact
     */
    static final Set<String> targetRefreshMethods = new HashSet<String>();

    /*
     * A table that maps a name string to its cannonical constant. This is used to speed up
     * parsing of messages .equals reduces to == if we use the constant value.
     * 
     * jeand : Setting the capacity to save on memory since the capacity here will never change
     */
    static final Map<String, String> nameTable = new ConcurrentHashMap<String, String>(15);

    static final Set<String> headersToIncludeInResponse = new HashSet<String>(0);

    static void putName(String name) {
        nameTable.put(name, name);
    }

    /**
     * @return true iff the method is a target refresh
     */
    public static boolean isTargetRefresh(String ucaseMethod) {
        return targetRefreshMethods.contains(ucaseMethod);
    }

    /**
     * @return true iff the method is a dialog creating method
     */
    public static boolean isDialogCreating(String ucaseMethod) {
        return SIPTransactionStack.isDialogCreated(ucaseMethod);
    }

    /**
     * Set to standard constants to speed up processing. this makes equals
     * comparisons run much faster in the stack because then it is just identity
     * comparision. Character by char comparison is not required. The method
     * returns the String CONSTANT corresponding to the String name.
     *
     */
    public static String getCannonicalName(String method) {

        if (nameTable.containsKey(method)) {
            return (String) nameTable.get(method);
        } else {
            return method;
        }
    }

    /**
     * Get the Request Line of the SIPRequest.
     *
     * @return the request line of the SIP Request.
     */
    public gov.nist.javax.sip.header.SipRequestLine getRequestLine();

    /**
     * Set the request line of the SIP Request.
     *
     * @param requestLine is the request line to set in the SIP Request.
     */
    public void setRequestLine(RequestLine requestLine);

    /**
     * Check header for constraints. (1) Invite options and bye requests can
     * only have SIP URIs in the contact headers. (2) Request must have cseq, to
     * and from and via headers. (3) Method in request URI must match that in
     * CSEQ.
     */
    public void checkHeaders() throws ParseException;

    /**
     * Creates a default SIPResponse message for this request. Note You must add
     * the necessary tags to outgoing responses if need be. For efficiency, this
     * method does not clone the incoming request. If you want to modify the
     * outgoing response, be sure to clone the incoming request as the headers
     * are shared and any modification to the headers of the outgoing response
     * will result in a modification of the incoming request. Tag fields are
     * just copied from the incoming request. Contact headers are removed from
     * the incoming request. Added by Jeff Keyser.
     *
     * @param statusCode Status code for the response. Reason phrase is
     * generated.
     *
     * @return A SIPResponse with the status and reason supplied, and a copy of
     * all the original headers from this request.
     */
    public SIPResponse createResponse(int statusCode);

    /**
     * Creates a default SIPResponse message for this request. Note You must add
     * the necessary tags to outgoing responses if need be. For efficiency, this
     * method does not clone the incoming request. If you want to modify the
     * outgoing response, be sure to clone the incoming request as the headers
     * are shared and any modification to the headers of the outgoing response
     * will result in a modification of the incoming request. Tag fields are
     * just copied from the incoming request. Contact headers are removed from
     * the incoming request. Added by Jeff Keyser. Route headers are not added
     * to the response.
     *
     * @param statusCode Status code for the response.
     * @param reasonPhrase Reason phrase for this response.
     *
     * @return A SIPResponse with the status and reason supplied, and a copy of
     * all the original headers from this request except the ones that are not
     * supposed to be part of the response .
     */
    public SIPResponse createResponse(int statusCode, String reasonPhrase);

    /**
     * Creates a default SIPResquest message that would cancel this request.
     * Note that tag assignment and removal of is left to the caller (we use
     * whatever tags are present in the original request).
     *
     * @return A CANCEL SIPRequest constructed according to RFC3261 section 9.1
     *
     * @throws SipException
     * @throws ParseException
     */
    public SIPRequest createCancelRequest() throws SipException;

    /**
     * Creates a default ACK SIPRequest message for this original request. Note
     * that the defaultACK SIPRequest does not include the content of the
     * original SIPRequest. If responseToHeader is null then the toHeader of
     * this request is used to construct the ACK. Note that tag fields are just
     * copied from the original SIP Request. Added by Jeff Keyser.
     *
     * @param responseToHeader To header to use for this request.
     *
     * @return A SIPRequest with an ACK method.
     */
    public SIPRequest createAckRequest(To responseToHeader);

    /**
     * Creates an ACK for non-2xx responses according to RFC3261 17.1.1.3
     *
     * @return A SIPRequest with an ACK method.
     * @throws SipException
     * @throws NullPointerException
     * @throws ParseException
     *
     * @author jvb
     */
    public SIPRequest createErrorAck(To responseToHeader) throws SipException,
            ParseException;

    /**
     * Get the host from the topmost via header.
     *
     * @return the string representation of the host from the topmost via
     * header.
     */
    public String getViaHost();

    /**
     * Get the port from the topmost via header.
     *
     * @return the port from the topmost via header (5060 if there is no port
     * indicated).
     */
    public int getViaPort();

    /**
     * Book keeping method to return the current tx for the request if one
     * exists.
     *
     * @return the assigned tx.
     */
    public Object getTransaction();

    /**
     * Book keeping field to set the current tx for the request.
     *
     * @param transaction
     */
    public void setTransaction(Object transaction);

    /**
     * Book keeping method to get the messasge channel for the request.
     *
     * @return the message channel for the request.
     */
    public Object getMessageChannel();

    /**
     * Set the message channel for the request ( bookkeeping field ).
     *
     * @param messageChannel
     */
    public void setMessageChannel(Object messageChannel);

    /**
     * @param inviteTransaction the inviteTransaction to set
     */
    public void setInviteTransaction(Object inviteTransaction);

    /**
     * @return the inviteTransaction
     */
    public Object getInviteTransaction();

    /**
     * Generates an Id for checking potentially merged requests.
     *
     * @return String to check for merged requests
     */
    public String getMergeId();
}
