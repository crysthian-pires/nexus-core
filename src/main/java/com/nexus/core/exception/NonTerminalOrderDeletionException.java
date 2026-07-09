package com.nexus.core.exception;

import com.nexus.core.serviceorder.ServiceOrderStatus;

public class NonTerminalOrderDeletionException extends RuntimeException {
    public NonTerminalOrderDeletionException(ServiceOrderStatus currentStatus) {
        super("Não é possível deletar uma OS em estado " + currentStatus +
                ". Apenas ordens FINALIZADO ou CANCELADO podem ser deletadas.");
    }
}
