package com.universal.core.library.pagination;

import com.universal.core.library.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class OffsetPageService extends Object {

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String ORDERBY = "orderBy";
    private static final String ORDERDIRECTION = "orderDirection";
    @Autowired
    HttpServletRequest request;

    public HttpServletRequest getCurrentRequest() {
        return request;
    }

    private List<String> getQueryParams(String param) {
        return request == null ? null
                : ServletUriComponentsBuilder.fromCurrentRequest().build().getQueryParams().get(param);
    }

    private String getFirstDecodeStringFromList(List<String> list) {
        return URLDecoder.decode(list.get(0), StandardCharsets.UTF_8);

    }

    private int getOffset() {
        List<String> offset = getQueryParams(OFFSET);

        if (offset == null || offset.isEmpty()) {
            return 0;
        } else if (StringUtils.isNumeric(getFirstDecodeStringFromList(offset))
                && Integer.parseInt(getFirstDecodeStringFromList(offset)) >= 0) {
            return Integer.parseInt(getFirstDecodeStringFromList(offset));
        } else {
            throw new BadRequestException("offset must be positive value");
        }
    }

    private int getLimit(Optional<Integer> defaultLimit) {
        List<String> limit = getQueryParams(LIMIT);

        if (limit == null || limit.isEmpty()) {
            return defaultLimit.isEmpty() ? Integer.MAX_VALUE : defaultLimit.get();
        } else if (StringUtils.isNumeric(getFirstDecodeStringFromList(limit))
                && Integer.parseInt(getFirstDecodeStringFromList(limit)) > 0) {
            return Integer.parseInt(getFirstDecodeStringFromList(limit));
        } else {
            throw new BadRequestException("limit must be equal or greater than 0");
        }
    }

    private String getOrderBy(String defaultOrder) {
        List<String> orderBy = getQueryParams(ORDERBY);
        return orderBy == null ? defaultOrder : getFirstDecodeStringFromList(orderBy);
    }

    private Sort.Direction getOrderDirection(Sort.Direction defaultOrder) {
        List<String> value = getQueryParams(ORDERDIRECTION);
        return value == null ? defaultOrder : getDirection(Integer.parseInt(getFirstDecodeStringFromList(value)));
    }

    private Sort.Direction getDirection(int direction) {
        return direction == 1 ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public OffsetPage getOffsetPage(Optional<Integer> defaultLimit, Optional<Direction> direction,
                                    Optional<String> defaultOrder) {
        return new OffsetPage(this.getOffset(), this.getLimit(defaultLimit),
                getOrderDirection(direction.orElse(Direction.ASC)), this.getOrderBy(defaultOrder.orElse("id")));
    }

}
