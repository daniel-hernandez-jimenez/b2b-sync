package com.fcrd.b2b.sync.service.b2b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.api.client.sync.api.ProductsApi;
import com.fcrd.b2b.api.client.sync.model.GetProductsSyncStatsResponse;
import com.fcrd.b2b.api.client.sync.model.MergeProductRequest;
import com.fcrd.b2b.api.client.sync.model.MergeProductsRequest;
import com.fcrd.b2b.api.client.sync.model.MergeResult;
import com.fcrd.b2b.api.client.sync.model.Product;
import com.fcrd.b2b.api.client.sync.model.SyncOperation;
import com.fcrd.b2b.api.client.sync.model.SyncStats;

@Service
public class B2BProductsService extends B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BProductsService.class);
	
	protected static ProductsApi productsApi;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("Initializing B2BProductsService");
		
		productsApi = new ProductsApi();
	}
	
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

	