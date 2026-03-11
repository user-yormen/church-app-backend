package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
    Page<Group> findByChurchId(UUID churchId, Pageable pageable);

    Page<Group> findByChurchIdAndStatus(UUID churchId, Group.GroupStatus status, Pageable pageable);

    @Query("select g from Group g where g.church.id = :churchId and (:q is null or lower(g.name) like lower(concat('%', :q, '%')) or lower(g.description) like lower(concat('%', :q, '%')))")
    Page<Group> searchByChurch(@Param("churchId") UUID churchId, @Param("q") String query, Pageable pageable);
}
