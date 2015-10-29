package org.lightmare.linq.lambda;

import java.io.Serializable;

/**
 * Container class for {@link java.lang.invoke.SerializedLambda} fields
 * 
 * @author Levan Tsinadze
 * @see java.lang.invoke.SerializedLambda
 */
class SLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;

    public String implClass;

    public String implMethodName;

    public String implMethodSignature;
}
