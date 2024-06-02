package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	//handler for open home page
	@RequestMapping("/")
	public String home(Model model) {
	
		model.addAttribute("title","Home-Smart Contact Manager");
		return"home";
	}
	//handler for open signup form
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","signup-Smart Contact Manager");
		model.addAttribute("user", new User());
		return"signup";
		
	}
	
	//handler for register
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement",defaultValue="false") boolean agreement,@RequestParam("profileImage1") MultipartFile file,Model model,HttpSession session)
	{
	
	//  System.out.println(agreement);
			 
			//check agreement
			try {
				
				  if(file.isEmpty()) {
					  System.out.println("please upload the file");
					  user.setImageUrl("default_contact.jpg");			
					  }
						
						  else { user.setImageUrl(file.getOriginalFilename()); File file2 = new
						  ClassPathResource("static/images").getFile(); Path path = (Path)
						  Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
						  
						  Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
						  }
						 
				 if(!agreement) {
			   	System.out.println("not check terms and conditions");
				   
			   	throw new Exception("not check terms and conditions");
				   }
				  
				  //check validation
				
				 if(result.hasErrors()) {
						
						model.addAttribute("user",user);
						return"signup";
					}
				 
					/*
					 * user.setImageUrl(file.getOriginalFilename()); File file2 = new
					 * ClassPathResource("static/images").getFile(); Path path = (Path)
					 * Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
					 * 
					 * Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
					 */
					    user.setRole("ROLE_USER");
					    user.setEnabled(true);
					  
					    user.setPassword(passwordEncoder.encode(user.getPassword()));
					    this.userRepository.save(user);
					    model.addAttribute("user",new User());
					    System.out.println("success");
					    session.setAttribute("message",new Message("Successfully registered" , "alert-success"));
					    
					    //code for send email after registration
					    
					    String subject = "Welcome to Smart Contact Manager";
						  String message ="<h1>you are registered successfully and your password is"+this.passwordEncoder.encode(user.getPassword())+" </h1>";
						  String to = user.getEmail();
					    this.emailService.sendEmail(subject, message, to);
					    return"signup";
			
				  
			} catch (Exception e) {
				
				
				e.printStackTrace();
		        model.addAttribute("user",user);
				session.setAttribute("message",new Message("something went wrong","alert-danger"));
				return"signup";
			}		
		 
			
			
		
	}
	
	//handler for login 
@GetMapping("/signin")
	public String login(Model  model) {
		
        model.addAttribute("title","login-smart contact manager");
		return"login";
	}
	
	
}
