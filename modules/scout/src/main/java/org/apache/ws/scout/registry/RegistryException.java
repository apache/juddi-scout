/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.scout.registry;

import org.apache.ws.scout.uddi.DispositionReport;
import org.apache.ws.scout.uddi.DispositionReportDocument;
import org.apache.ws.scout.uddi.ErrInfo;
import org.apache.ws.scout.uddi.Result;

/**
 * Thrown to indicate that a UDDI Exception was encountered.
 * 
 * <i>Borrowed from jUDDI project.</i>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryException extends Exception
{

	public static final int E_ASSERTION_NOT_FOUND = 30000;
	public static final int E_AUTH_TOKEN_EXPIRED = 10110;
	public static final int E_AUTH_TOKEN_REQUIRED = 10120;
	public static final int E_ACCOUNT_LIMIT_EXCEEDED = 10160;
	public static final int E_BUSY = 10400;
	public static final int E_CATEGORIZATION_NOT_ALLOWED = 20100;
	public static final int E_FATAL_ERROR = 10500;
	public static final int E_INVALID_KEY_PASSED = 10210;
	public static final int E_INVALID_PROJECTION = 20230;
	public static final int E_INVALID_CATEGORY = 20000;
	public static final int E_INVALID_COMPLETION_STATUS = 30100;
	public static final int E_INVALID_URL_PASSED = 10220;
	public static final int E_INVALID_VALUE = 20200;
	public static final int E_KEY_RETIRED = 10310;
	public static final int E_LANGUAGE_ERROR = 10060;
	public static final int E_MESSAGE_TOO_LARGE = 30110;
	public static final int E_NAME_TOO_LONG = 10020;
	public static final int E_OPERATOR_MISMATCH = 10130;
	public static final int E_PUBLISHER_CANCELLED = 30220;
	public static final int E_REQUEST_DENIED = 30210;
	public static final int E_SECRET_UNKNOWN = 30230;
	public static final int E_SUCCESS = 0;
	public static final int E_TOO_MANY_OPTIONS = 10030;
	public static final int E_TRANSFER_ABORTED = 30200;
	public static final int E_UNRECOGNIZED_VERSION = 10040;
	public static final int E_UNKNOWN_USER = 10150;
	public static final int E_UNSUPPORTED = 10050;
	public static final int E_USER_MISMATCH = 10140;
	public static final int E_VALUE_NOT_ALLOWED = 20210;
	public static final int E_UNVALIDATABLE = 20220;
	public static final int E_REQUEST_TIMEOUT = 20240;
	public static final int E_INVALID_TIME = 40030;
	public static final int E_RESULT_SET_TOO_LARGE = 40300;

  // SOAP SOAPFault Actor
  private String faultActor;

  // SOAP SOAPFault Code
  private String faultCode;

  // SOAP SOAPFault SOAPMessage
  private String faultString;

  // UDDI DispositionReport
  private DispositionReport dispReport;

  /**
   * Constructs a RegistryException instance.
   * @param msg additional error information
   */
  public RegistryException(String msg)
  {
    super(msg);

    setFaultCode(null);
    setFaultString(msg);
    setFaultActor(null);
  }

  /**
   * Constructs a RegistryException instance.
   * @param ex the original exception
   */
  public RegistryException(Exception ex)
  {
    super(ex);
    
    if (ex != null)
    {
      // Not sure why this would ever happen but 
      // just in case we are asked to create a new
      // RegistryException using values from another
      // let's be sure to grab all relevant values.
      //
      if (ex instanceof RegistryException)
      {
        RegistryException regex = (RegistryException)ex;
        setFaultCode(regex.getFaultCode());
        setFaultString(regex.getFaultString());
        setFaultActor(regex.getFaultActor());
        setDispositionReport(regex.getDispositionReport());
      }
      else // Not a RegistryException (or subclass)
      {
        setFaultString(ex.getMessage());
      }
    }
  }

  /**
   * Constructs a RegistryException instance.
   * @param ex the original exception
   */
  public RegistryException(String fCode,String fString,String fActor,DispositionReport dispRpt)
  {
    super(fString);

    setFaultCode(fCode);
    setFaultString(fString);
    setFaultActor(fActor);
    setDispositionReport(dispRpt);
  }

  /**
   * Constructs a RegistryException instance.
   * @param ex the original exception
   */
  RegistryException(String fCode,int errno,String msg)
  {
    super(buildMessage(errno,msg));

    String errCode = lookupErrCode(errno);

    if (fCode != null) {
    	setFaultCode(fCode);
    }
    
    setFaultString(getMessage());
    
    Result r = Result.Factory.newInstance();
    ErrInfo ei = ErrInfo.Factory.newInstance();

    if (errCode != null) {
    	ei.setErrCode(errCode);
    }

    ei.setStringValue(getMessage());

   	r.setErrno(errno);

    if (ei != null) {
    	r.setErrInfo(ei);
    }

    addResult(r);
  }

  /**
   * Sets the fault actor of this SOAP SOAPFault to the given value.
   * @param actor The new actor value for this SOAP SOAPFault.
   */
  public void setFaultActor(String actor)
  {
    this.faultActor = actor;
  }

  /**
   * Returns the fault actor of this SOAP SOAPFault.
   * @return The fault actor of this SOAP SOAPFault.
   */
  public String getFaultActor()
  {
    return this.faultActor;
  }

  /**
   * Sets the fault code of this SOAP SOAPFault to the given value.
   * @param code The new code number for this SOAP SOAPFault.
   */
  public void setFaultCode(String code)
  {
    this.faultCode = code;
  }

  /**
   * Returns the fault code of this SOAP SOAPFault.
   * @return The fault code of this SOAP SOAPFault.
   */
  public String getFaultCode()
  {
    return this.faultCode;
  }

  /**
   * Sets the fault string of this SOAP SOAPFault to the given value.
   * @param value The new fault string for this SOAP SOAPFault.
   */
  public void setFaultString(String value)
  {
    this.faultString = value;
  }

  /**
   * Returns the fault string of this SOAP SOAPFault.
   * @return The fault string of this SOAP SOAPFault.
   */
  public String getFaultString()
  {
    return this.faultString;
  }

  /**
   * Sets the UDDI DispositionReport value to the instance
   * specified
   * @param dispRpt The new UDDI DispositionReport instance for
   *  this SOAP Fault.
   */
  public void setDispositionReport(DispositionReport dispRpt)
  {
    this.dispReport = dispRpt;
  }

  /**
   * Returns the disposition report associated with this jUDDI exception. It
   * uses the results Vector to determine if a disposition report is present
   * and should be returned.
   * @return The disposition report associated with this jUDDI exception.
   */
  public DispositionReport getDispositionReport()
  {
    return this.dispReport;
  }

  /**
   * Adds a result instance to this Exception. Multiple result objects
   * may exist within a DispositionReport
   */
  public void addResult(Result result)
  {
    if (this.dispReport==null) {
  	  DispositionReportDocument doc = DispositionReportDocument.Factory.newInstance();
      this.dispReport = doc.addNewDispositionReport();
    }

    Result r = this.dispReport.addNewResult();
    
    if (result.getErrInfo() != null) r.setErrInfo(result.getErrInfo());
    if (result.getKeyType() != null) r.setKeyType(result.getKeyType());
    r.setErrno(result.getErrno());
  }

  /**
   *
   */
  public String toString()
  {
    String msg = getMessage();
    if (msg == null)
      return "";
    else
      return getMessage();
  }
  
  private static final String buildMessage(int errno,String msg)
  {
    StringBuffer buffer = new StringBuffer();
    
    String errCode = lookupErrCode(errno);
    if (errCode != null)
    {
      buffer.append(errCode);
      buffer.append(" ");
    }
    
    buffer.append("(");
    buffer.append(errno);
    buffer.append(") ");
    
    //String errText = lookupErrText(errno);
    // FIXME: What should error text be?
    String errText = "";
    if (errText != null)
    {
      buffer.append(errText);
      buffer.append(" ");
    }
    
    if ((msg != null) && (msg.trim().length() > 0))
    {
      buffer.append(msg);
    }
    
    return buffer.toString();
  }

  public static final String lookupErrCode(int errno)
  {
	  switch (errno)
	  {
	  case E_ACCOUNT_LIMIT_EXCEEDED     : return "E_accountLimitExceeded";
	  case E_ASSERTION_NOT_FOUND        : return "E_assertionNotFound"; 
	  case E_AUTH_TOKEN_EXPIRED         : return "E_authTokenExpired";
	  case E_AUTH_TOKEN_REQUIRED        : return "E_authTokenRequired";
	  case E_BUSY                       : return "E_busy";
	  case E_CATEGORIZATION_NOT_ALLOWED : return "E_categorizationNotAllowed";
	  case E_FATAL_ERROR                : return "E_fatalError";
	  case E_INVALID_CATEGORY           : return "E_invalidCategory";
	  case E_INVALID_COMPLETION_STATUS  : return "E_invalidCompletionStatus";
	  case E_INVALID_KEY_PASSED         : return "E_invalidKeyPassed";
	  case E_INVALID_PROJECTION         : return "E_invalidProjection";
	  case E_INVALID_TIME               : return "E_invalidTime";
	  case E_INVALID_URL_PASSED         : return "E_invalidURLPassed";
	  case E_INVALID_VALUE              : return "E_invalidValue";
	  case E_KEY_RETIRED                : return "E_keyRetired";
	  case E_LANGUAGE_ERROR             : return "E_languageError";
	  case E_MESSAGE_TOO_LARGE          : return "E_messageTooLarge";
	  case E_NAME_TOO_LONG              : return "E_nameTooLong";
	  case E_OPERATOR_MISMATCH          : return "E_operatorMismatch";
	  case E_PUBLISHER_CANCELLED        : return "E_publisherCancelled";
	  case E_REQUEST_DENIED             : return "E_requestDenied";
	  case E_REQUEST_TIMEOUT            : return "E_requestTimeout";
	  case E_RESULT_SET_TOO_LARGE       : return "E_resultSetTooLarge";
	  case E_SECRET_UNKNOWN             : return "E_secretUnknown";
	  case E_SUCCESS                    : return "E_success";
	  case E_TOO_MANY_OPTIONS           : return "E_tooManyOptions";
	  case E_TRANSFER_ABORTED           : return "E_transferAborted";
	  case E_UNKNOWN_USER               : return "E_unknownUser";
	  case E_UNRECOGNIZED_VERSION       : return "E_unrecognizedVersion";
	  case E_UNSUPPORTED                : return "E_unsupported";
	  case E_UNVALIDATABLE              : return "E_unvalidatable";
	  case E_USER_MISMATCH              : return "E_userMismatch";
	  case E_VALUE_NOT_ALLOWED          : return "E_valueNotAllowed";
	  default                           : return null;
	  }
  }  
}