package model

type HwWalletObject struct {
	PassVersion         string `json:"passVersion"`
	PassTypeIdentifier  string `json:"passTypeIdentifier"`
	PassStyleIdentifier string `json:"passStyleIdentifier"`
	OrganizationName    string `json:"organizationName"`
	OrganizationPassID  string `json:"organizationPassId"`
	SerialNumber        string `json:"serialNumber"`
	Fields              struct {
		CountryCode  string `json:"countryCode"`
		CurrencyCode string `json:"currencyCode"`
		Status       struct {
			State      string `json:"state"`
			EffectTime string `json:"effectTime"`
			ExpireTime string `json:"expireTime"`
		} `json:"status"`
		RelatedPassIds struct {
			TypeId string `json:"typeId"`
			Id     string `json:"id"`
		} `json:"relatedPassIds"`
		LocationList []struct {
			Longitude string `json:"longitude"`
			Latitude  string `json:"latitude"`
		} `json:"locationList"`
		BarCode struct {
			Text     string `json:"text"`
			Type     string `json:"type"`
			Value    string `json:"value"`
			Encoding string `json:"encoding"`
		} `json:"barCode"`
		CommonFields []struct {
			Key            string `json:"key"`
			Value          string `json:"value"`
			Label          string `json:"label,omitempty"`
			LocalizedValue string `json:"localizedValue,omitempty"`
		} `json:"commonFields"`
		AppendFields []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
			Label string `json:"label,omitempty"`
		} `json:"appendFields"`
		MessageList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
			Label string `json:"label"`
		} `json:"messageList"`
		TimeList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
			Label string `json:"label"`
		} `json:"timeList"`
		ImageList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
		} `json:"imageList"`
		TextList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
		} `json:"textList"`
		Localized []struct {
			Key      string `json:"key"`
			Language string `json:"language"`
			Value    string `json:"value"`
		} `json:"localized"`
		TicketInfoList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
		} `json:"ticketInfoList"`
		UrlList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
		} `json:"urlList"`
	} `json:"fields"`
}

type PageInfo struct {
	ServiceType string `json:"serviceType"`
	PageSize    int64  `json:"pageSize"`
	NextSession string `json:"nextSession"`
}

type BatchQueryResp struct {
	PageInfo PageInfo         `json:"pageInfo"`
	Data     []HwWalletObject `json:"data"`
}

type EventTicketInstance struct {
	PassVersion         string `json:"passVersion"`
	PassTypeIdentifier  string `json:"passTypeIdentifier"`
	PassStyleIdentifier string `json:"passStyleIdentifier"`
	OrganizationName    string `json:"organizationName"`
	OrganizationPassID  string `json:"organizationPassId"`
	SerialNumber        string `json:"serialNumber"`
	Fields              struct {
		CountryCode  string `json:"countryCode"`
		CurrencyCode string `json:"currencyCode"`
		LocationList []struct {
			Longitude string `json:"longitude"`
			Latitude  string `json:"latitude"`
		} `json:"locationList"`
		CommonFields []struct {
			Key            string `json:"key"`
			Value          string `json:"value"`
			Label          string `json:"label,omitempty"`
			LocalizedValue string `json:"localizedValue,omitempty"`
		} `json:"commonFields"`
		AppendFields []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
			Label string `json:"label,omitempty"`
		} `json:"appendFields"`
		MessageList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
			Label string `json:"label"`
		} `json:"messageList"`
		ImageList []struct {
			Key   string `json:"key"`
			Value string `json:"value"`
		} `json:"imageList"`
		Localized []struct {
			Key      string `json:"key"`
			Language string `json:"language"`
			Value    string `json:"value"`
		} `json:"localized"`
	} `json:"fields"`
}
