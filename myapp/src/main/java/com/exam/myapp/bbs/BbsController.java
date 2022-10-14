package com.exam.myapp.bbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.exam.myapp.attach.AttachVo;
import com.exam.myapp.member.MemberVo;

@Controller
@RequestMapping("/bbs/")
public class BbsController {	
	@Autowired
	private BbsService bbsService;
	
	@RequestMapping(path = "list.do", method = RequestMethod.GET)
	public String list(Model model) {		
		List<BbsVo> list = bbsService.selectList();
		model.addAttribute("bbsList", list);
		return "bbs/list";
	}
	
	@RequestMapping(path = "add.do", method = RequestMethod.GET)
	public String addform(BbsVo vo) {		
		return "bbs/add";
	}
	
	@RequestMapping(path = "add.do", method = RequestMethod.POST)
	public String add(@Valid BbsVo vo, BindingResult bindingResult, HttpSession session) {
//		if(bindingResult.hasErrors()) { //검증결과 에러가 있다면
//			System.out.println("검증 실패!");
//			return "bbs/add"; //회원추가 jsp 화면을 다시 출력
//		}
		MemberVo mvo = (MemberVo) session.getAttribute("loginUser");
		vo.setBbsWriter(mvo.getMemId());
		
		int num = bbsService.insert(vo);
		return "redirect:/bbs/list.do";
	}
	
	@RequestMapping(path = "edit.do", method = RequestMethod.GET)
	public String editform(BbsVo vo, Model model) {	
		
		BbsVo mvo = bbsService.select(vo);
		model.addAttribute("bbsVo", mvo);
		
		return "bbs/edit";
	}
	
	@RequestMapping(path = "edit.do", method = RequestMethod.POST)
	public String edit(@ModelAttribute("bbsVo") @Valid BbsVo vo, BindingResult bindingResult) {
//		if(bindingResult.hasFieldErrors("bbsName") || bindingResult.hasFieldErrors("bbsPoint")) {
//			return "bbs/edit";
//		}		
		int num = bbsService.update(vo);
		return "redirect:/bbs/list.do";
	}
	
	@RequestMapping(path = "del.do", method = RequestMethod.GET)
	public String del(BbsVo vo) {			
		int num = bbsService.delete(vo);		
		return "redirect:/bbs/list.do";
	}
	
	@RequestMapping(path = "down.do", method = RequestMethod.GET)
	public void checkId(AttachVo vo, HttpServletResponse resp) {	
		AttachVo avo = bbsService.selectAttach(vo);
		
		//브라우저가 응답데이터를 다운로드하도록 컨텐트타입 설정
		resp.setContentType("application/octet-stream");
		
		File f = new File("D:/web/upload/", avo.getAttNewName());
		try {
			FileCopyUtils.copy(new FileInputStream(f), resp.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
