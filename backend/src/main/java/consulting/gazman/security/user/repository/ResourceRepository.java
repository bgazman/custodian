package consulting.gazman.security.user.repository;


import consulting.gazman.security.user.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByType(String type);
    @Query(value = "SELECT * FROM resources WHERE attributes::text LIKE %:attribute%", nativeQuery = true)
    List<Resource> findByAttributesContaining(@Param("attribute") String attribute);
    Optional<Resource> findByName(String systemLogs);

    List<Resource> findByNameContaining(String partialName);
}

