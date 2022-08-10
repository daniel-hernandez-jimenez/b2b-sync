package com.fcrd.b2b.sync.dto.b2b;

import java.math.BigDecimal;

import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderLine;

public class B2BOrderLine {
	
	protected String externalKey;
	protected String externalType;
	protected String externalNo;
//	protected String externalSalesOrderNo;
	protected String description;
	protected BigDecimal quantity;
	protected BigDecimal unitPrice;
	protected BigDecimal externalQtyToShip;
	protected BigDecimal externalQuantityShipped;
	protected BigDecimal externalQtyToInvoice;
	protected BigDecimal externalQuantityInvoiced;
	protected Integer externalLineNo;
	
	public B2BOrderLine() {
		
	}
	public B2BOrderLine(B2BSalesOrderLine navSalesOrderLine) {
		externalKey = navSalesOrderLine.getKey();
		externalType = navSalesOrderLine.getType().toString();
		externalNo = navSalesOrderLine.getNo();
//		externalSalesOrderNo = salesOrderNo;
		description = navSalesOrderLine.getDescription();
		quantity = navSalesOrderLine.getQuantity();
		unitPrice = navSalesOrderLine.getUnitPrice();
		externalQtyToShip = navSalesOrderLine.getQtyToShip();
		externalQuantityShipped = navSalesOrderLine.getQuantityShipped();
		externalQtyToInvoice = navSalesOrderLine.getQtyToInvoice();
		externalQuantityInvoiced = navSalesOrderLine.getQtyToInvoice();
		externalLineNo = navSalesOrderLine.getLineNo();
	}
	
	public String getExternalKey() {
		return externalKey;
	}
	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}
	public String getExternalType() {
		return externalType;
	}
	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}
	public String getExternalNo() {
		return externalNo;
	}
	public void setExternalNo(String externalNo) {
		this.externalNo = externalNo;
	}
//	public String getExternalSalesOrderNo() {
//		return externalSalesOrderNo;
//	}
//	public void setExternalSalesOrderNo(String externalSalesOrderNo) {
//		this.externalSalesOrderNo = externalSalesOrderNo;
//	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getExternalQtyToShip() {
		return externalQtyToShip;
	}
	public void setExternalQtyToShip(BigDecimal externalQtyToShip) {
		this.externalQtyToShip = externalQtyToShip;
	}
	public BigDecimal getExternalQuantityShipped() {
		return externalQuantityShipped;
	}
	public void setExternalQuantityShipped(BigDecimal externalQuantityShipped) {
		this.externalQuantityShipped = externalQuantityShipped;
	}
	public BigDecimal getExternalQtyToInvoice() {
		return externalQtyToInvoice;
	}
	public void setExternalQtyToInvoice(BigDecimal externalQtyToInvoice) {
		this.externalQtyToInvoice = externalQtyToInvoice;
	}
	public BigDecimal getExternalQuantityInvoiced() {
		return externalQuantityInvoiced;
	}
	public void setExternalQuantityInvoiced(BigDecimal externalQuantityInvoiced) {
		this.externalQuantityInvoiced = externalQuantityInvoiced;
	}
	public Integer getExternalLineNo() {
		return externalLineNo;
	}
	public void setExternalLineNo(Integer externalLineNo) {
		this.externalLineNo = externalLineNo;
	}
	
}
