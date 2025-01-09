package com.codetest.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TrackingNumGenService {

	public String generateTrackingNumber(String originCountryId, String destinationCountryId, double weight,
			String createdAt, UUID customerId, String customerName, String customerSlug) {

		long uniqueId = System.currentTimeMillis() + (long) (Math.random() * 1000);
		System.out.println("uniqueId::"+uniqueId);
		String uniqueIdBase36 = Long.toString(uniqueId, 36).toUpperCase();
		System.out.println("uniqueIdBase36::"+uniqueIdBase36);
		uniqueIdBase36 = uniqueIdBase36.length() > 16 ? uniqueIdBase36.substring(0, 16) : uniqueIdBase36;
		System.out.println("uniqueIdBase362::"+uniqueIdBase36);
		String trackingNumber = originCountryId + destinationCountryId + uniqueIdBase36 + weight;
		System.out.println("trackingNumber::"+trackingNumber);
		return trackingNumber.length() > 16 ? trackingNumber.toUpperCase().substring(0, 16).replaceAll("[^A-Z0-9]", "X") :
			trackingNumber.toUpperCase().replaceAll("[^A-Z0-9]", "X");
	}
}
