package com.backendsyndicate.smashclub.booking;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.booking.dto.request.*;
import com.backendsyndicate.smashclub.booking.model.*;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
public abstract class BookingTestBase {

    protected static final String TEST_USER_ID = "test-user-id";
    protected static final String TEST_BOOKING_CODE = "BK-240225-0001";
    protected static final Long TEST_COURT_ID = 1L;
    protected static final Long TEST_COACH_ID = 1L;
    protected static final Long TEST_EQUIPMENT_ID = 1L;
    protected static final Long TEST_EQUIPMENT_CATEGORY_ID = 1L;

    protected User testUser;
    protected Court testCourt;
    protected Coach testCoach;
    protected Equipment testEquipment;
    protected EquipmentCategory testEquipmentCategory;
    protected Booking testBooking;
    protected CoachDetail testCoachDetail;
    protected EquipmentDetail testEquipmentDetail;

    protected ResponseEntity<Object> successResponse;
    protected ResponseEntity<Object> errorResponse;
    protected ResponseEntity<Object> notFoundResponse;
    protected ResponseEntity<Object> createdResponse;

    @BeforeEach
    void setUpBase() {
        testUser = createTestUser();
        testCourt = createTestCourt();
        testCoach = createTestCoach();
        testEquipmentCategory = createTestEquipmentCategory();
        testEquipment = createTestEquipment();
        testBooking = createTestBooking();
        testCoachDetail = createTestCoachDetail();
        testEquipmentDetail = createTestEquipmentDetail();

        successResponse = ResponseEntity.ok().build();
        errorResponse = ResponseEntity.badRequest().build();
        notFoundResponse = ResponseEntity.status(404).build();
        createdResponse = ResponseEntity.status(201).build();
    }

    // ============ HELPER METHODS TO CREATE TEST ENTITIES ============

    protected User createTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        return user;
    }

    protected Court createTestCourt() {
        Court court = new Court();
        court.setId(TEST_COURT_ID);
        court.setCourtCode("CRT-001");
        court.setCourtName("Main Court");
        court.setCourtImgLink("https://example.com/court.jpg");
        court.setOpenTime(LocalTime.of(8, 0));
        court.setCloseTime(LocalTime.of(22, 0));
        court.setPricePerHour(new BigDecimal("100000"));
        court.setStatus(BookingConstant.RESOURCE_ACTIVE);
        court.setCreatedAt(LocalDateTime.now());
        court.setUpdatedAt(LocalDateTime.now());
        return court;
    }

    protected Coach createTestCoach() {
        Coach coach = new Coach();
        coach.setId(TEST_COACH_ID);
        coach.setCoachCode("COACH-001");
        coach.setCoachName("John Doe");
        coach.setCoachImgLink("https://example.com/coach.jpg");
        coach.setPricePerHour(new BigDecimal("50000"));
        coach.setStatus(BookingConstant.RESOURCE_ACTIVE);
        coach.setCreatedAt(LocalDateTime.now());
        return coach;
    }

    protected EquipmentCategory createTestEquipmentCategory() {
        EquipmentCategory category = new EquipmentCategory();
        category.setId(TEST_EQUIPMENT_CATEGORY_ID);
        category.setCategoryName("Racket");
        category.setStatus(BookingConstant.RESOURCE_ACTIVE);
        return category;
    }

    protected Equipment createTestEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(TEST_EQUIPMENT_ID);
        equipment.setEquipmentName("Pro Racket");
        equipment.setBrand("Yonex");
        equipment.setType("Badminton");
        equipment.setPrice(new BigDecimal("50000"));
        equipment.setStock(10);
        equipment.setEquipmentImgLink("https://example.com/racket.jpg");
        equipment.setDescription("High quality racket");
        equipment.setStatus(BookingConstant.RESOURCE_ACTIVE);
        equipment.setEquipmentCategory(testEquipmentCategory);
        equipment.setCreatedAt(LocalDateTime.now());
        return equipment;
    }

    protected Booking createTestBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBookingCode(TEST_BOOKING_CODE);
        booking.setUser(testUser);
        booking.setCourt(testCourt);
        booking.setBookingDate(LocalDate.now().plusDays(1));
        booking.setStartTime(LocalTime.of(10, 0));
        booking.setEndTime(LocalTime.of(12, 0));
        booking.setDurationHour(2);
        booking.setBasePrice(new BigDecimal("200000"));
        booking.setTotalPrice(new BigDecimal("300000"));
        booking.setStatus(BookingConstant.BOOKING_PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }

    protected CoachDetail createTestCoachDetail() {
        CoachDetail coachDetail = new CoachDetail();
        coachDetail.setId(1L);
        coachDetail.setCoach(testCoach);
        coachDetail.setBooking(testBooking);
        coachDetail.setCoachHour(2);
        coachDetail.setBookingDate(testBooking.getBookingDate());
        coachDetail.setStartTime(testBooking.getStartTime());
        coachDetail.setEndTime(testBooking.getEndTime());
        coachDetail.setCoachPrice(new BigDecimal("100000"));
        coachDetail.setCreatedAt(LocalDateTime.now());
        return coachDetail;
    }

    protected EquipmentDetail createTestEquipmentDetail() {
        EquipmentDetail equipmentDetail = new EquipmentDetail();
        equipmentDetail.setId(1L);
        equipmentDetail.setEquipment(testEquipment);
        equipmentDetail.setBooking(testBooking);
        equipmentDetail.setQuantity(2);
        equipmentDetail.setStartTime(testBooking.getStartTime());
        equipmentDetail.setEndTime(testBooking.getEndTime());
        equipmentDetail.setEquipmentPrice(new BigDecimal("100000"));
        equipmentDetail.setCreatedAt(LocalDateTime.now());
        return equipmentDetail;
    }

    // ============ REQUEST BUILDERS ============

    protected CourtAvailabilityRequest createCourtAvailabilityRequest(Long courtId) {
        CourtAvailabilityRequest request = new CourtAvailabilityRequest();
        request.setDate(LocalDate.now().plusDays(1));
        request.setCourtId(courtId);
        return request;
    }

    protected BookingRequest createBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setCourtId(TEST_COURT_ID);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        return request;
    }

    protected BookingRequest createBookingRequestWithCoach() {
        BookingRequest request = createBookingRequest();
        request.setCoaches(List.of(createCoachSelectionRequest()));
        return request;
    }

    protected BookingRequest createBookingRequestWithEquipment() {
        BookingRequest request = createBookingRequest();
        request.setEquipment(List.of(createEquipmentSelectionRequest()));
        return request;
    }

    protected BookingRequest createBookingRequestWithAll() {
        BookingRequest request = createBookingRequest();
        request.setCoaches(List.of(createCoachSelectionRequest()));
        request.setEquipment(List.of(createEquipmentSelectionRequest()));
        return request;
    }

    protected CoachSelectionRequest createCoachSelectionRequest() {
        CoachSelectionRequest request = new CoachSelectionRequest();
        request.setCoachId(TEST_COACH_ID);
        request.setDurationHours(2);
        return request;
    }

    protected EquipmentSelectionRequest createEquipmentSelectionRequest() {
        EquipmentSelectionRequest request = new EquipmentSelectionRequest();
        request.setEquipmentId(TEST_EQUIPMENT_ID);
        request.setQuantity(2);
        return request;
    }

    protected BookingStatusUpdateRequest createBookingStatusUpdateRequest(byte status) {
        BookingStatusUpdateRequest request = new BookingStatusUpdateRequest();
        request.setStatus(status);
        request.setCancellationReason(status == BookingConstant.BOOKING_CANCELLED ? "Test cancellation" : null);
        return request;
    }
}