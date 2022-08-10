package com.fcrd.b2b.sync.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemList;
import com.fcrd.b2b.sync.api.client.model.Product;
import com.fcrd.b2b.sync.api.client.model.ProductExternalData;
import com.fcrd.b2b.sync.config.SyncSchedulingConfigurer;
import com.fcrd.b2b.sync.service.b2b.B2BProductsService;
import com.fcrd.b2b.sync.service.nav.NavItemsService;
import com.fcrd.b2b.sync.utils.DateTimeUtils;

@Service
public class ProductSyncService extends SyncSchedulingConfigurer {
	private static Logger logger = LoggerFactory.getLogger(ProductSyncService.class);
	
	@Value("${product.sync.initialDelay}")
	protected String productSyncInitialDelay;
	
	@Value("${product.sync.scheduleDelay}")
	protected String productSyncScheduleDelay;
	
	@Autowired
	B2BProductsService b2bProductsService;
	
	@Autowired
	NavItemsService navItemsService;
	
	@PostConstruct
	private void initialSettings() {
		setInitialDelay(productSyncInitialDelay);
		setScheduleDelay(productSyncScheduleDelay);
	}
	
	protected void doit() {
		syncFromNavToB2BWithLastModifiedDate(false);
	}
	
	public void syncFromNavToB2BWithLastModifiedDate(boolean retrying) {
		logger.info("Starting synchronization of Product from Nav to B2B");
		try {
			String lastExternalModifiedDateTime = b2bProductsService.getLastExternalModifiedDateTime();
			
			List<B2BItemList> navItemList = navItemsService.getItemsWithChanges(lastExternalModifiedDateTime);
			
			if (navItemList.size() == 0) {
				logger.debug("No NAV Products data to sync");
			}
			else {
				logger.info("There are " + navItemList.size() + " Dynamics Nav Products to sync");
				
				List<Product> productList = new ArrayList<>();
				for (B2BItemList navItem : navItemList) {
					productList.add(generateProductFromB2BItemList(navItem));
				}
				b2bProductsService.mergeProducts(productList);
			}
		}
		catch (B2BUnauthorizedException e) {
			logger.error("Error synchronizing Products.", e);
			if (!retrying) {
				retryAfterCreateAPISession();
			}
		}
		catch (Exception e) {
			logger.error("Error synchronizing Products.", e);
		}
	}
	
	private void retryAfterCreateAPISession() {
		try {
			b2bProductsService.createAPISession();
			syncFromNavToB2BWithLastModifiedDate(true);
		}
		catch (Exception e) {
			logger.error("Error retrying synchronizing Products after create API Session.", e);
		}
	}
	
	private Product generateProductFromB2BItemList(B2BItemList navItem) throws Exception {
		Product product = new Product();
		
		ProductExternalData externalData = new ProductExternalData();
		externalData.setId(navItem.getNo());
		externalData.setKey(navItem.getKey());
		try {
			externalData.setLastModifiedDateTime(DateTimeUtils.xmlGregorianCalendarToOffsetDateTime(navItem.getLastModifiedDateTime()));
		}
		catch (Exception e) {
			throw new Exception("Error converting product.lastModifiedDataTime: " + navItem.getLastModifiedDateTime().toString());
		}
		externalData.setName(navItem.getDescription());
		
		product.setExternalData(externalData);
		
		return product;
	}
	
}
