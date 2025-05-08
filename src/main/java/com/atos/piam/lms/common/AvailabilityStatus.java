package com.atos.piam.lms.common;

public enum AvailabilityStatus {
	AVAILABLE("AVAILABLE"), LOANED_OUT("LOANED_OUT"); // Future states: RESERVED, DAMAGED, LOST

	private String value;

	AvailabilityStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}