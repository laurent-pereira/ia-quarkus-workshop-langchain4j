package dev.langchain4j.quarkus.workshop;

import static dev.langchain4j.quarkus.workshop.Exceptions.*;

import java.time.LocalDate;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import dev.langchain4j.agent.tool.Tool;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BookingRepository implements PanacheRepository<Booking> {

    private static final Logger LOG = Logger.getLogger(BookingRepository.class);

    @Tool("Cancel a booking")
    @Transactional
    public void cancelBooking(long bookingId, String customerFirstName, String customerLastName) {
        LOG.infof("cancelBooking called with bookingId=%d, customerFirstName=%s, customerLastName=%s", bookingId,
                customerFirstName, customerLastName);
        var booking = getBookingDetails(bookingId, customerFirstName, customerLastName);

        // Add log for dates
        LOG.infof("Booking details: dateFrom=%s, dateTo=%s", booking.dateFrom, booking.dateTo);
        // Add log for minuisDays calculations
        LOG.infof("Current date: %s", LocalDate.now());
        // Comparaison log
        LOG.infof("Booking dateFrom minus 11 days: %s", booking.dateFrom.minusDays(11));
        LOG.infof("Booking dateTo minus 4 days: %s", booking.dateTo.minusDays(4));

        // too late to cancel
        if (booking.dateFrom.minusDays(11).isBefore(LocalDate.now())) {
            throw new BookingCannotBeCancelledException(bookingId, "booking from date is 11 days before today");
        }
        // too short to cancel
        if (booking.dateTo.minusDays(4).isBefore(booking.dateFrom)) {
            throw new BookingCannotBeCancelledException(bookingId, "booking period is less than four days");
        }
        delete(booking);
    }

    @Tool("List booking for a customer")
    @Transactional
    public List<Booking> listBookingsForCustomer(String customerName, String customerSurname) {
        LOG.infof("listBookingsForCustomer called with customerName=%s, customerSurname=%s", customerName,
                customerSurname);
        var found = Customer.findByFirstAndLastName(customerName, customerSurname);

        return found
                .map(customer -> list("customer", customer))
                .orElseThrow(() -> new CustomerNotFoundException(customerName, customerSurname));
    }

    @Tool("Get booking details")
    @Transactional
    public Booking getBookingDetails(long bookingId, String customerFirstName, String customerLastName) {
        LOG.infof("getBookingDetails called with bookingId=%d, customerFirstName=%s, customerLastName=%s", bookingId,
                customerFirstName, customerLastName);
        var found = findByIdOptional(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!found.customer.firstName.equals(customerFirstName) || !found.customer.lastName.equals(customerLastName)) {
            throw new BookingNotFoundException(bookingId);
        }
        return found;
    }
}
