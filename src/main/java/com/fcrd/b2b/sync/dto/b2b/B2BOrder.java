package com.fcrd.b2b.sync.dto.b2b;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrder;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderLine;

public class B2BOrder {

	protected String externalKey;
	protected String externalNo;
	protected String externalCustomerNo;
	protected String externalCustomerName;
	protected String address;
	protected String address2;
	protected String city;
	protected String county;
	protected String postCode;
	protected String phoneNo;
	protected Date orderDate;
	protected String externalQuoteNo;
	protected String externalDocumentNo;
	protected String externalSalespersonCode;
	protected String externalAssignedUserID;
	protected String externalStatus;
	protected String billToCustomerNo;
	protected String billToName;
	protected String billToAddress;
	protected String billToAddress2;
	protected String billToCity;
	protected String billToCounty;
	protected String billToPostCode;
	protected String billToPhoneNo;
	protected String externalPaymentTermsCode;
	protected String externalShipToAddressType;
	protected String shipToName;
	protected String shipToAddress;
	protected String shipToAddress2;
	protected String shipToCity;
	protected String shipToCounty;
	protected String shipToPostCode;
	protected String shipToContact;
	protected String shipToPhoneNo;
	protected String externalPrepmtPaymentTermsCode;
	protected List<B2BOrderLine> orderLineList;
	
	public B2BOrder() {
		
	}
	public B2BOrder(B2BSalesOrder navSalesOrder) {
		externalKey = navSalesOrder.getKey();
		externalNo = navSalesOrder.getNo();
		externalCustomerNo = navSalesOrder.getSellToCustomerNo();
		externalCustomerName = navSalesOrder.getSellToCustomerName();
		address = navSalesOrder.getSellToAddress();
		address2 = navSalesOrder.getSellToAddress2();
		city = navSalesOrder.getSellToCity();
		county = navSalesOrder.getSellToCounty();
		postCode = navSalesOrder.getSellToPostCode();
		phoneNo = navSalesOrder.getSellToPhoneNo();
		orderDate = navSalesOrder.getOrderDate().toGregorianCalendar().getTime();
		externalQuoteNo = navSalesOrder.getQuoteNo();
		externalDocumentNo = navSalesOrder.getExternalDocumentNo();
		externalSalespersonCode = navSalesOrder.getSalespersonCode();
		externalAssignedUserID = navSalesOrder.getAssignedUserID();
		externalStatus = navSalesOrder.getStatus().toString();
		billToCustomerNo = navSalesOrder.getBillToCustomerNo();
		billToName = navSalesOrder.getBillToName();
		billToAddress = navSalesOrder.getBillToAddress();
		billToAddress2 = navSalesOrder.getBillToAddress2();
		billToCity = navSalesOrder.getBillToCity();
		billToCounty = navSalesOrder.getBillToCounty();
		billToPostCode = navSalesOrder.getBillToPostCode();
		billToPhoneNo = navSalesOrder.getBillToPhoneNo();
		externalPaymentTermsCode = navSalesOrder.getPaymentTermsCode();
		externalShipToAddressType = navSalesOrder.getShipToAddressType().toString();
		shipToName = navSalesOrder.getShipToName();
		shipToAddress = navSalesOrder.getShipToAddress();
		shipToAddress2 = navSalesOrder.getShipToAddress2();
		shipToCity = navSalesOrder.getShipToCity();
		shipToCounty = navSalesOrder.getShipToCounty();
		shipToPostCode = navSalesOrder.getShipToPostCode();
		shipToContact = navSalesOrder.getShipToContact();
		shipToPhoneNo = navSalesOrder.getShipToPhoneNo();
		externalPrepmtPaymentTermsCode = navSalesOrder.getPrepmtPaymentTermsCode();
		
		List<B2BOrderLine> b2bOrderLineList = new ArrayList<>();
		for (B2BSalesOrderLine navSalesOrderLine : navSalesOrder.getSalesLines().getB2BSalesOrderLine()) {
			b2bOrderLineList.add(new B2BOrderLine(navSalesOrderLine));
		}
	}

	public String getExternalKey() {
		return externalKey;
	}
	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}
	public String getExternalNo() {
		return externalNo;
	}
	public void setExternalNo(String externalNo) {
		this.externalNo = externalNo;
	}
	public String getExternalCustomerNo() {
		return externalCustomerNo;
	}
	public void setExternalCustomerNo(String externalCustomerNo) {
		this.externalCustomerNo = externalCustomerNo;
	}
	public String getExternalCustomerName() {
		return externalCustomerName;
	}
	public void setExternalCustomerName(String externalCustomerName) {
		this.externalCustomerName = externalCustomerName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getExternalQuoteNo() {
		return externalQuoteNo;
	}
	public void setExternalQuoteNo(String externalQuoteNo) {
		this.externalQuoteNo = externalQuoteNo;
	}
	public String getExternalDocumentNo() {
		return externalDocumentNo;
	}
	public void setExternalDocumentNo(String externalDocumentNo) {
		this.externalDocumentNo = externalDocumentNo;
	}
	public String getExternalSalespersonCode() {
		return externalSalespersonCode;
	}
	public void setExternalSalespersonCode(String externalSalespersonCode) {
		this.externalSalespersonCode = externalSalespersonCode;
	}
	public String getExternalAssignedUserID() {
		return externalAssignedUserID;
	}
	public void setExternalAssignedUserID(String externalAssignedUserID) {
		this.externalAssignedUserID = externalAssignedUserID;
	}
	public String getExternalStatus() {
		return externalStatus;
	}
	public void setExternalStatus(String externalStatus) {
		this.externalStatus = externalStatus;
	}
	public String getBillToCustomerNo() {
		return billToCustomerNo;
	}
	public void setBillToCustomerNo(String billToCustomerNo) {
		this.billToCustomerNo = billToCustomerNo;
	}
	public String getBillToName() {
		return billToName;
	}
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	public String getBillToAddress() {
		return billToAddress;
	}
	public void setBillToAddress(String billToAddress) {
		this.billToAddress = billToAddress;
	}
	public String getBillToAddress2() {
		return billToAddress2;
	}
	public void setBillToAddress2(String billToAddress2) {
		this.billToAddress2 = billToAddress2;
	}
	public String getBillToCity() {
		return billToCity;
	}
	public void setBillToCity(String billToCity) {
		this.billToCity = billToCity;
	}
	public String getBillToCounty() {
		return billToCounty;
	}
	public void setBillToCounty(String billToCounty) {
		this.billToCounty = billToCounty;
	}
	public String getBillToPostCode() {
		return billToPostCode;
	}
	public void setBillToPostCode(String billToPostCode) {
		this.billToPostCode = billToPostCode;
	}
	public String getBillToPhoneNo() {
		return billToPhoneNo;
	}
	public void setBillToPhoneNo(String billToPhoneNo) {
		this.billToPhoneNo = billToPhoneNo;
	}
	public String getExternalPaymentTermsCode() {
		return externalPaymentTermsCode;
	}
	public void setExternalPaymentTermsCode(String externalPaymentTermsCode) {
		this.externalPaymentTermsCode = externalPaymentTermsCode;
	}
	public String getExternalShipToAddressType() {
		return externalShipToAddressType;
	}
	public void setExternalShipToAddressType(String externalShipToAddressType) {
		this.externalShipToAddressType = externalShipToAddressType;
	}
	public String getShipToName() {
		return shipToName;
	}
	public void setShipToName(String shipToName) {
		this.shipToName = shipToName;
	}
	public String getShipToAddress() {
		return shipToAddress;
	}
	public void setShipToAddress(String shipToAddress) {
		this.shipToAddress = shipToAddress;
	}
	public String getShipToAddress2() {
		return shipToAddress2;
	}
	public void setShipToAddress2(String shipToAddress2) {
		this.shipToAddress2 = shipToAddress2;
	}
	public String getShipToCity() {
		return shipToCity;
	}
	public void setShipToCity(String shipToCity) {
		this.shipToCity = shipToCity;
	}
	public String getShipToCounty() {
		return shipToCounty;
	}
	public void setShipToCounty(String shipToCounty) {
		this.shipToCounty = shipToCounty;
	}
	public String getShipToPostCode() {
		return shipToPostCode;
	}
	public void setShipToPostCode(String shipToPostCode) {
		this.shipToPostCode = shipToPostCode;
	}
	public String getShipToContact() {
		return shipToContact;
	}
	public void setShipToContact(String shipToContact) {
		this.shipToContact = shipToContact;
	}
	public String getShipToPhoneNo() {
		return shipToPhoneNo;
	}
	public void setShipToPhoneNo(String shipToPhoneNo) {
		this.shipToPhoneNo = shipToPhoneNo;
	}
	public String getExternalPrepmtPaymentTermsCode() {
		return externalPrepmtPaymentTermsCode;
	}
	public void setExternalPrepmtPaymentTermsCode(String externalPrepmtPaymentTermsCode) {
		this.externalPrepmtPaymentTermsCode = externalPrepmtPaymentTermsCode;
	}
	public List<B2BOrderLine> getOrderLineList() {
		return orderLineList;
	}
	public void setOrderLineList(List<B2BOrderLine> orderLineList) {
		this.orderLineList = orderLineList;
	}

}
