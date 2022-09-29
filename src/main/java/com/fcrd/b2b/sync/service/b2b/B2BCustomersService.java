package com.fcrd.b2b.sync.service.b2b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.sync.api.client.CustomersApi;
import com.fcrd.b2b.sync.api.client.model.Customer;
import com.fcrd.b2b.sync.api.client.model.GetCustomersSyncStatsResponse;
import com.fcrd.b2b.sync.api.client.model.MergeCustomerRequest;
import com.fcrd.b2b.sync.api.client.model.MergeCustomersRequest;
import com.fcrd.b2b.sync.api.client.model.MergeResult;
import com.fcrd.b2b.sync.api.client.model.SyncOperation;
import com.fcrd.b2b.sync.api.client.model.SyncStats;

@Service
public class B2BCustomersService extends B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BCustomersService.class);
	
	@Value("${datetime.pattern}")
	protected String dateTimePattern;
	
	protected static CustomersApi customersApi;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("B2BCustomersService.initialSettings");
		
		customersApi = new CustomersApi(getApiSyncClient());
	}
	
	public SyncStats requestSyncStats() throws Exception {
		GetCustomersSyncStatsResponse response = customersApi.getCustomersSyncStats();
		SyncStats syncStats = response.getItem();
		
		return syncStats;
	}
	
	public void mergeCustomers(List<Customer> customerList) throws Exception {
		List<MergeCustomerRequest> mergeCustomerRequestList = new ArrayList<>();

		Iterator<Customer> iterator = customerList.iterator();
		while (iterator.hasNext()) {
			mergeCustomerRequestList.add(createMergeCustomerRequest(iterator.next()));
			
			if (mergeCustomerRequestList.size() == 100) {
				merge(mergeCustomerRequestList);
				mergeCustomerRequestList.clear();
			}
		}
		if (mergeCustomerRequestList.size() > 0) {
			merge(mergeCustomerRequestList);
		}
	}
	
	private void merge(List<MergeCustomerRequest> mergeCustomerRequestList) throws Exception {
		logger.info("Total customers to merge in B2B: " + mergeCustomerRequestList.size());
		
		MergeCustomersRequest mergeCustomersRequest = new MergeCustomersRequest();
		mergeCustomersRequest.customers(mergeCustomerRequestList);
		
		MergeResult mergeResult = customersApi.mergeCustomers(mergeCustomersRequest);
		
		if (mergeResult.getSuccess()) {
			logger.info("The Customers merger in B2B was successful");
		}
		else {
			logger.error("Something went wrong with the Customers merger in B2B. " + mergeResult.getMessage());
		}
	}
	
	private MergeCustomerRequest createMergeCustomerRequest(Customer customer) {
		MergeCustomerRequest mergeCustomerRequest = new MergeCustomerRequest();
		
		mergeCustomerRequest.setExternalId(customer.getExternalData().getId());
		mergeCustomerRequest.setOperation(SyncOperation.MERGE);
		mergeCustomerRequest.setValue(customer);
		
		return mergeCustomerRequest;
	}
	
}

	