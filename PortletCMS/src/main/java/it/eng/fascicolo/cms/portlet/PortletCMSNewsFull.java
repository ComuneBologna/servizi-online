
package it.eng.fascicolo.cms.portlet;

import it.eng.fascicolo.cms.portlet.cache.CachedCMSNewsFull;
import it.eng.fascicolo.cms.portlet.context.SpringApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.liferay.portal.util.PortalUtil;

/***
 * Portlet to display a full article from iperbole.
 * 
 * @author Eyal Gross
 */


public class PortletCMSNewsFull extends com.liferay.util.bridges.mvc.MVCPortlet {

	Logger logger = LoggerFactory.getLogger(PortletCMSNewsFull.class);
	protected String viewJsp_ = "/newsFull.jsp";

	@Autowired
	CachedCMSNewsFull newsFullUrlReader;
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException, PortletSecurityException, IOException {

		logger.debug("PortletCMSNewsFull: doView IN");
		
		try {
			// Avoid autowiring problems, if any
			if (newsFullUrlReader == null) {
				newsFullUrlReader = SpringApplicationContext.getContext().getBeansOfType(CachedCMSNewsFull.class).values().iterator().next();
			}
			setCSS(renderRequest);
			setBody(renderRequest);
			include(viewJsp_, renderRequest, renderResponse);
			logger.debug("PortletCMSNewsFull: doView IN");
		}
		catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			logger.debug("PortletCMSNewsFull: doView IN");
		}

	}

	public CachedCMSNewsFull getNewsFullUrlReader() {
		return newsFullUrlReader;
	}

	public void setNewsFullUrlReader(CachedCMSNewsFull newsFullUrlReader) {
		this.newsFullUrlReader = newsFullUrlReader;
	}

	/***
	 * Method to get the content from the passed in url parameter and add it to
	 * the request to be displayed.
	 * 
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setBody(RenderRequest renderRequest)
		throws Exception {

		logger.debug("PortletCMSNewsFull: setBody IN");

		Properties prop = new Properties();
		prop.load(PortletCMSNewsFront.class.getClassLoader().getResourceAsStream("portlet.properties"));

		// Get parameters from request
		String paramUrl = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)).getParameter("url");
		URL parameterUrl = new URL(paramUrl);
		String paramHost = parameterUrl.getHost();
		logger.info("NewsFull -- paramHost: " + paramHost);
		
		String iperboleBaseUrl = prop.getProperty("iperboleBaseUrl");
		URL iperboleUrl = new URL(iperboleBaseUrl);
		String iperboleHost = iperboleUrl.getHost();
		logger.info("NewsFull -- iperboleHost: " + iperboleHost);
		
		// Validate that base URL of passed in url is valid (is iperbole) and
		// retrieve and display the HTML
		if (paramHost != null && paramHost.equalsIgnoreCase(iperboleHost)) {
			logger.info("NewsFull -- paramUrl: " + paramUrl);
			/*
			URL url = new URL(paramUrl);
			InputStream stream = url.openStream();
			org.jsoup.nodes.Document doc = Jsoup.parse(stream, null, paramUrl);
			*/
//			org.jsoup.nodes.Document doc = 
			
			//Document doc = newsFullUrlReader.readURLContent(paramUrl);
			
			//String bodyStr = new String();
			//NodeList nl = doc.getElementsByTagName("html");
			//bodyStr = nl.item(0).getTextContent();
			//org.jsoup.nodes.Document docjsoup = Jsoup.parse(bodyStr);
			
//			Elements body = doc.select("div#content");
//			//parse and replace relative iperbole url
//			Elements linkElements = body.select("a");
//			String relHref = null;
//			if (linkElements != null) {
//				for (Element link : linkElements) {
//					relHref = link.attr("href");
//					if (!StringUtils.isEmpty(relHref) && relHref.startsWith("/")) {
//						relHref = iperboleBaseUrl.concat(relHref);
//						logger.debug("PortletCMSNewsFull: relHref= " + relHref);
//						link.attr("href", relHref);
//						logger.debug("PortletCMSNewsFull: relHref= " + link);
//					}
//				}
//			}

			String bodyStr = newsFullUrlReader.readJsoupURLContent(paramUrl, iperboleBaseUrl);

			logger.debug("PortletCMSNewsFull: body String = " + bodyStr);
			renderRequest.setAttribute("body", bodyStr);
		}
		else {
			renderRequest.setAttribute("body", "Parametro url non valido.");
		}

		logger.debug("PortletCMSNewsFull: setBody OUT");
	}

	/***
	 * Method to get the css and add it to the request to be inserted in the
	 * head.
	 * 
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setCSS(RenderRequest renderRequest)
		throws Exception {

		Properties prop = new Properties();
		prop.load(PortletCMSNewsFull.class.getClassLoader().getResourceAsStream("portlet.properties"));
		
		/*
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URL url = new URL(prop.getProperty("baseCssUrl") + "?css=all&html=true");
		InputStream stream = url.openStream();
		Document doc = db.parse(stream);
		*/
		String fullUrlString = prop.getProperty("baseCssUrl") + "?html=true";
		Document doc = newsFullUrlReader.readURLContent(fullUrlString);
		
		String cssStr = doc.getDocumentElement().getTextContent();
		cssStr = cssStr.replace("\n", "").replace("\r", "");
		cssStr = cssStr.replaceAll("<style", "<style id=\"portlet-cms-style\"");
		renderRequest.setAttribute("drupalCSS", cssStr);
	}

	private org.jsoup.nodes.Document parse(String baseUrl, String resource)
		throws IOException {

		String html = null;
		if (resource != null) {
			html = getResponseBody(baseUrl + resource);
		}
		else {
			html = getResponseBody(baseUrl);
		}

		if (html != null)
			return Jsoup.parse(html);
		else
			return null;
	}

	private String getResponseBody(String url)
		throws IOException {

		String responseBody = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {

					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						if (entity != null && entity.getContentType().getValue().contains("text/html")) {
							return EntityUtils.toString(entity);
						}
						else
							return null;
					}
					else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}
			};

			responseBody = httpclient.execute(httpget, responseHandler);
		}
		catch (ClientProtocolException e) {
			logger.error(e.getMessage(), e);
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			httpclient.close();
		}
		return responseBody;
	}
}
