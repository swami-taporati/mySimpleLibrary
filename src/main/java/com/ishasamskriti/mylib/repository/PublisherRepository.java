package com.ishasamskriti.mylib.repository;

import com.ishasamskriti.mylib.domain.Publisher;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Publisher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long>, JpaSpecificationExecutor<Publisher> {
}
