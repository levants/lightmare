package org.lightmare.utils;

public class StringUtils {

    public static final String EMPTY_STRING = "";

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
}
