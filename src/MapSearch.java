import java.net.URLEncoder;

import org.json.JSONArray;


public class MapSearch {
	
	public String parse(String s) {
		if (s.isEmpty()) {
			return null;
		}
		
		String t = s.replaceFirst("\\)\\]\\}\\'", "");
		
		try {
			JSONArray ja_root = new JSONArray(t);
			JSONArray ja_0 = ja_root.getJSONArray(0);
			JSONArray ja_0_1 = (JSONArray) ja_0.get(1);
			JSONArray ja_0_1_0 = ja_0_1.getJSONArray(0);
			JSONArray ja_0_1_0_14 = (JSONArray) ja_0_1_0.get(14);
			return (String)ja_0_1_0_14.get(11);
			
//			if (ja_root != null) {
//				JSONArray ja_0 = ja_root.getJSONArray(0);
//				if (ja_0 != null) {
//					JSONArray ja_0_1 = (JSONArray) ja_0.get(1);
//					if (ja_0_1 != null) {
//						JSONArray ja_0_1_0 = ja_0_1.getJSONArray(0);
//						if (ja_0_1_0 != null) {
//							JSONArray ja_0_1_0_14 = (JSONArray) ja_0_1_0.get(14);
//							if (ja_0_1_0_14 != null) {
//								return (String)ja_0_1_0_14.get(11);
//							}
//						}
//					}
//				}
//			}
		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.print("\n\n" + s + "\n\n");
		}
		
		return null;
	}
	
	public String search(String q) {
		String url = "https://www.google.com/search?tbm=map&fp=1&authuser=0&hl=zh-CN&q=";
		
		try {
			url += URLEncoder.encode(q,"UTF-8");
			return parse(http_req.sendGet(url, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	double lon2x(double lon) {
		return (lon / 180.0) * 20037508.3428;
	}
	
	double lat2y(double lat) {
		return (Math.log(Math.tan(lat * Math.PI/180.0) + 1.0 / Math.cos(lat * Math.PI/180.0)) / Math.PI) * 20037508.3428;
	}
	
	double x2lon(double x) {
		return (x / 20037508.3428) * 180.0;
	}
	
	double y2lat(double y) {
		return  180 / Math.PI * (2 * Math.atan(Math.exp((y / 20037508.3428 * 180) * Math.PI / 180.0)) - Math.PI / 2);
	}
	
	String str2lonlat(String s) {
		String[]	t	= s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(" ");
		String		rt	= String.format("%.6f,%.6f", x2lon(Double.parseDouble(t[0])), y2lat(Double.parseDouble(t[1])));
		return rt;
	}
	
	public String baiduSearch(String[] q) {

		
		return null;
	}

	public HttpRequest http_req = new HttpRequest();
}
