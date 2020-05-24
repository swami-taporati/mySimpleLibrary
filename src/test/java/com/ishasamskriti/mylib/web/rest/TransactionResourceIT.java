package com.ishasamskriti.mylib.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ishasamskriti.mylib.MySimpleLibraryApp;
import com.ishasamskriti.mylib.domain.Book;
import com.ishasamskriti.mylib.domain.Client;
import com.ishasamskriti.mylib.domain.Transaction;
import com.ishasamskriti.mylib.repository.TransactionRepository;
import com.ishasamskriti.mylib.repository.search.TransactionSearchRepository;
import com.ishasamskriti.mylib.service.TransactionQueryService;
import com.ishasamskriti.mylib.service.TransactionService;
import com.ishasamskriti.mylib.service.dto.TransactionCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@SpringBootTest(classes = MySimpleLibraryApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class TransactionResourceIT {
    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BORROW_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_RETURN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_RETURN_DATE = LocalDate.ofEpochDay(-1L);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    /**
     * This repository is mocked in the com.ishasamskriti.mylib.repository.search test package.
     *
     * @see com.ishasamskriti.mylib.repository.search.TransactionSearchRepositoryMockConfiguration
     */
    @Autowired
    private TransactionSearchRepository mockTransactionSearchRepository;

    @Autowired
    private TransactionQueryService transactionQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction().borrowDate(DEFAULT_BORROW_DATE).returnDate(DEFAULT_RETURN_DATE);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction().borrowDate(UPDATED_BORROW_DATE).returnDate(UPDATED_RETURN_DATE);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    public void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        // Create the Transaction
        restTransactionMockMvc
            .perform(
                post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getBorrowDate()).isEqualTo(DEFAULT_BORROW_DATE);
        assertThat(testTransaction.getReturnDate()).isEqualTo(DEFAULT_RETURN_DATE);

        // Validate the Transaction in Elasticsearch
        verify(mockTransactionSearchRepository, times(1)).save(testTransaction);
    }

    @Test
    @Transactional
    public void createTransactionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // Create the Transaction with an existing ID
        transaction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(
                post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Transaction in Elasticsearch
        verify(mockTransactionSearchRepository, times(0)).save(transaction);
    }

    @Test
    @Transactional
    public void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get("/api/transactions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));
    }

    @Test
    @Transactional
    public void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get("/api/transactions/{id}", transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()));
    }

    @Test
    @Transactional
    public void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionShouldBeFound("id.equals=" + id);
        defaultTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate equals to DEFAULT_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.equals=" + DEFAULT_BORROW_DATE);

        // Get all the transactionList where borrowDate equals to UPDATED_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.equals=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate not equals to DEFAULT_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.notEquals=" + DEFAULT_BORROW_DATE);

        // Get all the transactionList where borrowDate not equals to UPDATED_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.notEquals=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate in DEFAULT_BORROW_DATE or UPDATED_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.in=" + DEFAULT_BORROW_DATE + "," + UPDATED_BORROW_DATE);

        // Get all the transactionList where borrowDate equals to UPDATED_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.in=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate is not null
        defaultTransactionShouldBeFound("borrowDate.specified=true");

        // Get all the transactionList where borrowDate is null
        defaultTransactionShouldNotBeFound("borrowDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate is greater than or equal to DEFAULT_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.greaterThanOrEqual=" + DEFAULT_BORROW_DATE);

        // Get all the transactionList where borrowDate is greater than or equal to UPDATED_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.greaterThanOrEqual=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate is less than or equal to DEFAULT_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.lessThanOrEqual=" + DEFAULT_BORROW_DATE);

        // Get all the transactionList where borrowDate is less than or equal to SMALLER_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.lessThanOrEqual=" + SMALLER_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate is less than DEFAULT_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.lessThan=" + DEFAULT_BORROW_DATE);

        // Get all the transactionList where borrowDate is less than UPDATED_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.lessThan=" + UPDATED_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBorrowDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where borrowDate is greater than DEFAULT_BORROW_DATE
        defaultTransactionShouldNotBeFound("borrowDate.greaterThan=" + DEFAULT_BORROW_DATE);

        // Get all the transactionList where borrowDate is greater than SMALLER_BORROW_DATE
        defaultTransactionShouldBeFound("borrowDate.greaterThan=" + SMALLER_BORROW_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate equals to DEFAULT_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.equals=" + DEFAULT_RETURN_DATE);

        // Get all the transactionList where returnDate equals to UPDATED_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.equals=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate not equals to DEFAULT_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.notEquals=" + DEFAULT_RETURN_DATE);

        // Get all the transactionList where returnDate not equals to UPDATED_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.notEquals=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate in DEFAULT_RETURN_DATE or UPDATED_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.in=" + DEFAULT_RETURN_DATE + "," + UPDATED_RETURN_DATE);

        // Get all the transactionList where returnDate equals to UPDATED_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.in=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate is not null
        defaultTransactionShouldBeFound("returnDate.specified=true");

        // Get all the transactionList where returnDate is null
        defaultTransactionShouldNotBeFound("returnDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate is greater than or equal to DEFAULT_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.greaterThanOrEqual=" + DEFAULT_RETURN_DATE);

        // Get all the transactionList where returnDate is greater than or equal to UPDATED_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.greaterThanOrEqual=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate is less than or equal to DEFAULT_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.lessThanOrEqual=" + DEFAULT_RETURN_DATE);

        // Get all the transactionList where returnDate is less than or equal to SMALLER_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.lessThanOrEqual=" + SMALLER_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate is less than DEFAULT_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.lessThan=" + DEFAULT_RETURN_DATE);

        // Get all the transactionList where returnDate is less than UPDATED_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.lessThan=" + UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByReturnDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where returnDate is greater than DEFAULT_RETURN_DATE
        defaultTransactionShouldNotBeFound("returnDate.greaterThan=" + DEFAULT_RETURN_DATE);

        // Get all the transactionList where returnDate is greater than SMALLER_RETURN_DATE
        defaultTransactionShouldBeFound("returnDate.greaterThan=" + SMALLER_RETURN_DATE);
    }

    @Test
    @Transactional
    public void getAllTransactionsByBookIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        Book book = BookResourceIT.createEntity(em);
        em.persist(book);
        em.flush();
        transaction.setBook(book);
        transactionRepository.saveAndFlush(transaction);
        Long bookId = book.getId();

        // Get all the transactionList where book equals to bookId
        defaultTransactionShouldBeFound("bookId.equals=" + bookId);

        // Get all the transactionList where book equals to bookId + 1
        defaultTransactionShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    @Test
    @Transactional
    public void getAllTransactionsByClientIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);
        Client client = ClientResourceIT.createEntity(em);
        em.persist(client);
        em.flush();
        transaction.setClient(client);
        transactionRepository.saveAndFlush(transaction);
        Long clientId = client.getId();

        // Get all the transactionList where client equals to clientId
        defaultTransactionShouldBeFound("clientId.equals=" + clientId);

        // Get all the transactionList where client equals to clientId + 1
        defaultTransactionShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get("/api/transactions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get("/api/transactions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get("/api/transactions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get("/api/transactions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransaction() throws Exception {
        // Initialize the database
        transactionService.save(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction.borrowDate(UPDATED_BORROW_DATE).returnDate(UPDATED_RETURN_DATE);

        restTransactionMockMvc
            .perform(
                put("/api/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getBorrowDate()).isEqualTo(UPDATED_BORROW_DATE);
        assertThat(testTransaction.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);

        // Validate the Transaction in Elasticsearch
        verify(mockTransactionSearchRepository, times(2)).save(testTransaction);
    }

    @Test
    @Transactional
    public void updateNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Transaction in Elasticsearch
        verify(mockTransactionSearchRepository, times(0)).save(transaction);
    }

    @Test
    @Transactional
    public void deleteTransaction() throws Exception {
        // Initialize the database
        transactionService.save(transaction);

        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete("/api/transactions/{id}", transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Transaction in Elasticsearch
        verify(mockTransactionSearchRepository, times(1)).deleteById(transaction.getId());
    }

    @Test
    @Transactional
    public void searchTransaction() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        transactionService.save(transaction);
        when(mockTransactionSearchRepository.search(queryStringQuery("id:" + transaction.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(transaction), PageRequest.of(0, 1), 1));

        // Search the transaction
        restTransactionMockMvc
            .perform(get("/api/_search/transactions?query=id:" + transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));
    }
}
