package com.smart.controller;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	 private  UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	
	//handler for add common data
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) { 
		

		//principal object store the username of autenticated user
		 String username = principal.getName();
		 System.out.println(username);
		 
		  User user = this.userRepository.getUserByUserName(username);
		   System.out.println(user.getPassword());
		   model.addAttribute("user",user);
		 
	}
	//handler for open dashboard
	@GetMapping("/index")
	public String dashboard(Model model) {
	   model.addAttribute("title","user dashboard");
		return"normal/user_dashboard";
	}
	
	//open add contact form handler
   @GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

	   
	   model.addAttribute("title","add-contact");
	   model.addAttribute("contact",new Contact());
	  return"normal/add_contact_form";
 	
}
   
   @PostMapping("/contact-process")
   public String contactProcess(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,  Principal principal,HttpSession session) {
	   try {
			/*
			 * String fileContent = FileToStringConverter.convertToString(file);
			 * contact.setImage(fileContent);
			 */
			
			  System.out.println(contact);
			  String name = principal.getName(); 
			  User user = this.userRepository.getUserByUserName(name); 
			  
			  //upload the image 
			  
			  
			  if(file.isEmpty()) {
				  System.out.println("please upload the file");
				  contact.setImage("default_contact.jpg");			
				  }
			 else {
			  contact.setImage(file.getOriginalFilename());
			   File file2 = new ClassPathResource("static/images").getFile();
			  Path path =  (Path) Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
				   
			 Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
			 }
			  user.getContacts().add(contact);
			  contact.setUser(user);	
			  this.userRepository.save(user);
			  System.out.println("contact saved");
			 
			  //success message send
    	       session.setAttribute("message", new Message("contact added successfully","success"));
    	   
       } catch (Exception e) {
           e.printStackTrace();
           
           //error message
           session.setAttribute("message", new Message("contact not added","danger"));
           return "Error occurred while processing the file.";
       }
	   return"normal/add_contact_form";   
	   
	  
   }
   //per-page=5[n]
   //current page = 0[page]
   @GetMapping("/show-contacts/{page}")
   public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
	   
	    String userName =  principal.getName();
	     User user  =   this.userRepository.getUserByUserName(userName);
	   //current page-page hoga
	 	//contact per page-5 hoga
	     Pageable pageable = PageRequest.of(page,7);
	     Page<Contact> contacts=  this.contactRepository.findContactByUser(user.getId(),pageable);
	     model.addAttribute("contacts",contacts);
	     model.addAttribute("title","show contact");
	     model.addAttribute("currentPage",page);
	     model.addAttribute("totalPages",contacts.getTotalPages());
	   
	   
	   return "normal/show_contacts";
   }
   @GetMapping("/contact/{cid}")
   public String showingDetail(@PathVariable("cid") int cid,Model model) {
	   
	   System.out.println(cid);
	     Contact contact = this.contactRepository.getById(cid);
	     
	     model.addAttribute("title","show contact_details");
	   
	     model.addAttribute("contact",contact);
	   
	   
	   return "normal/contact_detail";
   }
   @GetMapping("/profile/{id}")
   public String showProfile(@PathVariable("id") int id,Model model) {
	   
	   User user = this.userRepository.getById(id);
	   model.addAttribute("title","user-profile");
	   model.addAttribute("user",user);
	    
	   return "normal/user_profile";
   }
   
   //delete a single contact
   @GetMapping("/delete/{cid}")
   public String deleteContact(@PathVariable("cid") int cid,HttpSession session,Principal principal) {
	   
	   
	    Optional<Contact> optionalcontact = this.contactRepository.findById(cid);
	     Contact contact = (Contact) optionalcontact.get();
	      String email =  principal.getName();
	        User user = this.userRepository.getUserByUserName(email);
	        if(user.getId() == contact.getUser().getId()) {
		    	 
	        	  contact.setUser(null);
		    	 this.contactRepository.delete(contact);
			     session.setAttribute("message", new Message("contact delete successfully","success"));
		  	   
		     }
	   
	  
	    
	   return "redirect:/user/show-contacts/0";
   }
   
   //handler for open update form
   @PostMapping("/update-form/{cid}")
   public String openUpdateForm(@PathVariable("cid") int cid,Model model) {
	   
	   
	  Optional optional =  this.contactRepository.findById(cid);
	   Contact contact =  (Contact) optional.get();
	   model.addAttribute("contact",contact);
	   model.addAttribute("title","update-form");
	   return"normal/contact_update";
   }
   
   //handler for update contact
   @PostMapping("/update-process")
   public String update(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,Principal principal,HttpSession session) {
	   
	  try {
		  
		 //fetch old contact details
		    Contact oldContactDetail = this.contactRepository.findById(contact.getContactId()).get();
		  //System.out.println("contact id is"+contact.getContactId());
		    
		    //image ka work
		    
		    if(!file.isEmpty()) {
		    	
		    	
		    	//delete old image
		    	 File deleteFile = new ClassPathResource("static/images").getFile();
		    	 File file1 = new File(deleteFile,oldContactDetail.getImage());
		    	 file1.delete();
				  
		    	//update new image
		    	 
				   File file2 = new ClassPathResource("static/images").getFile();
				  Path path =  (Path) Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
					   
				 Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				 
				 contact.setImage(file.getOriginalFilename());
		    	
		    	
		    }else 
		    {
		    	contact.setImage(oldContactDetail.getImage());
		    	
		    }
		    
		  String username  =  principal.getName();
		  User user1 =  this.userRepository.getUserByUserName(username);
		  contact.setUser(user1);
		  this.contactRepository.save(contact);
		  
		  session.setAttribute("message",new Message("update successfully...","success"));
		  
		  
	}//end of try block
	  catch (Exception e) {
		
		e.printStackTrace();
	
	}//end of catch block
		 return"redirect:/user/contact/"+contact.getContactId();
   }
   
   //handler for open settings
   @GetMapping("/settings")
   public String openSettings() {
	   return"normal/settings";
   }
   //handler for open change password module
   @GetMapping("/passwordForm")
   public String openChangePasswordForm() {
	   return"normal/changePassword";
   }
   
   
   //handler for process change password 
   
   @PostMapping("/change-password")
   public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session) {
	   
	   String email = principal.getName();
	    User user = this.userRepository.getUserByUserName(email);
	   //user.getPassword();
	    if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
	    	
	    	//set the new password 
	    	user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
	    	//commit or update the data base
	    	this.userRepository.save(user);
	    	session.setAttribute("message", new Message("Password has changed", "success"));
	    	
	    }
	    else {
	    	//error
	    	session.setAttribute("message", new Message("Something went wrong and try again", "warning"));
	    	return"normal/changePassword";
	    }
	  
	   
	   return"normal/user_dashboard";
   }
   
   
 //handler for open update user profile form
   @GetMapping("/update-user-profile")
   public String openUpdateUser() {
	  
	   return"normal/update_user_profile";
   }
   
   //handler for update user profile
   
   @GetMapping("/userupdate")
   public String updateUser(@ModelAttribute User user,MultipartFile file,Principal principal,HttpSession session) {
	   
	  Optional optional =  this.userRepository.findById(user.getId());
	   User olduser = (User)optional.get();
	   System.out.println(user.getName());
	   System.out.println(user.getEmail());
	   //image ka work
	    
	   try {
		
		   if(!file.isEmpty()) {
		    	
		    	
		    	//delete old image
		    	 File deleteFile = new ClassPathResource("static/images").getFile();
		    	 File file1 = new File(deleteFile,olduser.getImageUrl());
		    	 file1.delete();
				  
		    	//update new image
		    	 
				   File file2 = new ClassPathResource("static/images").getFile();
				  Path path =  (Path) Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
					   
				 Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				 
				 user.setImageUrl(file.getOriginalFilename());
		    	
		    	
		    }else 
		    {
		    	user.setImageUrl(olduser.getImageUrl());
		    	
		    }
		
		   //work for update process
		   
		   String username  =  principal.getName();
		   User user1 =  this.userRepository.getUserByUserName(username);
		   this.userRepository.save(user1);
			 
			  
			  session.setAttribute("message",new Message("update successfully...","success"));
			  
	} catch (Exception e) {
		e.printStackTrace();
		
	} 
	   return"normal/user_dashboard";
   }
   
}

