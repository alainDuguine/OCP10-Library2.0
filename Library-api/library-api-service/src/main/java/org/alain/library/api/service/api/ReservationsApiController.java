package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.service.dto.ReservationDto;
import org.alain.library.api.service.dto.ReservationForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.alain.library.api.service.api.Converters.*;

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
        log.info("Getting reservation " + id);
        Optional<Reservation> reservation = reservationManagement.getReservation(id);
        if(reservation.isPresent()){
            return new ResponseEntity<ReservationDto>((convertReservationModelToReservationDto(reservation.get())), HttpStatus.OK);
        }
        log.warn("Unknown reservation " + id);
        return new ResponseEntity<ReservationDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<ReservationDto>> getReservations(@ApiParam(value = "Status values as filter in research", allowableValues = "reserved, notified, terminated, canceled")
                                                                @Valid @RequestParam(value = "currentStatus", required = false) String currentStatus,
                                                                @ApiParam(value = "User id as filter in research") @Valid @RequestParam(value = "user", required = false) Long user,
                                                                @ApiParam(value = "Book id as filter in research") @Valid @RequestParam(value = "book", required = false) Long book) {
        log.info("Requeting reservation list with params : [(currentStatus: " + currentStatus + ")(userId: " + user + ")(bookId: " + book +")]");
        List<Reservation> reservationList = reservationManagement.getReservationsByStatusAndUserIdAndBookId(currentStatus,user,book);
        log.info("Reservation resultSet :" + reservationList.size());
        return new ResponseEntity<List<ReservationDto>>(convertListReservationModelToListReservationDto(reservationList), HttpStatus.OK);
    }

    public ResponseEntity<ReservationDto> addReservation(@ApiParam(value = "User identification" ,required=true)
                                                         @RequestHeader(value="Authorization", required=true) String authorization,
                                                         @ApiParam(value = "reservation to add to database" ,required=true )
                                                         @Valid @RequestBody ReservationForm reservationForm) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ReservationDto>(objectMapper.readValue("{  \"book\" : {    \"copiesAvailable\" : 7,    \"isbn\" : \"isbn\",    \"id\" : 5,    \"title\" : \"title\",    \"authors\" : [ {      \"firstName\" : \"firstName\",      \"lastName\" : \"lastName\",      \"books\" : [ \"books\", \"books\" ],      \"id\" : 2    }, {      \"firstName\" : \"firstName\",      \"lastName\" : \"lastName\",      \"books\" : [ \"books\", \"books\" ],      \"id\" : 2    } ]  },  \"id\" : 0,  \"user\" : {    \"firstName\" : \"firstName\",    \"lastName\" : \"lastName\",    \"password\" : \"password\",    \"loans\" : [ {      \"endDate\" : \"endDate\",      \"currentStatus\" : \"currentStatus\",      \"currentStatusDate\" : \"currentStatusDate\",      \"userEmail\" : \"userEmail\",      \"bookCopy\" : {        \"editor\" : \"editor\",        \"book\" : {          \"copiesAvailable\" : 7,          \"isbn\" : \"isbn\",          \"id\" : 5,          \"title\" : \"title\",          \"authors\" : [ {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          }, {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          } ]        },        \"available\" : true,        \"id\" : 5,        \"barcode\" : \"barcode\"      },      \"id\" : 6,      \"userId\" : 1,      \"startDate\" : \"startDate\"    }, {      \"endDate\" : \"endDate\",      \"currentStatus\" : \"currentStatus\",      \"currentStatusDate\" : \"currentStatusDate\",      \"userEmail\" : \"userEmail\",      \"bookCopy\" : {        \"editor\" : \"editor\",        \"book\" : {          \"copiesAvailable\" : 7,          \"isbn\" : \"isbn\",          \"id\" : 5,          \"title\" : \"title\",          \"authors\" : [ {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          }, {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          } ]        },        \"available\" : true,        \"id\" : 5,        \"barcode\" : \"barcode\"      },      \"id\" : 6,      \"userId\" : 1,      \"startDate\" : \"startDate\"    } ],    \"roles\" : \"roles\",    \"id\" : 0,    \"email\" : \"email\"  }}", ReservationDto.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ReservationDto>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ReservationDto>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> updateReservation(@ApiParam(value = "User identification" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                  @ApiParam(value = "Id of reservation to update",required=true) @PathVariable("id") Long id,
                                                  @ApiParam(value = "Status values to add to reservation history" ,required=true )  @Valid @RequestBody String status) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<ReservationDto>> checkAndGetExpiredReservation(@ApiParam(value = "User identification" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<ReservationDto>>(objectMapper.readValue("[ {  \"book\" : {    \"copiesAvailable\" : 7,    \"isbn\" : \"isbn\",    \"id\" : 5,    \"title\" : \"title\",    \"authors\" : [ {      \"firstName\" : \"firstName\",      \"lastName\" : \"lastName\",      \"books\" : [ \"books\", \"books\" ],      \"id\" : 2    }, {      \"firstName\" : \"firstName\",      \"lastName\" : \"lastName\",      \"books\" : [ \"books\", \"books\" ],      \"id\" : 2    } ]  },  \"id\" : 0,  \"user\" : {    \"firstName\" : \"firstName\",    \"lastName\" : \"lastName\",    \"password\" : \"password\",    \"loans\" : [ {      \"endDate\" : \"endDate\",      \"currentStatus\" : \"currentStatus\",      \"currentStatusDate\" : \"currentStatusDate\",      \"userEmail\" : \"userEmail\",      \"bookCopy\" : {        \"editor\" : \"editor\",        \"book\" : {          \"copiesAvailable\" : 7,          \"isbn\" : \"isbn\",          \"id\" : 5,          \"title\" : \"title\",          \"authors\" : [ {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          }, {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          } ]        },        \"available\" : true,        \"id\" : 5,        \"barcode\" : \"barcode\"      },      \"id\" : 6,      \"userId\" : 1,      \"startDate\" : \"startDate\"    }, {      \"endDate\" : \"endDate\",      \"currentStatus\" : \"currentStatus\",      \"currentStatusDate\" : \"currentStatusDate\",      \"userEmail\" : \"userEmail\",      \"bookCopy\" : {        \"editor\" : \"editor\",        \"book\" : {          \"copiesAvailable\" : 7,          \"isbn\" : \"isbn\",          \"id\" : 5,          \"title\" : \"title\",          \"authors\" : [ {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          }, {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          } ]        },        \"available\" : true,        \"id\" : 5,        \"barcode\" : \"barcode\"      },      \"id\" : 6,      \"userId\" : 1,      \"startDate\" : \"startDate\"    } ],    \"roles\" : \"roles\",    \"id\" : 0,    \"email\" : \"email\"  }}, {  \"book\" : {    \"copiesAvailable\" : 7,    \"isbn\" : \"isbn\",    \"id\" : 5,    \"title\" : \"title\",    \"authors\" : [ {      \"firstName\" : \"firstName\",      \"lastName\" : \"lastName\",      \"books\" : [ \"books\", \"books\" ],      \"id\" : 2    }, {      \"firstName\" : \"firstName\",      \"lastName\" : \"lastName\",      \"books\" : [ \"books\", \"books\" ],      \"id\" : 2    } ]  },  \"id\" : 0,  \"user\" : {    \"firstName\" : \"firstName\",    \"lastName\" : \"lastName\",    \"password\" : \"password\",    \"loans\" : [ {      \"endDate\" : \"endDate\",      \"currentStatus\" : \"currentStatus\",      \"currentStatusDate\" : \"currentStatusDate\",      \"userEmail\" : \"userEmail\",      \"bookCopy\" : {        \"editor\" : \"editor\",        \"book\" : {          \"copiesAvailable\" : 7,          \"isbn\" : \"isbn\",          \"id\" : 5,          \"title\" : \"title\",          \"authors\" : [ {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          }, {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          } ]        },        \"available\" : true,        \"id\" : 5,        \"barcode\" : \"barcode\"      },      \"id\" : 6,      \"userId\" : 1,      \"startDate\" : \"startDate\"    }, {      \"endDate\" : \"endDate\",      \"currentStatus\" : \"currentStatus\",      \"currentStatusDate\" : \"currentStatusDate\",      \"userEmail\" : \"userEmail\",      \"bookCopy\" : {        \"editor\" : \"editor\",        \"book\" : {          \"copiesAvailable\" : 7,          \"isbn\" : \"isbn\",          \"id\" : 5,          \"title\" : \"title\",          \"authors\" : [ {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          }, {            \"firstName\" : \"firstName\",            \"lastName\" : \"lastName\",            \"books\" : [ \"books\", \"books\" ],            \"id\" : 2          } ]        },        \"available\" : true,        \"id\" : 5,        \"barcode\" : \"barcode\"      },      \"id\" : 6,      \"userId\" : 1,      \"startDate\" : \"startDate\"    } ],    \"roles\" : \"roles\",    \"id\" : 0,    \"email\" : \"email\"  }} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<ReservationDto>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<ReservationDto>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
