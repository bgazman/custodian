package common.dto;

import java.util.Map;

public class ApiRequest<T> {
    private T data;
    private Map<String, Object> metadata;
    // getters and setters
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}