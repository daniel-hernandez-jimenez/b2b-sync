package com.fcrd.b2b.sync.service.nav;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerList;
import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerListFields;
import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerListFilter;
import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerListList;
import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerListPort;
import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerListService;

@Service
public class NavCustomersService {
	private static Logger logger = LoggerFactory.getLogger(NavCustomersService.class);
	
	private B2BCustomerListPort service;
	
	@PostConstruct
	private void postConstruct() {
		try {
			service = new B2BCustomerListService().getB2BCustomerListPort();
		}
		catch (Exception e) {
			logger.error("Error creating NAV Customers service instance", e);
		}
	}
	
	public List<B2BCustomerList> getCustomersWithChanges(String lastModifiedDateTime) throws Exception {
		List<B2BCustomerList> customersWithChanges = new ArrayList<>();
		
		B2BCustomerListFilter filter = new B2BCustomerListFilter();
		filter.setField(B2BCustomerListFields.LAST_MODIFIED_DATE_TIME);
		if (lastModifiedDateTime!=null) {
			filter.setCriteria(">"+lastModifiedDateTime);
		}

		List<B2BCustomerListFilter> filters = new ArrayList<>();
		filters.add(filter);

		String bookMarkKey = null;
		Integer setSize = 1000;
		while (true) {
			B2BCustomerListList customerListList = service.readMultiple(filters, bookMarkKey, setSize);
			
			if (customerListList.getB2BCustomerList().size() > 0) {
				customersWithChanges.addAll(customerListList.getB2BCustomerList());
			}
			
			if (customerListList.getB2BCustomerList().size() < setSize) {
				break;
			}
			
			bookMarkKey = customerListList.getB2BCustomerList().get(setSize-1).getKey();
		}
		
		return customersWithChanges;
	}
	
}
