package com.ishasamskriti.mylib.web.rest;

import com.ishasamskriti.mylib.MySimpleLibraryApp;
import com.ishasamskriti.mylib.domain.Author;
import com.ishasamskriti.mylib.domain.Book;
import com.ishasamskriti.mylib.repository.AuthorRepository;
import com.ishasamskriti.mylib.service.AuthorService;
import com.ishasamskriti.mylib.service.dto.AuthorCriteria;
import com.ishasamskriti.mylib.service.AuthorQueryService;

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
 * Integration tests for the {@link AuthorResource} REST controller.
 */
@SpringBootTest(classes = MySimpleLibraryApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class AuthorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorQueryService authorQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuthorMockMvc;

    private Author author;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createEntity(EntityManager em) {
        Author author = new Author()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME);
        return author;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createUpdatedEntity(EntityManager em) {
        Author author = new Author()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME);
        return author;
    }

    @BeforeEach
    public void initTest() {
        author = createEntity(em);
    }

    @Test
    @Transactional
    public void createAuthor() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();
        // Create the Author
        restAuthorMockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isCreated());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate + 1);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAuthor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    public void createAuthorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();

        // Create the Author with an existing ID
        author.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorMockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = authorRepository.findAll().size();
        // set the field null
        author.setFirstName(null);

        // Create the Author, which fails.


        restAuthorMockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isBadRequest());

        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = authorRepository.findAll().size();
        // set the field null
        author.setLastName(null);

        // Create the Author, which fails.


        restAuthorMockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isBadRequest());

        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAuthors() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList
        restAuthorMockMvc.perform(get("/api/authors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));
    }
    
    @Test
    @Transactional
    public void getAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get the author
        restAuthorMockMvc.perform(get("/api/authors/{id}", author.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(author.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME));
    }


    @Test
    @Transactional
    public void getAuthorsByIdFiltering() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        Long id = author.getId();

        defaultAuthorShouldBeFound("id.equals=" + id);
        defaultAuthorShouldNotBeFound("id.notEquals=" + id);

        defaultAuthorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAuthorShouldNotBeFound("id.greaterThan=" + id);

        defaultAuthorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAuthorShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAuthorsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where firstName equals to DEFAULT_FIRST_NAME
        defaultAuthorShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the authorList where firstName equals to UPDATED_FIRST_NAME
        defaultAuthorShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByFirstNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where firstName not equals to DEFAULT_FIRST_NAME
        defaultAuthorShouldNotBeFound("firstName.notEquals=" + DEFAULT_FIRST_NAME);

        // Get all the authorList where firstName not equals to UPDATED_FIRST_NAME
        defaultAuthorShouldBeFound("firstName.notEquals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultAuthorShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the authorList where firstName equals to UPDATED_FIRST_NAME
        defaultAuthorShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where firstName is not null
        defaultAuthorShouldBeFound("firstName.specified=true");

        // Get all the authorList where firstName is null
        defaultAuthorShouldNotBeFound("firstName.specified=false");
    }
                @Test
    @Transactional
    public void getAllAuthorsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where firstName contains DEFAULT_FIRST_NAME
        defaultAuthorShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the authorList where firstName contains UPDATED_FIRST_NAME
        defaultAuthorShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where firstName does not contain DEFAULT_FIRST_NAME
        defaultAuthorShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the authorList where firstName does not contain UPDATED_FIRST_NAME
        defaultAuthorShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }


    @Test
    @Transactional
    public void getAllAuthorsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where lastName equals to DEFAULT_LAST_NAME
        defaultAuthorShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the authorList where lastName equals to UPDATED_LAST_NAME
        defaultAuthorShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByLastNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where lastName not equals to DEFAULT_LAST_NAME
        defaultAuthorShouldNotBeFound("lastName.notEquals=" + DEFAULT_LAST_NAME);

        // Get all the authorList where lastName not equals to UPDATED_LAST_NAME
        defaultAuthorShouldBeFound("lastName.notEquals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultAuthorShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the authorList where lastName equals to UPDATED_LAST_NAME
        defaultAuthorShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where lastName is not null
        defaultAuthorShouldBeFound("lastName.specified=true");

        // Get all the authorList where lastName is null
        defaultAuthorShouldNotBeFound("lastName.specified=false");
    }
                @Test
    @Transactional
    public void getAllAuthorsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where lastName contains DEFAULT_LAST_NAME
        defaultAuthorShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the authorList where lastName contains UPDATED_LAST_NAME
        defaultAuthorShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void getAllAuthorsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where lastName does not contain DEFAULT_LAST_NAME
        defaultAuthorShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the authorList where lastName does not contain UPDATED_LAST_NAME
        defaultAuthorShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }


    @Test
    @Transactional
    public void getAllAuthorsByBookIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);
        Book book = BookResourceIT.createEntity(em);
        em.persist(book);
        em.flush();
        author.addBook(book);
        authorRepository.saveAndFlush(author);
        Long bookId = book.getId();

        // Get all the authorList where book equals to bookId
        defaultAuthorShouldBeFound("bookId.equals=" + bookId);

        // Get all the authorList where book equals to bookId + 1
        defaultAuthorShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuthorShouldBeFound(String filter) throws Exception {
        restAuthorMockMvc.perform(get("/api/authors?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));

        // Check, that the count call also returns 1
        restAuthorMockMvc.perform(get("/api/authors/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuthorShouldNotBeFound(String filter) throws Exception {
        restAuthorMockMvc.perform(get("/api/authors?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuthorMockMvc.perform(get("/api/authors/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get("/api/authors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuthor() throws Exception {
        // Initialize the database
        authorService.save(author);

        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Update the author
        Author updatedAuthor = authorRepository.findById(author.getId()).get();
        // Disconnect from session so that the updates on updatedAuthor are not directly saved in db
        em.detach(updatedAuthor);
        updatedAuthor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME);

        restAuthorMockMvc.perform(put("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAuthor)))
            .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAuthor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc.perform(put("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(author)))
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAuthor() throws Exception {
        // Initialize the database
        authorService.save(author);

        int databaseSizeBeforeDelete = authorRepository.findAll().size();

        // Delete the author
        restAuthorMockMvc.perform(delete("/api/authors/{id}", author.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
