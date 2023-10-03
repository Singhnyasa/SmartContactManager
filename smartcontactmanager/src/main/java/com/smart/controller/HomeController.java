package com.smart.controller;

import javax.servlet.http.HttpSession;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userrepo;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home-Smart Contact manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About-Smart Contact manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Signup-Smart Contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	@RequestMapping(value="/do_register",method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model,BindingResult  result,HttpSession session) {
		try {
			
			if(result.hasErrors()) {
				//System.out.print("ERROR", result.toString());
			      model.addAttribute("user",user);
				return "signup";
			}
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.print("USER" + user);
			 User result1=this.userrepo.save(user);
		   // model.addAttribute("user",result);
		    // model.addAttribute(user);
		     model.addAttribute("user", new User());
		     session.setAttribute("message",new Message("Successfully registered","alert-success"));
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("Something went wrong"+e.getMessage(),"alert-danger"));
		    
		}
		return "signup";
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title" , "Login-Page");
		return "login";
	}
	
   
}
