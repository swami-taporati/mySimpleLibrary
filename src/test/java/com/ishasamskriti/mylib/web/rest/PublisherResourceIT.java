package com.ishasamskriti.mylib.web.rest;

import com.ishasamskriti.mylib.MySimpleLibraryApp;
import com.ishasamskriti.mylib.domain.Publisher;
import com.ishasamskriti.mylib.repository.PublisherRepository;
import com.ishasamskriti.mylib.service.PublisherService;
import com.ishasamskriti.mylib.service.dto.PublisherCriteria;
import com.ishasamskriti.mylib.service.PublisherQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PublisherResource} REST controller.
 */
@SpringBootTest(classes = MySimpleLibraryApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class PublisherResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private PublisherQueryService publisherQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPublisherMockMvc;

    private Publisher publisher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publisher createEntity(EntityManager em) {
        Publisher publisher = new Publisher()
            .name(DEFAULT_NAME);
        return publisher;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publisher createUpdatedEntity(EntityManager em) {
        Publisher publisher = new Publisher()
            .name(UPDATED_NAME);
        return publisher;
    }

    @BeforeEach
    public void initTest() {
        publisher = createEntity(em);
    }

    @Test
    @Transactional
    public void createPublisher() throws Exception {
        int databaseSizeBeforeCreate = publisherRepository.findAll().size();
        // Create the Publisher
        restPublisherMockMvc.perform(post("/api/publishers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isCreated());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeCreate + 1);
        Publisher testPublisher = publisherList.get(publisherList.size() - 1);
        assertThat(testPublisher.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createPublisherWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = publisherRepository.findAll().size();

        // Create the Publisher with an existing ID
        publisher.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublisherMockMvc.perform(post("/api/publishers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = publisherRepository.findAll().size();
        // set the field null
        publisher.setName(null);

        // Create the Publisher, which fails.


        restPublisherMockMvc.perform(post("/api/publishers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isBadRequest());

        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPublishers() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList
        restPublisherMockMvc.perform(get("/api/publishers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publisher.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getPublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get the publisher
        restPublisherMockMvc.perform(get("/api/publishers/{id}", publisher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publisher.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }


    @Test
    @Transactional
    public void getPublishersByIdFiltering() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        Long id = publisher.getId();

        defaultPublisherShouldBeFound("id.equals=" + id);
        defaultPublisherShouldNotBeFound("id.notEquals=" + id);

        defaultPublisherShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPublisherShouldNotBeFound("id.greaterThan=" + id);

        defaultPublisherShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPublisherShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPublishersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where name equals to DEFAULT_NAME
        defaultPublisherShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the publisherList where name equals to UPDATED_NAME
        defaultPublisherShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPublishersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where name not equals to DEFAULT_NAME
        defaultPublisherShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the publisherList where name not equals to UPDATED_NAME
        defaultPublisherShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPublishersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPublisherShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the publisherList where name equals to UPDATED_NAME
        defaultPublisherShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPublishersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where name is not null
        defaultPublisherShouldBeFound("name.specified=true");

        // Get all the publisherList where name is null
        defaultPublisherShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllPublishersByNameContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where name contains DEFAULT_NAME
        defaultPublisherShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the publisherList where name contains UPDATED_NAME
        defaultPublisherShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPublishersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where name does not contain DEFAULT_NAME
        defaultPublisherShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the publisherList where name does not contain UPDATED_NAME
        defaultPublisherShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPublisherShouldBeFound(String filter) throws Exception {
        restPublisherMockMvc.perform(get("/api/publishers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publisher.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restPublisherMockMvc.perform(get("/api/publishers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPublisherShouldNotBeFound(String filter) throws Exception {
        restPublisherMockMvc.perform(get("/api/publishers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPublisherMockMvc.perform(get("/api/publishers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPublisher() throws Exception {
        // Get the publisher
        restPublisherMockMvc.perform(get("/api/publishers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePublisher() throws Exception {
        // Initialize the database
        publisherService.save(publisher);

        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();

        // Update the publisher
        Publisher updatedPublisher = publisherRepository.findById(publisher.getId()).get();
        // Disconnect from session so that the updates on updatedPublisher are not directly saved in db
        em.detach(updatedPublisher);
        updatedPublisher
            .name(UPDATED_NAME);

        restPublisherMockMvc.perform(put("/api/publishers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPublisher)))
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
        Publisher testPublisher = publisherList.get(publisherList.size() - 1);
        assertThat(testPublisher.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublisherMockMvc.perform(put("/api/publishers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(publisher)))
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePublisher() throws Exception {
        // Initialize the database
        publisherService.save(publisher);

        int databaseSizeBeforeDelete = publisherRepository.findAll().size();

        // Delete the publisher
        restPublisherMockMvc.perform(delete("/api/publishers/{id}", publisher.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
