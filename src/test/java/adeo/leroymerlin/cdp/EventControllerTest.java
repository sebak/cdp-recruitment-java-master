package adeo.leroymerlin.cdp;

import adeo.leroymerlin.cdp.factory.EventFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Before
    public void setupDb() {
        eventRepository.saveAll(EventFactory.buildEvent());
    }

    @After
    public void cleanDb() {
        eventRepository.deleteAll();
    }

    @Test
    public void shouldDeleteEventById() throws Exception {
        List<Event> eventsBeforeDelete = Lists.newArrayList(eventRepository.findAll());
        assertEquals(2, eventsBeforeDelete.size());
        Long eventIdToDelete = eventsBeforeDelete.get(0).getId();

        mockMvc.perform(delete("/api/events/{id}", eventIdToDelete))
                .andExpect(status().isOk());

        assertFalse(eventRepository.findById(eventIdToDelete).isPresent());
    }

    @Test
    public void shouldUpdateEventById() throws Exception {
        Event event = eventRepository.findAllBy().get(0);
        Long eventIdToUpdate = event.getId();

        Event eventUpdate = new Event();
        eventUpdate.setComment("I Like It");
        eventUpdate.setNbStars(3);

        mockMvc.perform(put("/api/events/{id}", eventIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUpdate)))
                .andExpect(status().isOk());

        Event eventUpdated = eventRepository.findById(eventIdToUpdate).get();

        assertEquals(eventUpdate.getComment(), eventUpdated.getComment());
        assertEquals(eventUpdate.getNbStars(), eventUpdated.getNbStars());
    }

    @Test
    public void shouldSearchByQueryContainInAtLeastOneMemberOfBandsOfEachEvent() throws Exception {
        List<Event> events = Lists.newArrayList(eventRepository.findAll());
        String query = "king";

        mockMvc.perform(get("/api/events/search/{query}", query))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(events.size())));
    }

    @Test
    public void shouldSearchByQueryContainOnlyInAtLeastOneMemberOfBandsOnOneOfTowEvent() throws Exception {
        List<Event> events = Lists.newArrayList(eventRepository.findAll());
        String query = "audrey";

        assertEquals(2, events.size());
        mockMvc.perform(get("/api/events/search/{query}", query))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    public void shouldSearchByQueryNonContainInAnyOfMembersOfBandsOfAllEvents() throws Exception {
        List<Event> events = Lists.newArrayList(eventRepository.findAll());
        String query = "notExist";

        assertEquals(2, events.size());
        mockMvc.perform(get("/api/events/search/{query}", query))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }
}
