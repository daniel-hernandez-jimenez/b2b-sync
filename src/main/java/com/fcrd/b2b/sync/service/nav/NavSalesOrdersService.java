package com.fcrd.b2b.sync.service.nav;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrder;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderFields;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderFilter;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderList;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderPort;
import com.fcrd.b2b.nav.soap.client.b2bsalesorder.B2BSalesOrderService;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.B2BSalesQuoteOrder;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.B2BSalesQuoteOrderPort;
import com.fcrd.b2b.nav.soap.client.b2bsalesquoteorder.Root;

@Service
public class NavSalesOrdersService {
	private static Logger logger = LoggerFactory.getLogger(NavSalesOrdersService.class);
	
	private B2BSalesOrderPort salesOrderService;
	
	private B2BSalesQuoteOrderPort salesQuoteOrderService;
	
	@PostConstruct
	private void postConstruct() {
		try {
			salesOrderService = new B2BSalesOrderService().getB2BSalesOrderPort();
			salesQuoteOrderService = new B2BSalesQuoteOrder().getB2BSalesQuoteOrderPort();
		}
		catch (Exception e) {
			logger.error("Error creating NAV SalesOrder service instance", e);
		}
	}
	
	public List<B2BSalesOrder> getSalesOrdersWithChanges(String lastModifiedDateTime) {
		List<B2BSalesOrder> salesOrdersWithChanges = new ArrayList<>();
		try {
			B2BSalesOrderFilter filterByLastModifiedDateTime = new B2BSalesOrderFilter();
			filterByLastModifiedDateTime.setField(B2BSalesOrderFields.LAST_MODIFIED_DATE_TIME);
			filterByLastModifiedDateTime.setCriteria(">="+lastModifiedDateTime);
			
			B2BSalesOrderFilter filterByNo = new B2BSalesOrderFilter();
			filterByNo.setField(B2BSalesOrderFields.NO);
			filterByNo.setCriteria("B2B*");

			List<B2BSalesOrderFilter> filters = new ArrayList<>();
			filters.add(filterByLastModifiedDateTime);
			filters.add(filterByNo);

			String bookMarkKey = null;
			Integer setSize = 1000;
			while (true) {
				B2BSalesOrderList salesOrderList = salesOrderService.readMultiple(filters, bookMarkKey, setSize);
				
				if (salesOrderList.getB2BSalesOrder().size() > 0) {
					salesOrdersWithChanges.addAll(salesOrderList.getB2BSalesOrder());
				}
				
				if (salesOrderList.getB2BSalesOrder().size() < setSize) {
					break;
				}
				
				bookMarkKey = salesOrderList.getB2BSalesOrder().get(setSize-1).getKey();
			}
		}
		catch (Exception e) {
			logger.error("Error getting B2BSalesOrder filtered by lastModifiedDateTime", e);
		}
		return salesOrdersWithChanges;
	}
	
	public String createSalesOrder(Holder<Root> b2bSalesOrder) {
		Holder<String> returnValue = new Holder<String>();
		
		salesQuoteOrderService.createSalesQuoteOROrder(b2bSalesOrder, returnValue);
		
//		logger.info("NAV Create SalesOrder result: " + returnValue.value);
//		logger.info(((TempSalesHeader)b2bSalesOrder.value.getContent().get(0)).getDocumentNo());
		
		return returnValue.value;
	}
	
}
