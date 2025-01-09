package com.codetest.model;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TrackingNumGenResponse {
	
	private String tracking_number;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")  // RFC 3339 format
    private String created_at;
	
	public TrackingNumGenResponse(String tracking_number, String created_at) {
        this.tracking_number = tracking_number;
        this.created_at = created_at;
    }

	public String getTracking_number() {
		return tracking_number;
	}

	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}
