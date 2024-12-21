package common.advice;

import common.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//@ControllerAdvice
//public class ApiResponseAdvice implements ResponseBodyAdvice<ApiResponse<?>> {
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return ApiResponse.class.isAssignableFrom(returnType.getParameterType());
//    }
//
//    @Override
//    public ApiResponse<?> beforeBodyWrite(ApiResponse<?> body, MethodParameter returnType,
//                                          MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType,
//                                          ServerHttpRequest request, ServerHttpResponse response) {
//        response.setStatusCode(HttpStatus.valueOf(body.getStatusCode()));
//        return body;
//    }
//}