package com.fin.proj.board.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fin.proj.board.model.exception.BoardException;
import com.fin.proj.board.model.service.BoardService;
import com.fin.proj.board.model.vo.Board;
import com.fin.proj.board.model.vo.Reply;
import com.fin.proj.common.Pagination;
import com.fin.proj.common.model.vo.PageInfo;
import com.fin.proj.member.model.vo.Member;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService bService;

	@GetMapping("faqMain.bo")
	public String faqMain(@RequestParam(value="page", required=false) Integer currentPage, Model model) {
		
		if(currentPage == null) {
			currentPage = 1;
		}
		
		int listCount = bService.getListCount(6);
//		System.out.println(listCount);
		
		PageInfo pageInfo= Pagination.getPageInfo(currentPage, listCount, 10);
		
		ArrayList<Board> list = bService.selectBoardList(pageInfo, 6);
//		System.out.println(list);
		
		if(list != null) {
			model.addAttribute("pi", pageInfo);
			model.addAttribute("list", list);
			return "faq";
		} else {
			throw new BoardException("게시글 목록 조회 실패");
		}
	}
	
	@GetMapping("faq_detail.bo")
	public String faqDetail(@RequestParam("bNo") int bNo, @RequestParam("writer") String writer,
							@RequestParam("page") int page, HttpSession session, Model model) {
//		System.out.println(bNo + ", " + writer + ", "+ page);
		// 상세보기의 경우 조회수 +1, but 내 글 클릭 시 조회수 +0 (로그인 유저 정보 가져와서 비교)
		// 파라미터로 받은 page를 통해 목록으로 돌아갔을 시 원래 보던 페이지 노출
		
		Member m = (Member)session.getAttribute("loginUser");
//		System.out.println(m);
		
		String readerNickName = null;
		if(m != null) {
			readerNickName = m.getuNickName();
		}
		
		boolean countYN = false;
		if(!writer.equals(readerNickName)) {
			countYN = true;
		}
		
		Board board = bService.selectBoard(bNo, countYN);
//		System.out.println(board);
		
		ArrayList<Reply> replyList = bService.selectReply(bNo);
//		System.out.println(replyList);
		
		if(board != null) {
			model.addAttribute("board", board);
			model.addAttribute("page", page);
//			model.addAttribute("replyList", replyList);
			return "faq_Detail";
		} else {
			throw new BoardException("게시글 상세 조회 실패");
		}
	}
	
	@GetMapping("faq_form.bo")
	public String faqForm() {
		return "faq_form";
	}
	
	@GetMapping("finePeopleMain.bo")
	public String finePeopleMain() {
		return "finePeople";
	}
	
	@GetMapping("finePeople_form.bo")
	public String finePeopleForm() {
		return "finePeople_form";
	}
	
	@GetMapping("fruitMain.bo")
	public String fruitMain(@RequestParam(value="page", required=false) Integer currentPage, Model model) {
		
		if(currentPage == null) {
			currentPage = 1;
		}
		
		int listCount = bService.getListCount(5);
//		System.out.println(listCount);
		
		PageInfo pageInfo= Pagination.getPageInfo(currentPage, listCount, 10);
		
		ArrayList<Board> list = bService.selectBoardList(pageInfo, 5);
//		System.out.println(list);
		
		if(list != null) {
			model.addAttribute("pi", pageInfo);
			model.addAttribute("list", list);
			return "fruit";
		} else {
			throw new BoardException("게시글 목록 조회 실패");
		}
	}
	
	@GetMapping("fruit_form.bo")
	public String fruitForm() {
		return "fruit_form";
	}
	
	@GetMapping("fruit_detail.bo")
	public String fruitDetail(@RequestParam("bNo") int bNo, @RequestParam("writer") int writer,
							  @RequestParam("page") int page, HttpSession session, Model model) {
		
		System.out.println(writer);
		Member m = (Member)session.getAttribute("loginUser");
//		System.out.println(m);
		
		boolean countYN = false;
		if(writer == 1) {
			countYN = true;
		}
		
		Board board = bService.selectBoard(bNo, countYN);
		System.out.println(board);
		
		ArrayList<Reply> replyList = bService.selectReply(bNo);
//		System.out.println(replyList);
		
		if(board != null) {
			model.addAttribute("board", board);
			model.addAttribute("page", page);
			model.addAttribute("replyList", replyList);
			return "fruit_detail";
		} else {
			throw new BoardException("게시글 상세 조회 실패");
		}
	}
	
	@GetMapping("fineNewsMain.bo")
	public String fineNewsMain() {
		return "fineNews";
	}
	
	@GetMapping("fineNews_form.bo")
	public String fineNewsForm() {
		return "fineNews_form";
	}
	
	@GetMapping("commList.bo")
		public String CommMain(@RequestParam(value="page", required=false) Integer currentPage, Model model) {
		
		if(currentPage == null) {
			currentPage = 1;
		}
		
		int listCount = bService.getListCount(1);
		
		PageInfo pageInfo= Pagination.getPageInfo(currentPage, listCount, 10);
		
		ArrayList<Board> list = bService.selectBoardList(pageInfo, 1);
		
		if(list != null) {
			model.addAttribute("pi", pageInfo);
			model.addAttribute("list", list);
			return "commList";
		} else {
			throw new BoardException("게시글 목록 조회 실패");
		}
	}
	
	@GetMapping("writeComm.bo")
	public String writeComm() {
		return "writeComm";
	}
	
	@GetMapping("commDetailPage.bo")
	public String CommDetail(@RequestParam("bNo") int bNo, @RequestParam("writer") String writer,
							@RequestParam("page") int page, HttpSession session, Model model) {
		Member m = (Member)session.getAttribute("loginUser");
		
		String readerNickName = null;
		if(m != null) {
			readerNickName = m.getuNickName();
		}
		
		boolean countYN = false;
		if(!writer.equals(readerNickName)) {
			countYN = true;
		}
		
		Board board = bService.selectBoard(bNo, countYN);
		
		ArrayList<Reply> replyList = bService.selectReply(bNo);
		System.out.println(replyList);
		
		if(board != null) {
			model.addAttribute("board", board);
			model.addAttribute("page", page);
//			model.addAttribute("replyList", replyList);
			return "commDetail";
		} else {
			throw new BoardException("게시글 상세 조회 실패");
		}
	}
	
	@PostMapping("insertBoard.bo")
	public String insertCommBoard(@ModelAttribute Board b, HttpSession session) {
		String id = ((Member)session.getAttribute("loginUser")).getuId();
		b.setuId(id);
		b.setBoardType(1);
		
		int result = bService.insertBoard(b);
		if(result > 0) {
			return "redirect:commList.bo";
			
		} else {
			throw new BoardException("게시글 작성 실패 ㅠ");
		}
	}
	
	@RequestMapping("updateForm.bo")
	public String updateForm(@RequestParam("bNo") int boardNo, @RequestParam("page") int page, Model model) {
		Board board = bService.selectBoard(boardNo, false);
		model.addAttribute("board", board);
		model.addAttribute("page", page);
		return "editComm";
	}
	
	@PostMapping("updateBoard.bo")
	public String updateCommBoard(@ModelAttribute Board b, @RequestParam("page") int page, Model model, HttpSession session) {
		
		b.setBoardType(1);
		int result = bService.updateBoard(b);
		System.out.println(result);
		
		if(result > 0) {
			model.addAttribute("bNo", b.getBoardNo());
			model.addAttribute("writer", ((Member)session.getAttribute("loginUser")).getuId());
			model.addAttribute("page", page);
			return "redirect:selectBoard.bo";
			
		} else {
			throw new BoardException("게시글 수정을 실패하였습니다.");
		}
	}
	
	@RequestMapping("delete.bo")
	public String deleteCommBoard(@RequestParam("bId") String encode) {
		
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(encode);
		String decode = new String(byteArr);
		int bId = Integer.parseInt(decode);
		
		int result = bService.deleteBoard(bId);
		if(result > 0) {
			return "redirect:list.bo";
		} else {
			throw new BoardException("게시글 삭제 실패했습니다유ㅠ");
		}
	}
	
	
	@GetMapping("noticeList.bo")
	public String noticeList() {
		return "noticeList";
	}
	
	@GetMapping("writeNotice.bo")
	public String writeNotice() {
		return "writeNotice";
	}
	
	@GetMapping("qaList.bo")
	public String qaList() {
		return "qaList";
	}
	
	@GetMapping("writeQa.bo")
	public String writeQa() {
		return "writeQa";
	}
	
	@GetMapping("commDetail.bo")
	public String commDetail() {
		return "commDetail";
	}
	
	@GetMapping("qaDetail.bo")
	public String qaDetail() {
		return "qaDetail";
	}
	
	@GetMapping("replyQa.bo")
	public String replyQa() {
		return "replyQa";
	}
	
	@GetMapping("editComm.bo")
	public String editComm() {
		return "editComm";
	}
	
	@GetMapping("editQa.bo")
	public String editQa() {
		return "editQa";
	}
	
	@GetMapping("editNotice.bo")
	public String editNotice() {
		return "editNotice";
	}
	
	
	// my page
	@GetMapping("myBoard.bo")
	public String myBoard() {
		return "myBoard";
	}
	
	@GetMapping("myReply.bo")
	public String myReply() {
		return "myReply";
	}
	
	// 댓글
	@RequestMapping("insertReply.bo")
	public void insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
		bService.insertReply(r);
		ArrayList<Reply> list = bService.selectReply(r.getBoardNo());
		
		response.setContentType("application/json; charset=UTF-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
		try {
			gson.toJson(list, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		} 
	}
}
