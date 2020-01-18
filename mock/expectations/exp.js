var client = require('mockserver-client').mockServerClient("localhost", 1080);

var pricesResponseObjcet = [
    {
        name : "London Book Shop",
        currency: "GBP"
    },
    {
		name: "Sydney Book Shop",
		currency: "AUD"
	},
	{
		name: "NewYork Book Shop",
		currency: "USD"
	}

]
var strpricesResponse  = JSON.stringify(pricesResponseObjcet);


// Responses.
client.mockAnyResponse({

    "httpRequest": {
        "method": "GET",
        "path"  : "/shop-api/shops"
    },
    "httpResponse": {
        "statusCode" : 200,
        "body": {
                    "not": false,
                    "type": "JSON",
                    "json": strpricesResponse,
                    "contentType": "application/json",
                    "matchType": "ONLY_MATCHING_FIELDS"
        },
        "delay": {
            "timeUnit": "SECONDS",
            "value": 5
        }
    },
    "times": {
        //"remainingTimes": 3,
        "unlimited": true
    }

});