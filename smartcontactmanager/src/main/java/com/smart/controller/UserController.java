package com.smart.controller;

import java.security.Principal;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.ConatctRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Controller
@RequestMapping("/user")
public class UserController {
	
	
	public BCryptPasswordEncoder bycyrpt;
	
	@Autowired
	private ConatctRepository  contactrepo;

	@Autowired	
	private UserRepository userRepository;
    
	@ModelAttribute
	public void addCommondata(Model model,Principal principal) {
		 String userName= principal.getName();
		  
		
		System.out.println("USERNAME" + userName);
		 
		 User user =userRepository.getUserByUserName(userName);
		 
		 System.out.println("USER " + user);
		 model.addAttribute("user",user);
		
		
	}
	
	//dashboard home
		@RequestMapping("/index")
	public String dashboard(Model  model , Principal principal) {
			  model.addAttribute("title","DashBoard");
		 
			return "admin/normal/user_dashboard";
	}
		
	//open add form handler
		@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
	 model.addAttribute("title","Add Contact");
	 model.addAttribute("contact",new Contact());
		return "admin/normal/add_contact";
	}
	     
		@PostMapping("/process_contact")
		public String process_contact(@ModelAttribute Contact contact,Principal principal,HttpSession session) {
			try {
			String name= principal.getName();
			User user =this.userRepository.getUserByUserName(name);
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.print("Added");
			System.out.print("data " + contact);
			  session.setAttribute("message", new Message("Your Contact is added !! ","success"));
			}catch(Exception e) {
				e.printStackTrace();
				  session.setAttribute("message", new Message("Something Went Wrong !! ","danger"));
				
			}
			return "admin/normal/add_contact";
		}
		
		@GetMapping("/show-contacts/{page}")
		public String showContacts(@PathVariable("page") Integer page,Model m,Principal prin) {
			
			
			
			m.addAttribute("title","User Contacts");
			
			String username =prin.getName();
		 User user=	this.userRepository.getUserByUserName(username);
		
		 Pageable pageable = PageRequest.of(page, 3);
		 
		 Page<Contact> contacts =this.contactrepo.findConatctsByUSer(user.getId(),pageable);
			
		 m.addAttribute("contacts",contacts);
		 m.addAttribute("currentpage",page);
		 m.addAttribute("totalPages",contacts.getTotalPages());
			return "admin/normal/show_contacts";
		}
		
		@RequestMapping("/{cId}/contact")
		public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal pr) {
			
			System.out.println(cId);
			
			String name =pr.getName();
			User user =this.userRepository.getUserByUserName(name);
			
			
			
			
			java.util.Optional<Contact> contactop = this.contactrepo.findById(cId);
			 Contact contact =contactop.get();
			 
			 
			 if(user.getId() == contact.getUser().getId()) {
				 model.addAttribute("contact",contact);
			         model.addAttribute("title",contact.getName());
			 }
			 
			 model.addAttribute("model",contact);
			
			return "admin/normal/contact_details";
			
		}
		
		@GetMapping("/delete/{cId}")
		public String deletecontact(@PathVariable("cId")Integer cId,Model model,HttpSession session) {
			
			 
			 Contact contact = this.contactrepo.findById(cId).get();
			 contact.setUser(null);
			 this.contactrepo.delete(contact);
			 session.setAttribute("message", new Message("Deleted Successfuly!!!","success"));
			 return "redirect:/user/show-contacts/0";
		}
		
		@PostMapping("/update-contact/{cId}")
		public String updateForm(Model model,@PathVariable("cId")Integer cId ) {
			model.addAttribute("title","Update Contact");
			Contact contact= this.contactrepo.findById(cId).get();
			model.addAttribute("contact",contact);
			return "admin/normal/update_form";
		}
		
		
		@RequestMapping(value="/process_update",method = RequestMethod.POST)
		public String upadteprofile(@ModelAttribute Contact contact,Model model,HttpSession session,Principal pr) {
			System.out.print("CONTACT ME" + contact.getName());
			System.out.print("CONTACT ME" + contact.getcId());
			
			User user = this.userRepository.getUserByUserName(pr.getName());
			contact.setUser(user);
			this.contactrepo.save(contact);
			session.setAttribute("message", new Message("Your contact is updated.." ,"Success"));
			return "redirect://user/"+contact.getcId()+"/contact";
		}
		
		@GetMapping("/profile")
		public String yourprofile(Model mode) {
			mode.addAttribute("title","Profile Page");
			return "admin/normal/profile";
		}
		
		
		@GetMapping("/settings")
	public String openSetting() {
		return "admin/normal/settings";
	}
		
		@PostMapping("/change-password")
		public String chnagepassword(@RequestParam("oldPassword") String oldpassword,@RequestParam("newPassword")String newPassword,Principal princi,HttpSession session ) {
			
			String username = princi.getName();
			User currentUser = this.userRepository.getUserByUserName(username);
			System.out.println();
			System.out.println("Old Password" + oldpassword);
			System.out.println("New Password" + newPassword);
			if(this.bycyrpt.matches(oldpassword, currentUser.getPassword())) {
				currentUser.setPassword(this.bycyrpt.encode(newPassword));
				this.userRepository.save(currentUser);
			//	session.setAttribute("message", new Message("Your Password is Changed..." ,"success"));
				
			}else {
				//session.setAttribute("message", new Message("Wrong Old Password..." ,"danger"));
				//return "redirect:/user/settings";
			}
			 return "redirect:/user/index";
		}
}
