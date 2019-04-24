package com.microting.report.jasper;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
class Report {

	private String template;
	private String inputDataUri;
}
