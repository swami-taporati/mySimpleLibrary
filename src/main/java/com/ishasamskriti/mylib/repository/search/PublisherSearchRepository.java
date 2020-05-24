package com.ishasamskriti.mylib.repository.search;

import com.ishasamskriti.mylib.domain.Publisher;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Publisher} entity.
 */
public interface PublisherSearchRepository extends ElasticsearchRepository<Publisher, Long> {}
