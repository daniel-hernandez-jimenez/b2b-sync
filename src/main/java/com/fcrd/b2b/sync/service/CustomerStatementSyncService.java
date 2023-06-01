package com.fcrd.b2b.sync.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bcustomerstatement.B2BCustomerStatement;
import com.fcrd.b2b.api.client.sync.model.CustomerStatement;
import com.fcrd.b2b.api.client.sync.model.CustomerStatementExternalData;
import com.fcrd.b2b.api.client.sync.model.ExternalAccountOperationType;
import com.fcrd.b2b.sync.config.SyncSchedulingConfigurer;
import com.fcrd.b2b.sync.service.b2b.B2BCustomerStatementsService;
import com.fcrd.b2b.sync.service.nav.NavCustomerStatementsService;
import com.fcrd.b2b.sync.utils.DateTimeUtils;

@Service
public class CustomerStatementSyncService extends SyncSchedulingConfigurer {
	private static Logger logger = LoggerFactory.getLogger(CustomerStatementSyncService.class);
	
	@Value("${customerStatement.sync.initialDelay}")
	protected String customerStatementSyncInitialDelay;
	
	@Value("${customerStatement.sync.scheduleDelay}")
	protected String customerStatementSyncScheduleDelay;
	
	@Autowired
	B2BCustomerStatementsService b2bCustomerStatementsService;
	
	@Autowired
	NavCustomerStatementsService navCustomerStatementsService;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("Initializing CustomerStatementSyncService");
		setInitialDelay(customerStatementSyncInitialDelay);
		setScheduleDelay(customerStatementSyncScheduleDelay);
	}
	
	protected void doit() {
		syncFromNavToB2BWithLastModifiedDate(false);
	}
	
	public void syncFromNavToB2BWithLastModifiedDate(boolean retrying) {
		logger.info("Starting Customer Statements sync from Nav to B2B");
		try {
			List<B2BCustomerStatement> navCustomerStatementList = navCustomerStatementsService.getOpenCustomerStatements();
			
			if (navCustomerStatementList.size() == 0) {
				logger.info("No NAV CustomerStatements data to sync");
			}
			else {
				logger.info("There are " + navCustomerStatementList.size() + " Dynamics Nav CustomerStatements to sync");
				
				List<CustomerStatement> customerStatementList = new ArrayList<>();
				for (B2BCustomerStatement navCustomerStatement : navCustomerStatementList) {
					customerStatementList.add(generateCustomerStatementFromB2BCustomerStatementList(navCustomerStatement));
				}
				
				b2bCustomerStatementsService.updateCustomerStatements(customerStatementList);
			}
		}
		catch (B2BUnauthorizedException e) {
			logger.error("Error synchronizing CustomerStatements.", e);
			if (!retrying) {
				retryAfterCreateAPISession();
			}
		}
		catch (Exception e) {
			logger.error("Error synchronizing CustomerStatements.", e);
		}
	}
	
	private void retryAfterCreateAPISession() {
		try {
			b2bCustomerStatementsService.createAPISession();
			syncFromNavToB2BWithLastModifiedDate(true);
		}
		catch (Exception e) {
			logger.error("Error retrying synchronizing Customer Statements after create API Session.", e);
		}
	}
	
	private CustomerStatement generateCustomerStatementFromB2BCustomerStatementList(B2BCustomerStatement navCustomerStatement) throws Exception {
		CustomerStatement customerStatement = new CustomerStatement();
		
		CustomerStatementExternalData externalData = new CustomerStatementExternalData();
		
		externalData.setEntryNo(navCustomerStatement.getEntryNo());
		externalData.setKey(navCustomerStatement.getKey());
		try {
			externalData.setAmount(navCustomerStatement.getAmount());
		}
		catch (Exception e) {
			throw new Exception("Error converting customerStatement.amount: " + navCustomerStatement.getAmount() + ". " + e.getMessage());
		}
		externalData.setCustomerNo(navCustomerStatement.getCustomerNo());
		externalData.setDescription(navCustomerStatement.getDescription());
		try {
			externalData.setDocumentDate(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navCustomerStatement.getDocumentDate()));
		}
		catch (Exception e) {
			throw new Exception("Error converting customerStatement.documentDate: " + navCustomerStatement.getDocumentDate() + ". " + e.getMessage());
		}
		externalData.setDocumentNo(navCustomerStatement.getDocumentNo());
		try {
			externalData.setDocumentType(ExternalAccountOperationType.fromValue(navCustomerStatement.getDocumentType().toString()));
		} catch (Exception e) { }
		try {
			externalData.setDueDate(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navCustomerStatement.getDueDate()));
		}
		catch (Exception e) {
			throw new Exception("Error converting customerStatement.dueDate: " + navCustomerStatement.getDueDate() + ". " + e.getMessage());
		}
		externalData.setOrderNo(null);
		try {
			externalData.setPostingDate(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navCustomerStatement.getPostingDate()));
		}
		catch (Exception e) {
			throw new Exception("Error converting customerStatement.postingDate: " + navCustomerStatement.getPostingDate() + ". " + e.getMessage());
		}
		try {
			externalData.setRemainingAmount(navCustomerStatement.getRemainingAmount());
		}
		catch (Exception e) {
			throw new Exception("Error converting customerStatement.remainingAmount: " + navCustomerStatement.getRemainingAmount() + ". " + e.getMessage());
		}
		
//		customerStatement.setId(navCustomerStatement.getEntryNo().longValue());
		customerStatement.setCustomerId(14896L);
		customerStatement.setExternalData(externalData);
		
		return customerStatement;
	}
	
}
