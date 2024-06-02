package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PasswordForgotController {

	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/forgot")
	public String openForgotPasswordForm() {
		
		return"/forgot_password";
	}
	
	//handler for send otp
   @PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		
	   //code for generate 6 digit otp
	   Random random = new Random(10000);
	   int otp =  random.nextInt(999999);
	   System.out.println(otp);
	   
	  String subject = "OTP from Smart Contact Manager";
	  String message ="<h1>OTP :"+otp+" </h1>";
	  String to = email;
	    boolean b = this.emailService.sendEmail(subject,message,to);
	   
	    if(b) {
	    	session.setAttribute("myotp", otp);
	    	session.setAttribute("email",email);
	    	 return"/verify_otp";
	    	
	    }
	    else{
	    	
	    	session.setAttribute("message", "check  your email id please");
	    	
	    	 return"/forgot_password";
	   	  
	    }
	   
	}
	@PostMapping("/verify-otp")
   public String verifyOTP(@RequestParam("otp") int userotp,HttpSession session,Principal principal) {
	   
		
	//code for otp verification from session otp and email id
		
		int myotp = (int)session.getAttribute("myotp");
		System.out.println("myotp is"+myotp);
		String email  = (String)session.getAttribute("email");
		System.out.println("userotp"+userotp);
		
		if(myotp==userotp) {
			
			
			 User user = this.userRepository.getUserByUserName(email);
			if(user==null) {
				//send error message
				session.setAttribute("message", "user does not exist with this email !!");
		    	
		    	 return"/forgot_password";
				
			}else {
				//send change password form
				
			}
			 
			return"/password_change_form";
		}
		
		else {
		  
			session.setAttribute("message","you entered wrong otp" );
			return"/verify_otp";
		}
		
   }
	
	//handler for new password add or change password
	@PostMapping("/newpassword")
 public String newPassword(@RequestParam("newPassword") String newPassword,HttpSession session) {
	 
	     	String email = (String)session.getAttribute("email");
	         User user =  this.userRepository.getUserByUserName(email);
	         
	         user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
	         this.userRepository.save(user);
	         
	        //session.setAttribute("message", "password changed successfully");
	         
	       
		return"redirect:/signin?change=password changed succuessfully..";
 }
	
	
	
	
	
}
