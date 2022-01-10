package adeo.leroymerlin.cdp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAllBy();
}
