package org.koreait.controller;

import org.koreait.articleManager.Container;
import org.koreait.dto.Article;
import org.koreait.dto.Member;
import org.koreait.service.ArticleService;
import org.koreait.service.MemberService;
import org.koreait.util.Util;

import java.util.List;
import java.util.Scanner;

public class ArticleController extends Controller {

    private Scanner sc;
    private String cmd;
    private List<Member> members;

    private ArticleService articleService;
    private MemberService memberService;

    private int lastArticleId = 3;


    public ArticleController(Scanner sc) {
        this.articleService = Container.articleService;
        this.memberService = Container.memberService;
        members = memberService.getMembers();
        this.sc = sc;
    }

    public void doAction(String cmd, String actionMethodName) {
        this.cmd = cmd;

        switch (actionMethodName) {
            case "write":
                doWrite();
                break;
            case "list":
                showList();
                break;
            case "detail":
                showDetail();
                break;
            case "modify":
                doModify();
                break;
            case "delete":
                doDelete();
                break;
            default:
                System.out.println("명령어 확인 (actionMethodName) 오류");
                break;
        }
    }

    private void doWrite() {
        System.out.println("==게시글 작성==");
        int id = lastArticleId + 1;
        String regDate = Util.getNow();
        String updateDate = regDate;
        System.out.print("제목 : ");
        String title = sc.nextLine();
        System.out.print("내용 : ");
        String body = sc.nextLine();

        Article article = new Article(id, regDate, updateDate, loginedMember.getId(), title, body);
        articleService.add(article);

        System.out.println(id + "번 글이 생성되었습니다");
        lastArticleId++;
    }

    private void showList() {
        System.out.println("==게시글 목록==");
        if (articleService.getSize() == 0) {
            System.out.println("아무것도 없어");
            return;
        }

        String searchKeyword = cmd.substring("article list".length()).trim();

        List<Article> forPrintArticles = articleService.getForPrintArticles(searchKeyword);


        String writerName = null;

        System.out.println("  번호   /    날짜     /    작성자   /   제목   /   내용   ");
        for (int i = forPrintArticles.size() - 1; i >= 0; i--) {
            Article article = forPrintArticles.get(i);

            for (Member member : members) {
                if (article.getMemberId() == member.getId()) {
                    writerName = member.getName();
                    break;
                }
            }

            if (Util.getNow().split(" ")[0].equals(article.getRegDate().split(" ")[0])) {
                System.out.printf("  %d   /   %s     /     %s     /   %s   /   %s  \n", article.getId(), article.getRegDate().split(" ")[1], writerName, article.getTitle(), article.getBody());
            } else {
                System.out.printf("  %d   /   %s     /     %s     /   %s   /   %s  \n", article.getId(), article.getRegDate().split(" ")[0], writerName, article.getTitle(), article.getBody());
            }

        }

    }

    private void showDetail() {
        System.out.println("==게시글 상세보기==");

        int id = Integer.parseInt(cmd.split(" ")[2]);

        Article foundArticle = articleService.getArticleById(id);

        if (foundArticle == null) {
            System.out.println("해당 게시글은 없습니다");
            return;
        }
        System.out.println("번호 : " + foundArticle.getId());
        System.out.println("작성날짜 : " + foundArticle.getRegDate());
        System.out.println("수정날짜 : " + foundArticle.getUpdateDate());
        System.out.println("작성자 : " + foundArticle.getMemberId());
        System.out.println("제목 : " + foundArticle.getTitle());
        System.out.println("내용 : " + foundArticle.getBody());
    }

    private void doDelete() {
        System.out.println("==게시글 삭제==");

        int id = Integer.parseInt(cmd.split(" ")[2]);

        Article foundArticle = articleService.getArticleById(id);

        if (foundArticle == null) {
            System.out.println("해당 게시글은 없습니다");
            return;
        }

        if (foundArticle.getMemberId() != loginedMember.getId()) {
            System.out.println("권한 없음");
            return;
        }

        articleService.remove(foundArticle);
        System.out.println(id + "번 게시글이 삭제되었습니다");
    }

    private void doModify() {
        System.out.println("==게시글 수정==");

        int id = Integer.parseInt(cmd.split(" ")[2]);

        Article foundArticle = articleService.getArticleById(id);

        if (foundArticle == null) {
            System.out.println("해당 게시글은 없습니다");
            return;
        }

        if (foundArticle.getMemberId() != loginedMember.getId()) {
            System.out.println("권한 없음");
            return;
        }

        System.out.println("기존 제목 : " + foundArticle.getTitle());
        System.out.println("기존 내용 : " + foundArticle.getBody());
        System.out.print("새 제목 : ");
        String newTitle = sc.nextLine();
        System.out.print("새 내용 : ");
        String newBody = sc.nextLine();

        foundArticle.setTitle(newTitle);
        foundArticle.setBody(newBody);
        foundArticle.setUpdateDate(Util.getNow());

        System.out.println(id + "번 게시글이 수정되었습니다");
    }


    public void makeTestData() {
        System.out.println("게시글 테스트 데이터 생성");
        articleService.add(new Article(1, "2023-12-12 12:12:12", "2023-12-12 12:12:12", 1, "제목123", "내용1"));
        articleService.add(new Article(2, Util.getNow(), Util.getNow(), 1, "제목72", "내용2"));
        articleService.add(new Article(3, Util.getNow(), Util.getNow(), 2, "제목1233", "내용3"));
    }
}
