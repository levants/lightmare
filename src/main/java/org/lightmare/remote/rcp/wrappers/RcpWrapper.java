package org.lightmare.remote.rcp.wrappers;

/**
 * RPC response wrapper class for serialization
 * 
 * @author levan
 * @since 0.0.16-SNAPSHOT
 */
public class RcpWrapper {

    private boolean valid;

    private Object value;

    public boolean isValid() {
	return valid;
    }

    public void setValid(boolean valid) {
	this.valid = valid;
    }

    public Object getValue() {
	return value;
    }

    public void setValue(Object value) {
	this.value = value;
    }
}
