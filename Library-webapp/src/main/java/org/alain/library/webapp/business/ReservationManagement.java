package org.alain.library.webapp.business;

import io.swagger.client.model.BookDto;
import io.swagger.client.model.ReservationDto;
import org.alain.library.webapp.model.ExtendedBook;
import org.alain.library.webapp.model.ExtendedReservation;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationManagement {

    private final BookManagement bookManagement;

    public ReservationManagement(BookManagement bookManagement) {
        this.bookManagement = bookManagement;
    }

    public List<ExtendedReservation> getExtendedReservations(HttpSession session, List<ReservationDto> reservations) throws IOException {
        List<ExtendedReservation> extendedReservationList = new ArrayList<>();
        for(ReservationDto reservation : reservations){
            BookDto book = bookManagement.getBookDto(reservation.getBookId());
            ExtendedBook extendedBook = new ExtendedBook(book);
            extendedBook.setEarliestReturn(bookManagement.getDateNextReturn(session, extendedBook.getBookDto()));
            ExtendedReservation extendedReservation = new ExtendedReservation(extendedBook, reservation);
            extendedReservationList.add(extendedReservation);
        }
        return extendedReservationList;
    }
}
