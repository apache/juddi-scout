package org.apache.ws.scout.registry;

import javax.xml.registry.JAXRResponse;
import javax.xml.registry.JAXRException;

/**
 * Implementation of JAXRResponse

 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 */
public class JAXRResponseImpl implements JAXRResponse {

    public final static int STATUS_SUCCESS = 0;
    public final static int STATUS_FAILURE = 1;
    public final static int STATUS_UNAVAILABLE = 2;
    public final static int STATUS_WARNING = 3;

    private int status = STATUS_SUCCESS;

    private String requestId = null;
    private boolean available = false;

    public String getRequestId() throws JAXRException {
        return this.requestId;
    }

    public void setRequestId(String s) {
        this.requestId = s;
    }

    public int getStatus() throws JAXRException {
        return this.status;
    }

    public void setStatus(int s) {
        this.status = s;
    }

    public void setAvailable(boolean b) {
        this.available = b;
    }
    public boolean isAvailable() throws JAXRException {
        return this.available;
    }
}
