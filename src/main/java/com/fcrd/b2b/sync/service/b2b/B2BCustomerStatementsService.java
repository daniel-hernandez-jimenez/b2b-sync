package com.fcrd.b2b.sync.service.b2b;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.api.client.sync.api.CustomerStatementsApi;
import com.fcrd.b2b.api.client.sync.model.CustomerStatement;
import com.fcrd.b2b.api.client.sync.model.MergeResult;
import com.fcrd.b2b.api.client.sync.model.UpdateCustomerStatementsRequest;

@Service
public class B2BCustomerStatementsService extends B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BCustomerStatementsService.class);
	
	protected static CustomerStatementsApi customerStatementsApi;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("Initializing B2BCustomerStatementsService");
		
		customerStatementsApi = new CustomerStatementsApi(getApiSyncClient());
	}
	
	public void updateCustomerStatements(List<CustomerStatement> customerStatementList) throws Exception {
	    UpdateCustomerStatementsRequest updateCustomerStatementsRequest = new UpdateCustomerStatementsRequest();
	    
	    updateCustomerStatementsRequest.setCustomerStatements(customerStatementList);
	    
	    MergeResult mergeResult = customerStatementsApi.updateCustomerStatements(updateCustomerStatementsRequest);

        if (mergeResult.getSuccess()) {
            logger.info("The Customer Statements update in B2B was successful");
        }
        else {
            logger.error("Something went wrong with the Customer Statements update in B2B. " + mergeResult.getMessage());
        }
	}
	
}

	