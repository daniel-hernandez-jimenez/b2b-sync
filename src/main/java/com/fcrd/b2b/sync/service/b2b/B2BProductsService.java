package com.fcrd.b2b.sync.service.b2b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.sync.api.client.ProductsApi;
import com.fcrd.b2b.sync.api.client.model.GetProductsSyncStatsResponse;
import com.fcrd.b2b.sync.api.client.model.MergeProductRequest;
import com.fcrd.b2b.sync.api.client.model.MergeProductsRequest;
import com.fcrd.b2b.sync.api.client.model.MergeResult;
import com.fcrd.b2b.sync.api.client.model.Product;
import com.fcrd.b2b.sync.api.client.model.SyncOperation;
import com.fcrd.b2b.sync.api.client.model.SyncStats;

@Service
public class B2BProductsService extends B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BProductsService.class);
	
	@Value("${datetime.pattern}")
	protected String dateTimePattern;
	
	protected static ProductsApi productsApi;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("B2BProductsService.initialSettings");
		
		productsApi = new ProductsApi();
	}
	
//	public String getLastExternalModifiedDateTime() throws Exception {
//		String lastModifiedDateTime = null;
//		try {
//			GetProductsSyncStatsResponse response = productsApi.getProductsSyncStats();
//			SyncStats syncStats = response.getItem();
//			
////			lastModifiedDateTime = DateTimeUtils.offsetDateTimeToString(syncStats.getLastExternalModifiedTime(), dateTimePattern);
//			lastModifiedDateTime = DateTimeUtils.offsetDateTimeToString(syncStats.getLastExternalModifiedTime(), "MM/dd/yyyy HH:mm:ss");
//			
//			logger.info("B2B Products LastExternalModifiedDateTime: " + lastModifiedDateTime);
//		}
//		catch (ApiException e) {
//			throw (e.getCode()==401 ? new B2BUnauthorizedException("Unauthorized") : e);
//		}
//		return lastModifiedDateTime;
//	}
	
	public SyncStats requestSyncStats() throws Exception {
		GetProductsSyncStatsResponse response = productsApi.getProductsSyncStats();
		SyncStats syncStats = response.getItem();
		
		return syncStats;
	}
	
	public void mergeProducts(List<Product> productList) throws Exception {
		List<MergeProductRequest> mergeProductRequestList = new ArrayList<>();
		
		Iterator<Product> iterator = productList.iterator();
		while (iterator.hasNext()) {
			mergeProductRequestList.add(createMergeProductRequest(iterator.next()));
			
			if (mergeProductRequestList.size() == 100) {
				merge(mergeProductRequestList);
				mergeProductRequestList.clear();
			}
		}
		if (mergeProductRequestList.size() > 0) {
			merge(mergeProductRequestList);
		}
	}
	
	private void merge(List<MergeProductRequest> mergeProductRequestList) throws Exception {
		logger.info("Total products to merge in B2B: " + mergeProductRequestList.size());
		
		MergeProductsRequest mergeProductsRequest = new MergeProductsRequest();
		mergeProductsRequest.products(mergeProductRequestList);
		
		MergeResult mergeResult = productsApi.mergeProducts(mergeProductsRequest);
		
		if (mergeResult.getSuccess()) {
			logger.info("The Products merger in B2B was successful");
		}
		else {
			logger.error("Something went wrong with the Products merger in B2B. " + mergeResult.getMessage());
		}
	}
	
	private MergeProductRequest createMergeProductRequest(Product product) {
		MergeProductRequest mergeProductRequest = new MergeProductRequest();
		
		mergeProductRequest.setExternalId(product.getExternalData().getId());
		mergeProductRequest.setOperation(SyncOperation.MERGE);
		mergeProductRequest.setValue(product);
		
		return mergeProductRequest;
	}
	
}

	