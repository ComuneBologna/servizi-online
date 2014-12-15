package it.eng.fascicolo.commons.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageMixUtil {

	static Logger logger = LoggerFactory.getLogger(ImageMixUtil.class);
	/*
	@Autowired
	@Qualifier("configPropeties")
	private static Properties configPropeties;
	*/
	//TODO Sostituire con properties
	private static final int LOGO_RESIZE = 50;
	
	public static  byte[]  mixImage(byte[] fotoUtente, byte[] logoEBologna){
		if (fotoUtente != null && logoEBologna!= null){
			InputStream streamFotoUtente = null;
			InputStream streamLogoEBologna = null;
			try{
				boolean fotoUtenteIsBase64 = Base64.isBase64(fotoUtente);
				
				if (fotoUtenteIsBase64) {
					streamFotoUtente = new ByteArrayInputStream(Base64.decodeBase64(fotoUtente));
				} else {
					streamFotoUtente = new ByteArrayInputStream(fotoUtente);
				}
				boolean logoEBolognaIsBase64 = Base64.isBase64(logoEBologna);
				
				if (logoEBolognaIsBase64) {
					streamLogoEBologna = new ByteArrayInputStream(Base64.decodeBase64(logoEBologna));
				} else {
					streamFotoUtente = new ByteArrayInputStream(fotoUtente);
				}

				// load source images
				BufferedImage foto = ImageIO.read(streamFotoUtente);
				BufferedImage logo = ImageIO.read(streamLogoEBologna);
				if (foto != null) {
					try {
						//TODO configProperties l'injection non va
						//if (configPropeties.getProperty("ebologna.image.mixed.pixel.size") != null) {
						//int LOGO_RESIZE = Integer.parseInt(configPropeties.getProperty("ebologna.image.mixed.pixel.size"));
						if (LOGO_RESIZE>0 && logo != null) {
							int type = logo.getType() == 0? BufferedImage.TYPE_INT_ARGB : logo.getType();
							BufferedImage logoResized = resizeImageWithHint(logo, type, LOGO_RESIZE, LOGO_RESIZE);
							if (logoResized != null) {
								logo = logoResized;
							}
							//logo = resizeImage(logo, type, LOGO_RESIZE, LOGO_RESIZE);
						}
						//} else {
						//	logger.debug("properties ebologna.image.mixed.pixel.size non trovata");
						//}
					} catch (Exception e) {
						logger.error("resize logo in errore",e);
					}
					// create the new image, canvas size is the max. of both image sizes
					int w = Math.max(foto.getWidth(), logo.getWidth());
					int h = Math.max(foto.getHeight(), logo.getHeight());
					
					int wMerged = (foto.getWidth()+1)-logo.getWidth();
					int hMerged = (foto.getHeight()+1)-logo.getHeight();
					BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					// paint both images, preserving the alpha channels
					Graphics g = combined.getGraphics();
					g.drawImage(foto, 0, 0, null);
					g.drawImage(logo, wMerged, hMerged, null);
					byte[] imgByte = null;
					try {
						File img = File.createTempFile("avatarMixed", ".png");
						ImageIO.write(combined, "PNG", img);
						imgByte = read(img);
						img.delete();
					} catch (IOException ioe) {
						logger.error("non è stato possibile mixare le immagini",ioe);
					}
					logger.debug("imgByte: "+imgByte);
					return imgByte;
				} else {
					return null;
				}
			} catch (Exception e) {
				logger.error("eccezione nel mix delle immagini ",e);
			} finally {
				
				try {
					streamFotoUtente.close();
				} catch (Exception e) {
					
				}
				try {
					streamLogoEBologna.close();
				} catch (Exception e) {
					
				}
			}
		} else {
			logger.error("Tentativo di mixare immagini null");
		}
		return null;
		//combined.
	}
	
	public static byte[] read(File file) throws IOException {

	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    try {
	        byte[] buffer = new byte[4096];
	        ous = new ByteArrayOutputStream();
	        ios = new FileInputStream(file);
	        int read = 0;
	        while ( (read = ios.read(buffer)) != -1 ) {
	            ous.write(buffer, 0, read);
	        }
	    } finally { 
	        try {
	             if ( ous != null ) 
	                 ous.close();
	        } catch ( IOException e) {
	        }

	        try {
	             if ( ios != null ) 
	                  ios.close();
	        } catch ( IOException e) {
	        }
	    }
	    return ous.toByteArray();
	}
	
	public static BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height){

		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
	 
		return resizedImage;
	}
	 
	public static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int width,  int height){
	    
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);
	 
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	 
		return resizedImage;
	}
	
}