package com.cos.util;

import com.cos.dto.BoardVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyUtil {
	private static String naming = "MyUtil : ";
	
	//HotPost 변경유무 확인 (데이터 조회, 수정, 삭제시 호출됨)
	public static boolean getBoardChange(ArrayList<BoardVO> hotPost1, ArrayList<BoardVO> hotPost2) {
		boolean change = false;
		for(int i=0; i< hotPost1.size(); i++) {
			if(hotPost1.get(i).getNum() != hotPost2.get(i).getNum()) change = true;
			if(!hotPost1.get(i).getTitle().equals(hotPost2.get(i).getTitle())) change = true;
			if(hotPost1.get(i).getReadcount() != hotPost2.get(i).getReadcount()) change = true;
		}
		return change;
	}
	
	//쿠키 넣기
	public static String getMyCookie(HttpServletRequest request) {
		String cookieID = null;

		Cookie[] cookies = request.getCookies();

		for (Cookie c : cookies) {
			if (c.getName().equals("cookieID")) {
				// 엘레멘트 찾아서 넣어주면 됨.
				cookieID = c.getValue();
			}
		}
		System.out.println(naming+cookieID);
		return cookieID;
	}
	
	//크로스 사이트 스크립트 공격 방어
	public static String getReplace(String code) {	
		return code.replaceAll(" ", "&nbsp;").replaceAll("<", "&lt").replaceAll(">", "&gt").replaceAll("\n", "<br>");
	}
	
	//미리보기 화면을 위한 함수
	public static String preview(String content){
		
		Document doc = Jsoup.parse(content);
		Elements img_tag = doc.select("img");
		Elements iframe_tag = doc.select("iframe");
		
		String remove_content = removeTag(content);

		if(remove_content.length() == 0){
			if(img_tag.size() > 0 && iframe_tag.size() > 0){
				remove_content = "본문에 이미지와 영상만 존재합니다.";
			}else if((img_tag.size() > 0 && iframe_tag.size() == 0)){
				remove_content = "본문에 이미지만 존재합니다.";
			}else if((img_tag.size() == 0 && iframe_tag.size() > 0)){
				remove_content = "본문에 영상만 존재합니다.";
			}else{
				remove_content = "본문에 내용이 존재하지 않습니다.";
			}
		}else{
			//한줄에 영어만 하면 73줄, 한글로만 하면 50줄!!
			if(remove_content.length() > 50){
				remove_content = remove_content.substring(0, 50);	
			}	
		}	
		
		return remove_content;
	}
	
	//모든 HTML 태그 제거
	public static String removeTag(String html){
		html = html.replaceAll("&nbsp;", " ");
		return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	// 자바 http 요청
	public static String HttpCon(String address){
		try {
			URL url = new URL(address);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	//유투브 영상 걸러내기
	public static String makeYoutube(String content){
	    Document doc = Jsoup.parse(content);
	    Elements a_tag = doc.select("a");
	    
	    String iFrame="";
	    
	    System.out.println(a_tag.size());
	    for(int i=0; i< a_tag.size(); i++){
	    	if(a_tag.get(i).attr("href").contains("www.youtube.com/watch")){
		    	System.out.println("영상 링크가 존재합니다.");
		    	String href = a_tag.get(i).attr("href");
		    	String sp[] = href.split("=");
		    	String value = sp[1];
		    	iFrame = "<iframe id=\"player\" type=\"text/html\" width=\"90%\" height=\"409\" src=\"http://www.youtube.com/embed/"+value+"\" frameborder=\"0\" webkitallowfullscreen=\"\" mozallowfullscreen=\"\" allowfullscreen=\"\"></iframe>";
		    	a_tag.get(i).after(iFrame);
		    }   
	    }
	    
	    
	    System.out.println(doc);
	    return doc.toString();
	}
	
	//지도 검출기
	public static String makeNavermap(String content){
	    Document doc = Jsoup.parse(content);
	    String remove_content = removeTag(doc.toString());
	    //System.out.println(remove_content);
	    
	    if(remove_content.contains("/nmap/")) {
	    	String sp[] = remove_content.split("/nmap/");	
	    	//System.out.println(sp[1]);
	    	Elements el = doc.getElementsContainingOwnText(sp[1]);
	    	el.get(el.size() - 1).after(getNavermapHTML(sp[1]));
	    	//System.out.println(el.get(el.size() - 1));
	    }
	    
	    //System.out.println(doc.toString());
	    return doc.toString().replaceAll("/nmap/", "");
	}
	
	public static String getNavermapHTML(String addr) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div id=\"map\" style=\"width:90%;height:400px;\"></div>");
		sb.append("<script>");
		sb.append("var map = new naver.maps.Map('map');\r\n" + 
				"	      var myaddress = '"+addr+"';// 도로명 주소나 지번 주소만 가능 (건물명 불가!!!!)\r\n" + 
				"	      naver.maps.Service.geocode({address: myaddress}, function(status, response) {\r\n" + 
				"	          if (status !== naver.maps.Service.Status.OK) {\r\n" + 
				"	              return alert(myaddress + '의 검색 결과가 없거나 기타 네트워크 에러');\r\n" + 
				"	          }\r\n" + 
				"	          var result = response.result;\r\n" + 
				"	          // 검색 결과 갯수: result.total\r\n" + 
				"	          // 첫번째 결과 결과 주소: result.items[0].address\r\n" + 
				"	          // 첫번째 검색 결과 좌표: result.items[0].point.y, result.items[0].point.x\r\n" + 
				"	          var myaddr = new naver.maps.Point(result.items[0].point.x, result.items[0].point.y);\r\n" + 
				"	          map.setCenter(myaddr); // 검색된 좌표로 지도 이동\r\n" + 
				"	          // 마커 표시\r\n" + 
				"	          var marker = new naver.maps.Marker({\r\n" + 
				"	            position: myaddr,\r\n" + 
				"	            map: map\r\n" + 
				"	          });\r\n" + 
				"	          // 마커 클릭 이벤트 처리\r\n" + 
				"	          naver.maps.Event.addListener(marker, \"click\", function(e) {\r\n" + 
				"	            if (infowindow.getMap()) {\r\n" + 
				"	                infowindow.close();\r\n" + 
				"	            } else {\r\n" + 
				"	                infowindow.open(map, marker);\r\n" + 
				"	            }\r\n" + 
				"	          });\r\n" + 
				"	          // 마크 클릭시 인포윈도우 오픈\r\n" + 
				"	          var infowindow = new naver.maps.InfoWindow({\r\n" + 
				"	              content: '<h4> [네이버 개발자센터]</h4><a href=\"https://developers.naver.com\" target=\"_blank\"><img src=\"https://developers.naver.com/inc/devcenter/images/nd_img.png\"></a>'\r\n" + 
				"	          });\r\n" + 
				"	      });");
		sb.append("</script>");
		
		return sb.toString();
	}
	
	//자바 한글 검출기
    public static boolean containsHangul(String str)
    {
        for ( int i = 0 ; i < str.length( ) ; i++ )
        {
            char ch = str.charAt( i );
            UnicodeBlock unicodeBlock = UnicodeBlock.of( ch );
            if ( UnicodeBlock.HANGUL_SYLLABLES.equals( unicodeBlock ) || 
                    UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals( unicodeBlock )
                    || UnicodeBlock.HANGUL_JAMO.equals( unicodeBlock ) )
            {
                return true;
            }
        }
        return false;
    }
}
