package mx.oiga.bots.cultura.unam.converter;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.oiga.bots.cultura.unam.parsers.CulturaUnamDateParser;
import mx.oiga.extractors.model.entities.Documento;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.oiga.model.entities.Event;
import org.oiga.model.entities.EventCategory;
import org.oiga.model.entities.Tag;

import scala.MatchError;
import scala.collection.mutable.StringBuilder;

public class CulturaUnamEventConverter extends EventConverter{
		
	/**
	 * @param htmlFileName
	 * @param eventFileName
	 */
	public CulturaUnamEventConverter(String htmlFileName,
			String eventFileName) {
		super(htmlFileName, eventFileName);
	}

	@Override
	public Event parseEvent(Documento doc){
		Event e = new Event();
		EventCategory ec = new EventCategory();
		List<EventCategory> categories = new ArrayList<EventCategory>();
		Set<Tag> tags = new HashSet<Tag>();
		Document dom = Jsoup.parse(doc.getContenido());
		
		dom.select(".evmas").remove();
		
		Elements paragraphs = dom.getElementsByTag("p");
		String categoryName = dom.select("#MainContent_lblGenero").text();
		String titulo = dom.select("#MainContent_lblNombre").text();
		String host = dom.select("#MainContent_lblOrganizador").text();
		//FIXME:Usar Xpath para extraer el contenido
		String descripcion =  getContentFor(paragraphs.outerHtml(), "MainContent_lblDescripcionCorta");
		String descripcionCorta = getContentFor(paragraphs.outerHtml(), "MainContent_lblDescripcionLarga");
		String recinto = dom.select("#MainContent_lblRecinto").text();
		String recintoDir = dom.select("#MainContent_lblUbicacion").text();
		String horarios = dom.select("#MainContent_lblHorarios").text();
		String precios = dom.select("#MainContent_lblDesSeccionPrecio").text();
		String img = dom.select("#MainContent_divFotos img").attr("src");
		String date = dom.select("#MainContent_lblFechasRango , #lblFechas").text();
		String tipoDeEvento = dom.select("#MainContent_lblActividad").text();
		
		Date[] fechas = CulturaUnamDateParser.parse(date);
		tags.add(new Tag(tipoDeEvento.replaceAll(" ", ""), 3));
		
		ec.setName(categoryName);
		e.setName(titulo);
		e.setExternalId(doc.getId());
		e.setHost(host);
		e.setDescription(descripcionCorta + descripcion);
		e.getCategories().addAll(categories);
		e.setLocation(recinto);
		e.setLocationAdress(recintoDir);
		e.setHoursDetails(horarios);
		e.setAudience("GENERAL");
		e.setTicketPrices(precios);
		e.setPicture(img);
		e.setStartDate(fechas[0]);
		e.setEndDate(fechas[1]);
	
		return e;
	}
	
	private String getContentFor(Elements paragraphs, String id){
		StringBuilder builder = new StringBuilder(); 
		for (Element e : paragraphs) {
			Elements select = e.select(id);
			if(!select.isEmpty()){
				for(Element p :e.siblingElements()){
					builder.append(p.text()+"<br>");
					if(!p.hasText()){
						break;
					}
				}
			}
		}
		logger.info("contenido extraido: "+builder.toString());
		return builder.toString();
	}
	
	/**
	 * Tiene muchos bugs
	 * @param text
	 * @param id
	 * @return
	 */
	@Deprecated
	private String getContentFor(String text, String id){
		logger.info(text);
		Pattern pmatcher  = Pattern.compile(id+"\">\\s*</span>\\s*</p>\\s*(.*)<p>\\s*</p>"); 
		 
		StringBuilder builder = new StringBuilder(); 
		Matcher match = pmatcher.matcher(text.replace("\n", "").replace("\r", ""));
		if(match.find()){
			builder.append(match.group(1));
		}
		logger.info("contenido extraido: '"+builder.toString()+"'");
		return builder.toString();
	}
	
}
