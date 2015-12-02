package com.bitmonlab.osiris.test.acceptancetest.map.metadata;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.bitmonlab.osiris.test.acceptancetest.map.commons.HttpResponse;
import com.bitmonlab.commons.api.map.model.geojson.MetaData;
import com.bitmonlab.osiris.api.map.transferobject.MetaDataDTO;
import com.bitmonlab.restsender.ClientResponse;
import com.bitmonlab.restsender.Headers;
import com.bitmonlab.restsender.RestMethod;
import com.bitmonlab.restsender.RestRequestSender;
import com.sun.jersey.api.client.GenericType;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetMetaData {
	
	@Inject
	@Named("osirisMapRequestSender")
	private RestRequestSender sender;
	
	@Inject 
	@Named("osirisGeolocationMongoTemplate")
	private MongoTemplate mongoTemplate;
	
	private ClientResponse<MetaDataDTO> response;
	
	@Inject
	private HttpResponse httpResponse;
	
	private final static String collectionMetaData="Metadata";
	
	@Given("^I have a map with appId \"([^\"]*)\" and metadata osmChecksum \"([^\"]*)\", routingChecksum \"([^\"]*)\", minLat \"([^\"]*)\", minLon \"([^\"]*)\", maxLat \"([^\"]*)\" and maxLon \"([^\"]*)\"$")
	public void I_have_a_map_with_appId_and_metadata_osmChecksum_routingChecksum_minLat_minLon_maxLat_and_maxLon(String appId, String osmChecksum, String routingChecksum, String minLat, String minLon, String maxLat, String maxLon){
	    // Express the Regexp above with the code you wish you had
		MetaData metadata=createMetaData(appId, osmChecksum, routingChecksum, minLat, minLon, maxLat, maxLon);
		mongoTemplate.save(metadata, collectionMetaData);
	}

	@When("^I invoke a GET metadata to \"([^\"]*)\" and applicationIdentifier \"([^\"]*)\"$")
	public void I_invoke_a_GET_metadata_to_and_applicationIdentifier(String url, String appId){
	    // Express the Regexp above with the code you wish you had
		response= sender.invoke(RestMethod.GET, url , new GenericType<MetaDataDTO>(){}, new Headers("api_key", appId));				
		httpResponse.setResponse(response);
	}

	@Then("^I verify metadata has same fields osmChecksum \"([^\"]*)\", routingChecksum \"([^\"]*)\", minLat \"([^\"]*)\", minLon \"([^\"]*)\", maxLat \"([^\"]*)\" and maxLon \"([^\"]*)\"$")
	public void I_verify_metadata_has_same_fields_osmChecksum_routingChecksum_minLat_minLon_maxLat_and_maxLon(String osmChecksum, String routingChecksum, String minLat, String minLon, String maxLat, String maxLon){
	    // Express the Regexp above with the code you wish you had
		String chkSumResponse = response.getEntity().getOSMChecksum();
		String minLatResponse = response.getEntity().getMinLatitude();
		String minLonResponse = response.getEntity().getMinLongitude();
		String maxLatResponse = response.getEntity().getMaxLatitude();
		String maxLonResponse = response.getEntity().getMaxLongitude();
		String checksumRouting = response.getEntity().getRoutingChecksum();
											
		Assert.assertEquals("Checksum must be the same", osmChecksum, chkSumResponse);	
		Assert.assertEquals("minLat must be the same", minLat, minLatResponse);
		Assert.assertEquals("minLon must be the same", minLon, minLonResponse);
		Assert.assertEquals("maxLat must be the same", maxLat, maxLatResponse);
		Assert.assertEquals("maxLon must be the same", maxLon, maxLonResponse);
		Assert.assertEquals("RoutingChecksum must be the same",routingChecksum, checksumRouting);
	}
	
	private MetaData createMetaData(String appId, String osmChecksum, String routingChecksum, String minLat, String minLon, String maxLat, String maxLon){
		MetaData metaData=new MetaData();
		metaData.setAppId(appId);
		metaData.setMaxlat(Double.parseDouble(maxLat));
		metaData.setMaxlon(Double.parseDouble(maxLon));
		metaData.setMinlat(Double.parseDouble(minLat));
		metaData.setMinlon(Double.parseDouble(minLon));
		metaData.setOSMChecksum(osmChecksum);
		metaData.setRoutingChecksum(routingChecksum);
		return metaData;
	}
	
}
