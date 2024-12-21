package consulting.gazman.security.service;

import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.User;

import java.util.List;

public interface UserService {
    ApiResponse<List<User>> getAllUsers();
    ApiResponse<User> findById(Long id);
    ApiResponse<User> save(User user);
    ApiResponse<User> update(Long id, User user);
    ApiResponse<Void> delete(Long id);
}
