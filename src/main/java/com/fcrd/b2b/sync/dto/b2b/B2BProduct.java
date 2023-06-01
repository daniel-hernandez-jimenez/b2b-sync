package com.fcrd.b2b.sync.dto.b2b;

import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemList;

public class B2BProduct {
	
	private String externalId;
	private Float listPrice;
	
	public B2BProduct() {
		
	}
	
	public B2BProduct(B2BItemList navItem) {
		externalId = navItem.getNo();
		listPrice = navItem.getB2BSalesPrice().floatValue();
	}

	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public Float getListPrice() {
		return listPrice;
	}
	public void setListPrice(Float listPrice) {
		this.listPrice = listPrice;
	}
	
}
