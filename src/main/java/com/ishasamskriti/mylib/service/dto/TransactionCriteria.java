package com.ishasamskriti.mylib.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.ishasamskriti.mylib.domain.Transaction} entity. This class is used
 * in {@link com.ishasamskriti.mylib.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TransactionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter borrowDate;

    private LocalDateFilter returnDate;

    private LongFilter bookId;

    private LongFilter clientId;

    public TransactionCriteria() {
    }

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.borrowDate = other.borrowDate == null ? null : other.borrowDate.copy();
        this.returnDate = other.returnDate == null ? null : other.returnDate.copy();
        this.bookId = other.bookId == null ? null : other.bookId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateFilter borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateFilter getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateFilter returnDate) {
        this.returnDate = returnDate;
    }

    public LongFilter getBookId() {
        return bookId;
    }

    public void setBookId(LongFilter bookId) {
        this.bookId = bookId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(borrowDate, that.borrowDate) &&
            Objects.equals(returnDate, that.returnDate) &&
            Objects.equals(bookId, that.bookId) &&
            Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        borrowDate,
        returnDate,
        bookId,
        clientId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (borrowDate != null ? "borrowDate=" + borrowDate + ", " : "") +
                (returnDate != null ? "returnDate=" + returnDate + ", " : "") +
                (bookId != null ? "bookId=" + bookId + ", " : "") +
                (clientId != null ? "clientId=" + clientId + ", " : "") +
            "}";
    }

}
