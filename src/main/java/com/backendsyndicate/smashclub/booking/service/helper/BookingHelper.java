package com.backendsyndicate.smashclub.booking.service.helper;

import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.booking.dto.request.BookingStatusUpdateRequest;
import com.backendsyndicate.smashclub.booking.dto.response.BookingResponse;
import com.backendsyndicate.smashclub.booking.model.Booking;
import com.backendsyndicate.smashclub.booking.model.CoachDetail;
import com.backendsyndicate.smashclub.booking.model.Equipment;
import com.backendsyndicate.smashclub.booking.model.EquipmentDetail;
import com.backendsyndicate.smashclub.booking.repository.*;
import com.backendsyndicate.smashclub.booking.service.BookingCodeGenerator;
import com.backendsyndicate.smashclub.booking.service.BookingService;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingHelper extends BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private EquipmentDetailRepository equipmentDetailRepository;
    @Autowired
    private CoachDetailRepository coachDetailRepository;

    public BookingHelper(BookingRepository bookingRepository, CourtRepository courtRepository, CoachRepository coachRepository, EquipmentRepository equipmentRepository, CoachDetailRepository coachDetailRepository, EquipmentDetailRepository equipmentDetailRepository, UserRepository userRepository, BookingCodeGenerator bookingCodeGenerator, ResponseHandler responseHandler, PaymentService paymentService) {
        super(bookingRepository, courtRepository, coachRepository, equipmentRepository, coachDetailRepository, equipmentDetailRepository, userRepository, bookingCodeGenerator, responseHandler, paymentService);
    }


    @Transactional
    public boolean updateBookingStatus(
            String bookingCode,
            BookingStatusUpdateRequest request) {

        log.info("Updating booking status - bookingCode: {}, status: {}",
                bookingCode, request.getStatus());

        try {
            Booking booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));

            // Validate status transition
            if (!super.isValidStatusTransition(booking.getStatus(), request.getStatus())) {
                Logging.handleException("BookingHelper", "updateBookingStatus(String bookingCode, BookingStatusUpdateRequest request)", 52, BookingConstant.ERROR_INVALID_STATUS_TRANSITION, String.format("Cannot change status from %s to %s",
                        BookingConstant.getBookingStatusDescription(booking.getStatus()),
                        BookingConstant.getBookingStatusDescription(request.getStatus())));
                return false;
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

            return true;

        } catch (Exception e) {
            Logging.handleException("BookingHelper", "updateBookingStatus(String bookingCode, BookingStatusUpdateRequest request)", 84, "BOOKING_500", e.getMessage());
            log.error("Error updating booking status: {}", e.getMessage());
            return false;
        }
    }

    public BookingResponse getBookingDetails(
            String bookingCode) {

        log.info("Getting booking details - bookingCode: {}", bookingCode);
        BookingResponse response = null;

        try {
            Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);
            if (bookingOpt.isEmpty()) {
                Logging.handleException("BookingHelper", "getBookingDetails(String bookingCode)", 109, BookingConstant.ERROR_BOOKING_NOT_FOUND, "Booking tidak ditemukan!");
                return null;
            }

            Booking booking = bookingOpt.get();
            Hibernate.initialize(booking.getCourt());

            // Get coach details
            List<CoachDetail> coachDetails = coachDetailRepository
                    .findByBookingIdWithDetails(booking.getId());
            for( CoachDetail item: coachDetails ) {
                Hibernate.initialize(item.getCoach());
            }

// Get equipment details
            List<EquipmentDetail> equipmentDetails = equipmentDetailRepository
                    .findByBookingIdWithDetails(booking.getId());
            for( EquipmentDetail item: equipmentDetails ) {
                Hibernate.initialize(item.getEquipment());
            }

            response = super.buildBookingResponse(booking, coachDetails, equipmentDetails);
        } catch (Exception e) {
            log.error("Error getting booking details: {}", e.getMessage());
        }

        return response;
    }
}
