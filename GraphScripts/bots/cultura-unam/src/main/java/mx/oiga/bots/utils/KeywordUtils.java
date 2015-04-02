package mx.oiga.bots.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;
import org.oiga.model.entities.Tag;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class KeywordUtils {
	private static Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_36);
	
	public static List<Tag> extractKeywords(String text){
		List<Tag> tokens = tokenizeString(analyzer, text);
		return tokens;
	}
	public static List<Tag> extractKeywords(String text, int threshold){
		List<Tag> tokens = extractKeywords(text);
		tokens = applyThreshold(tokens, threshold);
		return tokens;
	}
	
	public static List<Tag> applyThreshold(List<Tag> tags, int threshold){
		List<Tag> newTags = new ArrayList<Tag>();
		for(Tag t:tags){
			if(t.getCount()>=threshold){
				newTags.add(t);
			}
		}
		return newTags;
	}
	public static void addTag(List<Tag> tags, String keyword){
		for(Tag t:tags){
			if(t.getKeyword().equals(keyword)){
				t.setCount(t.getCount() + 1);
				return;
			}
		}
		Tag t = new Tag();
		t.setKeyword(keyword);
		t.setCount(1);
		tags.add(t);
		
	}
	
	private static String tokenizeWord(String string){
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(
					string));
			
			stream.reset();
			stream.incrementToken();
			CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
			String token = term.toString();
			return token;	
			}catch(IOException ioe){
				return "";
			}
	}
	
	private static List<Tag> tokenizeString(Analyzer analyzer, String string) {
		List<Tag> tags = new ArrayList<Tag>(); 
		Multimap<String, String> tokens = ArrayListMultimap.create();
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(
					string));
			
			stream.reset();
			while (stream.incrementToken()) {
				//String v = stream.reflectAsString(true);
				//System.out.println(v);
				CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
				OffsetAttribute offsetAtt	= stream.addAttribute(OffsetAttribute.class);
				String word = string.substring(offsetAtt.startOffset(),offsetAtt.endOffset());
				String token = term.toString();
				tokens.put(token, word);
			}
			stream.close();
			for(String token:tokens.keySet()){
				Collection<String> words = tokens.get(token);
				Tag tag = new Tag();
				int count = words.size();
				String keyword="";
				if(words.contains(token)){
					keyword = token;
				}else if(words.iterator().hasNext()){
					keyword = words.iterator().next().toLowerCase();
				}
				tag.setCount(count);
				tag.setKeyword(keyword);
				tags.add(tag);
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tags;
	}

}
