package com.backendsyndicate.smashclub.admin.service.report;

import com.backendsyndicate.smashclub.admin.core.IStatistic;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminBookingListDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminBookingMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionItemDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingCoachDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingEquipmentDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingEquipmentDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionListDTO;
import com.backendsyndicate.smashclub.admin.dto.response.*;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.booking.dto.request.BookingStatusUpdateRequest;
import com.backendsyndicate.smashclub.booking.dto.response.BookingResponse;
import com.backendsyndicate.smashclub.booking.dto.response.CoachDetailResponse;
import com.backendsyndicate.smashclub.booking.dto.response.EquipmentDetailResponse;
import com.backendsyndicate.smashclub.booking.model.Booking;
import com.backendsyndicate.smashclub.booking.model.Coach;
import com.backendsyndicate.smashclub.booking.model.CoachDetail;
import com.backendsyndicate.smashclub.booking.model.EquipmentDetail;
import com.backendsyndicate.smashclub.booking.repository.BookingRepository;
import com.backendsyndicate.smashclub.booking.repository.CoachDetailRepository;
import com.backendsyndicate.smashclub.booking.repository.EquipmentDetailRepository;
import com.backendsyndicate.smashclub.booking.service.helper.BookingHelper;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionConstant;
import com.backendsyndicate.smashclub.common.constant.TransactionTypeConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderDetailDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespOrderItemDTO;
import com.backendsyndicate.smashclub.ecommerce.service.helper.OrderHelper;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.repo.TransactionRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminBookingService implements IStatistic {
    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private CoachDetailRepository coachDetailRepository;
    @Autowired
    private EquipmentDetailRepository equipmentDetailRepository;
    @Autowired
    private BookingHelper bookingHelper;
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Display:
     * 1. Total Booking Count
     * 2. Average Booking Hour Count
     * 3. Occupancy Rate
     * 4. Monthly Booking Statistic: (Total Count, Avg Hour Count, Occupancy Rate, Total Price)
     *
     * @param yearStart
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<Object> statistic(int yearStart, HttpServletRequest request) {
        RespAdminBookingStatisticDTO response = null;

        try {
            LocalDateTime startYear = LocalDateTime.of(LocalDate.of(yearStart, 1, 1), LocalTime.of(0, 0, 0));
            LocalDateTime endYear = startYear == LocalDateTime.of(LocalDate.of(2026, 1, 1), LocalTime.of(0, 0, 0)) ?LocalDateTime.now() : startYear.plusYears(1);

            int totalBookingCount = bookingRepo.countByCreatedAt(startYear, endYear);
            double averageHourCount = bookingRepo.averageBookingHourByCreatedAt(startYear, endYear);
            BigDecimal occupancyRate = bookingRepo.occupancyRateByCreatedAt(startYear, endYear);
            List<Map<String, Object>> monthlyBooking = bookingRepo.findAllGroupByCreatedAtMonthly(startYear, endYear);

            response = new RespAdminBookingStatisticDTO();
            response.setTotalBookingCount(totalBookingCount);
            response.setAverageBookingHours(averageHourCount);
            response.setOccupancyRate(occupancyRate);
            response.setMonthlyBookingStatistic(monthlyBooking.stream().map( item -> {
                return Util.mapToModel(item, ExtAdminBookingMonthlyDTO.class);
            } ).toList());

        } catch(Exception e) {
            Logging.handleException("AdminBookingService", "statistic(LocalDate yearStart, HttpServletRequest request)", 55, AdminConstant.ADMIN_BOOKING_SERVICE_STATISTIC_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_BOOKING_SERVICE_STATISTIC_EXCEPTION, "AdminBookingService@statistic()", e.getMessage());
            return GlobalResponse.failed("Failed to get booking statistics!", AdminConstant.ADMIN_BOOKING_SERVICE_STATISTIC_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch booking statistics!", response, request);
    }

    @Override
    public ResponseEntity<Object> list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request) {
        RespAdminBookingListDTO response = null;

        try {
            LocalDateTime startMonth = LocalDateTime.of(LocalDate.of(yearStart, monthStart, 1), LocalTime.of(0, 0, 0));
            LocalDateTime endMonth = startMonth.plusMonths(1);

            int totalBookingCount = bookingRepo.countByCreatedAt(startMonth, endMonth);
            double averageHourCount = bookingRepo.averageBookingHourByCreatedAt(startMonth, endMonth);
            BigDecimal occupancyRate = bookingRepo.occupancyRateByCreatedAt(startMonth, endMonth);
            Page<Booking> bookings = null;
            if( !keyword.isEmpty() ) {
                bookings = bookingRepo.findAllByCreatedAtBetweenAndBookingCodeContainsIgnoreCase(startMonth, endMonth, keyword, pageable);
            } else {
                bookings = bookingRepo.findAllByCreatedAtBetween(startMonth, endMonth, pageable);
            }

            if( bookings.isEmpty() ) {
                Logging.handleException("AdminBookingService", "list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request)", 120, AdminConstant.ADMIN_BOOKING_SERVICE_LIST_EMPTY, "Booking list is empty");
                return GlobalResponse.failed("Failed to get booking list!", AdminConstant.ADMIN_BOOKING_SERVICE_LIST_EMPTY, null, request);
            }

            response = new RespAdminBookingListDTO();
            response.setTotalBookingCount(totalBookingCount);
            response.setAverageBookingHours(averageHourCount);
            response.setOccupancyRate(occupancyRate);
            Page<ExtAdminBookingListDTO> listDTO = bookings.map(new Function<Booking, ExtAdminBookingListDTO>() {
                @Override
                public ExtAdminBookingListDTO apply(Booking booking) {
                    return mapListToDTO(booking);
                }
            });
            response.setBookings(listDTO);

        } catch(Exception e) {
            Logging.handleException("AdminBookingService", "list(int yearStart, int monthStart, String keyword, Pageable pageable, HttpServletRequest request)", 136, AdminConstant.ADMIN_BOOKING_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_BOOKING_SERVICE_LIST_EXCEPTION, "AdminBookingService@list()", e.getMessage());
            return GlobalResponse.failed("Failed to get booking list!", AdminConstant.ADMIN_BOOKING_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch booking list!", response, request);
    }

    public ResponseEntity<Object> detail(String bookingCode, HttpServletRequest request) {
        if( bookingCode == null || bookingCode.isEmpty() ) {
            return GlobalResponse.failed("Failed to get booking detail!", AdminConstant.ADMIN_BOOKING_SERVICE_DETAIL_CODE_REQUIRED, null, request);
        }

        RespAdminBookingDetailDTO response = null;

        try {
            Optional<Booking> opt = bookingRepo.findByBookingCode(bookingCode);
            if( opt.isEmpty() ) {
                return GlobalResponse.failed("Failed to get booking detail!", AdminConstant.ADMIN_BOOKING_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            // Map booking to DTO
            Booking booking = opt.get();
            Hibernate.initialize(booking.getUser());
            Hibernate.initialize(booking.getCourt());
            List<CoachDetail> coachesDB = coachDetailRepository.findAllByBooking_Id(booking.getId());
            List<RelAdminBookingCoachDetailDTO> coaches = coachesDB.stream().map(new Function<CoachDetail, RelAdminBookingCoachDetailDTO>() {
                @Override
                public RelAdminBookingCoachDetailDTO apply(CoachDetail coachDetail) {
                    return mapCoachDetailToDTO(coachDetail);
                }
            }).toList();
            List<EquipmentDetail> equipmentsDB = equipmentDetailRepository.findAllByBooking_Id(booking.getId());
            List<RelAdminBookingEquipmentDetailDTO> equipments = equipmentsDB.stream().map(new Function<EquipmentDetail, RelAdminBookingEquipmentDetailDTO>() {
                @Override
                public RelAdminBookingEquipmentDetailDTO apply(EquipmentDetail equipmentDetail) {
                    return mapEquipmentDetailToDTO(equipmentDetail);
                }
            }).toList();

            response = modelMapper.map(booking, RespAdminBookingDetailDTO.class);
            response.setStatusDesc(BookingConstant.getBookingStatusDescription(booking.getStatus()));
            response.setCoaches(coaches);
            response.setEquipments(equipments);

        } catch(Exception e) {
            Logging.handleException("AdminBookingService", "detail(String bookingCode, HttpServletRequest request)", 109, AdminConstant.ADMIN_BOOKING_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_BOOKING_SERVICE_DETAIL_EXCEPTION, "AdminBookingService@detail()", e.getMessage());
            return GlobalResponse.failed("Failed to get booking detail!", AdminConstant.ADMIN_BOOKING_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully fetch booking detail!", response, request);
    }

    public ResponseEntity<Object> process(String bookingCode, int status, HttpServletRequest request) {
        if( bookingCode == null || bookingCode.isEmpty() ) {
            return GlobalResponse.failed("Failed to get booking detail!", AdminConstant.ADMIN_BOOKING_SERVICE_PROCESS_CODE_REQUIRED, null, request);
        }

        try {
            BookingStatusUpdateRequest dto = new BookingStatusUpdateRequest();
            dto.setStatus((byte) status);
            boolean isUpdated = bookingHelper.updateBookingStatus(bookingCode, dto);
            if( !isUpdated ) {
                Logging.handleException("AdminBookingService", "process(String bookingCode, int status, HttpServletRequest request)", 206, AdminConstant.ADMIN_BOOKING_SERVICE_PROCESS_FAILED, "Failed to update booking status!");
                return GlobalResponse.failed("Failed to process booking!", AdminConstant.ADMIN_BOOKING_SERVICE_PROCESS_FAILED, null, request);
            }

        } catch(Exception e) {
            Logging.handleException("AdminBookingService", "process(String bookingCode, int status, HttpServletRequest request)", 206, AdminConstant.ADMIN_BOOKING_SERVICE_PROCESS_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_BOOKING_SERVICE_PROCESS_EXCEPTION, "AdminBookingService@process()", e.getMessage());
            return GlobalResponse.failed("Failed to process booking!", AdminConstant.ADMIN_BOOKING_SERVICE_PROCESS_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully process booking!", null, request);
    }

    private ExtAdminBookingListDTO mapListToDTO(Booking booking) {
        ExtAdminBookingListDTO result = modelMapper.map(booking, ExtAdminBookingListDTO.class);
        result.setStatusDesc(BookingConstant.getBookingStatusDescription(booking.getStatus()));
        return result;
    }

    private RelAdminBookingCoachDetailDTO mapCoachDetailToDTO(CoachDetail coachDetail) {
        Hibernate.initialize(coachDetail.getCoach());
        RelAdminBookingCoachDetailDTO result = modelMapper.map(coachDetail, RelAdminBookingCoachDetailDTO.class);
        return result;
    }

    private RelAdminBookingEquipmentDetailDTO mapEquipmentDetailToDTO(EquipmentDetail equipmentDetail) {
        Hibernate.initialize(equipmentDetail.getEquipment());
        RelAdminBookingEquipmentDetailDTO result = new RelAdminBookingEquipmentDetailDTO();
        result.setEquipment(modelMapper.map(equipmentDetail.getEquipment(), RelAdminBookingEquipmentDTO.class));
        result.setEquipmentPrice(equipmentDetail.getEquipmentPrice());
        result.setQuantity(equipmentDetail.getQuantity());
        return result;
    }
}
