package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public List<User> getAllUsers() {
        // Fetch all users from the database
        return  userRepository.findAll();


    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
    }

    @Override
    public User save(User user) {
;
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));

        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Assuming the repository method returns Optional<User>
    }

    @Override
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void verifyEmail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public void resetFailedLoginAttempts(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    @Override
    public void trackLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.resourceNotFound("User not found with ID: " + id));
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public User createUser(User user) {
        // Check if the email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw AppException.userAlreadyExists("A user with this email already exists");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default properties
        user.setEmailVerified(false); // Default to unverified
        user.setEnabled(false);       // Default to disabled
//        user.setRegisteredAt(LocalDateTime.now());
        user.setLastPasswordChange(LocalDateTime.now());

        // Save the user to the database
        return userRepository.save(user);
    }
}


