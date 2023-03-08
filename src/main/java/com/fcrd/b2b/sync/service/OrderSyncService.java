package com.fcrd.b2b.sync.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrder;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderLine;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.BillTo;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.ObjectFactory;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.Root;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.Sellto;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.ShipTo;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.TempSalesHeader;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.TempSalesLines;
import com.fcrd.b2b.api.client.sync.model.AddressData;
import com.fcrd.b2b.api.client.sync.model.ExternalPaymentTermsCode;
import com.fcrd.b2b.api.client.sync.model.ExternalPrepmtPaymentTermsCode;
import com.fcrd.b2b.api.client.sync.model.ExternalShipToAddressType;
import com.fcrd.b2b.api.client.sync.model.Order;
import com.fcrd.b2b.api.client.sync.model.OrderExternalData;
import com.fcrd.b2b.api.client.sync.model.OrderItem;
import com.fcrd.b2b.api.client.sync.model.OrderItemExternalData;
import com.fcrd.b2b.api.client.sync.model.SyncResult;
import com.fcrd.b2b.api.client.sync.model.SyncStatus;
import com.fcrd.b2b.sync.config.SyncSchedulingConfigurer;
import com.fcrd.b2b.sync.service.b2b.B2BOrdersService;
import com.fcrd.b2b.sync.service.nav.NavOrdersService;
import com.fcrd.b2b.sync.utils.DateTimeUtils;

@Service
public class OrderSyncService extends SyncSchedulingConfigurer {
	private static Logger logger = LoggerFactory.getLogger(OrderSyncService.class);
	
	@Value("${order.sync.initialDelay}")
	protected String orderSyncInitialDelay;
	
	@Value("${order.sync.scheduleDelay}")
	protected String orderSyncScheduleDelay;
    
    @Value("${nav.order.no.prefix}")
    protected String navOrderNoPrefix;
	
	@Autowired
	B2BOrdersService b2bOrdersService;
	
	@Autowired
	NavOrdersService navOrdersService;
	
	protected ObjectFactory objectFactory;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("Initializing OrderSyncService");
		
		objectFactory = new ObjectFactory();
		
		setInitialDelay(orderSyncInitialDelay);
		setScheduleDelay(orderSyncScheduleDelay);
	}
	
	protected void doit() {
		syncFromNavToB2BWithLastModifiedDate(false);
		syncFromB2BToNav();
	}
	
	
	
	// ---------------------------------------------
	// Sales Orders changed in NAV (only B2B Orders)
	// ---------------------------------------------
	
	public void syncFromNavToB2BWithLastModifiedDate(boolean retrying) {
		logger.info("Starting Orders sync from Nav to B2B");
		try {
			String lastExternalModifiedDateTime = b2bOrdersService.getLastExternalModifiedDateTime();
			
			List<B2BSalesOrder> navSalesOrderList = navOrdersService.getSalesOrdersWithChanges(lastExternalModifiedDateTime);
			
			if (navSalesOrderList.size() == 0) {
				logger.info("No NAV Orders data to sync");
			}
			else {
				logger.info("There are " + navSalesOrderList.size() + " Dynamics Nav SalesOrders to sync");
				
				Collections.sort(navSalesOrderList, (o1, o2) -> (o1.getLastModifiedDateTime().compare(o2.getLastModifiedDateTime())));
				
				logger.info("The " + navSalesOrderList.size() + " Order records were sorted!");
				
				List<Order> orderList = new ArrayList<>();
				for (B2BSalesOrder navSalesOrder : navSalesOrderList) {
					orderList.add(generateOrderFromB2BSalesOrder(navSalesOrder));
				}
				
				b2bOrdersService.mergeOrders(orderList);
			}
		}
		catch (B2BUnauthorizedException e) {
			logger.error("Error synchronizing Orders.", e);
			if (!retrying) {
				retryAfterCreateAPISession();
			}
		}
		catch (Exception e) {
			logger.error("Error synchronizing Orders.", e);
		}
	}
	
	private void retryAfterCreateAPISession() {
		try {
			b2bOrdersService.createAPISession();
			syncFromNavToB2BWithLastModifiedDate(true);
		}
		catch (Exception e) {
			logger.error("Error retrying synchronizing Orders after create API Session.", e);
		}
	}
	
	private Order generateOrderFromB2BSalesOrder(B2BSalesOrder navSalesOrder) throws Exception {
		Order order = new Order();
		
		order.setId(Long.parseLong(navSalesOrder.getNo().substring(6)));
		order.setCreationDate(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navSalesOrder.getOrderDate()));
		order.setBillingCustomerAddress(generateBillingCustomerAddress(navSalesOrder));
		order.setShippingCustomerAddress(generateShippingCustomerAddress(navSalesOrder));
		order.setExternalData(generateExternalData(navSalesOrder));
		order.setItemAmount(null);
		order.setItemTaxAmount(null);
		order.setOrderItems(generateOrderItemList(navSalesOrder.getSalesLines().getB2BSalesOrderLine()));
		order.setShippingAmount(null);
		order.setShippingTaxAmount(null);
		order.setTotalAmount(null);
		order.setTotalTaxAmount(null);
		
		return order;
	}
	
	private AddressData generateBillingCustomerAddress(B2BSalesOrder navSalesOrder) {
		AddressData billingAddressData = new AddressData();
		
		billingAddressData.setCity(navSalesOrder.getBillToCity());
		billingAddressData.setCountryCode(null);
		billingAddressData.setName(navSalesOrder.getBillToName());
		billingAddressData.setPhone(navSalesOrder.getBillToPhoneNo());
		billingAddressData.setStateCode(null);
		billingAddressData.setStreetLine1(navSalesOrder.getBillToAddress());
		billingAddressData.setStreetLine2(navSalesOrder.getBillToAddress2());
		billingAddressData.setZip(navSalesOrder.getBillToPostCode());
		
		return billingAddressData;
	}
	
	private AddressData generateShippingCustomerAddress(B2BSalesOrder navSalesOrder) {
		AddressData shippingAddressData = new AddressData();
		
		shippingAddressData.setCity(navSalesOrder.getShipToCity());
		shippingAddressData.setCountryCode(null);
		shippingAddressData.setName(navSalesOrder.getShipToName());
		shippingAddressData.setPhone(navSalesOrder.getShipToPhoneNo());
		shippingAddressData.setStateCode(null);
		shippingAddressData.setStreetLine1(navSalesOrder.getShipToAddress());
		shippingAddressData.setStreetLine2(navSalesOrder.getShipToAddress2());
		shippingAddressData.setZip(navSalesOrder.getShipToPostCode());
		
		return shippingAddressData;
	}
	
	private OrderExternalData generateExternalData(B2BSalesOrder navSalesOrder) throws Exception {
		OrderExternalData externalData = new OrderExternalData();
		
		externalData.setId(navSalesOrder.getNo());
		externalData.setKey(navSalesOrder.getKey());
		externalData.setAssignedUserId(navSalesOrder.getAssignedUserID());
		externalData.setCustomerName(navSalesOrder.getSellToCustomerName());
		externalData.setCustomerId(navSalesOrder.getSellToCustomerNo());
		externalData.setExternalDocumentNo(navSalesOrder.getExternalDocumentNo());
		externalData.setLastModifiedDateTime(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navSalesOrder.getLastModifiedDateTime()));
		externalData.setPaymentTermsCode(ExternalPaymentTermsCode.fromValue(navSalesOrder.getPaymentTermsCode()));
		try {
		    externalData.setPrepmtPaymentTermsCode(ExternalPrepmtPaymentTermsCode.fromValue(navSalesOrder.getPrepmtPaymentTermsCode()));
		} catch (Exception e) { }
		externalData.setQuoteNo(navSalesOrder.getQuoteNo());
		externalData.setSalespersonCode(navSalesOrder.getSalespersonCode());
		externalData.setShipToAddressType(ExternalShipToAddressType.fromValue(navSalesOrder.getShipToAddressType().toString()));
		externalData.setStatus(navSalesOrder.getStatus().toString());
		
		return externalData;
	}
	
	private List<OrderItem> generateOrderItemList(List<B2BSalesOrderLine> navSalesOrderLineList) {
		List<OrderItem> orderItemList = new ArrayList<>();
		
		for (B2BSalesOrderLine navSalesOrderLine : navSalesOrderLineList) {
			orderItemList.add(generateOrderItem(navSalesOrderLine));
		}
		
		return orderItemList;
	}
	
	private OrderItem generateOrderItem(B2BSalesOrderLine navSalesOrderLine) {
		OrderItem orderItem = new OrderItem();
		
		orderItem.setExternalData(generateOrderItemExternalData(navSalesOrderLine));
		orderItem.setExternalLineNo(navSalesOrderLine.getLineNo());
		orderItem.setItemAmount(navSalesOrderLine.getLineAmount().floatValue());
		orderItem.setItemPrice(navSalesOrderLine.getUnitPrice().floatValue());
		orderItem.setItemTaxAmount(null);
		orderItem.setItemTaxRate(null);
		orderItem.setQuantity(navSalesOrderLine.getQuantity().intValue());
		orderItem.setShippingAmount(null);
		orderItem.setShippingPrice(null);
		orderItem.setShippingTaxAmount(null);
		orderItem.setShippingTaxRate(null);
		orderItem.setTotalAmount(null);
		orderItem.setTotalTaxAmount(null);
		
		return orderItem;
	}
	
	private OrderItemExternalData generateOrderItemExternalData(B2BSalesOrderLine navSalesOrderLine) {
		OrderItemExternalData itemExternalData = new OrderItemExternalData();
		
		itemExternalData.setId(navSalesOrderLine.getNo());
		itemExternalData.setKey(navSalesOrderLine.getKey());
		itemExternalData.setDescription(navSalesOrderLine.getDescription());
		itemExternalData.setLineNo(navSalesOrderLine.getLineNo());
		itemExternalData.setQtyToInvoice(navSalesOrderLine.getQtyToInvoice().intValue());
		itemExternalData.setQtyToShip(navSalesOrderLine.getQtyToShip().intValue());
		itemExternalData.setQuantityInvoiced(navSalesOrderLine.getQuantityInvoiced().intValue());
		itemExternalData.setQuantityShipped(navSalesOrderLine.getQuantityShipped().intValue());
		itemExternalData.setType(null);
		
		return itemExternalData;
	}
	
	
	
	// ----------------------------------
	// B2B Orders to create/update in Nav
	// ----------------------------------
	
	public void syncFromB2BToNav() {
		logger.info("Starting Orders sync from B2B to Nav");
		try {
			List<Order> ordersToCreate = b2bOrdersService.getOrdersToCreate();
			
			if (ordersToCreate.size() == 0) {
				logger.debug("No B2B Orders to sync");
			}
			else {
				logger.info("There are " + ordersToCreate.size() + " B2B Orders to sync");
				
				Collections.sort(ordersToCreate, (o1, o2) -> (o1.getId().compareTo(o2.getId())));
				
				List<SyncResult> syncResultList = new ArrayList<>();
				
				for (Order order : ordersToCreate) {
					SyncResult syncResult = new SyncResult();
					syncResult.setId(order.getId());
					syncResult.setTimestamp(OffsetDateTime.now(Clock.systemUTC()));
					
					String navResult = "";
					try {
						navResult = navOrdersService.createSalesOrder(generateHolderRootSalesOrder(order));
					}
					catch (Exception e) {
						navResult = e.getMessage();
					}
					
					if ("Imported".equals(navResult)) {
						logger.info("B2B-Order with ID '" + order.getId() + "' was created on NAV with NO '" + navOrderNoPrefix + String.format("%05d", order.getId()) + "'");
						
						syncResult.setStatus(SyncStatus.SUCCESSFUL);
					}
					else {
						logger.error("Error when creating in NAV the B2B-Order with ID '" + order.getId() + "'. " + navResult);
						
						syncResult.setMessage(navResult);
						syncResult.setStatus(SyncStatus.FAILED);
					}
					syncResultList.add(syncResult);
				}
				
				b2bOrdersService.reportSyncResults(syncResultList);
			}
		}
		catch (Exception e) {
			logger.error("Error synchronizing Order", e);
		}
	}
	
	private Holder<Root> generateHolderRootSalesOrder(Order order) {
//		logger.info(" > order: " + order);
		
		TempSalesHeader tempSalesHeader = createTempSalesHeader(order);
		logger.info(" > tempSalesHeader: " + tempSalesHeader);
		int lineNo = 0;
		for (OrderItem orderItem : order.getOrderItems()) {
			lineNo += 10000;
			tempSalesHeader.getTempSalesLines().add(createTempSalesLines(orderItem, lineNo));
		}

		Holder<Root> b2bSalesOrder = new Holder<Root>();
		
		b2bSalesOrder.value = objectFactory.createRoot();
		b2bSalesOrder.value.getContent().add(objectFactory.createRootTempSalesHeader(tempSalesHeader));
		
		return b2bSalesOrder;
	}

	private TempSalesHeader createTempSalesHeader(Order order) {
		TempSalesHeader tempSalesHeader = objectFactory.createTempSalesHeader();
		
		tempSalesHeader.setDocumentType("Order");
		tempSalesHeader.setDocumentNo(navOrderNoPrefix + String.format("%05d", order.getId()));
		
		tempSalesHeader.getSellto().add(createSellto(order));
		tempSalesHeader.getBillTo().add(createBillTo(order));
		tempSalesHeader.getShipTo().add(createShipTo(order));
		
		tempSalesHeader.setPaymentTermsCode(order.getExternalData().getPaymentTermsCode().getValue());
		tempSalesHeader.setExternalDocumentNo(order.getExternalData().getExternalDocumentNo());
		try { tempSalesHeader.setOrderDate(DateTimeUtils.offsetDateTimeToXmlGregorianCalendar(order.getCreationDate())); }
		catch (Exception e) { }
		try { tempSalesHeader.setRequestedDeliveryDate(DateTimeUtils.minXMLGregorianCalendar()); }
		catch (Exception e) { }
		try { tempSalesHeader.setPromisedDeliveryDate(DateTimeUtils.minXMLGregorianCalendar()); }
		catch (Exception e) { }
		tempSalesHeader.setQuoteNo(order.getExternalData().getQuoteNo());
		tempSalesHeader.setSalesPersonCode(order.getExternalData().getSalespersonCode());
		tempSalesHeader.setAssignedUserId(order.getExternalData().getAssignedUserId());
		tempSalesHeader.setStatus(order.getExternalData().getStatus());
		tempSalesHeader.setIncoterms("");
		// TODO: validate the field Interms
		tempSalesHeader.setShipAgentCode("");
		tempSalesHeader.setShipAgentServiceCode("");
		tempSalesHeader.setShippingAgentService("");
		tempSalesHeader.setDeliveryConfirmarion("");
		tempSalesHeader.setShipBillingType("");
		tempSalesHeader.setThirdPartyAccountNo("");
		tempSalesHeader.setPkgTrackingNo("");
		try { tempSalesHeader.setShipmentDate(DateTimeUtils.minXMLGregorianCalendar()); }
		catch (Exception e) { }
		
		return tempSalesHeader;
	}

	private Sellto createSellto(Order order) {
		Sellto sellto = objectFactory.createSellto();
		
		sellto.getContent().add(objectFactory.createSelltoSelltoCustomerNo(order.getExternalData().getCustomerId()));
		sellto.getContent().add(objectFactory.createSelltoSelltoCustomerName(order.getExternalData().getCustomerName()));
		sellto.getContent().add(objectFactory.createSelltoSelltoAddress(order.getBillingCustomerAddress().getStreetLine1()));
		sellto.getContent().add(objectFactory.createSelltoSelltoAddress2(order.getBillingCustomerAddress().getStreetLine2()));
		sellto.getContent().add(objectFactory.createSelltoSelltoZipCode(order.getBillingCustomerAddress().getZip()));
		sellto.getContent().add(objectFactory.createSelltoSelltoCity(order.getBillingCustomerAddress().getCity()));
		sellto.getContent().add(objectFactory.createSelltoSelltoState(order.getBillingCustomerAddress().getStateCode()));
//		sellto.getContent().add(objectFactory.createSelltoSelltoPhoneNo(order.getBillingCustomerAddress().getPhone()));
		// TODO: check why Phone field is no in sellTo object
		sellto.getContent().add(objectFactory.createSelltoSelltoCountry(order.getBillingCustomerAddress().getCountryCode()));
		
		return sellto;
	}

	private BillTo createBillTo(Order order) {
		BillTo billTo = objectFactory.createBillTo();

		billTo.getContent().add(objectFactory.createBillToBilltoCustomerNo(order.getExternalData().getCustomerId()));
		billTo.getContent().add(objectFactory.createBillToBilltoCustomerName(order.getExternalData().getCustomerName()));
		billTo.getContent().add(objectFactory.createBillToBilltoAddress(order.getBillingCustomerAddress().getStreetLine1()));
		billTo.getContent().add(objectFactory.createBillToBilltoAddress2(order.getBillingCustomerAddress().getStreetLine2()));
		billTo.getContent().add(objectFactory.createBillToBilltoZipCode(order.getBillingCustomerAddress().getZip()));
		billTo.getContent().add(objectFactory.createBillToBilltoCity(order.getBillingCustomerAddress().getCity()));
		billTo.getContent().add(objectFactory.createBillToBilltoState(order.getBillingCustomerAddress().getStateCode()));
		billTo.getContent().add(objectFactory.createBillToBillToPhone(order.getBillingCustomerAddress().getPhone()));
		billTo.getContent().add(objectFactory.createBillToBilltoCountry(order.getBillingCustomerAddress().getCountryCode()));
		
		return billTo;
	}

	private ShipTo createShipTo(Order order) {
		ShipTo shipTo = objectFactory.createShipTo();

		shipTo.getContent().add(objectFactory.createShipToShiptoCode(""));
		shipTo.getContent().add(objectFactory.createShipToShiptoName(order.getShippingCustomerAddress().getName()));
		shipTo.getContent().add(objectFactory.createShipToShiptoName2(""));
		shipTo.getContent().add(objectFactory.createShipToShiptoAddress(order.getShippingCustomerAddress().getStreetLine1()));
		shipTo.getContent().add(objectFactory.createShipToShiptoAddress2(order.getShippingCustomerAddress().getStreetLine2()));
		shipTo.getContent().add(objectFactory.createShipToShiptoZipCode(order.getShippingCustomerAddress().getZip()));
		shipTo.getContent().add(objectFactory.createShipToShiptoCity(order.getShippingCustomerAddress().getCity()));
		shipTo.getContent().add(objectFactory.createShipToShiptoState(order.getShippingCustomerAddress().getStateCode()));
		shipTo.getContent().add(objectFactory.createShipToShipToContact(""));
		// TODO: add contact to AddressData class
		shipTo.getContent().add(objectFactory.createShipToShipToPhone(order.getShippingCustomerAddress().getPhone()));
		shipTo.getContent().add(objectFactory.createShipToShiptoCountry(order.getShippingCustomerAddress().getCountryCode()));
		shipTo.getContent().add(objectFactory.createShipToShiptoAddressType(order.getExternalData().getShipToAddressType().toString()));
		
		return shipTo;
	}
	
	private TempSalesLines createTempSalesLines(OrderItem orderItem, Integer lineNo) {
		TempSalesLines tempSalesLine = objectFactory.createTempSalesLines();
		
		tempSalesLine.setType(orderItem.getExternalData().getType().toString());
		tempSalesLine.setNo(orderItem.getExternalData().getId());
//		tempSalesLine.setLineNo(lineNo);
		tempSalesLine.setLineNo(orderItem.getExternalData().getLineNo());
		tempSalesLine.setDescription(orderItem.getExternalData().getDescription());
		tempSalesLine.setUnitOfMeasure("EACH");
		tempSalesLine.setQuantity(new BigDecimal(orderItem.getQuantity()));
		tempSalesLine.setUnitPrice(new BigDecimal(orderItem.getItemPrice()));
		tempSalesLine.setDiscountAmount(new BigDecimal("0"));
		tempSalesLine.setTaxGroupCode("");
		tempSalesLine.setLineAmount(new BigDecimal(orderItem.getTotalAmount()));
		tempSalesLine.setQtyToShip(new BigDecimal(orderItem.getQuantity()));
		tempSalesLine.setQtyToInv(new BigDecimal(orderItem.getQuantity()));
		tempSalesLine.setQtyShipped(new BigDecimal("0"));
		tempSalesLine.setQtyInvoiced(new BigDecimal("0"));
		
		logger.info(" > tempSalesLine: " + tempSalesLine);
		
		return tempSalesLine;
	}
	
}
