/**
 * 
 */
package mx.oiga.bots.cultura.unam;


import mx.oiga.bots.cultura.unam.converter.CulturaUnamEventConverter;

/**
 * @author jaime.renato
 *
 */
public class CulturaUnamBot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* Extraccion */
//		CulturaUnamRepositoryExtractor extractor = new CulturaUnamRepositoryExtractor();
//		extractor.setStartDate(LocalDate.now());
//		extractor.setEndDate(LocalDate.now().dayOfMonth().withMaximumValue());
		//extractor.setEndDate(LocalDate.now());
		//extractor.extract();
		
		/*Event converter*/
		CulturaUnamEventConverter converter = new CulturaUnamEventConverter(
				"html_cultura_unam_2015_03_152015_03_31.json", 
				"event_cultura_unam.json");
		converter.convert();
	}

}
