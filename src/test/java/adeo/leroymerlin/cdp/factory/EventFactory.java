package adeo.leroymerlin.cdp.factory;

import adeo.leroymerlin.cdp.Band;
import adeo.leroymerlin.cdp.Event;
import adeo.leroymerlin.cdp.Member;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventFactory {
    public static List<Event> buildEvent() {
        Set<Member> members1 = Stream.of(new Member("queen alexia"), new Member("queen elisabeth"), new Member("king john"))
                .collect(Collectors.toCollection(HashSet::new));

        Set<Member> members2 = Stream.of(new Member("king paul"), new Member("audrey"), new Member("arnaud"))
                .collect(Collectors.toCollection(HashSet::new));

        Band band1 = new Band();
        band1.setName("band1");
        band1.setMembers(members1);

        Band band2 = new Band();
        band2.setName("band2");
        band2.setMembers(members2);

        Event event1 = new Event();
        event1.setTitle("event1");
        event1.setBands(Collections.singleton(band1));

        Event event2 = new Event();
        event2.setTitle("event2");
        event2.setBands(Collections.singleton(band2));

        return Arrays.asList(event1, event2);


    }
}
