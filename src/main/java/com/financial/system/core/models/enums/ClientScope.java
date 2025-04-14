package com.financial.system.core.models.enums;

public enum ClientScope {
    PRE_REGISTER,
    // Onboarding scopes
    ONBOARDING_PFAE_LOCAL,
    ONBOARDING_PFAE_INTERNATIONAL,
    ONBOARDING_PMORAL_LOCAL,
    ONBOARDING_PMORAL_INTERNATIONAL,

    PENDING_REVISION,
    PENDING_REVISION_W_CORRECTIONS,

    // Client accepted scopes
    FINISH_ONBOARDING_PFAE,
    FINISH_ONBOARDING_PMORAL,

    // Onboarding banking core
    ONBOARDING_AURUM_STEP_1,
    ONBOARDING_AURUM_STEP_2,

    //
    SIGN_CONTRACT,
    // Client accepted scope
    USE_ACCOUNT
}
