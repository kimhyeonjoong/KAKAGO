package gmail.hotjoong.servlet;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@WebServlet("/message")
public class message extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String clientId = "VAnpRlgjVkCDUBi5mBH6";//애플리케이션 클라이언트 아이디값";
    private static String clientSecret = "_PWdbBOgJx";//애플리케이션 클라이언트 시크릿값";
    private static String a,b,c;
    
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		StringBuffer jb = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null){ 
			jb.append(line);
		}
		
		String jsonString = jb.toString();
		String text ="";
		Object obj = JSONValue.parse(jsonString);
		JSONObject json = (JSONObject)obj;
		if(json != null){
			text = json.get("content").toString();
		}
		//text = "私は知らなかった";
		text = "안녕";
		//text = "你好";
		//text = "NekoネコNECO";
		if(CheckLanguage(text).equals("k")) {
			request.setAttribute("en_reply", caseKorean(text));
		}else {
			try {
				switch (CheckLanguage(text)) {
				case "e": //영어를 한국어로 번역 
					request.setAttribute("en_reply", papagoAPI2(text, "en"));
					break;
				case "j": //일본어를 한국어로 번역
					request.setAttribute("en_reply", papagoAPI2(text, "ja"));
					break;
				case "c": //중국어를 한국어로 변역
					request.setAttribute("en_reply", papagoAPI2(text, "zh-CN"));
					break;
				case "error": //한국어, 영어, 일본어, 중국어 모두 아닐때 출력
					request.setAttribute("en_reply", 
							papagoAPI2("Sorry. Please check the sentence to be translated again.", "en"));
					break;
				}
			} catch (Exception e) {
	            System.out.println(e);
	        }
		}
       
		RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp"); 
		dispatcher.forward(request, response);
		
	}
	
	
	public static String CheckLanguage(String str){
		char check;
		
		for(int i = 0; i<str.length(); i++){
			check = str.charAt(i);
			if((44032 < check && check < 55203) || (4352 < check && check < 4607) || (12593 < check && check < 12687))
			{
				//char값이 한글일 경우 
				return "k";
			}
			if(65 < check && check < 122)
			{
				//char값이 영어일 경우 
				return "e";
			}
			if((12352 < check && check < 12543) || (12784 < check && check < 12799))
			{
				//char값이 일본어일 경우 
				return "j";
			}
			if((11904 < check && check < 12031) || (13312 < check && check < 19903) || (19968 < check && check < 40895) || (63744 < check && check < 64255) || (131072 < check && check < 173791) || (194560 < check && check < 195103))
			{
				for(int j=i; j<str.length(); j++) {
					check = str.charAt(j);
					if((12352 < check && check < 12543) || (12784 < check && check < 12799))
					{
						//char값이 일본어일 경우 
						return "j";
						
					}
				}
				return "c";
			}
			
		}		
		return "error";
	}
	
	public static String caseKorean(String text) {
		String result4 ="";
		try {
			result4 += papagoAPI(text, "en");
			result4 += papagoAPI(text, "ja");
			result4 += papagoAPI(text, "zh-CN");
			
        } catch (Exception e) {
            System.out.println(e);
        }
		return result4;
	}
	
	public static String papagoAPI(String text, String target) throws Exception{
		String result4 = "";
		
		text = URLEncoder.encode(text, "UTF-8");
        String apiURL = "https://openapi.naver.com/v1/language/translate";
        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("X-Naver-Client-Id", clientId);
        con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
        // post request
        String postParams = "source=ko&target="+target+"&text=" + text;
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        BufferedReader br;
        if(responseCode==200) { // 정상 호출
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();
        
        
        String result ="";
        
        String text2 = response.toString();
		Object obj2 = JSONValue.parse(text2);
		JSONObject json2 = (JSONObject)obj2;
		if (json2 != null) {
			result = json2.get("message").toString();
		}
		String text3 = result;
		Object obj3 = JSONValue.parse(text3);
		JSONObject json3 = (JSONObject)obj3;
		
		String result3 ="";
		if (json3 != null) {
			result3 = json3.get("result").toString();
		}
		String text4 = result3;
		Object obj4 = JSONValue.parse(text4);
		JSONObject json4 = (JSONObject)obj4;
		
		if (json4 != null) {
			System.out.println(json4.get("translatedText").toString());
			result4 += json4.get("translatedText").toString() + "\\n" + "\\n";
		}
		return result4;
		
	}
	public static String papagoAPI2(String text, String source) throws Exception{
		String result4 = "";
		
		text = URLEncoder.encode(text, "UTF-8");
        String apiURL = "https://openapi.naver.com/v1/language/translate";
        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("X-Naver-Client-Id", clientId);
        con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
        // post request
        String postParams = "source="+source+"&target=ko&text=" + text;
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        BufferedReader br;
        if(responseCode==200) { // 정상 호출
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();
        
        
        String result ="";
        
        String text2 = response.toString();
		Object obj2 = JSONValue.parse(text2);
		JSONObject json2 = (JSONObject)obj2;
		if (json2 != null) {
			result = json2.get("message").toString();
		}
		String text3 = result;
		Object obj3 = JSONValue.parse(text3);
		JSONObject json3 = (JSONObject)obj3;
		
		String result3 ="";
		if (json3 != null) {
			result3 = json3.get("result").toString();
		}
		String text4 = result3;
		Object obj4 = JSONValue.parse(text4);
		JSONObject json4 = (JSONObject)obj4;
		
		
		if (json4 != null) {
			System.out.println(json4.get("translatedText").toString());
			result4 = json4.get("translatedText").toString();
		}
		return result4;
		
	}
}
