package com.backendsyndicate.smashclub.common.constant;

public class AdminConstant {
    // Menu Categories
    public static final int MENU_CATEGORY_DASHBOARD = 1;
    public static final int MENU_CATEGORY_MASTER_DATA = 2;
    public static final int MENU_CATEGORY_REPORT = 3;
    public static final int MENU_CATEGORY_USER = 4;

    // Menus
    public static final int MENU_DASHBOARD = 1;
    public static final int MENU_COURT = 2;
    public static final int MENU_COACH = 3;
    public static final int MENU_EQUIPMENT_CATEGORY = 4;
    public static final int MENU_EQUIPMENT = 5;
    public static final int MENU_PRODUCT = 6;
    public static final int MENU_PLAYER = 7;
    public static final int MENU_SALES = 8;
    public static final int MENU_BOOKING_SALES = 9;
    public static final int MENU_PRODUCT_SALES = 10;
    public static final int MENU_REFUND_REQUEST = 11;
    public static final int MENU_ROLES = 12;
    public static final int MENU_USER = 13;

    // Permissions
    public static final int PERMISSION_DASHBOARD_READ = 1;

    public static final int PERMISSION_COURT_READ = 2;
    public static final int PERMISSION_COURT_CREATE = 3;
    public static final int PERMISSION_COURT_EDIT = 4;
    public static final int PERMISSION_COURT_DELETE = 5;

    public static final int PERMISSION_COACH_READ = 6;
    public static final int PERMISSION_COACH_CREATE = 7;
    public static final int PERMISSION_COACH_EDIT = 8;
    public static final int PERMISSION_COACH_DELETE = 9;

    public static final int PERMISSION_EQUIPMENT_CATEGORY_READ = 10;
    public static final int PERMISSION_EQUIPMENT_CATEGORY_CREATE = 11;
    public static final int PERMISSION_EQUIPMENT_CATEGORY_EDIT = 12;
    public static final int PERMISSION_EQUIPMENT_CATEGORY_DELETE = 13;

    public static final int PERMISSION_EQUIPMENT_READ = 14;
    public static final int PERMISSION_EQUIPMENT_CREATE = 15;
    public static final int PERMISSION_EQUIPMENT_EDIT = 16;
    public static final int PERMISSION_EQUIPMENT_DELETE = 17;

    public static final int PERMISSION_PRODUCT_READ = 18;
    public static final int PERMISSION_PRODUCT_CREATE = 19;
    public static final int PERMISSION_PRODUCT_EDIT = 20;
    public static final int PERMISSION_PRODUCT_DELETE = 21;

    public static final int PERMISSION_PLAYER_READ = 22;
    public static final int PERMISSION_PLAYER_EDIT = 23;
    public static final int PERMISSION_PLAYER_DELETE = 24;

    public static final int PERMISSION_SALES_READ = 25;
    public static final int PERMISSION_SALES_DETAIL = 26;

    public static final int PERMISSION_BOOKING_SALES_READ = 27;
    public static final int PERMISSION_BOOKING_SALES_DETAIL = 28;

    public static final int PERMISSION_PRODUCT_SALES_READ = 29;
    public static final int PERMISSION_PRODUCT_SALES_DETAIL = 30;

    public static final int PERMISSION_REFUND_REQUEST_READ = 31;
    public static final int PERMISSION_REFUND_REQUEST_EDIT = 32;

    public static final int PERMISSION_ROLES_READ = 33;
    public static final int PERMISSION_ROLES_CREATE = 34;
    public static final int PERMISSION_ROLES_EDIT = 35;
    public static final int PERMISSION_ROLES_DELETE = 36;

    public static final int PERMISSION_USERS_READ = 37;
    public static final int PERMISSION_USERS_CREATE = 38;
    public static final int PERMISSION_USERS_EDIT = 39;
    public static final int PERMISSION_USERS_DELETE = 40;

    // Roles
    public static final int ROLE_DEVELOPER = 1;
    public static final int ROLE_ADMIN = 2;
    public static final int ROLE_USER = 3;

    // Equipment Categories
    public static final long EQUIPMENT_CATEGORY_RACQUET = 1L;
    public static final long EQUIPMENT_CATEGORY_BALL = 2L;

    // Products
    public static final long PRODUCT_INDOMIE = 1L;
    public static final long PRODUCT_AQUAFINA = 2L;
    public static final long PRODUCT_REDBULL = 3L;
    public static final long PRODUCT_KATSURICE = 4L;
    public static final long PRODUCT_WILSON_RACQUET = 5L;

    public static final String PRODUCT_IMG_INDOMIE = "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8bWllJTIwaW5zdGFufGVufDB8fDB8fHwy";
    public static final String PRODUCT_IMG_INDOMIE_VAR_1 = "https://images.unsplash.com/photo-1628610688436-e635552020fc?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8bWllJTIwaW5zdGFufGVufDB8fDB8fHwy";
    public static final String PRODUCT_IMG_AQUAFINA = "https://images.unsplash.com/photo-1629470937827-9f1c9b9df448?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    public static final String PRODUCT_IMG_AQUAFINA_VAR_1 = "https://images.unsplash.com/photo-1741518516414-a2eff67493d1?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8YXF1YWZpbmF8ZW58MHx8MHx8fDI%3D";
    public static final String PRODUCT_IMG_REDBULL = "https://images.unsplash.com/photo-1642532560930-77d5018c68f7?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    public static final String PRODUCT_IMG_REDBULL_VAR_1 = "https://images.unsplash.com/photo-1632858280935-d5611683e434?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8cmVkJTIwYnVsbHxlbnwwfHwwfHx8Mg%3D%3D";
    public static final String PRODUCT_IMG_KATSURICE = "https://images.unsplash.com/photo-1679279726946-a158b8bcaa23?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    public static final String PRODUCT_IMG_KATSURICE_VAR_1 = "https://images.unsplash.com/photo-1591814252471-068b545dff62?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8a2F0c3UlMjByaWNlfGVufDB8fDB8fHwy";
    public static final String PRODUCT_IMG_WILSON_RACQUET = "https://images.unsplash.com/photo-1542144582-1ba00456b5e3?q=80&w=778&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    public static final String PRODUCT_IMG_WILSON_RACQUET_VAR_1 = "https://images.unsplash.com/photo-1602211847326-dd96f7c45f10?q=80&w=715&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";

    // Error Codes
    public static final String ADMIN_DASHBOARD_SERVICE_DASHBOARD_EXCEPTION = "ADMDSBRD-01E010";
}
