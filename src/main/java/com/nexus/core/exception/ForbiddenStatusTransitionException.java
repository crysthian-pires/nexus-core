package com.nexus.core.exception;

import com.nexus.core.serviceorder.ServiceOrderStatus;

public class ForbiddenStatusTransitionException extends RuntimeException {
    public ForbiddenStatusTransitionException(ServiceOrderStatus from, ServiceOrderStatus to) {
        super("Não é possível alterar o status de " + from + " para " + to + " sem permissão de administrador");
    }
}
