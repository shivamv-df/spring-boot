package com.smart.controller;

import java.util.List;
import java.util.Optional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import com.smart.helper.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepositry;
import com.smart.dao.UserrRepositry;
import com.smart.entites.Contact;
import com.smart.entites.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserrRepositry userrRepositry;

	@Autowired
	private ContactRepositry contactRepositry;

	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println("username" + username);
		User user = userrRepositry.getUserByUserName(username);
		System.out.println("user" + user);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal) {
		System.out.println("working");
		model.addAttribute("title", "user dashboard");
		return "normal/user_dashboard.html";
	}

	@GetMapping("/add-contact")
	public String openAddContact(Model model) {
		model.addAttribute("tiittle", "add Contact");
		model.addAttribute(" contact", new Contact());
		return "normal/add_contact.html";
	}

	@PostMapping("/process-contact")
	public String processingAddContactForm(@ModelAttribute Contact contact, HttpSession session,
			@RequestParam("profileImage") MultipartFile file, Principal principal) {
		try {
			System.out.println("DATA" + contact);
			String name = principal.getName();
			User user = this.userrRepositry.getUserByUserName(name);
			// processing and uploading file
			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("contact.png");

			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userrRepositry.save(user);
			session.setAttribute("message", new Message("your contact is save add more", "success"));
		} catch (Exception e) {
			System.out.println("Error" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("something went wrong", "danger"));
		}
		return "normal/add_contact.html";
	}

	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "show contacts");
		String userName = principal.getName();
		User user = this.userrRepositry.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 8);
		Page<Contact> contact = this.contactRepositry.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contact", contact);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());

		return "normal/show-contacts";

	}

	@RequestMapping("{id}/contact")
	public String showContactDetails(@PathVariable("id") Integer id, Model model, Principal principal) {

		Optional<Contact> contactOptonal = this.contactRepositry.findById(id);
		Contact contact = contactOptonal.get();
		model.addAttribute("contact", contact);
		String userName = principal.getName();
		User user = this.userrRepositry.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", "viec contact details");

		}
		return "normal/contact_detail";
	}

	@GetMapping("/delete/{Id}")
	public String deleteContact(@PathVariable("Id") Integer Id, Model model, Principal principal, HttpSession session) {

		Contact contact = this.contactRepositry.findById(Id).get();
		contact.setUser(null);
		this.contactRepositry.delete(contact);
		session.setAttribute("message", new Message("contact deleted suceesfully", "success"));

		return "redirect:/user/show-contact/0";

	}

	@PostMapping("update-contact/{Id}")
	public String updateFormhandler(@PathVariable("Id") Integer Id, Model model) {
		model.addAttribute("title", "update contact details");
		Contact contact = this.contactRepositry.findById(Id).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	@PostMapping("/update-contact")
	public String upateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {
		System.out.println("contact name" + contact.getFirstName());
		System.out.println("contact name" + contact.getEmail());
		Contact oldContatc = this.contactRepositry.findById(contact.getId()).get();
		try {
			if (!file.isEmpty()) {
				// delete old image
				File deletFileFile = new ClassPathResource("static/image").getFile();
				File file2 = new File(deletFileFile, oldContatc.getImage());
				file2.delete();
				// update new image
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContatc.getImage());
			}
			User user = this.userrRepositry.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepositry.save(contact);
			session.setAttribute("Message", new Message("your contact is upated", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/" + contact.getId() + "/contact";
	}
	@GetMapping("/profile")
	public String myProfile(Model model) {
		model.addAttribute("title", "profile ash boared");

		return "normal/profile";
	}

}
