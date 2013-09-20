package org.lightmare.utils;

import java.lang.reflect.Array;

/**
 * Utility class for {@link String} operations
 * 
 * @author Levan
 * 
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    public static final char SPACE = ' ';

    public static final char HYPHEN = '-';

    public static final char DOT = '.';

    public static final String TAB = "\t";

    public static final String NEWLINE = "\n";

    public static final int NOT_EXISTING_INDEX = -1;

    /**
     * Appends contents of passed array to passed {@link StringBuilder} and for
     * each content if it is instance of array then append its content
     * recursively
     * 
     * @param tockens
     * @param builder
     */
    private static void append(Object tocken, StringBuilder builder) {

	if (CollectionUtils.isObjectArray(tocken)) {
	    Object[] tockens = ObjectUtils.cast(tocken);
	    for (Object subTocken : tockens) {
		append(subTocken, builder);
	    }
	} else if (CollectionUtils.isPrimitiveArray(tocken)) {
	    int length = Array.getLength(tocken);
	    Object subTocken;
	    for (int i = 0; i < length; i++) {
		subTocken = Array.get(tocken, i);
		append(subTocken, builder);
	    }
	} else {
	    builder.append(tocken);
	}
    }

    /**
     * Creates concatenates passed objects in one text and if one of them is
     * array then concatenates contents of this array recursively
     * 
     * @param tockens
     * @return {@link String}
     */
    public static String concatRecursively(Object... tockens) {

	String concat;

	if (ObjectUtils.available(tockens)) {
	    StringBuilder builder = new StringBuilder();
	    append(tockens, builder);
	    concat = builder.toString();
	} else {
	    concat = null;
	}

	return concat;
    }

    /**
     * Concatenates passed {@link Object}s in one {@link String} instance
     * 
     * @param parts
     * @return {@link String}
     */
    public static String concat(Object... parts) {

	String resultText;

	if (ObjectUtils.available(parts)) {

	    StringBuilder resultBuider = new StringBuilder();
	    for (Object part : parts) {
		resultBuider.append(part);
	    }

	    resultText = resultBuider.toString();
	} else {
	    resultText = null;
	}

	return resultText;
    }

    public static boolean notContains(String text, String item) {

	boolean contains = text.contains(item);

	return ObjectUtils.notTrue(contains);
    }
}
