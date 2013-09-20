package org.lightmare.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

public class MetaUtilsTest {

    @Test
    public void modifiersTest() {

	int publicMod = Modifier.PUBLIC;
	int privateMod = Modifier.PRIVATE;
	int protectedMod = Modifier.PROTECTED;
	int staticMod = Modifier.STATIC;
	int finalMod = Modifier.FINAL;

	Assert.assertTrue("modifiers not match", Modifier.isPublic(publicMod));
	Assert.assertTrue("modifiers not match", Modifier.isPrivate(privateMod));
	Assert.assertTrue("modifiers not match",
		Modifier.isProtected(protectedMod));
	Assert.assertTrue("modifiers not match", Modifier.isStatic(staticMod));
	Assert.assertTrue("modifiers not match", Modifier.isFinal(finalMod));
    }

    @Test
    public void modifiersEqTest() {

	int publicMod = Modifier.PUBLIC;
	int staticMod = Modifier.STATIC;
	int finalMod = Modifier.FINAL;

	Assert.assertTrue("modifiers not match", Modifier.isPublic(publicMod));
	Assert.assertTrue("modifiers not match", Modifier.isStatic(staticMod));
	Assert.assertTrue("modifiers not match", Modifier.isFinal(finalMod));

	int modifier = publicMod | staticMod | finalMod;

	Assert.assertTrue("modifiers not match", Modifier.isPublic(modifier));
	Assert.assertTrue("modifiers not match", Modifier.isStatic(modifier));
	Assert.assertTrue("modifiers not match", Modifier.isFinal(modifier));
    }

    private void method4() {

	try {
	    Class<?> thisClass = this.getClass();
	    String name = thisClass.getName();
	    String callerName = null;
	    String methodName = null;
	    boolean last = Boolean.FALSE;
	    StackTraceElement[] elements = Thread.currentThread()
		    .getStackTrace();
	    if (CollectionUtils.available(elements)) {
		int length = elements.length;
		int trace = 0;
		StackTraceElement element;
		for (int i = 0; i < length && trace < 2; i++) {

		    element = elements[i];
		    callerName = element.getClassName();
		    last = name.equals(callerName);

		    if (last) {
			methodName = element.getMethodName();
			trace++;
		    }

		    System.out.println(StringUtils.concat(
			    element.getClassName(), StringUtils.SPACE,
			    element.getMethodName(), StringUtils.SPACE,
			    element.getLineNumber(), StringUtils.SPACE,
			    element.getFileName()));
		}

		System.out.println(callerName);
		System.out.println(methodName);

		Method method = this.getClass().getEnclosingMethod();
		System.out.println(StringUtils.concat(method));
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    private void method3() {
	method4();
    }

    private void method2() {
	method3();
    }

    private void method1() {
	method2();
    }

    @Test
    public void callerMethodTest() {

	method1();
    }
}
