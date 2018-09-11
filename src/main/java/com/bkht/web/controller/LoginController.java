package com.bkht.web.controller;

import com.bkht.shiro.ShiroUser;
import com.bkht.system.entity.User;
import com.bkht.system.service.UserService;
import com.bkht.system.utils.Sex;
import com.bkht.system.utils.Status;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public String login() {

		if (!SecurityUtils.getSubject().isAuthenticated()) {
			return "login";
		}
		return "redirect:/";
	}

	/*@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName, Model model) {
		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
		return "login";
	}*/

	@RequestMapping(value = "/login",method = RequestMethod.POST)
	public String loginUser(String username,String password,HttpSession session) {
        /*User u = new User();
        u.setLoginName("admin");
        u.setStatus(Status.ENABLE);
        u.setSex(Sex.F);
        try {
            userService.saveOrUpdate(u);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(username,password);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(usernamePasswordToken);   //完成登录
			ShiroUser user1=(ShiroUser) subject.getPrincipal();
			session.setAttribute("user", user1);
			return "redirect:/";
		} catch(Exception e) {
			return "login";//返回登录页面
		}

	}
	@RequestMapping("/validateCode")
	public ResponseEntity<byte[]> validateCode(HttpSession session) throws IOException {
		/*HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String validateCode = ValidateCodeUtils.getCode(200, 60, 5, outputStream).toLowerCase();
		session.setAttribute(ValidateCodeAuthenticationFilter.DEFAULT_VALIDATE_CODE_PARAM,validateCode);
		byte[] bs = outputStream.toByteArray();
		outputStream.close();
		return new ResponseEntity<byte[]>(bs,headers, HttpStatus.OK);*/
		return null;
	}
}
