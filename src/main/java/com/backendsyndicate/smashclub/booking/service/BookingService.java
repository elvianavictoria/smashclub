package com.backendsyndicate.smashclub.booking.service;

import com.backendsyndicate.smashclub.booking.dto.request.*;
import com.backendsyndicate.smashclub.booking.dto.response.*;
import com.backendsyndicate.smashclub.booking.model.*;
import com.backendsyndicate.smashclub.booking.repository.*;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import com.backendsyndicate.smashclub.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final CoachRepository coachRepository;
    private final EquipmentRepository equipmentRepository;
    private final CoachDetailRepository coachDetailRepository;
    private final EquipmentDetailRepository equipmentDetailRepository;
    private final UserRepository userRepository;
    private final BookingCodeGenerator bookingCodeGenerator;
    private final ResponseHandler responseHandler;
    private final PaymentService paymentService;

    // ============ GET ALL COURTS ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getAllCourts(HttpServletRequest httpRequest) {
        log.info("Getting all courts");

        try {
            List<Court> courts = courtRepository.findAll();
            List<CourtInfo> courtInfos = courts.stream()
                    .map(this::convertToCourtInfo)
                    .collect(Collectors.toList());

            return responseHandler.handleResponse(
                    "Data court berhasil diambil",
                    HttpStatus.OK,
                    null,
                    courtInfos,
                    httpRequest
            );
        } catch (Exception e) {
            log.error("Error getting courts: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengambil data court",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_001",
                    null,
                    httpRequest
            );
        }
    }

    // ============ CHECK COURT AVAILABILITY ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> checkCourtAvailability(
            CourtAvailabilityRequest request,
            HttpServletRequest httpRequest) {

        log.info("Checking court availability - date: {}", request.getDate());

        try {
            List<Court> courts;
            if (request.getCourtId() != null) {
                // Check specific court
                Optional<Court> courtOpt = courtRepository.findById(request.getCourtId());
                if (courtOpt.isEmpty()) {
                    return responseHandler.handleResponse(
                            "Court tidak ditemukan",
                            HttpStatus.BAD_REQUEST,
                            BookingConstant.ERROR_COURT_NOT_FOUND,
                            null,
                            httpRequest
                    );
                }
                courts = List.of(courtOpt.get());
            } else {
                // Check all courts
                courts = courtRepository.findAvailableCourts();
            }

            List<CourtAvailabilityResponse> responses = new ArrayList<>();

            for (Court court : courts) {
                CourtAvailabilityResponse response = buildCourtAvailabilityResponse(
                        court, request.getDate());
                responses.add(response);
            }

            return responseHandler.handleResponse(
                    "Data availability berhasil diambil",
                    HttpStatus.OK,
                    null,
                    responses,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error checking availability: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal memeriksa ketersediaan",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_003",
                    null,
                    httpRequest
            );
        }
    }

    // ============ GET AVAILABLE COACHES ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getAvailableCoaches(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            HttpServletRequest httpRequest) {

        log.info("Getting available coaches - date: {}, time: {} - {}",
                date, startTime, endTime);

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            return responseHandler.handleResponse(
                    "Waktu mulai harus sebelum waktu selesai",
                    HttpStatus.BAD_REQUEST,
                    BookingConstant.ERROR_START_TIME_AFTER_END,
                    null,
                    httpRequest
            );
        }

        try {
            List<Coach> availableCoaches = coachRepository.findAvailableCoachesByDateTime(
                    date, startTime, endTime);

            List<CoachAvailabilityResponse> responses = availableCoaches.stream()
                    .map(coach -> {
                        // Get coach's booked times for this date
                        List<CoachDetail> coachBookings = coachDetailRepository
                                .findCoachBookings(coach.getId(), date);

                        List<String> occupiedTimes = coachBookings.stream()
                                .map(cd -> String.format("%s-%s",
                                        cd.getStartTime(), cd.getEndTime()))
                                .collect(Collectors.toList());

                        return CoachAvailabilityResponse.builder()
                                .id(coach.getId())
                                .coachCode(coach.getCoachCode())
                                .coachName(coach.getCoachName())
                                .coachImgLink(coach.getCoachImgLink())
                                .pricePerHour(coach.getPricePerHour())
                                .status(coach.getStatus())
                                .available(true)
                                .occupiedTimes(occupiedTimes)
                                .build();
                    })
                    .collect(Collectors.toList());

            return responseHandler.handleResponse(
                    "Data coach available berhasil diambil",
                    HttpStatus.OK,
                    null,
                    responses,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error getting available coaches: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengambil data coach",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_004",
                    null,
                    httpRequest
            );
        }
    }

    // ============ GET AVAILABLE EQUIPMENT ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getAvailableEquipment(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Integer requiredQuantity,
            HttpServletRequest httpRequest) {

        log.info("Getting available equipment - date: {}, time: {} - {}, requiredQty: {}",
                date, startTime, endTime, requiredQuantity);

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            return responseHandler.handleResponse(
                    "Waktu mulai harus sebelum waktu selesai",
                    HttpStatus.BAD_REQUEST,
                    BookingConstant.ERROR_START_TIME_AFTER_END,
                    null,
                    httpRequest
            );
        }

        try {
            List<Equipment> availableEquipment = equipmentRepository
                    .findAvailableEquipmentWithStockByDateTime(
                            date,
                            startTime,
                            endTime,
                            requiredQuantity != null ? requiredQuantity : 1);

            List<EquipmentAvailabilityResponse> responses = availableEquipment.stream()
                    .map(equipment -> {
                        int availableStock = equipmentRepository.getAvailableStock(
                                equipment.getId(), date, startTime, endTime);

                        return EquipmentAvailabilityResponse.builder()
                                .id(equipment.getId())
                                .equipmentName(equipment.getEquipmentName())
                                .brand(equipment.getBrand())
                                .type(equipment.getType())
                                .categoryName(equipment.getEquipmentCategory() != null ?
                                        equipment.getEquipmentCategory().getCategoryName() : "")
                                .price(equipment.getPrice())
                                .stock(equipment.getStock())
                                .availableStock(availableStock)
                                .equipmentImgLink(equipment.getEquipmentImgLink())
                                .description(equipment.getDescription())
                                .status(equipment.getStatus())
                                .build();
                    })
                    .collect(Collectors.toList());

            return responseHandler.handleResponse(
                    "Data equipment available berhasil diambil",
                    HttpStatus.OK,
                    null,
                    responses,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error getting available equipment: {}", e.getMessage(), e);
            return responseHandler.handleResponse(
                    "Gagal mengambil data equipment",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOK_500",
                    null,
                    httpRequest
            );
        }
    }


    // ============ CREATE BOOKING ============
    @Transactional
    public ResponseEntity<Object> createBooking(
            BookingRequest request,
            String userId,
            HttpServletRequest httpRequest) {

        log.info("Creating booking - userId: {}, courtId: {}, date: {}",
                userId, request.getCourtId(), request.getBookingDate());

        try {
            // 1. Validate user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return responseHandler.handleResponse(
                        "User tidak ditemukan",
                        HttpStatus.BAD_REQUEST,
                        BookingConstant.ERROR_USER_NOT_AUTHORIZED,
                        null,
                        httpRequest
                );
            }
            User user = userOpt.get();

            // 2. Check court availability
            ResponseEntity<Object> availabilityCheck = validateCourtAvailability(
                    request.getCourtId(),
                    request.getBookingDate(),
                    request.getStartTime(),
                    request.getEndTime(),
                    httpRequest);

            if (availabilityCheck.getStatusCode() != HttpStatus.OK) {
                return availabilityCheck;
            }

            // 3. Get court and calculate base price
            Court court = courtRepository.findById(request.getCourtId()).orElseThrow();
            long durationHours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
            BigDecimal basePrice = calculateCourtPrice(court, durationHours);
            // 4. Create booking entity
            Booking booking = new Booking();
            booking.setBookingCode(bookingCodeGenerator.generate());
            booking.setUser(user);
            booking.setCourt(court);
            booking.setBookingDate(request.getBookingDate());
            booking.setStartTime(request.getStartTime());
            booking.setEndTime(request.getEndTime());
            booking.setDurationHour((int) durationHours);
            booking.setBasePrice(basePrice);
            booking.setTotalPrice(basePrice); // Will be updated with coaches and equipment
            booking.setStatus((byte) 1); // PENDING
            booking.setCreatedAt(LocalDateTime.now());

            // 5. Save booking first to get ID
            booking = bookingRepository.save(booking);

            // 6. Process coaches if any
            BigDecimal coachesTotalPrice = BigDecimal.ZERO;
            List<CoachDetail> coachDetails = new ArrayList<>();

            if (request.getCoaches() != null && !request.getCoaches().isEmpty()) {

                // Coach hanya boleh 1 per booking
                if (request.getCoaches().size() > 1) {
                    throw new RuntimeException("Maksimal hanya 1 coach per booking");
                }

                for (CoachSelectionRequest coachRequest : request.getCoaches()) {
                    CoachDetail coachDetail = processCoachSelection(
                            coachRequest,
                            booking,
                            request.getBookingDate(),
                            request.getStartTime(),
                            request.getEndTime(),
                            httpRequest);

                    if (coachDetail == null) {
                        throw new RuntimeException("Coach tidak tersedia");
                    }

                    coachDetails.add(coachDetail);
                    coachesTotalPrice = coachesTotalPrice.add(coachDetail.getCoachPrice());
                }
            }

            // 7. Process equipment if any
            BigDecimal equipmentTotalPrice = BigDecimal.ZERO;
            List<EquipmentDetail> equipmentDetails = new ArrayList<>();

            if (request.getEquipment() != null && !request.getEquipment().isEmpty()) {
                for (EquipmentSelectionRequest equipmentRequest : request.getEquipment()) {
                    EquipmentDetail equipmentDetail = processEquipmentSelection(
                            equipmentRequest,
                            booking,
                            request.getBookingDate(),
                            request.getStartTime(),
                            request.getEndTime(),
                            httpRequest);

                    if (equipmentDetail == null) {
                        // Rollback jika equipment tidak tersedia
                        throw new RuntimeException("Equipment tidak tersedia");
                    }

                    equipmentDetails.add(equipmentDetail);
                    equipmentTotalPrice = equipmentTotalPrice.add(equipmentDetail.getEquipmentPrice());
                }
            }

            // 8. Update total price
            BigDecimal grandTotal = basePrice
                    .add(coachesTotalPrice)
                    .add(equipmentTotalPrice);

            booking.setTotalPrice(grandTotal);
            bookingRepository.save(booking);

            RespCreateTransactionDTO paymentResponse = paymentService.createTransaction(
                    booking.getUser().getId(),
                    booking.getTotalPrice(),
                    booking.getBookingCode(),
                    TransactionTypeConstant.COURT_BOOKING
            );

            if (paymentResponse == null) {
                throw new RuntimeException("Failed to create payment transaction");
            }

            // 9. Build response
            BookingResponse response = buildBookingResponse(booking, coachDetails, equipmentDetails);
            response.setRespCreateTransactionDTO(paymentResponse);

            log.info("Booking created successfully - bookingCode: {}", booking.getBookingCode());

            return responseHandler.handleResponse(
                    "Booking berhasil dibuat",
                    HttpStatus.CREATED,
                    null,
                    response,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal membuat booking: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    "BOOKING_007",
                    null,
                    httpRequest
            );
        }
    }

    // ============ GET BOOKING DETAILS ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBookingDetails(
            String bookingCode,
            HttpServletRequest httpRequest) {

        log.info("Getting booking details - bookingCode: {}", bookingCode);

        try {
            Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);
            if (bookingOpt.isEmpty()) {
                return responseHandler.handleResponse(
                        "Booking tidak ditemukan",
                        HttpStatus.BAD_REQUEST,
                        BookingConstant.ERROR_BOOKING_NOT_FOUND,
                        null,
                        httpRequest
                );
            }

            Booking booking = bookingOpt.get();

            // Get coach details
            List<CoachDetail> coachDetails = coachDetailRepository
                    .findByBookingIdWithDetails(booking.getId());

// Get equipment details
            List<EquipmentDetail> equipmentDetails = equipmentDetailRepository
                    .findByBookingIdWithDetails(booking.getId());

            BookingResponse response = buildBookingResponse(booking, coachDetails, equipmentDetails);

            return responseHandler.handleResponse(
                    "Detail booking berhasil diambil",
                    HttpStatus.OK,
                    null,
                    response,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error getting booking details: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengambil detail booking",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_009",
                    null,
                    httpRequest
            );
        }
    }

    // ============ GET USER BOOKINGS ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getUserBookings(
            String userId,
            HttpServletRequest httpRequest) {

        log.info("Getting user bookings - userId: {}", userId);

        try {
            List<Booking> bookings = bookingRepository.findByUserId(userId);

            List<BookingResponse> responses = new ArrayList<>();
            for (Booking booking : bookings) {
                List<CoachDetail> coachDetails = coachDetailRepository
                        .findByBookingIdWithDetails(booking.getId());
                List<EquipmentDetail> equipmentDetails = equipmentDetailRepository
                        .findByBookingIdWithDetails(booking.getId());
                responses.add(buildBookingResponse(booking, coachDetails, equipmentDetails));
            }

            return responseHandler.handleResponse(
                    "Data booking user berhasil diambil",
                    HttpStatus.OK,
                    null,
                    responses,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error getting user bookings: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengambil data booking",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_010",
                    null,
                    httpRequest
            );
        }
    }

    // ============ UPDATE BOOKING STATUS ============
    @Transactional
    public ResponseEntity<Object> updateBookingStatus(
            String bookingCode,
            BookingStatusUpdateRequest request,
            HttpServletRequest httpRequest) {

        log.info("Updating booking status - bookingCode: {}, status: {}",
                bookingCode, request.getStatus());

        try {
            Booking booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));

            // Validate status transition
            if (!isValidStatusTransition(booking.getStatus(), request.getStatus())) {
                return responseHandler.handleResponse(
                        String.format("Cannot change status from %s to %s",
                                BookingConstant.getBookingStatusDescription(booking.getStatus()),
                                BookingConstant.getBookingStatusDescription(request.getStatus())),
                        HttpStatus.BAD_REQUEST,
                        BookingConstant.ERROR_INVALID_STATUS_TRANSITION,
                        null,
                        httpRequest
                );
            }

            byte oldStatus = booking.getStatus();
            byte newStatus = request.getStatus();

            // Jika di CANCEL (dari PENDING atau CONFIRMED)
            if (newStatus == BookingConstant.BOOKING_CANCELLED) {

                // KEMBALIKAN STOCK EQUIPMENT
                List<EquipmentDetail> equipmentDetails = equipmentDetailRepository
                        .findByBookingIdWithDetails(booking.getId());

                for (EquipmentDetail ed : equipmentDetails) {
                    Equipment equipment = ed.getEquipment();
                    equipment.setStock(equipment.getStock() + ed.getQuantity());
                    equipmentRepository.save(equipment);
                    log.info(" Stock returned for cancelled booking: {} +{} (stock: {})",
                            equipment.getEquipmentName(), ed.getQuantity(), equipment.getStock());
                }

                log.info(" Booking cancelled - stock returned, details preserved for history");
            }

            // Update status
            booking.setStatus(newStatus);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            return responseHandler.handleResponse(
                    "Booking status updated successfully",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error updating booking status: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Failed to update booking status",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_500",
                    null,
                    httpRequest
            );
        }
    }

    // ============ GET BOOKING SUMMARY ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBookingSummary(
            String bookingCode,
            BookingRequest request,
            HttpServletRequest httpRequest) {

        log.info("Getting booking summary for court: {}, date: {}",
                request.getCourtId(), request.getBookingDate());

        try {
            // Validate court availability first
            ResponseEntity<Object> availabilityCheck = validateCourtAvailability(
                    request.getCourtId(),
                    request.getBookingDate(),
                    request.getStartTime(),
                    request.getEndTime(),
                    httpRequest);

            if (availabilityCheck.getStatusCode() != HttpStatus.OK) {
                return availabilityCheck;
            }

            // Get court and calculate price
            Court court = courtRepository.findById(request.getCourtId()).orElseThrow();
            long durationHours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
            BigDecimal courtTotalPrice = calculateCourtPrice(court, durationHours);

            // Process coaches
            BigDecimal coachesTotalPrice = BigDecimal.ZERO;
            List<CoachDetailResponse> coachResponses = new ArrayList<>();

            if (request.getCoaches() != null) {
                for (CoachSelectionRequest coachRequest : request.getCoaches()) {
                    Coach coach = coachRepository.findById(coachRequest.getCoachId())
                            .orElseThrow(() -> new RuntimeException("Coach tidak ditemukan"));

                    // Check coach availability
                    List<Coach> availableCoaches = coachRepository.findAvailableCoachesByDateTime(
                            request.getBookingDate(),
                            request.getStartTime(),
                            request.getEndTime());

                    if (availableCoaches.stream().noneMatch(c -> c.getId().equals(coachRequest.getCoachId()))) {
                        return responseHandler.handleResponse(
                                "Coach tidak tersedia pada waktu yang dipilih",
                                HttpStatus.BAD_REQUEST,
                                BookingConstant.ERROR_COACH_NOT_AVAILABLE,
                                null,
                                httpRequest
                        );
                    }

                    BigDecimal coachPrice = coach.getPricePerHour()
                            .multiply(BigDecimal.valueOf(coachRequest.getDurationHours()));

                    coachesTotalPrice = coachesTotalPrice.add(coachPrice);

                    coachResponses.add(CoachDetailResponse.builder()
                            .coachCode(coach.getCoachCode())
                            .coachName(coach.getCoachName())
                            .coachImgLink(coach.getCoachImgLink())
                            .pricePerHour(coach.getPricePerHour())
                            .coachHour(coachRequest.getDurationHours())
                            .bookingDate(request.getBookingDate())
                            .startTime(request.getStartTime())
                            .endTime(request.getEndTime())
                            .coachPrice(coachPrice)
                            .build());
                }
            }

            // Process equipment
            BigDecimal equipmentTotalPrice = BigDecimal.ZERO;
            List<EquipmentDetailResponse> equipmentResponses = new ArrayList<>();

            if (request.getEquipment() != null) {
                for (EquipmentSelectionRequest equipmentRequest : request.getEquipment()) {
                    Equipment equipment = equipmentRepository.findById(equipmentRequest.getEquipmentId())
                            .orElseThrow(() -> new RuntimeException("Equipment tidak ditemukan"));

                    // Check equipment availability
                    int availableStock = equipmentRepository.getAvailableStock(
                            equipmentRequest.getEquipmentId(),
                            request.getBookingDate(),
                            request.getStartTime(),
                            request.getEndTime()
                    );
                    if (availableStock < equipmentRequest.getQuantity()) {
                        return responseHandler.handleResponse(
                                String.format("Stock %s tidak mencukupi. Tersedia: %d, Dibutuhkan: %d",
                                        equipment.getEquipmentName(), availableStock, equipmentRequest.getQuantity()),
                                HttpStatus.BAD_REQUEST,
                                BookingConstant.ERROR_EQUIPMENT_OUT_OF_STOCK,
                                null,
                                httpRequest
                        );
                    }

                    BigDecimal equipmentPrice = equipment.getPrice()
                            .multiply(BigDecimal.valueOf(equipmentRequest.getQuantity()));

                    equipmentTotalPrice = equipmentTotalPrice.add(equipmentPrice);

                    equipmentResponses.add(EquipmentDetailResponse.builder()
                            .equipmentName(equipment.getEquipmentName())
                            .brand(equipment.getBrand())
                            .type(equipment.getType())
                            .categoryName(equipment.getEquipmentCategory() != null ?
                                    equipment.getEquipmentCategory().getCategoryName() : "")
                            .pricePerUnit(equipment.getPrice())
                            .quantity(equipmentRequest.getQuantity())
                            .startTime(request.getStartTime())
                            .endTime(request.getEndTime())
                            .equipmentPrice(equipmentPrice)
                            .build());
                }
            }

            // Calculate grand total
            BigDecimal grandTotal = courtTotalPrice
                    .add(coachesTotalPrice)
                    .add(equipmentTotalPrice);

            // Build response
            CourtInfo courtInfo = convertToCourtInfo(court);

            String paymentUrl = null;
            String statusDescription = null;
            Byte status = null;
            if (bookingCode != null && !bookingCode.isEmpty()) {
                paymentUrl = paymentService.getPaymentUrl(bookingCode);
                Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);
                if (bookingOpt.isPresent()) {
                    status = bookingOpt.get().getStatus();
                    statusDescription = BookingConstant.getBookingStatusDescription(status);
                }
            }

            BookingSummaryResponse summary = BookingSummaryResponse.builder()
                    .court(courtInfo)
                    .coaches(coachResponses)
                    .equipment(equipmentResponses)
                    .courtTotalPrice(courtTotalPrice)
                    .coachesTotalPrice(coachesTotalPrice)
                    .equipmentTotalPrice(equipmentTotalPrice)
                    .grandTotal(grandTotal)
                    .paymentUrl(paymentUrl)
                    .statusDescription(statusDescription)
                    .estimatedDuration(durationHours + " jam")
                    .build();

            return responseHandler.handleResponse(
                    "Summary booking berhasil dibuat",
                    HttpStatus.OK,
                    null,
                    summary,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error getting booking summary: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal membuat summary booking: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    "BOOKING_015",
                    null,
                    httpRequest
            );
        }
    }

    // ============ HELPER METHODS ============
    private ResponseEntity<Object> validateCourtAvailability(
            Long courtId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            HttpServletRequest httpRequest) {

        // Check if court exists and is available
        Optional<Court> courtOpt = courtRepository.findById(courtId);
        if (courtOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "Court tidak ditemukan",
                    HttpStatus.BAD_REQUEST,
                    BookingConstant.ERROR_COURT_NOT_FOUND,
                    null,
                    httpRequest
            );
        }

        Court court = courtOpt.get();
        if (court.getStatus() != BookingConstant.RESOURCE_ACTIVE) { // Not AVAILABLE
            return responseHandler.handleResponse(
                    "Court tidak tersedia untuk booking",
                    HttpStatus.BAD_REQUEST,
                    BookingConstant.ERROR_COURT_NOT_AVAILABLE,
                    null,
                    httpRequest
            );
        }

        // Check if time is within court operating hours
        if (startTime.isBefore(court.getOpenTime()) || endTime.isAfter(court.getCloseTime())) {
            return responseHandler.handleResponse(
                    String.format("Waktu booking harus antara %s - %s",
                            court.getOpenTime(), court.getCloseTime()),
                    HttpStatus.BAD_REQUEST,
                    BookingConstant.ERROR_OUTSIDE_BUSINESS_HOURS,
                    null,
                    httpRequest
            );
        }

        // Check for overlapping bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                courtId, date, startTime, endTime);

        if (!overlappingBookings.isEmpty()) {
            return responseHandler.handleResponse(
                    "Court sudah dibooking pada waktu tersebut",
                    HttpStatus.BAD_REQUEST,
                    BookingConstant.ERROR_COURT_ALREADY_BOOKED,
                    null,
                    httpRequest
            );
        }

        return responseHandler.handleResponse(
                "Court tersedia",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    private BigDecimal calculateCourtPrice(Court court, long durationHours) {
        return court.getPricePerHour().multiply(BigDecimal.valueOf(durationHours));
    }

    private CoachDetail processCoachSelection(
            CoachSelectionRequest coachRequest,
            Booking booking,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime,
            HttpServletRequest httpRequest) {

        Optional<Coach> coachOpt = coachRepository.findById(coachRequest.getCoachId());
        if (coachOpt.isEmpty()) {
            return null;
        }

        Coach coach = coachOpt.get();

        // CEK AVAILABILITY PAKAI METHOD YANG SUDAH ADA
        boolean isAvailable = coachRepository.isCoachAvailable(
                coachRequest.getCoachId(), bookingDate, startTime, endTime);

        if (!isAvailable) {
            log.warn("Coach not available: {}", coach.getId());
            return null;
        }

        // Calculate coach price
        BigDecimal coachPrice = coach.getPricePerHour()
                .multiply(BigDecimal.valueOf(coachRequest.getDurationHours()));

        // Create coach detail
        CoachDetail coachDetail = new CoachDetail();
        coachDetail.setCoach(coach);
        coachDetail.setBooking(booking);
        coachDetail.setCoachHour(coachRequest.getDurationHours());
        coachDetail.setBookingDate(bookingDate);
        coachDetail.setStartTime(startTime);
        coachDetail.setEndTime(endTime);
        coachDetail.setCoachPrice(coachPrice);
        coachDetail.setCreatedAt(LocalDateTime.now());

        return coachDetailRepository.save(coachDetail);
    }

    private EquipmentDetail processEquipmentSelection(
            EquipmentSelectionRequest equipmentRequest,
            Booking booking,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime,
            HttpServletRequest httpRequest) {

        Equipment equipment = equipmentRepository.findById(equipmentRequest.getEquipmentId())
                .orElse(null);
        if (equipment == null) {
            return null;
        }

        // Check stock tersedia
        if (equipment.getStock() < equipmentRequest.getQuantity()) {
            log.warn("Equipment insufficient stock: {}, available: {}, requested: {}",
                    equipment.getId(), equipment.getStock(), equipmentRequest.getQuantity());
            return null;
        }

        // KURANGI STOCK!
        equipment.setStock(equipment.getStock() - equipmentRequest.getQuantity());
        equipmentRepository.save(equipment);

        // Calculate equipment price
        BigDecimal equipmentPrice = equipment.getPrice()
                .multiply(BigDecimal.valueOf(equipmentRequest.getQuantity()));

        // Create equipment detail
        EquipmentDetail equipmentDetail = new EquipmentDetail();
        equipmentDetail.setEquipment(equipment);
        equipmentDetail.setBooking(booking);
        equipmentDetail.setQuantity(equipmentRequest.getQuantity());
        equipmentDetail.setStartTime(startTime);
        equipmentDetail.setEndTime(endTime);
        equipmentDetail.setEquipmentPrice(equipmentPrice);
        equipmentDetail.setCreatedAt(LocalDateTime.now());

        return equipmentDetailRepository.save(equipmentDetail);
    }

    private int getAvailableStockForEquipment(Long equipmentId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return equipmentRepository.getAvailableStock(equipmentId, date, startTime, endTime);
    }

    private CourtAvailabilityResponse buildCourtAvailabilityResponse(Court court, LocalDate date) {
        List<CourtAvailabilityResponse.TimeSlot> timeSlots = new ArrayList<>();

        // Generate time slots from open to close time
        LocalTime currentTime = court.getOpenTime();
        while (currentTime.isBefore(court.getCloseTime())) {
            LocalTime slotStart = currentTime;
            LocalTime slotEnd = currentTime.plusHours(1);

            // Check if this slot is available
            boolean available = isTimeSlotAvailable(court.getId(), date, slotStart, slotEnd);
            String status = available ? "AVAILABLE" : "BOOKED";

            // Calculate price for this slot
            BigDecimal price = calculateCourtPrice(court, 1);

            timeSlots.add(CourtAvailabilityResponse.TimeSlot.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .available(available)
                    .price(price)
                    .status(status)
                    .build());

            currentTime = currentTime.plusHours(1);
        }

        BigDecimal hourlyRate = calculateCourtPrice(court, 1);

        return CourtAvailabilityResponse.builder()
                .id(court.getId())
                .courtCode(court.getCourtCode())
                .courtName(court.getCourtName())
                .courtImgLink(court.getCourtImgLink())
                .openTime(court.getOpenTime())
                .closeTime(court.getCloseTime())
                .pricePerHour(court.getPricePerHour())
                .status(court.getStatus())
                .availableSlots(timeSlots)
                .build();
    }

    private boolean isTimeSlotAvailable(Long courtId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                courtId, date, startTime, endTime);
        return overlappingBookings.isEmpty();
    }

    private CourtInfo convertToCourtInfo(Court court) {
        BigDecimal hourlyRate = calculateCourtPrice(court, 1);

        return CourtInfo.builder()
                .id(court.getId())
                .courtCode(court.getCourtCode())
                .courtName(court.getCourtName())
                .courtImgLink(court.getCourtImgLink())
                .openTime(court.getOpenTime())
                .closeTime(court.getCloseTime())
                .pricePerHour(court.getPricePerHour())
                .status(court.getStatus())
                .build();
    }

    protected BookingResponse buildBookingResponse(
            Booking booking,
            List<CoachDetail> coachDetails,
            List<EquipmentDetail> equipmentDetails) {

        // Convert coach details to response
        List<CoachDetailResponse> coachResponses = coachDetails.stream()
                .map(cd -> CoachDetailResponse.builder()
                        .id(cd.getId())
                        .coachCode(cd.getCoach().getCoachCode())
                        .coachName(cd.getCoach().getCoachName())
                        .coachImgLink(cd.getCoach().getCoachImgLink())
                        .pricePerHour(cd.getCoach().getPricePerHour())
                        .coachHour(cd.getCoachHour())
                        .bookingDate(cd.getBookingDate())
                        .startTime(cd.getStartTime())
                        .endTime(cd.getEndTime())
                        .coachPrice(cd.getCoachPrice())
                        .build())
                .collect(Collectors.toList());

        // Convert equipment details to response
        List<EquipmentDetailResponse> equipmentResponses = equipmentDetails.stream()
                .map(ed -> EquipmentDetailResponse.builder()
                        .id(ed.getId())
                        .equipmentName(ed.getEquipment().getEquipmentName())
                        .brand(ed.getEquipment().getBrand())
                        .type(ed.getEquipment().getType())
                        .categoryName(ed.getEquipment().getEquipmentCategory() != null ?
                                ed.getEquipment().getEquipmentCategory().getCategoryName() : "")
                        .pricePerUnit(ed.getEquipment().getPrice())
                        .quantity(ed.getQuantity())
                        .startTime(ed.getStartTime())
                        .endTime(ed.getEndTime())
                        .equipmentPrice(ed.getEquipmentPrice())
                        .build())
                .collect(Collectors.toList());

        // Build court info
        CourtInfo courtInfo = convertToCourtInfo(booking.getCourt());

        // Build user info
        UserInfo userInfo = UserInfo.builder()
                .userId(booking.getUser().getId())
                .fullName(booking.getUser().getFullName())
                .email(booking.getUser().getEmail())
                .build();

        return BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .durationHour(booking.getDurationHour())
                .basePrice(booking.getBasePrice())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .statusDescription(BookingConstant.getBookingStatusDescription(booking.getStatus()))
                .createdAt(booking.getCreatedAt())
                .court(courtInfo)
                .coaches(coachResponses)
                .equipment(equipmentResponses)
                .user(userInfo)
                .build();
    }

    protected boolean isValidStatusTransition(byte currentStatus, byte newStatus) {
        Map<Byte, List<Byte>> validTransitions = Map.of(
                BookingConstant.BOOKING_CANCELLED, List.of(), // Tidak bisa berubah

                BookingConstant.BOOKING_PENDING, List.of(
                        BookingConstant.BOOKING_CANCELLED,     // Customer cancel
                        BookingConstant.BOOKING_CONFIRMED      // Payment success
                ),

                BookingConstant.BOOKING_CONFIRMED, List.of(
                        BookingConstant.BOOKING_CANCELLED,     // Customer cancel (sebelum check in)
                        BookingConstant.BOOKING_ONGOING        // Customer check-in / ambil barang
                ),

                BookingConstant.BOOKING_ONGOING, List.of(
                        BookingConstant.BOOKING_COMPLETED      // Selesai / barang dikembalikan
                ),

                BookingConstant.BOOKING_COMPLETED, List.of() // Tidak bisa berubah
        );

        return validTransitions.getOrDefault(currentStatus, List.of())
                .contains(newStatus);
    }

    private void releaseBookingResources(Booking booking) {
        // When a booking is cancelled, we don't need to do anything
        // because the resources are still considered "booked" until the booking is cancelled
        // In a real system, you might want to send notifications or update caches
        log.info("Releasing resources for cancelled booking: {}", booking.getBookingCode());
    }

    /**
     * Digunakan :
     * - Customer: saat ambil barang / check in
     * - Admin: konfirmasi customer datang
     */
    @Transactional
    public ResponseEntity<Object> startBooking(String bookingCode, HttpServletRequest httpRequest) {
        log.info("Starting booking - bookingCode: {}", bookingCode);

        try {
            Booking booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));

            // Validasi: hanya CONFIRMED yang bisa jadi ONGOING
            if (booking.getStatus() != BookingConstant.BOOKING_CONFIRMED) {
                return responseHandler.handleResponse(
                        "Only CONFIRMED booking can be started",
                        HttpStatus.BAD_REQUEST,
                        BookingConstant.ERROR_INVALID_STATUS_TRANSITION,
                        null,
                        httpRequest
                );
            }

            booking.setStatus(BookingConstant.BOOKING_ONGOING);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            log.info("Booking started - bookingCode: {}", bookingCode);

            return responseHandler.handleResponse(
                    "Booking started successfully",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error starting booking: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Failed to start booking",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_500",
                    null,
                    httpRequest
            );
        }
    }

    /**
     * Digunakan Admin:
     * - Setelah customer selesai / barang dikembalikan
     * - Akan mengembalikan stock coach & equipment
     */
    @Transactional
    public ResponseEntity<Object> completeBooking(String bookingCode, HttpServletRequest httpRequest) {
        log.info("Completing booking - bookingCode: {}", bookingCode);

        try {
            Booking booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));

            // Validasi: hanya ONGOING yang bisa jadi COMPLETED
            if (booking.getStatus() != BookingConstant.BOOKING_ONGOING) {
                return responseHandler.handleResponse(
                        "Only ONGOING booking can be completed",
                        HttpStatus.BAD_REQUEST,
                        BookingConstant.ERROR_INVALID_STATUS_TRANSITION,
                        null,
                        httpRequest
                );
            }

            //  KEMBALIKAN STOCK EQUIPMENT
            List<EquipmentDetail> equipmentDetails = equipmentDetailRepository
                    .findByBookingIdWithDetails(booking.getId());

            for (EquipmentDetail ed : equipmentDetails) {
                Equipment equipment = ed.getEquipment();
                equipment.setStock(equipment.getStock() + ed.getQuantity());
                equipmentRepository.save(equipment);
                log.info(" Stock returned for completed booking: {} +{} (stock: {})",
                        equipment.getEquipmentName(), ed.getQuantity(), equipment.getStock());
            }

            // Update status
            booking.setStatus(BookingConstant.BOOKING_COMPLETED);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            log.info(" Booking completed - stock returned, details preserved for history");

            return responseHandler.handleResponse(
                    "Booking completed successfully",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error completing booking: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Failed to complete booking",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "BOOKING_500",
                    null,
                    httpRequest
            );
        }
    }

    // ============ CREATE PAYMENT FOR BOOKING ============
    @Transactional
    public ResponseEntity<Object> createBookingPayment(
            String bookingCode,
            int paymentMethodId,
            HttpServletRequest httpRequest) {

        log.info("Creating payment for booking - bookingCode: {}", bookingCode);

        try {
            // 1. Validasi booking
            Booking booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));

            // 2. Validasi status booking (hanya PENDING yang bisa bayar)
            if (booking.getStatus() != BookingConstant.BOOKING_PENDING) {
                return responseHandler.handleResponse(
                        "Only PENDING booking can be paid",
                        HttpStatus.BAD_REQUEST,
                        BookingConstant.ERROR_INVALID_STATUS_TRANSITION,
                        null,
                        httpRequest
                );
            }

            // 3. Panggil payment service teman
            RespCreateTransactionDTO paymentResponse = paymentService.createTransaction(
                    booking.getUser().getId(),
                    booking.getTotalPrice(),
                    bookingCode,
                    TransactionTypeConstant.COURT_BOOKING
            );

            if (paymentResponse == null) {
                throw new RuntimeException("Failed to create payment transaction");
            }

            // 4. Return response
            Map<String, Object> response = new HashMap<>();
            response.put("bookingCode", bookingCode);
            response.put("paymentData", paymentResponse.getPaymentData());

            return responseHandler.handleResponse(
                    "Payment transaction created successfully",
                    HttpStatus.CREATED,
                    null,
                    response,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Error creating payment for booking: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Failed to create payment: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    "BOOKING_500",
                    null,
                    httpRequest
            );
        }
    }
}