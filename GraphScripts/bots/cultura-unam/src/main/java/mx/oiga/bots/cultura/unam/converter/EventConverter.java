package mx.oiga.bots.cultura.unam.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import mx.oiga.extractors.model.entities.Documento;
import mx.oiga.extractors.model.entities.Extraccion;

import org.oiga.model.entities.Event;
import org.oiga.model.entities.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class EventConverter {
	public static Logger logger = LoggerFactory.getLogger(EventConverter.class);
	private ObjectMapper mapper = new ObjectMapper();
	private String htmlFileName;
	private String eventFileName;
	
	
	/**
	 * @param htmlFileName
	 * @param eventFileName
	 */
	public EventConverter(String htmlFileName, String eventFileName) {
		super();
		this.htmlFileName = htmlFileName;
		this.eventFileName = eventFileName;
	}

	public abstract Event parseEvent(Documento doc);
	
	public void convert(){
		String eventFileName = getEventFileName();
		String htmlFileName = getHtmlFileName();
		if(htmlFileName == null){
			throw new RuntimeException("Falta especificar el nombre del archivo a cargar 'html_[domain]_[date].json'");
		}
		if(eventFileName == null){
			throw new RuntimeException("Falta especificar el nombre del archivo a guardar 'event_[domain]_[date].json'");
		}
		
		Extraccion extraccion;
		try {
			logger.debug("Leyendo el archivo {}",htmlFileName);
			extraccion = readHtml(htmlFileName);
		}catch (IOException e) {
			 throw new RuntimeException("No se pudo leer el archivo con el html "+htmlFileName+", Error:"+e.getMessage());
		}
		List<Event> events = parseEvents(extraccion);
		try {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(new File(eventFileName), events);
		} catch ( IOException e) {
			throw new RuntimeException("No se pudo escribir el archivo "+eventFileName+", Error:"+e.getMessage());
		}
	}
	
	private List<Event> parseEvents(Extraccion extraccion) {
		Repository repo = new Repository();
		ArrayList<Event> events = new ArrayList<Event>();
		repo.setName(extraccion.getNombreRepositorio());
		repo.setUrl(extraccion.getUrlRepositorio());
		for(Entry<String, Documento> entry:extraccion.getDocumentos().entrySet()){
			Documento d = entry.getValue();
			Event e = parseEvent(d);
			logger.debug("name:"+e.getName()+", host:"+e.getHost());
			e.setRepository(repo);
			e.setUrl(d.getFuente());
			events.add(e);
		}
		return events;
	}
	private Extraccion readHtml(String htmlFileName) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(new File(htmlFileName), Extraccion.class);
	}
	protected ObjectMapper getMapper() {
		return mapper;
	}
	public String getHtmlFileName() {
		return htmlFileName;
	}
	public void setHtmlFileName(String htmlFileName) {
		this.htmlFileName = htmlFileName;
	}
	public String getEventFileName() {
		return eventFileName;
	}
	public void setEventFileName(String eventFileName) {
		this.eventFileName = eventFileName;
	}
}
