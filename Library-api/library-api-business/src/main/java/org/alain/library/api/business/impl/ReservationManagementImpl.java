package org.alain.library.api.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.business.exceptions.ReservationException;
import org.alain.library.api.business.exceptions.UnauthorizedException;
import org.alain.library.api.business.exceptions.UnknowStatusException;
import org.alain.library.api.business.exceptions.UnknownUserException;
import org.alain.library.api.consumer.repository.BookRepository;
import org.alain.library.api.consumer.repository.ReservationRepository;
import org.alain.library.api.consumer.repository.UserRepository;
import org.alain.library.api.mail.contract.EmailService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Main class to manage the {@see Reservation} objects
 */

@Service
@Slf4j
public class ReservationManagementImpl extends CrudManagementImpl<Reservation> implements ReservationManagement {

    @Value("reservation.expiration.days")
    private static int RESERVATION_EXPIRATION_DAYS;

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final EmailService emailService;


    public ReservationManagementImpl(ReservationRepository reservationRepository, UserRepository userRepository, BookRepository bookRepository, EmailService emailService) {
        super(reservationRepository);
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.emailService = emailService;
    }

    /**
     * Returns a list of {@see Reservation} with filters
     * @param status currentStatus of the reservation {@see StatusEnum}
     * @param userId user id concerned by the reservation {@see User}
     * @param bookId book id concerned by the reservation {@see Book}
     * @return list od reservations {@see Reservation}
     */
    @Override
    public List<Reservation> getReservationsByStatusAndUserIdAndBookId(String status, Long userId, Long bookId) {
        log.info("GetReservation : status: {}, userId: {}, bookId: {}", status, userId, bookId);
        if(status != null)
            status = status.toUpperCase();
        return reservationRepository.findByCurrentStatusAndUserIdAndBookId(status, bookId, userId);
    }

    /**
     * Create and persist a new {@see Reservation} Object
     * @param bookId book id concerned by the reservation
     * @param userId user id concerned by the reservation
     * @param userPrincipal user authentication requesting the creation
     * @return created reservation
     */
    @Override
    public Reservation createNewReservation(Long bookId, Long userId, UserPrincipal userPrincipal) {
        log.info("CreateNewReservation : bookId: {}, userId: {}, UserPrincipal: {}", bookId, userId, userPrincipal.getUsername());
        Optional<Book> book = bookRepository.findById(bookId);
        Optional<User> user = userRepository.findById(userId);
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

    /**
     * Add a new status to an existing reservation
     * @param reservationId id of the reservation
     * @param status new status that should be added {@see StatusEnum}
     * @param userPrincipal authentication of the user requesting the update
     * @return updated reservation {@see Reservation}
     */
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
            Reservation updatedReservation = reservationRepository.save(reservation.get());
//   TODO check         if(reservationStatus.getStatus().equals(StatusEnum.CANCELED))
//                this.checkPendingListAndNotify(reservation.get().getBook().getId());
            return Optional.of(updatedReservation);
        }
        log.warn("Reservation update failed");
        return Optional.empty();
    }

    /**
     * check expired reservations,
     * update their status to 'CANCELED' {@see StatusEnum}
     * and call pending list check
     * @return list of reservations expired and modified
     */
    @Override
    public List<Reservation> updateAndGetExpiredReservation() {
        log.info("Request for expired reservations");
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(RESERVATION_EXPIRATION_DAYS);
        List<Reservation> reservationList = reservationRepository.findExpired(StatusEnum.RESERVED.name(), expirationDate);
        log.info("{} expired reservations found", reservationList.size());
        reservationList.forEach(reservation -> {
            reservation.addStatus(ReservationStatus.builder().date(LocalDateTime.now()).status(StatusEnum.CANCELED).build());
            reservationRepository.save(reservation);
            this.checkPendingListAndNotify(reservation.getBook().getId());
            log.info("Reservation expired : {}, status : {}, date : {}", reservation.getId(), reservation.getCurrentStatus(), reservation.getCurrentStatusDate());
        });
        return reservationList;
    }

    /**
     * Will look for reservations for user with <b>id</b>
     * call method {@see getUserPositionInReservationList} to retrieve user position in the reservation book pending list.
     * @param id user having reservations
     * @return reservation list with user position.
     */
    @Override
    public List<Reservation> getReservationsByUser(Long id) {
        log.info("Retrieving reservations for user {}", id);
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            log.info("One user found : {}", user.get().getEmail());
            for (Reservation reservation:user.get().getReservations()) {
                reservation.setUserPositionInList(getUserPositionInReservationList(reservation.getBook().getId(),id));
                reservation.setNextReturnDate(reservation.getBook().getNextReturnDate());
            }
            return user.get().getReservations();
        }
        throw new UnknownUserException("User "+id+" doesn't exists");
    }

    /**
     * Check if a reservation is pending
     * select first one in pending list
     * update and save status to RESERVED {@see StatusEnum}
     * and call EmailService {@see EmailService}
     * @param bookId that we want to check the pending list
     */
    @Override
    public void checkPendingListAndNotify(Long bookId) {
        log.info("Checking if a pending reservation exists for book {}", bookId);
        List<Reservation> reservationList = reservationRepository.findActiveReservationForBookOrderByDate(bookId);
        log.info("{} pending reservation for book {}", reservationList.size(), bookId);
        if(!reservationList.isEmpty()){
            Reservation reservation = reservationList.get(0);
            reservation.addStatus(ReservationStatus.builder().date(LocalDateTime.now()).status(StatusEnum.RESERVED).build());
            reservationRepository.save(reservation);
            log.info("Added status 'RESERVED' to reservation {}, and sending email to {}", reservation.getId(), reservation.getUser().getEmail());
            emailService.sendEmailForReservationAvailable(reservation);
        }
    }

    /**
     * Get active (pending) reservations, for book
     * and return the position of <b>userId</b> in thislist
     * @param bookId book concerned by reservations
     * @param userId id of user that we want the position from the list
     * @return the user's position if user not present or reservation not pending will return o.
     */
    private int getUserPositionInReservationList(Long bookId, Long userId){
        log.info("Retrieving all active reservations for book {}", bookId);
        List<Reservation> reservationList = reservationRepository.findActiveReservationForBookOrderByDate(bookId);
        log.info("{} reservations found", reservationList.size());
        int index = 1;
        log.info("Retrieving user position in reservation pending list");
        for (Reservation reservation : reservationList) {
            if (reservation.getUser().getId().equals(userId)) {
                return index;
            }
            index++;
        }
        return 0;
    }

    /**
     * Check if a Reservation can be added to the book
     * @param book to check reservations from
     */
    private void checkBookReservation(Book book){
        if (book.getNbCopiesAvailable() != 0){
            log.warn("Attempt to reserve book with available copies {}", book.getId());
            throw new BookStillAvailableException("Impossible to add reservation, book id " + book.getId() + " has " + book.getNbCopiesAvailable() + " copies available");
        }
        if (book.isReservationListFull()){
            log.warn("Attempt to reserve book with full pending list {}", book.getId());
            throw new FullReservationListException("Reservation List full : size:"+book.getReservations().size()+", book copies:"+ book.getCopyList().size());
        }
    }

    /**
     * check if a user already have a copy of the book in loan or in reservation
     * @param user we want to check
     * @param bookId to check loan and reservations
     */
    private void checkUserReservation(User user, Long bookId){
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
