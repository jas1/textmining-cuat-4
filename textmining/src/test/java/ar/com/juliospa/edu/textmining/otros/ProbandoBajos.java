package ar.com.juliospa.edu.textmining.otros;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProbandoBajos {
	
	private Logger log = LoggerFactory.getLogger(ProbandoBajos.class);
	static {
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yy-MM-dd HH:mm:ss.SSS");
	}
	
	private Gson gson = new Gson();
	private  Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void probandoBajosDB() {

		
//		listado | detalle > producto
		try {
			String dboxFolder = "C:/Users/julio/Dropbox/julio_box/educacion/maestria_explotacion_datos_uba/materias/cuat_4_text_mining/material/tp2/promusica_bajos20161208/www.promusica.com.ar/";
			String filteToSearch = dboxFolder+"resultado_busqueda.php_cmbCategoria=22&cmbMarca=26&cmbPaginado=100";
			File input = new File(filteToSearch);
//			String encoding = "UTF-8";
			String encoding = "ISO-8859-1";
			Document doc = Jsoup.parse(input, encoding, "http://example.com/");

			// https://jsoup.org/cookbook/extracting-data/selector-syntax
			// http://stackoverflow.com/questions/6152671/jsoup-select-div-having-multiple-classes
			Elements detalleListado = doc.select("div.detalle");// los h2. con class="detalle
			
			// para cada detalle encontrado
			// de estos saco el href del producto y el mini detalle y la imagen
			List<BajoDomain> listado = new ArrayList<>();
			
			for (Element detalle : detalleListado) {
				BajoDomain current = new BajoDomain();

				String hrefProducto = detalle.select("a").get(0).attr("href");
				String prodId = hrefProducto.substring(hrefProducto.indexOf("=")+1);
				String miniDetalle = detalle.select("p").get(0).text();
				current.setProductoURL(hrefProducto);
				current.setProductoID(prodId);
				current.setListadoDetalle(miniDetalle);
				
				// buscando detalle del producto
				String productUrlFile = dboxFolder + "producto.php_ID="+current.getProductoID();
				current.setProductUrlFile(productUrlFile);
				File prodFile = new File(productUrlFile);
				Document prodDoc = Jsoup.parse(prodFile, encoding, "http://example.com/");
				
				Elements detalleProd = prodDoc.select("div.detalle");
				// busco nombre 
				String producto = detalleProd.select("h1").text();
				// busco imagen
				String imgUrl = detalleProd.select("a").attr("href");
				String imgUrlFile = dboxFolder + imgUrl;
				// busco detalle
				String productoDetalleStr = detalleProd.select("p").text();
				// busco precio
				String precio =prodDoc.select("div.especif-celeste").text();
				
				current.setNombreProducto(producto);
				current.setProductoImg(imgUrl);
				current.setProductImgFile(imgUrlFile);
				current.setProductoDetalle(productoDetalleStr);
				current.setProductoPrecio(precio);
								
				listado.add(current);
			}
			
			
			List<BajoDomain>  listadoBajos = listado.stream().filter(res -> res.getNombreProducto().contains("Bajo")).collect(Collectors.toList());
			
			listadoBajos.stream().forEach(ea->log.info(ea.getProductoPrecio()));
			
			
			String jsonOut = gsonPretty.toJson(listadoBajos);
			
//			log.info(jsonOut);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
