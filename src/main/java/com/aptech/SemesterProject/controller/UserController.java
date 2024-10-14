package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.constant.RoleEnum;
import com.aptech.SemesterProject.dto.GuideDto;
import com.aptech.SemesterProject.dto.HttpResponse;
import com.aptech.SemesterProject.dto.UserWithFullReviewsAndBookingsDto;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.EmailService;
import com.aptech.SemesterProject.service.StorageService;
import com.aptech.SemesterProject.service.UserService;
import com.aptech.SemesterProject.utility.Crypto;
import com.aptech.SemesterProject.utility.SpringConfig;
import com.aptech.SemesterProject.utility.StorageProperties;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SpringConfig springConfig;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StorageService storageService;
    @Autowired
    private StorageProperties storageProperties;

    public static RoleEnum convertStringToEnum(String roleString) {
        try {
            return RoleEnum.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role string: " + roleString);
            return null; // Handle invalid input as needed
        }
    }

    public static boolean checkEmail(String value) {
        if (value == null) {
            return false;
        }
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> signUp(@RequestParam(name = "name") String name, @RequestParam(name = "email") String email, HttpServletRequest request, @RequestParam(name = "password") String password) throws MessagingException, UnknownHostException, CustomRuntimeException {
        if (!checkEmail(email)) {
            throw new CustomRuntimeException("Invalid email");
        }
        validateNewEmail("", email);
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(RoleEnum.USER);
        String token = UUID.randomUUID().toString().substring(0, 15);
        String hashedToken = Crypto.HashPassword(token, springConfig.getSalt());
        u.setAccountActivateToken(hashedToken);
        User result = userService.addUser(u);
        Map<String, Object> template = new HashMap<>();
        template.put("recipientName", result.getName());
        template.put("text", "Thank you for signing up at our website");
        template.put("senderName", "Vy Truong");
        String url = "http://localhost:5173/verifyAccount/" + result.getEmail() + "/" + token;
        template.put("website", url);
        try {
            emailService.sendMessageUsingThymeleafTemplate(result.getEmail(), "Welcome", template, "welcome_user.html");
        } catch (MessagingException | UnsupportedEncodingException e) {
            userRepo.deleteById(result.getId());
            return new ResponseEntity<>(result, HttpStatus.BAD_GATEWAY);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam(name = "name") String name,
                                           @RequestParam(name = "email") String email,
                                           @RequestParam(name = "password") String password,
                                           @RequestParam(name = "role") String role,
                                           @RequestParam(name = "photo", required = false) MultipartFile photo,
                                           @RequestParam(name = "fullName") String fullName,
                                           @RequestParam(name = "nationalID") String nationalID,
                                           @RequestParam(name = "nationality") String nationality,
                                           @RequestParam(name = "countryFlag") String countryFlag
    ) throws CustomRuntimeException {
        if (!checkEmail(email)) {
            throw new CustomRuntimeException("Invalid email");
        }
        validateNewEmail("", email);

        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(passwordEncoder.encode(password));
        u.setCreatedAt(new Date());
        u.setRole(convertStringToEnum(role));
        u.setNationalID(nationalID);
        u.setNationality(nationality);
        u.setCountryFlag(countryFlag);
        u.setFullName(fullName);
        if (photo != null && photo.getSize() > 0) {
            String photoDir = storageService.store(storageProperties.getLocation(), "user", photo);
            if (photoDir.length() > 0) u.setPhoto(photoDir);
        }

        User result = userService.addUser(u);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUserCrucialInfo(@RequestParam(name = "userId") String userId, @RequestParam(name = "fullName") String fullName, @RequestParam(name = "nationality") String nationality, @RequestParam(name = "nationalID") String nationalID, @RequestParam(name = "countryFlag") String countryFlag) throws CustomRuntimeException {
        User u = userRepo.findById(userId).orElse(null);
        if (u == null) throw new CustomRuntimeException("Invalid user");
        u.setFullName(fullName);
        u.setNationality(nationality);
        u.setNationalID(nationalID);
        u.setCountryFlag(countryFlag);
        User result = userRepo.save(u);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/unlocked-account")
    public ResponseEntity<Object> unlockedAccount(@RequestParam(name = "email") String email) throws CustomRuntimeException {
        if (!checkEmail(email)) {
            throw new CustomRuntimeException("Invalid email");
        }
        List<User> users = userRepo.findByEmail(email);
        if (users == null || users.isEmpty()) {
            throw new EntityNotFoundException("No user found by this email");
        }
        User user = users.get(0);
        String message = "Your account don't need unlocked";
        if (!user.isNotLocked()) {
            user.setNotLocked(true);
            userService.updateUser(user);
            message = "Your account has been unlocked";
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestParam(name = "id") String id,
                                           @RequestParam(name = "name") String name,
                                           @RequestParam(name = "role") String role,
                                           @RequestParam(name = "enable") String isActive,
                                           @RequestParam(name = "photo",required = false) MultipartFile photo,
                                           @RequestParam(name = "fullName") String fullName,
                                           @RequestParam(name = "nationalID") String nationalID,
                                           @RequestParam(name = "nationality") String nationality,
                                           @RequestParam(name = "countryFlag") String countryFlag) throws CustomRuntimeException, IOException {

        User u = userRepo.findById(id).orElse(null);
        if (u == null) throw new CustomRuntimeException("No user found");

        u.setName(name);
        u.setRole(convertStringToEnum(role));
        u.setActive(Boolean.parseBoolean(isActive));
        if (photo != null && photo.getSize() > 0) {
            if(u.getPhoto()!=null && u.getPhoto().length()>0){
                Path file = storageService.load(u.getPhoto(),"user");
                if(Files.exists(file)) {
                    storageService.deleteFile(u.getPhoto(), "user");
                }
            }
            String photoDir = storageService.store(storageProperties.getLocation(), "user", photo);
            if (photoDir.length() > 0) u.setPhoto(photoDir);
        }
        u.setNationalID(nationalID);
        u.setNationality(nationality);
        u.setCountryFlag(countryFlag);
        u.setFullName(fullName);
        User result = userService.updateUser(u);
//        Map<String, Object> template = new HashMap<>();
//        template.put("recipientName", result.getName());
//        template.put("text", "Your infomation has been change");
//        template.put("senderName", "Vy Truong");
//        String url = "http://localhost:8080/api/v1/users/change-password";
//        template.put("website", url);
//        try {
//            emailService.sendMessageUsingThymeleafTemplate(result.getEmail(), "Change information", template, "changeInfo_user.html");
//        } catch (MessagingException e) {
//            return new ResponseEntity<>(result, HttpStatus.BAD_GATEWAY);
//        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<HttpResponse> deleteUser(@RequestParam(name = "id") String userId) throws IOException {
        userService.deleteUser(userId);
        Optional<User> u = userRepo.findById(userId);
        if (u.isPresent()) {
            if (u.get().getPhoto() != null && !u.get().getPhoto().isEmpty()) {
                Path file = storageService.load(u.get().getPhoto(), "user");
                if (Files.exists(file)) {
                    storageService.deleteFile(u.get().getPhoto(), "user");
                }
            }
        }

        return new ResponseEntity<>(new HttpResponse(HttpStatus.NO_CONTENT.value(), HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.getReasonPhrase(), "User has been delete successfully"), HttpStatus.OK);
    }


    private User validateNewEmail(String currentEmail, String newEmail) throws CustomRuntimeException {


        List<User> newUserByEmail = userRepo.findByEmail(newEmail);

        if (StringUtils.isNotBlank(currentEmail)) {
            /// update
            User currentUser = userRepo.findByEmail(currentEmail).get(0);
            if (currentUser == null) {
                throw new CustomRuntimeException("No user found by username " + currentEmail);
            }
            if (newUserByEmail != null && !currentUser.getId().equals(newUserByEmail.get(0).getId())) {
                throw new CustomRuntimeException("Email has been registred for other user");
            }
            return currentUser;
        } else {
            /// create new
            if (newUserByEmail != null && newUserByEmail.size() > 0) {
                throw new CustomRuntimeException("Email has been registred for other user");
            }
            return null;
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserWithFullReviewsAndBookingsDto> getUserById(@PathVariable String id) throws CustomRuntimeException {
        UserWithFullReviewsAndBookingsDto u = userService.getUser(id);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) throws CustomRuntimeException {
        if (!checkEmail(email)) throw new CustomRuntimeException("Email invalid");
        User u = null;
        List<User> list = userRepo.findByEmail(email);
        if (list == null || list.isEmpty()) {
            throw new EntityNotFoundException("Can't find user from this email");
        }
        u=list.get(0);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<User> changePassword(@RequestParam(name = "email") String email,
                                               @RequestParam(name = "oldPassword") String oldPass,
                                               @RequestParam(name = "newPassword") String newPass,
                                               @RequestParam(name = "confirmPassword") String confirmPass) throws CustomRuntimeException {

        if (!checkEmail(email)) {
            throw new CustomRuntimeException("Invalid Email");
        }
        List<User> users = userRepo.findByEmail(email);
        if (users == null || users.isEmpty()) {
            throw new EntityNotFoundException("Can't find user from this email");
        }
        User u = users.get(0);
        if (oldPass.isEmpty()) {
            throw new CustomRuntimeException("The old password is required");
        }
        if (!oldPass.matches("\\S*")) {
            throw new CustomRuntimeException("The old password cannot contain spaces.");
        }
        boolean isPasswordMatch = passwordEncoder.matches(oldPass, u.getPassword());
        if (!isPasswordMatch) {
            throw new CustomRuntimeException("Wrong password");
        }
        if (newPass.isEmpty()) {
            throw new CustomRuntimeException("The new password is required");
        }
        if (!newPass.matches("\\S*")) {
            throw new CustomRuntimeException("The new password cannot contain spaces.");
        }
        if (confirmPass.isEmpty()) {
            throw new CustomRuntimeException("The confirm password is required");
        }
        if (!confirmPass.matches("\\S*")) {
            throw new CustomRuntimeException("The confirm password cannot contain spaces.");
        }

        if (oldPass.equals(newPass)) {
            throw new CustomRuntimeException("The new password must not be the same as the old password");
        }
        if (!confirmPass.equals(newPass)) {
            throw new CustomRuntimeException("The confirm password do not match");
        }

        u.setPassword(passwordEncoder.encode(newPass));
        User result = userService.updateUser(u);

        Map<String, Object> template = new HashMap<>();
        template.put("recipientName", result.getName());
        template.put("text", "Your information has been change");
        template.put("senderName", "Vy Truong");
        String url = "http://localhost:8080/api/v1/users/change-password";
        template.put("website", url);
        try {
            emailService.sendMessageUsingThymeleafTemplate(result.getEmail(), "Security", template, "changeInfo_user.html");
        } catch (MessagingException | UnsupportedEncodingException e) {

            return new ResponseEntity<>(result, HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/reset/{email}")
    public ResponseEntity<User> sendRequestResetPassword(@PathVariable(name = "email") String email) {
        List<User> users = userRepo.findByEmail(email);
        if (users == null || users.isEmpty()) {
            throw new EntityNotFoundException("No user found");
        }
        User u = users.get(0);
        String token = UUID.randomUUID().toString().substring(0, 15);
        String hashedToken = Crypto.HashPassword(token, springConfig.getSalt());
        u.setPasswordResetToken(hashedToken);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 5);
        u.setPasswordResetExpires(calendar.getTime());

        User result = userService.updateUser(u);

        Map<String, Object> template = new HashMap<>();
        template.put("recipientName", result.getName());
        template.put("text", "Your information has been change");
        template.put("senderName", "Vy Truong");
        String url = "http://localhost:5173/reset-password/" + result.getEmail() + "/" + token;
        template.put("website", url);
        try {
            emailService.sendMessageUsingThymeleafTemplate(result.getEmail(), "Security", template, "reset_password.html");
        } catch (MessagingException | UnsupportedEncodingException e) {
            User user = userRepo.findByEmail(result.getEmail()).get(0);
            user.setPasswordResetToken(null);
            user.setPasswordResetExpires(null);
            result = userService.updateUser(user);
            return new ResponseEntity<>(result, HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/reset-password/{email}/{token}")
    public ResponseEntity<Object> resetPassword(@PathVariable String email, @PathVariable String token, @RequestParam(name = "newPasswordReset") String newPasswordReset) throws CustomRuntimeException {
        List<User> users = userRepo.findByEmail(email);
        if (users == null || users.isEmpty()) {
            throw new EntityNotFoundException("No user found");
        }
        String hashedToken = Crypto.HashPassword(token, springConfig.getSalt());
        User user = userService.findUserWithEmailAndToken(email, hashedToken, "passwordResetToken");
        if (user == null) {
            throw new CustomRuntimeException("No token found");
        }
        if ((new Date()).after(user.getPasswordResetExpires())) {
            throw new CustomRuntimeException("Your token has been expires");
        }
        if (newPasswordReset.isEmpty()) {
            throw new CustomRuntimeException("The new password is required");
        }
        if (!newPasswordReset.matches("\\S*")) {
            throw new CustomRuntimeException("The new password cannot contain spaces.");
        }
        Map<String, Object> template = new HashMap<>();
        template.put("recipientName", user.getName());
        template.put("text", "Your password has been reset");
        template.put("senderName", "Vy Truong");

        try {
            user.setPassword(passwordEncoder.encode(newPasswordReset));
            user.setPasswordChangedAt(new Date());
            user.setPasswordResetExpires(null);
            user.setPasswordResetToken(null);
            userService.updateUser(user);
            emailService.sendMessageUsingThymeleafTemplate(user.getEmail(), "Security", template, "reset_password_success.html");

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomRuntimeException("Reset fail");
        }

        return new ResponseEntity<>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "Your password is now reset,please login again"), HttpStatus.OK);
    }

    @GetMapping("/guides")
    public ResponseEntity<List<GuideDto>> getAllGuides() {
        return new ResponseEntity<>(userService.getAllGuides(), HttpStatus.OK);
    }



}
