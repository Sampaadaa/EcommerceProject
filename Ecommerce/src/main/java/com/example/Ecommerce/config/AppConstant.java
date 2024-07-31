package com.example.Ecommerce.config;

public class AppConstant {
    public static final String pageNumber= "0";
    public static final String pageSize= "10";
    public static final String SORT_CATEGORIES_BY = "categoryId";
    public static final String SORT_PRODUCTS_BY = "productId";
    public static final String SORT_USERS_BY = "userId";
    public static final String SORT_DIR = "asc";
    public static final Long ADMIN_ID = 101L;
    public static final Long USER_ID = 102L;
    public static final String[] PUBLIC_URLS = { "/api/v1/auth/**", "/v3/api-docs", "/v2/api-docs", "/api", "/api/**",
            "/api/public", "/api/public/**", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**",
            "/swagger-ui.html" };

    public static final String[] USER_URLS = { "/api/public/**" };
    public static final String[] ADMIN_URLS = { "/api/admin/**" };


}
