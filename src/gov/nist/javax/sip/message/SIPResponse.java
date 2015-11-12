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
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD)         *
 *******************************************************************************/
package gov.nist.javax.sip.message;

import gov.nist.javax.sip.header.StatusLine;
import java.text.ParseException;


/**
 * SIP Response structure.
 *
 * @version 1.2 $Revision: 1.35 $ $Date: 2010-09-14 14:39:39 $
 * @since 1.1
 *
 * @author M. Ranganathan   <br/>
 *
 *
 */
public interface SIPResponse extends SIPMessage, javax.sip.message.Response, ResponseExt {

   
    public static String getReasonPhrase(int rc) {
        String retval = null;
        switch (rc) {

            case TRYING :
                retval = "Trying";
                break;

            case RINGING :
                retval = "Ringing";
                break;

            case CALL_IS_BEING_FORWARDED :
                retval = "Call is being forwarded";
                break;

            case QUEUED :
                retval = "Queued";
                break;

            case SESSION_PROGRESS :
                retval = "Session progress";
                break;

            case OK :
                retval = "OK";
                break;

            case ACCEPTED :
                retval = "Accepted";
                break;

            case MULTIPLE_CHOICES :
                retval = "Multiple choices";
                break;

            case MOVED_PERMANENTLY :
                retval = "Moved permanently";
                break;

            case MOVED_TEMPORARILY :
                retval = "Moved Temporarily";
                break;

            case USE_PROXY :
                retval = "Use proxy";
                break;

            case ALTERNATIVE_SERVICE :
                retval = "Alternative service";
                break;

            case BAD_REQUEST :
                retval = "Bad request";
                break;

            case UNAUTHORIZED :
                retval = "Unauthorized";
                break;

            case PAYMENT_REQUIRED :
                retval = "Payment required";
                break;

            case FORBIDDEN :
                retval = "Forbidden";
                break;

            case NOT_FOUND :
                retval = "Not found";
                break;

            case METHOD_NOT_ALLOWED :
                retval = "Method not allowed";
                break;

            case NOT_ACCEPTABLE :
                retval = "Not acceptable";
                break;

            case PROXY_AUTHENTICATION_REQUIRED :
                retval = "Proxy Authentication required";
                break;

            case REQUEST_TIMEOUT :
                retval = "Request timeout";
                break;

            case GONE :
                retval = "Gone";
                break;

            case TEMPORARILY_UNAVAILABLE :
                retval = "Temporarily Unavailable";
                break;

            case REQUEST_ENTITY_TOO_LARGE :
                retval = "Request entity too large";
                break;

            case REQUEST_URI_TOO_LONG :
                retval = "Request-URI too large";
                break;

            case UNSUPPORTED_MEDIA_TYPE :
                retval = "Unsupported media type";
                break;

            case UNSUPPORTED_URI_SCHEME :
                retval = "Unsupported URI Scheme";
                break;

            case BAD_EXTENSION :
                retval = "Bad extension";
                break;

            case EXTENSION_REQUIRED :
                retval = "Etension Required";
                break;

            case INTERVAL_TOO_BRIEF :
                retval = "Interval too brief";
                break;

            case CALL_OR_TRANSACTION_DOES_NOT_EXIST :
                retval = "Call leg/Transaction does not exist";
                break;

            case LOOP_DETECTED :
                retval = "Loop detected";
                break;

            case TOO_MANY_HOPS :
                retval = "Too many hops";
                break;

            case ADDRESS_INCOMPLETE :
                retval = "Address incomplete";
                break;

            case AMBIGUOUS :
                retval = "Ambiguous";
                break;

            case BUSY_HERE :
                retval = "Busy here";
                break;

            case REQUEST_TERMINATED :
                retval = "Request Terminated";
                break;

            //Issue 168, Typo fix reported by fre on the retval
            case NOT_ACCEPTABLE_HERE :
                retval = "Not Acceptable here";
                break;

            case BAD_EVENT :
                retval = "Bad Event";
                break;

            case REQUEST_PENDING :
                retval = "Request Pending";
                break;

            case SERVER_INTERNAL_ERROR :
                retval = "Server Internal Error";
                break;

            case UNDECIPHERABLE :
                retval = "Undecipherable";
                break;

            case NOT_IMPLEMENTED :
                retval = "Not implemented";
                break;

            case BAD_GATEWAY :
                retval = "Bad gateway";
                break;

            case SERVICE_UNAVAILABLE :
                retval = "Service unavailable";
                break;

            case SERVER_TIMEOUT :
                retval = "Gateway timeout";
                break;

            case VERSION_NOT_SUPPORTED :
                retval = "SIP version not supported";
                break;

            case MESSAGE_TOO_LARGE :
                retval = "Message Too Large";
                break;

            case BUSY_EVERYWHERE :
                retval = "Busy everywhere";
                break;

            case DECLINE :
                retval = "Decline";
                break;

            case DOES_NOT_EXIST_ANYWHERE :
                retval = "Does not exist anywhere";
                break;

            case SESSION_NOT_ACCEPTABLE :
                retval = "Session Not acceptable";
                break;

            case CONDITIONAL_REQUEST_FAILED:
                retval = "Conditional request failed";
                break;

            default :
                retval = "Unknown Status";

        }
        return retval;

    }



    /**
     * Get the status line of the response.
     *@return StatusLine
     */
    public StatusLine getStatusLine();



    /** Return true if the response is a final response.
     *@param rc is the return code.
     *@return true if the parameter is between the range 200 and 700.
     */
    public static boolean isFinalResponse(int rc) {
        return rc >= 200 && rc < 700;
    }

    /** Is this a final response?
     *@return true if this is a final response.
     */
    public boolean isFinalResponse();

    /**
     * Set the status line field.
     *@param sl Status line to set.
     */
    public void setStatusLine(StatusLine sl);


    /**
     * Check the response structure. Must have from, to CSEQ and VIA
     * headers.
     */
    public void checkHeaders() throws ParseException;

    /**
     *  Encode the SIP Request as a string.
     *@return The string encoded canonical form of the message.
     */

    public String encode();







    /**
     * @param isRetransmission the isRetransmission to set
     */
    public void setRetransmission(boolean isRetransmission);

    /**
     * @return the isRetransmission
     */
    public boolean isRetransmission();
}
