package com.fcrd.b2b.sync.service.b2b;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.api.client.sync.api.OrdersApi;
import com.fcrd.b2b.api.client.sync.model.GetOrdersSyncStatsResponse;
import com.fcrd.b2b.api.client.sync.model.ListOrderResponse;
import com.fcrd.b2b.api.client.sync.model.ListOrdersCondition;
import com.fcrd.b2b.api.client.sync.model.MergeOrderRequest;
import com.fcrd.b2b.api.client.sync.model.MergeOrdersRequest;
import com.fcrd.b2b.api.client.sync.model.MergeResult;
import com.fcrd.b2b.api.client.sync.model.Order;
import com.fcrd.b2b.api.client.sync.model.ReportOrdersSyncResultsRequest;
import com.fcrd.b2b.api.client.sync.model.SyncOperation;
import com.fcrd.b2b.api.client.sync.model.SyncResult;
import com.fcrd.b2b.api.client.sync.model.SyncStats;

@Service
public class B2BOrdersService extends B2BBaseService {
	private static Logger logger = LoggerFactory.getLogger(B2BOrdersService.class);
	
	protected static OrdersApi ordersApi;
	
	@PostConstruct
	private void initialSettings() {
		logger.info("Initializing B2BOrdersService");
		
		ordersApi = new OrdersApi(getApiSyncClient());
	}
	
	public SyncStats requestSyncStats() throws Exception {
		GetOrdersSyncStatsResponse response = ordersApi.getOrdersSyncStats();
		SyncStats syncStats = response.getItem();
		
		return syncStats;
	}
	
	public void mergeOrders(List<Order> orderList) throws Exception {
		List<MergeOrderRequest> mergeOrderRequestList = new ArrayList<>();

		Iterator<Order> iterator = orderList.iterator();
		while (iterator.hasNext()) {
			mergeOrderRequestList.add(createMergeOrderRequest(iterator.next()));
			
			if (mergeOrderRequestList.size() == 100) {
				merge(mergeOrderRequestList);
				mergeOrderRequestList.clear();
			}
		}
		if (mergeOrderRequestList.size() > 0) {
			merge(mergeOrderRequestList);
		}
	}
	
	private void merge(List<MergeOrderRequest> mergeOrderRequestList) throws Exception {
		logger.info("Total orders to merge in B2B: " + mergeOrderRequestList.size());
		for (MergeOrderRequest mergeOrderRequest : mergeOrderRequestList) {
		    logger.info(mergeOrderRequest.getExternalId());
		}
		
		MergeOrdersRequest mergeOrdersRequest = new MergeOrdersRequest();
		mergeOrdersRequest.setOrders(mergeOrderRequestList);;
		
		MergeResult mergeResult = ordersApi.mergeOrders(mergeOrdersRequest);
		
		if (mergeResult.getSuccess()) {
			logger.info("The Orders merger in B2B was successful");
		}
		else {
			logger.error("Something went wrong with the Orders merger in B2B. " + mergeResult.getMessage());
		}
	}
	
	private MergeOrderRequest createMergeOrderRequest(Order order) {
		MergeOrderRequest mergeOrderRequest = new MergeOrderRequest();
		
		mergeOrderRequest.setExternalId(order.getExternalData().getId());
		mergeOrderRequest.setOperation(SyncOperation.MERGE);
        mergeOrderRequest.setValue(order);
		
		return mergeOrderRequest;
	}
	
	
	
	public List<Order> getOrdersToCreate() throws Exception {
		ListOrderResponse response = ordersApi.listOrders(ListOrdersCondition.CREATE);
		List<Order> orderList = response.getOrders();

		logger.info("Total B2B Orders to create in Nav: " + orderList.size());
		
		return orderList;
	}
	
	public List<Order> getOrdersToUpdate() throws Exception {
		ListOrderResponse response = ordersApi.listOrders(ListOrdersCondition.UPDATE);
		List<Order> orderList = response.getOrders();
		
		logger.info("Total B2B Orders to update in Nav: " + orderList.size());
		
		return orderList;
	}

	public void reportSyncResults(List<SyncResult> syncResultList) throws Exception {
		try {
			ReportOrdersSyncResultsRequest reportOrdersSyncResultsRequest = new ReportOrdersSyncResultsRequest();
			reportOrdersSyncResultsRequest.setSyncResults(syncResultList);
			
//			ordersApi.reportOrdersSyncResults(reportOrdersSyncResultsRequest);
		}
		catch (Exception e) {
			logger.error("Error reporting Orders sync results in B2B. " + e.getMessage());
			throw e;
		}
	}
	
}

	