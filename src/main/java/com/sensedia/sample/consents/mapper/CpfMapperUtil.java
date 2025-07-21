package com.sensedia.sample.consents.mapper;

import org.mapstruct.Named;

public class CpfMapperUtil {

	@Named("onlyDigits")
	public static String onlyDigits(String cpf) {
		return cpf == null ? null : cpf.replaceAll("\\D", "");
	}
}
