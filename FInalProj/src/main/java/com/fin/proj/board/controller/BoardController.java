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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		
		int listCount = bService.getListCount("자주 묻는 질문");
//		System.out.println(listCount);
		
		PageInfo pageInfo= Pagination.getPageInfo(currentPage, listCount, 10);
		
		ArrayList<Board> list = bService.selectBoardList(pageInfo, "자주 묻는 질문");
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
		
//		ArrayList<Reply> replyList = bService.selectReply(bNo);
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
	
	@PostMapping("insert_faq.bo")
	public String insertFaq(@ModelAttribute Board b, HttpSession session) {
		
		int uNo = ((Member)session.getAttribute("loginUser")).getuNo();
		b.setuNo(uNo);
		b.setBoardType("자주 묻는 질문");
		b.setImageUrl(null);
		b.setNewsURL(null);
		b.setFpName(null);
		
		int result = bService.insertBoard(b);
		
		if(result > 0) {
			return "redirect:faqMain.bo";
		} else {
			throw new BoardException("게시글 작성 실패");
		}
	}
	
	@GetMapping("faq_edit.bo")
	public String faqEdit() {
		return "faq_edit";
	}
	
	@GetMapping("finePeopleMain.bo")
	public String finePeopleMain(@RequestParam(value="page", required=false) Integer currentPage, Model model) {
		
		if(currentPage == null) {
			currentPage = 1;
		}
		
		int listCount = bService.getListCount("선뜻한 사람");
//		System.out.println(listCount);
		
		PageInfo pageInfo= Pagination.getPageInfo(currentPage, listCount, 5);
		
		ArrayList<Board> list = bService.selectBoardList(pageInfo, "선뜻한 사람");
//		System.out.println(list);
		
		if(list != null) {
			model.addAttribute("pi", pageInfo);
			model.addAttribute("list", list);
			return "finePeople";
		} else {
			throw new BoardException("게시글 목록 조회 실패");
		}
	}
	
	@GetMapping("finePeople_form.bo")
	public String finePeopleForm() {
		return "finePeople_form";
	}
	
	@PostMapping("insertFinePeople.bo")
	public String insertFinePeople(@ModelAttribute Board b, HttpSession session) {
		
		int uNo = ((Member)session.getAttribute("loginUser")).getuNo();
		b.setuNo(uNo);
		b.setBoardType("선뜻한 사람");
		
		int result = bService.insertBoard(b);
		
		if(result > 0) {
			return "redirect:finePeopleMain.bo";
		} else {
			throw new BoardException("게시물 작성 실패");
		}
	}
	
	@GetMapping("fruitMain.bo")
	public String fruitMain(@RequestParam(value="page", required=false) Integer currentPage, Model model) {
		
		if(currentPage == null) {
			currentPage = 1;
		}
		
		int listCount = bService.getListCount("결실");
//		System.out.println(listCount);
		
		PageInfo pageInfo= Pagination.getPageInfo(currentPage, listCount, 10);
		
		ArrayList<Board> list = bService.selectBoardList(pageInfo, "결실");
//		System.out.println(list);
		
		if(list != null) {
			model.addAttribute("pi", pageInfo);
			model.addAttribute("list", list);
			return "fruit";
		} else {
			throw new BoardException("게시글 목록 조회 실패");
		}
	}
	
	@GetMapping("fruit_detail.bo")
	public String fruitDetail(@RequestParam("bNo") int bNo, @RequestParam("page") int page,
							  HttpSession session, Model model) {
		
		Member m = (Member)session.getAttribute("loginUser");
		System.out.println(m);
		
		boolean countYN = false;
		if(m == null || m.getIsAdmin() == 1) {
			countYN = true;
		}
		
		Board board = bService.selectBoard(bNo, countYN);
//		System.out.println(board);
		
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
	
	@GetMapping("fruit_form.bo")
	public String fruitForm() {
		return "fruit_form";
	}
	
	@PostMapping("insert_fruit.bo")
	public String insertFruit(@ModelAttribute Board b, HttpSession session) {
		
		System.out.println(b);
		int uNo = ((Member)session.getAttribute("loginUser")).getuNo();
		b.setuNo(uNo);
		b.setBoardType("결실");
		
		int result = bService.insertFruit(b);
		
		if(result > 0) {
			return "redirect:fruitMain.bo";
		} else {
			throw new BoardException("게시글 작성 실패");
		}
	}
	
	@GetMapping("fruit_edit.bo")
	public String fruitEdit() {
		return "fruit_edit"; 
	}
	
	@GetMapping("fineNewsMain.bo")
	public String fineNewsMain() {
		return "fineNews";
	}
	
	@GetMapping("fineNews_form.bo")
	public String fineNewsForm() {
		return "fineNews_form";
	}
	
	// 댓글
	@RequestMapping("insertReply.bo")
	public void insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
		
		System.out.println(r);
		bService.insertReply(r);
		
		ArrayList<Reply> list = bService.selectReply(r.getBoardNo());
		response.setContentType("application/json; charset=UTF-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd / HH:mm:ss").create();
		try {
			gson.toJson(list, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		} 
	}
	
	@RequestMapping("deleteReply.bo")
	public String deleteReply(@RequestParam("rNo") int replyNo,
							  @RequestParam("bNo") int boardNo,
							  @RequestParam("page") int page,
							  RedirectAttributes ra) {
		
		System.out.println(replyNo);
		System.out.println(boardNo);
		
		int result = bService.deleteReply(replyNo);

		if(result > 0) {
			ra.addAttribute("bNo", boardNo);
			ra.addAttribute("page", page);
			return "redirect:fruit_detail.bo";
		} else {
			throw new BoardException("댓글 삭제에 실패하였습니다.");
		}
		
		// replyNo 이용해 해당 댓글 삭제한 후,
		// boardNo 받아와서 selectReply 한 후 해당 디테일 페이지 띄워주기
		// RedirectAttribute 이용!
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
			System.out.println(list);
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
//		System.out.println(replyList);
		
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
		int id = ((Member)session.getAttribute("loginUser")).getuNo();
		System.out.println("id=" + id);
		b.setuNo(id);
		System.out.println("들어간 id" + id);
//		b.setBoardType(1);
		
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
	public String updateCommBoard(@ModelAttribute Board b, @RequestParam("page") int page, RedirectAttributes ra, HttpSession session) {
		
//		b.setBoardType(1);
		int result = bService.updateBoard(b);
//		System.out.println(result);
		
		if(result > 0) {
			ra.addAttribute("bNo", b.getBoardNo());
			ra.addAttribute("writer", ((Member)session.getAttribute("loginUser")).getuId());
			ra.addAttribute("page", page);
			return "redirect:commDetailPage.bo";
			
		} else {
			throw new BoardException("게시글 수정을 실패하였습니다.");
		}
	}
	
	@RequestMapping("Commdelete.bo")
	public String deleteCommBoard(@RequestParam("bId") String encode) {
		
		Decoder decoder = Base64.getDecoder();
		byte[] byteArr = decoder.decode(encode);
		String decode = new String(byteArr);
		int bId = Integer.parseInt(decode);
		
		int result = bService.deleteBoard(bId);
		if(result > 0) {
			return "redirect:commList.bo";
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
	
}
