package consulting.gazman.security.repository;

import consulting.gazman.security.entity.TenantUser;
import consulting.gazman.security.entity.TenantUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
    Optional<TenantUser> findById(TenantUserId tenantUserId);

    Optional<TenantUser> findByIdTenantIdAndIdUserId(Long tenantId, Long userId);
}