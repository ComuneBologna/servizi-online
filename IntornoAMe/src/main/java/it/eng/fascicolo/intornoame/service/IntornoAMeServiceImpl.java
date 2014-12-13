package it.eng.fascicolo.intornoame.service;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameCategoria;
import it.eng.fascicolo.commons.jpa.model.FceIntornoameKml;
import it.eng.fascicolo.commons.jpa.model.FceIntornoameKmlCat;
import it.eng.fascicolo.intornoame.beans.CategoriaKmlBean;
import it.eng.fascicolo.intornoame.beans.KmlBean;
import it.eng.fascicolo.intornoame.dao.CategoriaDAO;
import it.eng.fascicolo.intornoame.dao.KmlCategoriaDAO;
import it.eng.fascicolo.intornoame.dao.KmlDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IntornoAMeServiceImpl implements IntornoAMeService {

	@Autowired
	private KmlDAO kmlDAO;
	@Autowired
	private CategoriaDAO categoriaDAO;
	@Autowired
	private KmlCategoriaDAO kmlCategoriaDAO;
	
	Logger logger = LoggerFactory.getLogger(IntornoAMeServiceImpl.class);

	public IntornoAMeServiceImpl() {
	}

	@Override
	public ArrayList<CategoriaKmlBean> getCategorieKmlAttiveBeans() {
		ArrayList<CategoriaKmlBean> listaBean=new ArrayList<CategoriaKmlBean>();
		List<FceIntornoameKmlCat> kmlCat = kmlCategoriaDAO.getElencoKmlAttiviSortedByCategoria();
		logger.info("NUMERO CATEGORIE==="+kmlCat.size());

		CategoriaKmlBean ckb=null;
		KmlBean kb=null;
		for(FceIntornoameKmlCat fceIntornoameKmlCat : kmlCat) {
			if (ckb==null || !ckb.getIdCategoria().equals(fceIntornoameKmlCat.getFceIntornoameCategoria().getIdcategoria().toString())) {
				ckb=new CategoriaKmlBean();
				listaBean.add(ckb);
				ckb.setIdCategoria(fceIntornoameKmlCat.getFceIntornoameCategoria().getIdcategoria().toString());
				ckb.setDescrizione(fceIntornoameKmlCat.getFceIntornoameCategoria().getDescrizione());
				kb=new KmlBean(fceIntornoameKmlCat.getFceIntornoameKml().getIdkml().toString(), fceIntornoameKmlCat.getFceIntornoameKml().getTitolo());
				ckb.getKmls().add(kb);
			} else {
				ckb.setIdCategoria(fceIntornoameKmlCat.getFceIntornoameCategoria().getIdcategoria().toString());
				ckb.setDescrizione(fceIntornoameKmlCat.getFceIntornoameCategoria().getDescrizione());
				kb=new KmlBean(fceIntornoameKmlCat.getFceIntornoameKml().getIdkml().toString(), fceIntornoameKmlCat.getFceIntornoameKml().getTitolo());
				ckb.getKmls().add(kb);
			}
		}

		return listaBean;
	}

	@Override
	public List<String> getElencoIdKmlAttivi() {
		List<String> elencoIdKml = new ArrayList<String>();
		
		List<FceIntornoameKmlCat> kmlCat = kmlCategoriaDAO.getElencoKmlAttiviSortedByCategoria();
		for(FceIntornoameKmlCat fceIntornoameKmlCat : kmlCat) {
			elencoIdKml.add(fceIntornoameKmlCat.getFceIntornoameKml().getIdkml().toString());
		}
		
		return elencoIdKml;
	}

	@Override
	public List<FceIntornoameCategoria> getElencoCategorieKmlAttiveSorted() {
		List<FceIntornoameCategoria> categorie = categoriaDAO.getElencoCategorieKmlAttiveSorted();
		for (FceIntornoameCategoria fceIntornoameCategoria : categorie) {
			//
		}
		
		return categorie;
	}

	@Override
	public FceIntornoameKml getKmlById(BigDecimal idKml) {
		FceIntornoameKml kml=kmlDAO.getKmlFile(idKml);
		if (kml!=null) {
			kml.getKmlfile();
		}
		return kml;
	}
	
	
}
