package it.eng.eli4u.imieidati.utils;

import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DettaglioWrapper {
	
	private final Logger logger = LoggerFactory.getLogger(DettaglioWrapper.class);
	
	private HashMap<Long,DettaglioType> views = new HashMap<Long, DettaglioType>();
	private DettaglioType currentView = null;
	private String currentViewElements;
	private JAXBContext jaxbContext = null;
	
	public DettaglioWrapper() {
		try {
			jaxbContext = JAXBContext.newInstance(DettaglioType.class);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public HashMap<Long, DettaglioType> getViews() {
		return views;
	}
	
	public void setViews(HashMap<Long, DettaglioType> views) {
		this.views = views;
	}
	

	
    private String getMessaggioXml(DettaglioType value) throws Exception {

//    	ObjectFactory objectFactory = new ObjectFactory();
//    	JAXBElement<DettaglioType> p = objectFactory.createDettaglioFonte(value);
    	
//    	JAXBContext jaxbContext = JAXBContext.newInstance(DettaglioType.class);
    	Marshaller marshaller = jaxbContext.createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	StringWriter stringWriter = new StringWriter();
		marshaller.marshal( value,stringWriter);
		return stringWriter.toString();
    }
	

    public String getCurrentViewElements( String uriXsl) {
	  try {
		
		  String xml = getMessaggioXml(currentView);
		  ByteArrayOutputStream bos = null;
		  TransformerFactory tFactory = TransformerFactory.newInstance();
		  
//		  if (currentService == null)
//			return "";
//		  
//		  if(currentService.getServiceXsl() == null || "".equalsIgnoreCase(currentService.getServiceXsl())) {
			  Transformer transformer;
			 // if(currentView.getView()!=null && currentView.getView().size() > 1)
			//	  transformer = tFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream("/default-accordion.xsl")));
			 // else
			//	  transformer = tFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream("/prova.xsl")));
			  
			  transformer = tFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream("/" + uriXsl)));
		
			  bos = new ByteArrayOutputStream();
			  transformer.transform(new StreamSource(new ByteArrayInputStream(xml.getBytes())), new StreamResult(bos));
//		  }
//
//		  if(currentService.getServiceXsl() != null && !"".equalsIgnoreCase(currentService.getServiceXsl())) {
//			  String strFile = currentService.getServiceXsl();
//			  ByteArrayInputStream bis = new ByteArrayInputStream(strFile.getBytes());
//			  Transformer transformer = tFactory.newTransformer(new StreamSource(bis));
//			  bos = new ByteArrayOutputStream();
//			  transformer.transform(new StreamSource(new ByteArrayInputStream(xml.getBytes())), new StreamResult(bos));
//		  }
		
		  currentViewElements = new String(bos.toByteArray());
		  
	  } catch (Exception e) {
		  logger.error(e.getMessage(), e);
	  }
	  return currentViewElements;
	}

	public void setCurrentViewElements(DettaglioType dettaglio) {
		this.currentView = dettaglio;
	}
	

}
