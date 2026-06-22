package cn.edu.nynu.codelab.common;

/**
 * Shared validation patterns for request DTOs.
 */
public final class ValidationPatterns {

    public static final String HTTP_URL = "^(|https?://[^\\s]+)$";

    public static final String HTTP_OR_RELATIVE_URL = "^(|https?://[^\\s]+|/(?!/)(?!.*\\.\\.)[^\\s]*)$";

    private ValidationPatterns() {
    }
}
