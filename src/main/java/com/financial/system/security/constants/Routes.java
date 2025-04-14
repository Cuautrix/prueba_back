package com.financial.system.security.constants;

import com.financial.system.shared.constants.ApiPathConstants;

public abstract class Routes {
    public static String[] WHITE_LIST = {
            ApiPathConstants.V1_ROUTE + ApiPathConstants.HEALTH + "/check",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/sign-in",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/sign-up",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/request-reset-password",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/change-password",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/verify-email",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.AUTH_ROUTE + "/validate-username",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findNationalities",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findClientTypes",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findAllOccupations",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findAllGenders",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findAllEntidadesFederativas",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findBanksList",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findProductTypes",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.CATALOGS + "/findRegimens",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/savePFAEDatosProspecto",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/uploadDocuments",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/savePFAEDireccionProspecto",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/getPFAEMexDatosProspectoInformation",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/findAddressByZipCode/*",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/ocr",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/renapo/*",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/sat/grupo/*",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/sat/v2/*",
            ApiPathConstants.V1_ROUTE + ApiPathConstants.ONBOARDING + "/update-status"

    };
}
