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

public class StudentRegistrationCommand extends RegistrationCommand {
    public StudentRegistrationCommand(UserRepository userRepository, PasswordEncoder passwordEncoder, User user) {
        super(userRepository,passwordEncoder,user);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> addUser() {
        Map<String, String> response = new HashMap<>();
        Map<String, String> validity = check_invalid_input("student");

        if(user.getPassword() == null
                || user.getUsername() == null
                || user.getFullName() == null
                || user.getNim() == null
                || user.getPassword().isEmpty()
                || user.getUsername().isEmpty()
                || user.getFullName().isEmpty()
                || user.getNim().isEmpty()
        ) {
            response.put("status", "error");
            response.put("message", "Invalid payload");
            return new ResponseEntity<>(response, HttpStatus.valueOf(403));
        }

        if(!"valid".equals(validity.get("message"))) {
            response.put("status", "error");
            response.put("message", validity.get("message"));
            return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(validity.get("code"))));
        }
        
        if(userRepository.existsByNim(user.getNim())) {
            response.put("status", "error");
            response.put("message", "Nim already exists");
            return new ResponseEntity<>(response, HttpStatus.valueOf(404));
        }

        try{
            User newUser = new User(
                    UUID.randomUUID(),
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getFullName(),
                    false,
                    user.getNim()
            );

            userRepository.save(newUser);

            response.put("status", "accept");
            response.put("messages", "Success register");
            response.put("username", newUser.getUsername());
            response.put("role", "STUDENT");

            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }
        catch (Exception e) {
            response.put("status", "error");
            response.put("messages", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.valueOf(401));
        }
    }
}