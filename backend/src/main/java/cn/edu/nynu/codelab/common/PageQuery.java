package cn.edu.nynu.codelab.common;

/**
 * Centralized page parameter normalization for public and admin list APIs.
 */
public final class PageQuery {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    private PageQuery() {
    }

    public static int normalizePage(int page) {
        return page > 0 ? page : DEFAULT_PAGE;
    }

    public static int normalizePageSize(int pageSize) {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    public static int normalizePage(Integer page) {
        return page == null ? DEFAULT_PAGE : normalizePage(page.intValue());
    }

    public static int normalizePageSize(Integer pageSize) {
        return pageSize == null ? DEFAULT_PAGE_SIZE : normalizePageSize(pageSize.intValue());
    }
}
