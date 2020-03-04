package org.alain.library.api.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.BookManagement;
import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.business.contract.UserManagement;
import org.alain.library.api.business.exceptions.ReservationException;
import org.alain.library.api.business.exceptions.UnauthorizedException;
import org.alain.library.api.business.exceptions.UnknowStatusException;
import org.alain.library.api.business.exceptions.UnknownUserException;
import org.alain.library.api.consumer.repository.ReservationRepository;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.exceptions.BookAlreadyLoanedException;
import org.alain.library.api.model.exceptions.BookStillAvailableException;
import org.alain.library.api.model.exceptions.FullReservationListException;
import org.alain.library.api.model.exceptions.ReservationAlreadyExistsException;
import org.alain.library.api.model.loan.StatusDesignation;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.alain.library.api.model.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ReservationManagementImpl extends CrudManagementImpl<Reservation> implements ReservationManagement {

    private final ReservationRepository reservationRepository;
    private final BookManagement bookManagement;
    private final UserManagement userManagement;


    public ReservationManagementImpl(ReservationRepository reservationRepository, BookManagement bookManagement, UserManagement userManagement) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.bookManagement = bookManagement;
        this.userManagement = userManagement;
    }

    @Override
    public List<Reservation> getReservationsByStatusAndUserIdAndBookId(String status, Long userId, Long bookId) {
        log.info("GetReservation : status: {}, userId: {}, bookId: {}", status, userId, bookId);
        if(status != null)
            status = status.toUpperCase();
        return reservationRepository.findByCurrentStatusAndUserIdAndBookId(status, bookId, userId);
    }

    @Override
    public Reservation createNewReservation(Long bookId, Long userId, UserPrincipal userPrincipal) {
        log.info("CreateNewReservation : bookId: {}, userId: {}, UserPrincipal: {}", bookId, userId, userPrincipal.getUsername());
        Optional<Book> book = bookManagement.findOne(bookId);
        Optional<User> user = userManagement.findOne(userId);
        Reservation reservation = new Reservation();
        if(book.isPresent() && user.isPresent() && (userPrincipal.getId().equals(userId) || userPrincipal.hasRole("ADMIN"))){
            try{
                ReservationStatus status = new ReservationStatus();
                status.setDate(LocalDateTime.now());
                status.setStatus(StatusEnum.PENDING);
                reservation.addStatus(status);

                checkBookReservation(book.get());
                book.get().addReservation(reservation);

                checkUserReservation(user.get(), bookId);
                user.get().addReservation(reservation);

                reservationRepository.save(reservation);
            }catch (Exception e){
                log.warn("Wrong parameter while creating new reservation : {}", e.getMessage());
                throw new ReservationException(e.getMessage());
            }
        }else{
            log.error("Unexpected request for creating reservation : {}", userPrincipal.getUsername());
            throw new UnauthorizedException("Impossible to create reservation");
        }
        log.info("New reservation created : {}", reservation.toString());
        return reservation;
    }

    @Override
    public Optional<Reservation> updateReservation(Long reservationId, String status, UserPrincipal userPrincipal) {
        log.info("UpdateReservation : reservationId: {}, status: {}, userPrincipal: {}",reservationId, status, userPrincipal.getUsername());
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (reservation.isPresent() && (userPrincipal.hasRole("ADMIN") || userPrincipal.getId().equals(reservation.get().getUser().getId()))){
            StatusEnum statusEnum;
            try{
                statusEnum = StatusEnum.valueOf(status.toUpperCase());
            }catch (Exception ex){
                log.error("Unexpected status passed to update reservation status : {}", status);
                throw new UnknowStatusException("Status " + status + " doesn't exists");
            }
            if(!userPrincipal.hasRole("ADMIN") && !statusEnum.equals(StatusEnum.CANCELED)){
                log.error("Unexpected attempt to update a reservation with wrong status : user : {}, status : {}", userPrincipal.getUsername(), status);
                throw new UnauthorizedException("Users can't change status of a reservation");
            }
            ReservationStatus reservationStatus = ReservationStatus.builder().status(statusEnum).date(LocalDateTime.now()).build();
            reservation.get().addStatus(reservationStatus);
            return Optional.of(reservationRepository.save(reservation.get()));
        }
        log.warn("Reservation update failed");
        return Optional.empty();
    }

    @Override
    public List<Reservation> updateAndGetExpiredReservation() {
        log.info("Request for expired reservations");
        List<Reservation> reservationList = reservationRepository.findByCurrentStatusAndUserIdAndBookId(StatusEnum.RESERVED.name(), null, null);
        List<Reservation> reservationListExpired = new ArrayList<>();
        reservationList.forEach(reservation -> {
            if(reservation.getCurrentStatusDate().plusDays(2).isBefore(LocalDateTime.now())){
                reservation.addStatus(ReservationStatus.builder().date(LocalDateTime.now()).status(StatusEnum.CANCELED).build());
                reservationRepository.save(reservation);
                reservationListExpired.add(reservation);
                log.info("Reservation expired : {}, status : {}, date : {}", reservation.getId(), reservation.getCurrentStatus(), reservation.getCurrentStatusDate());
            }
        });
        log.info("{} expired reservations found", reservationListExpired.size());
        return reservationListExpired;
    }

    /**
     * Will look for reservations for user with <b>id</b>
     * Will retrieve all reservations for each book concerned by user's reservations
     * in order to calculate his position in the reservation book pending list.
     * @param id user having reservations
     * @return reservation list
     */
    @Override
    public List<Reservation> getReservationsByUser(Long id) {
        log.info("Retrieving reservations for user {}", id);
        Optional<User> user = userManagement.findOne(id);
        if(user.isPresent()){
            log.info("One user found : {}", user.get().getEmail());
            for (Reservation reservation:user.get().getReservations()) {
                log.info("Retrieving all reservations for book {}, concerned by reservation {}", reservation.getBook().getId(), reservation.getId());
                List<Reservation> reservationForBook = reservationRepository.findByCurrentStatusAndUserIdAndBookId(null, reservation.getBook().getId(),null);
                log.info("Calculating user position in reservation pending list");
                reservation.setUserPositionInList(calculateUserPositionInReservationList(reservationForBook, user.get().getId()));
                reservation.setNextReturnDate(bookManagement.getNextReturnDate(reservation.getBook().getId()));
            }
            return user.get().getReservations();
        }
        throw new UnknownUserException("User "+id+" doesn't exists");
    }

    /**
     * Filter pending reservations, sort them by date ascending
     * and return the position of <b>userId</b> in this filtered and sorted list
     * @param reservations list of reservations in which to search for user's position
     * @param userId id of user that we want the position from the list
     * @return the user's position
     */
    private int calculateUserPositionInReservationList(List<Reservation> reservations, Long userId) {
        log.info("Filtering and sorting {} active reservations", reservations.size());
        List<Reservation> activeReservationsSorted = reservations
                .stream()
                .filter(reservation -> reservation.getCurrentStatus().equals("PENDING"))
                .sorted(Comparator.comparing(Reservation::getCurrentStatusDate))
                .collect(Collectors.toList());
        log.info("{} reservations still present", activeReservationsSorted.size());

        log.info("Calculating user position");
        int index  = IntStream.range(0, activeReservationsSorted.size())
                .filter(i -> activeReservationsSorted.get(i).getUser().getId().equals(userId))
                .findFirst()
                .orElse(-1);
        log.info("User position calculated : {}", index+1);
        return index+1;
    }

    private void checkBookReservation(Book book){
        if (book.getNbCopiesAvailable() != 0){
            log.warn("Attempt to reserve book with available copies {}", book.getId());
            throw new BookStillAvailableException("Impossible to add reservation, book id " + book.getId() + " has " + book.getNbCopiesAvailable() + " copies available");
        }
        if (book.getReservations().size() == book.getCopyList().size() * 2){
            log.warn("Attempt to reserve book with full pending list {}", book.getId());
            throw new FullReservationListException("Reservation List full : size:"+book.getReservations().size()+", book copies:"+ book.getCopyList().size());
        }
    }

    private void checkUserReservation(User user, Long bookId){
        // check if user does not have already a copy of the book
        user.getLoans().forEach(loan -> {
            if(loan.getBookCopy().getBook().getId().equals(bookId)
                    && !loan.getCurrentStatus().equals(StatusDesignation.RETURNED.toString())){
                log.warn("User attempt to reserve a book already loaned : user : {}, book : {}", user.getId(), bookId);
                throw new BookAlreadyLoanedException("User has already a copy of the book " + bookId);
            }
        });
        user.getReservations().forEach(userReservations -> {
            if(userReservations.getBook().getId().equals(bookId)
                    && !(userReservations.getCurrentStatus().equals(StatusEnum.CANCELED.name()) || userReservations.getCurrentStatus().equals(StatusEnum.TERMINATED.name()))){
                log.warn("User attempt to reserve a book already reserved : User : {}, book : {}",user.getId(), bookId);
                throw new ReservationAlreadyExistsException("User has already a current reservation for the book " + bookId);
            }
        });
    }
}
