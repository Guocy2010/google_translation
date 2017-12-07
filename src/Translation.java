import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;


public class Translation {
	
	public Translation() {
		http_req = new HttpRequest();
		str_tkk = null;
		g = new int[32];
	}
	
	public ArrayList<String> strRegex(String str_src, String str_reg) {
		ArrayList<String> str_list = new ArrayList<String>();
		
		Pattern p = Pattern.compile(str_reg);
		Matcher m = p.matcher(str_src);
		while (m.find()) {
			str_list.add(m.group(1));
		}
		
		return str_list;
	}
	
	public void getTKK() {
		String s = http_req.sendGet("https://translate.google.cn/", null);
		ArrayList<String> ab = strRegex(s, "x3d(-?\\d+);");
		ArrayList<String> c = strRegex(s, ";return (-?\\d+)\\+");

		long a = Long.parseLong(ab.get(0));
		long b = Long.parseLong(ab.get(1));
		
		str_tkk = c.get(0) + "." + (a + b);
	}
	
	public String parse(String s) {
		if (s.isEmpty()) {
			return null;
		}
		JSONArray ja_root = new JSONArray(s);
		JSONArray ja_0 = ja_root.getJSONArray(0);
		JSONArray ja_0_0 = ja_0.getJSONArray(0);
		return ja_0_0.getString(0);
	}
	
	public String translate(String sl, String tl, String q) {
		String tk = tk(q);
		String url = "https://translate.google.cn/translate_a/single?client=t&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&ssel=3&tsel=3&kc=0";
		try {
			url += "&sl=" + sl + "&tl=" + tl + "&tk=" + tk + "&q=" + URLEncoder.encode(q,"UTF-8");
			return parse(http_req.sendGet(url, null));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int b(int a, String b) {
		for (int d = 0; d < b.length() - 2; d += 3) {
			int c = b.charAt(d + 2);
			c = 'a' <= c ? c - 87 : c - '0';
			c = '+' == b.charAt(d + 1) ? a >>> c : a << c;
			a = '+' == b.charAt(d) ? a + c & 0xFFFFFFFF : a ^ c;
		}
		return a;
	}
	
	public String tk(String a) {
		String[] e = str_tkk.split("\\.");
		long h = 0;
		int d = 0;
		
		try {
			h = Long.parseLong(e[0]);
		} catch (NumberFormatException e2) {
			h = 0;
		}
		
		if (g.length < a.length() * 4) {
			g = new int[a.length() * 4];
		}
		
		for (int f = 0; f < a.length(); f++) {
			int c = a.charAt(f);
			
			if (128 > c) {
				g[d++] = c;
			} else {
				if (2048 > c) {
					g[d++] = c >> 6 | 192;
				} else {
					if (55296 == (c & 64512) && f + 1 < a.length() && 56320 == (a.charAt(f + 1) & 64512)) {
						c = 65536 + ((c & 1023) << 10) + (a.charAt(++f) & 1023);
						g[d++] = c >> 18 | 240;
						g[d++] = c >> 12 & 63 | 128;
					} else {
						g[d++] = c >> 12 | 224;
					}
					g[d++] = c >> 6 & 63 | 128;
				}
				g[d++] = c & 63 | 128;
			}
		}
		
		int t_a = (int)h;
		for (int i = 0; i < d; i++) {
			t_a += g[i];
			t_a = b(t_a, "+-a^+6");
		}
		
		t_a = b(t_a, "+-3^+b+-f");
		
		long t_a64 = 1;
		if (0 > t_a) {
			t_a64 <<= 31;
			t_a64 += t_a & 0x7FFFFFFF;
		} else {
			t_a64 = t_a;
		}
	
		try {
			t_a64 ^= Long.parseLong(e[1]);
		} catch (NumberFormatException e1) {
			t_a64 ^= 0;
		}
		
		t_a64 %= 1000000;
		
		return Long.toString(t_a64) + "." + Long.toString(t_a64 ^ h);
	}
	
	public HttpRequest http_req;
	public String str_tkk;
	public int[] g;

}
