package com.proelkady.app.ws.ui.model.response;

public class OperationStatusModel {
	private String operationName;
	private String operationResult;

	public OperationStatusModel() {
		super();
	}

	public OperationStatusModel(String operationName, String operationResult) {
		super();
		this.operationName = operationName;
		this.operationResult = operationResult;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationResult() {
		return operationResult;
	}

	public void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}

}
