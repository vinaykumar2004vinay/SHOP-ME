package com.ecommerce.dto;


/**
 * ApiResponse — standard envelope for all API responses.
 * Keeps frontend handling consistent: always check success flag first.
 *
 * Example: { "success": true, "message": "Login successful", "data": {...} }
 */

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Convenience factory methods for clean code at call sites
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ApiResponse [success=" + success + ", message=" + message + ", data=" + data + "]";
	}

	public ApiResponse(boolean success, String message, T data) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public ApiResponse() {
		super();
	}
    
    
    
}
