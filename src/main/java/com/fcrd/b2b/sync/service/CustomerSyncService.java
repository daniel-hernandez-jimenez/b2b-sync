package com.fcrd.b2b.sync.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bcustomerlist.B2BCustomerList;
import com.fcrd.b2b.api.client.sync.model.Customer;
import com.fcrd.b2b.api.client.sync.model.CustomerExternalData;
import com.fcrd.b2b.sync.config.SyncSchedulingConfigurer;
import com.fcrd.b2b.sync.service.b2b.B2BCustomersService;
import com.fcrd.b2b.sync.service.nav.NavCustomersService;
import com.fcrd.b2b.sync.utils.DateTimeUtils;

@Service
public class CustomerSyncService extends SyncSchedulingConfigurer {
	private static Logger logger = LoggerFactory.getLogger(CustomerSyncService.class);
	
	@Value("${customer.sync.initialDelay}")
	protected String customerSyncInitialDelay;
	
	@Value("${customer.sync.scheduleDelay}")
	protected String customerSyncScheduleDelay;
	
	@Autowired
	B2BCustomersService b2bCustomersService;
	
	@Autowired
	NavCustomersService navCustomersService;
	
	@PostConstruct
	private void initialSettings() {
	    logger.info("Initializing CustomerSyncService");
		setInitialDelay(customerSyncInitialDelay);
		setScheduleDelay(customerSyncScheduleDelay);
	}
	
	protected void doit() {
		syncFromNavToB2BWithLastModifiedDate(false);
	}
	
	public void syncFromNavToB2BWithLastModifiedDate(boolean retrying) {
		logger.info("Starting Customers sync from Nav to B2B");
		try {
			String lastExternalModifiedDateTime = b2bCustomersService.getLastExternalModifiedDateTime();
			
			List<B2BCustomerList> navCustomerList = navCustomersService.getCustomersWithChanges(lastExternalModifiedDateTime);
			
			if (navCustomerList.size() == 0) {
				logger.info("No NAV Customers data to sync");
			}
			else {
				logger.info("There are " + navCustomerList.size() + " Dynamics Nav Customers to sync");
				
				Collections.sort(navCustomerList, (o1, o2) -> (o1.getLastModifiedDateTime().compare(o2.getLastModifiedDateTime())));
				
				logger.info("The " + navCustomerList.size() + " Customer records were sorted!");
				
				List<Customer> customerList = new ArrayList<>();
				for (B2BCustomerList navCustomer : navCustomerList) {
					customerList.add(generateCustomerFromB2BCustomerList(navCustomer));
				}
				
				b2bCustomersService.mergeCustomers(customerList);
			}
		}
		catch (B2BUnauthorizedException e) {
			logger.error("Error synchronizing Customers.", e);
			if (!retrying) {
				retryAfterCreateAPISession();
			}
		}
		catch (Exception e) {
			logger.error("Error synchronizing Customers.", e);
		}
	}
	
	private void retryAfterCreateAPISession() {
		try {
			b2bCustomersService.createAPISession();
			syncFromNavToB2BWithLastModifiedDate(true);
		}
		catch (Exception e) {
			logger.error("Error retrying synchronizing Customers after create API Session.", e);
		}
	}
	
	private Customer generateCustomerFromB2BCustomerList(B2BCustomerList navCustomer) throws Exception {
		Customer customer = new Customer();
		
		CustomerExternalData externalData = new CustomerExternalData();
		externalData.setId(navCustomer.getNo());
		externalData.setKey(navCustomer.getKey());
		externalData.setBalance(navCustomer.getBalanceLCY());
		try {
			externalData.setLastModifiedDateTime(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navCustomer.getLastModifiedDateTime()));
		}
		catch (Exception e) {
			throw new Exception("Error converting customer.lastModifiedDataTime: " + navCustomer.getLastModifiedDateTime().toString() + ". " + e.getMessage());
		}
		externalData.setName(navCustomer.getName());
		externalData.setPaymentTermsCode(navCustomer.getPaymentTermsCode());
		externalData.setSalesPersonCode(navCustomer.getSalespersonCode());
		
		customer.setExternalData(externalData);
		
		return customer;
	}
	
}
