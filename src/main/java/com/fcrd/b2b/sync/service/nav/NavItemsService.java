package com.fcrd.b2b.sync.service.nav;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemList;
import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemListFields;
import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemListFilter;
import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemListList;
import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemListPort;
import com.fcrd.b2b.nav.soap.client.b2bitemlist.B2BItemListService;

@Service
public class NavItemsService {
	private static Logger logger = LoggerFactory.getLogger(NavItemsService.class);
	
	private B2BItemListPort service;
	
	@PostConstruct
	private void postConstruct() {
		try {
			service = new B2BItemListService().getB2BItemListPort();
		}
		catch (Exception e) {
			logger.error("Error creating Navision Items service instance", e);
		}
	}

	public List<B2BItemList> getItemsWithChanges(String lastModifiedDateTime) {
		List<B2BItemList> itemsWithChanges = new ArrayList<>();
		try {
			B2BItemListFilter filter = new B2BItemListFilter();
			filter.setField(B2BItemListFields.LAST_MODIFIED_DATE_TIME);
			if (lastModifiedDateTime!=null) {
				filter.setCriteria(">"+lastModifiedDateTime);
			}

			List<B2BItemListFilter> filters = new ArrayList<>();
			filters.add(filter);

			String bookMarkKey = null;
			Integer setSize = 1000;
			while (true) {
				B2BItemListList itemListList = service.readMultiple(filters, bookMarkKey, setSize);
				
				if (itemListList.getB2BItemList().size() > 0) {
					itemsWithChanges.addAll(itemListList.getB2BItemList());
				}
				
				if (itemListList.getB2BItemList().size() < setSize) {
					break;
				}
				
				bookMarkKey = itemListList.getB2BItemList().get(setSize-1).getKey();
			}
		}
		catch (Exception e) {
			logger.error("Error getting B2BItemList filtered by lastModifiedDateTime", e);
		}
		return itemsWithChanges;
	}
	
}
