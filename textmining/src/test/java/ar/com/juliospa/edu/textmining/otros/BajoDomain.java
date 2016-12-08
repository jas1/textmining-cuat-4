package ar.com.juliospa.edu.textmining.otros;

import java.util.Arrays;

public class BajoDomain {
	
	private String nombreProducto;
	private String listadoDetalle;
	private String productoURL;
	private String productUrlFile;
	private String productImgFile;
	private String productoDetalle;
	private String productoPrecio;
	private String productoMailSubject;
	private String productoImg;
	private String productoID;
	
	private String listadoDetalleExploded;
	private String productDetalleExploded;
	
	public String explodeDetail(String input){
		String[] current = input.split("/");
		
		String resu = "no se pudo: "+ input;
		if (current.length > 1) {
			resu = Arrays.asList(current).toString();
		}else{
			current = input.split("·");
			if (current.length > 1) {	
				resu = Arrays.asList(current).toString();
			}
		}

		return resu;
	}
	
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}
	public String getListadoDetalle() {
		return listadoDetalle;
	}
	public void setListadoDetalle(String listadoDetalle) {
		
		this.listadoDetalle = listadoDetalle;
		this.listadoDetalleExploded = explodeDetail(this.listadoDetalle);
	}
	public String getProductoURL() {
		return productoURL;
	}
	public void setProductoURL(String productoURL) {
		this.productoURL = productoURL;
	}
	public String getProductoDetalle() {
		return productoDetalle;
	}
	public void setProductoDetalle(String productoDetalle) {
		this.productoDetalle = productoDetalle;
		this.productDetalleExploded = explodeDetail(this.productoDetalle);
	}
	public String getProductoPrecio() {
		return productoPrecio;
	}
	public void setProductoPrecio(String productoPrecio) {
		this.productoPrecio = productoPrecio;
	}
	public String getProductoMailSubject() {
		return productoMailSubject;
	}
	public void setProductoMailSubject(String productoMailSubject) {
		this.productoMailSubject = productoMailSubject;
	}
	public String getProductoImg() {
		return productoImg;
	}
	public void setProductoImg(String productoImg) {
		this.productoImg = productoImg;
	}
	public String getProductoID() {
		return productoID;
	}
	public void setProductoID(String productoID) {
		this.productoID = productoID;
	}
	public String getProductUrlFile() {
		return productUrlFile;
	}
	public void setProductUrlFile(String productUrlFile) {
		this.productUrlFile = productUrlFile;
	}
	public String getProductImgFile() {
		return productImgFile;
	}
	public void setProductImgFile(String productImgFile) {
		this.productImgFile = productImgFile;
	}

	public String getListadoDetalleExploded() {
		return listadoDetalleExploded;
	}

	public void setListadoDetalleExploded(String listadoDetalleExploded) {
		this.listadoDetalleExploded = listadoDetalleExploded;
	}

	public String getProductDetalleExploded() {
		return productDetalleExploded;
	}

	public void setProductDetalleExploded(String productDetalleExploded) {
		this.productDetalleExploded = productDetalleExploded;
	}
}
