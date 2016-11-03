package ru.mail.park.main;

import org.springframework.http.ResponseEntity;

@SuppressWarnings("unused")
public class ApiResponse {
    private int code;
    private Object content;

    public ApiResponse() {
    }

    public ApiResponse(int code, Object content) {
        this.code = code;
        this.content = content;
    }

    public ApiResponse(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    public static ResponseEntity ok(Object content) {
        return ResponseEntity.ok(new ApiResponse(ResponseCode.OK.getCode(), content));
    }

    public static ResponseEntity parameterMissing() {
        return ResponseEntity.ok(new ApiResponse(ResponseCode.PARAMETER_MISSING));
    }

    public static ResponseEntity authError() {
        return ResponseEntity.ok(new ApiResponse(ResponseCode.AUTH_ERROR));
    }

    public static ResponseEntity duplicateUser() {
        return ResponseEntity.ok(new ApiResponse(ResponseCode.DUPLICATE_USER));
    }

    public static ResponseEntity dbError() {
        return ResponseEntity.ok(new ApiResponse(ResponseCode.DB_ERROR));
    }
}
