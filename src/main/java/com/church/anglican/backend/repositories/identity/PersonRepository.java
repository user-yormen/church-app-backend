package com.church.anglican.backend.repositories.identity;

import com.church.anglican.backend.entities.identity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    @Query("""
        select p from Person p
        where (:status is null or p.status = :status)
          and (:q is null or trim(:q) = '' or
               lower(p.fullName) like lower(concat('%', :q, '%')) or
               lower(p.firstName) like lower(concat('%', :q, '%')) or
               lower(p.lastName) like lower(concat('%', :q, '%')) or
               lower(coalesce(p.emailAddress, '')) like lower(concat('%', :q, '%')) or
               lower(coalesce(p.phoneNumber, '')) like lower(concat('%', :q, '%')))
        """)
    Page<Person> search(@Param("q") String query, @Param("status") Person.PersonStatus status, Pageable pageable);
}
