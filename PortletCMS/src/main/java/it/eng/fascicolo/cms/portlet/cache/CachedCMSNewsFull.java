package it.eng.fascicolo.cms.portlet.cache;

import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;


@Configuration
@EnableCaching(proxyTargetClass = true)
public class CachedCMSNewsFull implements IURLReader {
	
	Logger logger = LoggerFactory.getLogger(CachedCMSNewsFull.class);
	
	private final URLReader urlReader;
	
	public CachedCMSNewsFull() {
		this.urlReader = new URLReader();
	}

	@Cacheable(value = "cmsNewsFullCache", key = "T(java.lang.String).valueOf(#urlStr)")
	@Override
	public Document readURLContent(String urlStr) throws Exception {
		return urlReader.readURLContent(urlStr);
	}

	@Cacheable(value = "cmsNewsFullCache", key = "T(java.lang.String).valueOf(#urlStr)")
	public String readJsoupURLContent(String urlStr, String iperboleBaseUrl) throws Exception {
		URL url = new URL(urlStr);
		InputStream stream = url.openStream();
		org.jsoup.nodes.Document doc = Jsoup.parse(stream, null, urlStr);
		
		Elements body = doc.select("div#content");
		//parse and replace relative iperbole url
		Elements linkElements = body.select("a");
		String relHref = null;
		if (linkElements != null) {
			for (Element link : linkElements) {
				relHref = link.attr("href");
				if (!StringUtils.isEmpty(relHref) && relHref.startsWith("/")) {
					relHref = iperboleBaseUrl.concat(relHref);
					logger.debug("PortletCMSNewsFull: relHref= " + relHref);
					link.attr("href", relHref);
					logger.debug("PortletCMSNewsFull: relHref= " + link);
				}
			}
		}
		
		return body.toString();

	}

}
