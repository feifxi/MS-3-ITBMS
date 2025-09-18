package sit.int204.itbmsbackend.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {
    private List<T> content;
    private Boolean last;
    private Boolean first;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private String sort;
    @JsonIgnore
    private Integer number;

    public Integer getPage() {
        return number;
    }
}