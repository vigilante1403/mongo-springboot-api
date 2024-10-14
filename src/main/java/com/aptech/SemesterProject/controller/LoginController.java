package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.constant.SecurityConstant;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.UserService;
import com.aptech.SemesterProject.utility.Crypto;
import com.aptech.SemesterProject.utility.JWTTokenProvider;
import com.aptech.SemesterProject.dto.HttpResponse;
import com.aptech.SemesterProject.utility.SpringConfig;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/v1")
public class LoginController {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private SpringConfig springConfig;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam(name="email")String email, @RequestParam(name="password")String password, HttpServletResponse response,@RequestParam(name="type")String type) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        List<User> loginUsers = null;
         loginUsers = userRepository.findByEmail(user.getUsername());
         User loginUser=null;

        if(loginUsers!=null&&loginUsers.size()>0){
            loginUser=loginUsers.get(0);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(loginUser.getRole().toString().equals("USER")&&!type.equals("USER")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        HttpHeaders jwtTokenHeader = new HttpHeaders();
        String token = jwtTokenProvider.generateJwtToken(loginUser);
        System.out.println(token);
        jwtTokenHeader.add(SecurityConstant.JWT_TOKEN_HEADER, token );
        jwtTokenHeader.add(HttpHeaders.AUTHORIZATION,"Bearer "+token);
        Cookie authenticatedCookie = new Cookie("token-vtravel-lib0-authw",token);
        authenticatedCookie.setMaxAge(60*60);
        authenticatedCookie.setHttpOnly(true);
        authenticatedCookie.setSecure(true);
        authenticatedCookie.setPath("/");

        Cookie userInfo = new Cookie("user-vtravel-info",loginUser.getUsername());
        userInfo.setMaxAge(60*60);
        userInfo.setPath("/");
        userInfo.setSecure(true);
        userInfo.setHttpOnly(true);
        Long epochTimeExpires = System.currentTimeMillis()+60*60*1000;
        loginUser.setTokenExpiresInMs(epochTimeExpires);
        userRepository.save(loginUser);
        System.out.println("Token expires at: " + loginUser.getTokenExpiresInMs());

        response.addCookie(authenticatedCookie);
        response.addCookie(userInfo);
        return ResponseEntity
                .ok()
                .headers(jwtTokenHeader)
                .body(loginUser);
    }
    @GetMapping("/unlockMe/{email}/{token}")

        public ResponseEntity<HttpResponse> activateUserAccount(@PathVariable String email, @PathVariable String token) {
            User u = userRepository.findByEmail(email).get(0);
            if (u == null) {
                throw new EntityNotFoundException("No user found");
            }
            String hashedToken = Crypto.HashPassword(token, springConfig.getSalt());
            User user = userService.findUserWithEmailAndToken(email, hashedToken, "accountActivateToken");
            if (user == null) {

                return new ResponseEntity<>(new HttpResponse(HttpStatus.BAD_GATEWAY.value(), HttpStatus.BAD_GATEWAY, HttpStatus.BAD_GATEWAY.getReasonPhrase(), "Activate Fail"), HttpStatus.BAD_GATEWAY);
            }
            try {
                user.setAccountActivateToken(null);
                user.setActive(true);
                userRepository.save(user);
            } catch (Exception e) {

                return new ResponseEntity<>(new HttpResponse(HttpStatus.BAD_GATEWAY.value(), HttpStatus.BAD_GATEWAY, HttpStatus.BAD_GATEWAY.getReasonPhrase(), "Activate Fail"), HttpStatus.BAD_GATEWAY);
            }


            return new ResponseEntity<>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "Your account is now activate,please login to continue"), HttpStatus.OK);
        }
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE','GUIDE','USER')")
    @PostMapping("/authenticate")
    public ResponseEntity<User> checkJWTTokenAuthenticated(HttpServletRequest request,HttpServletResponse response) throws CustomRuntimeException {
        User currentUser=null;
        String token = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null).getValue();
        String username = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("user-vtravel-info")).findFirst().orElse(null).getValue();
        boolean isTokenValid = jwtTokenProvider.isTokenValid(username,token);
        String subject = jwtTokenProvider.getSubject(token);
        Cookie cookie1 = Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null);
        Cookie cookie2 = Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("user-vtravel-info")).findFirst().orElse(null);
        if(!isTokenValid||!subject.equals(username)) {
            if (cookie1 != null) {
                cookie1.setMaxAge(0);
                cookie1.setPath("/");
                cookie1.setHttpOnly(true);
                cookie1.setSecure(true);
                response.addCookie(cookie1);
            }
            if (cookie2 != null) {
                cookie2.setMaxAge(0);
                cookie2.setPath("/");
                cookie2.setHttpOnly(true);
                cookie2.setSecure(true);
                response.addCookie(cookie2);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        currentUser = userRepository.findByEmail(username).stream().findFirst().orElse(null);
        if(currentUser==null) throw new CustomRuntimeException("Validation failed");
        return new ResponseEntity<>(currentUser,HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE','GUIDE','USER')")
    @PostMapping("/logout")
    public ResponseEntity<HttpResponse> clearCookie(HttpServletRequest request,HttpServletResponse response){
        Cookie cookie1 = Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null);
        Cookie cookie2 = Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("user-vtravel-info")).findFirst().orElse(null);
        if(cookie1==null) return ResponseEntity.ok().build();
        if (cookie1 != null) {
            cookie1.setMaxAge(0);
            cookie1.setPath("/");
            cookie1.setHttpOnly(true);
            cookie1.setSecure(true);
            response.addCookie(cookie1);
        }
        if(cookie2==null) return ResponseEntity.ok().build();
        if (cookie2 != null) {
            cookie2.setMaxAge(0);
            cookie2.setPath("/");
            cookie2.setHttpOnly(true);
            cookie2.setSecure(true);
            response.addCookie(cookie2);
        }

        return ResponseEntity
                .ok()
                .build();

    }
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE','GUIDE','USER')")
    @GetMapping("/get-authorized-token")
    public ResponseEntity<String> getAuthorizedToken(HttpServletRequest request,HttpServletResponse response){
        String token = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null).getValue();
        String username = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("user-vtravel-info")).findFirst().orElse(null).getValue();
        boolean isTokenValid = jwtTokenProvider.isTokenValid(username,token);
        String subject = jwtTokenProvider.getSubject(token);
        Cookie cookie1 = Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null);
        Cookie cookie2 = Arrays.stream(request.getCookies()).filter(ck->ck.getName().equals("user-vtravel-info")).findFirst().orElse(null);
        if(!isTokenValid||!subject.equals(username)) {
            if (cookie1 != null) {
                cookie1.setMaxAge(0);
                cookie1.setPath("/");
                cookie1.setHttpOnly(true);
                cookie1.setSecure(true);
                response.addCookie(cookie1);
            }
            if (cookie2 != null) {
                cookie2.setMaxAge(0);
                cookie2.setPath("/");
                cookie2.setHttpOnly(true);
                cookie2.setSecure(true);
                response.addCookie(cookie2);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(token,HttpStatus.OK);
    }
}