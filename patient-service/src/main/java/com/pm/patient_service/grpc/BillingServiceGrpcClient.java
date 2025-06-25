package com.pm.patient_service.grpc;

import billing.BillingServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
}
