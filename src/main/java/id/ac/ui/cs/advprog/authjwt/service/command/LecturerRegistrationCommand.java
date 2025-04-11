package id.ac.ui.cs.advprog.authjwt.service.command;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LecturerRegistrationCommand extends RegistrationCommand {
    public LecturerRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, User user) {
        super(userRepository,passwordEncoder,user);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> addUser() {
        Map<String, String> response = new HashMap<>();
        if(user.getPassword() == null
                || user.getUsername() == null
                || user.getFullName() == null
                || user.getNip() == null
                || user.getPassword().isEmpty()
                || user.getUsername().isEmpty()
                || user.getFullName().isEmpty()
                || user.getNip().isEmpty()
        ) {
            response.put("status", "error");
            response.put("message", "Invalid payload");
            return new ResponseEntity<>(response, HttpStatus.valueOf(403));
        }

        if(userRepository.existsByUsername(user.getUsername())) {
            response.put("status", "error");
            response.put("message", "Username already exists");
            return new ResponseEntity<>(response, HttpStatus.valueOf(404));
        }

        try{
            User newUser = new User(
                    UUID.randomUUID(),
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getFullName(),
                    true,
                    user.getNip()
            );

            userRepository.save(newUser);

            response.put("status", "accept");
            response.put("messages", "Success register");
            response.put("username", newUser.getUsername());
            response.put("role", "LECTURER");

            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(401));
        }
    }

}