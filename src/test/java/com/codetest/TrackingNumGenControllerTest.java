package com.codetest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.codetest.controller.TrackingNumGenController;
import com.codetest.service.TrackingNumGenService;

@WebMvcTest(TrackingNumGenController.class)
public class TrackingNumGenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingNumGenService trackingNumberService;

    //Valid Input
    @Test
    public void testGenerateTrackingNumberValidInput() throws Exception {
        // Mocking the service response
        when(trackingNumberService.generateTrackingNumber(
                anyString(),
                anyString(),
                anyDouble(),
                anyString(),
                any(UUID.class),
                anyString(),
                anyString()))
                .thenReturn("USIN123456XYZ789");

        // Testing the endpoint
        mockMvc.perform(get("/api/v1/next-tracking-number")
                .param("origin_country_id", "US")
                .param("destination_country_id", "IN")
                .param("weight", "10.0")
                .param("created_at", "2025-01-09T10:15:30Z")
                .param("customer_id", UUID.randomUUID().toString())
                .param("customer_name", "Test Customer")
                .param("customer_slug", "test-customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tracking_number").value("USIN123456XYZ789"));
    }
    
    //Missing Required Parameters
    @Test
    public void testGenerateTrackingNumberWithMissingParameters() throws Exception {
        mockMvc.perform(get("/api/v1/next-tracking-number")
                .param("origin_country_id", "US") // Missing other required params
        )
        .andExpect(status().isBadRequest());
    }
    
    //Invalid Country Codes
    @Test
    public void testGenerateTrackingNumberWithInvalidCountryCodes() throws Exception {
        mockMvc.perform(get("/api/v1/next-tracking-number")
                .param("origin_country_id", "INVALID")
                .param("destination_country_id", "INVALID")
                .param("weight", "10.0")
                .param("created_at", "2025-01-09T10:15:30Z")
                .param("customer_id", UUID.randomUUID().toString())
                .param("customer_name", "Test Customer")
                .param("customer_slug", "test-customer"))
        .andExpect(status().isBadRequest());
    }
    
    //Negative Weight
    @Test
    public void testGenerateTrackingNumberWithNegativeWeight() throws Exception {
        mockMvc.perform(get("/api/v1/next-tracking-number")
                .param("origin_country_id", "US")
                .param("destination_country_id", "IN")
                .param("weight", "-1.0") // Negative weight
                .param("created_at", "2025-01-09T10:15:30Z")
                .param("customer_id", UUID.randomUUID().toString())
                .param("customer_name", "Test Customer")
                .param("customer_slug", "test-customer"))
        .andExpect(status().isBadRequest());
    }
    
    //Invalid Date Format
    @Test
    public void testGenerateTrackingNumberWithInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/next-tracking-number")
                .param("origin_country_id", "US")
                .param("destination_country_id", "IN")
                .param("weight", "10.0")
                .param("created_at", "invalid-date") // Invalid date format
                .param("customer_id", UUID.randomUUID().toString())
                .param("customer_name", "Test Customer")
                .param("customer_slug", "test-customer"))
        .andExpect(status().isBadRequest());
    }

    //Special Characters in Customer Slug
    @Test
    public void testGenerateTrackingNumberWithInvalidCustomerSlug() throws Exception {
        mockMvc.perform(get("/api/v1/next-tracking-number")
                .param("origin_country_id", "US")
                .param("destination_country_id", "IN")
                .param("weight", "10.0")
                .param("created_at", "2025-01-09T10:15:30Z")
                .param("customer_id", UUID.randomUUID().toString())
                .param("customer_name", "Test Customer")
                .param("customer_slug", "invalid_slug@123")) // Invalid slug
        .andExpect(status().isBadRequest());
    }


}
