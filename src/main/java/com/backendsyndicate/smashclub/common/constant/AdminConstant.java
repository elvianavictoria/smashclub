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

    // Additional Permissions
    public static final int PERMISSION_PRODUCT_SALES_PROCESS = 41;
    public static final int PERMISSION_BOOKING_SALES_PROCESS = 42;

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
    public static final String ADMIN_AUTH_SERVICE_LOGIN_REQUEST_INVALID = "ADMAUTH-01E001";
    public static final String ADMIN_AUTH_SERVICE_LOGIN_NOT_FOUND = "ADMAUTH-01E002";
    public static final String ADMIN_AUTH_SERVICE_LOGIN_PASSWORD_INVALID = "ADMAUTH-01E003";
    public static final String ADMIN_AUTH_SERVICE_LOGIN_STATUS_INACTIVE = "ADMAUTH-01E004";
    public static final String ADMIN_AUTH_SERVICE_LOGIN_TOKEN_INVALID = "ADMAUTH-01E005";
    public static final String ADMIN_AUTH_SERVICE_LOGIN_EXCEPTION = "ADMAUTH-01E010";
    public static final String ADMIN_AUTH_SERVICE_LOGOUT_FAILED = "ADMAUTH-02E001";
    public static final String ADMIN_AUTH_SERVICE_LOGOUT_EXCEPTION = "ADMAUTH-02E010";
    public static final String ADMIN_AUTH_SERVICE_AUTHENTICATED_TOKEN_REQUIRED = "ADMAUTH-03E001";
    public static final String ADMIN_AUTH_SERVICE_AUTHENTICATED_TOKEN_INVALID = "ADMAUTH-03E002";
    public static final String ADMIN_AUTH_SERVICE_AUTHENTICATED_NOT_FOUND = "ADMAUTH-03E003";
    public static final String ADMIN_AUTH_SERVICE_AUTHENTICATED_STATUS_INACTIVE = "ADMAUTH-03E004";
    public static final String ADMIN_AUTH_SERVICE_AUTHENTICATED_EXCEPTION = "ADMAUTH-03E010";
    public static final String ADMIN_AUTH_SERVICE_GET_EXCEPTION = "ADMAUTH-04E010";
    public static final String ADMIN_AUTH_SERVICE_UPDATE_TOKEN_REQUIRED = "ADMAUTH-05E001";
    public static final String ADMIN_AUTH_SERVICE_UPDATE_TOKEN_INVALID = "ADMAUTH-05E002";
    public static final String ADMIN_AUTH_SERVICE_UPDATE_NOT_FOUND = "ADMAUTH-05E003";
    public static final String ADMIN_AUTH_SERVICE_UPDATE_EXCEPTION = "ADMAUTH-05E010";

    public static final String ADMIN_SESSION_SERVICE_GET_TOKEN_REQUIRED = "ADMSESS-01E001";
    public static final String ADMIN_SESSION_SERVICE_GET_TOKEN_INVALID = "ADMSESS-01E002";
    public static final String ADMIN_SESSION_SERVICE_GET_EXCEPTION = "ADMSESS-01E010";
    public static final String ADMIN_SESSION_SERVICE_SAVE_EXCEPTION = "ADMSESS-02E010";
    public static final String ADMIN_SESSION_SERVICE_INVALIDATE_TOKEN_INVALID = "ADMSESS-03E001";
    public static final String ADMIN_SESSION_SERVICE_ISVALID_TOKEN_REQUIRED = "ADMSESS-04E001";
    public static final String ADMIN_SESSION_SERVICE_ISVALID_EXCEPTION = "ADMSESS-04E010";

    public static final String ADMIN_DASHBOARD_SERVICE_DASHBOARD_EXCEPTION = "ADMDSBRD-01E010";

    public static final String ADMIN_ROLE_SERVICE_LIST_EMPTY = "ADMROLE-01E001";
    public static final String ADMIN_ROLE_SERVICE_LIST_EXCEPTION = "ADMROLE-01E010";
    public static final String ADMIN_ROLE_SERVICE_DETAIL_ID_REQUIRED = "ADMROLE-02E001";
    public static final String ADMIN_ROLE_SERVICE_DETAIL_NOT_FOUND = "ADMROLE-02E002";
    public static final String ADMIN_ROLE_SERVICE_DETAIL_EXCEPTION = "ADMROLE-02E010";
    public static final String ADMIN_ROLE_SERVICE_SAVE_REQUEST_INVALID = "ADMROLE-03E001";
    public static final String ADMIN_ROLE_SERVICE_SAVE_EXCEPTION = "ADMROLE-03E010";
    public static final String ADMIN_ROLE_SERVICE_UPDATE_ID_REQUIRED = "ADMROLE-04E001";
    public static final String ADMIN_ROLE_SERVICE_UPDATE_REQUEST_INVALID = "ADMROLE-04E002";
    public static final String ADMIN_ROLE_SERVICE_UPDATE_NOT_FOUND = "ADMROLE-04E003";
    public static final String ADMIN_ROLE_SERVICE_UPDATE_EXCEPTION = "ADMROLE-04E010";
    public static final String ADMIN_ROLE_SERVICE_DELETE_ID_REQUIRED = "ADMROLE-05E001";
    public static final String ADMIN_ROLE_SERVICE_DELETE_NOT_FOUND = "ADMROLE-05E002";
    public static final String ADMIN_ROLE_SERVICE_DELETE_EXCEPTION = "ADMROLE-05E010";

    public static final String ADMIN_PERMISSION_SERVICE_LIST_EMPTY = "ADMPRMS-01E001";
    public static final String ADMIN_PERMISSION_SERVICE_LIST_EXCEPTION = "ADMPRMS-01E010";

    public static final String ADMIN_USER_SERVICE_LIST_EMPTY = "ADMUSR-01E001";
    public static final String ADMIN_USER_SERVICE_LIST_EXCEPTION = "ADMUSR-01E010";
    public static final String ADMIN_USER_SERVICE_DETAIL_ID_REQUIRED = "ADMUSR-02E001";
    public static final String ADMIN_USER_SERVICE_DETAIL_NOT_FOUND = "ADMUSR-02E002";
    public static final String ADMIN_USER_SERVICE_DETAIL_EXCEPTION = "ADMUSR-02E010";
    public static final String ADMIN_USER_SERVICE_SAVE_REQUEST_INVALID = "ADMUSR-03E001";
    public static final String ADMIN_USER_SERVICE_SAVE_PASSWORD_INVALID = "ADMUSR-03E002";
    public static final String ADMIN_USER_SERVICE_SAVE_EXCEPTION = "ADMUSR-03E010";
    public static final String ADMIN_USER_SERVICE_UPDATE_ID_REQUIRED = "ADMUSR-04E001";
    public static final String ADMIN_USER_SERVICE_UPDATE_REQUEST_INVALID = "ADMUSR-04E002";
    public static final String ADMIN_USER_SERVICE_UPDATE_NOT_FOUND = "ADMUSR-04E003";
    public static final String ADMIN_USER_SERVICE_UPDATE_EXCEPTION = "ADMUSR-04E010";
    public static final String ADMIN_USER_SERVICE_DELETE_ID_REQUIRED = "ADMUSR-05E001";
    public static final String ADMIN_USER_SERVICE_DELETE_NOT_FOUND = "ADMUSR-05E002";
    public static final String ADMIN_USER_SERVICE_DELETE_EXCEPTION = "ADMUSR-05E010";
    public static final String ADMIN_USER_SERVICE_SAVE_FILE_REQUEST_INVALID = "ADMUSR-13E001";
    public static final String ADMIN_USER_SERVICE_SAVE_FILE_IMAGE_ERROR = "ADMUSR-13E002";
    public static final String ADMIN_USER_SERVICE_UPDATE_FILE_ID_REQUIRED = "ADMUSR-14E001";
    public static final String ADMIN_USER_SERVICE_UPDATE_FILE_REQUEST_INVALID = "ADMUSR-14E002";
    public static final String ADMIN_USER_SERVICE_UPDATE_FILE_IMAGE_ERROR = "ADMUSR-14E003";

    public static final String ADMIN_COACH_SERVICE_LIST_EMPTY = "ADMCCH-01E001";
    public static final String ADMIN_COACH_SERVICE_LIST_EXCEPTION = "ADMCCH-01E010";
    public static final String ADMIN_COACH_SERVICE_DETAIL_ID_REQUIRED = "ADMCCH-02E001";
    public static final String ADMIN_COACH_SERVICE_DETAIL_NOT_FOUND = "ADMCCH-02E002";
    public static final String ADMIN_COACH_SERVICE_DETAIL_EXCEPTION = "ADMCCH-02E010";
    public static final String ADMIN_COACH_SERVICE_SAVE_REQUEST_INVALID = "ADMCCH-03E001";
    public static final String ADMIN_COACH_SERVICE_SAVE_EXCEPTION = "ADMCCH-03E010";
    public static final String ADMIN_COACH_SERVICE_UPDATE_ID_REQUIRED = "ADMCCH-04E001";
    public static final String ADMIN_COACH_SERVICE_UPDATE_REQUEST_INVALID = "ADMCCH-04E002";
    public static final String ADMIN_COACH_SERVICE_UPDATE_NOT_FOUND = "ADMCCH-04E003";
    public static final String ADMIN_COACH_SERVICE_UPDATE_EXCEPTION = "ADMCCH-04E010";
    public static final String ADMIN_COACH_SERVICE_DELETE_ID_REQUIRED = "ADMCCH-05E001";
    public static final String ADMIN_COACH_SERVICE_DELETE_NOT_FOUND = "ADMCCH-05E002";
    public static final String ADMIN_COACH_SERVICE_DELETE_EXCEPTION = "ADMCCH-05E010";
    public static final String ADMIN_COACH_SERVICE_SAVE_FILE_REQUEST_INVALID = "ADMCCH-13E001";
    public static final String ADMIN_COACH_SERVICE_SAVE_FILE_IMAGE_ERROR = "ADMCCH-13E002";
    public static final String ADMIN_COACH_SERVICE_UPDATE_FILE_ID_REQUIRED = "ADMCCH-14E001";
    public static final String ADMIN_COACH_SERVICE_UPDATE_FILE_REQUEST_INVALID = "ADMCCH-14E002";
    public static final String ADMIN_COACH_SERVICE_UPDATE_FILE_IMAGE_ERROR = "ADMCCH-14E003";

    public static final String ADMIN_COURT_SERVICE_LIST_EMPTY = "ADMCRT-01E001";
    public static final String ADMIN_COURT_SERVICE_LIST_EXCEPTION = "ADMCRT-01E010";
    public static final String ADMIN_COURT_SERVICE_DETAIL_ID_REQUIRED = "ADMCRT-02E001";
    public static final String ADMIN_COURT_SERVICE_DETAIL_NOT_FOUND = "ADMCRT-02E002";
    public static final String ADMIN_COURT_SERVICE_DETAIL_EXCEPTION = "ADMCRT-02E010";
    public static final String ADMIN_COURT_SERVICE_SAVE_REQUEST_INVALID = "ADMCRT-03E001";
    public static final String ADMIN_COURT_SERVICE_SAVE_EXCEPTION = "ADMCRT-03E010";
    public static final String ADMIN_COURT_SERVICE_UPDATE_ID_REQUIRED = "ADMCRT-04E001";
    public static final String ADMIN_COURT_SERVICE_UPDATE_REQUEST_INVALID = "ADMCRT-04E002";
    public static final String ADMIN_COURT_SERVICE_UPDATE_NOT_FOUND = "ADMCRT-04E003";
    public static final String ADMIN_COURT_SERVICE_UPDATE_EXCEPTION = "ADMCRT-04E010";
    public static final String ADMIN_COURT_SERVICE_DELETE_ID_REQUIRED = "ADMCRT-05E001";
    public static final String ADMIN_COURT_SERVICE_DELETE_NOT_FOUND = "ADMCRT-05E002";
    public static final String ADMIN_COURT_SERVICE_DELETE_EXCEPTION = "ADMCRT-05E010";
    public static final String ADMIN_COURT_SERVICE_SAVE_FILE_REQUEST_INVALID = "ADMCRT-13E001";
    public static final String ADMIN_COURT_SERVICE_SAVE_FILE_IMAGE_ERROR = "ADMCRT-13E002";
    public static final String ADMIN_COURT_SERVICE_UPDATE_FILE_ID_REQUIRED = "ADMCRT-14E001";
    public static final String ADMIN_COURT_SERVICE_UPDATE_FILE_REQUEST_INVALID = "ADMCRT-14E002";
    public static final String ADMIN_COURT_SERVICE_UPDATE_FILE_IMAGE_ERROR = "ADMCRT-14E003";

    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_LIST_EMPTY = "ADMEQPCAT-01E001";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_LIST_EXCEPTION = "ADMEQPCAT-01E010";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_ID_REQUIRED = "ADMEQPCAT-02E001";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_NOT_FOUND = "ADMEQPCAT-02E002";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_DETAIL_EXCEPTION = "ADMEQPCAT-02E010";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_SAVE_REQUEST_INVALID = "ADMEQPCAT-03E001";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_SAVE_EXCEPTION = "ADMEQPCAT-03E010";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_ID_REQUIRED = "ADMEQPCAT-04E001";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_REQUEST_INVALID = "ADMEQPCAT-04E002";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_NOT_FOUND = "ADMEQPCAT-04E003";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_UPDATE_EXCEPTION = "ADMEQPCAT-04E010";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_ID_REQUIRED = "ADMEQPCAT-05E001";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_NOT_FOUND = "ADMEQPCAT-05E002";
    public static final String ADMIN_EQUIPMENT_CATEGORY_SERVICE_DELETE_EXCEPTION = "ADMEQPCAT-05E010";

    public static final String ADMIN_EQUIPMENT_SERVICE_LIST_EMPTY = "ADMEQP-01E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_LIST_EXCEPTION = "ADMEQP-01E010";
    public static final String ADMIN_EQUIPMENT_SERVICE_DETAIL_ID_REQUIRED = "ADMEQP-02E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_DETAIL_NOT_FOUND = "ADMEQP-02E002";
    public static final String ADMIN_EQUIPMENT_SERVICE_DETAIL_EXCEPTION = "ADMEQP-02E010";
    public static final String ADMIN_EQUIPMENT_SERVICE_SAVE_REQUEST_INVALID = "ADMEQP-03E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_SAVE_EXCEPTION = "ADMEQP-03E010";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_ID_REQUIRED = "ADMEQP-04E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_REQUEST_INVALID = "ADMEQP-04E002";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_NOT_FOUND = "ADMEQP-04E003";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_EXCEPTION = "ADMEQP-04E010";
    public static final String ADMIN_EQUIPMENT_SERVICE_DELETE_ID_REQUIRED = "ADMEQP-05E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_DELETE_NOT_FOUND = "ADMEQP-05E002";
    public static final String ADMIN_EQUIPMENT_SERVICE_DELETE_EXCEPTION = "ADMEQP-05E010";
    public static final String ADMIN_EQUIPMENT_SERVICE_SAVE_FILE_REQUEST_INVALID = "ADMEQP-13E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_SAVE_FILE_IMAGE_ERROR = "ADMEQP-13E002";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_ID_REQUIRED = "ADMEQP-14E001";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_REQUEST_INVALID = "ADMEQP-14E002";
    public static final String ADMIN_EQUIPMENT_SERVICE_UPDATE_FILE_IMAGE_ERROR = "ADMEQP-14E003";

    public static final String ADMIN_PRODUCT_SERVICE_LIST_EMPTY = "ADMPRD-01E001";
    public static final String ADMIN_PRODUCT_SERVICE_LIST_EXCEPTION = "ADMPRD-01E010";
    public static final String ADMIN_PRODUCT_SERVICE_DETAIL_ID_REQUIRED = "ADMPRD-02E001";
    public static final String ADMIN_PRODUCT_SERVICE_DETAIL_NOT_FOUND = "ADMPRD-02E002";
    public static final String ADMIN_PRODUCT_SERVICE_DETAIL_EXCEPTION = "ADMPRD-02E010";
    public static final String ADMIN_PRODUCT_SERVICE_SAVE_REQUEST_INVALID = "ADMPRD-03E001";
    public static final String ADMIN_PRODUCT_SERVICE_SAVE_EXCEPTION = "ADMPRD-03E010";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_ID_REQUIRED = "ADMPRD-04E001";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_REQUEST_INVALID = "ADMPRD-04E002";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_NOT_FOUND = "ADMPRD-04E003";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_VARIANT_FAILED = "ADMPRD-04E004";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_EXCEPTION = "ADMPRD-04E010";
    public static final String ADMIN_PRODUCT_SERVICE_DELETE_ID_REQUIRED = "ADMPRD-05E001";
    public static final String ADMIN_PRODUCT_SERVICE_DELETE_NOT_FOUND = "ADMPRD-05E002";
    public static final String ADMIN_PRODUCT_SERVICE_DELETE_EXCEPTION = "ADMPRD-05E010";
    public static final String ADMIN_PRODUCT_SERVICE_SAVE_FILE_REQUEST_INVALID = "ADMPRD-13E001";
    public static final String ADMIN_PRODUCT_SERVICE_SAVE_FILE_IMAGE_ERROR = "ADMPRD-13E002";
    public static final String ADMIN_PRODUCT_SERVICE_SAVE_FILE_VARIANT_FAILED = "ADMPRD-13E003";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_FILE_ID_REQUIRED = "ADMPRD-14E001";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_FILE_REQUEST_INVALID = "ADMPRD-14E002";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_FILE_IMAGE_ERROR = "ADMPRD-14E003";
    public static final String ADMIN_PRODUCT_SERVICE_UPDATE_FILE_VARIANT_FAILED = "ADMPRD-13E004";

    public static final String ADMIN_PRODUCT_VARIANT_SERVICE_SAVE_PRODUCT_NOT_FOUND = "ADMPRDVAR-01E001";
    public static final String ADMIN_PRODUCT_VARIANT_SERVICE_SAVE_EXCEPTION = "ADMPRDVAR-01E010";
    public static final String ADMIN_PRODUCT_VARIANT_SERVICE_DELETE_EXCEPTION = "ADMPRDVAR-02E010";

    public static final String ADMIN_PLAYER_SERVICE_LIST_EMPTY = "ADMPLYR-01E001";
    public static final String ADMIN_PLAYER_SERVICE_LIST_EXCEPTION = "ADMPLYR-01E010";
    public static final String ADMIN_PLAYER_SERVICE_DETAIL_ID_REQUIRED = "ADMPLYR-02E001";
    public static final String ADMIN_PLAYER_SERVICE_DETAIL_NOT_FOUND = "ADMPLYR-02E002";
    public static final String ADMIN_PLAYER_SERVICE_DETAIL_EXCEPTION = "ADMPLYR-02E010";
    public static final String ADMIN_PLAYER_SERVICE_UPDATE_ID_REQUIRED = "ADMPLYR-04E001";
    public static final String ADMIN_PLAYER_SERVICE_UPDATE_REQUEST_INVALID = "ADMPLYR-04E002";
    public static final String ADMIN_PLAYER_SERVICE_UPDATE_NOT_FOUND = "ADMPLYR-04E003";
    public static final String ADMIN_PLAYER_SERVICE_UPDATE_VARIANT_FAILED = "ADMPLYR-04E004";
    public static final String ADMIN_PLAYER_SERVICE_UPDATE_EXCEPTION = "ADMPLYR-04E010";
    public static final String ADMIN_PLAYER_SERVICE_DELETE_ID_REQUIRED = "ADMPLYR-05E001";
    public static final String ADMIN_PLAYER_SERVICE_DELETE_NOT_FOUND = "ADMPLYR-05E002";
    public static final String ADMIN_PLAYER_SERVICE_DELETE_EXCEPTION = "ADMPLYR-05E010";

    public static final String ADMIN_REFUND_REQUEST_SERVICE_LIST_EMPTY = "ADMRRQ-01E001";
    public static final String ADMIN_REFUND_REQUEST_SERVICE_LIST_EXCEPTION = "ADMRRQ-01E010";
    public static final String ADMIN_REFUND_REQUEST_SERVICE_PROCESS_ID_REQUIRED = "ADMRRQ-02E001";
    public static final String ADMIN_REFUND_REQUEST_SERVICE_PROCESS_NOT_FOUND = "ADMRRQ-02E002";
    public static final String ADMIN_REFUND_REQUEST_SERVICE_PROCESS_PROCESSED = "ADMRRQ-02E003";
    public static final String ADMIN_REFUND_REQUEST_SERVICE_PROCESS_EXCEPTION = "ADMRRQ-02E010";

    public static final String ADMIN_SALES_SERVICE_STATISTIC_EXCEPTION = "ADMSLS-01E010";
    public static final String ADMIN_SALES_SERVICE_LIST_EMPTY = "ADMSLS-02E001";
    public static final String ADMIN_SALES_SERVICE_LIST_EXCEPTION = "ADMSLS-02E010";
    public static final String ADMIN_SALES_SERVICE_DETAIL_CODE_REQUIRED = "ADMSLS-03E001";
    public static final String ADMIN_SALES_SERVICE_DETAIL_NOT_FOUND = "ADMSLS-03E002";
    public static final String ADMIN_SALES_SERVICE_DETAIL_EXCEPTION = "ADMSLS-03E010";

    public static final String ADMIN_BOOKING_SERVICE_STATISTIC_EXCEPTION = "ADMBKG-01E010";
    public static final String ADMIN_BOOKING_SERVICE_LIST_EMPTY = "ADMBKG-02E001";
    public static final String ADMIN_BOOKING_SERVICE_LIST_EXCEPTION = "ADMBKG-02E010";
    public static final String ADMIN_BOOKING_SERVICE_DETAIL_CODE_REQUIRED = "ADMBKG-03E001";
    public static final String ADMIN_BOOKING_SERVICE_DETAIL_NOT_FOUND = "ADMBKG-03E002";
    public static final String ADMIN_BOOKING_SERVICE_DETAIL_EXCEPTION = "ADMBKG-03E010";
    public static final String ADMIN_BOOKING_SERVICE_PROCESS_CODE_REQUIRED = "ADMBKG-04E001";
    public static final String ADMIN_BOOKING_SERVICE_PROCESS_NOT_FOUND = "ADMBKG-04E002";
    public static final String ADMIN_BOOKING_SERVICE_PROCESS_NOT_CANCELLABLE = "ADMBKG-04E003";
    public static final String ADMIN_BOOKING_SERVICE_PROCESS_INACTIVE = "ADMBKG-04E004";
    public static final String ADMIN_BOOKING_SERVICE_PROCESS_EXCEPTION = "ADMBKG-04E010";

    public static final String ADMIN_ORDER_SERVICE_STATISTIC_EXCEPTION = "ADMORD-01E010";
    public static final String ADMIN_ORDER_SERVICE_LIST_EMPTY = "ADMORD-02E001";
    public static final String ADMIN_ORDER_SERVICE_LIST_EXCEPTION = "ADMORD-02E010";
    public static final String ADMIN_ORDER_SERVICE_DETAIL_CODE_REQUIRED = "ADMORD-03E001";
    public static final String ADMIN_ORDER_SERVICE_DETAIL_NOT_FOUND = "ADMORD-03E002";
    public static final String ADMIN_ORDER_SERVICE_DETAIL_EXCEPTION = "ADMORD-03E010";
    public static final String ADMIN_ORDER_SERVICE_PROCESS_CODE_REQUIRED = "ADMORD-04E001";
    public static final String ADMIN_ORDER_SERVICE_PROCESS_NOT_FOUND = "ADMORD-04E002";
    public static final String ADMIN_ORDER_SERVICE_PROCESS_NOT_CANCELLABLE = "ADMORD-04E003";
    public static final String ADMIN_ORDER_SERVICE_PROCESS_INACTIVE = "ADMORD-04E004";
    public static final String ADMIN_ORDER_SERVICE_PROCESS_EXCEPTION = "ADMORD-04E010";
}
