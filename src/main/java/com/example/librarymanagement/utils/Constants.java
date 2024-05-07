package com.example.librarymanagement.utils;

public final class Constants {
    private Constants() {
    }

    public final class ERROR_CODE {
        public static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";
        public static final String USER_WITH_EMAIL_NOT_FOUND = "USER_WITH_EMAIL_NOT_FOUND";
        public static final String EMAIL_WITH_PASSWORD_INCORRECT = "EMAIL_WITH_PASSWORD_INCORRECT";
        public static final String USER_NOT_VERIFIED = "USER_NOT_VERIFIED";
        public static final String EMAIL_EXISTED = "EMAIL_EXISTED";
        public static final String VERIFICATION_TOKEN_INVALID = "VERIFICATION_TOKEN_INVALID ";
        public static final String USER_BLOCKED = "USER_BLOCKED";
        public static final String USER_SESSION_INVALID = "USER_SESSION_INVALID";
        public static final String WRONG_EMAIL_FORMAT = "WRONG_EMAIL_FORMAT";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND";
        public static final String VERIFICATION_QR_NOT_FOUND = "VERIFICATION_QR_NOT_FOUND";
        public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
        public static final String BOOK_CATEGORY_NOT_FOUND = "BOOK_CATEGORY_NOT_FOUND";
        public static final String BOOK_PUBLISHER_NOT_FOUND = "BOOK_PUBLISHER_NOT_FOUND";
        public static final String BOOK_AUTHOR_NOT_FOUND = "BOOK_AUTHOR_NOT_FOUND";
        public static final String BOOK_NOT_FOUND = "BOOK_NOT_FOUND";
        public static final String NEED_CHANGE_PASSWORD = "NEED_CHANGE_PASSWORD";
        public static final String AWS_S3_UPLOAD_OBJECT_ERROR = "AWS_S3_UPLOAD_OBJECT_ERROR";
        public static final String UNAUTHENTICATED = "ACTION FAILED, PLEASE LOGIN";
    }
}
