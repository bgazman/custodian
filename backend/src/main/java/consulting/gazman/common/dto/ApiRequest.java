package consulting.gazman.common.dto;


import lombok.Data;

import lombok.Builder;

@Data
@Builder
public class ApiRequest<T> {
    private ApiHeader header; // Request metadata
    private T data;           // Request payload
}

