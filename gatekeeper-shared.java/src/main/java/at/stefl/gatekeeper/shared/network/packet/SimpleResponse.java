package at.stefl.gatekeeper.shared.network.packet;

public class SimpleResponse {

	public static enum Status {
		OK, WARNING, ERROR;
	}

	private Status status;
	private int code;
	private String message;

	public Status getStatus() {
		return status;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
