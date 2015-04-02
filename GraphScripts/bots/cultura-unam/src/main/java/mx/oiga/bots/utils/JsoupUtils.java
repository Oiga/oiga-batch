package mx.oiga.bots.utils;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupUtils {
	public static Document post(String url, Map<String,String> params) throws IOException{
		Document doc = Jsoup.connect(url).data(params).post();
		return doc;
	}
	public static Document get(String url, Map<String,String> params) throws IOException{
		Document doc = Jsoup.connect(url).data(params).get();
		return doc;
	}
	public static Document post(String url) throws IOException{
		Document doc = Jsoup.connect(url).post();
		return doc;
	}
	public static Document get(String url) throws IOException{
		Document doc = Jsoup.connect(url).get();
		return doc;
	}
}
