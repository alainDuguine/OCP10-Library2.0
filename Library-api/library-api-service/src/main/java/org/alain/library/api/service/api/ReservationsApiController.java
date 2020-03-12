package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.business.impl.UserPrincipal;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.service.dto.ReservationDto;
import org.alain.library.api.service.dto.ReservationForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.alain.library.api.service.api.Converters.convertListReservationModelToListReservationDto;
import static org.alain.library.api.service.api.Converters.convertReservationModelToReservationDto;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-02-19T16:47:31.241+01:00")

@Controller
@Slf4j
public class ReservationsApiController implements ReservationsApi {

    private final ObjectMapper objectMapper;
    private final ReservationManagement reservationManagement;
    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ReservationsApiController(ObjectMapper objectMapper, ReservationManagement reservationManagement, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.reservationManagement = reservationManagement;
        this.request = request;
    }

    public ResponseEntity<ReservationDto> getReservation(@ApiParam(value = "Id of reservation to return",required=true) @PathVariable("id") Long id) {
        log.info("Getting reservation {}", id);
        Optional<Reservation> reservation = reservationManagement.findOne(id);
        if(reservation.isPresent()){
            return new ResponseEntity<ReservationDto>((convertReservationModelToReservationDto(reservation.get())), HttpStatus.OK);
        }
        log.warn("Unknown reservation {}", id);
        return new ResponseEntity<ReservationDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<ReservationDto>> getReservations(@ApiParam(value = "Status values as filter in research", allowableValues = "pending, reserved, terminated, canceled")
                                                                @Valid @RequestParam(value = "currentStatus", required = false) String currentStatus,
                                                                @ApiParam(value = "User id as filter in research") @Valid @RequestParam(value = "user", required = false) Long user,
                                                                @ApiParam(value = "Book id as filter in research") @Valid @RequestParam(value = "book", required = false) Long book) {
        log.info("Requesting reservation list with params : currentStatus: {}, userId: {}, bookId: {}",currentStatus, user, book);
        List<Reservation> reservationList = reservationManagement.getReservationsByStatusAndUserIdAndBookId(currentStatus,user,book);
        log.info("Reservation resultSet : {}", reservationList.size());
        return new ResponseEntity<List<ReservationDto>>(convertListReservationModelToListReservationDto(reservationList), HttpStatus.OK);
    }

    public ResponseEntity<List<ReservationDto>> getReservationByUser(@ApiParam(value = "User identification" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Requesting reservations for user : {}", userPrincipal.getId());
        List<Reservation> reservationList = reservationManagement.getReservationsByUser(userPrincipal.getId());
        log.info("Reservation founded : {}", reservationList.size());
        return new ResponseEntity<List<ReservationDto>>(convertListReservationModelToListReservationDto(reservationList), HttpStatus.OK);
    }

    public ResponseEntity<ReservationDto> addReservation(@ApiParam(value = "User identification" ,required=true)
                                                         @RequestHeader(value="Authorization", required=true) String authorization,
                                                         @ApiParam(value = "reservation to add to database" ,required=true )
                                                         @Valid @RequestBody ReservationForm reservationForm) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Requesting create new reservation : {}, user: {}", reservationForm.toString(), userPrincipal.getId());
        Reservation reservation = reservationManagement.createNewReservation(reservationForm.getBookId(), reservationForm.getUserId(), userPrincipal);
        log.info("Reservation created : {}", reservation.toString());
        return new ResponseEntity<ReservationDto>(convertReservationModelToReservationDto(reservation), HttpStatus.CREATED);
    }

    public ResponseEntity<Void> updateReservation(@ApiParam(value = "User identification" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                  @ApiParam(value = "Id of reservation to update",required=true) @PathVariable("id") Long id,
                                                  @NotNull @ApiParam(value = "Status values as filter in research", required = true, allowableValues = "pending, reserved, terminated, canceled")
                                                  @Valid @RequestParam(value = "status", required = true) String status) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Requesting updating reservation : {}, status: {}, user: {}", id, status, userPrincipal.getId());
        Optional<Reservation> reservation = reservationManagement.updateReservation(id, status, userPrincipal);
        if(reservation.isPresent()){
            log.info("Reservation update : {}", reservation.get().toString());
            // TODO check
            reservationManagement.checkPendingListAndNotify(reservation.get().getBook().getId());
            return new ResponseEntity<Void>(HttpStatus.OK);
        }else{
            log.warn("Unauthorized update request : {}", userPrincipal.getId());
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<List<ReservationDto>> checkAndGetExpiredReservation(@ApiParam(value = "User identification" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization) {
        log.info("Batch call to retrieve expired Reservations");
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrincipal.hasRole("ADMIN")){
            List<Reservation> reservationList = reservationManagement.updateAndGetExpiredReservation();
            log.info("Reservation List : {}", reservationList.size());
            return new ResponseEntity<List<ReservationDto>>(convertListReservationModelToListReservationDto(reservationList),HttpStatus.OK);
        }else{
            log.warn("Unauthorized batch call : {}", userPrincipal.getId());
            return new ResponseEntity<List<ReservationDto>>(HttpStatus.UNAUTHORIZED);
        }

    }

}
