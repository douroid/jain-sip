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

import gov.nist.javax.sip.header.AcceptEncodingList;
import gov.nist.javax.sip.header.AcceptLanguageList;
import gov.nist.javax.sip.header.AcceptList;
import gov.nist.javax.sip.header.AlertInfoList;
import gov.nist.javax.sip.header.AllowEventsList;
import gov.nist.javax.sip.header.AllowList;
import gov.nist.javax.sip.header.AuthorizationList;
import gov.nist.javax.sip.header.CallInfoList;
import gov.nist.javax.sip.header.ContactList;
import gov.nist.javax.sip.header.ContentEncodingList;
import gov.nist.javax.sip.header.ContentLanguageList;
import gov.nist.javax.sip.header.ErrorInfoList;
import gov.nist.javax.sip.header.ExtensionHeaderList;
import gov.nist.javax.sip.header.InReplyToList;
import gov.nist.javax.sip.header.ProxyAuthenticateList;
import gov.nist.javax.sip.header.ProxyAuthorizationList;
import gov.nist.javax.sip.header.ProxyRequireList;
import gov.nist.javax.sip.header.ReasonList;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.RequireList;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.SIPHeaderList;
import gov.nist.javax.sip.header.SupportedList;
import gov.nist.javax.sip.header.UnsupportedList;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.header.WWWAuthenticate;
import gov.nist.javax.sip.header.WWWAuthenticateList;
import gov.nist.javax.sip.header.WarningList;
import gov.nist.javax.sip.header.ims.PAssertedIdentity;
import gov.nist.javax.sip.header.ims.PAssertedIdentityList;
import gov.nist.javax.sip.header.ims.PAssociatedURIHeader;
import gov.nist.javax.sip.header.ims.PAssociatedURIList;
import gov.nist.javax.sip.header.ims.PMediaAuthorizationHeader;
import gov.nist.javax.sip.header.ims.PMediaAuthorizationList;
import gov.nist.javax.sip.header.ims.PVisitedNetworkID;
import gov.nist.javax.sip.header.ims.PVisitedNetworkIDList;
import gov.nist.javax.sip.header.ims.PathHeader;
import gov.nist.javax.sip.header.ims.PathList;
import gov.nist.javax.sip.header.ims.PrivacyHeader;
import gov.nist.javax.sip.header.ims.PrivacyList;
import gov.nist.javax.sip.header.ims.SecurityClient;
import gov.nist.javax.sip.header.ims.SecurityClientList;
import gov.nist.javax.sip.header.ims.SecurityServer;
import gov.nist.javax.sip.header.ims.SecurityServerList;
import gov.nist.javax.sip.header.ims.SecurityVerify;
import gov.nist.javax.sip.header.ims.SecurityVerifyList;
import gov.nist.javax.sip.header.ims.ServiceRouteHeader;
import gov.nist.javax.sip.header.ims.ServiceRouteList;
import io.pkts.packet.sip.header.ViaHeader;

import java.util.HashMap;
import java.util.Map;
import javax.sip.header.AcceptEncodingHeader;
import javax.sip.header.AcceptHeader;
import javax.sip.header.AcceptLanguageHeader;
import javax.sip.header.AlertInfoHeader;
import javax.sip.header.AllowEventsHeader;
import javax.sip.header.AllowHeader;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CallInfoHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentEncodingHeader;
import javax.sip.header.ContentLanguageHeader;
import javax.sip.header.ErrorInfoHeader;
import javax.sip.header.ExtensionHeader;
import javax.sip.header.Header;
import javax.sip.header.InReplyToHeader;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.ProxyAuthorizationHeader;
import javax.sip.header.ProxyRequireHeader;
import javax.sip.header.ReasonHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RequireHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.SupportedHeader;
import javax.sip.header.UnsupportedHeader;
import javax.sip.header.WarningHeader;

/**
 * A map of which of the standard headers may appear as a list
 *
 * @version 1.2 $Revision: 1.16 $ $Date: 2010-05-06 14:08:04 $
 * @since 1.1
 */
public class ListMap {
    // A table that indicates whether a header has a list representation or
    // not (to catch adding of the non-list form when a list exists.)
    // Entries in this table allow you to look up the list form of a header
    // (provided it has a list form). Note that under JAVA-5 we have
    // typed collections which would render such a list obsolete. However,
    // we are not using java 5.
    private static Map<Class<?>,Class<?>> headerListTable;

    private static boolean initialized;
    static {
        initializeListMap();
    }

    static private void initializeListMap() {
        /*
         * Build a table mapping between objects that have a list form and the
         * class of such objects.
         */
    	// jeand : using concurrent data structure to avoid excessive blocking
        headerListTable = new HashMap<Class<?>, Class<?>>(34);
        headerListTable.put(ExtensionHeader.class, ExtensionHeaderList.class);

        headerListTable.put(ContactHeader.class, ContactList.class);

        headerListTable.put(ContentEncodingHeader.class, ContentEncodingList.class);

        headerListTable.put(ViaHeader.class, ViaList.class);

        headerListTable.put(WWWAuthenticate.class, WWWAuthenticateList.class);

        headerListTable.put(AcceptHeader.class, AcceptList.class);

        headerListTable.put(AcceptEncodingHeader.class, AcceptEncodingList.class);

        headerListTable.put(AcceptLanguageHeader.class, AcceptLanguageList.class);

        headerListTable.put(ProxyRequireHeader.class, ProxyRequireList.class);

        headerListTable.put(RouteHeader.class, RouteList.class);

        headerListTable.put(RequireHeader.class, RequireList.class);

        headerListTable.put(WarningHeader.class, WarningList.class);

        headerListTable.put(UnsupportedHeader.class, UnsupportedList.class);

        headerListTable.put(AlertInfoHeader.class, AlertInfoList.class);

        headerListTable.put(CallInfoHeader.class, CallInfoList.class);

        headerListTable.put(ProxyAuthenticateHeader.class,ProxyAuthenticateList.class);

        headerListTable.put(ProxyAuthorizationHeader.class, ProxyAuthorizationList.class);

        headerListTable.put(AuthorizationHeader.class, AuthorizationList.class);

        headerListTable.put(AllowHeader.class, AllowList.class);
        
        // http://java.net/jira/browse/JSIP-410
        headerListTable.put(AllowEventsHeader.class, AllowEventsList.class);

        headerListTable.put(RecordRouteHeader.class, RecordRouteList.class);

        headerListTable.put(ContentLanguageHeader.class, ContentLanguageList.class);

        headerListTable.put(ErrorInfoHeader.class, ErrorInfoList.class);

        headerListTable.put(SupportedHeader.class, SupportedList.class);

        headerListTable.put(InReplyToHeader.class,InReplyToList.class);

        // IMS headers.

        headerListTable.put(PAssociatedURIHeader.class, PAssociatedURIList.class);

        headerListTable.put(PMediaAuthorizationHeader.class, PMediaAuthorizationList.class);

        headerListTable.put(PathHeader.class, PathList.class);

        headerListTable.put(PrivacyHeader.class,PrivacyList.class);

        headerListTable.put(ServiceRouteHeader.class, ServiceRouteList.class);

        headerListTable.put(PVisitedNetworkID.class, PVisitedNetworkIDList.class);

        headerListTable.put(SecurityClient.class, SecurityClientList.class);

        headerListTable.put(SecurityServer.class, SecurityServerList.class);

        headerListTable.put(SecurityVerify.class, SecurityVerifyList.class);

        headerListTable.put(PAssertedIdentity.class, PAssertedIdentityList.class);
        
        // https://java.net/jira/browse/JSIP-460
        headerListTable.put(ReasonHeader.class, ReasonList.class);

        initialized = true;

    }

    /**
     * return true if this has an associated list object.
     */
    static public boolean hasList(Header sipHeader) {
        if (sipHeader instanceof SIPHeaderList)
            return false;
        else {
            Class<?> headerClass = sipHeader.getClass().getInterfaces()[0];
            return headerListTable.get(headerClass) != null;
        }
    }

    /**
     * Return true if this has an associated list object.
     */
    static public boolean hasList(Class<?> sipHdrClass) {
        if (!initialized)
            initializeListMap();
        return headerListTable.get(sipHdrClass.getInterfaces()[0]) != null;
    }
    
    /**
     * Mark a listable Header 
     */
    static public void addListHeader(Class<? extends Header> sipHeaderClass,
                                     Class<? extends SIPHeaderList<? extends Header>> sipHeaderListClass)
    {
      headerListTable.put(sipHeaderClass, sipHeaderListClass);
    }

    /**
     * Get the associated list class.
     */
    static public Class<?> getListClass(Class<?> sipHdrClass) {
        if (!initialized)
            initializeListMap();
        return (Class<?>) headerListTable.get(sipHdrClass.getInterfaces()[0]);
    }

    /**
     * Return a list object for this header if it has an associated list object.
     */
    @SuppressWarnings("unchecked")
    static public SIPHeaderList<Header> getList(Header sipHeader) {
        if (!initialized)
            initializeListMap();
        try {
            Class<?> headerClass = sipHeader.getClass();
            Class<?> listClass =  headerListTable.get(headerClass.getInterfaces()[0]);
            SIPHeaderList<Header> shl = (SIPHeaderList<Header>) listClass.newInstance();
            shl.setHeaderName(sipHeader.getName());
            return shl;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

