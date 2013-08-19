package org.lightmare.utils;

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
}
