package mx.oiga.bots.cultura.unam.extractors;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mx.oiga.bots.utils.JsoupUtils;
import mx.oiga.extractors.model.entities.Documento;
import mx.oiga.extractors.model.entities.Extraccion;

import org.joda.time.LocalDate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CulturaUnamRepositoryExtractor {
	public static Logger logger = LoggerFactory
			.getLogger(CulturaUnamRepositoryExtractor.class);

	private String repoName = "Cultura UNAM";
	private String repoUrl = "http://www.cultura.unam.mx";
	private String logoUrl = "http://www.cultura.unam.mx/images/logo-cultura-unam.png";
	private String fileName;

	private final String FILE_NAME_EXP = "html_cultura_unam_%s_%s.json";

	private static final SimpleDateFormat FILE_DATE_PREFIX_FORMAT = new SimpleDateFormat(
			"yyyy_MM_dd");

	private LocalDate startDate;
	private LocalDate endDate;
	private ObjectMapper mapper = new ObjectMapper();

	public CulturaUnamRepositoryExtractor() {
		super();
		init();
	}

	private void init() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private Extraccion buildExtraccion() {
		Extraccion extraccion = new Extraccion();
		extraccion.setFechaExtraccion(new Date());
		extraccion.setNombreRepositorio(repoName);
		extraccion.setUrlRepositorio(repoUrl);
		extraccion.setLogoRepositorio(logoUrl);
		return extraccion;
	}

	private List<Integer> getActividadesId() {
		List<Integer> actividades = new ArrayList<Integer>();
		try {
			Document document = JsoupUtils.get(repoUrl);
			Elements select = document.select("#menuAct a");
			for (Element e : select) {
				String[] split = e.attr("href").split("/");
				if (split.length > 7) {
					actividades.add(Integer.parseInt(split[7]));
				}
			}
		} catch (IOException e) {
			logger.error("No se pudo cargar las actividades:", e);
		}
		return actividades;

	}

	public void extract() {
		List<Integer> actividades = getActividadesId();
		Set<Integer> ids = new LinkedHashSet<Integer>();
		Extraccion extraccion = buildExtraccion();
		Map<String, Documento> documentsMap = extraccion.getDocumentos();
		logger.info("Actividades ids " + actividades);
		logger.debug("Range " + startDate + " - " + endDate);
		for (LocalDate date = startDate; date.isBefore(endDate)
				|| date.isEqual(endDate); date = date.plusDays(1)) {
			try {
				ids.addAll(getIds(actividades, date));
			} catch (Exception e) {
				logger.error("No se pudo extraer el doc : ", e);
				;
			}
		}
		logger.info("Ids de eventos : " + ids);
		for (Integer id : ids) {
			Documento documento = getDocumento(id);
			if(documento != null){
				documentsMap.put(id.toString(), documento);
			}
		}

		saveExtraction(extraccion);

	}

	private void saveExtraction(Extraccion extraccion) {
		try {
			fileName = String.format(FILE_NAME_EXP,
					FILE_DATE_PREFIX_FORMAT.format(startDate.toDate()),
					FILE_DATE_PREFIX_FORMAT.format(endDate.toDate()));
			logger.info("Se extraera del repositorio \"" + repoUrl
					+ "\" y se guardara el archivo con el nombre: " + fileName);
			extraccion.setNombreArchivo(fileName);
			mapper.writeValue(new File(fileName), extraccion);
			logger.info("Se guardo el archivo correctamente.");
		} catch (IOException e) {
			throw new RuntimeException("No se pudo escribir el archivo "
					+ fileName + ", Error:" + e.getMessage());
		}
	}

	private Set<Integer> getIds(List<Integer> actividades, LocalDate date)
			throws IOException {
		Set<Integer> ids = new LinkedHashSet<Integer>();
		String d1 = date.toString("dd-MM-yyyy");

		for (Integer a : actividades) {
			String url = "http://www.cultura.unam.mx/Eventos/Consulta/0/" + d1
					+ "/" + d1 + "/0" + "/" + a + "/0/0";
			Document document = JsoupUtils.get(url);
			Elements select = document.select(".evhor a");
			for (Element e : select) {
				String[] split = e.attr("href").split("/");
				if (split.length > 3) {
					ids.add(Integer.valueOf(split[3]));
				}
			}
		}

		return ids;
	}

	public Documento getDocumento(Integer id) {
		String url = "http://www.cultura.unam.mx/Eventos/detalle/" + id;
		try {
			Document doc = JsoupUtils.get(url);
			Element e = doc.select(".detalle-cont").first();
			Documento d = new Documento();
			d.setContenido(e.html());
			d.setFuente(url);
			d.setId(id.toString());
			logger.info("Se extrajo el documento : '"
					+ e.select("div[class=evento-titulo]").text() + "'");
			return d;
		} catch (IOException e) {
			logger.info("No se pudo extraer la url  "+url);
		} catch (Exception e) {
			logger.info("No se pudo extraer la url  "+url);
		}
		return null;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
