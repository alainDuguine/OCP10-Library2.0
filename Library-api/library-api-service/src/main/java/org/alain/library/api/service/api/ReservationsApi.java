/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package org.alain.library.api.service.api;

import io.swagger.annotations.*;
import org.alain.library.api.service.dto.ReservationDto;
import org.alain.library.api.service.dto.ReservationForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-02-19T16:47:31.241+01:00")

@Api(value = "reservations", description = "the reservations API")
public interface ReservationsApi {

    @ApiOperation(value = "Add a new reservation", nickname = "addReservation", notes = "", response = ReservationDto.class, tags={ "reservation", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Reservation added successfully to database", response = ReservationDto.class),
        @ApiResponse(code = 403, message = "You are not allowed to perform this request") })
    @RequestMapping(value = "/reservations",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<ReservationDto> addReservation(@ApiParam(value = "User identification", required = true) @RequestHeader(value = "Authorization", required = true) String authorization, @ApiParam(value = "reservation to add to database", required = true) @Valid @RequestBody ReservationForm reservationForm);


    @ApiOperation(value = "check and get reservation list that are expired", nickname = "checkAndGetExpiredReservation", notes = "trigger a checkup for all reservation's, return the list and cancel the expired one's", response = ReservationDto.class, responseContainer = "List", tags={ "reservation", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Loan found", response = ReservationDto.class, responseContainer = "List") })
    @RequestMapping(value = "/reservations/expired",
        produces = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<List<ReservationDto>> checkAndGetExpiredReservation(@ApiParam(value = "User identification", required = true) @RequestHeader(value = "Authorization", required = true) String authorization);


    @ApiOperation(value = "Get reservation by Id", nickname = "getReservation", notes = "", response = ReservationDto.class, tags={ "reservation", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Reservation found", response = ReservationDto.class) })
    @RequestMapping(value = "/reservations/{id}",
        produces = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<ReservationDto> getReservation(@ApiParam(value = "Id of reservation to return", required = true) @PathVariable("id") Long id);


    @ApiOperation(value = "Get a list of all reservations", nickname = "getReservations", notes = "", response = ReservationDto.class, responseContainer = "List", tags={ "reservation", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Reservations found", response = ReservationDto.class, responseContainer = "List") })
    @RequestMapping(value = "/reservations",
        produces = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<List<ReservationDto>> getReservations(@ApiParam(value = "Status values as filter in research", allowableValues = "pending, reserved, terminated, canceled") @Valid @RequestParam(value = "currentStatus", required = false) String currentStatus, @ApiParam(value = "User id as filter in research") @Valid @RequestParam(value = "user", required = false) Long user, @ApiParam(value = "Book id as filter in research") @Valid @RequestParam(value = "book", required = false) Long book);


    @ApiOperation(value = "Update a reservation by adding a status to it", nickname = "updateReservation", notes = "", tags={ "reservation", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Loan updated") })
    @RequestMapping(value = "/reservations/{id}",
        produces = { "application/json" },
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<Void> updateReservation(@ApiParam(value = "User identification", required = true) @RequestHeader(value = "Authorization", required = true) String authorization, @ApiParam(value = "Id of reservation to update", required = true) @PathVariable("id") Long id, @ApiParam(value = "Status values to add to reservation history", required = true) @Valid @RequestBody String status);

}
