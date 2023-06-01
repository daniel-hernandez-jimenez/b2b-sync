package com.fcrd.b2b.sync.service.nav;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatement;
import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatementFields;
import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatementFilter;
import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatementList;
import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatementPort;
import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatementService;

@Service
public class NavCustomerStatementsService {
	private static Logger logger = LoggerFactory.getLogger(NavCustomerStatementsService.class);
	
	private B2BCustomerStatementPort service;
	
	@PostConstruct
	private void postConstruct() {
		try {
			service = new B2BCustomerStatementService().getB2BCustomerStatementPort();
		}
		catch (Exception e) {
			logger.error("Error creating NAV CustomerStatements service instance", e);
		}
	}
	
	public List<B2BCustomerStatement> getOpenCustomerStatements() throws Exception {
		List<B2BCustomerStatement> customerStatements = new ArrayList<>();
		
		B2BCustomerStatementFilter filter = new B2BCustomerStatementFilter();
		filter.setField(B2BCustomerStatementFields.OPEN);
		filter.setCriteria("=1");

		List<B2BCustomerStatementFilter> filters = new ArrayList<>();
		filters.add(filter);

		String bookMarkKey = null;
		Integer setSize = 1000;
		while (true) {
			B2BCustomerStatementList customerStatementList = service.readMultiple(filters, bookMarkKey, setSize);
			
			if (customerStatementList.getB2BCustomerStatement().size() > 0) {
				customerStatements.addAll(customerStatementList.getB2BCustomerStatement());
			}
			
			if (customerStatementList.getB2BCustomerStatement().size() < setSize) {
				break;
			}
			
			bookMarkKey = customerStatementList.getB2BCustomerStatement().get(setSize-1).getKey();
		}
		
		return customerStatements;
	}
	
}
