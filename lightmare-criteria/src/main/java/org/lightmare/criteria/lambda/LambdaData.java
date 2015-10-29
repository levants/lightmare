package org.lightmare.criteria.lambda;

import java.lang.invoke.SerializedLambda;

/**
 * Lambda expression meta information container class
 * 
 * @author Levan Tsinadze
 *
 */
public class LambdaData {

    private final String implClass;

    private final String implMethodName;

    private final String implMethodSignature;

    public LambdaData(final String implClass, final String implMethodName, final String implMethodSignature) {
	this.implClass = implClass;
	this.implMethodName = implMethodName;
	this.implMethodSignature = implMethodSignature;
    }

    public LambdaData(SerializedLambda serialized) {
	this(serialized.getImplClass(), serialized.getImplMethodName(), serialized.getImplMethodSignature());
    }

    public LambdaData(SLambda slambda) {
	this(slambda.implClass, slambda.implMethodName, slambda.implMethodSignature);
    }

    public String getImplClass() {
	return implClass;
    }

    public String getImplMethodName() {
	return implMethodName;
    }

    public String getImplMethodSignature() {
	return implMethodSignature;
    }
}
