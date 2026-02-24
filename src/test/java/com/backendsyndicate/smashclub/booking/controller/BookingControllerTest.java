package com.backendsyndicate.smashclub.booking.controller;

import com.backendsyndicate.smashclub.booking.BookingTestBase;
import com.backendsyndicate.smashclub.booking.dto.request.*;
import com.backendsyndicate.smashclub.booking.service.BookingService;
import com.backendsyndicate.smashclub.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingControllerTest extends BookingTestBase {

    @Mock
    private BookingService bookingService;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private BookingController bookingController;

    private final String authHeader = "Bearer valid.jwt.token";
    private CourtAvailabilityRequest courtAvailabilityRequest;
    private BookingRequest bookingRequest;
    private BookingStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    void setUp() {
        courtAvailabilityRequest = createCourtAvailabilityRequest(TEST_COURT_ID);
        bookingRequest = createBookingRequest();
        statusUpdateRequest = createBookingStatusUpdateRequest((byte) 2); // CONFIRMED

        when(authService.getUserIdFromToken("valid.jwt.token")).thenReturn(TEST_USER_ID);
        when(authService.getUserIdFromToken("invalid-token")).thenReturn(null);
    }

    @Nested
    @DisplayName("POST /api/v1/booking/availability/courts")
    class CheckCourtAvailabilityTests {

        @Test
        @DisplayName("Should check court availability")
        void checkCourtAvailability_WithValidRequest_ShouldCallService() {
            // Arrange
            when(bookingService.checkCourtAvailability(eq(courtAvailabilityRequest), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.checkCourtAvailability(
                    courtAvailabilityRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).checkCourtAvailability(eq(courtAvailabilityRequest), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking/availability/coaches")
    class GetAvailableCoachesTests {

        private final LocalDate date = LocalDate.now().plusDays(1);
        private final LocalTime startTime = LocalTime.of(10, 0);
        private final LocalTime endTime = LocalTime.of(12, 0);

        @Test
        @DisplayName("Should get available coaches")
        void getAvailableCoaches_WithValidParams_ShouldCallService() {
            // Arrange
            when(bookingService.getAvailableCoaches(eq(date), eq(startTime), eq(endTime), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getAvailableCoaches(
                    date, startTime, endTime, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getAvailableCoaches(eq(date), eq(startTime), eq(endTime), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking/availability/equipment")
    class GetAvailableEquipmentTests {

        private final LocalDate date = LocalDate.now().plusDays(1);
        private final LocalTime startTime = LocalTime.of(10, 0);
        private final LocalTime endTime = LocalTime.of(12, 0);
        private final Integer quantity = 2;

        @Test
        @DisplayName("Should get available equipment with quantity")
        void getAvailableEquipment_WithQuantity_ShouldCallService() {
            // Arrange
            when(bookingService.getAvailableEquipment(eq(date), eq(startTime), eq(endTime), eq(quantity), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getAvailableEquipment(
                    date, startTime, endTime, quantity, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getAvailableEquipment(eq(date), eq(startTime), eq(endTime), eq(quantity), eq(httpRequest));
        }

        @Test
        @DisplayName("Should get available equipment without quantity")
        void getAvailableEquipment_WithoutQuantity_ShouldCallServiceWithNull() {
            // Arrange
            when(bookingService.getAvailableEquipment(eq(date), eq(startTime), eq(endTime), isNull(), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getAvailableEquipment(
                    date, startTime, endTime, null, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getAvailableEquipment(eq(date), eq(startTime), eq(endTime), isNull(), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/booking/summary")
    class GetBookingSummaryTests {

        @Test
        @DisplayName("Should get booking summary")
        void getBookingSummary_WithValidRequest_ShouldCallService() {
            // Arrange
            when(bookingService.getBookingSummary(eq(bookingRequest), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getBookingSummary(
                    bookingRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getBookingSummary(eq(bookingRequest), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/booking")
    class CreateBookingTests {

        @Test
        @DisplayName("Should create booking when authorized")
        void createBooking_WithValidToken_ShouldCallService() {
            // Arrange
            when(bookingService.createBooking(eq(bookingRequest), eq(TEST_USER_ID), eq(httpRequest)))
                    .thenReturn(createdResponse);

            // Act
            ResponseEntity<Object> response = bookingController.createBooking(
                    bookingRequest, authHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(bookingService).createBooking(eq(bookingRequest), eq(TEST_USER_ID), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void createBooking_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";

            // Act
            ResponseEntity<Object> response = bookingController.createBooking(
                    bookingRequest, invalidHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(bookingService, never()).createBooking(any(), any(), any());
        }

        @Test
        @DisplayName("Should return 401 when header is null")
        void createBooking_WithNullHeader_ShouldReturnUnauthorized() {
            // Act
            ResponseEntity<Object> response = bookingController.createBooking(
                    bookingRequest, null, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(bookingService, never()).createBooking(any(), any(), any());
        }

        @Test
        @DisplayName("Should return 401 when header is not Bearer")
        void createBooking_WithNonBearerHeader_ShouldReturnUnauthorized() {
            // Act
            ResponseEntity<Object> response = bookingController.createBooking(
                    bookingRequest, "Basic credentials", httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(bookingService, never()).createBooking(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking/{bookingCode}")
    class GetBookingDetailsTests {

        @Test
        @DisplayName("Should get booking details")
        void getBookingDetails_WithValidCode_ShouldCallService() {
            // Arrange
            when(bookingService.getBookingDetails(eq(TEST_BOOKING_CODE), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getBookingDetails(
                    TEST_BOOKING_CODE, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getBookingDetails(eq(TEST_BOOKING_CODE), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking/my-bookings")
    class GetMyBookingsTests {

        @Test
        @DisplayName("Should get user bookings with default pagination")
        void getMyBookings_WithValidToken_ShouldCallServiceWithDefaultPageable() {
            // Arrange
            Pageable expectedPageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

            when(bookingService.getUserBookings(eq(TEST_USER_ID), any(Pageable.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getMyBookings(
                    authHeader, 0, 10, "createdAt", "desc", httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getUserBookings(eq(TEST_USER_ID), any(Pageable.class), eq(httpRequest));
        }

        @Test
        @DisplayName("Should get user bookings with custom pagination")
        void getMyBookings_WithCustomPagination_ShouldCallService() {
            // Arrange
            Pageable expectedPageable = PageRequest.of(1, 5, Sort.by("bookingDate").ascending());

            when(bookingService.getUserBookings(eq(TEST_USER_ID), any(Pageable.class), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getMyBookings(
                    authHeader, 1, 5, "bookingDate", "asc", httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getUserBookings(eq(TEST_USER_ID), any(Pageable.class), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void getMyBookings_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";

            // Act
            ResponseEntity<Object> response = bookingController.getMyBookings(
                    invalidHeader, 0, 10, "createdAt", "desc", httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(bookingService, never()).getUserBookings(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/booking/{bookingCode}/status")
    class UpdateBookingStatusTests {

        @Test
        @DisplayName("Should update booking status")
        void updateBookingStatus_WithValidRequest_ShouldCallService() {
            // Arrange
            when(bookingService.updateBookingStatus(eq(TEST_BOOKING_CODE), eq(statusUpdateRequest), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.updateBookingStatus(
                    TEST_BOOKING_CODE, statusUpdateRequest, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).updateBookingStatus(eq(TEST_BOOKING_CODE), eq(statusUpdateRequest), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking/courts")
    class GetAllCourtsTests {

        @Test
        @DisplayName("Should get all courts")
        void getAllCourts_ShouldCallService() {
            // Arrange
            when(bookingService.getAllCourts(eq(httpRequest))).thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.getAllCourts(httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).getAllCourts(eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/booking/{bookingCode}/start")
    class StartBookingTests {

        @Test
        @DisplayName("Should start booking")
        void startBooking_WithValidCode_ShouldCallService() {
            // Arrange
            when(bookingService.startBooking(eq(TEST_BOOKING_CODE), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.startBooking(
                    TEST_BOOKING_CODE, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).startBooking(eq(TEST_BOOKING_CODE), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/booking/{bookingCode}/complete")
    class CompleteBookingTests {

        @Test
        @DisplayName("Should complete booking")
        void completeBooking_WithValidCode_ShouldCallService() {
            // Arrange
            when(bookingService.completeBooking(eq(TEST_BOOKING_CODE), eq(httpRequest)))
                    .thenReturn(successResponse);

            // Act
            ResponseEntity<Object> response = bookingController.completeBooking(
                    TEST_BOOKING_CODE, httpRequest);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(bookingService).completeBooking(eq(TEST_BOOKING_CODE), eq(httpRequest));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/booking/{bookingCode}/payment")
    class CreateBookingPaymentTests {

        private final int paymentMethodId = 1;

        @Test
        @DisplayName("Should create payment when authorized")
        void createBookingPayment_WithValidToken_ShouldCallService() {
            // Arrange
            when(bookingService.createBookingPayment(eq(TEST_BOOKING_CODE), eq(paymentMethodId), eq(httpRequest)))
                    .thenReturn(createdResponse);

            // Act
            ResponseEntity<Object> response = bookingController.createBookingPayment(
                    TEST_BOOKING_CODE, paymentMethodId, authHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(bookingService).createBookingPayment(eq(TEST_BOOKING_CODE), eq(paymentMethodId), eq(httpRequest));
        }

        @Test
        @DisplayName("Should return 401 when token is invalid")
        void createBookingPayment_WithInvalidToken_ShouldReturnUnauthorized() {
            // Arrange
            String invalidHeader = "Bearer invalid-token";

            // Act
            ResponseEntity<Object> response = bookingController.createBookingPayment(
                    TEST_BOOKING_CODE, paymentMethodId, invalidHeader, httpRequest);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(bookingService, never()).createBookingPayment(any(), anyInt(), any());
        }
    }

    @Nested
    @DisplayName("Helper Method: extractUserIdFromToken")
    class ExtractUserIdFromTokenTests {

        @Test
        @DisplayName("Should extract user ID from valid token")
        void extractUserIdFromToken_WithValidToken_ShouldReturnUserId() throws Exception {
            // Use reflection to test private method
            java.lang.reflect.Method method = BookingController.class.getDeclaredMethod(
                    "extractUserIdFromToken", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(bookingController, authHeader);
            assertEquals(TEST_USER_ID, result);
        }

        @Test
        @DisplayName("Should return null when token is invalid")
        void extractUserIdFromToken_WithInvalidToken_ShouldReturnNull() throws Exception {
            java.lang.reflect.Method method = BookingController.class.getDeclaredMethod(
                    "extractUserIdFromToken", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(bookingController, "invalid-token");
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when header is null")
        void extractUserIdFromToken_WithNullHeader_ShouldReturnNull() throws Exception {
            java.lang.reflect.Method method = BookingController.class.getDeclaredMethod(
                    "extractUserIdFromToken", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(bookingController, (Object) null);
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when header is not Bearer")
        void extractUserIdFromToken_WithNonBearerHeader_ShouldReturnNull() throws Exception {
            java.lang.reflect.Method method = BookingController.class.getDeclaredMethod(
                    "extractUserIdFromToken", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(bookingController, "Basic credentials");
            assertNull(result);
        }
    }
}