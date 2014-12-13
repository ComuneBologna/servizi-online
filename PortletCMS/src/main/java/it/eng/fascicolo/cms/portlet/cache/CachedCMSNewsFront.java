package it.eng.fascicolo.cms.portlet.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;

@Configuration
@EnableCaching(proxyTargetClass = true)
public class CachedCMSNewsFront implements IURLReader {
	
	private final URLReader urlReader;
	
	public CachedCMSNewsFront() {
		this.urlReader = new URLReader();
	}

	@Cacheable(value = "cmsNewsFrontCache", key = "T(java.lang.String).valueOf(#urlStr)")
	@Override
	public Document readURLContent(String urlStr) throws Exception {
		return urlReader.readURLContent(urlStr);
	}

}
