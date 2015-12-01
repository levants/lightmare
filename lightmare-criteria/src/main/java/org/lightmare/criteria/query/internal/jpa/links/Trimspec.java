package org.lightmare.criteria.query.internal.jpa.links;

import org.lightmare.criteria.utils.StringUtils;

/**
 * Used to specify how strings are trimmed.
 */
public enum Trimspec {

    /**
     * Trim from leading end.
     */
    LEADING("LEADING "),

    /**
     * Trim from trailing end.
     */
    TRAILING("TRAILING "),

    /**
     * Trim from both ends.
     */
    BOTH("BOTH ");

    private final String prefix;

    private static final String FROM = "FROM";

    public final String pattern;

    private Trimspec(final String prefix) {
        this.prefix = prefix;
        this.pattern = StringUtils.concat(prefix, FROM);
    }

    public String locate(char ch) {
        return StringUtils.concat(prefix, StringUtils.QV, ch, StringUtils.QV, StringUtils.SPACE, FROM);
    }

    public static String locateAll(char ch) {
        return StringUtils.concat(StringUtils.QV, ch, StringUtils.QV, StringUtils.SPACE, FROM);
    }
}
