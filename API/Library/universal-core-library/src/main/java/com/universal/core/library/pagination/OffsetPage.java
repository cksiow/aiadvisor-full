package com.universal.core.library.pagination;

import com.universal.core.library.exception.BadRequestException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Optional;

public class OffsetPage implements Pageable, Serializable {

    private static final long serialVersionUID = 1L;
    private final Sort sort;
    private int limit;
    private int offset;

    public OffsetPage(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new BadRequestException("Offset index must not be less than zero!");
        }

        if (limit < 0) {
            throw new BadRequestException("Limit must not be less than zero!");
        }
        this.limit = limit == 0 ? Integer.MAX_VALUE : limit;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetPage(int offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    public OffsetPage(int offset, int limit) {
        this(offset, limit, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public boolean isUnpaged() {
        return false;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return null;
    }

    @Override
    public Pageable next() {
        return new OffsetPage((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    public OffsetPage previous() {
        return hasPrevious() ? new OffsetPage((int) (getOffset() - getPageSize()), getPageSize(), getSort())
                : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetPage(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return this;
    }


    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public Optional<Pageable> toOptional() {
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof OffsetPage))
            return false;

        OffsetPage that = (OffsetPage) o;

        return new EqualsBuilder().append(limit, that.limit).append(offset, that.offset).append(sort, that.sort)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(limit).append(offset).append(sort).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("limit", limit).append("offset", offset).append("sort", sort)
                .toString();
    }
}