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
 * ****************************************************************************
 */
package gov.nist.javax.sip.message;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.AlertInfo;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.ContactList;
import gov.nist.javax.sip.header.ContentLength;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.ErrorInfo;
import gov.nist.javax.sip.header.ErrorInfoList;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.InReplyTo;
import gov.nist.javax.sip.header.MaxForwards;
import gov.nist.javax.sip.header.Priority;
import gov.nist.javax.sip.header.ProxyAuthenticate;
import gov.nist.javax.sip.header.ProxyAuthorization;
import gov.nist.javax.sip.header.ProxyRequire;
import gov.nist.javax.sip.header.ProxyRequireList;
import gov.nist.javax.sip.header.RSeq;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.RetryAfter;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.SIPETag;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.SIPHeaderList;
import gov.nist.javax.sip.header.SIPHeaderNamesCache;
import gov.nist.javax.sip.header.SIPIfMatch;
import gov.nist.javax.sip.header.Server;
import gov.nist.javax.sip.header.Subject;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Unsupported;
import gov.nist.javax.sip.header.UserAgent;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.header.WWWAuthenticate;
import gov.nist.javax.sip.header.Warning;
import gov.nist.javax.sip.parser.HeaderParser;
import gov.nist.javax.sip.parser.ParserFactory;
import gov.nist.javax.sip.parser.PipelinedMsgParser;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.impl.SipMessageImpl;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.header.SipHeader;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.sip.SipException;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentEncodingHeader;
import javax.sip.header.ContentLanguageHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

/*
 * Acknowledgements: Yanick Belanger sent in a patch for the right content length when the content
 * is a String. Bill Mccormick from Nortel Networks sent in a bug fix for setContent.
 *
 */
/**
 * This is the main SIP Message structure.
 * <b>
 * This is an implementation class. WARNING do not directly use the methods of
 * this class in your application. Use the methods of the interfaces implemented
 * by this class.
 * </b>
 *
 * @see StringMsgParser
 * @see PipelinedMsgParser
 *
 * @version 1.2 $Revision: 1.59 $ $Date: 2010-12-02 22:44:52 $
 * @since 1.1
 *
 * @author M. Ranganathan <br/>
 *
 *
 */
public abstract class SIPMessageImpl extends MessageObjectImpl implements SIPMessage, javax.sip.message.Message,
        MessageExt {

    protected SipMessageImpl msgImpl;
    // JvB: use static here?
    private String contentEncodingCharset = MessageFactoryImpl.getDefaultContentEncodingCharset();

    /**
     * unparsed headers
     */
    protected LinkedList<String> unrecognizedHeaders;

    /*
     * True if this is a null request.
     */
    protected boolean nullRequest;

    protected MaxForwards maxForwardsHeader;

    /**
     * The application data pointer. This is un-interpreted by the stack. This
     * is provided as a convenient way of keeping book-keeping data for
     * applications.
     */
    protected Object applicationData;

    protected String forkId;

    /**
     * The remote address that this message is bound to or received from.
     */
    private InetAddress remoteAddress;

    /**
     * The remote port that this message is bound to or received from.
     */
    private int remotePort;

    /**
     * The local address that we will send the message from or that we received
     * it on.
     */
    private InetAddress localAddress;

    /**
     * The local port that we will send the message from or that we received it
     * on.
     */
    private int localPort;

    private InetAddress peerPacketSourceAddress;

    private int peerPacketSourcePort;

    /**
     * Return true if the header belongs only in a Request.
     *
     * @param sipHeader is the header to test.
     */
    public static boolean isRequestHeader(SIPHeader sipHeader) {
        return sipHeader instanceof AlertInfo || sipHeader instanceof InReplyTo
                || sipHeader instanceof Authorization || sipHeader instanceof MaxForwards
                || sipHeader instanceof UserAgent || sipHeader instanceof Priority
                || sipHeader instanceof ProxyAuthorization || sipHeader instanceof ProxyRequire
                || sipHeader instanceof ProxyRequireList || sipHeader instanceof Route
                || sipHeader instanceof RouteList || sipHeader instanceof Subject
                || sipHeader instanceof SIPIfMatch;
    }

    /**
     * Return true if the header belongs only in a response.
     *
     * @param sipHeader is the header to test.
     */
    public static boolean isResponseHeader(SIPHeader sipHeader) {
        return sipHeader instanceof ErrorInfo || sipHeader instanceof ProxyAuthenticate
                || sipHeader instanceof Server || sipHeader instanceof Unsupported
                || sipHeader instanceof RetryAfter || sipHeader instanceof Warning
                || sipHeader instanceof WWWAuthenticate || sipHeader instanceof SIPETag
                || sipHeader instanceof RSeq;

    }

    /**
     * Get A dialog identifier constructed from this messsage. This is an id
     * that can be used to identify dialogs.
     *
     * @param isServerTransaction is a flag that indicates whether this is a
     * server transaction.
     */
    public final String getDialogId(boolean isServer) {

        io.pkts.packet.sip.header.ToHeader to = this.msgImpl.getToHeader();
        return this.getDialogId(isServer, to.getTagIO());
    }

    /**
     * Get a dialog id given the remote tag.
     */
    public final String getDialogId(boolean isServer, Buffer toTag) {
        FromHeader from = msgImpl.getFromHeader();
        CallIdHeader cid = msgImpl.getCallIDHeader();
        StringBuffer retval = new StringBuffer(cid.getCallId());
        if (!isServer) {
            // retval.append(COLON).append(from.getUserAtHostPort());
            if (from.getTag() != null) {
                retval.append(COLON);
                retval.append(from.getTag());
            }
            // retval.append(COLON).append(to.getUserAtHostPort());
            if (toTag != null) {
                retval.append(COLON);
                retval.append(toTag);
            }
        } else {
            // retval.append(COLON).append(to.getUserAtHostPort());
            if (toTag != null) {
                retval.append(COLON);
                retval.append(toTag);
            }
            // retval.append(COLON).append(from.getUserAtHostPort());
            if (from.getTag() != null) {
                retval.append(COLON);
                retval.append(from.getTag());
            }
        }
        return retval.toString().toLowerCase();
    }

    /**
     * Template match for SIP messages. The matchObj is a SIPMessage template to
     * match against. This method allows you to do pattern matching with
     * incoming SIP messages. Null matches wild card.
     *
     * @param other is the match template to match against.
     * @return true if a match occured and false otherwise.
     */
    @Override
    public boolean match(Object other) {
        if (other == null) {
            return true;
        }
        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        SIPMessageImpl matchObj = (SIPMessageImpl) other;
        Iterator<SIPHeader> li = matchObj.getHeaders();
        while (li.hasNext()) {
            SIPHeader hisHeaders = (SIPHeader) li.next();
            List<Header> myHeaders = this.getHeaderList(hisHeaders.getHeaderName());

            // Could not find a header to match his header.
            if (myHeaders == null || myHeaders.size() == 0) {
                return false;
            }

            if (hisHeaders instanceof SIPHeaderList) {
                ListIterator< ?> outerIterator = ((SIPHeaderList< ?>) hisHeaders)
                        .listIterator();
                while (outerIterator.hasNext()) {
                    SIPHeader hisHeader = (SIPHeader) outerIterator.next();
                    if (hisHeader instanceof ContentLength) {
                        continue;
                    }
                    ListIterator< ?> innerIterator = myHeaders.listIterator();
                    boolean found = false;
                    while (innerIterator.hasNext()) {
                        SIPHeader myHeader = (SIPHeader) innerIterator.next();
                        if (myHeader.match(hisHeader)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return false;
                    }
                }
            } else {
                SIPHeader hisHeader = hisHeaders;
                ListIterator<Header> innerIterator = myHeaders.listIterator();
                boolean found = false;
                while (innerIterator.hasNext()) {
                    SIPHeader myHeader = (SIPHeader) innerIterator.next();
                    if (myHeader.match(hisHeader)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;

    }

    /**
     * Merge a request with a template
     *
     * @param template -- template to merge with.
     *
     */
    @Override
    public void merge(Object template) {
        if (!template.getClass().equals(this.getClass())) {
            throw new IllegalArgumentException("Bad class " + template.getClass());
        }
        SIPMessageImpl templateMessage = (SIPMessageImpl) template;
        //TODO
        /*
         Object[] templateHeaders = templateMessage.msgImpl.headers.toArray();
         for (int i = 0; i < templateHeaders.length; i++) {
         SIPHeader hdr = (SIPHeader) templateHeaders[i];
         String hdrName = hdr.getHeaderName();
         List<SIPHeader> myHdrs = this.getHeaderList(hdrName);
         if (myHdrs == null) {
         this.attachHeader(hdr);
         } else {
         ListIterator<SIPHeader> it = myHdrs.listIterator();
         while (it.hasNext()) {
         SIPHeader sipHdr = (SIPHeader) it.next();
         sipHdr.merge(hdr);
         }
         }
         }
         */
    }

    /**
     * clone this message (create a new deep physical copy). All headers in the
     * message are cloned. You can modify the cloned copy without affecting the
     * original. The content is handled as follows: If the content is a String,
     * or a byte array, a new copy of the content is allocated and copied over.
     * If the content is an Object that supports the clone method, then the
     * clone method is invoked and the cloned content is the new content.
     * Otherwise, the content of the new message is set equal to the old one.
     *
     * @return A cloned copy of this object.
     */
    public Object clone() {
        SIPMessageImpl retval = (SIPMessageImpl) super.clone();
        retval.msgImpl = (SipMessageImpl) msgImpl.clone();
        return retval;
    }

    /**
     * Get the string representation of this header (for pretty printing the
     * generated structure).
     *
     * @return Formatted string representation of the object. Note that this is
     * NOT the same as encode(). This is used mainly for debugging purposes.
     */
    public String debugDump() {
        return "";
    }

    /**
     * Constructor: Initializes lists and list headers. All the headers for
     * which there can be multiple occurances in a message are derived from the
     * SIPHeaderListClass. All singleton headers are derived from SIPHeader
     * class.
     */
    public SIPMessageImpl() {
        msgImpl = null;
    }

    public SIPMessageImpl(SipMessage impl) {
        msgImpl = (SipMessageImpl) impl;
    }

    /**
     * Attach a header (replacing the original header).
     *
     * @param sipHeader SIPHeader that replaces a header of the same type.
     */
    @Override
    public void setHeader(Header sipHeader) {
        //TODO
        msgImpl.setHeader(null);
    }

    /**
     * Set a header from a linked list of headers.
     *
     * @param headers -- a list of headers to set.
     */
    public void setHeaders(java.util.List<SIPHeader> headers) {
        //TODO
    }

    /**
     * Attach a header to the end of the existing headers in this SIPMessage
     * structure. This is equivalent to the
     * attachHeader(SIPHeader,replaceflag,false); which is the normal way in
     * which headers are attached. This was added in support of JAIN-SIP.
     *
     * @param h header to attach.
     * @param replaceflag if true then replace a header if it exists.
     * @throws SIPDuplicateHeaderException If replaceFlag is false and only a
     * singleton header is allowed (fpr example CSeq).
     */
    protected void attachHeader(SIPHeader h, boolean replaceflag) throws SIPDuplicateHeaderException {
        //TODO
        this.attachHeader(h, replaceflag, false);
    }

    /**
     * Attach the header to the SIP Message structure at a specified position in
     * its list of headers.
     *
     * @param header Header to attach.
     * @param replaceFlag If true then replace the existing header.
     * @param top Location in the header list to insert the header.
     * @exception SIPDuplicateHeaderException if the header is of a type that
     * cannot tolerate duplicates and one of this type already exists (e.g. CSeq
     * header).
     * @throws IndexOutOfBoundsException If the index specified is greater than
     * the number of headers that are in this message.
     */
    protected void attachHeader(Header header, boolean replaceFlag, boolean top)
            throws SIPDuplicateHeaderException {
        if (header == null) {
            throw new NullPointerException("null header");
        }

        boolean hasList = ListMap.hasList(header);

        String headerNameLowerCase = header.getName();
        Buffer headerNameLowerBuf = Buffers.wrap(headerNameLowerCase);
        if (replaceFlag) {
            msgImpl.removeHeader(headerNameLowerCase);
        } else if (msgImpl.getHeader(headerNameLowerBuf) != null && !hasList) {
            /*if (h instanceof ContentLength) {
             try {
             ContentLength cl = (ContentLength) h;
             contentLengthHeader.setContentLength(cl.getContentLength());
             } catch (InvalidArgumentException e) {
             }
             }*/
            // Just ignore duplicate header.
            return;
        }

        if (msgImpl.getHeader(headerNameLowerBuf) == null) {
            msgImpl.setHeader(header);
        } else {
            try {
                if (hasList) {
                    if (top) {
                        //insert at the beggining of list,shifting all elems back
                        msgImpl.addFirst(header);
                    } else {
                        msgImpl.addLast(header);
                    }
                } else {
                    msgImpl.addLast(header);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Remove a header given its name. If multiple headers of a given name are
     * present then the top flag determines which end to remove headers from.
     *
     * @param headerName is the name of the header to remove.
     * @param top -- flag that indicates which end of header list to process.
     */
    public void removeHeader(String headerName, boolean top) {

        //TODO
    }

    /**
     * Remove all headers given its name.
     *
     * @param headerName is the name of the header to remove.
     */
    public void removeHeader(String headerName) {

        //TODO
    }

    /**
     * Generate (compute) a transaction ID for this SIP message.
     *
     * @return A string containing the concatenation of various portions of the
     * From,To,Via and RequestURI portions of this message as specified in RFC
     * 2543: All responses to a request contain the same values in the Call-ID,
     * CSeq, To, and From fields (with the possible addition of a tag in the To
     * field (section 10.43)). This allows responses to be matched with
     * requests. Incorporates a bug fix for a bug sent in by Gordon Ledgard of
     * IPera for generating transactionIDs when no port is present in the via
     * header. Incorporates a bug fix for a bug report sent in by Chris Mills of
     * Nortel Networks (converts to lower case when returning the transaction
     * identifier).
     *
     * @return a string that can be used as a transaction identifier for this
     * message. This can be used for matching responses and requests (i.e. an
     * outgoing request and its matching response have the same computed
     * transaction identifier).
     */
    public String getTransactionId() {
        ViaHeader topVia = getTopmostVia();
        String viaBranch = topVia.getBranch();
//        if (!this.getViaHeaders().isEmpty()) {
//            topVia = (Via) this.getViaHeaders().getFirst();
//        }
        // Have specified a branch Identifier so we can use it to identify
        // the transaction. BranchId is not case sensitive.
        // Branch Id prefix is not case sensitive.
        if (topVia != null
                && topVia.getBranch() != null
                && viaBranch.toUpperCase().startsWith(
                        SIPConstants.BRANCH_MAGIC_COOKIE_UPPER_CASE)) {
            // Bis 09 compatible branch assignment algorithm.
            // implies that the branch id can be used as a transaction
            // identifier.
            if (this.getCSeq().getMethod().equals(Request.CANCEL)) {
                return (topVia.getBranch() + ":" + this.getCSeq().getMethod()).toLowerCase();
            } else {
                return viaBranch.toLowerCase();
            }
        } else {
            // Old style client so construct the transaction identifier
            // from various fields of the request.
            StringBuilder retval = new StringBuilder();
            From from = (From) this.getFrom();
            To to = (To) this.getTo();
            // String hpFrom = from.getUserAtHostPort();
            // retval.append(hpFrom).append(":");
            if (from.hasTag()) {
                retval.append(from.getTag()).append("-");
            }
            // String hpTo = to.getUserAtHostPort();
            // retval.append(hpTo).append(":");
            String cid = this.msgImpl.getCallIDHeader().getCallId().toString();
            retval.append(cid).append("-");
            retval.append(msgImpl.getCSeqHeader().getSeqNumber()).append("-").append(
                    msgImpl.getCSeqHeader().getMethod());
            if (topVia != null) {
                //TODO
                /*retval.append("-").append(topVia.getSentBy().encode());
                 if (!topVia.getSentBy().hasPort()) {
                 retval.append("-").append(5060);
                 }*/
            }
            if (this.getCSeq().getMethod().equals(Request.CANCEL)) {
                retval.append(Request.CANCEL);
            }
            return retval.toString().toLowerCase().replace(":", "-").replace("@", "-")
                    + Utils.getSignature();
        }
    }

    /**
     * Override the hashcode method ( see issue # 55 ) Note that if you try to
     * use this method before you assemble a valid request, you will get a
     * constant ( -1 ). Beware of placing any half formed requests in a table.
     */
    public int hashCode() {
        if (msgImpl.getCallIDHeader() == null) {
            throw new RuntimeException(
                    "Invalid message! Cannot compute hashcode! call-id header is missing !");
        } else {
            return msgImpl.getCallIDHeader().getCallId().hashCode();
        }
    }

    /**
     * Return true if this message has a body.
     */
    public boolean hasContent() {
        return msgImpl.hasContent();
    }

    /**
     * Return an iterator for the list of headers in this message.
     *
     * @return an Iterator for the headers of this message.
     */
    public Iterator<SIPHeader> getHeaders() {
        //TODO
        return null;
    }

    /**
     * Get the first header of the given name.
     *
     * @return header -- the first header of the given name.
     */
    @Override
    public Header getHeader(String headerName) {
        return getHeaderLowerCase(headerName);
    }

    /**
     * Get the contentType header (null if one does not exist).
     *
     * @return contentType header
     */
    @Override
    public ContentType getContentTypeHeader() {
        return (ContentType) getHeaderLowerCase(CONTENT_TYPE_LOWERCASE);
    }

    private static final String CONTENT_TYPE_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(ContentTypeHeader.NAME);

    /**
     * Get the contentLength header.
     */
    public ContentLengthHeader getContentLengthHeader() {
        return this.getContentLength();
    }

    /**
     * Get the from header.
     *
     * @return -- the from header.
     */
    public FromHeader getFrom() {
        return msgImpl.getFromHeader();
    }

    /**
     * Get the ErrorInfo list of headers (null if one does not exist).
     *
     * @return List containing ErrorInfo headers.
     */
    public ErrorInfoList getErrorInfoHeaders() {
        return (ErrorInfoList) getSIPHeaderListLowerCase(ERROR_LOWERCASE);
    }

    private static final String ERROR_LOWERCASE = SIPHeaderNamesCache.toLowerCase(ErrorInfo.NAME);

    /**
     * Get the Contact list of headers (null if one does not exist).
     *
     * @return List containing Contact headers.
     */
    @Override
    public ContactList getContactHeaders() {
        io.pkts.packet.sip.header.ContactHeader cHeader = msgImpl.getContactHeader();
        ContactList list = new ContactList();
        list.add(cHeader);
        return list;
    }

    private static final String CONTACT_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(ContactHeader.NAME);

    /**
     * Get the contact header ( the first contact header) which is all we need
     * for the most part.
     *
     */
    @Override
    public ContactHeader getContactHeader() {
        ContactList clist = this.getContactHeaders();
        if (clist != null) {
            return clist.getFirst();

        } else {
            return null;
        }
    }

    /**
     * Get the Via list of headers (null if one does not exist).
     *
     * @return List containing Via headers.
     */
    @Override
    public ViaList getViaHeaders() {
        List<io.pkts.packet.sip.header.ViaHeader> viaHeaders = msgImpl.getViaHeaders();
        ViaList list = new ViaList();
        for (io.pkts.packet.sip.header.ViaHeader hdr : viaHeaders) {
            list.add(hdr);
        }
        return list;
    }

    private static final String VIA_LOWERCASE = SIPHeaderNamesCache.toLowerCase(ViaHeader.NAME);

    /**
     * Set A list of via headers.
     *
     * @param viaList a list of via headers to add.
     */
    public void setVia(java.util.List viaList) {
        ViaList vList = new ViaList();
        ListIterator it = viaList.listIterator();
        while (it.hasNext()) {
            ViaHeader via = (ViaHeader) it.next();
            vList.add(via);
        }
        this.setHeader(vList);
    }

    /**
     * Set the header given a list of headers.
     *
     * @param sipHeaderList a headerList to set
     */
    public void setHeader(SIPHeaderList<ViaHeader> sipHeaderList) {
        this.setHeader((Header) sipHeaderList);
    }

    /**
     * Get the topmost via header.
     *
     * @return the top most via header if one exists or null if none exists.
     */
    public ViaHeader getTopmostVia() {
        return msgImpl.getViaHeader();
    }

    /**
     * Get the CSeq list of header (null if one does not exist).
     *
     * @return CSeq header
     */
    public CSeqHeader getCSeq() {
        return msgImpl.getCSeqHeader();
    }

    /**
     * Get the Authorization header (null if one does not exist).
     *
     * @return Authorization header.
     */
    public AuthorizationHeader getAuthorization() {
        return (AuthorizationHeader) getHeaderLowerCase(AUTHORIZATION_LOWERCASE);
    }

    private static final String AUTHORIZATION_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(AuthorizationHeader.NAME);

    /**
     * Get the MaxForwards header (null if one does not exist).
     *
     * @return Max-Forwards header
     */
    @Override
    public MaxForwardsHeader getMaxForwards() {
        return (MaxForwardsHeader) msgImpl.getMaxForwards();
    }

    /**
     * Set the max forwards header.
     *
     * @param maxForwards is the MaxForwardsHeader to set.
     */
    public void setMaxForwards(MaxForwardsHeader maxForwards) {
        this.setHeader(maxForwards);
    }

    /**
     * Get the Route List of headers (null if one does not exist).
     *
     * @return List containing Route headers
     */
    public RouteList getRouteHeaders() {
        return (RouteList) getSIPHeaderListLowerCase(ROUTE_LOWERCASE);
    }

    private static final String ROUTE_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(RouteHeader.NAME);

    /**
     * Get the CallID header (null if one does not exist)
     *
     * @return Call-ID header .
     */
    public CallIdHeader getCallId() {
        return msgImpl.getCallIDHeader();
    }

    /**
     * Set the call id header.
     *
     * @param callId call idHeader (what else could it be?)
     */
    public void setCallId(CallIdHeader callId) {
        this.setHeader(callId);
    }

    /**
     * Get the CallID header (null if one does not exist)
     *
     * @param callId -- the call identifier to be assigned to the call id header
     */
    public void setCallId(String callId) throws java.text.ParseException {
        if (msgImpl.getCallIDHeader() == null) {
            this.setHeader(new CallID());
        }
        //TODO msgImpl.getCallIDHeader().setCallId(callId);
    }

    /**
     * Get the RecordRoute header list (null if one does not exist).
     *
     * @return Record-Route header
     */
    public RecordRouteList getRecordRouteHeaders() {
        return (RecordRouteList) this.getSIPHeaderListLowerCase(RECORDROUTE_LOWERCASE);
    }

    private static final String RECORDROUTE_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(RecordRouteHeader.NAME);

    /**
     * Get the To header (null if one does not exist).
     *
     * @return To header
     */
    @Override
    public ToHeader getTo() {
        return msgImpl.getToHeader();
    }

    @Override
    public void setTo(ToHeader to) {
        this.setHeader(to);
    }

    @Override
    public void setFrom(FromHeader from) {
        this.setHeader(from);

    }

    /**
     * Get the ContentLength header (null if one does not exist).
     *
     * @return content-length header.
     */
    @Override
    public ContentLengthHeader getContentLength() {
        if (msgImpl.getRawContentIO() != null ){
            return new ContentLength(msgImpl.getRawContentIO().capacity());
        } else {
            return new ContentLength(0);
        }
    }

    /**
     * Get the message body as a string. If the message contains a content type
     * header with a specified charset, and if the payload has been read as a
     * byte array, then it is returned encoded into this charset.
     *
     * @return Message body (as a string)
     * @throws UnsupportedEncodingException if the platform does not support the
     * charset specified in the content type header.
     *
     */
    public String getMessageContent() throws UnsupportedEncodingException {
        if (msgImpl.getContent() != null) {
            return msgImpl.getContent().toString();
        } else {
            return null;
        }
    }

    /**
     * Get the message content as an array of bytes. If the payload has been
     * read as a String then it is decoded using the charset specified in the
     * content type header if it exists. Otherwise, it is encoded using the
     * default encoding which is UTF-8.
     *
     * @return an array of bytes that is the message payload.
     */
    @Override
    public byte[] getRawContent() {
        return msgImpl.getRawContentIO().getArray();
    }

    /**
     * Set the message content after converting the given object to a String.
     *
     * @param content -- content to set.
     * @param contentTypeHeader -- content type header corresponding to content.
     */
    public void setContent(Object content, ContentTypeHeader contentTypeHeader)
            throws ParseException {
        if (content == null) {
            throw new NullPointerException("null content");
        }
        this.setHeader(contentTypeHeader);

        //TODO
        computeContentLength(content);
    }

    /**
     * Get the content (body) of the message.
     *
     * @return the content of the sip message.
     */
    public Object getContent() {
        return msgImpl.getContent();
    }

    /**
     * Set the message content for a given type and subtype.
     *
     * @param type is the messge type.
     * @param subType is the message subType.
     * @param messageContent is the message content as a byte array.
     */
    public void setMessageContent(String type, String subType, byte[] messageContent) {
        ContentType ct = new ContentType(type, subType);
        this.setHeader(ct);
        this.setMessageContent(messageContent);

        computeContentLength(messageContent);
    }

    /**
     * Set the message content for this message.
     *
     * @param content Message body as a string.
     */
    public void setMessageContent(byte[] content, boolean strict, boolean computeContentLength, int givenLength)
            throws ParseException {
        // Note that that this could be a double byte character
        // set - bug report by Masafumi Watanabe
        computeContentLength(content);
        if ((!computeContentLength)) {
            if ((!strict && msgImpl.getRawContentIO().capacity() != givenLength)
                    || msgImpl.getRawContentIO().capacity() < givenLength) {
                throw new ParseException("Invalid content length "
                        + msgImpl.getRawContentIO().capacity() + " / " + givenLength, 0);
            }
        }
        //TODO
    }

    /**
     * Set the message content as an array of bytes.
     *
     * @param content is the content of the message as an array of bytes.
     */
    public void setMessageContent(byte[] content) {
        computeContentLength(content);

        //TODO
    }

    /**
     * Method to set the content - called by the parser
     *
     * @param content
     * @throws ParseException
     */
    public void setMessageContent(byte[] content, boolean computeContentLength, int givenLength)
            throws ParseException {
        computeContentLength(content);
        if ((!computeContentLength) && msgImpl.getRawContentIO().capacity() < givenLength) {
            // System.out.println("!!!!!!!!!!! MISMATCH !!!!!!!!!!!");
            throw new ParseException("Invalid content length "
                    + msgImpl.getRawContentIO().capacity() + " / " + givenLength, 0);
        }
        //TODO
    }

    /**
     * Compute and set the Content-length header based on the given content
     * object.
     *
     * @param content is the content, as String, array of bytes, or other
     * object.
     */
    private void computeContentLength(Object content) {
        int length = 0;
        if (content != null) {
            if (content instanceof String) {
                try {
                    length = ((String) content).getBytes(getCharset()).length;
                } catch (UnsupportedEncodingException ex) {
                    InternalErrorHandler.handleException(ex);
                }
            } else if (content instanceof byte[]) {
                length = ((byte[]) content).length;
            } else {
                length = content.toString().length();
            }
        }

        /*TODO
         try {
         contentLengthHeader.setContentLength(length);
         } catch (InvalidArgumentException e) {
         // Cannot happen.
         }*/
    }

    /**
     * Remove the message content if it exists.
     */
    @Override
    public void removeContent() {
        //TODO
        /*try {
         this.contentLengthHeader.setContentLength(0);
         } catch (InvalidArgumentException ex) {
         }*/
    }

    protected Header getHeaderLowerCase(String lowerCaseHeaderName) {
        return msgImpl.getHeader(lowerCaseHeaderName);
    }

    /**
     * Get a SIP header or Header list given its name.
     *
     * @param headerName is the name of the header to get.
     * @return a header or header list that contians the retrieved header.
     */
    @SuppressWarnings("unchecked")
    @Override
    public ListIterator<Header> getHeaders(String headerName) {
        return msgImpl.getHeaders(headerName);
    }

    /**
     * Get a header of the given name as a string. This concatenates the headers
     * of a given type as a comma separted list. This is useful for formatting
     * and printing headers.
     *
     * @param name
     * @return the header as a formatted string
     */
    public String getHeaderAsFormattedString(String name) {
        /*TODO
       
         String lowerCaseName = SIPHeaderNamesCache.toLowerCase(name);
         if (this.headerTable.containsKey(lowerCaseName)) {
         return this.headerTable.get(lowerCaseName).toString();
         } else {
         return this.getHeader(name).toString();
         }*/
        return null;
    }

    public byte[] encodeAsBytes(String transport) {
        return new byte[0];
    }

    /**
     * Encode all the headers except the contents. For debug logging.
     */
    public abstract StringBuilder encodeMessage(StringBuilder retval);

    public Header getSIPHeaderListLowerCase(String lowerCaseHeaderName) {
        try {
            io.pkts.packet.sip.header.SipHeader sipHeader = msgImpl.getHeader(lowerCaseHeaderName);
            return sipHeader;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Get a list of headers of the given name ( or null if no such header
     * exists ).
     *
     * @param headerName -- a header name from which to retrieve the list.
     * @return -- a list of headers with that name.
     */
    @SuppressWarnings("unchecked")
    private List<Header> getHeaderList(String headerName) {
        try {
            ListIterator<Header> sipHeaders = msgImpl.getHeaders(headerName);
            LinkedList<Header> list = new LinkedList<>();
            while (sipHeaders.hasNext()) {
                list.add(sipHeaders.next());
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Return true if the SIPMessage has a header of the given name.
     *
     * @param headerName is the header name for which we are testing.
     * @return true if the header is present in the message
     */
    public boolean hasHeader(String headerName) {
        return msgImpl.getHeader(headerName) != null;
    }

    /**
     * Return true if the message has a From header tag.
     *
     * @return true if the message has a from header and that header has a tag.
     */
    public boolean hasFromTag() {
        return msgImpl.getFromHeader() != null && msgImpl.getFromHeader().getTag() != null;
    }

    /**
     * Return true if the message has a To header tag.
     *
     * @return true if the message has a to header and that header has a tag.
     */
    public boolean hasToTag() {
        return msgImpl.getToHeader() != null && msgImpl.getToHeader().getTag() != null;
    }

    /**
     * Return the from tag.
     *
     * @return the tag from the from header.
     *
     */
    public String getFromTag() {
        return msgImpl.getFromHeader() == null ? null : msgImpl.getFromHeader().getTag().toString();
    }

    /**
     * Set the From Tag.
     *
     * @param tag -- tag to set in the from header.
     */
    public void setFromTag(String tag) {
        /*try {
         //TODO msgImpl.getFromHeader().setTag(Buffers.wrap(tag));
         } catch (ParseException e) {
         }*/
    }

    /**
     * Set the to tag.
     *
     * @param tag -- tag to set.
     */
    @Override
    public void setToTag(String tag) {
        /*try {
         //TODO msgImpl.getToHeader().setTag(tag);
         } catch (ParseException e) {
         }*/
    }

    /**
     * Return the to tag.
     */
    public String getToTag() {
        return msgImpl.getToHeader().getTag() == null ? null : msgImpl.getToHeader().getTag().toString();
    }

    /**
     * Return the encoded first line.
     */
    @Override
    public abstract String getFirstLine();

    /**
     * Add a SIP header.
     *
     * @param sipHeader -- sip header to add.
     */
    @Override
    public void addHeader(Header sipHeader) {
        // Content length is never stored. Just computed.
        try {
            if ((sipHeader instanceof ViaHeader) || (sipHeader instanceof RecordRouteHeader)) {
                attachHeader(sipHeader, false, true);
            } else {
                attachHeader(sipHeader, false, false);
            }
        } catch (SIPDuplicateHeaderException ex) {
            /*TODO try {
             if (sipHeader instanceof ContentLength) {
             ContentLength cl = (ContentLength) sipHeader;
             contentLengthHeader.setContentLength(cl.getContentLength());
             }
             } catch (InvalidArgumentException e) {
             }*/
        }
    }

    /**
     * Add a header to the unparsed list of headers.
     *
     * @param unparsed -- unparsed header to add to the list.
     */
    @Override
    public void addUnparsed(String unparsed) {
        this.getUnrecognizedHeadersList().add(unparsed);
    }

    /**
     * Add a SIP header.
     *
     * @param sipHeader -- string version of SIP header to add.
     */
    public void addHeader(String sipHeader) {
        String hdrString = sipHeader.trim() + "\n";
        try {
            HeaderParser parser = ParserFactory.createParser(sipHeader);
            SIPHeader sh = parser.parse();
            this.attachHeader(sh, false);
        } catch (ParseException ex) {
            this.getUnrecognizedHeadersList().add(hdrString);
        }
    }

    /**
     * Get a list containing the unrecognized headers.
     *
     * @return a linked list containing unrecongnized headers.
     */
    public ListIterator<String> getUnrecognizedHeaders() {
        return this.getUnrecognizedHeadersList().listIterator();
    }

    /**
     * Get the header names.
     *
     * @return a list iterator to a list of header names. These are ordered in
     * the same order as are present in the message.
     */
    @Override
    public ListIterator<String> getHeaderNames() {
        return msgImpl.getHeaderNames();
    }

    /**
     * Compare for equality.
     *
     * @param other -- the other object to compare with.
     */
    public boolean equals(Object other) {
        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        SIPMessageImpl otherMessage = (SIPMessageImpl) other;
        ListIterator<String> it = msgImpl.getHeaderNames();

        while (it.hasNext()) {
            String mineName = it.next();
            SipHeader mine = msgImpl.getHeader(mineName);
            SipHeader his = otherMessage.msgImpl.getHeader(mineName);
            if (his == null) {
                return false;
            } else if (!his.equals(mine)) {
                return false;
            }
        }
        return true;
    }

    /**
     * get content disposition header or null if no such header exists.
     *
     * @return the contentDisposition header
     */
    public javax.sip.header.ContentDispositionHeader getContentDisposition() {
        return (ContentDispositionHeader) getHeaderLowerCase(CONTENT_DISPOSITION_LOWERCASE);
    }

    private static final String CONTENT_DISPOSITION_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(ContentDispositionHeader.NAME);

    /**
     * get the content encoding header.
     *
     * @return the contentEncoding header.
     */
    public javax.sip.header.ContentEncodingHeader getContentEncoding() {
        return (ContentEncodingHeader) getHeaderLowerCase(CONTENT_ENCODING_LOWERCASE);
    }

    private static final String CONTENT_ENCODING_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(ContentEncodingHeader.NAME);

    /**
     * Get the contentLanguage header.
     *
     * @return the content language header.
     */
    public javax.sip.header.ContentLanguageHeader getContentLanguage() {
        return (ContentLanguageHeader) getHeaderLowerCase(CONTENT_LANGUAGE_LOWERCASE);
    }

    private static final String CONTENT_LANGUAGE_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(ContentLanguageHeader.NAME);

    /**
     * Get the exipres header.
     *
     * @return the expires header or null if one does not exist.
     */
    @Override
    public javax.sip.header.ExpiresHeader getExpires() {
        return (ExpiresHeader) getHeaderLowerCase(EXPIRES_LOWERCASE);
    }

    private static final String EXPIRES_LOWERCASE = SIPHeaderNamesCache
            .toLowerCase(ExpiresHeader.NAME);

    /**
     * Set the expiresHeader
     *
     * @param expiresHeader -- the expires header to set.
     */
    @Override
    public void setExpires(ExpiresHeader expiresHeader) {
        this.setHeader(expiresHeader);
    }

    /**
     * Set the content disposition header.
     *
     * @param contentDispositionHeader -- content disposition header.
     */
    @Override
    public void setContentDisposition(ContentDispositionHeader contentDispositionHeader) {
        this.setHeader(contentDispositionHeader);

    }

    @Override
    public void setContentEncoding(ContentEncodingHeader contentEncodingHeader) {
        this.setHeader(contentEncodingHeader);

    }

    @Override
    public void setContentLanguage(ContentLanguageHeader contentLanguageHeader) {
        this.setHeader(contentLanguageHeader);
    }

    /**
     * Set the content length header.
     *
     * @param contentLength -- content length header.
     */
    @Override
    public void setContentLength(ContentLengthHeader contentLength) {
        /*TODO
         try {
         this.contentLengthHeader.setContentLength(contentLength.getContentLength());
         } catch (InvalidArgumentException ex) {
         }*/

    }

    /**
     * Set the size of all the headers. This is for book keeping. Called by the
     * parser.
     *
     * @param size -- size of the headers.
     */
    public void setSize(int size) {
        //TODO this.size = size;
    }

    public int getSize() {
        //TODO return this.size;
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.sip.message.Message#addLast(javax.sip.header.Header)
     */
    public void addLast(Header header) throws SipException, NullPointerException {
        if (header == null) {
            throw new NullPointerException("null arg!");
        }

        try {
            this.attachHeader((SIPHeader) header, false, false);
        } catch (SIPDuplicateHeaderException ex) {
            throw new SipException("Cannot add header - header already exists");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.sip.message.Message#addFirst(javax.sip.header.Header)
     */
    public void addFirst(Header header) throws SipException, NullPointerException {

        if (header == null) {
            throw new NullPointerException("null arg!");
        }

        try {
            this.attachHeader((SIPHeader) header, false, true);
        } catch (SIPDuplicateHeaderException ex) {
            throw new SipException("Cannot add header - header already exists");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.sip.message.Message#removeFirst(java.lang.String)
     */
    public void removeFirst(String headerName) throws NullPointerException {
        if (headerName == null) {
            throw new NullPointerException("Null argument Provided!");
        }
        this.removeHeader(headerName, true);

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.sip.message.Message#removeLast(java.lang.String)
     */
    public void removeLast(String headerName) {
        if (headerName == null) {
            throw new NullPointerException("Null argument Provided!");
        }
        this.removeHeader(headerName, false);

    }

    /**
     * Set the CSeq header.
     *
     * @param cseqHeader -- CSeq Header.
     */
    public void setCSeq(CSeqHeader cseqHeader) {
        this.setHeader(cseqHeader);
    }

    /**
     * Set the application data pointer. This method is not used the stack. It
     * is provided as a convenient way of storing book-keeping data for
     * applications. Note that null clears the application data pointer
     * (releases it).
     *
     * @param applicationData -- application data pointer to set. null clears
     * the application data pointer.
     */
    public void setApplicationData(Object applicationData) {
        this.applicationData = applicationData;
    }

    /**
     * Get the application data associated with this message.
     *
     * @return stored application data.
     */
    public Object getApplicationData() {
        return this.applicationData;
    }

    /**
     * Get the multipart MIME content
     *
     */
    public MultipartMimeContent getMultipartMimeContent() throws ParseException {
        if (msgImpl.getRawContentIO().capacity() == 0) {
            return null;
        }
        MultipartMimeContentImpl retval = new MultipartMimeContentImpl(this
                .getContentTypeHeader());
        byte[] rawContent = getRawContent();
        try {
            String body = new String(rawContent, getCharset());
            retval.createContentList(body);
            return retval;
        } catch (UnsupportedEncodingException e) {
            InternalErrorHandler.handleException(e);
            return null;
        }
    }

    @Override
    public CallIdHeader getCallIdHeader() {
        return msgImpl.getCallIDHeader();
    }

    @Override
    public FromHeader getFromHeader() {
        return msgImpl.getFromHeader();
    }

    @Override
    public ToHeader getToHeader() {
        return msgImpl.getToHeader();
    }

    @Override
    public ViaHeader getTopmostViaHeader() {
        return msgImpl.getViaHeader();
    }

    @Override
    public CSeqHeader getCSeqHeader() {
        return msgImpl.getCSeqHeader();
    }

    /**
     * Returns the charset to use for encoding/decoding the body of this message
     * @return 
     */
    protected final String getCharset() {
        ContentType ct = getContentTypeHeader();
        if (ct != null) {
            String c = ct.getCharset();
            return c != null ? c : contentEncodingCharset;
        } else {
            return contentEncodingCharset;
        }
    }

    /**
     * Return true if this is a null request (i.e. does not have a request line
     * ).
     *
     * @return true if null request.
     */
    public boolean isNullRequest() {
        return this.nullRequest;
    }

    /**
     * Set a flag to indiate this is a special message ( encoded with CRLFCRLF
     * ).
     *
     */
    public void setNullRequest() {
        this.nullRequest = true;
    }

    public String getForkId() {
        if (this.forkId != null) {
            return forkId;
        } else {
            String callId = this.getCallId().getCallId();
            String fromTag = this.getFromTag();
            if (fromTag == null) {
                throw new IllegalStateException("From tag is not yet set. Cannot compute forkId");
            }
            this.forkId = (callId + ":" + fromTag).toLowerCase();
            return this.forkId;
        }
    }

    public abstract void setSIPVersion(String sipVersion) throws ParseException;

    public abstract String getSIPVersion();

    public abstract String toString();

    public void cleanUp() {
//        callIdHeader = null;
//        contentEncodingCharset = null;
//        contentLengthHeader = null;
//        cSeqHeader = null;
//        forkId = null;
//        fromHeader = null;
//        if(headers != null) {
//            headers.clear();
//            headers = null;
//        }
//        matchExpression = null;
//        maxForwardsHeader = null;
//        messageContent = null;
//        messageContentBytes = null;
//        messageContentObject = null;
//        if(nameTable != null) {
//            nameTable.clear();
//            nameTable = null;
//        }
//        stringRepresentation = null;
//        toHeader = null;
//        if(unrecognizedHeaders != null) {
//            unrecognizedHeaders.clear();
//            unrecognizedHeaders = null;
//        }
    }

    /**
     * @param unrecognizedHeaders the unrecognizedHeaders to set
     */
    protected void setUnrecognizedHeadersList(LinkedList<String> unrecognizedHeaders) {
        this.unrecognizedHeaders = unrecognizedHeaders;
    }

    /**
     * @return the unrecognizedHeaders
     */
    protected LinkedList<String> getUnrecognizedHeadersList() {
        if (unrecognizedHeaders == null) {
            unrecognizedHeaders = new LinkedList<String>();
        }
        return unrecognizedHeaders;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setLocalAddress(InetAddress localAddress) {
        this.localAddress = localAddress;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setPeerPacketSourceAddress(InetAddress peerPacketSourceAddress) {
        this.peerPacketSourceAddress = peerPacketSourceAddress;
    }

    public InetAddress getPeerPacketSourceAddress() {
        return this.peerPacketSourceAddress;
    }

    public void setPeerPacketSourcePort(int peerPacketSourcePort) {
        this.peerPacketSourcePort = peerPacketSourcePort;
    }

    public int getPeerPacketSourcePort() {
        return this.peerPacketSourcePort;
    }
    
    public SipMessageImpl getMsgImpl() {
        return msgImpl;
    }
}
