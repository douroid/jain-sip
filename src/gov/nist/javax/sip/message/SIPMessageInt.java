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

import gov.nist.javax.sip.header.AlertInfo;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.ContactList;
import gov.nist.javax.sip.header.ErrorInfo;
import gov.nist.javax.sip.header.ErrorInfoList;
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
import gov.nist.javax.sip.header.SIPIfMatch;
import gov.nist.javax.sip.header.Server;
import gov.nist.javax.sip.header.Subject;
import gov.nist.javax.sip.header.Unsupported;
import gov.nist.javax.sip.header.UserAgent;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.header.WWWAuthenticate;
import gov.nist.javax.sip.header.Warning;
import gov.nist.javax.sip.parser.PipelinedMsgParser;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.pkts.buffer.Buffer;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Iterator;
import java.util.ListIterator;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;

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
public interface SIPMessageInt extends javax.sip.message.Message,MessageObject,
        MessageExt {

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
    String getDialogId(boolean isServer);

    /**
     * Get a dialog id given the remote tag.
     */
    String getDialogId(boolean isServer, Buffer toTag);

    /**
     * Template match for SIP messages. The matchObj is a SIPMessage template to
     * match against. This method allows you to do pattern matching with
     * incoming SIP messages. Null matches wild card.
     *
     * @param other is the match template to match against.
     * @return true if a match occured and false otherwise.
     */
    public boolean match(Object other);

    /**
     * Merge a request with a template
     *
     * @param template -- template to merge with.
     *
     */
    void merge(Object template);

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
    @Override
    Object clone();

    public String debugDump();

    /**
     * Attach a header (replacing the original header).
     *
     * @param sipHeader SIPHeader that replaces a header of the same type.
     */
    @Override
    public void setHeader(Header sipHeader);

    /**
     * Set a header from a linked list of headers.
     *
     * @param headers -- a list of headers to set.
     */
    public void setHeaders(java.util.List<SIPHeader> headers);

    /**
     * Remove a header given its name. If multiple headers of a given name are
     * present then the top flag determines which end to remove headers from.
     *
     * @param headerName is the name of the header to remove.
     * @param top -- flag that indicates which end of header list to process.
     */
    void removeHeader(String headerName, boolean top);

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
    String getTransactionId();

    /**
     * Return true if this message has a body.
     */
    public boolean hasContent();

    /**
     * Return an iterator for the list of headers in this message.
     *
     * @return an Iterator for the headers of this message.
     */
    public Iterator<SIPHeader> getHeaders();

    /**
     * Get the ErrorInfo list of headers (null if one does not exist).
     *
     * @return List containing ErrorInfo headers.
     */
    ErrorInfoList getErrorInfoHeaders();

    /**
     * Get the Contact list of headers (null if one does not exist).
     *
     * @return List containing Contact headers.
     */
    ContactList getContactHeaders();

    /**
     * Get the contact header ( the first contact header) which is all we need
     * for the most part.
     *
     */
    ContactHeader getContactHeader();

    /**
     * Get the Via list of headers (null if one does not exist).
     *
     * @return List containing Via headers.
     */
    ViaList getViaHeaders();

    /**
     * Set A list of via headers.
     *
     * @param viaList a list of via headers to add.
     */
    void setVia(java.util.List viaList);

    /**
     * Set the header given a list of headers.
     *
     * @param sipHeaderList a headerList to set
     */
    void setHeader(SIPHeaderList<ViaHeader> sipHeaderList);

    /**
     * Get the topmost via header.
     *
     * @return the top most via header if one exists or null if none exists.
     */
    ViaHeader getTopmostVia();

    /**
     * Get the CSeq list of header (null if one does not exist).
     *
     * @return CSeq header
     */
    CSeqHeader getCSeq();

    /**
     * Get the Authorization header (null if one does not exist).
     *
     * @return Authorization header.
     */
    public AuthorizationHeader getAuthorization();

    /**
     * Get the MaxForwards header (null if one does not exist).
     *
     * @return Max-Forwards header
     */
    public MaxForwardsHeader getMaxForwards();

    /**
     * Set the max forwards header.
     *
     * @param maxForwards is the MaxForwardsHeader to set.
     */
    public void setMaxForwards(MaxForwardsHeader maxForwards);

    /**
     * Get the Route List of headers (null if one does not exist).
     *
     * @return List containing Route headers
     */
    public RouteList getRouteHeaders();

    /**
     * Get the CallID header (null if one does not exist)
     *
     * @return Call-ID header .
     */
    public CallIdHeader getCallId();

    /**
     * Set the call id header.
     *
     * @param callId call idHeader (what else could it be?)
     */
    public void setCallId(CallIdHeader callId);

    /**
     * Get the CallID header (null if one does not exist)
     *
     * @param callId -- the call identifier to be assigned to the call id header
     */
    public void setCallId(String callId) throws java.text.ParseException;

    /**
     * Get the RecordRoute header list (null if one does not exist).
     *
     * @return Record-Route header
     */
    public RecordRouteList getRecordRouteHeaders();

    /**
     * Get the To header (null if one does not exist).
     *
     * @return To header
     */
    public ToHeader getTo();

    public void setTo(ToHeader to);

    public void setFrom(FromHeader from);

    /**
     * Get the ContentLength header (null if one does not exist).
     *
     * @return content-length header.
     */
    public ContentLengthHeader getContentLength();

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
    public String getMessageContent() throws UnsupportedEncodingException;

    /**
     * Set the message content for a given type and subtype.
     *
     * @param type is the messge type.
     * @param subType is the message subType.
     * @param messageContent is the message content as a byte array.
     */
    public void setMessageContent(String type, String subType, byte[] messageContent);

    /**
     * Set the message content for this message.
     *
     * @param content Message body as a string.
     */
    public void setMessageContent(byte[] content, boolean strict, boolean computeContentLength, int givenLength)
            throws ParseException;

    /**
     * Set the message content as an array of bytes.
     *
     * @param content is the content of the message as an array of bytes.
     */
    public void setMessageContent(byte[] content);

    /**
     * Method to set the content - called by the parser
     *
     * @param content
     * @throws ParseException
     */
    public void setMessageContent(byte[] content, boolean computeContentLength, int givenLength)
            throws ParseException;

    /**
     * Get a header of the given name as a string. This concatenates the headers
     * of a given type as a comma separted list. This is useful for formatting
     * and printing headers.
     *
     * @param name
     * @return the header as a formatted string
     */
    public String getHeaderAsFormattedString(String name);

    public byte[] encodeAsBytes(String transport);

    /**
     * Encode all the headers except the contents. For debug logging.
     */
    public abstract StringBuilder encodeMessage(StringBuilder retval);

    public Header getSIPHeaderListLowerCase(String lowerCaseHeaderName);

    /**
     * Return true if the SIPMessage has a header of the given name.
     *
     * @param headerName is the header name for which we are testing.
     * @return true if the header is present in the message
     */
    public boolean hasHeader(String headerName);

    /**
     * Return true if the message has a From header tag.
     *
     * @return true if the message has a from header and that header has a tag.
     */
    public boolean hasFromTag();

    /**
     * Return true if the message has a To header tag.
     *
     * @return true if the message has a to header and that header has a tag.
     */
    public boolean hasToTag();

    /**
     * Return the from tag.
     *
     * @return the tag from the from header.
     *
     */
    public String getFromTag();

    /**
     * Set the From Tag.
     *
     * @param tag -- tag to set in the from header.
     */
    public void setFromTag(String tag);

    /**
     * Set the to tag.
     *
     * @param tag -- tag to set.
     */
    public void setToTag(String tag);

    /**
     * Return the to tag.
     */
    public String getToTag();

    /**
     * Return the encoded first line.
     */
    public abstract String getFirstLine();

    /**
     * Add a header to the unparsed list of headers.
     *
     * @param unparsed -- unparsed header to add to the list.
     */
    public void addUnparsed(String unparsed);

    /**
     * Add a SIP header.
     *
     * @param sipHeader -- string version of SIP header to add.
     */
    public void addHeader(String sipHeader);

    /**
     * Get a list containing the unrecognized headers.
     *
     * @return a linked list containing unrecongnized headers.
     */
    public ListIterator<String> getUnrecognizedHeaders();

    /**
     * Set the size of all the headers. This is for book keeping. Called by the
     * parser.
     *
     * @param size -- size of the headers.
     */
    public void setSize(int size);

    public int getSize();

    /**
     * Set the CSeq header.
     *
     * @param cseqHeader -- CSeq Header.
     */
    public void setCSeq(CSeqHeader cseqHeader);

    public FromHeader getFromHeader();

    public ToHeader getToHeader();

    public ViaHeader getTopmostViaHeader();

    public CSeqHeader getCSeqHeader();

    /**
     * Return true if this is a null request (i.e. does not have a request line
     * ).
     *
     * @return true if null request.
     */
    public boolean isNullRequest();

    /**
     * Set a flag to indiate this is a special message ( encoded with CRLFCRLF
     * ).
     *
     */
    public void setNullRequest();

    public String getForkId();

    public abstract void setSIPVersion(String sipVersion) throws ParseException;

    public abstract String getSIPVersion();

    public abstract String toString();

    public void cleanUp();

    public void setRemoteAddress(InetAddress remoteAddress);

    public InetAddress getRemoteAddress();

    public void setRemotePort(int remotePort);

    public int getRemotePort();

    public void setLocalAddress(InetAddress localAddress);

    public InetAddress getLocalAddress();

    public void setLocalPort(int localPort);

    public int getLocalPort();

    public void setPeerPacketSourceAddress(InetAddress peerPacketSourceAddress);

    public InetAddress getPeerPacketSourceAddress();

    public void setPeerPacketSourcePort(int peerPacketSourcePort);

    public int getPeerPacketSourcePort();
}
