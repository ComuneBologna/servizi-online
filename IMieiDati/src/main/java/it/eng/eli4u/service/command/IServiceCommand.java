package it.eng.eli4u.service.command;

import java.io.IOException;

import javax.portlet.ResourceRequest;
import javax.xml.bind.JAXBException;

import org.dom4j.DocumentException;

public interface IServiceCommand {
	
	public String execute(ResourceRequest req) throws IOException, JAXBException, DocumentException, Exception;

}
