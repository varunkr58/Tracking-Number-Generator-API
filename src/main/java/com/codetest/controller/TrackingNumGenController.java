package com.codetest.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetest.model.ErrorResponse;
import com.codetest.model.TrackingNumGenResponse;
import com.codetest.service.TrackingNumGenService;

@RestController
@RequestMapping("/api/v1")
public class TrackingNumGenController {

	private final TrackingNumGenService trackingNumberService;

	public TrackingNumGenController(TrackingNumGenService trackingNumberService) {
		this.trackingNumberService = trackingNumberService;
	}

	@GetMapping("/next-tracking-number")
	public ResponseEntity<?> getNextTrackingNumber(@RequestParam String origin_country_id,
			@RequestParam String destination_country_id, @RequestParam double weight, @RequestParam String created_at,
			@RequestParam UUID customer_id, @RequestParam String customer_name, @RequestParam String customer_slug) {

		// Validate inputs (controller-level validation)
		List<ValidationCheck> validationChecks = List.of(
				new ValidationCheck("Please provide a valid origin_country_id value.",
						() -> isValidCountryCode(origin_country_id)),
				new ValidationCheck("Please provide a valid destination_country_id value.",
						() -> isValidCountryCode(destination_country_id)),
				new ValidationCheck("Weight must be greater than 0.", () -> weight > 0),
				new ValidationCheck("Please provide a valid created_at value.", () -> isValidDate(created_at)),
				new ValidationCheck("customer_name is required.",
						() -> customer_name != null && !customer_name.trim().isEmpty()),
				new ValidationCheck("Please provide a valid customer_slug value.",
						() -> customer_slug != null && customer_slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")));

		// Perform all validations and collect errors
		List<String> errors = validationChecks.stream().filter(check -> !check.getValidation().getAsBoolean())
				.map(ValidationCheck::getErrorMessage) // Get error message
				.collect(Collectors.toList());

		// If there are any errors, return a bad request response
		if (!errors.isEmpty()) {
			ErrorResponse errorResponse = new ErrorResponse("Validation failed", errors);
	        return ResponseEntity.badRequest().body(errorResponse);
		}

		// Delegate tracking number generation to the service
		String trackingNumber = trackingNumberService.generateTrackingNumber(origin_country_id, destination_country_id,
				weight, created_at, customer_id, customer_name, customer_slug);
		
		// Get current timestamp in RFC 3339 format
        String currentTimestamp = OffsetDateTime.now().toString();
        
        TrackingNumGenResponse response = new TrackingNumGenResponse(trackingNumber, currentTimestamp);

		return ResponseEntity.ok(response);
	}
	
	// Helper method to validate country code
    private boolean isValidCountryCode(String countryCode) {
        return countryCode != null && countryCode.matches("^[A-Z]{2}$");
    }

    // Helper method to validate RFC 3339 date format
    private boolean isValidDate(String date) {
        try {
            OffsetDateTime.parse(date); // RFC 3339 format validation
            return true;
        } catch (Exception e) {
            return false;
        }
    }

	public static class ValidationCheck {

		private final String errorMessage;
		private final BooleanSupplier validation;

		public ValidationCheck(String errorMessage, BooleanSupplier validation) {
			this.errorMessage = errorMessage;
			this.validation = validation;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public BooleanSupplier getValidation() {
			return validation;
		}

	}

}
