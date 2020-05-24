package com.ishasamskriti.mylib.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AuthorSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AuthorSearchRepositoryMockConfiguration {
    @MockBean
    private AuthorSearchRepository mockAuthorSearchRepository;
}
