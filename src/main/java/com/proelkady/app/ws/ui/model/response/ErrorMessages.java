package com.proelkady.app.ws.ui.model.response;

public enum ErrorMessages {
	MISSING_REQUIRED_FIELD("Missing required field"), RECORD_ALREADY_EXISTS("Record aleady exists"),
	INTERNAL_SERVER_ERROR("Internal server error"), NO_RECORD_FOUND("Record not found"),
	AUTHENTICATION_FAILED("Authentication failed"), COULD_NOT_UPDATE_RECORD("Could not update record"),
	COULD_NOT_DELETE_RECORD("Could not delete record"),
	EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified");

	private String errorMsg;

	ErrorMessages(String msg) {
		this.errorMsg = msg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
