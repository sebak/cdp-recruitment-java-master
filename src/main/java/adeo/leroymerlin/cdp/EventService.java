package adeo.leroymerlin.cdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAllBy();
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event with id " + id + " Not Found"));
    }

    @Transactional
    public void delete(Long id) {
        Event event = findEventById(id);
        event.setBands(new HashSet<>());
        eventRepository.save(event);
        eventRepository.delete(event);
    }

    public void updateEvent(Long id, Event event) {
        Event eventToUpdate = findEventById(id);
        eventToUpdate.setComment(event.getComment());
        eventToUpdate.setNbStars(event.getNbStars());
        eventRepository.save(eventToUpdate);
    }

    private Predicate<Set<Member>> filterByMemberName(String query) {
        return members -> members.stream().map(Member::getName).anyMatch(name -> name.contains(query));
    }

    private Predicate<Set<Band>> filterByBand(String query) {
        return bands -> bands.stream().map(Band::getMembers).anyMatch(members -> filterByMemberName(query).test(members));
    }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = eventRepository.findAllBy();
        // Filter the events list in pure JAVA here
        return events.stream().filter(event -> filterByBand(query).test(event.getBands())).collect(Collectors.toList());
    }
}
